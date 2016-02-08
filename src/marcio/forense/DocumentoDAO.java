package marcio.forense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

public class DocumentoDAO extends Conexao{
	private int id;
	private String nome;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public DocumentoDAO getDocumentoDAOByNome(String tabela, String nome){
		try {
			DocumentoDAO doc = new DocumentoDAO();
			String sql = "Select * from documento where nome ='"+nome+"'";
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				doc.id 		= rs.getInt(1);
				doc.nome	= rs.getString(2);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return doc;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				Conexao.getClose();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public DocumentoDAO getDocumentoDAOById(int id){
		try {
			DocumentoDAO doc = new DocumentoDAO();
			String sql = "Select * from documento where id = "+id;
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				doc.id 		= rs.getInt(1);
				doc.nome	= rs.getString(2);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return doc;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				Conexao.getClose();
			} catch (Exception e) {
				System.out.println(e.getMessage());;
			}
		}
	}

}
