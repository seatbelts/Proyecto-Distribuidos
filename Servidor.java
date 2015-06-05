
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Servidor {
    
    private static ServerSocket server;
    private static Socket cliente;
    private static final int puerto = 5055;
    private static String info;
    
    //Supogamos que ya estan llenas por ahora
    static ArrayList<String> matriculas = new ArrayList<>();
    
    public static void main(String[] args) {
        
        String in = "";
        Scanner reader = new Scanner(System.in);
        int cont = 0;
        
        System.out.println("Llenado de matriculas ('q' para terminar): ");
        
        while (in != "q"){
            in = reader.nextLine();
        }
        
        //Implemento un metodo o las dejo asi?
        /*matriculas.add("SXA-4455");
        matriculas.add("DFS-9988");
        matriculas.add("gatito");
        matriculas.add("gatito");
        matriculas.add("gatito");
        matriculas.add("gatito");*/
        
        try {
            server = new ServerSocket(puerto);
            System.out.println("Esperando clientes...");
            
            while (true) {
                try {
                    cont++;
                    cliente = server.accept();
                    //Instancia un cliente y le da un id
                    (new Clientes(cliente, cont)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Servidor MultiThread
    static class Clientes extends Thread {
        
        private Socket cliente;
        private BufferedReader in;
        private InputStreamReader is;
        private PrintWriter out;
        
        int cont;
        
        public Clientes(Socket cliente, int cont){
            this.cliente = cliente;
            this.cont = cont;
        }
        
        public void run(){
            System.out.println("Cliente Conectado: " + cliente.getInetAddress());
            
            try {
                
                out = new PrintWriter(cliente.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                System.out.println("Sending");
                
                if (cont == 1) {
                    out.println(cont);
                    //recibe la ip
                    info = in.readLine();
                    System.out.println(info);
                } else {
                    out.println(0);
                    Thread.sleep(100);
                    out.println(info);
                }
                
                for(String s : matriculas) {
                    out.println(s);
                }
                System.out.println("f sending");
                
                String test;
                while ((test = in.readLine()) != null){
                    System.out.println(test);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
