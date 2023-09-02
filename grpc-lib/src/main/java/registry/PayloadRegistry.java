package registry;


import request.Payload;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class PayloadRegistry {

    private static final Map<String, Class<?>> REGISTRY_REQUEST = new HashMap<>();

    static boolean initialized = false;

    public static void init() {
        scan();
    }

    private static synchronized void scan() {
        if (initialized) {
            return;
        }
        ServiceLoader<Payload> payloads = ServiceLoader.load(Payload.class);
        for (Payload payload : payloads) {
            register(payload.getClass().getSimpleName(), payload.getClass());
        }
        initialized = true;
    }

    static void register(String type, Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return;
        }
        if (REGISTRY_REQUEST.containsKey(type)) {
            throw new RuntimeException(String.format("Fail to register, type:%s ,clazz:%s ", type, clazz.getName()));
        }
        REGISTRY_REQUEST.put(type, clazz);
    }

    public static Class<?> getClassByType(String type) {
        return REGISTRY_REQUEST.get(type);
    }
}
