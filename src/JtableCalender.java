import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

class JtableCalender extends JPanel {
    private JLabel labelTable, labelTableIn, contentLabel, dayLabel, timeLabel, dayLabelDel, timeLabelDel;
    private JButton button, buttonOk, buttonQuit, buttonAdd, buttonDel, buttonDelOK, buttonDelQuit, buttonDelAll;
    private JPanel panelMenu, panelDay, panelDaySub, addPanel, timePanel, radioPanel, panelDel, panelDelSub, timePanelDel, panelDelSubAll;
    private JTextField textfield, contentField;
    private JDialog dialogSet, dialogAdd, dialogDel;
    private JTable table;
    private DefaultTableModel model;
    private JSpinner daySpinner, startSpinner , endSpinner, daySpinnerDel, startSpinnerDel, endSpinnerDel;
    private JRadioButton mealButton, restButton, moveButton, experienceButton;
    private ButtonGroup travelTypeGroup;
    private int radioInt = 0;
    private CellColorRenderer cellColorRenderer;

    public JtableCalender(JFrame parentFrame) {

        String data[][] = {
                {"6시", "", "", "", ""},{"7시", "", "", "", ""},{"8시", "", "", "", ""},{"9시", "", "", "", ""},{"10시", "", "", "", ""},
                {"11시", "", "", "", ""},{"12시", "", "", "", ""},{"13시", "", "", "", ""},{"14시", "", "", "", ""},{"15시", "", "", "", ""},
                {"16시", "", "", "", ""},{"17시", "", "", "", ""},{"18시", "", "", "", ""},{"19시", "", "", "", ""},{"20시", "", "", "", ""},
                {"21시", "", "", "", ""},{"22시", "", "", "", ""},{"23시", "", "", "", ""},{"24시", "", "", "", ""}
        };

        String columnName[] = {
                "", "1일차", "2일차", "3일차","4일차", "5일차", "6일차", "7일차", "8일차", "9일차", "10일차", "11일차", "12일차", "13일차",
                "14일차", "15일차", "16일차", "17일차", "18일차", "19일차", "20일차", "21일차", "22일차", "23일차", "24일차", "25일차", "26일차",
                "27일차", "28일차", "29일차", "30일차", "31일차", "32일차", "33일차", "34일차", "35일차", "36일차", "37일차", "38일차", "39일차", "40일차"
        };

        setLayout(new BorderLayout());

        dialogSet = new JDialog(parentFrame, "일정표 만들기", true);
        dialogAdd = new JDialog(parentFrame, "일정 등록하기", true);
        dialogDel = new JDialog(parentFrame, "일정 삭제하기", true);

        labelTable = new JLabel("일정표");
        button = new JButton("일정표 만들기");
        buttonAdd = new JButton("일정 등록하기");
        buttonDel = new JButton("일정 삭제하기");

        button.addActionListener(e -> {
            dialogSet.setVisible(true);
        });
        buttonAdd.addActionListener(e ->  {
            resetDialogAdd();
            dialogAdd.setVisible(true);
        });
        buttonDel.addActionListener(e -> {
            resetDialogDel();
            dialogDel.setVisible(true);
        });

        labelTableIn = new JLabel("여행 일수");
        textfield = new JTextField();
        buttonOk = new JButton("확인");

        panelDay = new JPanel(new BorderLayout());
        panelDay.add(labelTableIn, BorderLayout.NORTH);
        panelDay.add(textfield, BorderLayout.CENTER);

        buttonOk.addActionListener(e -> {
            int columnCount = Integer.parseInt(textfield.getText());
            resetColumn(columnCount, columnName);
            dialogSet.setVisible(false);
        });

        buttonQuit = new JButton("취소");
        buttonQuit.addActionListener(e -> dialogSet.setVisible(false));

        panelMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelMenu.add(labelTable);
        panelMenu.add(button);
        panelMenu.add(buttonAdd);
        panelMenu.add(buttonDel);

        panelDaySub = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panelDaySub.add(buttonOk);
        panelDaySub.add(buttonQuit);
        panelDay.add(panelDaySub, BorderLayout.SOUTH);

        addPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        contentLabel = new JLabel("일정 내용:");
        contentField = new JTextField();

        dayLabel = new JLabel("일차:");
        daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 40, 1));
        timeLabel = new JLabel("시간:");
        startSpinner = new JSpinner(new SpinnerNumberModel(6, 6, 24, 1));
        endSpinner = new JSpinner(new SpinnerNumberModel(6, 6, 24, 1));
        timePanel = new JPanel(new FlowLayout());
        timePanel.add(startSpinner);
        timePanel.add(new JLabel(" ~ "));
        timePanel.add(endSpinner);

        mealButton = new JRadioButton("식사");
        mealButton.setForeground(Color.ORANGE);
        restButton = new JRadioButton("휴식");
        restButton.setForeground(Color.CYAN);
        moveButton = new JRadioButton("이동");
        moveButton.setForeground(Color.GRAY);
        experienceButton = new JRadioButton("체험");
        experienceButton.setForeground(Color.GREEN);

        mealButton.addActionListener(e -> radioInt = 1);
        restButton.addActionListener(e -> radioInt = 2);
        moveButton.addActionListener(e -> radioInt = 3);
        experienceButton.addActionListener(e -> radioInt = 4);

        travelTypeGroup = new ButtonGroup();
        travelTypeGroup.add(mealButton);
        travelTypeGroup.add(restButton);
        travelTypeGroup.add(moveButton);
        travelTypeGroup.add(experienceButton);

        radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5, 5));
        radioPanel.add(mealButton);
        radioPanel.add(restButton);
        radioPanel.add(moveButton);
        radioPanel.add(experienceButton);

        addPanel.add(contentLabel);
        addPanel.add(contentField);
        addPanel.add(dayLabel);
        addPanel.add(daySpinner);
        addPanel.add(timeLabel);
        addPanel.add(timePanel);
        addPanel.add(new JLabel("여행 종류:"));
        addPanel.add(radioPanel);

        dialogAdd.add(addPanel, BorderLayout.CENTER);
        dialogAdd.setSize(400, 400);
        dialogAdd.setLocationRelativeTo(parentFrame);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        dialogAdd.add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            String content = contentField.getText();
            int day = (int) daySpinner.getValue();
            int startHour = (int) startSpinner.getValue();
            int endHour = (int) endSpinner.getValue();
            addSchedule(content, day, startHour, endHour, radioInt);
            dialogAdd.setVisible(false);
        });

        cancelButton.addActionListener(e -> dialogAdd.setVisible(false));

        panelDel = new JPanel(new GridLayout(3, 2, 5, 5));
        panelDelSub = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDelSubAll = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonDelOK = new JButton("확인");
        buttonDelQuit = new JButton("취소");
        buttonDelAll = new JButton("전부 삭제하기");

        dayLabelDel = new JLabel("일차:");
        daySpinnerDel = new JSpinner(new SpinnerNumberModel(1, 1, 40, 1));
        timeLabelDel = new JLabel("시간:");
        startSpinnerDel = new JSpinner(new SpinnerNumberModel(6, 6, 24, 1));
        endSpinnerDel = new JSpinner(new SpinnerNumberModel(6, 6, 24, 1));
        timePanelDel = new JPanel(new FlowLayout());
        timePanelDel.add(startSpinnerDel);
        timePanelDel.add(new JLabel(" ~ "));
        timePanelDel.add(endSpinnerDel);

        panelDel.add(dayLabelDel);
        panelDel.add(daySpinnerDel);
        panelDel.add(timeLabelDel);
        panelDel.add(timePanelDel);

        panelDelSub.add(buttonDelOK);
        panelDelSub.add(buttonDelQuit);
        panelDelSubAll.add(buttonDelAll);
        panelDel.add(panelDelSubAll);
        panelDel.add(panelDelSub);


        dialogDel.add(panelDel, BorderLayout.CENTER);
        dialogDel.setSize(400, 300);
        dialogDel.setLocationRelativeTo(parentFrame);

        buttonDelOK.addActionListener(e ->{
            int dayDel = (int) daySpinnerDel.getValue();
            int startHourDel = (int) startSpinnerDel.getValue();
            int endHourDel = (int) endSpinnerDel.getValue();
            delSchedule(dayDel, startHourDel, endHourDel);
            dialogDel.setVisible(false);
        });

        buttonDelQuit.addActionListener(e-> dialogDel.setVisible(false));

        buttonDelAll.addActionListener(e -> {
            clearAllSchedules();
            dialogDel.setVisible(false);
        });

        model = new DefaultTableModel(data, columnName) {
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀을 수정 불가능하도록 설정
            }
        };
        table = new JTable(model);

        cellColorRenderer = new CellColorRenderer();
        table.setDefaultRenderer(Object.class, cellColorRenderer);

        JScrollPane sp = new JScrollPane(table);

        add(panelMenu, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);

        dialogSet.add(panelDay);
        dialogSet.setSize(300, 150);
        dialogSet.setLocationRelativeTo(parentFrame);

        model.setColumnCount(5);
    }

    public class CellColorRenderer extends DefaultTableCellRenderer {
        private Map<Point, Color> cellColors = new HashMap<>();

        public void setCellColor(int row, int column, Color color) {
            cellColors.put(new Point(row, column), color);
        }

        public void clearCellColors() {
            cellColors.clear();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color color = cellColors.get(new Point(row, column));
            if (color != null) {
                c.setBackground(color);
            } else {
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    }

    public void resetColumn(int columnCount, String[] columnName) {
        model.setColumnIdentifiers(columnName);
        model.setColumnCount(columnCount + 1);
    }

    public void addSchedule(String content, int day, int startHour, int endHour, int radioInt) {
        try {
            if (day > model.getColumnCount() - 1) {
                throw new IllegalArgumentException("유효하지 않는 일차입니다.");
            }

            if (startHour > endHour) {
                throw new IllegalArgumentException("시작 시간이 종료 시간보다 클 수 없습니다.");
            }

            Color color = Color.WHITE;
            if (radioInt == 1) {
                color = Color.ORANGE;
            } else if (radioInt == 2) {
                color = Color.CYAN;
            } else if (radioInt == 3) {
                color = Color.GRAY;
            } else if (radioInt == 4) {
                color = Color.GREEN;
            }

            for (int i = startHour - 6; i <= endHour - 6; i++) {
                model.setValueAt(content, i, day);
                cellColorRenderer.setCellColor(i, day, color);
            }
            table.repaint();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogAdd, ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void delSchedule(int dayDel, int startHourDel, int endHourDel) {
        try {
            if (dayDel > model.getColumnCount() - 1) {
                throw new IllegalArgumentException("유효하지 않는 일차입니다.");
            }

            if (startHourDel > endHourDel) {
                throw new IllegalArgumentException("시작 시간이 종료 시간보다 클 수 없습니다.");
            }

            for (int i = startHourDel - 6; i <= endHourDel - 6; i++) {
                model.setValueAt("", i, dayDel);
                cellColorRenderer.setCellColor(i, dayDel, Color.WHITE);
            }
            table.repaint();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogDel, ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void clearAllSchedules() {
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 1; col < model.getColumnCount(); col++) {
                model.setValueAt("", row, col);
                cellColorRenderer.setCellColor(row, col, Color.WHITE);
            }
        }
        table.repaint();
    }

    public void resetDialogAdd() {
        daySpinner.setValue(1);
        startSpinner.setValue(6);
        endSpinner.setValue(6);
        contentField.setText("");
        travelTypeGroup.clearSelection();
        radioInt = 0;
    }

    public void resetDialogDel() {
        daySpinnerDel.setValue(1);
        startSpinnerDel.setValue(6);
        endSpinnerDel.setValue(6);
    }

}