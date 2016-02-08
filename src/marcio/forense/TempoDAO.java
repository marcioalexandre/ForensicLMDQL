package marcio.forense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

public class TempoDAO extends Conexao {
	private int id;
	private int dia;
	private int mes;
	private int ano;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDia() {
		return dia;
	}
	public void setDia(int dia) {
		this.dia = dia;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	
	public TempoDAO getTempoDAOById(int id){
		try {
			TempoDAO tem = new TempoDAO();
			String sql = "Select * from tempo where id="+id;
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			if (rs.next()){
				tem.id 	= rs.getInt(1);
				tem.dia = rs.getInt(2);
				tem.mes = rs.getInt(3);
				tem.ano = rs.getInt(4);
			}
			rs.close();
			stm.close();
			return tem;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public ArrayList<TempoDAO> getTempoDAOByDMA(int dia, int mes, int ano){
		try {
			ArrayList<TempoDAO> temps = new ArrayList<TempoDAO>(); 
			String sql = "Select * from tempo where dia="+dia+" and mes="+mes+" and ano="+ano;
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()){
				TempoDAO tem = new TempoDAO();
				tem.id 	= rs.getInt(1);
				tem.dia = rs.getInt(2);
				tem.mes = rs.getInt(3);
				tem.ano = rs.getInt(4);
				temps.add(tem);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return temps;
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
	
	public ArrayList<TempoDAO> getTempoDAOByAno(int ano){
		try {
			ArrayList<TempoDAO> temps = new ArrayList<TempoDAO>();
			String sql = "Select * from tempo where ano="+ano;
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				TempoDAO tem = new TempoDAO();
				tem.id 	= rs.getInt(1);
				tem.dia = rs.getInt(2);
				tem.mes = rs.getInt(3);
				tem.ano = rs.getInt(4);
				temps.add(tem);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return temps;
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
	} //
	
	public ArrayList<TempoDAO> getTempoDAOByMesAno(int mes, int ano){
		try {
			ArrayList<TempoDAO> temps = new ArrayList<TempoDAO>();
			String sql = "Select * from tempo where mes="+mes+" and ano="+ano;
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				TempoDAO tem = new TempoDAO();
				tem.id 	= rs.getInt(1);
				tem.dia = rs.getInt(2);
				tem.mes = rs.getInt(3);
				tem.ano = rs.getInt(4);
				temps.add(tem);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return temps;
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
	} //
	
	
} // class
