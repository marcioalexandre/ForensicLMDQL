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
/**
 *
 * @author marcio.alexandre83@gmail.com / paulo.caetano@pro.unifacs.br
 */
public class GriConformity implements UserDefinedFunction{
	public GriConformity(){
		
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Audit Analysis based on GRI rules";
	}


	public String getName() {
		// TODO Auto-generated method stub
		String var = "GriConformity";
		return var;
	}


	public Type[] getParameterTypes() {
		// TODO Auto-generated method stub
		return new Type[] {new SetType(MemberType.Unknown)};
	}

	public String[] getReservedWords() {
		// TODO Auto-generated method stub
		return null;
	}


	public Type getReturnType(Type[] arg0) {
		System.out.println("getReturnType");
		//return new StringType();
		return new SetType(MemberType.Unknown);
	}

	public Syntax getSyntax() {
		// TODO Auto-generated method stub
		return Syntax.Function;
	}
	
	@SuppressWarnings("unchecked")
	public Object execute(Evaluator evaluator, Argument[] arguments) {
	 	
		List<Member> memberList = (List<Member>) arguments[0].evaluate(evaluator);
		
		Object[] retorno = new Object[memberList.size() + 1];
			
		int i = 0;
		for(Member member : memberList) {
			 retorno[i] = member;
			 i++;
		}
		
		return Arrays.asList(retorno);
	}

}
