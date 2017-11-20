# Vertx3 client-connection cluster example

## Environment configuration

I've created 3 different configurations to cover the different possible environments

1. Local (disable multicast, enable tcp-ip and overwrite interfaces to use local 127.0.0.1)
2. Development (disable multicast, enable tcp-ip and overwrite interfaces with the nodes)
3. Test (enable multicast, overwrite interfaces to fix problems when multiple network interfaces on the server)


## Build by environment


```mvn clean install```


## Upload jars to different machines

1. Copy connection/target/connection-1.0-SNAPSHOT-fat.jar in one machine
2. Copy client/target/client-1.0-SNAPSHOT-fat.jar in a different machine

## Run the jars

1. Pong:

```java -jar connection-1.0-SNAPSHOT-fat.jar -cluster -cluster-host XXX.XXX.XXX.XX```

for example

```java -jar connection-1.0-SNAPSHOT-fat.jar -cluster -cluster-host 127.0.0.1```

or

```java -jar connection-1.0-SNAPSHOT-fat.jar -cluster -cluster-host 192.168.112.10```

2. Ping:

```java -jar client-1.0-SNAPSHOT-fat.jar -cluster -cluster-host 192.168.112.9```

for example

```java -jar client-1.0-SNAPSHOT-fat.jar -cluster -cluster-host 127.0.0.1```

or

```java -jar client-1.0-SNAPSHOT-fat.jar -cluster -cluster-host 192.168.112.9```


