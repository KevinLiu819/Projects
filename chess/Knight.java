import java.util.ArrayList;

public class Knight extends Piece {
    
    private static final int BOARD_SIZE = 8;

    public Knight(int row, int col, char color) {
        super(row, col, color);
    }

    public Knight(Piece piece) {
        super(piece);
    }

    public Piece create() {
        return new Knight(this);
    }

    public char abbrev() {
        return 'N';
    }

    public ArrayList<Move> possibleMoves(Board board, boolean lookup) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int[] d : DIR) {
            int row = row() + d[0];
            int col = col() + d[1];
            if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                if (board.board()[row][col].color() == opposite()) {
                    board.makeMove(new Move(row(), col(), row, col));
                    if (lookup && board.underCheck(color())) {
                        board.retract();
                        continue;
                    } else {
                        board.retract();
                        moves.add(new Move(row(), col(), row, col, true, false));
                    }
                } else if (board.board()[row][col].color() != color()) {
                    board.makeMove(new Move(row(), col(), row, col));
                    if (lookup && board.underCheck(color())) {
                        board.retract();
                        continue;
                    } else {
                        board.retract();
                        moves.add(new Move(row(), col(), row, col));
                    }
                }
            }
        }
        return moves;
    }

    private static final int[][] DIR = { {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2} };


}