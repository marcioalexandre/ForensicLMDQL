package mondrian.lmdql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import mondrian.olap.Util;

import org.xml.sax.SAXException;

import com.desenvolvatec.xpathplus.framework.Arc;
import com.desenvolvatec.xpathplus.framework.XPathPlusExecutor;
import com.desenvolvatec.xpathplus.xmldb.StartPoint;

public class OperatorDefinition {

	public static String queryOpDefProcessed(String query) throws ParserConfigurationException, SAXException, IOException, SQLException, ClassNotFoundException{
		if(query.trim().indexOf("WITH")==0) // é query pré-programada pelo MONDRIAN, não precisa tratamento
			return query;
		String newQuery = query;
		String[][] opDefs = getOpDefs();
		if(opDefs!=null){
			String withMembers = "with ";
			for(int i=0;i<opDefs[0].length;i++){
				newQuery = newQuery.replace("#"+opDefs[2][i]+"#", opDefs[1][i]);
				withMembers += " " + opDefs[3][i];
			}
			newQuery = withMembers + " " + newQuery;
		}
		return newQuery.trim();
	}

	public static String[][] getOpDefs() throws ParserConfigurationException, SAXException, IOException, SQLException, ClassNotFoundException{
		XLink.loadXpathplusConfigFile();
		XPathPlusExecutor xpathplus = new XPathPlusExecutor(Parameters.xmlXPathPlusFileName,true,false,StartPoint.OPERATOR_DEFINITION);
		Parameters.opDefArcs = new ArrayList<Arc>(xpathplus.getArcs()); // obtém todos os arcos de funções de operator definition
		ArrayList<String> functions = new ArrayList<String>();
		ArrayList<String> functionsCod = new ArrayList<String>();
		ArrayList<String> functionsName = new ArrayList<String>();
		ArrayList<String> functionsSubst = new ArrayList<String>();
		for(Arc arc:Parameters.opDefArcs){
			int indice = 0;
			for(indice=0;indice<functions.size() && functions.get(indice).indexOf(arc.getNodeFrom(0).getTextContent().trim())==-1;indice++); // nome da função está em "from"
			if(indice==functions.size()) {// não achou, função nova
				functionsCod.add(arc.getLabelFrom()); // label funciona como uma espécie de código Ex.UnitSales
				functionsName.add(arc.getNodeFrom(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim()); // nome a ser referido (e entendido)  no MDX. Ex.[Measures].[Unit Sales]
				functionsSubst.add(arc.getNodeFrom(0).getTextContent().trim()); // nome de referência externa para ser substituido pelo functionName. Ex. Unit Sales, a ser referenciado como #Unit Sales#
				functions.add("member "+arc.getNodeFrom(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim()+ " as '"); // definição da função. Ex. with member [Measures].[Unit Sales] as '[Measures].[Unit Sales] / [Measures].[Unit Sales]'
			}
			functions.set(indice,OperatorDefinition.complementaFunc(functions.get(indice),arc));
		}
		if(functions.size()==0) // não tem funções / operator definition
			return null;
		// pode ter função sem as aspas de fechamento (em caso de soma)
		for(int i=0;i<functions.size();i++){
			if(functions.get(i).lastIndexOf("'")!=functions.get(i).length()-1)
				functions.set(i, functions.get(i)+"'");
		}
		String[][] opDefs = new String[4][functions.size()];
		for(int i=0;i<functions.size();i++) {// montando o array de retorno
			opDefs[0][i] = functionsCod.get(i); // nome do código do operator definition
			opDefs[1][i] = functionsName.get(i); // nome mdx correto do operator definition
			opDefs[2][i] = functionsSubst.get(i); // nome do operador a ser substituido
			opDefs[3][i] = functions.get(i); // comando MDX
		}
		return opDefs;
	}

	private static String complementaFunc(String func, Arc arc) {
		// vai montando a funcao de acordo com o arcrole - só trata as 4 operações, a radiciação e a exponenciação
		String op = null;
		if(arc.getArcRole().indexOf("summation-item")!=-1) { // é soma - nunca encerra aqui
			String exp = arc.getNode().getAttributes().getNamedItem("weight").getNodeValue().trim() + "*" + arc.getNodeTo(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim();
			op = func + (func.indexOf("'")!=func.length()-1?"+":"") + exp; // pode ter expressão antes
		} else  if(arc.getArcRole().indexOf("multiplication-item")!=-1) {
			String exp = arc.getNode().getAttributes().getNamedItem("weight").getNodeValue().trim() + "*" + arc.getNodeTo(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim();
			op = func + exp + "'"; // abre e encerra a multiplicação
		} else  if(arc.getArcRole().indexOf("numerator-item")!=-1) { // ATENÇÃO: sempre vem antes do denominator
			op = func + arc.getNodeTo(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim(); // abre a divisão
		} else  if(arc.getArcRole().indexOf("denominator-item")!=-1) { // ATENÇÃO: sempre vem depois do numerator
			op = func + "/"+arc.getNodeTo(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim() + "'"; // encerra a divisão
		} else  if(arc.getArcRole().indexOf("exponentiation-item")!=-1) { // exponenciação
			String exp = "Power("+arc.getNodeTo(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim()+","+arc.getNode().getAttributes().getNamedItem("expoent").getNodeValue().trim()+")";
			op = func + exp + "'"; // abre e encerra a exponenciação
		} else  if(arc.getArcRole().indexOf("nthroot-item")!=-1) { // radiciação
			String exp = "Power("+arc.getNodeTo(0).getAttributes().getNamedItem("xldm:memberRef").getNodeValue().trim()+",1/"+arc.getNode().getAttributes().getNamedItem("index").getNodeValue().trim()+")";
			op = func + exp + "'"; // abre e encerra a radiciação
		}
		return op;
	}

	public static String createOperatorDefinition(String text){
		text = removeDoubleWhiteSpaces(text); // tirando espaços wm branco excessivos
		if(text==null || text.equals(""))
			return "Cannot enter a empty text";
		int pos1 = text.indexOf("'");
		if(pos1==-1 || !text.substring(0,pos1).trim().equalsIgnoreCase("With OperatorDefinition("))
			return "Bad format: 'With OperatorDefinition' statement";
		int pos2 = text.lastIndexOf("'");
		if(pos2<=pos1) 
			return "Invalid statement: missing single quotes";
		String exp = text.substring(pos1+1,pos2);
		if(!text.substring(pos2+1).trim().substring(0,1).equals(","))
			return "Bad format: 'With OperatorDefinition' without a comma separator";
		String opDefName = text.substring(pos2+1,text.length()-1).trim().substring(1).trim(); // substring(1) para tirar a virgula
		if(opDefName.equals("")) 
			return "Invalid operator name: "+opDefName;
		if(!text.substring(text.length()-1,text.length()).equals(")"))
			return "Invalid statement: unclosed parenthesis";
		String op = exp.indexOf("/")!=-1?"/":(exp.indexOf("*")!=-1?"*":(exp.indexOf("+")!=-1?"+":(exp.indexOf("/")!=-1?"/":(exp.indexOf("-")!=-1?"-":(exp.indexOf("^")!=-1?"^":(exp.indexOf("¬")!=-1?"¬":null))))));
		if(op==null) return "Missing operator";
		int pos = exp.indexOf(op);
		String membro1 = exp.substring(0,pos).trim(); String membro2 = exp.substring(pos+1,exp.length()).trim();
		if(membro1.equals("") || membro2.equals(""))
			return "Invalid member in expression";
		return buildOpDefXML(opDefName,membro1,membro2,op);
	}
	
	
    private static String buildOpDefXML(String opDefName, String membro1, String membro2, String op) {
    	int pos = membro1.lastIndexOf("["); String opMemberRef = "["+opDefName+"]"; // se pos==0
    	if(pos==-1)
    		return "Invalid member: "+membro1;
    	else if(pos>0)
    		opMemberRef = membro1.substring(0,pos)+opMemberRef;  
    	// definindo resource de arc para operator
    	String xmlOpDef = "<xldm:operator xlink:type=\"resource\" xlink:label=\""+opDefName+"\"" +
    			          " xlink:role=\"http://www.xbrl.org/2003/role/calculation\" id=\""+opDefName+"\"" +
    			          " xldm:memberRef=\""+opMemberRef+"\">"+opDefName+"</xldm:operator>";
    	// definindo labels de membro - membro1
    	String membro1Name = membro1.substring(pos+1,membro1.length()-1).trim();
    	if(membro1Name.equals(""))
    		return "Invalid member name: "+membro1;
    	// definindo labels de membro - membro1
    	pos = membro2.lastIndexOf("["); 
    	String membro2Name = null; // membro 2 pode ser um membro ou um número
    	if(pos==-1){
    		try{
    			double n = Double.parseDouble(membro2); // testando para ver se é um número
    			membro2Name = String.valueOf(n);
    		} catch (Exception e) {
    			return "Invalid member: "+membro2;
    		}
    	} else
        	membro2Name = membro2.substring(pos+1,membro2.length()-1).trim();
    	if(membro2Name.equals(""))
    		return "Invalid member name: "+membro2;
    	// definindo locators de arc para os members - outros lados do arc
    	xmlOpDef += "<xldm:member xlink:type=\"locator\" xlink:label=\""+membro1Name+"\" " +
    			    " xlink:href=\"xquery:db2-fn:xmlcolumn('WHOCARES.INFO')\" id=\""+membro1Name+"\"" +
    			    " xldm:memberRef=\""+membro1+"\" />";
    	xmlOpDef += "<xldm:member xlink:type=\"locator\" xlink:label=\""+membro2Name+"\" " +
				    " xlink:href=\"xquery:db2-fn:xmlcolumn('WHOCARES.INFO')\" id=\""+membro2Name+"\"" +
				    " xldm:memberRef=\""+membro2+"\" />";
    	// adicionando arcos para expressar os calculos a serem realizados - 4 operações + exponenciação e radiciação
    	if(op.equals("+") || op.equals("-")){
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/summation-item\"" +
    					" xlink:from=\""+opDefName+"\" xlink:to=\""+membro1Name+"\" weight=\"1.00\"/>";
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/summation-item\"" +
						" xlink:from=\""+opDefName+"\" xlink:to=\""+membro2Name+"\" weight=\""+(op.equals("-")?"-":"")+"1.00\"/>";
    	} else if(op.equals("*")) {
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/multiplication-item\"" +
    					" xlink:from=\""+opDefName+"\" xlink:to=\""+membro1Name+"\" weight=\""+membro2+"\"/>";
    	} else if(op.equals("/")){
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/numerator-item\"" +
    					" xlink:from=\""+opDefName+"\" xlink:to=\""+membro1Name+"\"/>";
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/denominator-item\"" +
						" xlink:from=\""+opDefName+"\" xlink:to=\""+membro2Name+"\"/>";
    	} else if(op.equals("^")) { // exponenciação
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/exponentiation-item\"" +
    					" xlink:from=\""+opDefName+"\" xlink:to=\""+membro1Name+"\" expoent=\""+membro2+"\"/>";
    	} else if(op.equals("¬")) { // radiciação
    		xmlOpDef += "<link:calculationArc xlink:type=\"arc\" xlink:arcrole=\"http://www.xbrl.org/2003/arcrole/nthroot-item\"" +
    					" xlink:from=\""+opDefName+"\" xlink:to=\""+membro1Name+"\" index=\""+membro2+"\"/>";
    	}
    	loadXmlOpDefContent();
    	String xmlContent = Parameters.xmlOpDefContent;
    	if(xmlContent.indexOf("xlink:from=\""+opDefName+"\"")!=-1) // já existe operator definition com o mesmo nome
    		return "OperatorDefinition "+opDefName+" already exists";
    	pos = xmlContent.indexOf("</link:calculationLink>");
    	if(pos==-1)
    		return "Invalid xml content for operator definition: no link:calculation tag";
    	xmlContent = xmlContent.substring(0,pos)+xmlOpDef+xmlContent.substring(pos);
    	return updateXmlOpDefContent(xmlContent);
	}

	private static String removeDoubleWhiteSpaces(String text) {
        //  Remove espaços brancos em excesso na expressão
    	text = text.replace("\n"," "); text = text.replace("\t"," "); // tirando tabs e quebras de linha
        String newText = ""; boolean isWhiteSpace = false;
        for(int i=0;i<text.length();i++) {
            String caracter = text.substring(i,i+1);
            if(caracter.equals(" ")&&isWhiteSpace) // espaço em branco duplicado
                caracter = "";
            else if(caracter.equals(" ")&&!isWhiteSpace) // primeiro espaço em branco
                isWhiteSpace = true;
            else // não é espaço em branco
                isWhiteSpace = false;
            newText = newText + caracter;
        }
        return newText.trim();
    }
	
	private static void loadXmlOpDefContent(){
		XLink.loadXpathplusConfigFile();
		String xmlRef = Database.getOpDefXMLFile(Parameters.xmlXPathPlusFileDoc);
		if(xmlRef!=null){
			Parameters.xmlOpDefContent = "";
			String db2Ref = xmlRef.substring(xmlRef.indexOf(":")+1,xmlRef.lastIndexOf("/"));
			try {
				Statement stmt = Database.getDB2ConnOpDef().createStatement();
			    ResultSet rs = stmt.executeQuery("xquery " + db2Ref); // executando query
			    while (rs.next())
			    	Parameters.xmlOpDefContent += rs.getString(1);
			} catch (Exception e) {
				System.out.println("Error while loading operatordefinition file: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private static String updateXmlOpDefContent(String xmlContent){
		try {
			String xmlRef = Database.getOpDefXMLFile(Parameters.xmlXPathPlusFileDoc);
			if(xmlRef==null)
				return "Error while loading operatordefinition expression";
			String db2Table = xmlRef.substring(xmlRef.indexOf("'")+1,xmlRef.lastIndexOf("."));
			String xmlField = xmlRef.substring(xmlRef.lastIndexOf(".")+1,xmlRef.lastIndexOf("'"));
			String sql = "UPDATE "+db2Table+" SET "+xmlField+"=?";
			PreparedStatement prepStmt = Database.getDB2ConnOpDef().prepareStatement(sql);
			prepStmt.setString(1,xmlContent);
			int execSQL = prepStmt.executeUpdate();
			if(execSQL==PreparedStatement.EXECUTE_FAILED)
				return "Error while updating operatordefinition data: "+sql;
			else
				return "OK";
		} catch (Exception e) {
			System.out.println("Error while updating operatordefinition data: "+e.getMessage());
			e.printStackTrace();
			return "Error while updating operatordefinition data: "+e.getMessage();
		}
	}

}
