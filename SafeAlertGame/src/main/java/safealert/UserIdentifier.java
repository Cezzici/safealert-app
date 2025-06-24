package safealert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;

public class UserIdentifier {

    private static String displayName = "anonim";
    private static String uuid = null;

    // Salvează numele introdus și generează UUID unic per nume
    public static void setDisplayName(String name) {
        displayName = name.trim().toLowerCase().replaceAll("[^a-z0-9]", "_");
        Path uuidPath = Paths.get("user_id_" + displayName + ".txt");

        if (Files.exists(uuidPath)) {
            try {
                uuid = Files.readString(uuidPath, StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            uuid = UUID.randomUUID().toString();
            try {
                Files.write(uuidPath, uuid.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path namePath = Paths.get("display_name.txt");
        try {
            Files.write(namePath, name.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDisplayName() {
        Path path = Paths.get("display_name.txt");
        if (Files.exists(path)) {
            try {
                return Files.readString(path, StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Necunoscut";
    }

    public static String getUUID() {
        return uuid != null ? uuid : UUID.randomUUID().toString();
    }
}
