import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * TCP Client1
 * Fairly complete client. Connects / disconnects / sends a message and awaits a reply
 * Connect/disconnect button allows multiple interactions
 * @author Dishank Jhaveri, Tiffany Ellis
 * @version 9-16-2017
 */
public class TCPClient1 extends JFrame implements ActionListener {
   // Components - NORTH
   // NORTH will itself be GridLayout ... we will use Row1 and Row2 of the NORTH
   // These are for Row1
   private JLabel jlServerIP = new JLabel("Server Name or IP: ");
   private JTextField jtfServerIP = new JTextField(20);
   private JButton jbConnect = new JButton("Connect");

   // These will be in Row2
   private JLabel jlSentence = new JLabel("Sentence: ");
   private JTextField jtfSentence = new JTextField(25);
   private JButton jbSend = new JButton("Send");
   
   // Compoonents - CENTER
   private JLabel jlLog = new JLabel("Log:", JLabel.LEFT);
   private JTextArea jtaLog = new JTextArea(10, 35);

   // IO attributes
   private PrintWriter pwt = null;
   private Scanner scn = null;

   // OTHER attributes
   public static final int SERVER_PORT = 32001;
   private Socket socket = null;

   /**
    * main program 
    */
   public static void main(String[] args) {
      new TCPClient1();
   }

   /**
    * Constructor ... draw and set up GUI
    */
   public TCPClient1() {
      this.setTitle("TCP Client");
      this.setSize(475, 300);
      this.setLocation(100, 50);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setResizable(false);
      
      // NORTH ... GridLayout ... 
      // GridLayout and use the 1st two rows, Row1 and Row2 as JPanels
      // (FlowLayout) for two rows of components.
      JPanel jpNorth = new JPanel();
         jpNorth.setLayout(new GridLayout(0,1));
         
         // Row1
         JPanel jpRow1 = new JPanel();
            jpRow1.add(jlServerIP);
            jpRow1.add(jtfServerIP);
            jpRow1.add(jbConnect);
         jpNorth.add(jpRow1);

         // Row2 - Textfield for a sentence to send and Send button
         JPanel jpRow2 = new JPanel();
            jpRow2.add(jlSentence);
            jpRow2.add(jtfSentence);
            jpRow2.add(jbSend);
            
            // jtfSentence and jbSend disabled until connected
            jtfSentence.setEnabled(false);
            jbSend.setEnabled(false);
         jpNorth.add(jpRow2);
      this.add(jpNorth, BorderLayout.NORTH);
      
      // CENTER ... Label + text area
      JPanel jpCenter = new JPanel();
         jpCenter.add(jlLog);
         jpCenter.add(new JScrollPane(jtaLog));
      this.add(jpCenter, BorderLayout.CENTER);
      
      // Listen for the buttons
      jbConnect.addActionListener(this);
      jbSend.addActionListener(this);
      
      this.setVisible(true);
   }

   /** 
    * Button dispatcher
    */
    public void actionPerformed(ActionEvent ae) {
      switch(ae.getActionCommand()) {
         case "Connect":
            doConnect();
            break;
         case "Disconnect":
            doDisconnect();
            break;
         case "Send":
            doSend();
            break;
      }
   }

   /**
    * doConnect - Connect button
    */
   private void doConnect() {
      try {
         // Connect to server and set up two streams, a Scanner for input from the
         // server and a PrintWriter for output to the server
         socket = new Socket(jtfServerIP.getText(), SERVER_PORT);
         scn = new Scanner(new InputStreamReader(socket.getInputStream()));
         pwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      }
      catch(IOException ioe) {
         jtaLog.append("IO Exception: " + ioe + "\n");
         return;
      }
      jtaLog.append("Connected!\n");
      jbConnect.setText("Disconnect");
      
      // Enable text field and Send button
      jtfSentence.setEnabled(true);
      jbSend.setEnabled(true);
   }

   /**
    * doDisconnect - Disconnect button'
    */
   private void doDisconnect() {
      try {
         // Close the socket and streams
         socket.close();
         scn.close();
         pwt.close();
      }
      catch(IOException ioe) {
         jtaLog.append("IO Exception: " + ioe + "\n");
         return;
      }
      jbConnect.setText("Connect");
      
      // Disable text field and Send button
      jtfSentence.setEnabled(false);
      jbSend.setEnabled(false);
   }

   /**
    * doSend - Send button'
    */
   private void doSend() {
      // Get the sentence, send to server, wait for reply
      pwt.println(jtfSentence.getText());
      pwt.flush();
      jtaLog.append("Sent: " + jtfSentence.getText() + "\n");
      jtfSentence.setText("");
      String reply = scn.nextLine();
      jtaLog.append("Reply: " + reply + "\n");
   }

}