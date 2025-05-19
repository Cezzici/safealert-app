module SafeAlertGame {
    requires javafx.controls;
    requires javafx.fxml;

    opens safealert to javafx.fxml;
    exports safealert;
}