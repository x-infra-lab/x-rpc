package io.github.xinfra.lab.rpc.common;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectCache {
    /**
     * key: service class name
     * value: classloader
     */
    public Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    /**
     * key: methodName
     * value: key: methodSign - value: method
     */
    private Map<String, Map<String, Method>> overrideMethodMap = new ConcurrentHashMap<>();

    public void loadClass(Class<?> serviceInterfaceClass) {
         classLoaderMap.put(serviceInterfaceClass.getName(),
                serviceInterfaceClass.getClassLoader());

         for (Method method : serviceInterfaceClass.getMethods() ){
            loadMethod(serviceInterfaceClass, method);
         }
    }

    private void loadMethod(Class<?> clazz , Method method) {
        Map<String, Method> methodSignMap = overrideMethodMap.computeIfAbsent(clazz.getName(), k -> new ConcurrentHashMap<>());
        methodSignMap.put(ClassUtils.genMethodSign(method), method);
    }

    public Method find(String serviceName, String methodName, String[] methodArgTypes) {
        Map<String, Method> methodMap = overrideMethodMap.get(serviceName);
        if (methodMap == null) {
            return null;
        }
        return methodMap.get(ClassUtils.genMethodSign(methodName, methodArgTypes));
    }
}
