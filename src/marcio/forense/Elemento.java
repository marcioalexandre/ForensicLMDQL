package marcio.forense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

public class Elemento extends Conexao {
	private int id;
	private String nome;

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public ArrayList<Elemento> listarTodos() {
		try {
			ArrayList<Elemento> elementos = new ArrayList<Elemento>();
			String sql = "Select * from elemento";
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				Elemento ele = new Elemento();
				ele.id = rs.getInt(1);
				ele.nome = rs.getString(2);
				elementos.add(ele);
			}
			rs.close();
			stm.close();
			return elementos;
		} catch (SQLException e) {
			System.out.println("Erro na conexao (listarTodos): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Erro na funcao 'listarTodos' em Elemento:"+ e.getMessage());
			return null;
		}finally{
			try {
				Conexao.conn.close();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar banco:"+ e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			}
		}
	}

	public Elemento listarPeloId(int id) {
		try {
			int i = 1;
			Elemento ele = new Elemento();
			String sql = "Select * from elemento where id="+id;
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			if(rs.next()){
				ele.id = rs.getInt(1);
				ele.nome = rs.getString(2);
			}
			rs.close();
			stm.close();
			return ele;

		} catch (SQLException e) {
			System.out.println("Erro na conexao (listarPeloId): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Erro na funcao listarPeloId de Elemento: " + e.getMessage());
			return null;
		}finally{
			try {
				Conexao.conn.close();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			} catch (Exception e) {
				System.out.println("Erro ao fechar banco:" + e.getMessage());
			}
		}
	}
	// public void save(Elemento ele) {}
	// public void delete(Elemento ele) {}


}
