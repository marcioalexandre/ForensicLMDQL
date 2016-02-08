package marcio.forense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

public class SetCache implements UserDefinedFunction {
		

		public String getDescription() {
			return "Retorna qtd de dados armazenados em cache";
		}
		public Syntax getSyntax() {
			return Syntax.Function;
		}
		public String getName() {
			return "SetCache";
		}
		
		public Type getReturnType(Type[] parameterTypes) {
	        //return new BooleanType();
			//return new StringType();
			return new SetType(MemberType.Unknown);
	    }
	    
		  
		public Type[] getParameterTypes() {
			//ano do documento e nome do documento
	        //return new Type[] {new StringType(),new StringType()};
	        //return new Type[] { new SetType(MemberType.Unknown), MemberType.Unknown, new NumericType()};
			return new Type[] { new SetType(MemberType.Unknown), new SetType(MemberType.Unknown), new NumericType()};
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
		public Object execute(Evaluator eva, Argument[] arg) {//(empresa,ano,documento)
			
			List<Member> valores = (List<Member>) arg[0].evaluate(eva);	
			List<Member> membro  = (List<Member>) arg[1].evaluate(eva);	
			Object valor = (Object) arg[2].evaluate(eva);
			//Member membro = (Member) arguments[1].evaluate(evaluator);	
			//Object quantidadeValoresPadraoObject = arguments[2].evaluateScalar(evaluator);
						
			//final Object param0 = arg1[0].evaluateScalar(arg0); //nome da empresa
			//final Object param1 = arg1[1].evaluateScalar(arg0); //nome da empresa
			//final Object param1 = arg1[1].evaluateScalar(arg0); //data do documento
			//final Object param2 = arg1[2].evaluateScalar(arg0); //nome do documento
			//final Object param3 = arg1[3].evaluateScalar(arg0); //teste com valores 
			
			//System.out.println(String.valueOf(param1));
			//System.out.println(String.valueOf(param2));
			//System.out.println(String.valueOf(param3));
/**
			final Object[] param = new Object[2];
			for (int i=0;i<=1;i++){
				param[i] = arg1[i].evaluateScalar(arg0);
			}
*/			
			
			Object[] retorno = new Object[valores.size()+1];
			
			//System.out.println(membro.toString());
			//System.out.println(valores.toString());
			ArrayList<String> arrTok =  new ArrayList<String>();
			for (Member member1: membro){
				String membroString = member1.toString();
				String tokens[] = membroString.split("].");
				for (int i=0;i<tokens.length;i++){
					tokens[i] = tokens[i].replace("[", "");
					tokens[i] = tokens[i].replace("]", "");
					if (arrTok.contains(tokens[i])){
					}else{
						arrTok.add(tokens[i]);
					}
				}
			}
			System.out.println("token Documento: "+arrTok.toString());			
					
			//System.out.println(membro.toString());
			//System.out.println(valores.toString());
			ArrayList<String> arrTok2 =  new ArrayList<String>();
			for (Member member1: valores){
				String membroString = member1.toString();
				String tokens[] = membroString.split("].");
				for (int i=0;i<tokens.length;i++){
					tokens[i] = tokens[i].replace("[", "");
					tokens[i] = tokens[i].replace("]", "");
					if (arrTok2.contains(tokens[i])){
					}else{
						arrTok2.add(tokens[i]);
					}
				}
			}
			System.out.println("token Entidade: "+arrTok2.toString());		
			retorno[0] = membro.get(1);
			int i = 1;		
			
			for (Member member : valores) {
				retorno[i] = member;
				i++;
			}
			return Arrays.asList(retorno);
		
			//return String.valueOf(param0)+" - "+String.valueOf(param1);
			
		}
}
		

