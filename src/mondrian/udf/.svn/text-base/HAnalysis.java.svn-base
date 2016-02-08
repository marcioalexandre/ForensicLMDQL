package mondrian.udf;

import java.util.Arrays;
import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class HAnalysis implements UserDefinedFunction{
	
	public static Double[] indicesCorrecao; 
	
	
	public Object execute(Evaluator evaluator, Argument[] arguments) {
		
		List<Member> membersAvaliados = (List<Member>) arguments[1].evaluate(evaluator);
		
		String indices = new String((String) arguments[2].evaluateScalar(evaluator));
		if(indices.trim().length()==0)
			this.indicesCorrecao = null;
		else {
			String[] indiceStr = indices.split(",");
			this.indicesCorrecao = new Double[indiceStr.length];
			for(int i=0;i<indiceStr.length;i++){
				this.indicesCorrecao[i] = Double.parseDouble(indiceStr[i]);
			}
		}
		
		Object[] retorno = new Object[membersAvaliados.size()];
		
		/*Object arg0 = arguments[0].evaluate(evaluator);
		Member membro = (Member)arg0;  
		retorno[0] = membro;*/
	 			
		
		
		/*Object quantidadeValoresPadraoObject = arguments[2].evaluateScalar(evaluator);
		Integer indiceCorretivoInteger = new Integer(((Number) quantidadeValoresPadraoObject).intValue());
		indiceCorretivo = indiceCorretivoInteger;*/
		
		
		if(arguments.length > 1) {
			
			int i = 0;
			for(Member member : membersAvaliados) {
				 retorno[i] = member;
				 i++;
			}
			
		}
		
		return Arrays.asList(retorno);
		
	}

	

	public String getDescription() {		
		return null;
	}

	
	public String getName() {
		String nome = "HAnalysis";
		return nome;
	}

	  public Type[] getParameterTypes() {
	        return new Type[] { MemberType.Unknown, new SetType(MemberType.Unknown), new StringType() };
	    }


	public String[] getReservedWords() {	
		return null;
	}

	  public Type getReturnType(Type[] parameterTypes) {
	    	//System.out.println("4");
	        return new SetType(MemberType.Unknown);
	    }

	

	 public Syntax getSyntax() {
	        return Syntax.Function;
	    }

}
