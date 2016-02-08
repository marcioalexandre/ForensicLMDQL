package marcio.forense;

import java.util.ArrayList;

import mondrian.olap.Evaluator;
import mondrian.olap.Syntax;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;
import mondrian.spi.UserDefinedFunction.Argument;

public class Empresa implements UserDefinedFunction {

	public String getDescription() {
		return "Retorna nome da empresa";
	}

	public String getName() {
		return "Empresa";
	}

	public Type[] getParameterTypes() {
		//nome do documento, nome do elemento 
		//(os anos não se tornam interessantes pq na análise já vem todos)
		return new Type[] {new StringType()};
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

	public Object execute(Evaluator arg0, Argument[] arg1) {
		final Object param1 = arg1[0].evaluateScalar(arg0); //exmeplo
		System.out.println("O que tem dentro:"+param1.toString());
		return param1;
	} //execute
}
