/*
 * Copyright 2016 Marcio Alexandre P. Silva
 * Licensed under the Apache License, Version 2.0 (the "License");
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package mondrian.fmdql.servlet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;

/**
 *
 * @author Marcio Silva (marcio.alexandre83@gmail.com), Paulo (paulo.caetano@pro.unifacs.br)
 */
public class RenderLMDQLZTest extends RenderFMDQL implements IRenderFMDQL {
        int coun1=0, coun2=0, coun3=0, coun4=0, coun5=0, coun6=0, coun7=0, coun8=0, coun9=0, total=0;
	public int quantidadeDeColunasExtras() {
		return 2;
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
            	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' width='100'>&nbsp;</td>";
            	
                for (int j=0; j<columns.size(); j++) {
                	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' align='center'>Value</td><td nowrap class='rowheading' align='center'>FirstDigit Analysis</td>";
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
                        this.showCellZTest(html, cell, memberName,i);
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
	
        private void showCellZTest(StringBuffer out, Cell cell, String ele, int i) {
            if(cell.getFormattedValue().trim().equals("")) {
                     out.append("<td class='cell'>-</td>");
                     out.append("<td class='cell'>-</td>");
            }else {
                    if (ele != null || !(ele.equals("")) || !(ele.length()==0)){
                        ele = this.getElementName(ele);
                    }
                    String valor = this.formatValor(cell.getFormattedValue()); 

                     out.append("<td class='cell'>").append(valor).append("</td>");
                     //String flag = "<font color='red'>No</font>";
                     //for (String e : this.griConcepts){
                     //	 if (ele.equals(e)){
                     //		 flag = "<font color='blue'>Yes</font>";
                     //	 }
                     //}
                     String fdigit = this.getFirstDigit(valor);
                     int intFDigit = Integer.parseInt(fdigit);
                     total++;
                     switch(intFDigit){
                             case 1: coun1++; break;
                             case 2: coun2++; break;
                             case 3: coun3++; break;
                             case 4: coun4++; break;
                             case 5: coun5++; break;
                             case 6: coun6++; break;
                             case 7: coun7++; break;
                             case 8: coun8++; break;
                             case 9: coun9++; break;
                     }
                     //p0 probability of observing https://en.wikipedia.org/wiki/Z-test
                     //p1 waited probalibility - https://www.mathsisfun.com/numbers/benfords-law.html
                     double c1 = this.getZTest(coun1*100/total, 30.10, total);
                     double c2 = this.getZTest(coun2*100/total, 17.61, total);
                     double c3 = this.getZTest(coun3*100/total, 12.49, total);
                     double c4 = this.getZTest(coun4*100/total, 9.69, total);
                     double c5 = this.getZTest(coun5*100/total, 7.92, total);
                     double c6 = this.getZTest(coun6*100/total, 6.69, total);
                     double c7 = this.getZTest(coun7*100/total, 5.80, total);
                     double c8 = this.getZTest(coun8*100/total, 5.11, total);
                     double c9 = this.getZTest(coun9*100/total, 4.58, total);
                     DecimalFormat fmt = new DecimalFormat("#,##0.00");
			//double abs = Double.parseDouble(fmt.format(Math.abs(po-pe)));
                     String flag = "";
                         flag += "<font color='red'>Rule: -2.57 < value < 2.57</font><br>";
                         flag += "1: "+fmt.format(c1)+"<br>";
                         flag += "2: "+fmt.format(c2)+"<br>";
                         flag += "3: "+fmt.format(c3)+"<br>";
                         flag += "4: "+fmt.format(c4)+"<br>";
                         flag += "5: "+fmt.format(c5)+"<br>";
                         flag += "6: "+fmt.format(c6)+"<br>";
                         flag += "7: "+fmt.format(c7)+"<br>";
                         flag += "8: "+fmt.format(c8)+"<br>";
                         flag += "9: "+fmt.format(c9)+"<br>";

                        /*
                         flag += "<a href='#' onclick='window.open(\"graphFirstDigit.jsp?one="+coun1*100/total+"&two="
                                 + ""+coun2*100/total+"&three="+coun3*100/total+"&four="+coun4*100/total+"&five="+coun5*100/total+""
                                 + "&six="+coun6*100/total+"&seven="+coun7*100/total+"&eight="+coun8*100/total+"&nine="
                                 + ""+coun9*100/total+"\", \"_blank\", \"toolbar=yes, scrollbars=yes, resizable=yes, top=500,"
                                 + " left=500, width=400, height=400\")'"
                                 + "> graphic view </a>";
                        */
                                 
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
            
        private double getZTest(double po, double pe, int n){
            try{
		System.out.println("Po:"+po+", Pe:"+pe+", N:"+n);
		double diff = po-pe;
		double calc = Math.abs(pe-(pe*pe)/n);
		double sqrt = Math.sqrt(calc);
		double z = 0;
		if (diff < 1/(2*n)){
                    z = diff/sqrt;
		}else{
			z = (diff - (1/(2*n)))/sqrt;
		}		
		System.out.println("Imprimindo resultado:"+z);
		return z;
		}catch(Exception e){
                    throw new RuntimeException("ZTest - Erro: "+e.getMessage());	
		}
        }
}

