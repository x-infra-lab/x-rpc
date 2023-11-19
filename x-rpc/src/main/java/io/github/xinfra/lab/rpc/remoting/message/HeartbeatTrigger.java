package io.github.xinfra.lab.rpc.remoting.message;

import io.netty.channel.Channel;

public interface HeartbeatTrigger {
    void triggerHeartBeat(Channel channel);
}
