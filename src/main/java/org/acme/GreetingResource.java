package org.acme;

import io.smallrye.common.annotation.Identifier;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Path("/hello")
public class GreetingResource {

    Logger log = LoggerFactory.getLogger(GreetingResource.class);

    final Queue queue;

    @Inject
    @Identifier("<default>")
    ConnectionFactory connectionFactory;

    @Inject
    @Identifier("other")
    ConnectionFactory connectionFactoryRA;

    GreetingResource(@ConfigProperty(name = "my-queue") String queueName) {
        queue = ActiveMQDestination.createQueue(queueName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @POST
    @Path("/send")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String send(@QueryParam("text") @DefaultValue("hello") String text,
                       @QueryParam("count") @DefaultValue("1") int count,
                       @QueryParam("cf") @DefaultValue("<default>") String cfname) {

        var cf = cfname.equals("ra") ? connectionFactoryRA : connectionFactory;

        String out = send(text, count, cf);
        if (out != null) return out;

        return "OK: sent " + count + " message(s) with text " + text;
    }

    private String send(String text, int count, ConnectionFactory cf) {
        log.info("sending "+ count +" message");
        try {
            try (JMSContext context = cf.createContext()) {
                JMSProducer producer = context.createProducer();
                for (int i = 0; i < count; i++) {
                    TextMessage message = context.createTextMessage(text + "_" + i);
                    producer.send(queue, message);
                }
            }
        } catch (Exception e) {
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out));
            log.error("Error sending message", e);
            return "ERROR: " + out;
        }
        return null;
    }
}
