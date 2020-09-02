import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 48 * 8;
    private static final int HEIGHT = 48 * 8 + 44;

    public GUI() {
        super("Chess");
        _widget = new BoardWidget(_commands);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(this);
        menu.add(quit);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        setLayout(new BorderLayout());
        add(_widget, BorderLayout.CENTER);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setResizable(false);
    }

    public void update(Game game) {
        Board board = game.board();
        _widget.update(board);
    }

    public String readCommand() {
        try {
            _widget.setAcceptingMoves(true);
            String command = _commands.take();
            _widget.setAcceptingMoves(false);
            return command;
        } catch (InterruptedException e) {
            return "";
        }
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Quit")) {
            _commands.offer("quit");
        }
    }

    private ArrayBlockingQueue<String> _commands = new ArrayBlockingQueue<>(5);
    private BoardWidget _widget;

}