import java.io.*;

public class Console{
    int rowOrigin, columnOrigin, row, column;
    Line line;
    
    public Console() throws Exception {
        this.row = 1;
        this.column = 1;
    }

//Variable initialization
    public void inicializar() throws Exception{
        System.out.print("\033[2J");
        this.rowOrigin = 0;
        this.columnOrigin = 0;
        System.out.print("\033["+this.rowOrigin+";"+this.columnOrigin+"f");
    }

    public void setLine(Line line) {
        this.line = line;
    }

//Information    
    protected int getColumns() throws Exception{
        Process colsProcess = new ProcessBuilder("bash", "-c", "tput cols 2> /dev/tty").start();
        BufferedReader colsReader = new BufferedReader(new InputStreamReader(colsProcess.getInputStream()));
        String cols = colsReader.readLine();
        return Integer.parseInt(cols);
    }
    
    protected int getRows() throws Exception{
       Process rowProcess = new ProcessBuilder("bash", "-c", "tput cols 2> /dev/tty").start();
       BufferedReader rowReader = new BufferedReader(new InputStreamReader(rowProcess.getInputStream()));
       String rows = rowReader.readLine();
       return Integer.parseInt(rows);
    }
    
    public void positionCursor(){
        int columnCursor = this.column + this.columnOrigin;
        int rowCursor = this.row + this.rowOrigin;
        System.out.print("\033["+rowCursor+";"+columnCursor+"f");
    }
    
    public int linealPosition() throws Exception {
        return ((this.getRowCursor()-1)*this.getColumns() + this.getColumnCursor());
    }

    public int[] matricialPosition(int len) throws Exception { //le pasamos tamaño total escrito: line.getLength
        int positions[] = new int[2];
        positions[1] = len % this.getColumns(); //Column
        if(positions[1] == 0) { //estoy justo al final
            positions[0] = len / this.getColumns(); //Row
        } else {
            positions[0] = len / this.getColumns() + 1; //Row
        }
        return positions;
    }

    public int getColumnCursor(){
        return this.column;
    }

    public int getRowCursor(){
        return this.row;
    }

//Movement of rows and columns
    public void increaseColumn(int len) throws Exception{
        this.column++;
        if (this.column > this.getColumns()){
            this.column = 1;
            this.increaseRow();
        } else if(this.column > len){
            this.column = len + 1;
        }
    }
    
    public void increaseRow() throws Exception{
        if(this.row < this.getRows()) this.row++;
    }
    
    public void decreaseColumn() throws Exception{
        if (this.row > 1) {
            if (this.column == 1){
                this.column = this.getColumns();
                this.decreaseRow();
            } else if(this.column > 1){
                this.column--;
            }
        } else {
            if (this.column == 1){
                this.column = 1;
            } else if(this.column > 1){
                this.column--;
            }
        }
    }
    
    public void decreaseRow() {
        if(this.row > 1) this.row--;
    }
    
//Writing function
    public void write(char line) throws Exception {
        System.out.print("\033[@");
        System.out.print(line);
        if(this.getRowCursor()*this.getColumns() < this.line.getLength()) {
            char ch = (char) this.line.getChar(this.getRowCursor()*this.getColumns());
            System.out.print("\033["+(this.getRowCursor()+1)+";1f");
            System.out.print("\033[@");
            System.out.print(ch);
            this.positionCursor();
        }
    }

//Characters, numbers, symbols and space
    public void character (int len, char line) throws Exception {
        this.write(line);
        this.increaseColumn(len);
        this.positionCursor();
    }

//Movement keys
    public void tab (int len, char line) throws Exception {
        this.column = this.column + 8; //1 tab = 8 space
        if (this.column > this.getColumns()){
            this.column = this.column % this.getColumns();
            this.increaseRow();
        } else if(this.column > len){
            this.column = len + 1;
        }
        this.write(line);
        this.positionCursor();
    }

    public void moveRight(int len) throws Exception {
        int[] pos = matricialPosition(len);
        //int[0] fila; int[1] columna --> last position
        if(pos[0] == this.row) { 
            //we are at last row
            if((pos[1]+1) != this.column) { 
                //we aren't at last column+1
                this.increaseColumn(len);
            }    
        } else {
            //other case
            this.increaseColumn(len);
        }
        this.positionCursor();
    }

    public void moveLeft() throws Exception {
        this.decreaseColumn();
        this.positionCursor();
    }

    public void moveHome() throws Exception {
        this.row = 1;
        this.column = 1;
        this.positionCursor();
    }

    public void moveEnd(int len) throws Exception {
        this.column = len + 1;
        int[] pos = matricialPosition(len);
        //int[0] fila; int[1] columna --> last position
        if(pos[0] == this.row) { 
            //we are at last row
            this.column = pos[1]+1;
        } else { 
            //we aren't at last row
            this.column = this.getColumns();
        }
        this.positionCursor();
    }

    public void moveUp() throws Exception {
        this.decreaseRow();
        this.positionCursor();
    }

    public void moveDown(int len) throws Exception {
        int[] pos = matricialPosition(len);
        //int[0] fila; int[1] columna --> last position
        if(pos[0] != this.row) { 
            //we aren't at last row
            this.increaseRow();
            if(pos[0] == this.row && pos[1] < this.column) {
                this.column = pos[1]+1;
            } 
        } 
        this.positionCursor();
    }

//Delete characteres
    public void deleteChar() throws Exception {
        this.decreaseColumn();
        this.positionCursor();
        System.out.print("\033[P");
    }
    
    public void suprimirChar() throws Exception {
        System.out.print("\033[P");
        this.positionCursor();
    }
}