package mondrian.fmdql.servlet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;

/**
 *
 * @author marcio.alexandre83@gmail.com / paulo.caetano@pro.unifacs.br
 */
public class RenderLMDQLEmpiricalRule  extends RenderFMDQL implements IRenderFMDQL  {
        public int quantidadeDeColunasExtras() {
		return 3;
	}
	public StringBuffer renderResultado(StringBuffer html) {
		// exemplo VAnalysis
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
            	String linhaCabecalhoProcentagem = "<tr>";
            	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading'>&nbsp;</td>";
            	
                for (int j=0; j<columns.size(); j++) {
                	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' align='center'>Negative Sigmas</td><td nowrap class='rowheading' align='center'>Value</td><td nowrap class='rowheading' align='center'>Positive Sigmas</td>";
                }
                linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "</tr>"; 
                html.append(linhaCabecalhoProcentagem);
  
                int contadorPorcentagens = 0;
                for (int i=0; i<rows.size(); i++) {
                    html.append("<tr>");
                    final Position row = rows.get(i);
                    String memberName = null;
                    //apresenta elementos das linhas
                    int x = 0;
                    for (Member member : row) {
                        x++;
                        String elementName = this.getElementName(member.getUniqueName());
                        if (elementName.length() >= 50){
                            elementName = elementName.substring(0, 50);
                        }
                        html.append("<td nowrap class='rowheading' >").append(
                                "<a href='#' onclick='window.alert(\""+this.getElementName(member.getUniqueName())+"\");'>"+elementName+"</a>").append("</td>"); //column with element names
                        memberName = member.getUniqueName();
                    }

                    for (int j=0; j<columns.size(); j++) {
                        Cell cell = this.getResultado().getCell(new int[]{j,i});                
                        showCellEmpiricalRule(html, cell, memberName,i);
                        //contadorPorcentagens++;
                    }

                    html.append("</tr>");
                }
            } else {
                html.append("<tr>");
                for (int i=0; i<columns.size(); i++) {
                    html.append("marcio - 2");
                    //showCellVAnalysis(html,this.getResultado().getCell(new int[]{i}), 1.0);
                }
                html.append("</tr>");
            }
            html.append("</table>");
            return html;
	}
	
        private void showCellEmpiricalRule(StringBuffer out, Cell cell, String ele, int i) {
            if(cell.getFormattedValue().trim().equals("")) {
                     out.append("<td class='cell'>-</td>");
                     out.append("<td class='cell'>-</td>");
                     out.append("<td class='cell'>-</td>");
            }else {
                    if (ele != null || !(ele.equals("")) || !(ele.length()==0)){
                        ele = this.getElementName(ele);
                    }
                    String valor = this.formatValor(cell.getFormattedValue()); 
                    String flag = "EmpiricalRuling...";
                    out.append("<td class='cell'>").append(flag).append("</td>");
                    out.append("<td class='cell'>").append(valor).append("</td>");
                    out.append("<td class='cell'>").append(flag).append("</td>");

            }       
        }
		
	private String getElementName(String ele){
            String name = null;
            String[] elePart = ele.split("\\[");
            String[] eleName = elePart[3].split("\\:");
            if (eleName.length > 1){
                name = eleName[1];
            }else{
                name = eleName[0];
            }
            name = name.replace("]", "").trim();
            return name;
	}
        
        /*
        private String getFirstDigit(String valor){
            System.out.println("getFirstDigit - "+valor);
            String fdigit = null;
            System.out.println(valor.substring(0,1));
            System.out.println(valor.substring(0,2));
            
            if (valor.substring(0,2).contains("-")){
                fdigit = valor.substring(1,2);
            }else{
                fdigit = valor.substring(0,1);
            }
            System.out.println("Digito: "+fdigit);
            return fdigit;
	}
        */
}
