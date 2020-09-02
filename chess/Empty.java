import java.util.ArrayList;

public class Empty extends Piece {

    public Empty(int row, int col) {
        super(row, col);
    }

    public Piece create() {
        return new Empty(row(), col());
    }

    public char abbrev() {
        return '-';
    }

    public boolean isEmpty() {
        return true;
    }

    public ArrayList<Move> possibleMoves(Board board, boolean lookup) {
        return new ArrayList<Move>();
    }

}