package marcio.forense;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mondrian.olap.Evaluator;
import mondrian.olap.Syntax;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class ChiSquaredTest implements UserDefinedFunction  {
	public ChiSquaredTest (){
		
	}
	public double ChiQuadrado(double po, double pe){
		return (Math.pow(po, 2) - (2*po*pe) + Math.pow(pe, 2))/pe;
	}

	public String getDescription() {
		return "Retorna análise probabilistica baseada no Teste QuiQuadrado (x^2)";
	}

	public String getName() {
		return "ChiSquaredTest";
	}

	public Type[] getParameterTypes() {
		return new Type[] {new StringType(), new StringType(), new NumericType()};
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
		// embasamento teórico: http://www.ufpa.br/dicas/biome/bioqui.htm
		long tempInicial = System.currentTimeMillis();
		final Object param1 = arg1[0].evaluateScalar(arg0); //probabilidade esperada - serializado
		final Object param2 = arg1[1].evaluateScalar(arg0); //probabilidade observada- serializado
		final Object param3 = arg1[2].evaluateScalar(arg0); //ChiCrítico
				
		System.out.println("Entrou chicritico em PE: "+String.valueOf(param1));
		ArrayList<Double> pe = new ArrayList<Double>();		
		String valoresPe = String.valueOf(param1);
		String[] arrPe = valoresPe.split(";");
		for (int i=0;i<=(arrPe.length - 1);i++){
			pe.add(Double.parseDouble(String.valueOf(arrPe[i]).replace(",", ".")));
		}
		System.out.println(pe.toString());
		
		System.out.println("Entrou chicritico em PO: "+String.valueOf(param2));
		ArrayList<Double> po = new ArrayList<Double>();
		String valoresPo = String.valueOf(param2);
		if (valoresPo.equalsIgnoreCase("Não foi encontrado qualquer relatório baseado nos parâmetros informados.")){
			return "A função utilizada não retornou qualquer relatório";
		}
		String[] arrPo = valoresPo.split(";");
		for (int i=0;i<=(arrPo.length - 1);i++){
			po.add(Double.parseDouble(String.valueOf(arrPo[i]).replace(",", ".")));
		}
		System.out.println(po.toString());
		
		if(pe.size() != po.size()){
			return "Quantidade de dados enviados incompatível";
		}else{
			System.out.println("Entrou em ChiTest3");
			ArrayList<Double> list = new ArrayList<Double>();
			for (int j=0;j<=pe.size()-1;j++){
				list.add(this.ChiQuadrado(po.get(j), pe.get(j)));
			}
				
			System.out.println("Entrou em chicritico4");
			String resultado = "";
			double chicritico= Double.parseDouble(String.valueOf(param3));
			int i=0, j=0;
			DecimalFormat fmt = new DecimalFormat("#,#00.00");
			for (Object lista: list){
				j++;
				if ( (Double.parseDouble(String.valueOf(lista)) > chicritico )){
					i++;
				}
				resultado += "Digit "+j+": "+fmt.format(Double.parseDouble(lista.toString()))+"<Br>"; 
			}
			//}else{
			if (i > 0){
				resultado += "<br><font color='red'>H0 Analisys is invalid.<BR> Checked values are not consistent with the expected values (15,507) </font>."; 
			}else{
				resultado += "<br><font color='blue'>H0 Analisys is valid. <BR> Checked values are consistent with the expected values (15,507)</font>.";
			}
			long tempFinal = System.currentTimeMillis();
			long dif = tempFinal - tempInicial;
			resultado += "<br>Performance time: "+String.format("%02d second(s)  %02d milliseconds", dif/1000, dif%1000);
			return resultado;
		/*
		double x = this.ChiQuadrado(po, pe);
		 
		double variancia = 0.05;
		double xcritico = 15.507;
		String result = "";
		
		result += String.valueOf(x);
		if (x <= xcritico){
			result += ": "+true;
		}else{
			result += ": "+false;
		}
		*/
		}
	} //execute
	
}// class
