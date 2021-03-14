import java.util.ArrayList;

public class Line {
    private ArrayList line;
    private boolean mode; //True: overwrite; False: insertion
    private int pos;
    
    public Line(int len){
        line = new ArrayList<>(len);
        this.mode = false;
        this.pos = 0;
    }
    
    public void addChar(char c, int pos) throws IndexOutOfBoundsException{
        if(!this.mode){
            //insertion
            this.line.add(pos-1, c); 
        } else {
            //overwrite
            this.suprimirChar(pos-1);
            this.line.add(pos-1, c);
        }     
    }
    
    public void deleteChar(int pos) throws IndexOutOfBoundsException {
        this.line.remove(pos-1);
    }
    
    public void suprimirChar(int pos) throws IndexOutOfBoundsException {
        this.line.remove(pos);
    }
       
    public void tab(int cursor){
        String tab2 = " ";
        char tab = tab2.charAt(0);
        for(int i=0; i<8; i++) this.addChar(tab, cursor);
    }
    
    public void insert(){
        this.setMode();
    }

    public int getLinePos(){
        return this.pos;
    }
    
    public String toString(){
        StringBuffer s = new StringBuffer();
        for(int i=0; i<getLength(); i++) {
            s.append(getChar(i));
        }
        return s.toString();
    }
    
    public boolean getMode(){
        return this.mode;
    }
    
    public void setMode(){
        if(this.mode) {
            this.mode = false;
        } else {
            this.mode = true;
        }
    }
    
    public void setPos(int pos){
        if(this.pos>0 && this.pos<line.size()) this.pos = pos;
    }
    
    public int getLength(){
        return line.size();
    }
    
    public Object getChar(int pos){
        return this.line.get(pos);
    }
}