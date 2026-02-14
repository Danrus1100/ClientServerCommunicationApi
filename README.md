# Client-Server Communication API

how to use for developer:
- Register packet in common code:
```java
public class FooBarPacket implements CscPacket{
    
    public FooBarPacket(ByteBuf buf) {
        read(buf);
    }

    private int foo;

    @Override
    public void read(ByteBuf buf) {
        foo = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(foo);
    }
}
```

```java
public static CscPacketType<FooBarPacket> FOO_BAR_PACKET = CscApi.registerPacket(
  "foo_bar", 
  FooBarPacket.class, 
  FooBarPacket::new
);
```

- Use it on Server:

```java
public class ServerClass {
    private static final Logger log = LoggerFactory.getLogger(ServerClass.class);

    public void init() {
        CscServer.getService().registerListener((packet, player) -> {
            log.info("Received packet {} from player {}", packet.getClass().getSimpleName(), player.getName());
        }, ModPackets.FOO_BAR_PACKET);
    }
    
    public void sendFooBarPacket(Player player) {
        CscServer.getService().sendPacket(new FooBarPacket(), player);
    }
}
```
- Use it on Client:

```java
public class ClientClass {
    private static final Logger log = LoggerFactory.getLogger(ClientClass.class);

    public void init() {
        CscClient.getService().registerListener(packet -> {
            log.info("Received packet: {}", packet);
        }, ModPackets.FOO_BAR_PACKET);
    }
    
    public void sendFooBarPacket() {
        CscClient.getService().sendPacket(new FooBarPacket());
    }
}
```
