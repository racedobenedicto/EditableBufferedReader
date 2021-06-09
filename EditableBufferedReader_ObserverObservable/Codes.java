

public class Codes {
    public final static int CTRL_C = 3;
    public final static int CTRL_V = 22;
    public final static int CTRL_Z = 26;
    public final static int CR = 13; //Enter
    public final static int SPACE = 32; 
    public final static int TAB = 9; //Horizontal Tabul
    public final static int ESC = 27;
    public final static int WAVE = 126; //"~"
    public final static int DEL = 127;
    public final static int KEY = 91; //"["
    public final static int UP = 279165;
    public final static int DOWN = 279166;
    public final static int RIGHT = 279167;
    public final static int LEFT = 279168;
    public final static int INSERT = 279150126;
    public final static int SUPR = 279151126;
    public final static int HOME = 279172;
    public final static int END = 279170;

    public final static int CARACT = 1; //Characters and Space

    public final static String CLEAR = "\033[2J";
    public final static String INSERT_BLANK = "\033[@";
    public final static String DELETE = "\033[P";
    
    private int code;
    
    public Codes(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
}
