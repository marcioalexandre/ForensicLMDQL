package mondrian.fmdql.servlet;

import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;
/**
 *
 * @author paulo.caetano@pro.unifacs.br
 */
public class RenderFMDQLCross extends RenderFMDQL implements IRenderFMDQL {

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
              
        if (this.getResultado().getAxes().length > 1) {        	
        
            for (int i=0; i<rows.size(); i++) {                  
                for (int j=0; j<columns.size(); j++) {
                	                    	
                	Cell cell = this.getResultado().getCell(new int[]{j,i});
                	String formattedValue = cell.getFormattedValue();
                	html.append("<tr>"+formattedValue+"</tr>");
                	
                }
            }
        }
      
            
        html.append("</table>");
		
		
	
		return html;
	}

}
