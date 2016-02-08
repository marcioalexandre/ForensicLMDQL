package marcio.forense;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class EmpiricalRule implements UserDefinedFunction {
	DecimalFormat fmt = new DecimalFormat("###,####,#00.00");
	public String getDescription() {
		return "Retorna análise de documentos baseada na Lei Empírica";
	}

	public String getName() {
		return "EmpiricalRule";
	}

	public Type[] getParameterTypes() {
		//nome do documento, nome do elemento 
		//(os anos não se tornam interessantes pq na análise já vem todos)
		//return new Type[] {new StringType(), new StringType()};
		return new Type[] {new SetType(MemberType.Unknown), new SetType(MemberType.Unknown), new SetType(MemberType.Unknown), new SetType(MemberType.Unknown)};
	}

	public String[] getReservedWords() {
		return null;
	}

	public Type getReturnType(Type[] arg0) {
		return new StringType();
	}

	public Syntax getSyntax() {
		return Syntax.Function;
	}
    
	private BigDecimal mediaAritimetica(ArrayList<BigDecimal> arg) {
		//http://educacao.uol.com.br/matematica/media-desvio-padrao-e-variancia-nocoes-de-estatistica.jhtm
        double somatorio = 0;  
        for (BigDecimal d : arg) {
            somatorio += d.doubleValue();  
        }  
        return BigDecimal.valueOf(somatorio / arg.size());  
    }  
	
    private BigDecimal desvioPadrao(ArrayList<BigDecimal> arg) {  
    	//http://educacao.uol.com.br/matematica/media-desvio-padrao-e-variancia-nocoes-de-estatistica.jhtm
    	//System.out.println("Entrou no desvio padrão");
    	
        if (arg.size() == 1) {
        	System.out.println("Tamanho insuficiente para calcular DP");
            return BigDecimal.valueOf(0.0);  
        } else {
            BigDecimal ma = this.mediaAritimetica(arg);
            //System.out.println("Media Aritmetica:"+fmt.format(ma));
            double somatorio = 0;
            for (int i = 0; i < arg.size(); i++) {
            	//System.out.println("Valor a ser calculado:"+fmt.format(arg.get(i)));
                double result = arg.get(i).doubleValue() - ma.doubleValue();
                somatorio += result * result;
            }
            double variancia = somatorio/arg.size();
            //System.out.println("Variancia:"+fmt.format(variância));
            BigDecimal dp = BigDecimal.valueOf(Math.sqrt(variancia));
            return dp;
        }
    }
    public ArrayList<RelatorioDAO> relatoriosBefore (RelatorioDAO rel, ArrayList<RelatorioDAO> rels){
    	//System.out.println("entrou em relatoriosBefore");
    	ArrayList<RelatorioDAO> arrR = new ArrayList<RelatorioDAO>();
    	for (RelatorioDAO r: rels){
    		//System.out.println("RelatorioBefore.Data:"+r.getDate());
    		if (r.getDate().before(rel.getDate())){
    			arrR.add(r);
    		}
    	}
    	//ordenar datas dentro do arraylist
		Collections.sort(arrR);    	
    	return arrR;
    	
    }

	public Object execute(Evaluator eva, Argument[] arg) { //empresa,ano,documento,elemento
		long tempInicial = System.currentTimeMillis(); 
		long tempFinal = 0;
		List<Member> 	param0 = (List<Member>) arg[0].evaluateScalar(eva); // Empresa(s)
		List<Member> 	param1 = (List<Member>) arg[1].evaluateScalar(eva); // Data(s)
		List<Member> 	param2 = (List<Member>) arg[2].evaluateScalar(eva); // Documento(s)
		List<Member>	param3 = (List<Member>) arg[3].evaluateScalar(eva); // Elemento(s)
					
		String conclusao = "";
	
		//buscar no DW todos os elementos: empresa->documento->daquele ano.
	
		// ===================================== pegando entidades enviadas no MDX 28/10/2013
		ArrayList<EntidadeDAO> ents = new ArrayList<EntidadeDAO>();
		EntidadeDAO ent = new EntidadeDAO();
		ArrayList<String> arrEnt =  new ArrayList<String>();
		//System.out.println(param0.toString());
		for (int m=0;m<param0.size();m++){
			String membroString = String.valueOf(param0.get(m));
			String tokens[] = membroString.split("].");
			for (int i=0;i<tokens.length;i++){
				tokens[i] = tokens[i].replace("[", "");
				tokens[i] = tokens[i].replace("]", "");
				if (arrEnt.contains(tokens[i])){
				}else{
					arrEnt.add(tokens[i]);
				}
			}
		}
		for (String nomeEnt: arrEnt){
			EntidadeDAO tempEnt = ent.getEntidadeByName(arrEnt.get(0),nomeEnt);
			if (tempEnt.getNome() != null) {
				//System.out.println("Entidade : "+tempEnt.getNome());
				ents.add(tempEnt);
			}
		}
				
		// ===================================== pegando Tempo enviado no MDX 28/10/2013
		// poderá ser vários anos. arraylist de todos membros-anos: [tempo].children
		// poderá ser de um ano todo. arraylist de todos membros-meses: [tempo].[1992].children
		// de um mês de um ano. arraylist de todos os membros-dias: [tempo].[1992].[10].children
		// dia determinado [tempo].[1992].[10].[01]
		
		ArrayList<ArrayList<TempoDAO>> arrTems = new ArrayList<ArrayList<TempoDAO>>(); 
		for(int t=0; t<param1.size();t++){
			ArrayList<TempoDAO> tems = new ArrayList<TempoDAO>(); 
			TempoDAO tem = new TempoDAO();
			String membroString = String.valueOf(param1.get(t));
			membroString = membroString.replace("[", "");
			membroString = membroString.replace("]", "");
			membroString = membroString.replace(".", "-");
			String tokens[] = membroString.split("-");
			switch (tokens.length){
			case 4: //ano mes dia
				System.out.println("Entrou com dia mes e ano");
				tems = tem.getTempoDAOByDMA(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[1]));
				break;
			case 3: // ano mes
				System.out.println("Entrou com mes e ano");
				tems = tem.getTempoDAOByMesAno(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[1]));
				break;
			case 2: // ano
				System.out.println("Entrou com ano");
				tems = tem.getTempoDAOByAno(Integer.parseInt(tokens[1]));
				break;
			}
			arrTems.add(tems);
		}
		
		ArrayList<TempoDAO> tems = new ArrayList<TempoDAO>(); 
		for (ArrayList<TempoDAO> tempArrT: arrTems){
			for (TempoDAO tempT: tempArrT){
				System.out.println("Ano:"+tempT.getAno()+", Mes:"+tempT.getMes()+", Dia:"+tempT.getDia()+", Id:"+tempT.getId());
				tems.add(tempT);
			}
		}
		
		/*
		
		String data =  String.valueOf(param1);
		String[] datav = data.split("/");
		int dia = Integer.parseInt(datav[0]);
		int mes = Integer.parseInt(datav[1]);
		int ano = Integer.parseInt(datav[2]);
		tem = tem.getTempoDAOByDMA(dia,mes,ano);
		System.out.println("Tempo encontrado,ano:"+tem.getAno()+", mes:"+tem.getMes()+", dia:"+tem.getDia());
		*/
		
		// ===================================== pegando Documentos enviados no MDX 28/10/2013
		ArrayList<DocumentoDAO> docs = new ArrayList<DocumentoDAO>();
		DocumentoDAO doc = new DocumentoDAO();
		ArrayList<String> arrDoc =  new ArrayList<String>();
		for (int m=0;m<param2.size();m++){
			String membroString = String.valueOf(param2.get(m));
			String tokens[] = membroString.split("].");
			for (int i=0;i<tokens.length;i++){
				tokens[i] = tokens[i].replace("[", "");
				tokens[i] = tokens[i].replace("]", "");
				if (arrDoc.contains(tokens[i])){
				}else{
					arrDoc.add(tokens[i]);
				}
			}
		}
		for (String nomeDoc: arrDoc){
			DocumentoDAO auxDoc = doc.getDocumentoDAOByNome(arrDoc.get(0),nomeDoc);
			if (auxDoc.getNome() != null){
				System.out.println("Documento(s): "+auxDoc.getNome());
				docs.add(auxDoc);
			}
		}
		
		//doc = doc.getDocumentoDAOByNome(((String) param2).toString());
		//System.out.println("Documento encontrado, id:"+doc.getId()+", nome:"+doc.getNome());

		// ===================================== Carregando Elementos do relatório daquela entidade...10/10/2013
		
		ArrayList<ElementoDAO> eles = new ArrayList<ElementoDAO>();
		ElementoDAO ele = new ElementoDAO();
		ArrayList<String> arrEle =  new ArrayList<String>();
		for (int e=0;e<param3.size();e++){
			String membroString = String.valueOf(param3.get(e));
			String tokens[] = membroString.split("].");
			for (int i=0;i<tokens.length;i++){
				tokens[i] = tokens[i].replace("[", "");
				tokens[i] = tokens[i].replace("]", "");
				//System.out.println("Token: "+tokens[i]);
				if (arrEle.contains(tokens[i])){ //se o elemento já existe no array, não add
				}else{
					arrEle.add(tokens[i]);
				}
			}
		}
		eles = ele.getElementoDAOByArrNomeEle(arrEle);
		
		// ===================================== Carregando Relatórios...28/10/2013
		//Ver todos os documentos das Entidades...
		ArrayList<ArrayList<RelatorioDAO>> rels = new ArrayList<ArrayList<RelatorioDAO>>(); 
		ArrayList<RelatorioDAO> r = new ArrayList<RelatorioDAO>();
		/*
		Conexao con = new Conexao();
		
		try {
			System.out.println("conexão abrindo...");
			con.getConexao();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

		RelatorioDAO rel = new RelatorioDAO();
		rels = rel.getRelByArrETDE(ents,tems,docs,eles);
		System.out.println("Qt de registros-relatorios (code line: 249): "+rels.size());
		/*
		for (EntidadeDAO e: ents){
			for (DocumentoDAO d: docs){
				for (TempoDAO t: tems){
					for (ElementoDAO el: eles){
						//cria um arrayList com as sequencias Ent+Doc registrados e só passa qd não existir no arrayList.
						//if(!arrtempData.contains(tempData)){		
							//System.out.println("rel.getRelByETDE("+e.getId()+","+t.getId()+","+d.getId()+","+el.getId()+")");
							RelatorioDAO rel = new RelatorioDAO();
							r = rel.getRelByETDE(e.getId(),t.getId(),d.getId(),el.getId());
							rels.add(r); // em caso de vários documentos (continuar raciocinio)
					}
				}
			}
		}
		*/		
		/*
		try {
			con.conn.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		String result = "";
		// ===================================== Aplicar a Regra Empirica
		
		// ===================================== Carregar valores, para aplicar a Regra Empírica
		//ArrayList<Double> valores = new ArrayList<Double>();
		
		 if (rels.size() > 0){
			int j= 0;
			for(ArrayList<RelatorioDAO> rel2: rels){
				//System.out.println("Entrou! Quat de arrays: "+rel2.size());
				//result += "==============<br>";
				for (RelatorioDAO r2: rel2){
					//System.out.println("Entrou2!");
					DocumentoDAO d= new DocumentoDAO();
					//EntidadeDAO e =new EntidadeDAO();
					ElementoDAO el=new ElementoDAO();
					j++;
					result += "<p align='Left'>";
					//System.out.println("===================== Relatorio:"+r2.getId());
					//result += "Relatorio:"+r2.getId()+". ";
					SimpleDateFormat df= new SimpleDateFormat("MM/dd/yyyy");
					//result += "<p align='Left'><font size='1'>"+df.format(r2.getDate())+". ";
					//result += "Document: "+d.getDocumentoDAOById(r2.getDocumento_id()).getNome()+". ";
					//System.out.println("Data do Relatorio: "+df.format(r2.getDate()));
					// pegar cada relatório com seus elementos 10/11/2013
					// puxar os valores de relatórios com mesmos elementos de três tempos passados
					
					//puxar os demais relatório que tem na base para comparar as datas
					ArrayList<RelatorioDAO> passRels = new ArrayList<RelatorioDAO>();
					RelatorioDAO passRel = new RelatorioDAO();
					passRels = passRel.getRelByEntDocEle(r2.getEntidade_id(), r2.getDocumento_id(), r2.getElemento_id());
					System.out.println("Elemento id:"+r2.getElemento_id());
					ArrayList<RelatorioDAO> sortRelatorio = new ArrayList<RelatorioDAO>(); 
					
					sortRelatorio = this.relatoriosBefore(r2, passRels);
					result += j+". Element: "+el.getElementoDAOById(r2.getElemento_id()).getNome()+".</font><br>";
					
					/*
					result += "Stored Data(s): {";
					for (RelatorioDAO auxR: sortRelatorio){
						result += fmt.format(auxR.getValor())+"|";
					}
					result += "}. <br>";
					*/
					result += "Sigma position (New Value: R$ "+fmt.format(r2.getValor())+"):";
					if (sortRelatorio.size() < 2){
						result += "<font color='green'>There are not enough stored data.</font><br></p>";
					}else{
						//DecimalFormat fmt = new DecimalFormat("###,###,###,#00.00");
						ArrayList<BigDecimal> valor= new ArrayList<BigDecimal>();
						for (int i=0; i<sortRelatorio.size();i++){
							//System.out.println("Valores capturados: "+sortRelatorio.get(i).getValor());
							valor.add(BigDecimal.valueOf(sortRelatorio.get(i).getValor()));
						}
						BigDecimal mar = this.mediaAritimetica(valor);
						BigDecimal dpa = this.desvioPadrao(valor);
						double ma = mar.doubleValue();
						double dp = dpa.doubleValue();
						System.out.println("Media: "+ma+", Devio padrao:"+dp);
						//System.out.println("Média Padrão:"+fmt.format(ma));
						//System.out.println("Desvio Padrão:"+fmt.format(dp));
						if (r2.getValor() < ma ){
							result += "-";
							if (r2.getValor() < (ma-(3*dp))){
								result += "<font color='red'>out</font>";
							}else{
								result += "<font color='blue'>";
								if (r2.getValor() < (ma-(2*dp)) && r2.getValor() > (ma-(3*dp)) || r2.getValor() == (ma-(3*dp)) ){
									result +="3rd sigma";
								}else{
									if (r2.getValor() < (ma-dp) && r2.getValor() > (ma-(2*dp)) || r2.getValor() == (ma-(2*dp)) ){
										result +="2nd sigma";
								}else{
									if (r2.getValor() < ma && r2.getValor() > (ma-dp)|| (r2.getValor() == (ma) || r2.getValor() == (ma-dp))){
										result +="1st sigma";
									}
								}
								}
								result += "</font>";
							}
						}else{
							result += "+";
							if (r2.getValor() > (ma+(3*dp))){
								result += "<font color='red'>out</font>";
							}else{
								result += "<font color='blue'>";
								if ( (r2.getValor() > (ma+(2*dp)) && r2.getValor() < (ma+(3*dp))) || (r2.getValor() == (ma+(3*dp))) ){
									result +="3rd sigma";
								}else{
									if ( (r2.getValor() > (ma+dp) && r2.getValor() < (ma+(2*dp))) || (r2.getValor() == (ma+(2*dp))) ){
										result +="2nd sigma";
									}else{
										if ( (r2.getValor() > ma && r2.getValor() < (ma+dp)) || ((r2.getValor() == (ma)) || (r2.getValor() == (ma+dp))) ){
											result +="1st sigma";
										}
									}
								}
								result += "</font>";
							}
						}
						//result += ". <Br><font color='black' size='1'>Média Aritm:"+fmt.format(ma)+", Desvio Padrao:"+fmt.format(dp)+".</font>";	
						result += "<br><br></p>";
						//System.out.println(result);
						
					}
					//System.out.println("===================== Fim do relatorio:"+r2.getId());
					
					/*
					if (passRels.size() < 3){
						return "Não há relatórios suficientes na base de dados para o cálculo da Regra Empírica";
					}else{
						//pegar do montante as três ultimas datas, deixar em memória estes relatório, limpar o array e recarregar.
						//ArrayList<RelatorioDAO> ThreeLastRels = this.dataCompare(r2, passRels);
							
					}
					*/
					}
				//result += "==============<br>";
				tempFinal = System.currentTimeMillis(); 
				}
				long dif = tempFinal - tempInicial;
				result += "Number of checked element(s): "+j+"<Br>";
				result += "Performance time: "+String.format("%02d second(s)  %02d milliseconds", dif/1000, dif%1000);
		}else{
			result += "Sem relatórios com os parâmetros informados";
		}
		System.out.println("Conexão aberta: "+Conexao.i);
		System.out.println("Conexão fechada: "+Conexao.j);

		return result;
		
		
		
