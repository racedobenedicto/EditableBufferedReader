import java.io.*;
//Fichero de prueba
public class Main {
    public static void main(String[] args){               
                try {
                    EditableBufferedReader ebr = new EditableBufferedReader(new InputStreamReader(System.in));
                    Console console = new Console();
                    console.inicializar();
                    String tecla = ebr.readLine();
                } catch(Exception ex) {
                        ex.printStackTrace();
                }
        }
}
