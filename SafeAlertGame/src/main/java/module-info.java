module SafeAlertGame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.desktop; // ✅ Adaugă asta

    opens com.example.safealertgame to javafx.fxml;
    opens safealert to javafx.fxml;

    exports com.example.safealertgame;
    exports safealert;
}
