import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import map.AddressVO;

public class ShowMap extends JPanel {
    JTextField address;
    JLabel resAddress, resX, resY, jibunAddress;
    JLabel imageLabel;
    JLabel[] addressLabels; // 주소를 출력할 라벨 배열
    int addressCount; // 저장된 주소 개수
    JPanel pan1; // pan1을 멤버 변수로 선언
    JLabel routeLabel; // 계산된 경로를 출력할 라벨
    List<AddressInfo> addressList; // 주소 리스트

    public ShowMap() {
        addressLabels = new JLabel[4]; // 최대 4개의 주소를 출력할 라벨 배열 초기화
        addressCount = 0; // 초기 저장된 주소 개수는 0
        addressList = new ArrayList<>(); // 주소 리스트 초기화
        initGUI();
        clearDatabase(); // 앱이 시작될 때 데이터베이스 초기화
    }

    public void initGUI() {
        setLayout(new BorderLayout());

        imageLabel = new JLabel("");
        JPanel pan = new JPanel();
        JLabel addressLbl = new JLabel("주소입력");
        address = new JTextField(40);
        JButton btn = new JButton("주소 입력");
        JButton calculateButton = new JButton("최적의 경로 계산하기");
        pan.add(addressLbl);
        pan.add(address);
        pan.add(btn);
        btn.addActionListener(new NaverMap(this)); // NaverMap 클래스의 인스턴스를 생성하여 액션 리스너로 등록
        pan.add(calculateButton);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addressList.size() >= 2) {
                    List<AddressInfo> optimalRoute = findOptimalRoute();
                    StringBuilder route = new StringBuilder("최적의 경로: ");
                    for (AddressInfo addr : optimalRoute) {
                        route.append(addr.getAddress()).append(" -> ");
                    }
                    route.setLength(route.length() - 4); // 마지막 " -> " 제거
                    routeLabel.setText(route.toString());
                } else {
                    routeLabel.setText("주소를 2개 이상 입력해주세요.");
                }
            }
        });

        pan1 = new JPanel();
        pan1.setLayout(new GridLayout(4, 1));

        routeLabel = new JLabel("최적의 경로: ");

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(pan1, BorderLayout.NORTH);
        southPanel.add(routeLabel, BorderLayout.SOUTH);

        add(BorderLayout.NORTH, pan);
        add(BorderLayout.CENTER, imageLabel);
        add(BorderLayout.SOUTH, southPanel);
    }

    // 네이버 API에서 주소 정보를 가져오는 메서드
    public void addAddressInfo(AddressVO vo) {
        if (addressCount < 4) {
            // 주소 번호와 함께 출력할 라벨 생성
            addressLabels[addressCount] = new JLabel((addressCount + 1) + ". " + vo.getRoadAddress());
            // 패널에 추가
            pan1.add(addressLabels[addressCount]);
            // 개수 증가
            addressCount++;
            // 패널을 새로 그리기
            validate();
            repaint();

            // AddressVO 객체를 사용하여 AddressInfo 객체 생성
            double longitude = Double.parseDouble(vo.getX());
            double latitude = Double.parseDouble(vo.getY());
            AddressInfo addressInfo = new AddressInfo(vo.getRoadAddress(), longitude, latitude);
            addressList.add(addressInfo);

            // MySQL에 데이터 저장
            saveAddressInfoToDatabase(addressInfo);
        }
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2){
        // 주어진 위도와 경도를 라디안으로 변환
        double theta = Math.toRadians(lon1 - lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // 구면 코사인 법칙을 이용하여 거리 계산
        double dist = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(theta);
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344; // 단위 km로 변환

        return dist;
    }

    private List<AddressInfo> findOptimalRoute() {
        if (addressList.size() <= 2) {
            return addressList;
        }

        List<AddressInfo> route = new ArrayList<>();
        List<AddressInfo> remaining = new ArrayList<>(addressList);

        AddressInfo[] current = { remaining.get(0) };
        route.add(current[0]);
        remaining.remove(current[0]);

        while (!remaining.isEmpty()) {
            AddressInfo next = Collections.min(remaining, Comparator.comparingDouble(a -> calculateDistance(
                    current[0].getLatitude(), current[0].getLongitude(), a.getLatitude(), a.getLongitude())));
            route.add(next);
            remaining.remove(next);
            current[0] = next;
        }

        return route;
    }

    class AddressInfo {
        private String address;
        private double longitude;
        private double latitude;

        public AddressInfo(String address, double longitude, double latitude) {
            this.address = address;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public String getAddress() {
            return address;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }

    private void saveAddressInfoToDatabase(AddressInfo addressInfo) {
        String url = "jdbc:mysql://localhost:3306/travelmate";
        String user = "root";
        String password = "0000";

        String query = "INSERT INTO address_info (address, longitude, latitude) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, addressInfo.getAddress());
            pstmt.setDouble(2, addressInfo.getLongitude());
            pstmt.setDouble(3, addressInfo.getLatitude());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearDatabase() {
        String url = "jdbc:mysql://localhost:3306/travelmate";
        String user = "root";
        String password = "0000";

        String query = "DELETE FROM address_info";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frm = new JFrame("Map View");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.add(new ShowMap());
        frm.setSize(600, 400);
        frm.setVisible(true);
    }
}
