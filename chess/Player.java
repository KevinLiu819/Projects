
public abstract class Player {

    public Player(char side, Game game) {
        _side = side;
        _game = game;
    }

    abstract String getMove();

    abstract Player create(char side, Game game);

    abstract boolean manual();

    public char side() {
        return _side;
    }

    public Game game() {
        return _game;
    }

    public Board board() {
        return _game.board();
    }

    private char _side;
    private Game _game;

}