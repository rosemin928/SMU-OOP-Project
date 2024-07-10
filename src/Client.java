import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;

public class Client extends JPanel implements ActionListener, Runnable {
    private static final String SERVER_IP = "10.101.38.30"; // 서버 IP 주소를 입력하세요

    private Socket socket;
    private JTextField jtf;
    private JTextArea jta;
    private JLabel jlb1, jlb2;
    private JPanel jp1, jp2;
    private String ip;
    private String chatName;
    private JButton jbtn;

    private BufferedReader br_in;
    private PrintWriter pw;

    public Client(String userId) {
        chatName = userId;
        ip = SERVER_IP;

        setLayout(new BorderLayout());

        jp1 = new JPanel();
        jp1.setLayout(new BorderLayout());
        jtf = new JTextField(30);
        jbtn = new JButton("종료");
        jp1.add(jbtn, BorderLayout.EAST);
        jp1.add(jtf, BorderLayout.CENTER);

        jp2 = new JPanel();
        jp2.setLayout(new BorderLayout());
        jlb1 = new JLabel("대화명: [[" + chatName + "]]");
        jlb1.setBackground(Color.YELLOW);
        jlb2 = new JLabel("서버 IP 주소: " + ip);
        jlb2.setBackground(Color.WHITE);
        jp2.add(jlb1, BorderLayout.CENTER);
        jp2.add(jlb2, BorderLayout.EAST);

        jta = new JTextArea("", 10, 50);
        jta.setBackground(Color.WHITE);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(jp1, BorderLayout.SOUTH);
        add(jp2, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);

        jtf.addActionListener(this);
        jbtn.addActionListener(this);

        jta.setEditable(false);

        init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String msg = jtf.getText();
        if (obj == jtf) {
            if (msg == null || msg.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "글을 쓰세요", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    pw.println(chatName + "#" + msg);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                jtf.setText("");
            }
        } else if (obj == jbtn) {
            try {
                pw.println(chatName + "#exit");
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            System.exit(0);
        }
    }

    public void init() {
        try {
            socket = new Socket(ip, 5000);
            br_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            Thread t = new Thread(this);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message = null;
        String[] receiveMsg = null;
        boolean isStop = false;
        while (!isStop) {
            try {
                message = br_in.readLine();
                if (message == null) {
                    break;
                }
                receiveMsg = message.split("#");
            } catch (Exception e) {
                e.printStackTrace();
                isStop = true;
            }
            if (receiveMsg != null && receiveMsg.length == 2) {
                if (receiveMsg[1].equals("exit")) {
                    if (receiveMsg[0].equals(chatName)) {
                        System.exit(0);
                    } else {
                        jta.append(receiveMsg[0] + " 님이 종료했습니다\n");
                        jta.setCaretPosition(jta.getDocument().getLength());
                    }
                } else {
                    jta.append(receiveMsg[0] + ":" + receiveMsg[1] + "\n");
                    jta.setCaretPosition(jta.getDocument().getLength());
                }
            }
        }
    }
}
