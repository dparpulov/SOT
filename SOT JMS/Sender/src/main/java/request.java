import org.json.JSONObject;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class request {
    static Connection connection;
    static Session session;
    static Destination sendDestination;
    static MessageProducer producer;
    static Destination receiveDestination;
    static MessageConsumer consumer;
    static JSONObject json;

    public static void main(String args[]){
        ArrayList<Message> messages = new ArrayList<>();
        json = new JSONObject();

        Random rand = new Random();
        int eqw = rand.nextInt(50);
        String receive = "Reply destination" + Integer.toString(eqw);

        //region Form builder
        JFrame frame = new JFrame("JMSRequest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jp = new JPanel();
        JPanel jp2 = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        jp.setLayout(gridbag);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel a = new JLabel("name");
        JTextField name = new JTextField(32);
        JLabel b = new JLabel("price");
        JTextField price = new JTextField(32);

        jp.add(a);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(name, c);
        jp.add(name);

        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

        jp.add(b);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(price, c);
        jp.add(price);


        JButton btnAdd = new JButton();
        btnAdd.setText("Request");
        jp.add(btnAdd);

        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Offer (Message sent)");
        model.addColumn("Message Sent ID");
        model.addColumn("Message Replied");
        table.setModel(model);

        //table.removeColumn(table.getColumnModel().getColumn(1));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(450,250));
        jp2.add(scrollPane, BorderLayout.CENTER);

        jp.setPreferredSize(new Dimension(450,150));
        jp2.setSize(450,300);
        frame.add(jp,BorderLayout.CENTER);
        frame.add(jp2,BorderLayout.AFTER_LAST_LINE);
        frame.setSize(500, 400);
        frame.setVisible(true);

        //endregion

        //Receive message
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put(("queue." + receive), receive);
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            receiveDestination = (Destination) jndiContext.lookup(receive);
            consumer = session.createConsumer(receiveDestination);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message msg) {
                    if (msg instanceof TextMessage) {
                        try {
                            System.out.println("received: " + msg);
                            String received = ((TextMessage) msg).getText();
                            String corr = msg.getJMSCorrelationID();
                            int index = getRowByValue(model, corr);
                            model.setValueAt(received, index,2);
                            table.setModel(model);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    else {
                        throw new IllegalArgumentException("Message must be of type TextMessage");
                    }
                }
            });
            connection.start(); // this is needed to start receiving messages
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Send message
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put(("queue.myRequestDestination"), "myRequestDestination");
            props.put(("queue." + receive), receive);
            Context jndiContext = new InitialContext(props);
            Destination finalDestination = (Destination) jndiContext.lookup(receive);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            sendDestination = (Destination) jndiContext.lookup("myRequestDestination");
            producer = session.createProducer(sendDestination);

            Session finalSession = session;

            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Product product = new Product(name.getText(), Double.valueOf(price.getText()));

                    json.put("name", name.getText());
                    json.put("price", price.getText());

                    String id = null;
                    Message msg = null;
                    try {
                        msg = finalSession.createTextMessage(json.toString());
                        msg.setJMSReplyTo(finalDestination);
                        producer.send(msg);
                        messages.add(msg);
                        System.out.println("JMSMessageID=" + msg.getJMSMessageID()
                                + " JMSDestination=" + msg.getJMSDestination()
                                + " Text=" + ((TextMessage) msg).getText());
                        id = msg.getJMSMessageID();
                        model.addRow(new String[]{name.getText() + " for " + price.getText() + " euros", id});
                        table.setModel(model);

                        name.setText("");
                        price.setText("");

                        System.out.println(msg);
                    } catch (JMSException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    static int getRowByValue(TableModel model, Object value) {
        for (int i = model.getRowCount() - 1; i >= 0; --i) {
            for (int j = model.getColumnCount() - 1; j >= 0; --j) {
                if (model.getValueAt(i, j) != null && model.getValueAt(i, j).equals(value)){
                    return i;
                }
            }
        }
        return 0;
    }
}
