package org.example.grpctutorials.registy;


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import registry.PayloadRegistry;
import request.RequestHandler;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestHandlerRegistry.
 */

@Service
public class RequestHandlerRegistry implements ApplicationListener<ContextRefreshedEvent> {

    static {
        //加载payLoad
        PayloadRegistry.init();
    }

    Map<String, RequestHandler> registryHandlers = new HashMap<>();

    /**
     * Get Request Handler By request Type.
     *
     * @param requestType see definitions  of sub constants classes of RequestTypeConstants
     * @return request handler.
     */
    public RequestHandler getByRequestType(String requestType) {
        return registryHandlers.get(requestType);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, RequestHandler> beansOfType = event.getApplicationContext().getBeansOfType(RequestHandler.class);
        Collection<RequestHandler> values = beansOfType.values();
        for (RequestHandler requestHandler : values) {

            Class<?> clazz = requestHandler.getClass();
            boolean skip = false;
            while (!clazz.getSuperclass().equals(RequestHandler.class)) {
                if (clazz.getSuperclass().equals(Object.class)) {
                    skip = true;
                    break;
                }
                clazz = clazz.getSuperclass();
            }
            if (skip) {
                continue;
            }
            Class tClass = (Class) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
            registryHandlers.putIfAbsent(tClass.getSimpleName(), requestHandler);
        }
    }
}
