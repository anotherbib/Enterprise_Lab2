/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consumer;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class Main {
    @Resource(mappedName = "jms/JMSTopic")
    private static Topic topic;
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/JMSQueue")
    private static Queue queue;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String destType = null;
        Connection connection = null;
        Session session = null;
        Destination dest = null;
        MessageConsumer consumer = null;
        TextMessage message = null;

        if (args.length != 1) {
            System.err.println("Program takes one argument: <dest_type>");
            System.exit(1);
        }

        destType = args[0];
        //System.out.println("Destination type is " + destType);

        if (!(destType.equals("queue") || destType.equals("topic"))) {
            System.err.println("Argument must be \"queue\" or \"topic\"");
            System.exit(1);
        }

        try {
            if (destType.equals("queue")) {
                dest = (Destination) queue;
            } else {
                dest = (Destination) topic;
            }
        } catch (Exception e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(dest);
            connection.start();
            Scanner sc = new Scanner(System.in);
            System.out.println("To end program, type q or Q, then <return>");
            while (true) {
                Message m = consumer.receive();
                
                if (m != null) {
                    if (m instanceof TextMessage) {
                        message = (TextMessage) m;
                        System.out.println("Updated!: " + message.getText());
                        String quit = sc.nextLine(); 
                        if ("q".equals(quit) || "Q".equals(quit)) break;
                    } else {
                        break;
                    }
                }
                else {
                    break;
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
