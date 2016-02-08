package marcio.forense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

public class ElementoDAO extends Conexao {
	private int id;
	private String nome;
	private String substitutionGroup;
	private String type;
	private int	Taxonomia_id;
	private int	nillable;
	private int abstrato;
	private String periodoType;
	
	//setters and getters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	
	public String getSubstitutionGroup() {
		return substitutionGroup;
	}
	public void setSubstitutionGroup(String substitutionGroup) {
		this.substitutionGroup = substitutionGroup;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTaxonomia_id() {
		return Taxonomia_id;
	}
	public void setTaxonomia_id(int taxonomia_id) {
		Taxonomia_id = taxonomia_id;
	}
	public int getNillable() {
		return nillable;
	}
	public void setNillable(int nillable) {
		this.nillable = nillable;
	}
	public int getAbstrato() {
		return abstrato;
	}
	public void setAbstrato(int abstrato) {
		this.abstrato = abstrato;
	}
	public String getPeriodoType() {
		return periodoType;
	}
	public void setPeriodoType(String periodoType) {
		this.periodoType = periodoType;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	//functions
	public ElementoDAO getElementoDAOByNome(String nome){
		try {
			ElementoDAO ele = new ElementoDAO();
			String sql = "Select id,nome from elemento where nome='"+nome+"'";
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			if(rs.next()){
				ele.id 		= rs.getInt(1);
				ele.nome	= rs.getString(2);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return ele;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getElementoDAOByNome function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getElementoDAOByNome' function:"+ e.getMessage());
			return null;
		}finally{
			try {
				Conexao.getClose();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar banco:"+ e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			}
		}
	}
	public ArrayList<ElementoDAO> getElementoDAOByArrNomeEle(ArrayList<String> eles){
		try {
			Conexao.getConexao();
			ArrayList<ElementoDAO> arrEle = new ArrayList<ElementoDAO>();
			for (String e: eles){
				//System.out.println("Elemento (getElementoDAOByArrNomeEle):"+e);
				ElementoDAO ele = new ElementoDAO();
				String sql = "Select id,nome from elemento where nome='"+e+"'";
				Statement stm = (Statement) conn.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				if(rs.next()){
					ele.id 		= rs.getInt(1);
					ele.nome	= rs.getString(2);
				}
				arrEle.add(ele);
				rs.close();
				stm.close();
			}
			Conexao.getClose();
			return arrEle;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getElementoDAOByNome function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getElementoDAOByNome' function:"+ e.getMessage());
			return null;
		}finally{
			try {
				Conexao.getClose();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar banco:"+ e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			}
		}
	}
	
	public ArrayList<ElementoDAO> getElementos(){
		try {
			Conexao.getConexao();
			ArrayList<ElementoDAO> arrEle = new ArrayList<ElementoDAO>();
			String sql = "Select * from elemento";
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				ElementoDAO ele = new ElementoDAO();
				ele.id 		= rs.getInt(1);
				ele.nome	= rs.getString(2);
				arrEle.add(ele);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return arrEle;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getElementoDAOByNome function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getElementoDAOByNome' function:"+ e.getMessage());
			return null;
		}finally{
			try {
				Conexao.getClose();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar banco:"+ e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			}
		}
	}	
	
	
	public ElementoDAO getElementoDAOById(int id){
		try {
			ElementoDAO ele = new ElementoDAO();
			String sql = "Select id,nome from elemento where id="+id;
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			if(rs.next()){
				ele.id 		= rs.getInt(1);
				ele.nome	= rs.getString(2);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return ele;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getElementoDAOById function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getElementoDAOById' function:"+ e.getMessage());
			return null;
		}finally{
			try {
				Conexao.getClose();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar banco:"+ e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			}
		}
	}
	
	
	
}
