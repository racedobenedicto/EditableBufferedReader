
import java.io.*;

public class Console{
    int rowOrigin, columnOrigin, row, column;
    
    public Console() {
        //this.inicializar();
        this.row = 1;
        this.column = 1;
    }

    public void inicializar2() throws Exception{
        System.out.print("\033[6n");
        System.out.print(System.in.read());

        //String r2 = r.substring(2);
        //System.out.println("r2: " + r2);
        //String[] rr = r.split(";");
        //System.out.println(rr[0]);
        //String[] row1 = rr[0].split("[[");
        //System.out.println(row1[1]);
        //this.rowOrigin = Integer.parseInt(rr[0]);
        //this.columnOrigin = Integer.parseInt(rr[1]);
    }
//inicializar variables
    public void inicializar() throws Exception{
        System.out.print("\033[2J");
        this.rowOrigin = 0;
        this.columnOrigin = 0;
        System.out.print("\033["+this.rowOrigin+";"+this.columnOrigin+"f");
    }
//Informacion    
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

    public int getColumnCursor(){
        return this.column;
    }

    public int getRowCursor(){
        return this.row;
    }

//Movimiento de filas y columnas
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
    
//Escritura
    public void write(String line) throws Exception {
        System.out.print("\033["+(rowOrigin+1)+";"+(columnOrigin+1)+"f");
        //Process p = new ProcessBuilder("bash", "-c", "echo -en '\033[K' > /dev/tty").start();
        //if(p.waitFor() ==1) p.destroy();
        System.out.print("\033[K");
        System.out.print(line);
    }

    //Caracteres, numeros, simbolos y espacio

    public void character (int len, String line) throws Exception {
        this.increaseColumn(len);
        this.write(line);
        this.positionCursor();
    }

    //Teclas de movimiento

    public void tab (int len, String line) throws Exception {
        this.column = this.column + 8;
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
        this.increaseColumn(len);
        this.positionCursor();
    }

    public void moveLeft() throws Exception {
        this.decreaseColumn();
        this.positionCursor();
    }

    public void moveHome() throws Exception {
        this.row = 1;
        this.column = 1;
        //System.out.print("\033["+this.row+";"+this.column+"f");
        this.positionCursor();
    }

    public void moveEnd(int len) throws Exception {
        //this.row = 1;
        this.column = len + 1;
        //System.out.print("\033["+this.row+";"+this.column+"f");
        this.positionCursor();
    }

    public void moveUp() throws Exception {
        this.decreaseRow();
        this.positionCursor();
    }

    public void moveDown() throws Exception {
        //if(this.getRowCursor()+1 != null)  QUE HAY EN LA FILA DE DESPUES
        this.increaseRow();
        this.positionCursor();
    }

    //Borrar caracteres

    public void deleteChar(String line) throws Exception {
        this.decreaseColumn();
        this.write(line);
        this.positionCursor();
    }
    
    public void suprimirChar(String line) throws Exception {
        this.write(line);
        this.positionCursor();
    }
}