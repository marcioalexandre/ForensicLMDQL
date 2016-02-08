package mondrian.fmdql.servlet;

import java.util.Hashtable;

import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Exp;
import mondrian.olap.FunDef;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.Result;

public class GerenciadorRender {
	
	private static final Hashtable<String, RenderFMDQL> GERENCIADOR_RENDERS;
	
	private RenderFMDQL render;
	
	
	
	static {
	
		GERENCIADOR_RENDERS = new Hashtable<String, RenderFMDQL>();
		GERENCIADOR_RENDERS.put("VAnalysis", new RenderFMDQLVAnalysis());	
		//GERENCIADOR_RENDERS.put("HAnalysis", new RenderFMDQLHAnalysis());
		//GERENCIADOR_RENDERS.put("Separatrix", new RenderFMDQLSeparatrix());	
		//GERENCIADOR_RENDERS.put("NNearestValues", new RenderFMDQLNNearestValues());	
		//GERENCIADOR_RENDERS.put("NNearestValuesPercentual", new RenderFMDQLNNearestValuesPercentual());	
		GERENCIADOR_RENDERS.put("Cross", new RenderFMDQLCross());
		GERENCIADOR_RENDERS.put("GriConformity", new RenderLMDQLGriConformity());
                GERENCIADOR_RENDERS.put("GriGaple", new RenderLMDQLGriGaple());
                GERENCIADOR_RENDERS.put("GriGee", new RenderLMDQLGriGee());
                GERENCIADOR_RENDERS.put("FirstDigit", new RenderLMDQLFirstDigit());
                GERENCIADOR_RENDERS.put("EmpiricalRule", new RenderLMDQLEmpiricalRule());
	}
	
	
	public GerenciadorRender(Query consulta, Result result) {
		System.out.println("GerenciadorRender.java");
		
		QueryAxis queryAxis = consulta.getAxes()[1];
        ResolvedFunCall set = (ResolvedFunCall)queryAxis.getSet();
        Exp exp = set.getArg(0);
        String name = null;
        if (exp.toString().contains("GriConformity")){
        	System.out.println("achou gri");
        	name = "GriConformity";
        }else if(exp.toString().contains("FirstDigit")){
            	System.out.println("There is FirstDigit operator");
            	name = "FirstDigit";
        }else if(exp.toString().contains("EmpiricalRule")){
            	System.out.println("There is EmpiricalRule operator");
            	name = "EmpiricalRule";
        }else if(exp.toString().contains("GriGaple")){
            	System.out.println("There is GriGaple operator");
            	name = "GriGaple";
        }else if(exp.toString().contains("GriGee")){
            	System.out.println("There is GriGee operator");
            	name = "GriGee";
        }

        if (name != null){
	        this.render = GERENCIADOR_RENDERS.get(name);
	        if(this.render != null) {
	        	this.render.setResultado(result);
	        	
	        }
        }
              
	}
	
	
	public StringBuffer renderFMDQL() {
		
		StringBuffer html = new StringBuffer();	
		//html.append("GerenciadorRender.java - marcio");
     
		if(this.render != null) {
			this.render.renderizarCabecalho(html);    		
			this.render.renderResultado(html);
		}
           
       
       		
		return html;		
	}
	
		

}
