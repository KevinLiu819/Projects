import java.util.ArrayList;

public class MachinePlayer extends Player {

    private static final int BOARD_SIZE = 8;
    private static final int DEPTH = 2;
    private static final int MIN = -Integer.MAX_VALUE;
    private static final int MAX = Integer.MAX_VALUE;
    private static final int WINMIN = MIN + 20;
    private static final int WINMAX = MAX - 20;
    private static final int MULTIPLIER = 100;

    public MachinePlayer(char side, Game game) {
        super(side, game);
    }

    public Player create(char side, Game game) {
        return new MachinePlayer(side, game);
    }

    public String getMove() {
        if (board().gameover()) {
            return game().readLine();
        }
        if (side() == 'W') {
            findMove(board(), DEPTH, true, 1, MIN, MAX);
        } else {
            findMove(board(), DEPTH, true, -1, MIN, MAX);
        }
        return _move.toString();
    }

    public boolean manual() {
        return false;
    }

    private int findMove(Board board, int depth, boolean save, int sense, int alpha, int beta) {
        if (board.gameover()) {
            if (board.winner() == 'W') {
                return WINMAX;
            } else if (board.winner() == 'B') {
                return WINMIN;
            } else {
                return 0;
            }
        }
        ArrayList<Move> moves = board.legalMoves();
        int best = MIN * sense;
        for (Move move : moves) {
            board.makeMove(move);
            int next = 0;
            if (depth == 0) {
                next = heuristic(board);
            } else {
                next = findMove(board, depth - 1, false, -sense, alpha, beta);
            }
            board.retract();
            if (next * sense > best * sense) {
                best = next;
                if (save) {
                    _move = move;
                }
                if (sense == 1) {
                    alpha = Math.max(alpha, next);
                } else {
                    beta = Math.min(beta, next);
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return best;
    }

    private int heuristic(Board board) {
        if (board.gameover()) {
            if (board.winner() == 'W') {
                return WINMAX;
            } else if (board.winner() == 'B') {
                return WINMIN;
            } else {
                return 0;
            }
        }
        int value = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int sense = 0;
                if (board.board()[i][j].color() == 'W') {
                    sense = 1;
                } else if (board.board()[i][j].color() == 'B') {
                    sense = -1;
                }
                switch (board.board()[i][j].abbrev()) {
                case 'Q':
                    value += (9 * sense) * MULTIPLIER * MULTIPLIER;
                    value += (BOARD_SIZE / 2 - board.board()[i][j].row()) * MULTIPLIER;
                    break;
                case 'R':
                    value += (5 * sense) * MULTIPLIER * MULTIPLIER;
                    break;
                case 'B':
                    value += (3 * sense) * MULTIPLIER * MULTIPLIER;
                    value += (BOARD_SIZE / 2 - board.board()[i][j].row()) * MULTIPLIER;
                    break;
                case 'N':
                    value += (3 * sense) * MULTIPLIER * MULTIPLIER;
                    value += (BOARD_SIZE / 2 - board.board()[i][j].row()) * MULTIPLIER;
                    break;
                case 'P':
                    value += sense * MULTIPLIER * MULTIPLIER;
                    value += (BOARD_SIZE / 2 - board.board()[i][j].row()) * MULTIPLIER;
                    break;
                }
            }
        }
        if (board.underCheck('W')) {
            value += MULTIPLIER;
        } else if (board.underCheck('B')) {
            value -= MULTIPLIER;
        }
        value += (int) (Math.random() * 4 - 2) * MULTIPLIER;
        return value;
    }

    private Move _move;

}