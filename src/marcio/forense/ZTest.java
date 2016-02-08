package marcio.forense;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mondrian.olap.Evaluator;
import mondrian.olap.Member.MemberType;
import mondrian.olap.Syntax;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class ZTest implements UserDefinedFunction {
	public ZTest(){
		//embasamento teorico no arquivo "teste_z_para_comparacao_de_medcias_1.ppt" em download.
		
	}
	public double ZTest(double po, double pe, int n){
		try{
			
			System.out.println("Po:"+po+", Pe:"+pe+", N:"+n);
			//DecimalFormat fmt = new DecimalFormat("#,###.00");
			//double abs = Double.parseDouble(fmt.format(Math.abs(po-pe)));
			double diferencapopulacao = po-pe;
			//System.out.println("numerador:"+diferencapopulacao);
			
			double calculo = Math.abs(pe-(pe*pe)/n);
			//System.out.println("denominado:"+calculo);
			
			double sqrt = Math.sqrt(calculo);
			//System.out.println("Rais Sqrt:"+sqrt);
			
			double z = 0;
			if (diferencapopulacao < 1/(2*n)){
				z = diferencapopulacao/sqrt;
			}else{
				z = (diferencapopulacao - (1/(2*n)))/sqrt;
			}		
			System.out.println("Imprimindo resultado:"+z);
			return z;
		}catch(Exception e){
			throw new RuntimeException("Erro: "+e.getMessage());	
		}
	}

	public String getDescription() {
		return "Retorna análise probabilistica baseada no Teste de Z";
	}

	public String getName() {
		String var = "ZTest";
		return var;
	}

	public Type[] getParameterTypes() {
		return new Type[] {new StringType(), new StringType(), new NumericType(), new NumericType() };
	}

	public String[] getReservedWords() {
		return null;
	}

	public Type getReturnType(Type[] arg0) {
		return new StringType();
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

	public Syntax getSyntax() {
		return Syntax.Function;
	}
	public Object execute(Evaluator arg0, Argument[] arg1) {
		// embasamento teórico: http://www.fm.usp.br/dim/testez/index.php
		long tempInicial = System.currentTimeMillis();
		final Object param1 = arg1[0].evaluateScalar(arg0); //probabilidade esperada - serializado
		final Object param2 = arg1[1].evaluateScalar(arg0); //probabilidade observada- serializado
		final Object param3 = arg1[2].evaluateScalar(arg0); //numero de amostras
		final Object param4 = arg1[3].evaluateScalar(arg0); //ZCrítico
		
		if (String.valueOf(param2).equals("Não foi encontrado qualquer relatório baseado nos parâmetros informados.")){
			return "A função utilizada não retornou qualquer relatório";
		}
		
		System.out.println("Entrou em PE: "+String.valueOf(param1));
		ArrayList<Double> pe = new ArrayList<Double>();		
		String valoresPe = String.valueOf(param1);
		String[] arrPe = valoresPe.split(";");
		for (int i=0;i<=(arrPe.length - 1);i++){
			pe.add(Double.parseDouble(String.valueOf(arrPe[i]).replace(",", ".")));
		}
		System.out.println(pe.toString());
		
		System.out.println("Entrou em PO: "+String.valueOf(param2));
		ArrayList<Double> po = new ArrayList<Double>();
		String valoresPo = String.valueOf(param2);
		String[] arrPo = valoresPo.split(";");
		for (int i=0;i<=(arrPo.length - 1);i++){
			po.add(Double.parseDouble(String.valueOf(arrPo[i]).replace(",", ".")));
		}
		System.out.println(po.toString());
		
		//double z=0;
		//if (pe.size() == po.size()){
		System.out.println("TAmanho pe: "+pe.size());
		System.out.println("TAmanho po: "+po.size());
		if(pe.size() != po.size()){
			return "Quantidade de dados enviados incompatível";
		}else{
			System.out.println("Entrou em ZTest3");
			ArrayList<Double> list = new ArrayList<Double>();
			int num = (int) Double.parseDouble(String.valueOf(param3));//Integer.parseInt(param3.toString());
			System.out.println("qtd de amostra: "+num);
			for (int j=0;j<=pe.size()-1;j++){
				list.add(this.ZTest(po.get(j), pe.get(j), num));
			}
			
			System.out.println("Entrou em ZTest4");
			String resultado = "";
			int i=0, j=0;
			double zcritico = Double.parseDouble(String.valueOf(param4));
			DecimalFormat fmt = new DecimalFormat("#,##0.00");
			for (Object lista: list){
				j++;
				if ( (Double.parseDouble(String.valueOf(lista)) > zcritico ) || (Double.parseDouble(String.valueOf(lista)) < -zcritico )){
					i++;
				}
				resultado += "Digit "+j+": "+fmt.format(Double.parseDouble(lista.toString()))+"<Br>"; 
			}
			//}else{
			if (i > 0){
				resultado += "<font color='red'><br>H0 Analisys is invalid.<BR><br> Checked values are not consistent with the expected values (+/- "+param4.toString()+").</font>"; 
			}else{
				resultado += "<font color='blue'><br>H0 Analisys is valid.<BR><Br> Checked values are consistent with the expected values (+/- "+param4.toString()+").</font>";
			}
			long tempFinal = System.currentTimeMillis();
			long dif = tempFinal - tempInicial;
			resultado += "<br>Performance time: "+String.format("%02d second(s) %02d milliseconds", dif/1000, dif%1000);
			return resultado;
		}
	}	
}
