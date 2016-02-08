package com.desenvolvatec.sql2xquery.xquery;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;


public class XQueryRunner {

    ArrayList<String> results = new ArrayList<String>();

    public static String[] runExist(String[] xquery,Collection coll) throws XMLDBException {
        ArrayList res = new ArrayList();
        XPathQueryService service = (XPathQueryService)coll.getService("XPathQueryService", "1.0");
        service.setProperty("indent", "no"); // sem indentação
        for(int i=1;i<xquery.length;i++) { // elemento 0 é parametrização
            ArrayList<String> resXquery = new ArrayList<String>();
            ResourceSet rs = service.query(xquery[i]); // executando xquery
            ResourceIterator ri = rs.getIterator();
            while (ri.hasMoreResources()) {
                Resource r = ri.nextResource();
                resXquery.add((String)r.getContent());                
            }
            res.add(resXquery); 
        }
        XQueryRunner xr = new XQueryRunner(res,xquery[0]);
        return xr.getResults().toArray(new String[xr.getResults().size()]);
    }

    public static String[] runDB2(String[] xquery,Connection conn) throws SQLException {
        ArrayList res = new ArrayList();
        Statement stmt = conn.createStatement();
        for(int i=1;i<xquery.length;i++) { // elemento 0 é parametrização
            ArrayList<String> resXquery = new ArrayList<String>();
            ResultSet rs = stmt.executeQuery("xquery " + xquery[i]); // executando query
            while (rs.next())
                resXquery.add(rs.getString(1));
            res.add(resXquery); 
        }
        XQueryRunner xr = new XQueryRunner(res,xquery[0]);
        return xr.getResults().toArray(new String[xr.getResults().size()]);
    }

    public static String[] runBerkeley(String[] xquery,XmlManager bkManager) throws XmlException {
        ArrayList res = new ArrayList();
        XmlQueryContext context = bkManager.createQueryContext();
        context.setEvaluationType(XmlQueryContext.Eager); // default, carrega tudo na memória
        for(int i=1;i<xquery.length;i++) { // elemento 0 é parametrização
            ArrayList<String> resXquery = new ArrayList<String>();
            XmlQueryExpression query = bkManager.prepare(xquery[i], context); // preparando query para execução
            XmlResults rs = query.execute(context); // executando query
            while (rs.hasNext())
                resXquery.add(rs.next().asString());
            res.add(resXquery); 
        }
        XQueryRunner xr = new XQueryRunner(res,xquery[0]);
        return xr.getResults().toArray(new String[xr.getResults().size()]);
    }

    public XQueryRunner(ArrayList results,String param) {
        if(results.size()==1)  // só tem um, vai ele mesmo
            this.results = paramApply((ArrayList<String>)results.get(0),param);
         else { // múltiplos 
            ArrayList<String> aResBase = (ArrayList<String>)results.get(0);
            for(int i=1;i<results.size();i++){
                ArrayList<String> aRes = (ArrayList<String>)results.get(i);
                ArrayList<String> aResRaiz = new ArrayList<String>();
                for(String resBase: aResBase){
                    for(String res: aRes){
                        if(resBase.trim().equals(res.trim())){
                            aResRaiz.add(resBase);
                            break;
                        }
                    }
                }
                aResBase = aResRaiz;
            }
            this.results = paramApply(aResBase,param);
        }
    }
    
    ArrayList<String> paramApply(ArrayList<String> result,String param){
        boolean distinctValuesPosRun = param.indexOf("%DV%")!=-1;
        if(distinctValuesPosRun){
            ArrayList<String> finalResult = new ArrayList<String>();
            for(String res: result){
                boolean found = false;
                for(String fRes: finalResult)
                    if(res.equals(fRes)){ 
                        found = true;
                        break;
                    }
                if(!found)
                    finalResult.add(res);
            }
            return finalResult;                
        } 
        return result; // se não tiver parametrização pos execução, vai direto
    }

    public ArrayList<String> getResults() {
        return results;
    }
}
