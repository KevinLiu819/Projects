import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoardWidget extends JPanel implements MouseInputListener {

    private static final long serialVersionUID = 1L;

    private static final Color LIGHT_SQUARE = new Color(1.0f, 0.805f, 0.617f);
    private static final Color DARK_SQUARE = new Color(0.816f, 0.543f, 0.277f);

    private static final int BOARD_SIZE = 8;
    private static final int SIZE = 48;

    public BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        _acceptingMoves = false;
        _board = new Board();
        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        drawGrid(g);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                drawPiece(g, _board.board()[i][j]);
            }
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(LIGHT_SQUARE);
        g.fillRect(0, 0, BOARD_SIZE * SIZE, BOARD_SIZE * SIZE);
        g.setColor(DARK_SQUARE);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (i + 1) % 2; j < BOARD_SIZE; j += 2) {
                g.fillRect(i * SIZE, j * SIZE, SIZE, SIZE);
            }
        }
    }

    private void drawPiece(Graphics g, Piece piece) {
        if (piece.abbrev() == '-') {
            return;
        }
        try {
            BufferedImage image = ImageIO.read(new File("./images/" + piece.color() + piece.abbrev() + ".png"));
            g.drawImage(image, piece.col() * SIZE, piece.row() * SIZE, SIZE, SIZE, null);
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    public synchronized void update(Board board) {
        _board = new Board(board);
        repaint();
    }
    
    public void setAcceptingMoves(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX() / SIZE;
        int y = e.getY() / SIZE;
        if (_acceptingMoves && x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
            _fr = y;
            _fc = x;
        }
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
        int x = e.getX() / SIZE;
        int y = e.getY() / SIZE;
        if (_acceptingMoves && x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
            String from = _board.board()[_fr][_fc].toString();
            String to = _board.board()[y][x].toString();
            _commands.offer(from + "-" + to);
        }
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    private ArrayBlockingQueue<String> _commands;
    private boolean _acceptingMoves;
    private Board _board;
    private int _fr, _fc;

}