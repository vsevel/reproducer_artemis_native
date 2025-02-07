package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.activemq.artemis.core.protocol.hornetq.client.HornetQClientProtocolManagerFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

@RegisterForReflection(targets = {HornetQClientProtocolManagerFactory.class})
// FIXME this should be @RegisterForReflection(targets = {HornetQClientProtocolManagerFactory.class, ActiveMQConnectionFactory.class})
public class ReflectionConfig {
}
