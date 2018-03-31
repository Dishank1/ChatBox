import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * UDP Client1
 * Fairly complete client. Sends a message and awaits a reply
 * No Connect/Disconnect button ... connectionless
 * @author Dishank Jhaveri, Tiffany Ellis
 * @version 11-8-2017
 */
public class UDPClient1 extends JFrame implements ActionListener {
   // Components - NORTH
   // NORTH will itself be GridLayout ... we will use Row1 and Row2 of the NORTH
   // These are for Row1
   private JLabel jlServerIP = new JLabel("Server Name or IP: ");
   private JTextField jtfServerIP = new JTextField(20);

   // These will be in Row2
   private JLabel jlSentence = new JLabel("Sentence: ");
   private JTextField jtfSentence = new JTextField(25);
   private JButton jbSend = new JButton("Send");
   
   // Compoonents - CENTER
   private JLabel jlLog = new JLabel("Log:", JLabel.LEFT);
   private JTextArea jtaLog = new JTextArea(10, 35);

   // IO attributes
   private DatagramSocket socket = null;
   private InetAddress serverIP = null;
   
   // OTHER attributes
   public static final int SERVER_PORT = 32001;

   /**
    * main program 
    */
   public static void main(String[] args) {
      new UDPClient1();
   }

   /**
    * Constructor ... draw and set up GUI
    */
   public UDPClient1() {
      this.setTitle("UDP Client");
      this.setSize(475, 275);
      this.setLocation(100, 50);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setResizable(false);
      
      // NORTH ... GridLayout ... 
      // GridLayout and use the 1st two rows, Row1 and Row2 as JPanels
      // (FlowLayout) for two rows of components.
      JPanel jpNorth = new JPanel();
         jpNorth.setLayout(new GridLayout(0, 1));
         
         // Row1
         JPanel jpRow1 = new JPanel();
            jpRow1.add(jlServerIP);
            jpRow1.add(jtfServerIP);
         jpNorth.add(jpRow1);

         // Row2 - Textfield for a sentence to send and Send button
         JPanel jpRow2 = new JPanel();
            jpRow2.add(jlSentence);
            jpRow2.add(jtfSentence);
            jpRow2.add(jbSend);
         jpNorth.add(jpRow2);
      this.add(jpNorth, BorderLayout.NORTH);
      
      // CENTER ... Label + text area
      JPanel jpCenter = new JPanel();
         jpCenter.add(jlLog);
         jpCenter.add(new JScrollPane(jtaLog));
      this.add(jpCenter, BorderLayout.CENTER);
      
      // Listen for the buttons
      jbSend.addActionListener(this);
      
      this.setVisible(true);
      
      // Open a DatagramSocket for IO
      try {
         socket = new DatagramSocket();
      }
      catch(SocketException se) {
         JOptionPane.showMessageDialog(null, "Cannot create socket: " + se,
            "Socket Exception", JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }
   }

   /** 
    * Button dispatcher
    */
    public void actionPerformed(ActionEvent ae) {
      switch(ae.getActionCommand()) {
         case "Send":
            doSend();
            break;
      }
   }

   /**
    * doSend - Send button'
    */
   private void doSend() {
      // Get the sentence, send to server, wait for reply
//       pwt.println(jtfSentence.getText());
//       pwt.flush();
//       jtaLog.append("Sent: " + jtfSentence.getText() + "\n");
//       jtfSentence.setText("");
//       String reply = scn.nextLine();
//       jtaLog.append("Reply: " + reply + "\n");

      // get the server's ip
      try {
         serverIP = InetAddress.getByName(jtfServerIP.getText());
      }
      catch(UnknownHostException uhe) {
         JOptionPane.showMessageDialog(null, "No such host: " + uhe,
            "Unknown Host", JOptionPane.ERROR_MESSAGE);
         return;
      }

      
      // get the message and form a packet
      String msgStr = jtfSentence.getText();
      byte[] msg = msgStr.getBytes();
      DatagramPacket msgPkt = new DatagramPacket(msg, msg.length, serverIP, SERVER_PORT);
      
      // send the packet
      try {
         socket.send(msgPkt);
      }
      catch(IOException ioe) {
         JOptionPane.showMessageDialog(null, "Cannot send packet: " + ioe,
            "Failed Send", JOptionPane.ERROR_MESSAGE);
         return;
      }

         
      jtaLog.append("SENT: " + msgStr + "\n");
      
      // now, wait for the reply
      // Set up a buffer to hold the reply ... must be big enough for any reply
      byte[] reply = new byte[msg.length];
      // Make an empty DatagramPacket for the reply
      DatagramPacket replyPkt = new DatagramPacket(reply, reply.length);
      
      // await the reply
      try {
         socket.receive(replyPkt);
      }
      catch(IOException ioe) {
         JOptionPane.showMessageDialog(null, "Cannot receive packet: " + ioe,
            "Failed Receive", JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      // Get the string from the packet
      String replyStr = new String(replyPkt.getData(), replyPkt.getOffset(), replyPkt.getLength());
      jtaLog.append("REPLY: " + replyStr + "\n");
   }

}