import java.util.ArrayList;

public class Pawn extends Piece {

    private static final int BOARD_SIZE = 8;

    public Pawn(int row, int col, char color) {
        super(row, col, color);
        _movesWaited = 0;
        _color = color() == 'W' ? - 1 : 1;
    }

    public Pawn(Piece piece) {
        super(piece);
        _movesWaited = ((Pawn) piece).getWaited();
        _color = color() == 'W' ? - 1 : 1;
    }

    public Piece create() {
        return new Pawn(this);
    }

    public char abbrev() {
        return 'P';
    }

    public ArrayList<Move> possibleMoves(Board board, boolean lookup) {
        ArrayList<Move> moves = new ArrayList<Move>();
        int row = row() + 2 * _color;
        int col = col();
        if (movesMade() == 0 && inside(row, col)) {
            if ((color() == 'B' && row() == 1) || (color() == 'W' && row() == BOARD_SIZE - 2)) {
                if (board.board()[row][col].color() == '-' && board.board()[row - _color][col].color() == '-') {
                    board.makeMove(new Move(row(), col(), row, col));
                    if (!lookup || !board.underCheck(color())) {
                        moves.add(new Move(row(), col(), row, col));
                    }
                    board.retract();
                }
            }
        }
        row -= _color;
        if (inside(row, col) && board.board()[row][col].color() == '-') {
            board.makeMove(new Move(row(), col(), row, col));
            if (!lookup || !board.underCheck(color())) {
                moves.add(new Move(row(), col(), row, col));
            }
            board.retract();
        }
        col -= _color;
        handleCapture(moves, row, col, board, lookup);
        col += 2 * _color;
        handleCapture(moves, row, col, board, lookup);
        return moves;
    }

    private void handleCapture(ArrayList<Move> moves, int row, int col, Board board, boolean lookup) {
        if (inside(row, col)) {
            if (board.board()[row][col].color() == opposite()) {
                board.makeMove(new Move(row(), col(), row, col));
                if (!lookup || !board.underCheck(color())) {
                    moves.add(new Move(row(), col(), row, col, true, false));
                }
                board.retract();
            } else if (board.board()[row][col].color() == '-') {
                if (board.board()[row - _color][col].color() == opposite()) {
                    if (board.board()[row - _color][col].abbrev() == 'P') {
                        if (board.board()[row - _color][col].movesMade() == 1) {
                            if (((Pawn) board.board()[row - _color][col]).getWaited() <= 1) {
                                if (board.board()[row - _color][col].abbrev() == 'P') {
                                    if (row == (_color == -1 ? BOARD_SIZE - 1 : 0) + _color * 5) {
                                        board.makeMove(new Move(row(), col(), row, col));
                                        if (!lookup || !board.underCheck(color())) {
                                            moves.add(new Move(row(), col(), row, col, false, true));
                                        }
                                        board.retract();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean inside(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    public void setWaited(int wait) {
        if (movesMade() > 0 && !captured()) {
            _movesWaited = wait;
        }
    }

    public int getWaited() {
        return _movesWaited;
    }

    private int _movesWaited, _color;

}