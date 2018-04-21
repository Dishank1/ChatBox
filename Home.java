import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {

    private JPanel contentPane;
    JLabel chatTitle = new JLabel("Welcome to Chat Box!");
    JButton btnTChat = new JButton("TCP/IP Chat");
    JButton btnUChat = new JButton("UDP Chat");
    JButton btnTServer = new JButton("TCP/IP Server");
    JButton btnUServer = new JButton("UDP Server");
    JLabel lblServer = new JLabel("Start/Stop Server");


    /**
     * Create the frame.
     */
    public Home() {

        setResizable(false);
        this.setTitle("Chat Box");
        this.setSize(600, 400);
        this.setLocation(100, 50);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);


        chatTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 28));
        chatTitle.setHorizontalAlignment(SwingConstants.CENTER);
        chatTitle.setBounds(0, 0, 600, 67);
        contentPane.add(chatTitle);


        btnTChat.setBounds(10, 63, 294, 168);
        btnTChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TCPClient1().setVisible(true);
            }
        });
        contentPane.add(btnTChat);


        btnUChat.setBounds(312, 63, 282, 168);
        btnUChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UDPClient1().setVisible(true);
            }
        });
        contentPane.add(btnUChat);


        btnTServer.setBounds(6, 277, 294, 83);
        btnTServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TCPMTServerStartStop().setVisible(true);
            }
        });
        contentPane.add(btnTServer);


        btnUServer.setBounds(312, 277, 282, 83);
        btnUServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UDPServerStartStop().setVisible(true);
            }
        });
        contentPane.add(btnUServer);


        lblServer.setHorizontalAlignment(SwingConstants.CENTER);
        lblServer.setBounds(0, 243, 600, 33);
        contentPane.add(lblServer);

        this.setVisible(true);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    Home frame = new Home();
//                    frame.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        new Home();
    }

}
