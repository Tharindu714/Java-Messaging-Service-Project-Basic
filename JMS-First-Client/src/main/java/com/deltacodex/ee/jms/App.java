package com.deltacodex.ee.jms;

import jakarta.jms.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;

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
            session.createPublisher(topic);

            TopicPublisher topicPublisher = session.createPublisher(topic);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the message to be sent or type 'exit' to quit:");

            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("exit")) {
                    break;
                }
                TextMessage message = session.createTextMessage();
                message.setText(line);

                topicPublisher.publish(message);
            }

        } catch (NamingException | JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
