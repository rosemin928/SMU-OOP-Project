import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LocationOverlay extends JPanel {
    private JButton openRestaurantButton = new JButton("식당");
    private JButton openHotelButton = new JButton("호텔");
    private JButton openTourismButton = new JButton("관광");
    private JTabbedPane tabbedPane = new JTabbedPane();

    public LocationOverlay() {
        setLayout(null);

        add(openRestaurantButton);
        add(openHotelButton);
        add(openTourismButton);

        add(tabbedPane);

        openRestaurantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOverlay("식당 정보", "restaurant_info.txt");
            }
        });

        openHotelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOverlay("호텔 정보", "hotel_info.txt");
            }
        });

        openTourismButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOverlay("관광 정보", "tourism_info.txt");
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonPosition();
            }
        });

        updateButtonPosition();
    }

    private void updateButtonPosition() {
        int mainWidth = getWidth();
        int mainHeight = getHeight();

        double buttonX1Ratio = 0.5;
        double buttonX2Ratio = 0.6;
        double buttonX3Ratio = 0.7;
        double buttonYRatio = 0.05;

        int buttonWidth = 60;
        int buttonHeight = 30;

        int buttonX1 = (int) (mainWidth * buttonX1Ratio) - (buttonWidth / 2);
        int buttonX2 = (int) (mainWidth * buttonX2Ratio) - (buttonWidth / 2);
        int buttonX3 = (int) (mainWidth * buttonX3Ratio) - (buttonWidth / 2);
        int buttonY = (int) (mainHeight * buttonYRatio) - (buttonHeight / 2);

        openRestaurantButton.setBounds(buttonX1, buttonY, buttonWidth, buttonHeight);
        openHotelButton.setBounds(buttonX2, buttonY, buttonWidth, buttonHeight);
        openTourismButton.setBounds(buttonX3, buttonY, buttonWidth, buttonHeight);

        tabbedPane.setBounds(10, mainHeight / 2, mainWidth - 20, mainHeight / 2 - 20);
    }

    private void openOverlay(String info, String fileName) {
        int mainWidth = getWidth();
        int mainHeight = getHeight();

        double overlayWidthRatio = 1;
        double overlayHeightRatio = 1;

        int overlayWidth = (int) (mainWidth * overlayWidthRatio);
        int overlayHeight = (int) (mainHeight * overlayHeightRatio);
        int overlayX = getX() + (int) (mainWidth * 0.08);
        int overlayY = getY() + (int) (mainHeight * 0);

        JDialog overlayDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), info, true);
        overlayDialog.setSize(overlayWidth, overlayHeight);
        overlayDialog.setLocation(overlayX, overlayY);
        overlayDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        ArrayList<JCheckBox> checkBoxList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] infoParts = line.split(";");
                JCheckBox checkBox = new JCheckBox(infoParts[0]);
                JTextArea infoArea = new JTextArea(infoParts[1] + "\n" + infoParts[2]);
                infoArea.setEditable(false);
                contentPanel.add(checkBox);
                contentPanel.add(infoArea);

                checkBoxList.add(checkBox);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        overlayDialog.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add to Tab");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel tabContentPanel = new JPanel();
                tabContentPanel.setLayout(new BoxLayout(tabContentPanel, BoxLayout.Y_AXIS));

                for (JCheckBox checkBox : checkBoxList) {
                    if (checkBox.isSelected()) {
                        JTextArea infoArea = new JTextArea(checkBox.getText());
                        infoArea.setEditable(false);
                        tabContentPanel.add(infoArea);
                    }
                }

                tabbedPane.addTab(info, tabContentPanel);
                overlayDialog.dispose();
            }
        });

        overlayDialog.add(addButton, BorderLayout.SOUTH);

        overlayDialog.setVisible(true);
    }
}