package org.acme;

import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Path("/hello")
public class GreetingResource {

    Logger log = LoggerFactory.getLogger(GreetingResource.class);

    final String QUEUE = "jms.queue.DLQ";
    final Queue queue = ActiveMQDestination.createQueue(QUEUE);

    @Inject
    ConnectionFactory connectionFactory;

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
                       @QueryParam("count") @DefaultValue("1") int count) {

        log.info("sending "+count+" message");
        try {
            try (JMSContext context = connectionFactory.createContext()) {
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

        return "OK: sent " + count + " message(s) with text " + text;
    }
}
