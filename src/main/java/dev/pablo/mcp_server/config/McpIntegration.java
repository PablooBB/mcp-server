package dev.pablo.mcp_server.config;

import dev.pablo.mcp_server.service.ToolService;
import dev.pablo.mcp_server.dto.ToolDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Intenta integrar con el starter `spring-ai` MCP server si está presente en el classpath.
 * Usa reflexión para no forzar una dependencia de tipos en tiempo de compilación.
 * Si no se encuentra ninguna API conocida, deja el endpoint `/mcp/tools` como mecanismo de discovery.
 */
@Component
public class McpIntegration implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(McpIntegration.class);

    private final ToolService toolService;
    private final ApplicationContext ctx;

    public McpIntegration(ToolService toolService, ApplicationContext ctx) {
        this.toolService = toolService;
        this.ctx = ctx;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<ToolDto> tools = toolService.list();
        if (tools.isEmpty()) {
            log.info("No tools to register with MCP integration");
            return;
        }

        // Primero intentar encontrar beans cuyos paquetes pertenezcan al starter spring-ai
        var candidateBeanNames = Arrays.stream(ctx.getBeanDefinitionNames())
                .filter(name -> {
                    try {
                        var bean = ctx.getType(name);
                        return bean != null && bean.getPackageName().contains("org.springframework.ai.mcp");
                    } catch (Throwable e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        boolean integrated = false;

        if (!candidateBeanNames.isEmpty()) {
            log.info("Beans spring-ai MCP detectados en el contexto: {}", candidateBeanNames);
            for (var beanName : candidateBeanNames) {
                try {
                    Object bean = ctx.getBean(beanName);
                    Class<?> clazz = bean.getClass();

                    Method registerMethod = findRegisterMethod(clazz);
                    if (registerMethod == null) {
                        log.debug("No se encontró método de registro en bean {} (tipo {})", beanName, clazz.getName());
                        continue;
                    }

                    log.info("Integrando {} tools con bean {} ({}), usando método {}", tools.size(), beanName, clazz.getName(), registerMethod.getName());

                    var payloads = tools.stream().map(t -> Map.of(
                            "id", t.getId().toString(),
                            "name", t.getName(),
                            "description", t.getDescription(),
                            "tags", t.getTags(),
                            "runEndpoint", "/api/tools/" + t.getId() + "/run",
                            "streamEndpoint", "/api/tools/stream"
                    )).collect(Collectors.toList());

                    for (Map<String,Object> p : payloads) {
                        tryInvoke(registerMethod, bean, p);
                    }

                    integrated = true;
                    log.info("Integración con spring-ai MCP iniciada usando bean {}", beanName);
                    break;
                } catch (Throwable t) {
                    log.warn("Error integrando bean {}: {}", beanName, t.getMessage());
                }
            }
        } else {
            log.debug("No se encontraron beans específicos de spring-ai en el contexto.");
        }

        // Si no hubo integración por beans, intentar buscar por clases candidatas conocidas (compatibilidad)
        if (!integrated) {
            var knownCandidates = List.of(
                    "org.springframework.ai.mcp.server.webmvc.McpServer",
                    "org.springframework.ai.mcp.server.webmvc.ToolRegistry",
                    "org.springframework.ai.mcp.server.McpServer",
                    "org.springframework.ai.mcp.ToolRegistry"
            );

            for (var candidate : knownCandidates) {
                try {
                    Class<?> clazz = Class.forName(candidate);
                    try {
                        Object bean = ctx.getBean(clazz);
                        Method registerMethod = findRegisterMethod(clazz);
                        if (registerMethod != null) {
                            log.info("Integrando {} tools con MCP bean tipo {} usando método {}", tools.size(), candidate, registerMethod.getName());
                            var payloads = tools.stream().map(t -> Map.of(
                                    "id", t.getId().toString(),
                                    "name", t.getName(),
                                    "description", t.getDescription(),
                                    "tags", t.getTags(),
                                    "runEndpoint", "/api/tools/" + t.getId() + "/run",
                                    "streamEndpoint", "/api/tools/stream"
                            )).collect(Collectors.toList());
                            for (Map<String,Object> p : payloads) {
                                tryInvoke(registerMethod, bean, p);
                            }
                            integrated = true;
                            break;
                        }
                    } catch (Exception ex) {
                        log.debug("No hay bean de tipo {} en contexto: {}", candidate, ex.getMessage());
                    }
                } catch (ClassNotFoundException cnf) {
                    // ignore
                }
            }
        }

        if (!integrated) {
            log.info("No se detectó la API del starter spring-ai MCP o no se pudo registrar: se expondrá el endpoint /mcp/tools para discovery.");
        }
    }

    private Method findRegisterMethod(Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            String name = m.getName().toLowerCase();
            if ((name.contains("register") || name.contains("publish") || name.contains("add") || name.contains("declare"))
                    && m.getParameterTypes().length == 1) {
                return m;
            }
        }
        return null;
    }

    private void tryInvoke(Method method, Object bean, Map<String,Object> p) {
        try {
            Class<?> paramType = method.getParameterTypes()[0];
            if (paramType.isAssignableFrom(Map.class) || paramType.isAssignableFrom(Object.class)) {
                method.invoke(bean, p);
            } else if (paramType.isAssignableFrom(String.class)) {
                method.invoke(bean, p.toString());
            } else if (paramType.isAssignableFrom(List.class)) {
                method.invoke(bean, List.of(p));
            } else {
                // try best-effort: pass name
                method.invoke(bean, p.get("name"));
            }
        } catch (Throwable ex) {
            log.warn("Fallo al invocar método {} en {}: {}", method.getName(), bean.getClass().getName(), ex.getMessage());
        }
    }
}
