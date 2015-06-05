

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.mysql.jdbc.Statement;

import Conexion.Conexion;

public class Cliente {
	
	private static Socket socket;
	
	private static String host = "192.168.0.16";
	private static int port = 5055;
	
	private static BufferedReader in;
	private static PrintWriter out;
	private static ArrayList<String> matr = new ArrayList<>();
	
	private static Conexion con = new Conexion();
	
	static boolean status;
	
	public static void main(String[] args) throws InterruptedException{
		try{
			recv();
		} catch(Exception ex){
			/* Cuando se cae el servidor, entra el catch, donde ve si
			 * el cliente fue seleccionado como servidor */
			if	(status){
				BServer server = new BServer(port);
				try {
					server.start();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				ex.printStackTrace();
				Thread.sleep(1000);
				try{
					recv();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static void recv() throws UnknownHostException, IOException, InterruptedException{
		System.out.println("gato");
		socket = new Socket(host, port);
		
		in = new BufferedReader (
				new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(
				socket.getOutputStream(), true);
		System.out.println("perro");
		
		
		String mat = in.readLine();
		
		if(mat.equals(1)){
			
			status = true;
			System.out.println("woof");
			out.println(socket.getInetAddress());
			port++;
			
		} else {
			status = false;
			Thread.sleep(500);
			host = in.readLine();
			port++;
		}
		
		while ((mat = in.readLine()) != null){
			matr.add(mat);
			//(new Querys(mat, link)).start();
		}
		
		readDB(out);

	}
	
	static void readDB(PrintWriter out){
		
		Connection link = con.Conectar();
		
		Statement s;
		try {
			s = (Statement) link.createStatement();
			System.out.println("Statement creado");
			ResultSet rs = s.executeQuery("SELECT * FROM NuevoLeon");
			
			//Busca en la base de datos los registros y los envia al servidor
			while (rs.next()){
				String dummy = rs.getString(1);
				for (int i = 0; i < matr.size(); i++){
					String mem = matr.get(i);
					if (dummy.equals(mem)){
						String answ = (rs.getString(1) + "  " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
						out.println(socket.getLocalAddress() + " " + answ);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 *Lo hiba a hacer que en cada hilo buscara un registro, pero el problema es que
	 *los hilos seguian con los registros en vez de empezar cada uno desde 0
	 *por ejemplo, el hilo 0 empezaba desde el primer registro, pero el hilo 1 no empezaba
	 *desde 0, si no mas bien continuaba desde el ultimo que habia usado el hilo 0, y asi 
	 *con los demas hilos
	 
	 
	 
	static class Querys extends Thread{
		
		String matriculas;
		
		public Querys(String matriculas, Connection link){
			this.matriculas = matriculas;
		}
		
		public void run(){
			System.out.println("Thread iniciado: " + matriculas);
		}
	}*/
	
}


//Inicializa el servidor cuando el servidor principal se cae
class BServer{
	
	private ServerSocket newServer;
	private Socket newClient;
	
	int port;
	int cont = 0;
	
	String info;
	
	ArrayList<String> matriculas = new ArrayList<>();
	
	public BServer(int port){
		this.port = port;
	}
	
	public void start() throws InterruptedException{
		String in = "";
		Scanner reader = new Scanner(System.in);
		System.out.println("Llenado de Matriculas ('q' para terminar)");
		
		while (in != "q"){
			in = reader.nextLine();
		}
		
		
		try {
			
			newServer = new ServerSocket(port);
			System.out.print("Esperando Clientes...");
			
			while(true){
				try {
					cont++;
					newClient = newServer.accept();
					(new Clientes(newClient, cont)).start();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private class Clientes extends Thread{
		
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		
		int cont;
		
		Clientes(Socket socket, int cont){
			this.socket = socket;
			this.cont = cont;
		}
		
		public void run(){
			System.out.println("Conexion desde: " + socket.getInetAddress());
			
			try {
				
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				System.out.println("Enviando...");
				
				if (cont == 1){
					out.println(cont);
					info = in.readLine();
					System.out.println(info);
				} else {
					out.println(0);
					Thread.sleep(100);
					out.println(info);
				}
				
				for (String s : matriculas) {
					out.println(s);
				}
				
				String test;
				
				while ((test = in.readLine()) != null){
					System.out.println(test);
				}
				
			} catch (Exception ex){
				
			}
		}
		
	}
	
}
