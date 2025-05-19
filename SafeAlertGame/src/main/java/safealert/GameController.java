package safealert;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

public class GameController {

    @FXML private GridPane gameGrid;
    @FXML private Label statusLabel;

    private SafeAlertLogic game;
    private String userId = "anonim";

    @FXML
    public void initialize() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Identificare victimă");
        dialog.setHeaderText("Introduceți un prenume pentru identificare:");
        dialog.setContentText("Prenume:");

        dialog.showAndWait().ifPresentOrElse(input -> {
            if (!input.trim().isEmpty()) {
                userId = input.trim();
            }
        }, () -> {
            userId = "anonim"; // fallback garantat
        });

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
                            AlertSender.sendAlert(userId, severity);
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