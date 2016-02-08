package marcio.forense;

import marcio.forense.Xlink3;

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

public class EmpiricalRuleXBRL implements UserDefinedFunction {
	public String getDescription() {
		return "Retorna análise de documentos baseada na Lei Empírica";
	}

	public String getName() {
		return "EmpiricalRuleXBRL";
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
		@SuppressWarnings("unchecked")
		List<Member> 	param0 = (List<Member>) arg[0].evaluateScalar(eva); // Empresa(s)
		List<Member> 	param1 = (List<Member>) arg[1].evaluateScalar(eva); // Data(s)
		List<Member> 	param2 = (List<Member>) arg[2].evaluateScalar(eva); // Documento(s)
		List<Member>	param3 = (List<Member>) arg[3].evaluateScalar(eva); // Elemento(s)
		int j=0;
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
		
		DecimalFormat fmt = new DecimalFormat("###,####,#00.##");
		String empresa = arrEnt.get(1).toString().toLowerCase();
		System.out.println("Empresa: "+empresa);
				
		// ===================================== pegando Tempo enviado no MDX 28/10/2013
		// poderá ser vários anos. arraylist de todos membros-anos: [tempo].children
		// poderá ser de um ano todo. arraylist de todos membros-meses: [tempo].[1992].children
		// de um mês de um ano. arraylist de todos os membros-dias: [tempo].[1992].[10].children
		// dia determinado [tempo].[1992].[10].[01]
		
		ArrayList<ArrayList<TempoDAO>> arrTems = new ArrayList<ArrayList<TempoDAO>>();
		ArrayList<TempoDAO> tems = new ArrayList<TempoDAO>(); 
		int ano = 0;
		String quadrante = null;
		int dia=0;
		int mes=0;
		for(int t=0; t<param1.size();t++){

			TempoDAO tem = new TempoDAO();
			String membroString = String.valueOf(param1.get(t));
			System.out.println("Data enviada:"+membroString);
			membroString = membroString.replace("[", "");
			membroString = membroString.replace("]", "");
			membroString = membroString.replace(".", "-");
			String tokens[] = membroString.split("-");
			switch (tokens.length){
			case 4: //ano mes dia
				System.out.println("CAso 4");
				ano =  Integer.parseInt(tokens[1]);
				mes = Integer.parseInt(tokens[2]);
				System.out.println("Ano: "+ano+", Mes:"+mes);
				if (mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12 ){
					dia = 31;
				}else{
					dia = 30;
				}
				
				if (mes <= 3){
					quadrante = "Q1";
				}else{
					if (mes > 3 && mes < 7){
						quadrante= "Q2";
					}else{
						if (mes > 6 && mes < 10){
							quadrante = "Q3";
						}else{
							if (mes > 9 && mes <13){
								quadrante = "Q4";
							}
						}
					}
				}
				
				System.out.println("Quadrante:"+quadrante);
				break;
			case 3: // ano mes
				System.out.println("CAso 3");
				ano =  Integer.parseInt(tokens[1]);
				mes = Integer.parseInt(tokens[2]);
				if (mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12 ){
					dia = 31;
				}else{
					dia = 30;
				}
				
				if (mes <= 3){
					quadrante = "Q1";
				}else{
					if (mes > 3 && mes < 7){
						quadrante= "Q2";
					}else{
						if (mes > 6 && mes < 10){
							quadrante = "Q3";
						}else{
							if (mes > 9 && mes <13){
								quadrante = "Q4";
							}
						}
					}
				}						
				break;
			case 2: // ano
				System.out.println("Entrou somente com ano - tratar essa possibilidade");
				//tems = null; // tem.getTempoDAOByAno(Integer.parseInt(tokens[1]));
				break;
			}
		}
		System.out.println("Mes: "+Integer.valueOf(fmt.format(mes)));
		// ===========Carregando Xlink3
		System.out.println("Xlink3(empresa: "+empresa+" "
				+ "ano: "+ano+" mes: "+mes+" dia: "+dia+" quadrante: "+quadrante+")");
		Xlink3 xl = new Xlink3(empresa, ano, String.format("%02d", mes), dia, quadrante);	
		ArrayList<String> arrValue = new ArrayList<String>();
		ArrayList<String> arrEleNew = new ArrayList<String>();
		// ===========pegando Documentos enviados no MDX 10/08/2014
		conclusao += "<p align='left'>";
		try {
			System.out.println("Nome arquivo:"+xl.nomearquivo);
			if (!xl.checkDocumento(xl.nomearquivo+".xml")){
				conclusao = "There is not this XBRL file.<Br>";
			}else{ //todo o processo inicia aqui
				//verificar os documentos XBRL do banco
				ArrayList<String> arrDoc = xl.getDocs();
				ArrayList<String> arrEle = new ArrayList<String>(); 
				String membroString = null;
				ArrayList<String> ArrEle = new ArrayList<String>();
				arrDoc.remove(xl.nomearquivo+".xml"); //remove o file sob análise
				System.out.println("Quantidade de docs:"+arrDoc.size());
				// ============= verifica se é elemento único ou não
				ArrayList<ElementoDAO> eles = new ArrayList<ElementoDAO>();
				ElementoDAO ele = new ElementoDAO();
				ArrayList<BigDecimal> vals = new ArrayList<BigDecimal>();
				if (param3.size() == 1){ //chamada por um elemento
					//pega o nome do elemento
					for (int e=0;e<param3.size();e++){
						membroString = String.valueOf(param3.get(0));
						String tokens[] = membroString.split("].");
							membroString = tokens[1].replace("[", "");
							membroString = membroString.replace("]", "");
						}
						//membroString = tokens[0];
						//}
					//verifica se é elemento ou label
					String value = xl.getValueElement(xl.nomearquivo, membroString);
					System.out.println("Elemento solicitado (29/08):"+membroString);
					if (value.equals(null) || value == null){
						//se entrar aqui é pq o valor é null e o elemento não foi achado
						membroString = xl.getElementByLabel(xl.nomearquivo, membroString);
						String[] e = membroString.split("_");
						membroString = e[0]+":";
						membroString = e[1];
					}
					System.out.println("Valor Elemento: "+value+", baseado no label "+membroString);
					ArrEle.add(membroString);
				}else{
					// ============= Carregando todos Elementos do schema XBRL...09/08/2014
					ArrEle = null;
					ArrEle = xl.getElements(xl.nomearquivo+".xsd");
					System.out.println("Qtd de elementos (esquemas):"+ArrEle.size());
					System.out.println("Arquivo Corrente:"+xl.nomearquivo+".xml");
					System.out.println("Buscando dados nos arquivos XBRL e processando...");
				}
				for (String ele1: ArrEle){
					ele1 = ele1.replace("_",":");
					System.out.println("Elemento (linha 274 EmpiricalRuleXBRL):"+ele1);
					//verifica os elemento da instãncia XBRL em análsie
					String value = xl.getValueDoc(xl.nomearquivo+".xml",xl,ele1);
					if (value == null || value.equals(null)){ //não há valor
						//nao faz nada diante de um campo vazio, identifica a não existencia do elemento
					}else{ //existe valor
						Xlink3 xl2 = null;	
						j++;
						conclusao += j+". Element: "+ele1+"<Br>Sigma position (Current Value:"+value+" ):";
						//busca os outros arquivos do banco XML
						for (String filename: arrDoc){
							int year = xl.getYearOfDoc(filename);
							String q = xl.getQuadranteOfDoc(filename); 
							xl2 = new Xlink3(empresa, year, String.format("%02d", mes), dia, q);
							//System.out.print("Arquivo: "+filename+", ano: "+year+", quadrante: "+q+", elemento: "+ele+", valor: ");
							String v = xl.getValueDoc(filename,xl2,ele1);
							//System.out.println("valor:"+v);
							if (v == null || v.equals(null)){
								
							}else{
								BigDecimal bdv = new BigDecimal(v);
								vals.add(bdv);
							}
						}

						if (vals.size() <= 1){
							conclusao += "<font color='green'>There are not enough stored data.</font><br><br>";
						}else{
							//DecimalFormat fmt = new DecimalFormat("###,###,###,#00.00");
							BigDecimal mar = this.mediaAritimetica(vals);
							BigDecimal dpa = this.desvioPadrao(vals);
							double ma = mar.doubleValue();
							double dp = dpa.doubleValue();
							//System.out.println("Media: "+ma+", Devio padrao:"+dp);
							//System.out.println("Média Padrão:"+fmt.format(ma));
							//System.out.println("Desvio Padrão:"+fmt.format(dp));
							//for (BigDecimal r2: vals){ //
							Double newValue = Double.parseDouble(value);
							if (newValue <= ma){
								conclusao += "-";
								if (newValue < (ma-(3*dp))){
									conclusao += "<font color='red'>out</font>";
								}else{
									conclusao += "<font color='blue'>";
									if (newValue < (ma-(2*dp)) && newValue > (ma-(3*dp)) || newValue == (ma-(3*dp)) ){
										conclusao +="3rd sigma";
									}else{
										if (newValue < (ma-dp) && newValue > (ma-(2*dp)) || newValue == (ma-(2*dp)) ){
											conclusao +="2nd sigma";
									}else{
										if (newValue < ma && newValue > (ma-dp)|| (newValue == (ma) || newValue == (ma-dp))){
											conclusao +="1st sigma";
										}
									}
									}
								}
							}else{
								conclusao += "+";
								if (newValue > (ma+(3*dp))){
									conclusao += "<font color='red'>out</font>";
								}else{
									conclusao += "<font color='blue'>";
									if ( (newValue > (ma+(2*dp)) && newValue < (ma+(3*dp))) || (newValue == (ma+(3*dp))) ){
										conclusao +="3rd sigma";
									}else{
										if ( (newValue > (ma+dp) && newValue < (ma+(2*dp))) || (newValue == (ma+(2*dp))) ){
											conclusao +="2nd sigma";
										}else{
											if ( (newValue > ma && newValue < (ma+dp)) || ((newValue == (ma)) || (newValue == (ma+dp))) ){
												conclusao +="1st sigma";
											}
										}
									}
									
								}//else (newValue > (ma+(3*dp))){
							}//else (newValue <= ma){
						conclusao += "</font><br><br>";
					}//else (vals.size() <= 1){
				}//}else{ //existe valor
				}//for (String ele: arrEle){
			}// else (!xl.checkDocumento(xl.nomearquivo+".xml")){
			}catch (Exception e) {
			System.out.println("Erro EmpiricicalRuleXBRL:"+e.getMessage());
		}
		System.out.println("Fim da Busca e Processo.");
		System.out.println("Exibição do resultado da consulta pronta!");
		tempFinal = System.currentTimeMillis(); 
		long dif = tempFinal - tempInicial;
		conclusao += "Number of checked element(s): "+j+"<Br>";
		conclusao += "Performance time: "+String.format("%02d second(s)  %02d milliseconds", dif/1000, dif%1000);
		conclusao += "</p>";
		return conclusao;
	}
	
} //it closes EmpiricalRuleXBRL class 
