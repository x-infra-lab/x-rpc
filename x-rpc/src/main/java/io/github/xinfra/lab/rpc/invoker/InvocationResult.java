package io.github.xinfra.lab.rpc.invoker;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Setter
@Getter
public class InvocationResult {
    // todo
    private boolean isError;
    // todo
    private String errorMsg;
    private Object result;

    // todos
    CompletableFuture<InvocationResult> future = new CompletableFuture<>();


    public InvocationResult whenComplete(BiConsumer<? super InvocationResult, ? super Throwable> action) {
        future.whenComplete(action);
        return this;
    }
}
