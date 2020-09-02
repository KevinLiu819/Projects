import java.util.Scanner;
import java.io.PrintStream;

public class Game {

    public Game() {
        _board = new Board();
        _input = new Scanner(System.in);
        _output = System.out;
        _white = new HumanPlayer('W', this);
        _black = new MachinePlayer('B', this);
        _view = new GUI();
    }

    public String readLine() {
        if (_input.hasNextLine()) {
            return _input.nextLine().trim();
        } else {
            return "";
        }
    }

    public void play() {
        _playing = true;
        while (true) {
            _view.update(this);
            if (_promotion) {
                _output.print("Promote piece (Q, R, B, N)> ");
                _board.flipTurn();
            } else {
                _output.print(_board.turn() + "> ");
            }
            String command = "";
            if (_promotion) {
                command = readLine();
            } else if (_board.turn() == 'W') {
                if (!_white.manual()) {
                    command = _white.getMove();
                } else {
                    command = _view.readCommand();
                }
            } else if (_board.turn() == 'B') {
                if (!_black.manual()) {
                    command = _black.getMove();
                } else {
                    command = _view.readCommand();
                }
            }
            processCommand(command);
            if (_board.promote()) {
                _promotion = true;
            }
            if (_board.gameover() && _playing && !_promotion) {
                _playing = false;
                String winner = "";
                switch(_board.winner()) {
                    case 'W':
                        winner = "White wins!";
                        break;
                    case 'B':
                        winner = "Black wins!";
                        break;
                    case 'T':
                        winner = "Tie game.";
                        break;
                    default:
                        winner = "Game is not over.";
                        _playing = true;
                        break;
                }
                _output.println(winner);
            }
        }
    }

    private void processCommand(String command) {
        if (command == null) {
            return;
        }
        switch (command) {
            case "dump":
                _output.println(_board.toString());
                break;
            case "quit":
                _input.close();
                System.exit(0);
                return;
            default:
                processMove(command);
                break;
        }
    }

    private void processMove(String command) {
        if (_promotion) {
            if (command.length() == 0) {
                _output.println("Invalid command");
                return;
            }
            _promotion = false;
            _board.flipTurn();
            switch (command.charAt(0)) {
            case 'Q':
                _board.makePromotion('Q');
                return;
            case 'R':
                _board.makePromotion('R');
                return;
            case 'B':
                _board.makePromotion('B');
                return;
            case 'N':
                _board.makePromotion('N');
                return;
            }
        }
        Move move = new Move(command);
        if (move.isNull()) {
            _output.println("Invalid command");
        } else if (!_playing) {
            _output.println("No game in progress");
        } else if (!_board.isLegal(move)) {
            _output.println("Invalid move: " + move.toString());
        } else {
            if (_board.turn() == 'W' && !_white.manual()) {
                _output.println("* " + move);
            } else if (_board.turn() == 'B' && !_black.manual()) {
                _output.println("* " + move);
            }
            _board.makeMove(move);
        }
    }

    public Board board() {
        return _board;
    }

    private Board _board;
    private Scanner _input;
    private PrintStream _output;
    private GUI _view;
    private Player _white, _black;
    private boolean _playing, _promotion;

}