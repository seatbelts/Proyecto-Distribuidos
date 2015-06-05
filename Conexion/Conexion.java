package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Conexion {

	private String db = "test";
	private String user = "root";
	private String pass = "root";
	private String url = "jdbc:mysql://localhost/" + db;
	private Connection link = null;
	
	public Conexion(){}
	
	public Connection Conectar(){
		
		try{
			
			link = DriverManager.getConnection(this.url, this.user, this.pass);
			
		}catch (SQLException ex){
			
			System.out.println(ex.getMessage());
			
		}
		
		return link;
	}
	
	public void Desconectar(){
		
		try {
			
			link.close();
			
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
}
