import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChattingServer extends JFrame {
    private ArrayList<MultiServerThread> list;
    private JTextArea ta;
    private JTextField tf;
    private ServerSocket serverSocket;

    public ChattingServer() {
        setTitle("채팅 서버");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ta = new JTextArea();
        add(new JScrollPane(ta));
        tf = new JTextField();
        tf.setEditable(false);
        add(tf, BorderLayout.SOUTH);
        setSize(300, 300);
        setVisible(true);

        list = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(5000);
            tf.setText("서버 정상 실행 중입니다.");

            while (true) {
                Socket socket = serverSocket.accept();
                MultiServerThread mst = new MultiServerThread(socket);
                list.add(mst);
                mst.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChattingServer();
    }

    public class MultiServerThread extends Thread {
        private Socket socket;
        private BufferedReader br_in;
        private PrintWriter pw;
        private boolean isStop = false;

        public MultiServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                br_in = new BufferedReader(new InputStreamReader(is));
                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)), true);

                while (!isStop) {
                    String message = br_in.readLine();
                    if (message == null) {
                        break;
                    }
                    String[] str = message.split("#");

                    if (str[1].equals("exit")) {
                        broadCasting(message);
                        isStop = true;
                    } else {
                        broadCasting(message);
                    }
                }

                list.remove(this);
                ta.append(socket.getInetAddress() + " IP주소의 사용자께서 종료하셨습니다.\n");
                tf.setText("남은 사용자 수: " + list.size());
            } catch (Exception e) {
                list.remove(this);
                ta.append(socket.getInetAddress() + " IP주소의 사용자께서 비정상 종료했습니다.");
                tf.setText("남은 사용자 수: " + list.size());
            }
        }

        public void broadCasting(String message) {
            for (MultiServerThread ct : list) {
                ct.send(message);
            }
            updateServerText(message + "\n");
        }

        public void send(String message) {
            pw.println(message);
        }

        private void updateServerText(String message) {
            SwingUtilities.invokeLater(() -> ta.append(message));
        }

        private void updateClientCount() {
            SwingUtilities.invokeLater(() -> tf.setText("남은 사용자 수: " + list.size()));
        }

    }
}