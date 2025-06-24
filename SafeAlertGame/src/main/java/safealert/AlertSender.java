package safealert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AlertSender {

    // ✅ Clasa pentru localizare
    public static class GeoLocation {
        public double latitude;
        public double longitude;

        public GeoLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    // ✅ Metoda care ia locația automat prin IP
    public static GeoLocation fetchLocationByIP() {
        GeoLocation defaultLocation = new GeoLocation(0.0, 0.0); // fallback

        try {
            URL url = new URL("http://ip-api.com/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String json = response.toString();
            double lat = Double.parseDouble(json.split("\"lat\":")[1].split(",")[0]);
            double lon = Double.parseDouble(json.split("\"lon\":")[1].split(",")[0]);

            return new GeoLocation(lat, lon);

        } catch (Exception e) {
            e.printStackTrace();
            return defaultLocation;
        }
    }

    // ✅ Metoda principală cu localizare inclusă
    public static void sendAlert(String uuid, int severity, String name) {
        new Thread(() -> {
            try {
                // 📍 Ia locația automat
                GeoLocation location = fetchLocationByIP();

                URL url = new URL("http://localhost/safealert_server/receive_alert.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);

                String data = "user_id=" + URLEncoder.encode(uuid, StandardCharsets.UTF_8) +
                        "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) +
                        "&severity=" + URLEncoder.encode(String.valueOf(severity), StandardCharsets.UTF_8) +
                        "&latitude=" + URLEncoder.encode(String.valueOf(location.latitude), StandardCharsets.UTF_8) +
                        "&longitude=" + URLEncoder.encode(String.valueOf(location.longitude), StandardCharsets.UTF_8);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Răspuns server: " + response.toString());

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
