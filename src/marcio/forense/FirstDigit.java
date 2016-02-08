package marcio.forense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;
import java.math.BigDecimal;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class FirstDigit implements UserDefinedFunction {

		public FirstDigit(){
			
		}
		public double FirstDigit(double valor, double per){
			double x = ((valor*per)/100)+valor;		
			return x;
		}
		public String getDescription() {
			return "Retorna análise de documentos baseados na Lei do Primeiro Dígito";
		}
		public Syntax getSyntax() {
			return Syntax.Function;
		}
		public String getName() {
			String var = "FirstDigit";
			return var;
		}
		public Type getReturnType(Type[] parameterTypes) {
	        //return new BooleanType();
			return new StringType();			
	    }
		public Type[] getParameterTypes() {
			//ano do documento e nome do documento
			//return new Type[] {new SetType(MemberType.Unknown), new StringType(), new SetType(MemberType.Unknown), new StringType()};
			return new Type[] {new SetType(MemberType.Unknown), new SetType(MemberType.Unknown), new SetType(MemberType.Unknown), new StringType()};
			
	        /*
	         Os tipos nativos com suporte no servidor OLAP Mondrian incluem: 
				• BooleanType – representa expressões do tipo booleano; 
				• CubeType – representa um cubo ou um cubo virtual; 
				• NumericType – representa tipos numéricos; 
				• DimensionType – representa uma dimensão; 
				• LevelType – representa um nível; 
				• MemberType – representa um membro; 
				• SetType – representa conjuntos; e 
				• StringType – representa uma String
			*/
	    }
		public String[] getReservedWords() {
			return null;
		}
		public Object execute(Evaluator eva, Argument[] arg) {//(entidade,ano,documento,tiporetorno) 28/10/2013
				long tempInicial = System.currentTimeMillis();
				long tempFinal = 0;
				List<Member> 	param0 = (List<Member>) arg[0].evaluateScalar(eva); // Empresa(s)
				List<Member> 	param1 = (List<Member>) arg[1].evaluateScalar(eva); // Data(s)
				List<Member> 	param2 = (List<Member>) arg[2].evaluateScalar(eva); // Documento(s)
				Object 			param3 = arg[3].evaluateScalar(eva); //flag de retorno
							
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
					for (int i=0; i<tokens.length;i++)
						System.out.println("Tokens de datas:"+tokens[i]);
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
						//System.out.println("Ano:"+tempT.getAno()+", Mes:"+tempT.getMes()+", Dia:"+tempT.getDia()+", Id:"+tempT.getId());
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
					DocumentoDAO tempDoc = doc.getDocumentoDAOByNome(arrDoc.get(0),nomeDoc);
					if (tempDoc.getNome() != null){
						//System.out.println("Documento(s): "+tempDoc.getNome());
						docs.add(tempDoc);
					}
				}
				
				//doc = doc.getDocumentoDAOByNome(((String) param2).toString());
				//System.out.println("Documento encontrado, id:"+doc.getId()+", nome:"+doc.getNome());
							
				// ===================================== Carregando Relatórios...28/10/2013
				//Ver todos os documentos das Entidades...
				ArrayList<ArrayList<RelatorioDAO>> rels = new ArrayList<ArrayList<RelatorioDAO>>(); 
				ArrayList<RelatorioDAO> r = new ArrayList<RelatorioDAO>();
				for (EntidadeDAO e: ents){
					for (DocumentoDAO d: docs){
						for (TempoDAO t: tems){
							//cria um arrayList com as sequencias Ent+Doc registrados e só passa qd não existir no arrayList.
							//if(!arrtempData.contains(tempData)){
								//System.out.println("rel.getRelByETD("+e.getId()+","+t.getId()+","+d.getId()+")");
								RelatorioDAO rel = new RelatorioDAO();
								r = rel.getRelByEntTemDoc(e.getId(),t.getId(),d.getId());
								rels.add(r); // em caso de vários documentos (continuar raciocinio)
						}
					}
				}			
				
				ArrayList<ElementoDAO> eles = new ArrayList<ElementoDAO>();
				int a=0,b=0,c=0,d=0,e1=0,f=0,g=0,h=0,x=0;
				int totaldigitos = 0;
				//System.out.println("Qt de arrays de relatorios inteiros: "+rels.size());
				
				// ===================================== Carregar valores, para aplicar a Lei do Primeiro Digito
				ArrayList<BigDecimal> valores = new ArrayList<BigDecimal>();
				for(ArrayList<RelatorioDAO> rel2: rels){
					for (RelatorioDAO r2: rel2){
						//puxar os nomes dos elementos (nesse caso não há necessidade)
						ElementoDAO ele = new ElementoDAO();
						ele.getElementoDAOById(r2.getId());
						eles.add(ele);
						
						//aplicar a lei de benford nos valores contido nestes relatórios
						BigDecimal valorBig = new BigDecimal("99999999999.99");
						DecimalFormat df = new DecimalFormat("###,###,##0.##");
						valorBig = BigDecimal.valueOf(r2.getValor());
						//System.out.println("Valor big Decimal:"+df.format(valorBig));
						String texto = df.format(valorBig);
						valores.add(valorBig);
						EntidadeDAO e = new EntidadeDAO();
						//System.out.println("Valor do documento ("+e.getEntidadeById(r2.getEntidade_id()).getNome()+"):"+texto);
						texto = texto.replace(".","");	
						texto = texto.replace(",", "");
						texto = texto.replace("-", "");
						//System.out.println("Texto do valor: "+texto);
						long valor = Long.parseLong(texto);
						//System.out.println("foi");
						for (int i=0;i<texto.length();i++){
							int digito = Integer.parseInt(String.valueOf(texto.charAt(i)));
							//System.out.println("Digito capturado: "+digito+". ");
							switch(digito){
								case 1: a++;totaldigitos++; break;
								case 2: b++;totaldigitos++; break;
								case 3: c++;totaldigitos++; break;
								case 4: d++;totaldigitos++; break;
								case 5: e1++;totaldigitos++; break;
								case 6: f++;totaldigitos++; break;
								case 7: g++;totaldigitos++; break;
								case 8: h++;totaldigitos++; break;
								case 9: x++;totaldigitos++; break;
							}
							
						} //for (int i=0;i<texto.length();i++){
					} //for (RelatorioDAO r2: rel2){
				} //for(ArrayList<RelatorioDAO> rel2: rels){
				/*
				System.out.println("Total de Digitos no documento: "+totaldigitos);
				System.out.println(". Total de 1: "+a);
				System.out.println(". Total de 2: "+b);
				System.out.println(". Total de 3: "+c);
				System.out.println(". Total de 4: "+d);
				System.out.println(". Total de 5: "+e1);
				System.out.println(". Total de 6: "+f);
				System.out.println(". Total de 7: "+g);
				System.out.println(". Total de 8: "+h);
				System.out.println(". Total de 9: "+x);
				*/
				//Aplicar teste Z:
					//nível de significancia aceitável: 0.05
					//z crítico: 1,96
				ZTest z = new ZTest();
				// PE - Benford: 
				if (totaldigitos == 0){
					return " Não foi encontrado qualquer relatório baseado nos parâmetros informados.";
				}
				double dig1 = (a * 100)/totaldigitos;
				double dig2 = (b * 100)/totaldigitos;
				double dig3 = (c * 100)/totaldigitos;
				double dig4 = (d * 100)/totaldigitos;
				double dig5 = (e1 * 100)/totaldigitos;
				double dig6 = (f * 100)/totaldigitos;
				double dig7 = (g * 100)/totaldigitos;
				double dig8 = (h * 100)/totaldigitos;
				double dig9 = (x * 100)/totaldigitos;
				
				double zcritico = (double) 1.96; // valor crítico para Hipótese nula po=pe.
				/*double significancia = Double.parseDouble("0,05"); //nivel de significancia aceitável
				System.out.println("Z critico: "+zcritico);
				System.out.println("Digito 1: "+dig1);
				System.out.println("Digito 2: "+dig2);
				System.out.println("Digito 3: "+dig3);
				System.out.println("Digito 4: "+dig4);
				System.out.println("Digito 5: "+dig5);
				*/
				
				//1- 30,10%, 2- 17,61%, 3- 12,49%, 4- 9,69%, 5- 7,92%, 6- 6,69%, 7- 5,80%, 8- 5,11%, 9- 4,58%
				
				int result = 0;
				if (dig1 > 30.10){
					result++;
				}else{
					if (dig2 > 17.61){
						result++;
					}else{
						if (dig3 > 12.49){
							result++;
						}else{
							if (dig4 > 9.69){
								result++;
							}else{
								if (dig5 > 7.92){
									result++;
								}else{
									if (dig6 > 6.69){
										result++;
									}else{
										if (dig7 > 5.80){
											result++;
										}else{
											if (dig8 > 5.11){
												result++;
											}else{
												if (dig9 > 4.58){
													result++;
												}
											}
										}
									}
								}
							}
						}
					}
				}
				ArrayList arrDigit = new ArrayList();
				arrDigit.add(dig1);
				arrDigit.add(dig2);
				arrDigit.add(dig3);
				arrDigit.add(dig4);
				arrDigit.add(dig5);
				arrDigit.add(dig6);
				arrDigit.add(dig7);
				arrDigit.add(dig8);
				arrDigit.add(dig9);
				
				// ============================================================  
				// ============================================================  Retorno dos dados analisados !!!!!
				// ============================================================
				
				DecimalFormat fmt = new DecimalFormat("#,#00.00");
				
				if (String.valueOf(param3).equals("serialized")){
					conclusao += fmt.format(dig1)+";"+fmt.format(dig2)+";"+fmt.format(dig3)+";"+fmt.format(dig4)+";"+fmt.format(dig5)+";"+fmt.format(dig6)+";"+fmt.format(dig7)+";"+fmt.format(dig8)+";"+fmt.format(dig9);
					//conclusao += "21,00;15,00;10,00;08,00;09,00;06,00;09,00;10,00;08,00";
				}else{
					conclusao+= "<STYLE TYPE='text/css'>"+
							"TD{font-family: Arial; font-size: 9pt;}"+
							"</STYLE>";
					conclusao += "<table style='text-align:right;'><font face='verdana' size='3'>";
					if (result == 0){
						conclusao+="<tr><td colspan='2'>";
						// conclusao += "Documento: "+doc.getNome()+" da Empresa: "+ent.getNome()+" <br>";
						conclusao += "<font color='blue'>Conformity with First Digits Law.</font><br>";
						conclusao += "</td></tr>";
						conclusao+="<tr><td>Expected Percentage<Br>";
						conclusao+="Digit 1:  30,10% <BR>Digit 2:  17,61%<BR>Digit 3:  12,49%<BR>Digit 4:  09,69%<BR>Digit 5:  07,92%<BR>Digit 6:  06,69%<BR>Digit 7:  05,80%<BR>Digit 8:  05,11%<BR>Digit 9:  04,58%</td>";
						conclusao += "<td>Found Percentage<Br> ";
						
						int i = 1;
						
						for (Object d1: arrDigit){
							//conclusao += "Digit "+i+": "+fmt.format(Double.parseDouble(String.valueOf(d1)))+"%<Br>";
							conclusao += fmt.format(Double.parseDouble(String.valueOf(d1)))+"%<Br>";
							i++;
						}
						
						//conclusao += "21,00%<Br>15,00%<Br>10,00%<Br>08,00%<Br>09,00%<Br>06,00%<Br>09,00%<Br>10,00%<Br>08,00 %<Br>";
						conclusao += "</td>";
						conclusao += "<tr><td colspan='2'>";
						//conclusao += "<Br>Financial Values of the Document:<Br>";
						/* impressão de todos os valores do documento
						for (BigDecimal v:valores){
							conclusao += fmt.format(v)+"<Br>";
						}
						*/
					}else{
						conclusao+="<tr><td colspan='2'>";
						//conclusao += "Documento: "+doc.getNome()+" da Empresa: "+ent.getNome()+" <br>";
						conclusao += "<font color='red'>Non-Conformity with First Digits Law.</font> <br>";
						conclusao += "</td></tr>";
						conclusao+="<tr><td>Expected Percentage<Br>";
						conclusao+="Digit 1: 30,10% <BR>Digit 2: 17,61%<BR>Digit 3: 12,49%<BR>Digit 4: 09,69%<BR>Digit 5: 07,92%<BR>Digit 6: 06,69%<BR>Digit 7: 05,80%<BR>Digit 8: 05,11%<BR>Digit 9: 04,58%</td>";
						conclusao += "<td>Found Percentage<Br> ";
						
						int i = 1;
						
						for (Object d1: arrDigit){
							//conclusao += "Digit "+i+": "+fmt.format(Double.parseDouble(String.valueOf(d1)))+"%<Br>";
							conclusao += fmt.format(Double.parseDouble(String.valueOf(d1)))+"%<Br>";
							i++;
						}
						
						// - impressao conclusao += "21,00%<Br>15,00%<Br>10,00%<Br>08,00%<Br>09,00%<Br>06,00%<Br>09,00%<Br>10,00%<Br>08,00%<Br>";
						conclusao += "</td>";
						conclusao += "<tr><td colspan='2'>";
						/* impressão de todos os valores do documento
						conclusao += "<Br>Financial Values of the Document:<Br>";
						for (BigDecimal v:valores){
							conclusao += fmt.format(v)+"<Br>";
						}	
						*/
					}
					tempFinal = System.currentTimeMillis(); 
					//totaldigitos = 853;
					conclusao += "<br>Total number of digits:"+totaldigitos+"<br>";
					long dif = tempFinal - tempInicial;
					conclusao += "Performance time: "+String.format("%02d second(s)  %02d milliseconds", dif/1000, dif%1000);
					conclusao += "</td></tr></font></table>";
				}
			return conclusao;
		}// execute
}
		

