import org.json.JSONObject;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;

public class reply {
    static Connection connection;
    static Session session;
    static Destination finalDestination;
    static MessageProducer producer;
    static Destination receiveDestination;
    static MessageConsumer consumer;


    public static void main(String args[]){
        ArrayList<Message> messages = new ArrayList<>();

        //region Form builder
        JFrame frame = new JFrame("JMSReply");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jp = new JPanel();

        JTextField input = new JTextField(30);
        jp.add(input, BorderLayout.CENTER);

        JButton btnAdd = new JButton();
        btnAdd.setText("Reply");
        jp.add(btnAdd, BorderLayout.AFTER_LAST_LINE);

        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Reply");
        table.setModel(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(450,250));
        jp.add(scrollPane, BorderLayout.CENTER);

        jp.setSize(400,250);
        frame.add(jp, BorderLayout.CENTER);
        frame.setSize(500, 350);
        frame.setVisible(true);
        //endregion


        //Receive message
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put(("queue.myRequestDestination"), " myRequestDestination");
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            receiveDestination = (Destination) jndiContext.lookup("myRequestDestination");
            consumer = session.createConsumer(receiveDestination);
            consumer.setMessageListener((MessageListener) msg -> {
                if (msg instanceof TextMessage) {
                    try {
                        System.out.println("received: " + msg);
                        messages.add(msg);

                        JSONObject receivedJson = new JSONObject(((TextMessage) msg).getText());
                        String name = receivedJson.getString("name");
                        Double price = Double.valueOf(receivedJson.getString("price"));
                        Product receivedProduct = new Product(name, price);
                        model.addRow(new String[]{receivedProduct.getName(), receivedProduct.getPrice().toString()});
                        table.setModel(model);
                    }
                    catch (JMSException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    throw new IllegalArgumentException("Message must be of type TextMessage");
                }
            });
            connection.start(); // this is needed to start receiving messages
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }



        //Send message
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Properties props = new Properties();
                props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
                props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
                int row = table.getSelectedRow();
                String productName = table.getValueAt(row,0).toString();
                System.out.println(productName);
                for (int i = 0;i < messages.size();i++){
                    try {
                        JSONObject jsonObject = new JSONObject(((TextMessage) messages.get(i)).getText());
                        String name = jsonObject.getString("name");
                        Double price = Double.valueOf(jsonObject.getString("price"));
                        Product receivedProduct = new Product(name, price);
                        if (receivedProduct.getName().equals(productName)){
                            finalDestination =  messages.get(i).getJMSReplyTo();
                        }
                    } catch (JMSException e1) {
                        e1.printStackTrace();
                    }
                }
                props.put((String.valueOf(finalDestination)), String.valueOf(finalDestination));
                System.out.println(finalDestination);

                Context jndiContext = null;
                try {
                    jndiContext = new InitialContext(props);
                    ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
                    connection = connectionFactory.createConnection();
                    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    producer = session.createProducer(finalDestination);
                } catch (NamingException e1) {
                    e1.printStackTrace();
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }

                Session finalSession = session;

                String body = input.getText();
                model.setValueAt(body, table.getSelectedRow(),2);
                table.setModel(model);
                input.setText("");

                // create a text message
                Message msg = null;
                try {
                    msg = finalSession.createTextMessage(body);
                    row = table.getSelectedRow();
                    productName = table.getValueAt(row,0).toString();
                    for (int i = 0; i < messages.size() ;i++){
                        JSONObject jsonObject = new JSONObject(((TextMessage) messages.get(i)).getText());
                        String name = jsonObject.getString("name");
                        Double price = Double.valueOf(jsonObject.getString("price"));
                        Product receivedProduct = new Product(name, price);
                        if (receivedProduct.getName().equals(productName)){
                            msg.setJMSCorrelationID(messages.get(i).getJMSMessageID());
                        }
                    }
                    producer.send(finalDestination,msg);
                    System.out.println(msg);
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }


                try {
                    System.out.println("JMSMessageID=" + msg.getJMSMessageID()
                            + " JMSDestination=" + msg.getJMSDestination()
                            + " Text=" + ((TextMessage) msg).getText());
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
