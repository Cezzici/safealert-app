package safealert;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {

    @FXML private GridPane gameGrid;
    @FXML private Label statusLabel;
    @FXML private Label scoreLabel;

    private SafeAlertLogic game;
    private String userId;

    private int scoreX = 0;
    private int scoreO = 0;

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
        updateScoreLabel();
    }

    private void drawBoard() {
        gameGrid.getChildren().clear();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button cell = new Button("");
                cell.getStyleClass().add("cell");
                int r = row, c = col;

                cell.setOnAction(e -> {
                    if (game.placeMove(r, c)) {
                        playClickSound();
                        animateCell(cell);
                        char symbol = game.getSymbolAt(r, c);
                        cell.setText(String.valueOf(symbol));

                        if (symbol == 'X') {
                            cell.setStyle("-fx-text-fill: blue;");
                        } else if (symbol == 'O') {
                            cell.setStyle("-fx-text-fill: red;");
                        }

                        char result = game.checkWinner();

                        if (result == 'X' || result == 'O') {
                            statusLabel.setText(result + " a câștigat!");
                            if (result == 'X') {
                                scoreX++;
                            } else {
                                scoreO++;
                            }
                            updateScoreLabel();
                            disableBoard();
                        } else if (result == 'D') {
                            statusLabel.setText("Remiză!");
                            disableBoard();
                        } else {
                            statusLabel.setText("Rândul lui " + game.getCurrentPlayer());
                            botMove();
                        }

                        int severity = game.getGravitate();

                        if (game.alertaDeTrimis()) {
                            game.marcheazaAlertaTrimisa();
                            String displayName = UserIdentifier.getDisplayName();
                            AlertSender.sendAlert(userId, severity, displayName);
                            statusLabel.setText("Alertă trimisă pentru " + displayName);
                        }
                    }
                });

                gameGrid.add(cell, col, row);
            }
        }
    }

    private void botMove() {
        List<Button> availableCells = new ArrayList<>();

        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (btn.getText().isEmpty()) {
                    availableCells.add(btn);
                }
            }
        }

        if (!availableCells.isEmpty() && !game.isGameOver()) {
            Button botCell = availableCells.get(new Random().nextInt(availableCells.size()));

            Integer row = GridPane.getRowIndex(botCell);
            Integer col = GridPane.getColumnIndex(botCell);

            int r = (row != null) ? row : 0;
            int c = (col != null) ? col : 0;

            if (game.placeMove(r, c)) {
                playClickSound();
                animateCell(botCell);
                char symbol = game.getSymbolAt(r, c);
                if (symbol == '0') {
                    botCell.setText("O");
                } else {
                    botCell.setText(String.valueOf(symbol));
                }

                if (symbol == 'X') {
                    botCell.setStyle("-fx-text-fill: blue;");
                } else if (symbol == 'O') {
                    botCell.setStyle("-fx-text-fill: red;");
                }

                char result = game.checkWinner();

                if (result == 'X' || result == 'O') {
                    statusLabel.setText(result + " a câștigat!");
                    if (result == 'X') {
                        scoreX++;
                    } else {
                        scoreO++;
                    }
                    updateScoreLabel();
                    disableBoard();
                } else if (result == 'D') {
                    statusLabel.setText("Remiză!");
                    disableBoard();
                } else {
                    statusLabel.setText("Rândul lui " + game.getCurrentPlayer());
                }

                int severity = game.getGravitate();

                if (game.alertaDeTrimis()) {
                    game.marcheazaAlertaTrimisa();
                    String displayName = UserIdentifier.getDisplayName();
                    AlertSender.sendAlert(userId, severity, displayName);
                    statusLabel.setText("Alertă trimisă pentru " + displayName);
                }
            }
        }
    }

    private void playClickSound() {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/com/example/safealertgame/click.wav"));
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Nu s-a găsit fișierul audio: " + e.getMessage());
        }
    }

    private void animateCell(Button cell) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), cell);
        st.setByX(0.2);
        st.setByY(0.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void updateScoreLabel() {
        String scoreText = "X: " + scoreX + "  |  O: " + scoreO;
        scoreLabel.setText(scoreText);
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

    @FXML
    private void showFakeSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Setări Joc");
        alert.setHeaderText("Opțiuni");
        alert.setContentText("Dificultate: Medie\nSunet: Activ\nVibrații: Dezactivate");
        alert.show();
    }
}
