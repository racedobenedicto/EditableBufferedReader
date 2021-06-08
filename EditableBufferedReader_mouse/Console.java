import java.io.*;
import java.util.Observer;
import java.util.Observable;

public class Console implements Observer {
    static final int rowOrigin = 0;
	static final int columnOrigin = 0;
    int row, column, rowRewriting, columnRewriting;
    int rewriting = 0;
    Line line;
    
    public Console() throws Exception {
        this.row = 1;
        this.column = 1;
        System.out.print(Codes.CLEAR);
        this.positionCursor(this.row, this.column);
    }

//Variable initialization
    public void setLine(Line line) {
        this.line = line;
    }
    
//Information    
    protected int getColumns() throws Exception {
        Process colsProcess = new ProcessBuilder("bash", "-c", "tput cols 2> /dev/tty").start();
        BufferedReader colsReader = new BufferedReader(new InputStreamReader(colsProcess.getInputStream()));
        String cols = colsReader.readLine();
        return Integer.parseInt(cols);
    }
    
    protected int getRows() throws Exception {
       Process rowProcess = new ProcessBuilder("bash", "-c", "tput cols 2> /dev/tty").start();
       BufferedReader rowReader = new BufferedReader(new InputStreamReader(rowProcess.getInputStream()));
       String rows = rowReader.readLine();
       return Integer.parseInt(rows);
    }
    
    public void positionCursor(int row, int column) {
        int columnCursor = this.column + this.columnOrigin;
        int rowCursor = this.row + this.rowOrigin;
        System.out.print("\033["+rowCursor+";"+columnCursor+"f");
    }
    
    public int linealPosition() throws Exception {
        return ((this.getRowCursor()-1)*this.getColumns() + this.getColumnCursor());
    }

    public int[] matricialPosition(int len) throws Exception { //total writed: line.getLength
        int positions[] = new int[2];
        positions[1] = len % this.getColumns(); //Column
        positions[0]=(positions[1] == 0)? len / this.getColumns():len / this.getColumns() + 1;
        return positions;
    }

    public int getColumnCursor(){
        return this.column;
    }

    public int getRowCursor(){
        return this.row;
    }

//Movement of rows and columns
    public void increaseColumn(int len) throws Exception {
        this.column = (this.column == this.getColumns())? 1 : this.column+1;
        if(this.column == 1) this.increaseRow();
    }
    
    public void increaseRow() throws Exception {
        if(this.row < this.getRows()) this.row++;
    }
    
    public void decreaseColumn() throws Exception {
        this.column = (this.row > 1)? ((this.column == 1)? this.getColumns() : this.column-1) : ((this.column == 1)? 1 : this.column-1); 
        if (this.column == this.getColumns()) this.decreaseRow();
    }
    
    public void decreaseRow() {
        if(this.row > 1) this.row--;
    }
    
//Writing function
    public void write(char line) throws Exception {
        System.out.print(Codes.INSERT_BLANK);
        System.out.print(c);
        if(this.getRowCursor()*this.getColumns() < this.line.getLength()) {
            char ch = (char) this.line.getChar(this.getRowCursor()*this.getColumns());
            this.positionCursor(this.getRowCursor()+1, 1);
            System.out.print(Codes.INSERT_BLANK);
            System.out.print(ch);
            this.positionCursor(this.row, this.column);
        }
    }

//Movement keys
    public void tab (int len, char line) throws Exception {
        for (int i=0; i<8; i++){
            this.character(len, line);
        }
    }

    public void moveRight(int len) throws Exception {
        int l = linealPosition();
        if(l <= len)this.increaseColumn(len);
        this.positionCursor(this.row, this.column);
    }

    public void moveLeft() throws Exception {
        this.decreaseColumn();
        this.positionCursor(this.row, this.column);
    }

    public void moveHome() throws Exception {
        this.row = 1;
        this.column = 1;
        this.positionCursor(this.row, this.column);
    }

