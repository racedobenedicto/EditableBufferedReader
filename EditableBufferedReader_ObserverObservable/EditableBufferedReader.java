import java.io.*;
import java.util.Observable;

public class EditableBufferedReader extends BufferedReader {
    
    public EditableBufferedReader(Reader r) {
        super(r);
    }

    protected void setRaw() throws Exception {
        new ProcessBuilder("bash", "-c", "stty -echo raw < /dev/tty").start().waitFor();
    }

    protected void unsetRaw() throws Exception {
        new ProcessBuilder("bash", "-c", "stty echo -raw < /dev/tty").start().waitFor();
    }
    
    public int read() throws IOException {
        int lect = super.read();
        int lect2, lect3;
        if (lect != Codes.ESC) return lect;
        lect2 = Codes.ESC*10000;
        lect = super.read();
        lect2 += lect*100;
        if(lect == Codes.KEY) {
            lect = super.read();
            lect2 += lect;
            if(lect == 50 || lect == 51) { //50 -> 2 ; 51 -> 3
                lect = super.read();
                lect2 = lect2*1000 + lect;
            }
            return lect2;
        }
        return lect;
    }

    public String readLine() throws IOException {
        try {
            this.setRaw();
            //Initialize Line and Console
            Line line;
            Console console = new Console();
            console.inicializar();
            line = new Line(console.getColumns());
            console.setLine(line);
            line.addObserver(console);

            int lect = 0;
            while((lect = this.read()) != Codes.CR) {
                switch(lect) {
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
                        line.insert();
                        break;
                    case Codes.SUPR:
                        if(console.linealPosition()-1 > 0) {
                            line.suprimirChar(console.linealPosition()-1);
                        }
                        break;
                    case Codes.HOME:
                        console.moveHome();
                        break;
                    case Codes.END:
                        console.moveEnd(line.getLength());                        
                        break;
                    default: //Characters and sapce
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
