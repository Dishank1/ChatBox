import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * TCPMTServer ... goes with TCPClient1
 * Allows multiple connect/send/disconnect cycles
 * As many clients as we wish ... one thread per client
 * @author Dishank Jhaveri, Tiffany Ellis
 * @version 9-16-2017
 */
public class TCPMTServerStartStop extends JFrame {
   // GUI Components
   private JButton jbStart = new JButton("Start");
   private JLabel jlLog = new JLabel("Log:");
   private JTextArea jtaLog = new JTextArea(10, 35);

   // Socket stuff
   private ServerSocket sSocket = null;
   public static final int SERVER_PORT = 32001;
   private ServerThread serverThread = null;
   static Vector<ClientThread> activeClients = new Vector<>();
     
    // counter for clients
    static int clientCount = 0;   
   /**
    * main - main program
    */
   public static void main(String[] args) {
      new TCPMTServerStartStop();
   }
   
   /**
    * Constructor, draw and set up GUI
    * Do server stuff
    */
   public TCPMTServerStartStop() {
      // Window setup
      this.setTitle("TCPServer");
      this.setSize(450, 250);
      this.setLocation(600, 50);
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
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
            sSocket = new ServerSocket(SERVER_PORT);
         }
         catch(IOException ioe) {
            jtaLog.append("IO Exception (1): "+ ioe);
            return;
         }
         
         while(true) {
            // Socket for the client
            Socket cSocket = null;
            
            try {
               // Wait for a connection and set up IO
               cSocket = sSocket.accept();
            }
            catch(IOException ioe) {
               // Happens when sSocket is closed in stop (below)
               // and the accept (above) is blocked. This is OK.
               jtaLog.append("IO Exception (2): "+ ioe);
               return;
            }   
            
            // Create a thread for the client
            ClientThread ct = new ClientThread(cSocket);
            activeClients.add(ct);
            ct.start();
            clientCount++;      
         }
      }
      
      public void stopServer() {
         try {
            sSocket.close();  // This terminates any blocked accepts
         }
         catch(Exception e) {
            jtaLog.append("Exception: " + e);
         }
      }
   }
   
   /**
    * Class ClientThread
    * A thread PER client to do the server side
    * stuff for one client
    */
   class ClientThread extends Thread {
      // Since attributes are per-object items, each ClientThread has its OWN
      // socket, unique to that client
      private Socket cSocket;
      private String label = "";
      private String name;
      private PrintWriter pwt;

      // Constructor for ClientThread
      public ClientThread(Socket _cSocket) {
         cSocket = _cSocket;
         label = cSocket.getInetAddress().getHostAddress() + ":" + cSocket.getPort() + " :: ";
      }
      
      // main program for a ClientThread
      public void run() {
         Scanner scn = null;
         pwt = null;
         
         jtaLog.append(label + "Client connected!\n");
         
         try {
            // Set up IO
            scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
            pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
         }
         catch(IOException ioe) {
            jtaLog.append(label + "IO Exception (ClientThread): "+ ioe);
            return;
         }

         // Repeatedly, read a line from the client and send back
         // uppercase version
         while(scn.hasNextLine()) {
            String message = scn.nextLine();
            jtaLog.append(label + "Received: " + message + "\n");
            for(ClientThread ct : activeClients){
               System.out.println(activeClients.size());
                ct.pwt.println(message.toUpperCase());
                ct.pwt.flush();
            }
             jtaLog.append(label + "Replied: " + message.toUpperCase() + "\n");
         }
     
         // on EOF, client has disconnected 
         try {
            cSocket.close();
            scn.close();
            pwt.close();
            //Make it so the print writers removed from the array
         }
         catch(IOException ioe) {
            jtaLog.append(label + "IO Exception (3): "+ ioe);
            return;
         }
         
         jtaLog.append(label + "Client disconnected!\n");
      }  
   } // End of inner class
}