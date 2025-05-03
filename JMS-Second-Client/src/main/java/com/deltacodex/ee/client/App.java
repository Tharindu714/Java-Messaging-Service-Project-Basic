package com.deltacodex.ee.client;

import jakarta.jms.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class App {
    public static void main(String[] args) {
        try {
            InitialContext ctx = new InitialContext();
            TopicConnectionFactory TopicconnectionFactory =
                    (TopicConnectionFactory) ctx.lookup("jms/MyConnectionFactory");
            System.out.println(TopicconnectionFactory);
            // OUTPUT ->> com.sun.messaging.jms.ra.ConnectionFactoryAdapter@6ace919c

            TopicConnection connection = TopicconnectionFactory.createTopicConnection();
            connection.start();

            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println(session);
            //OUTPUT ->> com.sun.messaging.jms.ra.SessionAdapter@7a1ddbf1

            Topic topic = (Topic) ctx.lookup("jms/MyTopic");

            TopicSubscriber subscriber = session.createSubscriber(topic);
//            Message message = subscriber.receive();
//            System.out.println(message.getBody(String.class));

            subscriber.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                       String msg = message.getBody(String.class);
                        System.out.println(msg);
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            while (true) {}
        } catch (NamingException | JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
