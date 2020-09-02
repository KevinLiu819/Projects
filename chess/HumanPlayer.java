
public class HumanPlayer extends Player {

    public HumanPlayer(char side, Game game) {
        super(side, game);
    }

    public Player create(char side, Game game) {
        return new HumanPlayer(side, game);
    }

    public String getMove() {
        return game().readLine();
    }

    public boolean manual() {
        return true;
    }

}