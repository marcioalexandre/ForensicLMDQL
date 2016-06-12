package mondrian.fmdql.servlet;

import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author marcio.alexandre83@gmail.com / paulo.caetano@pro.unifacs.br
 */
public class RenderLMDQLGriConformity extends RenderFMDQL implements IRenderFMDQL {
	
	private ArrayList<String> staticGriConcepts = getGriSchemaElements(); 
	public int quantidadeDeColunasExtras() {
		return 2;
	}
	public StringBuffer renderResultado(StringBuffer html) {
		List<Position> columns = this.getResultado().getAxes()[0].getPositions();
	    List<Position> rows = null;
	    if( this.getResultado().getAxes().length == 2 ) {
	         rows = this.getResultado().getAxes()[1].getPositions();
	    }
              
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
            	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' width='100'>&nbsp;</td>";
            	
                for (int j=0; j<columns.size(); j++) {
                	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' align='center'>Value</td><td nowrap class='rowheading' align='center'>GRI Conformity</td>";
                }
                linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "</tr>"; 
                html.append(linhaCabecalhoProcentagem);
          
            
                            
            int contadorPorcentagens = 0;
            for (int i=0; i<rows.size(); i++) {
                html.append("<tr>");
                final Position row = rows.get(i);
                String memberName = null;
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
                    showCellGriConformity(html, cell, memberName);

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
	
	private void showCellGriConformity(StringBuffer out, Cell cell, String ele) {
            String flag = "";
            int oj = 0;
    	if(cell.getFormattedValue().trim().equals("")) {
    		 out.append("<td class='cell'>-</td>");
    		 out.append("<td class='cell'>-</td>");
    	}else {
    		if (ele != null || !(ele.equals("")) || !(ele.length()==0)){
                    ele = this.getElementName(ele);
    		}
    		String valor = this.formatValor(cell.getFormattedValue()); 
    		    		
    		 out.append("<td class='cell'>").append(valor).append("</td>");
                 if (valor.trim() == ""){
                    flag = "<font color='red'>No</font>";
                 }else{
                    if (staticGriConcepts.contains(ele)){
                        flag = "<font color='blue'>yes</font>";
                    }
                 }
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
        
	final static ArrayList<String> getGriSchemaElements(){
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		ArrayList<String> eles = new ArrayList<String>();
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document dDoc = builder.parse("http://xbrl.globalreporting.org/2013-11-07/G4/GRI-Concepts.xsd");
                       
            XPath xPath = XPathFactory.newInstance().newXPath();
            //2038 elements from GRI
            for (int i=1;i<=2138;i++){
            	Node node = (Node) xPath.evaluate("(//element["+i+"]/@name)", dDoc, XPathConstants.NODE);
            	eles.add(node.getNodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return eles;
	}
        
}
