import javax.swing.*;
import java.awt.*;

public class MainTab extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainTab(String userId) {

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

        setTitle("Travelmate");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(100, 100, 200));

        JLabel appNameLabel = new JLabel("Travelmate");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(appNameLabel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JButton btnSchedule = createNavButton("일정표");
        JButton btnBudget = createNavButton("예산관리");
        JButton btnMap = createNavButton("지도");
        JButton btnLocations = createNavButton("식당호텔관광");
        JButton btnChat = createNavButton("채팅");

        navPanel.add(btnSchedule);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(btnBudget);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(btnMap);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(btnLocations);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(btnChat);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        cardPanel.add(new JtableCalender(this), "일정표");
        cardPanel.add(new BudgetManager(), "예산관리");
        cardPanel.add(new ShowMap(), "지도");
        cardPanel.add(new LocationOverlay(), "식당호텔관광");

        Client clientPanel = new Client(userId);
        cardPanel.add(clientPanel, "채팅");

        btnSchedule.addActionListener(e -> cardLayout.show(cardPanel, "일정표"));
        btnBudget.addActionListener(e -> cardLayout.show(cardPanel, "예산관리"));
        btnMap.addActionListener(e -> cardLayout.show(cardPanel, "지도"));
        btnLocations.addActionListener(e -> cardLayout.show(cardPanel, "식당호텔관광"));
        btnChat.addActionListener(e -> cardLayout.show(cardPanel, "채팅"));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.NORTH);

        JLabel placeholderLabel = new JLabel(userId + "님 환영합니다!");
        placeholderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(placeholderLabel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 35));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.WHITE);
        return button;
    }

    public static void main(String[] args) {
        // 서버 실행
        new Thread(ChattingServer::new).start();

        // 클라이언트 실행
        SwingUtilities.invokeLater(() -> new MainTab("User123"));
    }
}