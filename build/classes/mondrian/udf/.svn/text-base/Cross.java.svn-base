package mondrian.udf;

import java.util.ArrayList;
import java.util.Arrays;

import mondrian.lmdql.Parameters;
import mondrian.lmdql.XLink;
import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class Cross implements UserDefinedFunction {
	
	public static String linkbase;

	public Object execute(Evaluator evaluator, Argument[] arguments) {
		
		Member[] m = evaluator.getMembers();

		String membro = new String((String) arguments[0].evaluateScalar(evaluator));
		linkbase = new String((String) arguments[1].evaluateScalar(evaluator));
		
		ArrayList<String> membros = new ArrayList<String>();
		String[] nomes = XLink.getBracketNames(membro);
		for(int i=0;i<nomes.length;i++){
			if(Parameters.xmlMdxFileContent.indexOf("["+nomes[i]+"]")==-1) {// não achou, vai tentar localizar equivalente na taxonomia de label
				String[] similar = XLink.getMatchLabel(nomes[i]); 
				for(int k=0;k<similar.length;k++){
					String newMembro = membro.replace("["+nomes[i]+"]", "["+similar[k]+"]"); // troca todos
					newMembro = newMembro.replace("[All "+nomes[i]+"]", "[All "+similar[k]+"]"); // troca todos
					membros.add(newMembro);
				}
			}
		}
		
		Object[] retorno = new Object[membros.size()+1];
		retorno[0] = membro;
		
		int i = 1;		
		for (String member : membros) {
			retorno[i] = member;
			i++;
		}
		
		return Arrays.asList(retorno);	
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return "Cross";
	}

	public Type[] getParameterTypes() {
		return new Type[] { MemberType.Unknown, new StringType()};
	}

	public String[] getReservedWords() {
		return null;
	}

	public Type getReturnType(Type[] parameterTypes) {
        return new SetType(MemberType.Unknown);
	}

	public Syntax getSyntax() {
        return Syntax.Function;
	}

}
