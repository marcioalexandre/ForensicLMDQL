package marcio.forense;

import java.sql.SQLException;

public class ConexaoMain extends Conexao {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		try {
			Conexao.getConexao();
			System.out.println(Conexao.status);
		} catch (Exception e) {
			System.out.println("Erro:" + e.getMessage());
		}

	}

}
