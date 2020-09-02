import java.util.ArrayList;

public class Bishop extends Piece {

    private static final int BOARD_SIZE = 8;

    public Bishop(int row, int col, char color) {
        super(row, col, color);
    }

    public Bishop(Piece piece) {
        super(piece);
    }

    public Piece create() {
        return new Bishop(this);
    }

    public char abbrev() {
        return 'B';
    }

    public ArrayList<Move> possibleMoves(Board board, boolean lookup) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int[] d : DIR) {
            int row = row() + d[0];
            int col = col() + d[1];
            while (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                if (board.board()[row][col].color() == color()) {
                    break;
                } else if (board.board()[row][col].color() == opposite()) {
                    board.makeMove(new Move(row(), col(), row, col));
                    if (lookup && board.underCheck(color())) {
                        board.retract();
                        break;
                    } else {
                        board.retract();
                        moves.add(new Move(row(), col(), row, col, true, false));
                        break;
                    }
                } else {
                    board.makeMove(new Move(row(), col(), row, col));
                    if (lookup && board.underCheck(color())) {
                        board.retract();
                    } else {
                        board.retract();
                        moves.add(new Move(row(), col(), row, col));
                    }
                    row += d[0];
                    col += d[1];
                }
            }
        }
        return moves;
    }

    private static final int[][] DIR = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };

}