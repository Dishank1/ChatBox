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
   //Name
   private JLabel jlName = new JLabel("Name: ");
   private JTextField jtfName = new JTextField(25);
   private JButton jbSetName = new JButton("Set Name");

   
   // Components - CENTER
   private JLabel jlLog = new JLabel("Log:", JLabel.LEFT);
   private JTextArea jtaLog = new JTextArea(12, 35);
   
   //Components - East
   private JLabel jlUsersOnline = new JLabel("Users Online",JLabel.LEFT);
   private JTextArea jtaUsersOnline = new JTextArea(12,10);
   
   //Components - South
   private JLabel jlSentence = new JLabel("Sentence: ");
   private JTextField jtfSentence = new JTextField(25);
   private JButton jbSend = new JButton("Send");

   // IO attributes
   private PrintWriter pwt = null;
   private Scanner scn = null;

   // OTHER attributes
   public static final int SERVER_PORT = 32001;
   private Socket socket = null;
   private String name;

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
       this.setSize(600, 400);
      //this.setLocation(100, 50);
      this.setLocationRelativeTo(null);  // *** this will center your app ***

      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setResizable(false);
      
      //Set name to anonymous by default
      name = "Anonymous";
      
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
            jpRow2.add(jlName);
            jpRow2.add(jtfName);
            jpRow2.add(jbSetName);
            
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
      
      JPanel jpEast = new JPanel(new GridLayout(0,1));
      //jpEast.add(jlUsersOnline);
      jpEast.add(new JScrollPane(jtaUsersOnline));
      this.add(jpEast, BorderLayout.EAST);
      
      JPanel jpSouth = new JPanel(new GridLayout(0,1));
      jpSouth.add(jlSentence);
      jpSouth.add(jtfSentence);
      jpSouth.add(jbSend);
      this.add(jpSouth, BorderLayout.SOUTH);
      
      // Listen for the buttons
      jbConnect.addActionListener(this);
      jbSend.addActionListener(this);
      jbSetName.addActionListener(this);
      
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
         case "Set Name":
            doSetName();
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
         ChatInner ci = new ChatInner(socket);
         ci.start(); 

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
      }
   
  private void doSetName(){
   name = jtfName.getText();
  }
   
   class ChatInner extends Thread   {
      Socket cs;
      public ChatInner(Socket cs)  {
         this.cs = cs;
      }
      
      /**
      * Run method for threads to follow. The main communications between server and client. Will loop forever
      * until the client disconnects.
      */
      public void run() {

         BufferedReader br = null;
      
         try   {
            // Socket connect to server
            pwt = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));

            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            jtaLog.setText("");
            jtaLog.setText("Connected to Server \n");     
            
            while(true)  {
               String serverMsg = br.readLine();    // reads that client is connected - from server
              
               // When Client recieves OK the append the following texts onto rec textarea.
               if(serverMsg == null)  {
                  JOptionPane.showMessageDialog(null, "Server Not Responding");
               }
               
               else if(serverMsg!=null) {                  
                  jtaLog.append(serverMsg+"\n");
                  System.out.println("got here");
               }
               
            }
         
         }
         catch(UnknownHostException uhe) {
			jtaLog.setText("Unable to connect to host.");
			return;
		   }
         catch(NullPointerException npe)  {System.out.println("Server Not Responding");}
		   catch(IOException ie) {
		   	jtaLog.setText(ie.getMessage() + "\nIOException communicating with host.");
		   	return;
		   } 
      } // End of RUn  
   } // End of inner class


}