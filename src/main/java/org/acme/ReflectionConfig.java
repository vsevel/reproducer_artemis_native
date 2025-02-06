package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.activemq.artemis.core.protocol.hornetq.client.HornetQClientProtocolManagerFactory;

@RegisterForReflection(targets = {HornetQClientProtocolManagerFactory.class})
public class ReflectionConfig {
}
