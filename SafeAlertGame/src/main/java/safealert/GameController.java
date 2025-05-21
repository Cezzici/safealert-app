package safealert;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

public class GameController {

    @FXML private GridPane gameGrid;
    @FXML private Label statusLabel;

    private SafeAlertLogic game;
    private String userId;

    @FXML
    public void initialize() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Cine joacă?");
        dialog.setContentText("Username:");

        dialog.showAndWait().ifPresentOrElse(input -> {
            if (!input.trim().isEmpty()) {
                UserIdentifier.setDisplayName(input.trim());
            }
        }, () -> {
            UserIdentifier.setDisplayName("anonim");
        });

        userId = UserIdentifier.getUUID();

        game = new SafeAlertLogic();
        drawBoard();
    }

    private void drawBoard() {
        gameGrid.getChildren().clear();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button cell = new Button("");
                cell.setPrefSize(100, 100);
                int r = row, c = col;

                cell.setOnAction(e -> {
                    if (game.placeMove(r, c)) {
                        cell.setText(String.valueOf(game.getSymbolAt(r, c)));

                        char result = game.checkWinner();

                        if (result == 'X' || result == 'O') {
                            statusLabel.setText("🎉 " + result + " a câștigat!");
                            disableBoard();
                        } else if (result == 'D') {
                            statusLabel.setText("🤝 Remiză!");
                            disableBoard();
                        } else {
                            statusLabel.setText("Rândul lui " + game.getCurrentPlayer());
                        }

                        int severity = game.getGravitate();

                        if (game.alertaDeTrimis()) {
                            game.marcheazaAlertaTrimisa();

                            double latitude, longitude;
                            int choice = (int) (Math.random() * 3);
                            switch (choice) {
                                case 0:
                                    latitude = 44.4268 + Math.random() * 0.01;
                                    longitude = 26.1025 + Math.random() * 0.01;
                                    break;
                                case 1:
                                    latitude = 45.7489 + Math.random() * 0.01;
                                    longitude = 21.2087 + Math.random() * 0.01;
                                    break;
                                case 2:
                                    latitude = 47.1585 + Math.random() * 0.01;
                                    longitude = 27.6014 + Math.random() * 0.01;
                                    break;
                                default:
                                    latitude = 44.4268;
                                    longitude = 26.1025;
                            }

                            String displayName = UserIdentifier.getDisplayName();
                            AlertSender.sendAlert(userId, severity, latitude, longitude, displayName);
                            statusLabel.setText("✅ Alertă trimisă pentru " + displayName);
                        }
                    }
                });

                gameGrid.add(cell, col, row);
            }
        }
    }

    private void disableBoard() {
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
            }
        }
    }

    @FXML
    public void handleReset() {
        game.reset();
        statusLabel.setText("Rândul lui X");
        drawBoard();
    }
}
