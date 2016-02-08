package marcio.forense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Object;
import java.math.BigDecimal;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.BooleanType;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class FirstDigitDois implements UserDefinedFunction {
	public ArrayList<BigDecimal> valores = new ArrayList<BigDecimal>();
		public FirstDigitDois(){
			
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
			return "FirstDigit";
		}
		public Type getReturnType(Type[] parameterTypes) {
	        //return new BooleanType();
			return new NumericType();			
	    }
		public Type[] getParameterTypes() {
			//ano do documento e nome do documento
			//return new Type[] {new SetType(MemberType.Unknown), new StringType(), new SetType(MemberType.Unknown), new StringType()};
			//return new Type[] {new SetType(MemberType.Unknown), new StringType()};
			return new Type[] {MemberType.Unknown};
			
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
			/*
			List<Member>	param = (List<Member>) arg[0].evaluateScalar(eva); // valores
			Object			flag = arg[1].evaluateScalar(eva); //flag de retorno
			
			for (int i=0;i<param.size();i++){
				param.get(i).getMemberType();
				valores = BigDecimal.valueOf(param.get(i));
			}
			*/
			Object valor = (Object) arg[1].evaluate(eva);
			BigDecimal auxValor = new BigDecimal(valor.toString());
			valores.add(auxValor);
			
			String result = "";
			
			System.out.println(valores.size());
			
			for (int i=0;i<=valores.size();i++)
				result += valores.get(i).toString()+"-";
			System.out.println("Número de valores captados:"+valores.size());
			return auxValor;
			/*
			//buscar no DW todos os elementos: empresa->documento->daquele ano.
			int a=0,b=0,c=0,d=0,e1=0,f=0,g=0,h=0,x=0;
			int totaldigitos= 0;
			String conclusao = "";
			for (Member valor: valores){
				String texto = String.valueOf(valor);
				for (int i=0;i<texto.length();i++){
					int digito = Integer.parseInt(String.valueOf(texto.charAt(i)));
					System.out.println("Digito capturado: "+digito+". ");
					switch(digito){
						case 1: a++; break;
						case 2: b++; break;
						case 3: c++; break;
						case 4: d++; break;
						case 5: e1++; break;
						case 6: f++; break;
						case 7: g++; break;
						case 8: h++; break;
						case 9: x++; break;
					}
					totaldigitos++;
				} //for (int i=0;i<texto.length();i++){
			}
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
				/*
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
				
				if (String.valueOf(flag).equals("serialized")){
					conclusao += fmt.format(dig1)+";"+fmt.format(dig2)+";"+fmt.format(dig3)+";"+fmt.format(dig4)+";"+fmt.format(dig5)+";"+fmt.format(dig6)+";"+fmt.format(dig7)+";"+fmt.format(dig8)+";"+fmt.format(dig9);
				}else{
					if (result == 0){
						// conclusao += "Documento: "+doc.getNome()+" da Empresa: "+ent.getNome()+" <br>";
						conclusao += "<color=\"blue\"> Corresponde a Lei do Primeiro Dígito.";
						conclusao += "</color> <br><br>Percentual encontrado: <Br> ";
						
						int i = 1;
						for (Object d1: arrDigit){
							conclusao += "Dígito"+i+""+fmt.format(Double.parseDouble(String.valueOf(d1)));
							i++;
						}
						//for (double v:valores){
						//	conclusao += fmt.format(v)+"<Br>";
						//}
	
					}else{
						//conclusao += "Documento: "+doc.getNome()+" da Empresa: "+ent.getNome()+" <br>";
						conclusao += "Dados passivos de erros/fraudes. </color> <br>";
						conclusao += "<br>Percentual encontrado: <Br> ";
						
						int i = 1;
						for (Object d1: arrDigit){
							conclusao += "Dígito"+i+": "+fmt.format(Double.parseDouble(String.valueOf(d1)))+"<Br>";
							i++;
						}
						conclusao += "<Br>Valores do Documento:<Br>";
						//for (double v:valores){
						//	conclusao += fmt.format(v)+"<Br>";
						//}	
					}
					conclusao += "<br>Total dígitos:"+totaldigitos;
				}

			return conclusao;
			*/
		}// execute
}
		




