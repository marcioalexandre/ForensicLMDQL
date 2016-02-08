package marcio.forense;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.xbrlapi.data.bdbxml.*;
import org.xbrlapi.Entity;

public class EntidadeDAO extends Conexao {
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
	public ArrayList<EntidadeDAO> getEntidades(){
	try{
		ArrayList<EntidadeDAO> ents = new ArrayList<EntidadeDAO>();
		String sql = "Select * from entidade";
		getConexao();
		PreparedStatement stm = conn.prepareStatement(sql);  
		ResultSet rs = stm.executeQuery();
		while(rs.next()){
			EntidadeDAO ent = new EntidadeDAO();
			ent.id 	= rs.getInt(1);
			ent.nome	= rs.getString(2);
			ents.add(ent);
		}
		rs.close();
		stm.close();
		Conexao.getClose();
		return ents;
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

	public EntidadeDAO getEntidadeByName(String tabela, String nome){
	try{
		EntidadeDAO ent = new EntidadeDAO();
		tabela = "entidade";
		String sql = "Select * from entidade where nome = '"+nome+" '";
		getConexao();
		PreparedStatement stm = conn.prepareStatement(sql);  
		ResultSet rs = stm.executeQuery();
		if(rs.next()){
			ent.id 	= rs.getInt(1);
			ent.nome	= rs.getString(2);
		}
		rs.close();
		stm.close();
		Conexao.getClose();
		return ent;
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
	
	public EntidadeDAO getEntidadeById(int id){
	try{
		EntidadeDAO ent = new EntidadeDAO();
		String sql = "Select * from entidade where id = '"+id+" '";
		getConexao();
		PreparedStatement stm = conn.prepareStatement(sql);  
		ResultSet rs = stm.executeQuery();
		if(rs.next()){
			ent.id 	= rs.getInt(1);
			ent.nome	= rs.getString(2);
		}
		rs.close();
		stm.close();
		Conexao.getClose();
		return ent;
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
	
}
