import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import map.AddressVO;

public class NaverMap implements ActionListener {

    ShowMap naverMap;

    public NaverMap(ShowMap naverMap) {
        this.naverMap = naverMap;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String clientId = "trmig48jcc";
        String clientSecret = "c77a9hQmFuUoMdaky0ubDwoPQFK9rr08XzbYBjiJ";
        AddressVO vo = null;

        try {
            String address = naverMap.address.getText();
            String addr = URLEncoder.encode(address, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;
            URL url = new URL(apiURL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            JSONTokener tokener = new JSONTokener(response.toString());
            JSONObject object = new JSONObject(tokener);
            System.out.println(object);

            JSONArray arr = object.getJSONArray("addresses");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                vo = new AddressVO();
                vo.setRoadAddress((String) temp.get("roadAddress"));
                vo.setJibunAddress((String) temp.get("jibunAddress"));
                vo.setX((String) temp.get("x"));
                vo.setY((String) temp.get("y"));
                System.out.println(vo);

                // MySQL에 저장
                saveAddressInfoToDatabase(vo);

                // ShowMap에 주소 정보 추가
                naverMap.addAddressInfo(vo);
            }

            map_service(vo);

        } catch (Exception err) {
            System.out.println(err);
        }
    }

    public void map_service(AddressVO vo) {
        String URL_STATICMAP = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";

        try {
            String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");
            URL_STATICMAP += "center=" + vo.getX() + "," + vo.getY();
            URL_STATICMAP += "&level=16&w=700&h=500";
            URL_STATICMAP += "&markers=type:t|size:mid|pos:" + pos
                    + "|label:" + URLEncoder.encode(vo.getRoadAddress(), "UTF-8");

            URL url = new URL(URL_STATICMAP);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "trmig48jcc");
            con.setRequestProperty("X-NCP-APIGW-API-KEY", "c77a9hQmFuUoMdaky0ubDwoPQFK9rr08XzbYBjiJ");

            int responseCode = con.getResponseCode();
            BufferedReader br;

            // 정상호출인 경우.
            if (responseCode == 200) {
                InputStream is = con.getInputStream();

                int read = 0;
                byte[] bytes = new byte[1024];

                // 랜덤 파일명으로 파일 생성
                String tempName = Long.valueOf(new Date().getTime()).toString();
                File file = new File(tempName + ".jpg");    // 파일 생성.

                file.createNewFile();

                OutputStream out = new FileOutputStream(file);

                while ((read = is.read(bytes)) != -1) {
                    out.write(bytes, 0, read);    // 파일 작성
                }

                is.close();
                ImageIcon img = new ImageIcon(file.getName());
                naverMap.imageLabel.setIcon(img);
                naverMap.resAddress.setText(vo.getRoadAddress());
                naverMap.jibunAddress.setText(vo.getJibunAddress());
                naverMap.resX.setText(vo.getX());
                naverMap.resY.setText(vo.getY());

            } else {
                System.out.println(responseCode);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void saveAddressInfoToDatabase(AddressVO vo) {
        String url = "jdbc:mysql://localhost:3306/travelmate";
        String user = "root";
        String password = "0000";

        String query = "INSERT INTO address_info (road_address, jibun_address, longitude, latitude) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, vo.getRoadAddress());
            pstmt.setString(2, vo.getJibunAddress());
            pstmt.setString(3, vo.getX());
            pstmt.setString(4, vo.getY());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

