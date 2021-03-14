import java.io.*;
import java.util.Observable;

public class EditableBufferedReader extends BufferedReader{
   
    public Line line;
    private Console console;
    
    public EditableBufferedReader(Reader r) {
        super(r);
        try {
            this.console = new Console();
            console.inicializar();
            this.line = new Line(console.getColumns());
            console.setLine(this.line);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void setRaw() throws Exception {
        new ProcessBuilder("bash", "-c", "stty -echo raw < /dev/tty").start().waitFor();
    }

    protected void unsetRaw() throws Exception {
        new ProcessBuilder("bash", "-c", "stty echo -raw < /dev/tty").start().waitFor();
    }
    
    public int read() throws IOException{
        int lect = super.read();
        int lect2;
        if (lect != Codes.ESC) return lect;
        lect2 = Codes.ESC*10000;
        lect = super.read();
        lect2 += lect*100;
        if(lect == Codes.KEY) return (lect2 += super.read());
        return lect;
    }

    public String readLine() throws IOException {
        try {
            this.setRaw();
            line.addObserver(this.console);
            int carriageReturn = 0;
            while(carriageReturn != 1) {
                int lect = this.read();
                switch(lect) {
                    case Codes.CR:
                        carriageReturn = 1;
                        break;
                    case Codes.SPACE:
                        line.addChar((char) lect, console.linealPosition());
                        break;
                    case Codes.TAB:
                        line.tab(console.linealPosition());
                        break;
                    case Codes.DEL:
                        if(console.linealPosition()-1 > 0) {
                            line.deleteChar(console.linealPosition()-1);
                        }
                        break;
                    case Codes.UP:
                        console.moveUp();
                        break;
                    case Codes.DOWN:
                        console.moveDown(line.getLength());
                        break;
                    case Codes.RIGHT:
                        console.moveRight(line.getLength());
                        break;
                    case Codes.LEFT:
                        console.moveLeft();
                        break;
                    case Codes.INSERT:
                        this.read(); //126
                        line.insert();
                        break;
                    case Codes.SUPR:
                        this.read(); //126
                        if(line.getLength() - console.linealPosition() +1 > 0) {
                            line.suprimirChar(console.linealPosition()-1);
                        }
                        break;
                    case Codes.HOME:
                        console.moveHome();
                        break;
                    case Codes.END:
                        console.moveEnd(line.getLength());                        
                        break;
                    case Codes.CTRL_C:
                        break;
                    case Codes.CTRL_V:
                        break;
                    case Codes.CTRL_Z:
                        break;
                    default:
                        line.addChar((char) lect, console.linealPosition());
                        break;
            }
        }
        this.unsetRaw();
        return line.toString();   
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }        
    }
}