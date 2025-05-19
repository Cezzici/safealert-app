package safealert;

public class SafeAlertLogic {
    private final char[][] board = new char[3][3];
    private boolean xTurn;
    private int moves;
    private int codGravitate; // 0 = nimic, 2/4/5 = cod detectat

    public SafeAlertLogic() {
        reset();
    }

    public void reset() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = ' ';
        xTurn = true;
        moves = 0;
        codGravitate = 0;
    }

    public boolean placeMove(int row, int col) {
        if (board[row][col] != ' ') return false;

        char player = getCurrentPlayer();
        board[row][col] = player;
        moves++;

        // Cod G4: (0,0) -> (2,2)
        if (player == 'X' && board[0][0] == 'X' && board[2][2] == 'X') {
            codGravitate = 4;
        }

        // Cod G2: (1,1) -> (0,2)
        if (player == 'X' && board[1][1] == 'X' && board[0][2] == 'X') {
            codGravitate = 2;
        }

        // Cod G5: (2,0) -> (2,2)
        if (player == 'X' && board[2][0] == 'X' && board[2][2] == 'X') {
            codGravitate = 5;
        }

        xTurn = !xTurn;
        return true;
    }

    public char getCurrentPlayer() {
        return xTurn ? 'X' : 'O';
    }

    public char getSymbolAt(int row, int col) {
        return board[row][col];
    }

    public int getGravitate() {
        return codGravitate;
    }

    public char checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (same(board[i][0], board[i][1], board[i][2])) return board[i][0];
            if (same(board[0][i], board[1][i], board[2][i])) return board[0][i];
        }
        if (same(board[0][0], board[1][1], board[2][2])) return board[0][0];
        if (same(board[0][2], board[1][1], board[2][0])) return board[0][2];
        if (moves == 9) return 'D';
        return ' ';
    }

    private boolean same(char a, char b, char c) {
        return a != ' ' && a == b && b == c;
    }

    private boolean alertaTrimisa = false;

    public boolean alertaDeTrimis() {
        return !alertaTrimisa && codGravitate > 0;
    }

    public void marcheazaAlertaTrimisa() {
        alertaTrimisa = true;
    }
}