package marcio.forense;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XPathQueryService;

public class Conexao {
	static String status="";
	static Connection conn = null;
	static int i=0;
	static int j=0;
	static Collection col = null;
	static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";

	public static Connection getConexao() throws SQLException {
		try{
			i++;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// String url =
			// "jdbc:mysql://localhost/mydb?user=marcio&password=Ma@1983";
			String url = "jdbc:mysql://127.0.0.1:3306/producao?user=marcio&password=Ma@1983";
			conn = DriverManager.getConnection(url);
			status = "Conexão aberta";
			//System.out.println(status);
		}catch (SQLException e){
			throw new RuntimeException(e);
		}catch (ClassNotFoundException e){
			throw new RuntimeException(e);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return conn;
	}
	
	public static void getClose() throws SQLException{
		j++;
		status = "Conexão fechada";
		//System.out.println(status);
		try{
			conn.close();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
	}
	public static void getConexaoXML() throws Exception{
		try{   
		   final String driver = "org.exist.xmldb.DatabaseImpl";
		   System.out.println("Driver: "+driver);
		   Class cl = Class.forName(driver);
		   Database database = (Database) cl.newInstance();
		   database.setProperty("create-database", "true");
		   DatabaseManager.registerDatabase(database);
	       String colPath = "/db/xbrl";
	       System.out.println("Caminho do banco:"+URI+colPath);
	       col = DatabaseManager.getCollection(URI+colPath);
           XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
           xpqs.setProperty("indent", "yes");
		}catch(Exception e){
			System.out.println("Erro como Exist DB: "+e.getMessage());
		}
	}
	public static void getCloseXML() throws Exception{
        if(col != null) {
            try { 
            	col.close(); 
            } catch(Exception xe) {
            	xe.printStackTrace();
            }
        }
	}
}
