package mondrian.udf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class Separatrix implements UserDefinedFunction{
	
	private List<Double> valoresPadrao;
	
	private static int quantidadeValoresPadrao = 0;
	
	
	
	public List<Double> getValoresPadrao() {
		return valoresPadrao;
	}


	public void setValoresPadrao(List<Double> valoresPadrao) {
		this.valoresPadrao = valoresPadrao;
	}

	

	public Object execute(Evaluator evaluator, Argument[] arguments) {
		 	
		//List<Member> memberList = (List<Member>) arguments[0].evaluate(evaluator);	
		//List<Member> valores = (List<Member>) arguments[1].evaluate(evaluator);	
		
		// Ajustando para MemberSet, Member, IntegerExpression (estava MemberSet, MemberSet, IntegerExpression) #Paulo Caetano
		
		List<Member> valores = (List<Member>) arguments[0].evaluate(evaluator);	
		Member membro = (Member) arguments[1].evaluate(evaluator);	
		
		Object quantidadeValoresPadraoObject = arguments[2].evaluateScalar(evaluator);
		//Integer membro = new Integer(((Number) quantidadeValoresPadraoObject).intValue());
		// Subtraindo 1, pois o n�mero informado deve ser o n�mero de intervalos, n�o o n�mero de separatrizes, como estava antes. #Paulo Caetano
		quantidadeValoresPadrao = new Integer(((Number) quantidadeValoresPadraoObject).intValue()-1);
				
		
		Object[] retorno = new Object[valores.size()+1];
		retorno[0] = membro;
				
		int i = 1;		
		
		for (Member member : valores) {
			retorno[i] = member;
			i++;
		}
		
		return Arrays.asList(retorno);	
	}
	
	
	
	public static List<Double> getPercentis(List<Double> valores) {
		// Reescrito por Paulo Caetano
		List<Double> percentis = new ArrayList<Double>();
		
		valores.remove(0);
		Double[] arrayDouble = valores.toArray(new Double[valores.size()]);
		
		Arrays.sort(arrayDouble);
		
		double min_valor = Math.floor(arrayDouble[0]);
		double max_valor = Math.ceil(arrayDouble[arrayDouble.length-1]);
		// determinando ordem de grandeza
		int ord = String.valueOf((int)max_valor).trim().length(); 
		if(String.valueOf((int)Math.abs(min_valor)).trim().length()>ord)
			ord = String.valueOf((int)Math.abs(min_valor)).trim().length();
		//
	    ArrayList<Double> xiList = new ArrayList<Double>();
	    // Construindo classes Xi, com o intervalo de acordo com a ordem de grandeza
	    // fica um para cada n�mero
		for(double x=min_valor;x<max_valor;x+=Math.pow(10, ord-5)){
			xiList.add(x);
		}
		xiList.add(max_valor); // colocando teto
		// definindo fi e fac
		Double[] xi = xiList.toArray(new Double[xiList.size()]);
		Double[] fi = new Double[xi.length];
		Double[] fac = new Double[xi.length];
		double n=0;
		for(int acum=0,k=0,i=0;i<=arrayDouble.length;i++){
			while(i==arrayDouble.length || arrayDouble[i]>xi[k]){
				n += acum;
				fi[k]= (double)acum;
				fac[k++]= n;
				acum=0; 
				if(i==arrayDouble.length) break;
			}
			acum++;
		}
		// obtendo os percentis
		double percentil = 0.0;
		for(int i=0;i<quantidadeValoresPadrao;i++){
			double valComp = (n * (i+1)) / (quantidadeValoresPadrao+1);
			for(int k=0;k<xi.length;k++){
				if(fac[k]>=valComp){
					percentil = (k==0?0:xi[k-1]) + ((valComp - (k==0?0:fac[k-1]))/fi[k]) * (xi[k]-(k==0?0:xi[k-1]));
					percentis.add(percentil);
					break;
				}
			}
		}
				
		return percentis;		
	}

	public static List<Double> getPercentis_OLD(List<Double> valores) {
		List<Double> percentis = new ArrayList<Double>();
		
		valores.remove(0);
		int quantidadeDeEntradas = valores.size();
		
		Double[] arrayDouble = new Double[quantidadeDeEntradas];
		arrayDouble = valores.toArray(arrayDouble);
		
		Arrays.sort(arrayDouble);		
		
		double percentil = 0.0;
		int elemento1 = 0;
		for(int i = 0; i < quantidadeValoresPadrao; i++) {
			
			if(i == 0) {
				elemento1 = quantidadeDeEntradas / (quantidadeValoresPadrao + 1);
			}else {
				elemento1 = elemento1 * 2;
			}
			
						
			if(elemento1 < arrayDouble.length) {
				percentil = (arrayDouble[elemento1 - 1] + arrayDouble[elemento1]) / 2;
			}
			
			percentis.add(percentil);
		}
				
		return percentis;		
	}
	
	public static int posicaoRelativa(Double valor, List<Double> indicesPadrao) {
		Integer posicao = null;
		
		for(int i = 0; i < indicesPadrao.size(); i++) {
			double valorAtual = indicesPadrao.get(i);
			
			if(valor <= valorAtual) {
				posicao = i;
				break;				
			}
		}
		
		if(posicao == null) {
			posicao = indicesPadrao.size(); 
		}
				
		return posicao;
	}
	
	

	public String getDescription() {		
		return null;
	}

	
	public String getName() {
		String nome = "Separatrix";
		return nome;
	}

	  public Type[] getParameterTypes() {
	    	System.out.println("2");
	        return new Type[] { new SetType(MemberType.Unknown), MemberType.Unknown, new NumericType()};
	    }


	public String[] getReservedWords() {	
		return null;
	}

	  public Type getReturnType(Type[] parameterTypes) {
	    	System.out.println("4");
	        return new SetType(MemberType.Unknown);
	    }

	

	 public Syntax getSyntax() {
	        return Syntax.Function;
	    }

}
