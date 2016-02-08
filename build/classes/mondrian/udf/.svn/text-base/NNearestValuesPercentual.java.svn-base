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
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class NNearestValuesPercentual implements UserDefinedFunction {
	
	private static double percNearestMembers;
	private static String refNearestMembers;

	public Object execute(Evaluator evaluator, Argument[] arguments) {

		List<Member> valores = (List<Member>) arguments[0].evaluate(evaluator);	
		Member membro = (Member) arguments[1].evaluate(evaluator);	
		percNearestMembers = new Double(((Number) arguments[2].evaluateScalar(evaluator)).intValue());
		refNearestMembers = new String((String) arguments[3].evaluateScalar(evaluator));

		Object[] retorno = new Object[valores.size()+1];
		retorno[0] = membro;
				
		int i = 1;		
		for (Member member : valores) {
			retorno[i] = member;
			i++;
		}
		
		return Arrays.asList(retorno);	
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return "NNearestValuesPercentual";
	}

	public Type[] getParameterTypes() {
        return new Type[] { new SetType(MemberType.Unknown),MemberType.Unknown, new NumericType(), new StringType()};
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
	
	public static List<Double> getNNearestValuesPercentual(List<Double> valores){
		
		Double valorRef = valores.get(0);
		valores.remove(0); // já existe um valorRef lá dentro
		Double[] arrayValores = valores.toArray(new Double[valores.size()]);
		Arrays.sort(arrayValores);
		
		int pos = Arrays.binarySearch(arrayValores,valorRef); // valorRef faz parte do array de valores (é o elemento 0)
		if(pos<0) 
			throw new IllegalArgumentException("Value "+pos+" not found in MemberSet");
		
		ArrayList<Double> valProximos = new ArrayList<Double>();
		
		if(refNearestMembers.equalsIgnoreCase("ASC")){ // os acimas
			for(int i=pos+1; i<arrayValores.length; i++)
				if(arrayValores[i]>valorRef && arrayValores[i]<=valorRef*(1+percNearestMembers/100.)) // até o percentual solicitado
					valProximos.add(arrayValores[i]);
		} else if(refNearestMembers.equalsIgnoreCase("DESC")){ // os abaixos
			for(int i=0; i<pos; i++){
				if(arrayValores[i]<valorRef && arrayValores[i]>=valorRef*(1-percNearestMembers/100.)) // até o percentual solicitado
					valProximos.add(arrayValores[i]);
			}
		} else { // apenas os mais próximos - diferença absoluta de percentuais
			ArrayList<Double> valDif = new ArrayList<Double>();
			for(int i=0; i<arrayValores.length; i++){
				double percAbs = Math.abs(1 - arrayValores[i]/valorRef)*100.;
				if(percAbs<=percNearestMembers && percAbs!=0) // só coloca os que forem até o percentual desejado, exceto se for o próprio valorRef (percAbs==0)
					valProximos.add(arrayValores[i]);
			}
		}
		return valProximos;
		
	}

}