    public void moveEnd(int len) throws Exception {
        int[] pos = matricialPosition(len);
        this.column = (pos[0] == this.row)? pos[1]+1 : this.getColumns(); 
        this.positionCursor(this.row, this.column);
    }

    public void moveUp() throws Exception {
        this.decreaseRow();
        this.positionCursor(this.row, this.column);
    }

    public void moveDown(int len) throws Exception {
        int[] pos = matricialPosition(len);
        if(pos[0] != this.row) { 
            //we aren't at last row
            this.increaseRow();
            if(pos[0] == this.row && pos[1] < this.column) {
                this.column = pos[1]+1;
            } 
        } 
        this.positionCursor(this.row, this.column);
    }

//Mouse functionalities
    public void activateMouse() throws Exception {
        System.out.println(Codes.ACTIVATE);
        this.positionCursor(); 
    }

    public void desactivateMouse() throws Exception {
        System.out.println(Codes.DESACTIVATE);
    }

    public void clickRight(int len) throws Exception { //Equivalent to moveRigth
        this.moveRight(len);
    }

    public void clickLeft(int len, int[] mPos) throws Exception { //position cursor
        int[] pos = matricialPosition(len);
        //int[0] row; int[1] column --> last position
        this.row = mPos[1]-32;
        if(this.row > pos[0]) this.row = pos[0];
        this.column = mPos[0]-32;
        if(this.column > pos[1] && this.row == pos[0]) this.column = pos[1]+1;        
        this.positionCursor();
    }

    public void scrollUp() throws Exception{ //Equivalent to moveUp
        this.moveUp();
    }

    public void scrollDown(int len) throws Exception{ //Equivalent to moveDown
        this.moveDown(len);
    }

    public void scrollButton() throws Exception{ //Equivalent to moveLeft
        this.moveLeft();
    }

//Delete characters
    public void deleteChar() throws Exception {
        this.decreaseColumn();
        this.positionCursor(this.row, this.column);
        System.out.print(Codes.DELETE);
        this.rewrite(len);
    }
    
    public void suprimirChar() throws Exception {
        System.out.print(Codes.DELETE);
        this.rewrite(len);
    }

//Characters, numbers, symbols and space
    public void character (int len, char line) throws Exception {
        this.write(line);
        this.increaseColumn(len);
        this.positionCursor(this.row, this.column);
    }

//Rewriting function
    private void rewrite(int len) throws Exception {
        int[] pos = matricialPosition(len);
        if(this.rewriting == 0){
                this.rowRewriting = this.row;
                this.columnRewriting = this.column;
                this.rewriting = 1;
            }
        while(pos[0] > this.row) {            
            this.column = this.getColumns();
            this.positionCursor(this.row, this.column);
            int linealpos = this.linealPosition();
            System.out.print((char)this.line.getChar(linealpos-1));
            this.increaseColumn(len);
            this.positionCursor(this.row, this.column);
            System.out.print(Codes.DELETE);
        }
        if(pos[1]==0 && pos[0]>1){
            this.column = this.getColumns();
            this.positionCursor(this.row, this.column);
            int linealpos = this.linealPosition();
            System.out.print((char)this.line.getChar(linealpos-1));
            this.row++;
            this.column = 1;
            this.positionCursor(this.row, this.column);
            System.out.print(Codes.DELETE);
        }
        if(this.rewriting == 1){
            this.rewriting = 0;
            this.row = this.rowRewriting;
            this.column = this.columnRewriting;
            this.positionCursor(this.row, this.column);
        }
    }

//Observer / Observable
    public void update(Observable obs, Object args) {
        if(obs == this.line) {
            try {
                switch(((Codes)args).getCode()) {
                    case Codes.TAB:
                        this.tab(line.getLength(), (char) line.getChar(this.linealPosition()-1));
                        break;
                    case Codes.SUPR:
                        this.suprimirChar();
                        break;
                    case Codes.DEL:
                        this.deleteChar();
                        break;
                    default:
                        this.character(line.getLength(), (char) line.getChar(this.linealPosition()-1));
                }                
            } catch(Exception ex) { ex.printStackTrace(); }
            
        }
    }
}