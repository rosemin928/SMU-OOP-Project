import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Pattern;

public class MembershipForm {

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("회원가입");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //'닫기' 버튼 클릭하면 프로그램 종료
        frame.setLayout(null);

        JLabel title = new JLabel("Travelmate");
        title.setBounds(70, 100, 100, 100);
        title.setFont(new Font("SanSerif", Font.BOLD, 18));
        title.setForeground(new Color(0, 0, 139));

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel idLabel = new JLabel("아이디: ");
        JTextField idField = new JTextField();

        JLabel passwordLabel = new JLabel("비밀번호: ");
        JPasswordField passwordField = new JPasswordField();

        JButton registerButton = new JButton("가입하기");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());

                if (isIdDuplicated(id)) {
                    JOptionPane.showMessageDialog(frame, "이미 존재하는 아이디입니다!");
                    return;
                }

                if (!isValidPassword(password)) {
                    JOptionPane.showMessageDialog(frame, "비밀번호는 숫자와 기호가 포함된 6자리 이상의 비밀번호여야 합니다!");
                    return;
                }

                Member member = new Member(id, password);
                saveMemberToDatabase(member);
                JOptionPane.showMessageDialog(frame, "가입 완료!");

                // ChattingServer 실행
                new Thread(ChattingServer::new).start();

                new MainTab(id); // 아이디를 전달
                frame.setVisible(false); // 이 창을 안 보이게 숨김
            }
        });

        frame.setLayout(null);
        panel.setBounds(280, 120, 250, 100); // panel의 위치

        panel.add(idLabel);
        panel.add(idField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(registerButton);

        frame.add(title);
        frame.add(panel);
        frame.setVisible(true);
    }

    private static boolean isIdDuplicated(String id) {
        boolean isDuplicated = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT id FROM members WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            isDuplicated = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return isDuplicated;
    }

    private static boolean isValidPassword(String password) {
        // 비밀번호 유효성 검사: 6자리 이상, 숫자와 기호 포함
        if (password.length() < 6) {
            return false;
        }
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();
        return hasDigit && hasSpecialChar;
    }

    private static void saveMemberToDatabase(Member member) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String query = "INSERT INTO members (id, password) VALUES (?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, null);
        }
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/travelmate"; // 데이터베이스 URL
        String user = "root"; // 데이터베이스 사용자명
        String password = "0000"; // 데이터베이스 비밀번호
        return DriverManager.getConnection(url, user, password);
    }

    private static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class Member {
        private String id;
        private String password;

        public Member(String id, String password) {
            this.id = id;
            this.password = password;
        }

        public String getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "아이디: " + id + ", 비밀번호: " + password;
        }
    }
}


