module SafeAlertGame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    // Permite accesul JavaFX la clasele marcate cu @FXML
    opens com.example.safealertgame to javafx.fxml;
    opens safealert to javafx.fxml;

    // Permite folosirea claselor în alte părți ale aplicației
    exports com.example.safealertgame;
    exports safealert;
}
