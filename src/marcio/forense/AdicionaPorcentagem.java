package marcio.forense;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.Syntax;
import mondrian.olap.type.LevelType;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class AdicionaPorcentagem implements UserDefinedFunction {
	ArrayList<BigDecimal> valores = new ArrayList<BigDecimal>();
	public AdicionaPorcentagem (){
		
	}
	public double AdicionaPorcentagem(double valor, double per){
		double x = ((valor*per)/100)+valor;		
		return x;
	}
	public String getDescription() {
		return "Retorna o valor add da porcentagem";
	}
	public Syntax getSyntax() {
		return Syntax.Function;
	}
	public String getName() {
		return "AdicionaPorcentagem";
	}
	public Type getReturnType(Type[] parameterTypes) {
        return new StringType();
    }
	public Type[] getParameterTypes() {
        //return new Type[] {MemberType.Unknown, new NumericType()};
		//return new Type[] {MemberType.Unknown};
		return new Type[] { new StringType()};
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
	public Object execute(Evaluator eva, Argument[] arg) {//(Evaluator evaluator, Exp[] arguments)
		final Object pvalor =  arg[0].evaluateScalar(eva);
		//final Object per = arg[1].evaluateScalar(eva);
		
		System.out.println("Valor: "+pvalor.toString());
		return pvalor;
		/*
		BigDecimal auxValor = null;
		String texto = "";
		if (pvalor == null || pvalor.equals(null) || pvalor.equals("") || pvalor.equals(0.0)){
			//valores.add(new BigDecimal(String.valueOf(0.0)));
		}else{
			auxValor = new BigDecimal(String.valueOf(pvalor));
			valores.add(auxValor);	
		}
		DecimalFormat df = new DecimalFormat("###,###,##0.##");
		for(BigDecimal v : valores){
			System.out.println("valor: "+v);
			texto += df.format(v)+";";
		}
		
		int a=0,b=0,c=0,d=0,e1=0,f=0,g=0,h=0,x=0,totaldigitos = 0;
		texto = texto.replace(".","");	
		texto = texto.replace(",", "");
		texto = texto.replace(";", "");
		texto = texto.replace("-", "");
		//System.out.println("Texto do valor: "+texto);
		//long valor = Long.parseLong(texto);
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
	
	double zcritico = (double) 1.96;
	
	DecimalFormat fmt = new DecimalFormat("#,#00.00");
	
	String conclusao = fmt.format(dig1)+";"+fmt.format(dig2)+";"+fmt.format(dig3)+";"+fmt.format(dig4)+";"+fmt.format(dig5)+";"+fmt.format(dig6)+";"+fmt.format(dig7)+";"+fmt.format(dig8)+";"+fmt.format(dig9);
			
	//System.out.println("Número de valores captados:"+valores.size());
	return conclusao;

		/* original - calculo da porcentagem
		//System.out.println("Primeiro: "+String.valueOf(valor));
		//System.out.println("Segundo: "+per.toString());
		
		if (valor == null || valor.equals(null)){
			valores.add(0.0);
		}else{
			valores.add(Double.parseDouble(String.valueOf(valor)));
		}
		
		if (valor instanceof Number) {
			return this.AdicionaPorcentagem(((Number) valor).doubleValue(), ((Number) per).doubleValue());//((Number) argValue2).doubleValue());
			//return ((Number) argValue).doubleValue() + ((Number) argValue2).doubleValue();
		}else{
			return null;
		}
		*/
		
	}

}
