package marcio.forense;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class LabelDAO extends Conexao {
	int id;
	String label;
	int Elemento_id;
	
	public LabelDAO getElementoByLabel(String label){
		try{
		String sql = "Select * from label where nome = '"+label+"'";
		this.getConexao();
		Statement stm = (Statement) conn.createStatement();
		ResultSet rs = stm.executeQuery(sql);
		LabelDAO lab = new LabelDAO();
		if (rs.next()){
			
			lab.id 		= rs.getInt(1);
			lab.label 	= rs.getString(2);
			lab.Elemento_id = rs.getInt(3);
		}
		return lab;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getElementoByLabel(String label)): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getElementoByLabel(String label)' function:"+ e.getMessage());
			return null;
		}finally{
			try {
				Conexao.getClose();
			} catch (SQLException e) {
				System.out.println("getElementoByLabel(String label)- Erro ao fechar banco:"+ e.getMessage());
			} catch (Exception e) {
				System.out.println("getElementoByLabel(String label)- Erro ao fechar banco:" + e.getMessage());
			}
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getElemento_id() {
		return Elemento_id;
	}
	public void setElemento_id(int elemento_id) {
		Elemento_id = elemento_id;
	}

	

}
