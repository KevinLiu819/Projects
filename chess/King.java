import java.util.ArrayList;

public class King extends Piece {

    private static final int BOARD_SIZE = 8;

    public King(int row, int col, char color) {
        super(row, col, color);
    }

    public King(Piece piece) {
        super(piece);
    }

    public Piece create() {
        return new King(this);
    }

    public char abbrev() {
        return 'K';
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

    public boolean kingCastle(Board board) {
        if (movesMade() > 0) {
            return false;
        } else if (board.underCheck(color())) {
            return false;
        } else if (board.underAttack(row(), col() + 1, color())) {
            return false;
        } else if (board.underAttack(row(), col() + 2, color())) {
            return false;
        } else if (board.board()[row()][col() + 1].color() != '-') {
            return false;
        } else if (board.board()[row()][col() + 2].color() != '-') {
            return false;
        } else {
            Piece piece = board.board()[row()][col() + 3];
            if (piece.abbrev() != 'R' || piece.movesMade() > 0 || piece.color() != color()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean queenCastle(Board board) {
        if (movesMade() > 0) {
            return false;
        } else if (board.underCheck(color())) {
            return false;
        } else if (board.underAttack(row(), col() - 1, color())) {
            return false;
        } else if (board.underAttack(row(), col() - 2, color())) {
            return false;
        } else if (board.board()[row()][col() - 1].color() != '-') {
            return false;
        } else if (board.board()[row()][col() - 2].color() != '-') {
            return false;
        } else if (board.board()[row()][col() - 3].color() != '-') {
            return false;
        } else {
            Piece piece = board.board()[row()][col() - 4];
            if (piece.abbrev() != 'R' || piece.movesMade() > 0 || piece.color() != color()) {
                return false;
            } else {
                return true;
            }
        }
    }

    private static final int[][] DIR = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1} };

}