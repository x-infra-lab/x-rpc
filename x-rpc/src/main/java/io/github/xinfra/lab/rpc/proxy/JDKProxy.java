package io.github.xinfra.lab.rpc.proxy;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
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

            Invocation invocation = new Invocation();
            invocation.setArgs(args);
            invocation.setInterfaceName(interfaceId.getName());
            invocation.setMethodName(method.getName());
            String[] argSigns = (String[]) Arrays.stream(method.getParameterTypes())
                    .map(Class::getName).toArray();
            invocation.setArgSigns(argSigns);

            InvocationResult invocationResult = invoker.invoke(invocation);

            if (invocationResult.isError()) {
                // todo
                throw new RpcException(ErrorCode.SERVER_UNDEFINED_ERROR, invocationResult.getErrorMsg());
            }

            Object result = invocationResult.getResult();

            if (result instanceof Throwable) {
                // todo
                throw (Throwable) result;
            }

            if (result == null) {
                return ClassUtils.getDefaultPrimitiveValue(method.getReturnType());
            }
            return result;
        }
    }
}
