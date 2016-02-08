package mondrian.web.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mondrian.lmdql.OperatorDefinition;
import mondrian.lmdql.Parameters;
import mondrian.olap.MondrianProperties;
import mondrian.spi.CatalogLocator;
import mondrian.spi.impl.ServletContextCatalogLocator;

/**
 * Servlet implementation class for Servlet: OpDefServlet
 *
 */
 public class OpDefServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   private String connectString;
   private CatalogLocator locator;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public OpDefServlet() {
		super();
	}   	
	
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
    public void destroy() {    }
    
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
	}

	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
    	// guarda o retorno do processamento em 'result', variável a ser tratada em operator_definition.jsp
    	request.setAttribute("result", OperatorDefinition.createOperatorDefinition(request.getParameter("opDef")));
    	response.setHeader("Content-Type", "text/html");
        getServletContext().getRequestDispatcher("/operator_definition.jsp").include(request, response);
    }
	
}