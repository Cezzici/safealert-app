package safealert;

public class SafeAlertLogic {

    private char[][] board;
    private boolean xTurn;
    private int movesCount;
    private boolean alertaTrimisa;

    private int gravitate;
    private int pragMutari;

    public SafeAlertLogic() {
        board = new char[3][3];
        gravitate = generareGravitate();
        pragMutari = gravitate;
        reset();
    }

    public void reset() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = ' ';
        xTurn = true;
        movesCount = 0;
        alertaTrimisa = false;

        gravitate = generareGravitate();
        pragMutari = gravitate;
    }

    public boolean placeMove(int row, int col) {
        if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ')
            return false;

        board[row][col] = getCurrentPlayer();
        xTurn = !xTurn;
        movesCount++;
        return true;
    }

    public char getCurrentPlayer() {
        return xTurn ? 'X' : 'O';
    }

    public char getSymbolAt(int row, int col) {
        return board[row][col];
    }

    public char checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2])
                return board[i][0];
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i])
                return board[0][i];
        }

        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0];

        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2];

        if (movesCount == 9)
            return 'D';

        return ' ';
    }

    public boolean alertaDeTrimis() {
        return !alertaTrimisa && movesCount == pragMutari;
    }

    public void marcheazaAlertaTrimisa() {
        alertaTrimisa = true;
    }

    public int getGravitate() {
        return gravitate;
    }

    private int generareGravitate() {
        return 3 + (int)(Math.random() * 3);
    }

    public boolean isGameOver() {
        char result = checkWinner();
        return result == 'X' || result == 'O' || result == 'D';
    }
}
