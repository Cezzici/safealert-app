package safealert;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AlertSender {

    public static void sendAlert(String uuid, int severity, double latitude, double longitude, String name) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost/safealert_server/receive_alert.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "user_id=" + URLEncoder.encode(uuid, StandardCharsets.UTF_8) +
                        "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) +
                        "&severity=" + URLEncoder.encode(String.valueOf(severity), StandardCharsets.UTF_8) +
                        "&latitude=" + URLEncoder.encode(String.valueOf(latitude), StandardCharsets.UTF_8) +
                        "&longitude=" + URLEncoder.encode(String.valueOf(longitude), StandardCharsets.UTF_8);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
