package mondrian.udf;

import java.util.Arrays;
import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class VAnalysis implements UserDefinedFunction{

	public Object execute(Evaluator evaluator, Argument[] arguments) {
		 	
		List<Member> memberList = (List<Member>) arguments[1].evaluate(evaluator);
		
		 Object[] retorno = new Object[memberList.size() + 1];
			
			Object arg0 = arguments[0].evaluate(evaluator);
			Member membro = (Member)arg0;  
			retorno[0] = membro;
		
		if(arguments.length > 1) {
			
			int i = 1;
			for(Member member : memberList) {
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
		String nome = "VAnalysis";
		return nome;
	}

	  public Type[] getParameterTypes() {
	    	System.out.println("2");
	        return new Type[] { MemberType.Unknown, new SetType(MemberType.Unknown)};
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
