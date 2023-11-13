package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.connection.Connection;
import io.github.xinfra.lab.rpc.remoting.protocol.Message;
import io.github.xinfra.lab.rpc.remoting.protocol.MessageFactory;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class BaseRemoting {
    private MessageFactory messageFactory;

    public BaseRemoting(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public Message syncCall(Message message, Connection connection, int timeoutMills) throws InterruptedException {
        int requestId = message.id();
        InvokeFuture<Message> invokeFuture = new InvokeFuture<>();
        try {
            connection.addInvokeFuture(requestId, invokeFuture);
            connection.getChannel().writeAndFlush(message).addListener(
                    (ChannelFuture future) -> {
                        if (!future.isSuccess()) {
                            connection.removeInvokeFuture(requestId);
                            invokeFuture.finish(messageFactory.createSendFailMessage(future.cause()));
                            log.error("Send message fail. id:{}", requestId, future.cause());
                        }
                    }
            );
        } catch (Throwable t) {
            connection.removeInvokeFuture(requestId);
            invokeFuture.finish(messageFactory.createSendFailMessage(t));
            log.error("Invoke sending message fail. id:{}", requestId, t);
        }

        Message result = invokeFuture.get(timeoutMills, TimeUnit.MILLISECONDS);

        if (result == null) {
            connection.removeInvokeFuture(requestId);
            result = messageFactory.createTimeoutMessage();
            log.warn("Wait result timeout. id:{}", requestId);
        }
        return result;
    }

    public Future<Message> asyncCall(Message message, Connection connection) {
        // todo
        return null;
    }

    public void asyncCall(Message message, Connection connection, CallBack<Message> callBack) {
        // todo
    }

    public void oneway(Message message, Connection connection) {
        // todo
    }
}
