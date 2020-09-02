import java.util.ArrayList;

public abstract class Piece {

    public Piece(int row, int col) {
        this(row, col, '-');
    }

    public Piece(int row, int col, char color) {
        _row = row;
        _col = col;
        _color = color;
        _movesMade = 0;
        _captured = false;
    }

    public Piece(Piece piece) {
        _row = piece.row();
        _col = piece.col();
        _color = piece.color();
        _movesMade = piece.movesMade();
        _captured = piece.captured();
    }

    abstract Piece create();

    abstract char abbrev();

    abstract ArrayList<Move> possibleMoves(Board board, boolean lookup);

    public boolean validMove(int row, int col, Board board, boolean lookup) {
        if (abbrev() == 'K') {
            if (row() == row && col - col() == 2) {
                return ((King) this).kingCastle(board);
            } else if (row() == row && col() - col == 2) {
                return ((King) this).queenCastle(board);
            }
        }
        ArrayList<Move> moves = possibleMoves(board, lookup);
        for (Move m : moves) {
            if (m.tr() == row && m.tc() == col) {
                return true;
            }
        }
        return false;
    }

    public void set(int row, int col) {
        _row = row;
        _col = col;
    }

    public void capture() {
        _captured = !_captured;
    }
    
    public void move() {
        _movesMade++;
    }

    public void undo() {
        if (movesMade() > 0) {
            _movesMade--;
        }
    }

    public int row() {
        return _row;
    }

    public int col() {
        return _col;
    }

    public int movesMade() {
        return _movesMade;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean captured() {
        return _captured;
    }

    public char color() {
        return _color;
    }

    public char opposite() {
        switch (_color) {
        case 'W':
            return 'B';
        case 'B':
            return 'W';
        default:
            return '-';
        }
    }

    public String toString() {
        return String.format("%c%d", (char) (_col + 'a'), 8 - row());
    }

    private int _row, _col;
    private char _color;
    private int _movesMade;
    private boolean _captured;

}