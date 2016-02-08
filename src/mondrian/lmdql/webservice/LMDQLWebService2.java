/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mondrian.lmdql.webservice;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mondrian.web.servlet.MdxQueryServlet;

/**
 *
 * @author Marcio
 */
@WebService(serviceName = "LMDQLWebService2")
public class LMDQLWebService2 {


    /**
     * Operação de Web service
     */
    @WebMethod(operationName = "processRequest")
    public String processRequest(@WebParam(name = "query") String query, @WebParam(name = "dbtype") String dbtype) {
        String result = null;
		if (query != null){
			HttpServletRequest request = null;
			HttpServletResponse response = null;
			
			query = query.replace(" ","+");
			
			request.setAttribute("query",query);
			request.setAttribute("database", dbtype);
			
			MdxQueryServlet mqs = new MdxQueryServlet(); 
		
			try {
				result = mqs.doWebService(request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
    }
}
