  

import java.io.*;

public class Main {
    public static void main(String[] args){
                //EditableBufferedReader ebr = new EditableBufferedReader(new InputStreamReader(System.in));
                //String tecla = System.in.read();  
                
                try {
                    EditableBufferedReader ebr = new EditableBufferedReader(new InputStreamReader(System.in));    
                    //String tecla = ebr.readLine();
                    Console console = new Console();
                    console.inicializar();
                    String tecla = ebr.readLine();
                    //System.out.println(ebr.line.toString());
                } catch(Exception ex) {
                        ex.printStackTrace();
                }
        }
}
