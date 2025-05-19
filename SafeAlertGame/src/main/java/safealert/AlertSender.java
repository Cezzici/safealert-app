package safealert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AlertSender {

    public static void sendAlert(String userId, int severity, double latitude, double longitude) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost/safealert_server/receive_alert.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "user_id=" + URLEncoder.encode(userId, StandardCharsets.UTF_8) +
                        "&severity=" + URLEncoder.encode(String.valueOf(severity), StandardCharsets.UTF_8) +
                        "&latitude=" + URLEncoder.encode(String.valueOf(latitude), StandardCharsets.UTF_8) +
                        "&longitude=" + URLEncoder.encode(String.valueOf(longitude), StandardCharsets.UTF_8);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                System.out.println("✅ Alertă trimisă | utilizator: " + userId + " | gravitate: " + severity + " | răspuns: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                System.out.println("🟣 Răspuns de la server: " + response);

                conn.disconnect();
            } catch (Exception e) {
                System.err.println("❌ Eroare la trimiterea alertei: " + e.getMessage());
            }
        }).start();
    }
}
