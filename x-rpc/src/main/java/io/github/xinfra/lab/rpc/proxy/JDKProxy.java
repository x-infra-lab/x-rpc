package io.github.xinfra.lab.rpc.proxy;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import io.github.xinfra.lab.rpc.common.ClassUtils;
import io.github.xinfra.lab.rpc.exception.ErrorCode;
import io.github.xinfra.lab.rpc.exception.RpcException;
import io.github.xinfra.lab.rpc.invoker.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class JDKProxy<T> implements Proxy<T> {
    @Override
    public T getObject(Class<T> interfaceId, Invoker invoker) {

        return (T) java.lang.reflect.Proxy.newProxyInstance
                (Thread.currentThread().getContextClassLoader(),
                        new Class[]{interfaceId}, new JDKInvocationHandler(invoker, interfaceId));
    }

    public static class JDKInvocationHandler implements InvocationHandler {
        private Invoker invoker;
        private Class<?> interfaceId;

        public JDKInvocationHandler(Invoker invoker, Class<?> interfaceId) {
            this.invoker = invoker;
            this.interfaceId = interfaceId;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // TODO: handle toString hashCode equals method
            RpcRequest request = new RpcRequest();
            request.setArgs(args);
            request.setInterfaceName(interfaceId.getName());
            request.setMethodName(method.getName());
            String[] argSigns = (String[]) Arrays.stream(method.getParameterTypes())
                    .map(Class::getName).toArray();
            request.setArgSigns(argSigns);

            RpcResponse response = invoker.invoke(request);

            if (response.isError()) {
                throw new RpcException(ErrorCode.SERVER_UNDEFINED_ERROR, response.getErrorMsg());
            }

            Object result = response.getResult();

            if (result instanceof Throwable) {
                throw (Throwable) result;
            }

            if (result == null) {
                return ClassUtils.getDefaultPrimitiveValue(method.getReturnType());
            }
            return result;
        }
    }
}
