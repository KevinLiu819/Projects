
public class Move {

    public Move(int fr, int fc, int tr, int tc) {
        this(fr, fc, tr, tc, false, false);
    }
    
    public Move(int fr, int fc, int tr, int tc, boolean capture, boolean enpassant) {
        _fr = fr;
        _fc = fc;
        _tr = tr;
        _tc = tc;
        _promote = false;
        _capture = capture;
        _enpassant = enpassant;
        _kingCastle = false;
        _queenCastle = false;
        _null = false;
    }

    public Move(int fr, int fc, int tr, int tc, boolean kingCastle) {
        this(fr, fc, tr, tc, false, false);
        _kingCastle = kingCastle;
        _queenCastle = !kingCastle;
    }

    public Move(String s) {
        s = s.trim();
        if (s.matches("[a-h][1-8]-[a-h][1-8]\\b.*")) {
            String from = s.substring(0, 2);
            String to = s.substring(3, 5);
            _fr = 8 - from.charAt(1) + '0';
            _fc = from.charAt(0) - 'a';
            _tr = 8 - to.charAt(1) + '0';
            _tc = to.charAt(0) - 'a';
            _capture = false;
            _promote = false;
            _kingCastle = false;
            _queenCastle = false;
            _null = false;
        } else {
            _null = true;
        }
    }

    public int fr() {
        return _fr;
    }

    public int fc() {
        return _fc;
    }

    public int tr() {
        return _tr;
    }

    public int tc() {
        return _tc;
    }

    public void setCapture() {
        _capture = true;
    }

    public void setEnpassant() {
        _enpassant = true;
    }

    public void setPromote() {
        _promote = true;
    }

    public void setKingCastle() {
        _kingCastle = true;
    }

    public void setQueenCastle() {
        _queenCastle = true;
    }

    public boolean capture() {
        return _capture;
    }

    public boolean enpassant() {
        return _enpassant;
    }

    public boolean promote() {
        return _promote;
    }

    public boolean kingCastle() {
        return _kingCastle;
    }

    public boolean queenCastle() {
        return _queenCastle;
    }

    public boolean isNull() {
        return _null;
    }

    public String toString() {
        return String.format("%c%d-%c%d", (char) (_fc + 'a'), 8 - _fr, (char) (_tc + 'a'), 8 - _tr);
    }

    private int _fr, _fc, _tr, _tc;
    private boolean _capture, _enpassant, _promote;
    private boolean _kingCastle, _queenCastle;
    private boolean _null;

}