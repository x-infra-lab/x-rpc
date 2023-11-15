package io.github.xinfra.lab.rpc.remoting.serialization;

public interface Serializer<T> {

    byte[] serialize(T t);


    T deserialize(byte[] data);
}
