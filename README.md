# code-with-quarkus

start hornetq
```
docker network create my-network
docker run -it --rm -p 5445:5445 -p 5455:5455 --net my-network --name hornetq mansante/hornetq
```

JVM mode:
```
mvn clean package -Dquarkus.container-image.tag=jvm
docker run --rm -p 8080:8080 --net my-network --name myapp vsevel/code-with-quarkus:jvm
curl -X POST localhost:8080/hello/send
# ==> OK: sent 1 message(s) with text hello
```

native mode:
```
mvn clean package -Dnative -Dquarkus.container-image.tag=native
docker run --rm -p 8080:8080 --net my-network --name myapp vsevel/code-with-quarkus:native
curl -X POST localhost:8080/hello/send
# ==> KO: Caused by: ActiveMQConnectionTimedOutException[errorType=CONNECTION_TIMEDOUT message=AMQ219013: Timed out waiting to receive cluster topology. Group:null]
```

this fails with:
``` 
$ curl -X POST localhost:8080/hello/send

ERROR: jakarta.jms.JMSRuntimeException: Failed to create session factory
        at org.apache.activemq.artemis.jms.client.JmsExceptionUtils.convertToRuntimeException(JmsExceptionUtils.java:88)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:338)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:326)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:316)
        at jakarta.jms.ConnectionFactory_xLt8S2PyCZ4yomgUwkQrJvHfdzI_Synthetic_ClientProxy.createContext(Unknown Source)
        at org.acme.GreetingResource.send(GreetingResource.java:41)
        at org.acme.GreetingResource$quarkusrestinvoker$send_b23b9faad56de2c71236a9892991d08efa266d9d.invoke(Unknown Source)
        at org.jboss.resteasy.reactive.server.handlers.InvocationHandler.handle(InvocationHandler.java:29)
        at io.quarkus.resteasy.reactive.server.runtime.QuarkusResteasyReactiveRequestContext.invokeHandler(QuarkusResteasyReactiveRequestContext.java:141)
        at org.jboss.resteasy.reactive.common.core.AbstractResteasyReactiveContext.run(AbstractResteasyReactiveContext.java:147)
        at io.quarkus.vertx.core.runtime.VertxCoreRecorder$15.runWith(VertxCoreRecorder.java:639)
        at org.jboss.threads.EnhancedQueueExecutor$Task.doRunWith(EnhancedQueueExecutor.java:2675)
        at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2654)
        at org.jboss.threads.EnhancedQueueExecutor.runThreadBody(EnhancedQueueExecutor.java:1627)
        at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1594)
        at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:11)
        at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:11)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.base@21.0.6/java.lang.Thread.runWith(Thread.java:1596)
        at java.base@21.0.6/java.lang.Thread.run(Thread.java:1583)
        at org.graalvm.nativeimage.builder/com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:896)
        at org.graalvm.nativeimage.builder/com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:872)
Caused by: jakarta.jms.JMSException: Failed to create session factory
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createConnectionInternal(ActiveMQConnectionFactory.java:912)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:333)
        ... 20 more
Caused by: ActiveMQConnectionTimedOutException[errorType=CONNECTION_TIMEDOUT message=AMQ219013: Timed out waiting to receive cluster topology. Group:null]
        at org.apache.activemq.artemis.core.client.impl.ServerLocatorImpl.createSessionFactory(ServerLocatorImpl.java:778)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createConnectionInternal(ActiveMQConnectionFactory.java:910)
        ... 21 more
```


the container prints:
``` 
$ docker run --rm -p 18080:8080 x/code-with-quarkus:1.0.0-SNAPSHOT
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2025-02-06 12:37:24,158 INFO  [io.quarkus] (main) code-with-quarkus 1.0.0-SNAPSHOT native (powered by Quarkus 3.18.2) started in 0.055s. Listening on: http://0.0.0.0:8080
2025-02-06 12:37:24,159 INFO  [io.quarkus] (main) Profile prod activated.
2025-02-06 12:37:24,159 INFO  [io.quarkus] (main) Installed features: [artemis-jms, cdi, rest, smallrye-context-propagation, vertx]
2025-02-06 12:38:12,768 ERROR [org.acm.GreetingResource] (executor-thread-1) Error sending message: jakarta.jms.JMSRuntimeException: Failed to create session factory
        at org.apache.activemq.artemis.jms.client.JmsExceptionUtils.convertToRuntimeException(JmsExceptionUtils.java:88)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:338)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:326)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:316)
        at jakarta.jms.ConnectionFactory_xLt8S2PyCZ4yomgUwkQrJvHfdzI_Synthetic_ClientProxy.createContext(Unknown Source)
        at org.acme.GreetingResource.send(GreetingResource.java:41)
        at org.acme.GreetingResource$quarkusrestinvoker$send_b23b9faad56de2c71236a9892991d08efa266d9d.invoke(Unknown Source)
        at org.jboss.resteasy.reactive.server.handlers.InvocationHandler.handle(InvocationHandler.java:29)
        at io.quarkus.resteasy.reactive.server.runtime.QuarkusResteasyReactiveRequestContext.invokeHandler(QuarkusResteasyReactiveRequestContext.java:141)
        at org.jboss.resteasy.reactive.common.core.AbstractResteasyReactiveContext.run(AbstractResteasyReactiveContext.java:147)
        at io.quarkus.vertx.core.runtime.VertxCoreRecorder$15.runWith(VertxCoreRecorder.java:639)
        at org.jboss.threads.EnhancedQueueExecutor$Task.doRunWith(EnhancedQueueExecutor.java:2675)
        at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2654)
        at org.jboss.threads.EnhancedQueueExecutor.runThreadBody(EnhancedQueueExecutor.java:1627)
        at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1594)
        at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:11)
        at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:11)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.base@21.0.6/java.lang.Thread.runWith(Thread.java:1596)
        at java.base@21.0.6/java.lang.Thread.run(Thread.java:1583)
        at org.graalvm.nativeimage.builder/com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:896)
        at org.graalvm.nativeimage.builder/com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:872)
Caused by: jakarta.jms.JMSException: Failed to create session factory
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createConnectionInternal(ActiveMQConnectionFactory.java:912)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createContext(ActiveMQConnectionFactory.java:333)
        ... 20 more
Caused by: ActiveMQConnectionTimedOutException[errorType=CONNECTION_TIMEDOUT message=AMQ219013: Timed out waiting to receive cluster topology. Group:null]
        at org.apache.activemq.artemis.core.client.impl.ServerLocatorImpl.createSessionFactory(ServerLocatorImpl.java:778)
        at org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory.createConnectionInternal(ActiveMQConnectionFactory.java:910)
        ... 21 more
```

test with artemis:
```
docker run -it --rm -p 61616:61616 -p 8161:8161 --net my-network --name artemis apache/activemq-artemis:latest-alpine
docker run --rm -e QUARKUS_ARTEMIS_URL=tcp://artemis:61616 -e QUARKUS_ARTEMIS_USER=artemis -e QUARKUS_ARTEMIS_PASSWORD=artemis -e MY_QUEUE=DLQ -p 8080:8080 --net my-network --name myapp vsevel/code-with-quarkus:jvm
docker run --rm -e QUARKUS_ARTEMIS_URL=tcp://artemis:61616 -e QUARKUS_ARTEMIS_USER=artemis -e QUARKUS_ARTEMIS_PASSWORD=artemis -e MY_QUEUE=DLQ -p 8080:8080 --net my-network --name myapp vsevel/code-with-quarkus:native
curl -X POST localhost:8080/hello/send
# ==> OK: sent 1 message(s) with text hello
```