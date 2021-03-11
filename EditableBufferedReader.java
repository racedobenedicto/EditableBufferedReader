  
import java.io.*;

public class EditableBufferedReader extends BufferedReader{
   
    //private Line[] lines;
    public Line line;
    private Console console;
    private int[] character;
    
    public EditableBufferedReader(Reader r) throws Exception{
        super(r);
        console = new Console();
        line = new Line(console.getColumns());
        //lines = new Line[10];
        //lines[0] = new Line(console.getColumns());
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
            int carriageReturn = 0;
            while(carriageReturn != 1) {
                int lect = this.read();
                switch(lect) {
                    case Codes.CR:
                        carriageReturn = 1;
                        break;
                    case Codes.SPACE:
                        line.addChar((char) lect, console.linealPosition());
                        console.character(line.getLength(), line.toString());
                        break;
                    case Codes.TAB:
                        line.tab(console.linealPosition());                        
                        console.tab(line.getLength(), line.toString());
                        break;
                    case Codes.DEL:
                        if(console.linealPosition()-1 > 0) {
                            line.deleteChar(console.linealPosition()-1);
                            console.deleteChar(line.toString());
                        }
                        break;
                    case Codes.UP:
                        console.moveUp();
                        break;
                    case Codes.DOWN:
                        console.moveDown();
                        break;
                    case Codes.RIGHT:
                        console.moveRight(line.getLength());
                        break;
                    case Codes.LEFT:
                        console.moveLeft();
                        break;
                    case Codes.INSERT:
                        this.read();
                        line.insert();
                        break;
                    case Codes.SUPR:
                        this.read();
                        line.suprimirChar(console.linealPosition()-1);
                        console.suprimirChar(line.toString());
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
                        console.character(line.getLength(), line.toString());
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