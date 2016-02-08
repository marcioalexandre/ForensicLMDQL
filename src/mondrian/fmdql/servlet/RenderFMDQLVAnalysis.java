package mondrian.fmdql.servlet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;
/**
 *
 * @author paulo.caetano@pro.unifacs.br
 */
public class RenderFMDQLVAnalysis extends RenderFMDQL implements IRenderFMDQL {
	
	
	@Override
	public int quantidadeDeColunasExtras() {
		return 2;
	}
	
	
	
	public StringBuffer renderResultado(StringBuffer html) {
		
			
		List<Position> columns = this.getResultado().getAxes()[0].getPositions();
	    List<Position> rows = null;
	    if( this.getResultado().getAxes().length == 2 ) {
	         rows = this.getResultado().getAxes()[1].getPositions();
	    }
		
		
        //if is two axes, show
              
        List<Double> valores = new ArrayList<Double>();
        
        if (this.getResultado().getAxes().length > 1) {        	
        
            for (int i=0; i<rows.size(); i++) {                  
                for (int j=0; j<columns.size(); j++) {
                	                    	
                	Cell cell = this.getResultado().getCell(new int[]{j,i});
                	String formattedValue = cell.getFormattedValue();
                	formattedValue = formattedValue.replace(",", "");
                	
                	double valor = 0.0;
                	if(!formattedValue.trim().equals("")) {
                		valor = Double.parseDouble(formattedValue);
                	}                							
					valores.add(valor);	
                }
            }
      
            
            List<Double> calcularPorcentagens = this.calcularPorcentagens(valores);
     
            	String linhaCabecalhoProcentagem = "<tr>";
            	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading'>&nbsp;</td>";
            	
                for (int j=0; j<columns.size(); j++) {
                	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' align='center'>Absolute Value</td><td nowrap class='rowheading' align='center'>VA (%)</td>";
                }
                linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "</tr>"; 
                html.append(linhaCabecalhoProcentagem);
          
            
                            
            int contadorPorcentagens = 0;
            for (int i=0; i<rows.size(); i++) {
                html.append("<tr>");
                final Position row = rows.get(i);
                for (Member member : row) {
                    html.append("<td nowrap class='rowheading'>").append(
                        member.getUniqueName()).append("</td>");
                }
                for (int j=0; j<columns.size(); j++) {
                	                    	
                	Cell cell = this.getResultado().getCell(new int[]{j,i});                
                		showCellVAnalysis(html, cell, calcularPorcentagens.get(contadorPorcentagens));
                    	contadorPorcentagens++;
                
                }
                
                html.append("</tr>");
            }
            
       
        } else {
            html.append("<tr>");
            for (int i=0; i<columns.size(); i++) {
            	showCellVAnalysis(html,this.getResultado().getCell(new int[]{i}), 1.0);
            }
            html.append("</tr>");
        }
        html.append("</table>");
		
		
	
		return html;
	}


	private List<Double> calcularPorcentagens(List<Double> valores) {
		List<Double> porcentagens = new ArrayList<Double>();
		porcentagens.add(100.0);
	
		if(valores != null && valores.size() > 1) {
			double valorReferencia = valores.get(0);
		
			for(int i = 1; i < valores.size(); i++) {
				double valorAtual = valores.get(i);
				double porcentagem = (100 * valorAtual) / valorReferencia;
				porcentagens.add(porcentagem);
			}
		}
		
		return porcentagens;
	}
	
	
	private void showCellVAnalysis( StringBuffer out, Cell cell, double porcentagem) {
    	
    	DecimalFormat decimal = new DecimalFormat("###############0.00");
    	String procentagemFormatada = decimal.format(porcentagem);
    	procentagemFormatada = this.formatValor(procentagemFormatada);
    	
    	if(cell.getFormattedValue().trim().equals("")) {
    		
    		 out.append("<td class='cell'></td>");
    		 out.append("<td class='cell'></td>");
    	}else {
    		String valor = this.formatValor(cell.getFormattedValue()); 
    		    		
    		 out.append("<td class='cell'>").append(valor).append("</td>");
    		 out.append("<td class='cell'>").append(procentagemFormatada).append("</td>");
    		    		 
    	}       
    }



}
