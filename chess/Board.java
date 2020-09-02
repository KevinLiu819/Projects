import java.util.ArrayList;

public class Board {

    private static final int BOARD_SIZE = 8;

    public Board() {
        _board = new Piece[BOARD_SIZE][BOARD_SIZE];
        _moves = new ArrayList<Move>();
        _captured = new ArrayList<Piece>();
        _turn = 'W';
        _winner = '-';
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                char piece = INITIAL_PIECE[i][j];
                char color = INITIAL_COLOR[i][j];
                switch (piece) {
                case 'P':
                    _board[i][j] = new Pawn(i, j, color);
                    break;
                case 'R':
                    _board[i][j] = new Rook(i, j, color);
                    break;
                case 'N':
                    _board[i][j] = new Knight(i, j, color);
                    break;
                case 'B':
                    _board[i][j] = new Bishop(i, j, color);
                    break;
                case 'Q':
                    _board[i][j] = new Queen(i, j, color);
                    break;
                case 'K':
                    _board[i][j] = new King(i, j, color);
                    break;
                default:
                    _board[i][j] = new Empty(i, j);
                    break;
                }
            }
        }
    }

    public Board(Board board) {
        this();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                _board[i][j] = board._board[i][j].create();
            }
        }
        _moves.addAll(board._moves);
        _captured.addAll(board._captured);
        _turn = board._turn;
        _winner = board._winner;
    }

    public Piece[][] board() {
        return _board;
    }

    public void makeMove(Move move) {
        _moves.add(move);
        _captured.add(_board[move.tr()][move.tc()].create());
        Piece from = _board[move.fr()][move.fc()].create();
        from.set(move.tr(), move.tc());
        from.move();
        if (_board[move.tr()][move.tc()].color() == opposite(from.color())) {
            move.setCapture();
            if (!_board[move.tr()][move.tc()].captured()) {
                _board[move.tr()][move.tc()].capture();
            }
        }
        from = handlePromotion(from, move);
        handleEnpassant(from, move);
        handleCastling(from, move);
        _board[move.tr()][move.tc()] = from;
        _board[move.fr()][move.fc()] = new Empty(move.fr(), move.fc());
        updatePawns(1);
        flipTurn();
    }

    public void retract() {
        if (_moves.size() > 0) {
            updatePawns(-1);
            Move move = _moves.get(movesMade() - 1);
            Piece captured = _captured.get(movesMade() - 1);
            _moves.remove(movesMade() - 1);
            _captured.remove(movesMade());
            Piece to = _board[move.tr()][move.tc()].create();
            to.set(move.fr(), move.fc());
            to.undo();
            handleRetractCastling(to, move);
            if (move.promote()) {
                _promote = false;
                to = new Pawn(to.row(), to.col(), to.color());
            }
            if (move.enpassant()) {
                _board[move.tr()][move.tc()] = new Empty(move.tr(), move.tc());
            }
            _board[captured.row()][captured.col()] = captured;
            if (captured.captured()) {
                captured.capture();
            }
            _board[move.fr()][move.fc()] = to;
            flipTurn();
        }
        _winner = '-';
    }

    private void handleCastling(Piece from, Move move) {
        if (from.abbrev() == 'K' && move.tc() - move.fc() == 2) {
            move.setKingCastle();
            Piece rook = _board[move.tr()][move.tc() + 1].create();
            rook.set(move.fr(), move.fc() + 1);
            rook.move();
            _board[rook.row()][rook.col()] = rook;
            _board[move.tr()][move.tc() + 1] = new Empty(move.tr(), move.tc() + 1);
        } else if (from.abbrev() == 'K' && move.tc() - move.fc() == -2) {
            move.setQueenCastle();
            Piece rook = _board[move.tr()][move.tc() - 2].create();
            rook.set(move.fr(), move.fc() - 1);
            rook.move();
            _board[rook.row()][rook.col()] = rook;
            _board[move.tr()][move.tc() - 2] = new Empty(move.tr(), move.tc() - 2);
        }
    }

    private void handleEnpassant(Piece from, Move move) {
        if (from.abbrev() == 'P' && !move.capture() && Math.abs(move.tc() - move.fc()) == 1) {
            move.setEnpassant();
            int color = from.color() == 'W' ? -1 : 1;
            if (!_board[move.tr()][move.tc()].captured()) {
                _board[move.tr()][move.tc()].capture();
            }
            _captured.remove(movesMade() - 1);
            _captured.add(_board[move.tr() - color][move.tc()].create());
            _board[move.tr() - color][move.tc()] = new Empty(move.tr() - color, move.tc());
        }
    }

    private Piece handlePromotion(Piece from, Move move) {
        if (promote(move.tr(), move.tc(), from)) {
            move.setPromote();
            _promote = true;
            return from;
        } else {
            return from;
        }
    }

    private boolean promote(int row, int col, Piece p) {
        if (p.abbrev() == 'P') {
            return (p.color() == 'W' && row == 0) || (p.color() == 'B' && row == BOARD_SIZE - 1);
        } else {
            return false;
        }
    }

    private void handleRetractCastling(Piece to, Move move) {
        if (to.abbrev() == 'K' && move.tc() - move.fc() == 2) {
            Piece rook = _board[move.fr()][move.fc() + 1].create();
            rook.set(move.tr(), move.tc() + 1);
            rook.undo();
            _board[rook.row()][rook.col()] = rook;
            _board[move.fr()][move.fc() + 1] = new Empty(move.fr(), move.fc() + 1);
        } else if (to.abbrev() == 'K' && move.tc() - move.fc() == -2) {
            Piece rook = _board[move.fr()][move.fc() - 1].create();
            rook.set(move.tr(), move.tc() - 2);
            rook.undo();
            _board[rook.row()][rook.col()] = rook;
            _board[move.fr()][move.fc() - 1] = new Empty(move.fr(), move.fc() - 1);
        }
    }

    private void updatePawns(int inc) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (_board[i][j].abbrev() == 'P') {
                    Pawn pawn = ((Pawn) _board[i][j]);
                    pawn.setWaited(pawn.getWaited() + inc);
                }
            }
        }
    }

    public void makePromotion(char piece) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            int x = 0;
            boolean promote = false;
            if (_board[0][i].abbrev() == 'P') {
                promote = true;
            } else if (_board[BOARD_SIZE - 1][i].abbrev() == 'P') {
                promote = true;
                x = BOARD_SIZE - 1;
            }
            if (promote) {
                char color = _board[x][i].color();
                switch (piece) {
                case 'Q':
                    _board[x][i] = new Queen(x, i, color);
                    break;
                case 'R':
                    _board[x][i] = new Rook(x, i, color);
                    break;
                case 'B':
                    _board[x][i] = new Bishop(x, i, color);
                    break;
                case 'N':
                    _board[x][i] = new Knight(x, i, color);
                    break;
                }
            }
        }
        _promote = false;
    }

    public boolean gameover() {
        return winner() != '-';
    }

    public char winner() {
        if (checkmate()) {
            _winner = opposite();
        } else if (stalemate()) {
            _winner = 'T';
        }
        return _winner;
    }

    public boolean underAttack(int row, int col) {
        return underAttack(row, col, _turn);
    }

    public boolean underAttack(int row, int col, char turn) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (_board[i][j].color() == opposite(turn)) {
                    if (_board[i][j].validMove(row, col, this, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean underCheck() {
        return underCheck(_turn);
    }

    public boolean underCheck(char turn) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (_board[i][j].color() == turn && _board[i][j].abbrev() == 'K') {
                    return underAttack(i, j, turn);
                }
            }
        }
        return false;
    }

    public boolean checkmate() {
        return underCheck() && legalMoves().size() == 0;
    }

    public boolean stalemate() {
        return !underCheck() && legalMoves().size() == 0;
    }

    public boolean isLegal(Move move) {
        Piece from = _board[move.fr()][move.fc()].create();
        if (from.color() != turn()) {
            return false;
        }
        return from.validMove(move.tr(), move.tc(), this, true);
    }

    public ArrayList<Move> legalMoves() {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int fr = 0; fr < BOARD_SIZE; fr++) {
            for (int fc = 0; fc < BOARD_SIZE; fc++) {
                if (_board[fr][fc].color() == turn()) {
                    moves.addAll(_board[fr][fc].possibleMoves(this, true));
                }
            }
        }
        return moves;
    }

    public int movesMade() {
        return _moves.size();
    }

    public boolean promote() {
        return _promote;
    }

    public char turn() {
        return _turn;
    }

    public void flipTurn() {
        _turn = opposite();
    }

    public char opposite() {
        return opposite(_turn);
    }

    public char opposite(char turn) {
        switch (turn) {
        case 'W':
            return 'B';
        case 'B':
            return 'W';
        default:
            return '-';
        }
    }

    public String toString() {
        String s = "\n";
        for (int i = 0; i < BOARD_SIZE; i++) {
            s += String.format("%d ", 8 - i);
            for (int j = 0; j < BOARD_SIZE; j++) {
                Piece p = _board[i][j];
                s += String.format(" %c%c", p.color(), p.abbrev());
            }
            s += "\n";
        }
        s += "  ";
        for (int i = 0; i < BOARD_SIZE; i++) {
            s += String.format(" %c ", (char) (i + 'a'));
        }
        return s;
    }

    private static final char[][] INITIAL_PIECE = { {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}, 
                                                    {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                                                    {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'} };
    
    private static final char[][] INITIAL_COLOR = { {'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B'}, 
                                                    {'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'-', '-', '-', '-', '-', '-', '-', '-'},
                                                    {'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'},
                                                    {'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'} };


    private Piece[][] _board;
    private ArrayList<Move> _moves;
    private ArrayList<Piece> _captured;
    private char _turn;
    private char _winner;
    private boolean _promote;

}