/*		
		final Object param1 = arg1[0].evaluateScalar(arg0); //empresa do documento
		final Object param2 = arg1[1].evaluateScalar(arg0); //ano do documento
		final Object param3 = arg1[0].evaluateScalar(arg0); //documento
		final Object param4 = arg1[0].evaluateScalar(arg0); //elemento do documento a ser analisado
		
		String result = "";
		//buscar no DW todos os elementos: empresa->documento-> ano.
		EntidadeDAO ent = new EntidadeDAO();
		ent.getEntidadeByName(((String) param1).toString());
		
		TempoDAO tem = new TempoDAO();
		tem.getTempoDAOByDMA(0, 0, 0);
		
		DocumentoDAO doc = new DocumentoDAO();
		doc.getDocumentoDAOByNome(((String) param3).toString());
		
		RelatorioDAO rel = new RelatorioDAO();
		ArrayList<RelatorioDAO> rels = rel.getRelByETD(ent.getId(),tem.getId(),doc.getId());

		ArrayList<ElementoDAO> eles = new ArrayList<ElementoDAO>();
		for(RelatorioDAO r: rels){
			ElementoDAO ele = new ElementoDAO();
			ele.getElementoDAOById(r.getEntidade_id());
			eles.add(ele);
		
			//Pegar o valor do elemento do documento, dos anos anteriores
			for (ElementoDAO e: eles){
				ArrayList<Double> valores = new ArrayList<Double>();
				for (int i=1;i<=3;i++){
					//RelatorioDAO rel2 = rel.getRelByETDE(r.getEntidade_id(), tem.getAno()-i, r.getDocumento_id(), r.getElemento_id());
					//valores.add(rel2.getValor());
				}
				double ma = this.mediaAritimetica(valores);
				double dp = this.desvioPadrao(valores);
				
				//calcular sigma do valor na Regra Empírica do relatório corrente, com relação aos valores dos outros anos:
				result = e.getNome()+": ";
				if (r.getValor() < ma ){
					result += "-";
					if (r.getValor() < (ma-(3*dp))){
						result += "out";
					}else{
						if (r.getValor() < (ma+(2*dp)) && r.getValor() < (ma+(3*dp)) ){
							result +="3";
						}else{
							if (r.getValor() < (ma+dp) && r.getValor() < (ma+(2*dp)) ){
								result +="2";
						}else{
							if (r.getValor() < ma && r.getValor() > (ma+dp)){
								result +="1";
							}
						}
						}
					}
				}else{
					result += "+";
					if (r.getValor() > (ma+(3*dp))){
						result += "out";
					}else{
						if (r.getValor() > (ma+(2*dp)) && r.getValor() < (ma+(3*dp)) ){
							result +="3";
						}else{
							if (r.getValor() > (ma+dp) && r.getValor() < (ma+(2*dp)) ){
								result +="2";
						}else{
							if (r.getValor() < ma && r.getValor() < (ma+dp)){
								result +="1";
							}
						}
						}
					}

				}
				result = "<br>";
			} //for dos elementos

		} //for dos relatórios
		return result;
*/
	} //execute
	
} //it closes EmpiricalRule class 
