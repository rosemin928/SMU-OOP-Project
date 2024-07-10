import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BudgetManager extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField typeField;
    private JTextField detailsField;
    private JTextField amountField;
    private JLabel totalLabel;

    public BudgetManager() {
        setLayout(new BorderLayout());

        String[] columnNames = {"종류", "상세 정보", "금액"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 7));
        typeField = new JTextField();
        detailsField = new JTextField();
        amountField = new JTextField();
        JButton addButton = new JButton("+");
        addButton.addActionListener(new AddButtonListener());

        inputPanel.add(new JLabel("종류", SwingConstants.CENTER));
        inputPanel.add(typeField);
        inputPanel.add(new JLabel("상세 정보", SwingConstants.CENTER));
        inputPanel.add(detailsField);
        inputPanel.add(new JLabel("금액", SwingConstants.CENTER));
        inputPanel.add(amountField);
        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.NORTH);

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BorderLayout());
        totalLabel = new JLabel("합계: 0", SwingConstants.CENTER);
        totalLabel.setFont(new Font("돋움", Font.BOLD, 20));
        totalPanel.add(totalLabel, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.SOUTH);
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String type = typeField.getText();
            String details = detailsField.getText();
            String amountStr = amountField.getText();
            if (type.isEmpty() || details.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(BudgetManager.this, "모든 필드를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int amount = Integer.parseInt(amountStr);
                tableModel.addRow(new Object[]{type, details, amount});
                updateTotal();
                typeField.setText("");
                detailsField.setText("");
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(BudgetManager.this, "금액은 숫자로 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateTotal() {
        int total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (int) tableModel.getValueAt(i, 2);
        }
        totalLabel.setText("합계: " + total);
    }
}