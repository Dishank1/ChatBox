import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * UDPServerStartStop ... goes with UDPClient1
 * Allows multiple clients sending to the server at once
 * NOT multi-threaded because the amount of code to serve each
 * client request is short.
 * @author Dishank Jhaveri, Tiffany Ellis
  * @version 9-16-2017
 */
public class UDPServerStartStop extends JFrame {
   // GUI Components
   private JButton jbStart = new JButton("Start");
   private JLabel jlLog = new JLabel("Log:");
   private JTextArea jtaLog = new JTextArea(10, 35);

   // Socket stuff
   private DatagramSocket socket;
   private ArrayList<InetAddress> clAddresses;
   private ArrayList<Integer> clPorts;
   private HashSet<String> exClients;
   public static final int SERVER_PORT = 32001;
   public static final int PACKET_MAX = 1500;
   private ServerThread serverThread;
   
   /**
    * main - main program
    */
   public static void main(String[] args) {
      new UDPServerStartStop();
   }
   
   /**
    * Constructor, draw and set up GUI
    * Do server stuff
    */
   public UDPServerStartStop() {


       clAddresses = new ArrayList<>();
       clPorts = new ArrayList<>();
       exClients = new HashSet<>();
       socket = null;
       serverThread = null;

      // Window setup
      this.setTitle("UDPServer");
      this.setSize(450, 250);
      this.setLocation(600, 50);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // NORTH components (Start/Stop button)
      JPanel jpNorth = new JPanel();
      jpNorth.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jpNorth.add(jbStart);
      this.add(jpNorth, BorderLayout.NORTH);
      jbStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae){
            switch(ae.getActionCommand()) {
               case "Start":
                  doStart();
                  break;
               case "Stop":
                  doStop();
                  break;
            }
         }
      } );
   
      // CENTER components
      JPanel jpCenter = new JPanel();
      jpCenter.add(jlLog);
      jpCenter.add(new JScrollPane(jtaLog));
      this.add(jpCenter, BorderLayout.CENTER);
      
      this.setVisible(true);
   } 
   
   public void doStart() {
      jbStart.setText("Stop");

      serverThread = new ServerThread();
      serverThread.start();
   }
   
   public void doStop() {
      jbStart.setText("Start");
      serverThread.stopServer();
   }
   
   /**
    * Class ServerThread
    * A thread used to start the server when the Start button is pressed
    * Also, the stop method will stop the server
    */
   class ServerThread extends Thread {
      public void run() {
         // Server stuff ... wait for a connection and process it
         try {
            socket = new DatagramSocket(SERVER_PORT);
         }
         catch(IOException ioe) {
            jtaLog.append("IO Exception (1): "+ ioe);
            return;
         }
         
         while(true) { // repeatedly wait for a request and send a reply
            // now, wait for the request
            // Set up a buffer to hold the request ... must be big enough for any request
            byte[] request = new byte[PACKET_MAX];
            // Make an empty DatagramPacket for the request
            DatagramPacket requestPkt = new DatagramPacket(request, request.length);
            
            // await the reply
            try {
               socket.receive(requestPkt);



            }
            catch(IOException ioe) {
               if(ioe.toString().indexOf("socket closed") < 0 )
                  JOptionPane.showMessageDialog(null, "Cannot receive packet: " + ioe,
                     "Failed Receive", JOptionPane.ERROR_MESSAGE);
               return;
            }


            InetAddress clientAddr = requestPkt.getAddress();
            int port = requestPkt.getPort();


            String id = clientAddr.toString()+" , "+port;
            // adds client address and port to lists if id does not exist
             if (!exClients.contains(id)) {
                 exClients.add( id );
                 clPorts.add( port );
                 clAddresses.add(clientAddr);
             }


             // Get the string from the packet
            String requestStr = new String(requestPkt.getData(), requestPkt.getOffset(), requestPkt.getLength());
            jtaLog.append("REQUEST: " + requestStr + "\n");
            
            // Formulate a reply
            String replyStr = requestStr.toUpperCase();
                  
            // Build the packet. NOTE: We use the requestPkt's address and port to send this
            // reply back to whoever sent us the request
            byte[] reply = replyStr.getBytes();
            DatagramPacket replyPkt;

             for (int i=0; i < clAddresses.size(); i++) {
                 InetAddress cl = clAddresses.get(i);
                 int cp = clPorts.get(i);
                 replyPkt = new DatagramPacket(reply, reply.length, cl, cp);

                 // send the packet
                 try {
                     socket.send(replyPkt);
                 } catch (IOException ioe) {
                     JOptionPane.showMessageDialog(null, "Cannot send packet: " + ioe,
                             "Failed Send", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
             }
         }
      }
            
      public void stopServer() {
         try {
            socket.close();  // This terminates any blocked accepts
         }
         catch(Exception e) {
            jtaLog.append("Exception: " + e);
         }
      }

   }
}