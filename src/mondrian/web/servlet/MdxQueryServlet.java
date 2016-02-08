/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/web/servlet/MDXQueryServlet.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2002-2002 Kana Software, Inc.
// Copyright (C) 2002-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// Sean McCullough, 13 February, 2002, 10:25 PM
*/

package mondrian.web.servlet;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mondrian.fmdql.servlet.GerenciadorRender;
import mondrian.lmdql.Database;
import mondrian.lmdql.Parameters;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Cell;
import mondrian.olap.DriverManager;
import mondrian.olap.Exp;
import mondrian.olap.FunDef;
import mondrian.olap.Member;
import mondrian.olap.MondrianProperties;
import mondrian.olap.Position;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.Result;
import mondrian.olap.Util;
import mondrian.spi.CatalogLocator;
import mondrian.spi.impl.ServletContextCatalogLocator;
import mondrian.web.taglib.ResultCache;

import org.eigenbase.xom.StringEscaper;

/**
 * <code>MDXQueryServlet</code> is a servlet which receives MDX queries,
 * executes them, and formats the results in an HTML table.
 *
 * @author  Sean McCullough
 * @since 13 February, 2002
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/web/servlet/MDXQueryServlet.java#2 $
 */
public class MdxQueryServlet extends HttpServlet {
    private String connectString;
    private CatalogLocator locator;
    private String wsResult;

    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("init/MdxQueryServlet.java");
        connectString = config.getInitParameter("connectString");
        Enumeration initParameterNames = config.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String name = (String) initParameterNames.nextElement();
            String value = config.getInitParameter(name);
            MondrianProperties.instance().setProperty(name, value);
        }
        locator = new ServletContextCatalogLocator(config.getServletContext());
        Parameters.pathServer = config.getServletContext().getRealPath("/");
    }

    /** Destroys the servlet.
     */
    public void destroy() {

    }

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
    		System.out.println("processRequest/MDXQueryServlet.java");
	    	String database = request.getParameter("database");
	    	if(database.equals("XML")) // verifica se � processamento para banco XML ou banco relacional #Paulo Caetano
	    		Parameters.databaseType = Database.XML;
	    	else
	    		Parameters.databaseType = Database.RELATIONAL;
	    	System.out.println("Passou!");
	        String queryName = request.getParameter("query");
	        request.setAttribute("query", queryName);
	        if (queryName != null) {
	            processTransform(request,response);
	            return;
	        }
	        String queryString = request.getParameter("queryString");
	        request.setAttribute("queryString", queryString);
	        mondrian.olap.Connection mdxConnection = null;
	        StringBuffer html = new StringBuffer();
	    try {
	        // execute the query
        
 
            mdxConnection = DriverManager.getConnection(connectString, locator);
           
            Query q = mdxConnection.parseQuery(queryString);
            
            
            System.out.println("MDXQueryServlet.java Query q:"+q.toString());
            
            QueryAxis queryAxis = q.getAxes()[0];
            ResolvedFunCall set = (ResolvedFunCall)queryAxis.getSet();
            Exp exp = set.getArg(0);
            String name = null;
            
            /**
            try{ 
            	System.out.println("FunDef: "+set.getArg(0).toString());
	            ResolvedFunCall arg = (ResolvedFunCall) exp;
	            System.out.println("Marcio - MDXQueryServlet.java (arg): "+arg.toString());
	            FunDef funDef = arg.getFunDef();
	            System.out.println("Marcio - MDXQueryServlet.java (funDef): "+arg.toString());
	            name = funDef.getName();
	            System.out.println("alow6"+name);
            }catch(Exception e){
            	System.out.println(e.getLocalizedMessage());
            	e.printStackTrace();
            	
            }
            */
            
            Result result = mdxConnection.execute(q);
            
            System.out.println("Entrar em GerenciadorRender.java");
            GerenciadorRender gerenciadorRender = new GerenciadorRender(q, result);
            System.out.println("Saiu em GerenciadorRender.java");
            html = gerenciadorRender.renderFMDQL();
            System.out.println("html produzido: "+html.toString());
            
            if(html == null || html.toString() == null || html.toString().trim().length() == 0) {
            	          
            	System.out.println("Nao retornou hmtl do renderFMDQL");
	            List<Position> slicers = result.getSlicerAxis().getPositions();
	            html.append("<table class='resulttable' cellspacing=1 border=0>");
	            html.append(Util.nl);
	
	            List<Position> columns = result.getAxes()[0].getPositions();
	            List<Position> rows = null;
	            if( result.getAxes().length == 2 )
	                rows = result.getAxes()[1].getPositions();
	
	            int columnWidth = columns.get(0).size();
	            int rowWidth = 0;
	            if( result.getAxes().length == 2 )
	                    rowWidth = result.getAxes()[1].getPositions().get(0).size();
	
	            for (int j=0; j<columnWidth; j++) {
	                html.append("<tr>");
	
	                // if it has more than 1 dimension
	                if (j == 0 && result.getAxes().length > 1) {
	                    // Print the top-left cell, and fill it with slicer members.
	                    html.append("<td nowrap class='slicer' rowspan='").append(
	                        columnWidth).append("' colspan='").append(rowWidth)
	                        .append("'>");
	                    for (Position position : slicers) {
	                        int k = 0;
	                        for (Member member : position) {
	                            if (k > 0) {
	                                html.append("<br/>");
	                            }
	                            html.append(member.getUniqueName());
	                            k++;
	                        }
	
	                    }
	                    html.append("&nbsp;</td>").append(Util.nl);
	                }
	
	                // Print the column headings.
	                for (int i=0; i<columns.size(); i++) {
	                    Position position = columns.get(i);
	                    //Member member = columns[i].getMember(j);
	                    Member member = position.get(j);
	                    
	                                     
	                    int width = 2;
	                    while ((i + 1) < columns.size() &&
	                            columns.get(i + 1).get(j) == member) {
	                        i++;
	                        width++;
	                    }
	                    html.append("<td nowrap class='columnheading' colspan='")
	                        .append(width).append("'>")
	                        .append(member.getUniqueName()).append("</td>");
	                }
	                html.append("</tr>").append(Util.nl);
	            }
	            //if is two axes, show
	            
	            
	          
	            List<Double> valores = new ArrayList<Double>();
	            
	            if (result.getAxes().length > 1) {
	            	
	            	  if(name.equals("VAnalysis")) {
	            	//Parte Relativa � AnaliseVertical
	                for (int i=0; i<rows.size(); i++) {                  
	                    for (int j=0; j<columns.size(); j++) {
	                    	                    	
	                    	Cell cell = result.getCell(new int[]{j,i});
	                    	String formattedValue = cell.getFormattedValue();
	                    	formattedValue = formattedValue.replace(",", ".");
	                    	
	                    	double valor = 0.0;
	                    	if(!formattedValue.trim().equals("")) {
	                    		valor = Double.parseDouble(formattedValue);
	                    	}
	                    							
							valores.add(valor);	
	                    }
	                }
	                }
	                
	                List<Double> calcularPorcentagens = this.calcularPorcentagens(valores);
	                
	                
	                if(name.equals("VAnalysis")) {
	                	String linhaCabecalhoProcentagem = "<tr>";
	                	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading'>&nbsp;</td>";
	                	
	                    for (int j=0; j<columns.size(); j++) {
	                    	linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "<td nowrap class='rowheading' align='center'>Valor Absoluto</td><td nowrap class='rowheading' align='center'>AV</td>";
	                    }
	                    linhaCabecalhoProcentagem = linhaCabecalhoProcentagem + "</tr>"; 
	                    html.append(linhaCabecalhoProcentagem);
	                }
	                
	                
	                                
	                int contadorPorcentagens = 0;
	                for (int i=0; i<rows.size(); i++) {
	                    html.append("<tr>");
	                    final Position row = rows.get(i);
	                    for (Member member : row) {
	                        html.append("<td nowrap class='rowheading'>").append(
	                            member.getUniqueName()).append("</td>");
	                    }
	                    for (int j=0; j<columns.size(); j++) {
	                    	                    	
	                    	Cell cell = result.getCell(new int[]{j,i});
	                    	if(name.equals("VAnalysis")) {
	                    		showCellVAnalysis(html, cell, calcularPorcentagens.get(contadorPorcentagens));
	                        	contadorPorcentagens++;
	                    	}else {
	                    		showCell(html,cell);
	                    	}
	                    }
	                    
	                    html.append("</tr>");
	                }
	                
	           
	            } else {
	                html.append("<tr>");
	                for (int i=0; i<columns.size(); i++) {
	                    showCell(html,result.getCell(new int[]{i}));
	                }
	                html.append("</tr>");
	            }
	            html.append("</table>");
            }else{
            	System.out.println("Retornou algum hmtl do renderFMDQL");
            }
        } catch (Throwable e) {
        	System.out.println("Algo deu errado!");
            final String[] strings = Util.convertStackToString(e);
            html.append("Error:<pre><blockquote>");
            for (String string : strings) {
                StringEscaper.htmlEscaper.appendEscapedString(string, html);
            }
            html.append("</blockquote></pre>");
        } finally {
            if (mdxConnection != null) {
                mdxConnection.close();
            }
        }

        request.setAttribute("timesub",request.getParameter("timesub")); // repassa a date de submiss�o #Paulo Caetano
        request.setAttribute("result", html.toString());
        response.setHeader("Content-Type", "text/html");
        this.wsResult = html.toString();
        //System.out.println("Output LMDQL WService: "+html.toString());
        
        FileWriter arq = new FileWriter("d:\\Output_LMDQLWService.txt");
        PrintWriter gravarArq = new PrintWriter(arq);

        gravarArq.printf("+--Output LMDQL WService--+%n");
        
        gravarArq.printf(html.toString());

        arq.close();
        
        getServletContext().getRequestDispatcher("/adhoc.jsp").include(request, response);
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
    
    
    private void showCell( StringBuffer out, Cell cell) {
        out.append("<td class='cell' colspan='2'>").append(cell.getFormattedValue()).append(
            "</td>");
    }
    
    private void showCellVAnalysis( StringBuffer out, Cell cell, double porcentagem) {
    	
    	DecimalFormat decimal = new DecimalFormat("####################.00");
    	String procentagemFormatada = decimal.format(porcentagem);
    	if(cell.getFormattedValue().trim().equals("")) {
    		
    		 out.append("<td class='cell'></td>");
    		 out.append("<td class='cell'></td>");
    	}else {
    		 out.append("<td class='cell'>").append(cell.getFormattedValue()).append("</td>");
    		 out.append("<td class='cell'>").append(procentagemFormatada + "%").append("</td>");
    		    		 
    	}       
    }
    
    
    private void processTransform(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryName = request.getParameter("query");
        ResultCache rc = ResultCache.getInstance(request.getSession(), getServletContext(), queryName);
        Query query = rc.getQuery();
        query = query.safeClone();
        rc.setDirty();
        String operation = request.getParameter("operation");
        if (operation.equals("expand")) {
            String memberName = request.getParameter("member");
            boolean fail = true;
            Member member = query.getSchemaReader(true).getMemberByUniqueName(
                    Util.parseIdentifier(memberName), fail);
            if (true) {
                throw new UnsupportedOperationException(
                        "query.toggleDrillState(member) has been de-supported");
            }
        } else {
            throw Util.newInternal("unkown operation '" + operation + "'");
        }
        rc.setQuery(query);
        String redirect = request.getParameter("redirect");
        if (redirect == null) {
            redirect = "/adhoc.jsp";
        }
        response.setHeader("Content-Type", "text/html");
        getServletContext().getRequestDispatcher(redirect).include(request, response);
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    public String doWebService (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
        return this.wsResult;
    }

    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Process an MDX query and return the result formatted as an HTML table";
    }
    
}
