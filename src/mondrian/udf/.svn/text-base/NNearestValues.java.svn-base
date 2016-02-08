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

public class NNearestValues implements UserDefinedFunction {
	
	private static int numNearestMembers;
	private static String refNearestMembers = null;

	public Object execute(Evaluator evaluator, Argument[] arguments) {

		List<Member> valores = (List<Member>) arguments[0].evaluate(evaluator);	
		Member membro = (Member) arguments[1].evaluate(evaluator);	
		numNearestMembers = new Integer(((Number) arguments[2].evaluateScalar(evaluator)).intValue());
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
		return "NNearestValues";
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
	
	public static List<Double> getNNearestValues(List<Double> valores){
		
		Double valorRef = valores.get(0);
		valores.remove(0); // já existe um valorRef lá dentro
		Double[] arrayValores = valores.toArray(new Double[valores.size()]);
		Arrays.sort(arrayValores);
		
		int pos = Arrays.binarySearch(arrayValores,valorRef); // valorRef faz parte do array de valores (é o elemento 0)
		if(pos<0) 
			throw new IllegalArgumentException("Value "+pos+" not found in MemberSet");
		
		ArrayList<Double> valProximos = new ArrayList<Double>();
		
		if(refNearestMembers.equalsIgnoreCase("ASC")){ // os acimas
			for(int i=pos+1; i<=pos+numNearestMembers && i<arrayValores.length; i++)
				valProximos.add(arrayValores[i]);
		} else if(refNearestMembers.equalsIgnoreCase("DESC")){ // os abaixos
			for(int i=pos-numNearestMembers; i<pos; i++){
				i = i<0?0:i;
				valProximos.add(arrayValores[i]);
			}
		} else { // apenas os mais próximos - diferença absoluta
			ArrayList<Double> valDif = new ArrayList<Double>();
			for(int i=pos-numNearestMembers; i<=pos+numNearestMembers && i<arrayValores.length; i++){
				i = i<0?0:i;
				valDif.add(Math.abs(arrayValores[i]-valorRef));
			}
			Double[] arrayValDif = valDif.toArray(new Double[valDif.size()]);
			Arrays.sort(arrayValDif); // ordenando pela diferença (valor absoluto)
			for(int k=1;k<=numNearestMembers && k<arrayValDif.length;k++){ // não conta o 0 - o próprio valorRef
				for(int i=pos-numNearestMembers; i<=pos+numNearestMembers && i<arrayValores.length; i++){
					i = i<0?0:i;
					if(i!=pos && Math.abs(arrayValores[i]-valorRef)==arrayValDif[k]) // não coloca o elemento de valorRef na lista (i!=pos)
						valProximos.add(arrayValores[i]);
				}
			}
			// ordenando por valor
			Double[] arrayValProximos = valProximos.toArray(new Double[valProximos.size()]);
			Arrays.sort(arrayValProximos);
			valProximos = new ArrayList<Double>();
			for(Double valor:arrayValProximos)
				valProximos.add(valor);
		}
		return valProximos;
		
	}

}
