package marcio.forense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.mysql.jdbc.Statement;

public class RelatorioDAO extends Conexao implements Comparable<RelatorioDAO> {
	private int id;
	private int Entidade_id;
	private int Label_id;
	private int Tempo_id;
	private int Elemento_id;
	private int Documento_id;
	private double valor;
	private Date data;
	public int compareTo(RelatorioDAO arg) {
		int x = 0;
		if (this.data.before(arg.data)) {
			x = 1;
	    }
	    if (this.data.after(arg.data)) {
	    	x = -1;
	    }
	    return x;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEntidade_id() {
		return Entidade_id;
	}
	public void setEntidade_id(int entidade_id) {
		Entidade_id = entidade_id;
	}
	public int getTempo_id() {
		return Tempo_id;
	}
	public void setTempo_id(int tempo_id) throws ParseException {
		Tempo_id = tempo_id;
	}
	public int getElemento_id() {
		return Elemento_id;
	}
	public void setElemento_id(int elemento_id) {
		Elemento_id = elemento_id;
	}
	public int getDocumento_id() {
		return Documento_id;
	}
	public void setDocumento_id(int documento_id) {
		Documento_id = documento_id;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public Date getDate(){
		return this.data;
	}
	//ent.getId(),tem.getId(),doc.getId(),ele.getId()
	public ArrayList<RelatorioDAO> getRelByEntTemDoc(int idEmpresa,int idTempo, int idDoc){
		try {
			ArrayList<RelatorioDAO> rels = new ArrayList<RelatorioDAO>();
			String sql = "Select * from relatorio where Documento_id="+idDoc+" and Tempo_id="+idTempo+" and Entidade_id="+idEmpresa;
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()){
				RelatorioDAO rel = new RelatorioDAO();
				rel.id 				= rs.getInt(1);
				rel.Entidade_id		= rs.getInt(2);
				rel.Elemento_id		= rs.getInt(3);
				//rel.Label_id		= rs.getInt(4);
				rel.Documento_id	= rs.getInt(4);
				rel.Tempo_id		= rs.getInt(5);
				rel.valor			= rs.getDouble(6);
				TempoDAO t = new TempoDAO();
				t = t.getTempoDAOById(rel.getTempo_id());
				SimpleDateFormat df= new SimpleDateFormat("dd/MM/yyyy");
				rel.data			= df.parse(t.getDia()+"/"+t.getMes()+"/"+t.getAno()); 
				rels.add(rel);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return rels;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getRelByEntTemDoc function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getRelByEntTemDoc' function:"+ e.getMessage());
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
	public ArrayList<RelatorioDAO> getRelByEntDocEle(int idEmpresa,int idDoc,int idElemento ){
		try {
			ArrayList<RelatorioDAO> rels = new ArrayList<RelatorioDAO>();
			String sql = "Select * from relatorio where Documento_id="+idDoc+" and Elemento_id="+idElemento+" and Entidade_id="+idEmpresa;
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()){
				RelatorioDAO rel = new RelatorioDAO();
				rel.id 				= rs.getInt(1);
				rel.Entidade_id		= rs.getInt(2);
				rel.Elemento_id		= rs.getInt(3);
				//rel.Label_id		= rs.getInt(4);
				rel.Documento_id	= rs.getInt(4);
				rel.Tempo_id		= rs.getInt(5);
				rel.valor			= rs.getDouble(6);
				TempoDAO t = new TempoDAO();
				t = t.getTempoDAOById(rel.getTempo_id());
				SimpleDateFormat dateformat= new SimpleDateFormat("dd/MM/yyyy");
				rel.data = dateformat.parse(t.getDia()+"/"+t.getMes()+"/"+t.getAno()); 
				rels.add(rel);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return rels;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getRelByEntDocEle function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getRelByEntDocEle' function:"+ e.getMessage());
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
	public ArrayList<RelatorioDAO> getRelByETDE(int idEmpresa,int idTempo, int idDoc, int idElemento){
		try {
			ArrayList<RelatorioDAO> rels = new ArrayList<RelatorioDAO>();
			String sql = "Select * from relatorio where Documento_id="+idDoc+" and Tempo_id="+idTempo+" and Entidade_id="+idEmpresa+" and Elemento_id="+idElemento;
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()){
				RelatorioDAO rel = new RelatorioDAO();
				rel.id 				= rs.getInt(1);
				rel.Entidade_id		= rs.getInt(2);
				rel.Elemento_id		= rs.getInt(3);
				//rel.Label_id		= rs.getInt(4);
				rel.Documento_id	= rs.getInt(4);
				rel.Tempo_id		= rs.getInt(5);
				rel.valor			= rs.getDouble(6);
				TempoDAO t = new TempoDAO();
				t = t.getTempoDAOById(rel.getTempo_id());
				SimpleDateFormat dateformat= new SimpleDateFormat("dd/MM/yyyy");
				rel.data			= dateformat.parse(t.getDia()+"/"+t.getMes()+"/"+t.getAno()); 
				rels.add(rel);
			}
			rs.close();
			stm.close();
			Conexao.getClose();
			return rels;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getRelByETDE function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getRelByETDE' function:"+ e.getMessage());
			return null;
		}finally{
			try{
				Conexao.getClose();
			}catch(Exception e){
				System.out.println("Error: Conexao (getRelByETDE function): " + e.getMessage());
			}
		}
	}
	
	public ArrayList<ArrayList<RelatorioDAO>> getRelByArrETDE(ArrayList<EntidadeDAO> ents,ArrayList<TempoDAO> tems,ArrayList<DocumentoDAO> docs,ArrayList<ElementoDAO> eles){
		try {
			//System.out.println("Entrou getRelByArrETDE");
			ArrayList<ArrayList<RelatorioDAO>> arrRels = new ArrayList<ArrayList<RelatorioDAO>>();
			Conexao.getConexao();
			Statement stm = (Statement) conn.createStatement();
			//System.out.println("Qtd ents: "+ents.size()+", Qtd tems: "+tems.size()+", Qtd docs: "+docs.size()+", Qtd eles: "+eles.size());
			//System.out.println("Entidade_id:"+ents.get(0).getId()+", Tempo_id:"+tems.get(0).getId()+", Documento_id: "+docs.get(0).getId());
			for (EntidadeDAO e: ents){
				for (DocumentoDAO d: docs){
					for (TempoDAO t: tems){
						for (ElementoDAO el: eles){		
							ArrayList<RelatorioDAO> rels = new ArrayList<RelatorioDAO>(); 
							String sql = "Select * from relatorio where Documento_id="+d.getId()+" and Tempo_id="+t.getId()+" and Entidade_id="+e.getId()+" and Elemento_id="+el.getId();
							ResultSet rs = stm.executeQuery(sql);
							int i = 0;
							while(rs.next()){
								//System.out.println("Entrou heavy metal!!!");
								RelatorioDAO rel = new RelatorioDAO();
								rel.id 				= rs.getInt(1);
								rel.Entidade_id		= rs.getInt(2);
								rel.Elemento_id		= rs.getInt(3);
								//rel.Label_id		= rs.getInt(4);
								rel.Documento_id	= rs.getInt(4);
								rel.Tempo_id		= rs.getInt(5);
								rel.valor			= rs.getDouble(6);
								TempoDAO te = new TempoDAO();
								te = te.getTempoDAOById(rel.getTempo_id());
								SimpleDateFormat dateformat= new SimpleDateFormat("dd/MM/yyyy");
								rel.data			= dateformat.parse(t.getDia()+"/"+t.getMes()+"/"+t.getAno()); 
								//System.out.println("Valor-relatório:"+rel.getValor());
								rels.add(rel);
							}
							//System.out.println("Numero registros:"+i);
							arrRels.add(rels);
							rs.close();
						}
					}
				}
			}
			stm.close();
			Conexao.getClose();
			return arrRels;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getRelByArrETDE function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getRelByArrETDE' function:"+ e.getMessage());
			return null;
		}finally{
			try{
				Conexao.getClose();
			}catch(Exception e){
				System.out.println("Error: Conexao (getRelByArrETDE function): " + e.getMessage());
			}
		}
	}
	
	
	/*
	public RelatorioDAO getRelByEDETemporal(int idEmpresa,int ano, int idDoc, int idElemento){
		try {
			RelatorioDAO rel = new RelatorioDAO();
			TempoDAO tem = new TempoDAO();
			tem.getTempoDAOByAno(ano);
			String sql = "Select * from relatorio where Documento_id="+idDoc+" and Tempo_id="+tem.getId()+" and Entidade_id="+idEmpresa+" and Elemento_id="+idElemento;
			this.getConexao();
			Statement stm = (Statement) conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			if(rs.next()){
				rel.id 				= rs.getInt(1);
				rel.Entidade_id		= rs.getInt(2);
				rel.Elemento_id		= rs.getInt(3);
				rel.Documento_id	= rs.getInt(4);
				rel.Tempo_id		= rs.getInt(5);
				rel.valor			= rs.getDouble(6);
			}
			rs.close();
			stm.close();
			return rel;
		} catch (SQLException e) {
			System.out.println("Error: Conexao (getRelByEDETemporal function): " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Error: 'getRelByEDETemporal' function:"+ e.getMessage());
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
	*/

}
