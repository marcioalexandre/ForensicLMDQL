package com.desenvolvatec.sql2xquery.xquery;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.FlworExpressionException;
import com.desenvolvatec.sql2xquery.exception.ImproperConfigFileException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;
import com.desenvolvatec.sql2xquery.sql.BetweenComp;
import com.desenvolvatec.sql2xquery.sql.ColumnItem;
import com.desenvolvatec.sql2xquery.sql.ColumnRef;
import com.desenvolvatec.sql2xquery.sql.FunctionItem;
import com.desenvolvatec.sql2xquery.sql.InComp;
import com.desenvolvatec.sql2xquery.sql.IsNullComp;
import com.desenvolvatec.sql2xquery.sql.OrderByClause;
import com.desenvolvatec.sql2xquery.sql.RelatComp;
import com.desenvolvatec.sql2xquery.sql.SQLQuery;
import com.desenvolvatec.sql2xquery.sql.SelectClause;
import com.desenvolvatec.sql2xquery.sql.SelectStmt;
import com.desenvolvatec.sql2xquery.sql.TableRef;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class XQueryGenerator {

    Document configDoc = null;
    SelectStmt selectStmt = null;
    ArrayList<String> aXQuery = new ArrayList<String>();
    ArrayList<Boolean> isComplexStructure = null;
    ArrayList<TableRef> usedTables = null;
    ArrayList<String> tableTags = null;
    ArrayList<String> namespaces = new ArrayList<String>();
    ArrayList<String> forExpression = new ArrayList<String>();
    ArrayList<String> letExpression = new ArrayList<String>();
    ArrayList<String> aggregateLetExpression = new ArrayList<String>();
    ArrayList<String> orderByExpression = new ArrayList<String>();
    ArrayList<String> returnExpression = new ArrayList<String>();
    XQWhere xqWhere = null;
    XQWhere xqWhereInitial = null;
    String whereAdjusted = null;

    public static String[] fromSQL(String query, Document configDoc) throws QueryExpressionException,ImproperConfigFileException,FlworExpressionException {
        ArrayList<SelectStmt> selects = new SQLQuery(query,false).getSelects();
        ArrayList<String> aXQuery = new ArrayList<String>();
        if ((selects.get(0).getGroupByClause()!=null && !hasAggregateFunctions(selects.get(0).getColumnItemList())) // group by sem função agregada ou select distinct
             || selects.get(0).getColumnItemList().getSelectType()==SelectClause.DISTINCT) // indica que tem que existir processamento/filtragem após a execução da xquery
            aXQuery.add("%DV%"); // primeiro elemento será sempre de parametrização - "distinct-values pos Run" neste caso
        else
            aXQuery.add("%%");
        for(int i=0;i<selects.size();i++) {
            XQueryGenerator xq = new XQueryGenerator(selects.get(i),configDoc);
            aXQuery.add(Util.arraylistToString(xq.getAXQuery(),"\n"));   
        }
        return aXQuery.toArray(new String[aXQuery.size()]);
    }
    
    XQueryGenerator(SelectStmt selectStatement, Document XmlConfig) throws ImproperConfigFileException,FlworExpressionException {
        this.selectStmt = selectStatement;
        this.configDoc = XmlConfig; // configDoc é o arquivo sql2query.xml utilizado para correspondências de tabelas e tags, dentre outras coisas
        this.usedTables = selectStmt.getFromClause().getTableRef(); // identifica tabelas usadas
        this.isComplexStructure = verifyComplexStructure(); // verifica se as tabelas envolvidas possuem estrutura complexa
        this.tableTags = tablesToTags(); // e seus respectivos tags XML
        buildXQWhere(); // constrói XQWhere
        if(this.selectStmt.getGroupByClause()!=null) { // com agrupamento (group by)
            ArrayList<ColumnRef> item = this.selectStmt.getGroupByClause().getColumnRef();
            for(int i=0;i<item.size();i++) // monta os distinct-values para cada item do group by
                buildDistinctValuesFor(item.get(i)); 
        } else { // montando query sem agrupamento (sem group by)
            // testa se é um função de agrupamento sem group by - tem que ser única e isolada
            ArrayList<ColumnItem> item = this.selectStmt.getColumnItemList().getColumnItem();
            if(item.size()==1 && item.get(0).getColumItemType() == ColumnItem.FUNCTION && // se é item único e se é função
                                 isAggregateFunction(item.get(0).getFunctionItem().getFunctionType())) // se é função agregada
                buildDistinctValuesFor(item.get(0));  // constrói for com distinct-values e let para função de agrupamento  (1 campo ou *)
            else // select, sem função de agrupamento, mesmo com DISTINCT (é tratado no DVPR)
                buildSingleFor(); // constrói um for simples, sem função de agrupamento
        }
        buildXmlns();
        buildColumnItemList();
        buildXPathFor(); // com For
        buildLetToWhereReferences();
        buildXPathLet(); // com Let
        buildWhereJoinConditions();
        buildOrderBy();
        buildReturn();
        adjustingFields();
        buildXQuery();
    }

    boolean isWhereForXPath(){ 
        // verifica se o where é possível de se encaixar no XPath do For (é tudo ou nada!)
        // ATENÇÃO:  apartir do xqWhereInitial - sem modificações
        if (this.xqWhereInitial==null)
            return false;
        ArrayList aCond = this.xqWhereInitial.getConditItemList();
        for(int i=0;i<aCond.size();i++){
            Object cond = aCond.get(i);
            if(cond instanceof String && (((String)cond).equals("not") || ((String)cond).equals("or"))) // existe NOT ou um OR
                return false;
        }
        return true;
    }


    void adjustingFields() throws FlworExpressionException {
        // Ajusta campos (fields) de acordo com sua definição no arquivo de configração (se necessário)
        // Procura em expressões For, Let, Where e OrderBy, depois delas prontas com regras padrões
        boolean hasComplex = false;
        for(Boolean isComplex: this.isComplexStructure){
            if(isComplex) {
                hasComplex=true;
                break;
            }
        }
        if(!hasComplex) return;
        // existem estruturas complexas de definição de tabelas
        for(int i=0;i<this.usedTables.size();i++){ // procura por tabela de estrutura complexa
            if(this.isComplexStructure.get(i)) {// é complexa
                String tableName = this.usedTables.get(i).getTableName().trim();
                String tag = this.tableTags.get(i).trim();
                
                // For expressions
                for(int j=0;j<this.forExpression.size();j++){
                    String exp = this.forExpression.get(j).trim();
                    if(exp.indexOf(tag)!=-1 || exp.indexOf("$"+tableName+"/")!=-1) { // existe a tag ou referencia neste For 
                        // verifica se a expressão in do For possui referência de campo
                        int beforeField = exp.indexOf("]/")!=-1?(exp.indexOf("]/")+2):(exp.indexOf(tag+"/")!=-1?exp.indexOf(tag+"/")+(tag+"/").length():-1);
                        int afterField = exp.indexOf(")",beforeField)!=-1?exp.indexOf(")",beforeField):exp.length();
                        if(beforeField>-1 && afterField>beforeField) // existe referência de campo na expressão For
                            exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                        // tratamento do xpath do For
                        int posChave = exp.indexOf("[");
                        if(posChave!=-1) {// existe expressão xpath, verifica a tabela corrente
                            if(exp.indexOf(tag)>-1 && exp.indexOf(tag)<posChave){ // é a tabela corrente (não tem $table)
                                int pos = posChave;
                                if(exp.substring(posChave+1,posChave+2).equals("$")){
                                    do {
                                        pos = exp.indexOf(" ",pos+1);
                                    } while(pos!=-1 && (
                                            exp.substring(pos+1,pos+2).equals("=") || 
                                            exp.substring(pos+1,pos+2).equals(">") ||
                                            exp.substring(pos+1,pos+2).equals("<") ||
                                            exp.substring(pos+1,pos+2).equals("!") ||
                                            exp.substring(pos+1,pos+2).equals("$")));
                                }
                                while(pos!=-1){
                                    beforeField = pos+1;
                                    afterField = exp.indexOf(" ",beforeField);  // termina sempre com espaço 
                                    if(beforeField==-1 || afterField==-1){ // se achou algum desses, termina
                                        pos = -1;
                                        continue;
                                    }
                                    if(!exp.substring(beforeField,afterField).equalsIgnoreCase("and") &&
                                            !exp.substring(beforeField,afterField).equalsIgnoreCase("or") &&
                                            !exp.substring(beforeField,afterField).equalsIgnoreCase("not"))  // ignora and, or e not
                                        exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                                    do {
                                        pos = exp.indexOf(" ",pos+1);
                                    } while(pos!=-1 && (
                                            exp.substring(pos+1,pos+2).equals("=") || 
                                            exp.substring(pos+1,pos+2).equals(">") ||
                                            exp.substring(pos+1,pos+2).equals("<") ||
                                            exp.substring(pos+1,pos+2).equals("!") ||
                                            exp.substring(pos+1,pos+2).equals("$")));
                                }
                            } else {// não é tabela corrente    
                                for(int pos=exp.indexOf("$"+tableName+"/",posChave);pos!=-1;){
                                    beforeField = pos+("$"+tableName+"/").length();
                                    afterField = exp.indexOf(" ",pos)!=-1?exp.indexOf(" ",pos):exp.indexOf("]",pos);  // termina com espaço ou "]"
                                    exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                                    pos = exp.indexOf("$"+tableName+"/",afterField);                                    
                                }
                            }
                        } // existe expressão xpath
                    } // se existe a tag ou referência neste For
                    if(!exp.equals(this.forExpression.get(j).trim())) // houve alteração
                        this.forExpression.set(j,exp);
                } // fim forExpression
                
                // Let expressions
                for(int j=0;j<this.letExpression.size();j++){
                    String exp = this.letExpression.get(j).trim();
                    if(exp.indexOf(tag)!=-1 || exp.indexOf("$"+tableName+"/")!=-1) { // existe a tag ou referencia neste Let 
                        // verifica se a expressão de atribuição do Let possui referência de campo fora dos []
                        // verificando com Tag
                        int beforeField = exp.indexOf("]/")!=-1?(exp.indexOf("]/")+2):(exp.indexOf(tag+"/")!=-1?exp.indexOf(tag+"/")+(tag+"/").length():-1);
                        int afterField = exp.indexOf(")",beforeField)!=-1?exp.indexOf(")",beforeField):exp.length();
                        if(beforeField>-1 && afterField>beforeField) // existe referência de campo na expressão Let
                            exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                        //verificando sem Tag ($tableName)   
                        int pos = exp.indexOf("$"+tableName+"/");
                        beforeField = pos + (pos!=-1?("$"+tableName+"/").length():0);
                        afterField = exp.indexOf(" ",pos)!=-1?exp.indexOf(" ",pos):exp.indexOf(")",pos);  // termina com espaço ou ")"
                         if(beforeField>-1 && afterField>beforeField) // existe referência de campo na expressão Let
                             exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                        // tratamento do xpath do Let
                        int posChave = exp.indexOf("[");
                        if(posChave!=-1) {// existe expressão xpath, verifica a tabela corrente
                            if(exp.indexOf(tag)>-1 && exp.indexOf(tag)<posChave){ // é a tabela corrente (não tem $table)
                                pos = posChave;
                                if(exp.substring(posChave+1,posChave+2).equals("$")){
                                    do {
                                        pos = exp.indexOf(" ",pos+1);
                                    } while(pos!=-1 && (
                                            exp.substring(pos+1,pos+2).equals("=") || 
                                            exp.substring(pos+1,pos+2).equals(">") ||
                                            exp.substring(pos+1,pos+2).equals("<") ||
                                            exp.substring(pos+1,pos+2).equals("!") ||
                                            exp.substring(pos+1,pos+2).equals("$")));
                                }
                                while(pos!=-1){
                                    beforeField = pos+1;
                                    afterField = exp.indexOf(" ",beforeField);  // termina sempre com espaço 
                                    if(beforeField==-1 || afterField==-1){ // se achou algum desses, termina
                                        pos = -1;
                                        continue;
                                    }
                                    if(!exp.substring(beforeField,afterField).equalsIgnoreCase("and") &&
                                            !exp.substring(beforeField,afterField).equalsIgnoreCase("or") &&
                                            !exp.substring(beforeField,afterField).equalsIgnoreCase("not"))  // ignora and, or e not
                                        exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                                    do {
                                        pos = exp.indexOf(" ",pos+1);
                                    } while(pos!=-1 && (
                                            exp.substring(pos+1,pos+2).equals("=") || 
                                            exp.substring(pos+1,pos+2).equals(">") ||
                                            exp.substring(pos+1,pos+2).equals("<") ||
                                            exp.substring(pos+1,pos+2).equals("!") ||
                                            exp.substring(pos+1,pos+2).equals("$")));
                                }
                            } else {// não é tabela corrente    
                                for(pos=exp.indexOf("$"+tableName+"/",posChave);pos!=-1;){
                                    beforeField = pos+("$"+tableName+"/").length();
                                    afterField = exp.indexOf(" ",pos)!=-1?exp.indexOf(" ",pos):exp.indexOf("]",pos);  // termina com espaço ou "]"
                                    exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                                    pos = exp.indexOf("$"+tableName+"/",afterField);                                    
                                }
                            }
                        } // existe expressão xpath
                    } // se existe a tag ou referência neste Let
                    if(!exp.equals(this.letExpression.get(j).trim())) // houve alteração
                        this.letExpression.set(j,exp);
                } // fim letExpression
                
                // Where expression (whereAdjusted)
                if(this.xqWhere!=null){
                    String exp = ("where "+getWhereExpression(this.xqWhere)).trim(); // todos que estão lá no where
                    if(exp.indexOf("$"+tableName+"/")!=-1) { // existe a referencia no Where 
                        // verifica (e troca) se a expressão Where possui referência de campo ($tableName)
                         for(int pos=exp.indexOf("$"+tableName+"/");pos!=-1;){
                             int beforeField = pos+("$"+tableName+"/").length();
                             int afterField = exp.indexOf(" ",pos)!=-1?exp.indexOf(" ",pos):exp.length();  // termina com espaço ou vai até o fim
                             exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                             pos = exp.indexOf("$"+tableName+"/",afterField);                                    
                         }
                    } // se existe a referência neste OrderBy
                    if(!exp.equals(("where "+getWhereExpression(this.xqWhere)).trim())) // houve alteração
                        this.whereAdjusted = exp.trim();
                } // fim whereAjusted

                // OrderBy expressions
                for(int j=0;j<this.orderByExpression.size();j++){
                    String exp = this.orderByExpression.get(j).trim();
                    if(exp.indexOf("$"+tableName+"/")!=-1) { // existe a referencia neste OrderBy 
                        // verifica se a expressão do OrderBy possui referência de campo ($tableName)
                         for(int pos=exp.indexOf("$"+tableName+"/");pos!=-1;){
                             int beforeField = pos+("$"+tableName+"/").length();
                             int afterField = exp.indexOf(" ",pos);  // termina com espaço antes de ascending/descending
                             exp = changeFieldByRefer(tableName,exp,beforeField,afterField); // ajustando a expressão com a referência correta
                             pos = exp.indexOf("$"+tableName+"/",afterField);                                    
                         }
                    } // se existe a referência neste OrderBy
                    if(!exp.equals(this.orderByExpression.get(j).trim())) // houve alteração
                        this.orderByExpression.set(j,exp);
                } // fim orderByExpression
                
            } // fim do se é complexa
        } // varredura de tabelas
    }

    String changeFieldByRefer(String tableName,String expression,int before,int after) {
        if(after<before || after<0 || before<0)
            return expression;
        String fieldRef = null; 
        try {
            fieldRef = fieldRefer(tableName,expression.substring(before,after)); // capturando a referência
            if(fieldRef.length()==0) // string vazia, é o valor da tag (tirar o "/" que antecede, diminuindo o before)
                before--;
            return expression.substring(0,before) + fieldRef + expression.substring(after); // ajustando a referência
        } catch (ImproperConfigFileException e) {
            return expression; // se não estiver definido retorna a expressão original, sem mudança
        }
    }
    
    String fieldRefer(String tableName,String fieldName) throws ImproperConfigFileException {
        NodeList nlEnt = this.configDoc.getElementsByTagName("entities"); 
        Element tagEnt = (Element)nlEnt.item(0); // só existe uma tag entities
        NodeList children = tagEnt.getElementsByTagName("entity"); // lista nodes de tags filhas de elementos entity
        for(int i=0;i<children.getLength();i++){
            Element child = (Element)children.item(i);
            if(child.getAttribute("tableName").equalsIgnoreCase(tableName)) {// procurando a tabela
                NodeList fields = child.getElementsByTagName("field"); // verifica se possui filhos (definição de fields)
                for(int j=0;j<fields.getLength();j++){
                    Element field = (Element)fields.item(j);
                    if(field.getAttribute("name").equalsIgnoreCase(fieldName))
                        return field.getAttribute("value");
                }
            }
        }
       throw new ImproperConfigFileException("Field name "+fieldName+"("+tableName+") not found in config file"); // se não achou
    }

    void buildXPathFor() throws FlworExpressionException {
        // Chama buildPath para For
        buildXPath(0);
    }

    void buildXPathLet() throws FlworExpressionException {
        // Chama buildPath para Let
        buildXPath(1);
    }

    private void buildXPath(int op) throws FlworExpressionException {
        // coloca condição nos xpath dos For (0) ou Let (1)
        if(isWhereForXPath()){ // apenas se só tiver "and"
            if(op==0) { // For (primeira vez)
                // limpando strings e parenteses do xqWhere, proveniente do SQL
                for(int i=0;this.xqWhere!=null && i<this.xqWhere.getConditItemList().size();i++){ // removendo todas as strings (só tem parenteses e AND)
                    if(this.xqWhere.getConditItemList().get(i) instanceof String){
                        this.xqWhere.inUse.remove(i);
                        this.xqWhere.getConditItemList().remove(i--); // removendo e voltando, pois o removido vai embora e o próximo passa a ser o atual
                    }
                }
                // se ficar "limpo" - nao é para acontecer - coloca null
                if(this.xqWhere!=null && this.xqWhere.getConditItemList().size()==0)
                    this.xqWhere = null;;
                // localiza a origem das referências (as tags), onde vai colocar os XPath  
                for(int t=0;t<this.tableTags.size();t++){
                    // apenas procura a tag no For - onde define dos group by e dos from
                    for(int f=0;f<this.forExpression.size();f++){
                        String foR = this.forExpression.get(f);
                        if((foR.indexOf(tableTags.get(t)))!=-1) // achou
                            mountXPath(this.forExpression,f,tableTags.get(t)); // tenta montar o xpath
                        
                    }
                }
            } else { // Let (segunda vez)
                // ordenando os letExpression de acordo com as necessidades de aggregateLetExpression (uso da tabela no aggregateLetExpression joga o respectivo let para o final do letExpression)
                for(String aggLet: this.aggregateLetExpression){
                    String tableName = aggLet.substring(aggLet.indexOf("$",5)+1,aggLet.indexOf("/")); // 5 é devido ao let $ no início, pots tem que procurar o segundo $
                    for(int k=0;k<this.letExpression.size()-1;k++){ // vai até um antes do último
                        String let = this.letExpression.get(k);
                        if(let.indexOf("$"+tableName+" ")!=-1){ // achou definição de let de tabela que está na função de agreagamento
                            this.letExpression.add(let); // colocando no final
                            this.letExpression.remove(k--); // removendo a original que já tinha sido colocado no final
                        }
                    }
                }
                // adicionando o aggregateLetExpression ao letExpression
                for(String aggLet: this.aggregateLetExpression)
                    this.letExpression.add(aggLet);
                this.aggregateLetExpression = null; // não vai usar mais
                // procurando a tag no Let
                for(int t=0;t<this.tableTags.size();t++){
                    // apenas procura a tag no Let - onde define dos group by e dos from
                    for(int f=0;f<this.letExpression.size();f++){
                        if((this.letExpression.get(f).indexOf(tableTags.get(t)))!=-1) // achou
                            mountXPath(this.letExpression,f,tableTags.get(t)); // garante que achou
                    }
                }
                // preparando novo XQWhere com as condições que sobraram - que não foram para o XPath
                if(this.xqWhere!=null){
                    ArrayList conds = new ArrayList();
                    ArrayList<Boolean> condsInUse = new ArrayList<Boolean>();
                    for(int i=0;i<this.xqWhere.getConditItemList().size();i++){
                        if(!this.xqWhere.inUse.get(i)) {// se não foi utilizado
                            conds.add("and"); condsInUse.add(false); // lembrando que é isWhereForXPath
                            conds.add(this.xqWhere.getConditItemList().get(i)); 
                            condsInUse.add(this.xqWhere.inUse.get(i)); // sempre falso
                        }
                    }
                    if(conds.size()==0)
                        this.xqWhere = null;
                    else {
                        conds.remove(0); condsInUse.remove(0); // removendo o primeiro "and"
                        this.xqWhere.setConditItemList(conds);
                        this.xqWhere.inUse = condsInUse;
                    }
                }
            }
        }
    }

    void mountXPath(ArrayList<String> expressions, int index, String tag) throws FlworExpressionException {
        // monta o xpath no expressions (Let ou For)
        String expression = expressions.get(index);
        boolean create = expression.indexOf(tag+"[")==-1;
        String tableName = tagToTableLoaded(tag).trim();
        for(int i=0;this.xqWhere!=null && i<this.xqWhere.getConditItemList().size();i++){ 
            String cond = mountConditExpression(this.xqWhere.getConditItemList().get(i)).trim(); // montando a condição
            if(cond.indexOf("$"+tableName+"/")!=-1){// achou referência a tabela
                // verifica se existe outra tabela refereniada e se ela já existe referência em expressions
                int pos=0; String otherTable = null; boolean found = false;
                for(pos=0;pos!=-1 && cond.indexOf("$",pos)==cond.indexOf("$"+tableName+"/");pos=cond.indexOf("$",pos+1));
                if(pos!=-1) // achou outra tabela
                    otherTable = cond.substring(pos+1,cond.indexOf("/",pos));
                for(int j=0;j<index && !found;j++){
                    if(expressions.get(j).indexOf("$"+otherTable+"/")!=-1 || 
                       expressions.get(j).indexOf("$"+otherTable+" ")!=-1) 
                        found = true;
                }
                // se a outra tabela já é referenciada, ou não existe outra tabela, inclui a condição
                if(otherTable==null || found){
                    pos = create?expression.indexOf(tag)+tag.length():expression.indexOf("]"); // posição a inserir
                    cond = cond.replace("$"+tableName+"/","").trim(); // preparando a referência para o xpath - coloca em branco a referência à tabela da expressão For/Let vigente
                    if(cond.substring(0,1).equals("$")) // vem a referência de outra tabela antes da atual - no eXist isso dá pau!!!
                        cond = invertTableRef(cond);
                    expression = expression.substring(0,pos)+(create?"[":" and ")+cond+(create?"]":"")+expression.substring(pos); // inserindo a expressão como xpath 
                    expressions.set(index,expression); 
                    this.xqWhere.inUse.set(i,true); // marcando a condição como usada no xpath
                    create = false;
                }
            } 
        }
    }
    
    String invertTableRef(String cond){
        // localiza o campo sem referência explícita, o que indica que ele é da tabela atual
        // sendo da tabela atual, ele deve vir na frente de outro campo com referência,
        // o que significa que a condição deve ser invertida
        // --> só pode aocntecer estes casos para condição originada de objetos RelatComp
        cond = cond.trim();
        String leftSide = cond.substring(0,cond.indexOf(" ")).trim(); // espaço é utilizado como separador na expressão 
        String rightSide = cond.substring(cond.lastIndexOf(" ")+1).trim();
        String operator = cond.replace(leftSide,"").replace(rightSide,"").trim();
        // verificando se pode sofrer inversão se <, <=, > ou >=
        if(operator.substring(0,1).equals("<"))
            operator = ">"+operator.substring(1);
        else if(operator.substring(0,1).equals(">"))
            operator = "<"+operator.substring(1);
        return rightSide+" "+operator+" "+leftSide;
    }
    
    ArrayList<String> tablesToTags() throws ImproperConfigFileException {
        ArrayList<String> tags = new ArrayList<String>();
        for(int i=0;i<this.usedTables.size();i++) {
            String tagName = tableToTag(this.usedTables.get(i).getTableName());
            if(tagName!=null && !tagName.trim().equals("")) // tag válida
                tags.add(tagName.trim());
            else
                throw new ImproperConfigFileException("Table name not properly defined: "+this.usedTables.get(i).getTableName());
        }
        return tags;
    }
    
    ArrayList<Boolean> verifyComplexStructure() throws ImproperConfigFileException {
        ArrayList<Boolean> complex = new ArrayList<Boolean>();
        for(int i=0;i<this.usedTables.size();i++) {
            Boolean cond = isComplexStructTable(this.usedTables.get(i).getTableName());
            if(cond!=null) // tabela processada
                complex.add(cond);
            else
                throw new ImproperConfigFileException("Table name not properly defined: "+this.usedTables.get(i).getTableName());
        }
        return complex;
    }

    void buildSingleFor() {
        // preparando o For do FLWOR para as tabelas/tags envolvidas
        for(int i=0;i<this.usedTables.size();i++)
            addSingleFor(this.usedTables.get(i).getTableName(),this.tableTags.get(i));
    }

    void addSingleFor(String tableName,String tagName){
        this.forExpression.add("for $"+tableName+" in "+tagName);
    }
    
    void buildDistinctValuesFor(ColumnItem item) throws FlworExpressionException {
        // adiciona For e Let de acordo com o item, que é uma função de agrupamento - SEM GROUP BY
        FunctionItem func = item.getFunctionItem();
        String funcName = functionName(func.getFunctionType());
        boolean distinct = func.getSelectType()==SelectClause.DISTINCT;
        if(!isAggregateFunction(func.getFunctionType())) // funções não de agrupamento
            throw new FlworExpressionException("Aggregate function do not recognized: "+funcName);
        String aliasName = item.getColumnAlias()==null?funcName:item.getColumnAlias();
        if(func.isAsterisk())  // se é *, só deve ter apenas uma tabela no FromClause
            addAggregateLet(aliasName,this.tableTags.get(0),null,null,distinct?")":null,funcName+(distinct?"(distinct-values":""),false);
        else { // só tem um campo
            String columnNameFunc = func.getColumnRef().getColumnName();
            String tagName = tableToTagLoaded(func.getColumnRef().getTableName());
            addAggregateLet(aliasName,tagName,null,null,columnNameFunc+(distinct?")":""),funcName+(distinct?"(distinct-values":""),false);
        }
    }
    
    void buildDistinctValuesFor(ColumnRef col) throws FlworExpressionException {
       // adiciona só o for distinct-values com o campo desejado - colunas do GROUP BY
        String tableName = col.getTableName();
        String columnNameDistinct = col.getColumnName();
        String tagName = tableToTagLoaded(tableName);
        addDistinctValuesFor(tableName,tagName,columnNameDistinct,columnRefToAlias(col));
    }

    String buildAggregateLet(ColumnItem item,boolean returnValue) throws FlworExpressionException {
        // adiciona Let de acordo com o item, considerando que ele é função agregada
        String let = null;
        if(item.getColumItemType()!=ColumnItem.FUNCTION)
            throw new FlworExpressionException("Aggregate function do not recognized: "+item.getColumnRef().getTableName()+"."+item.getColumnRef().getColumnName());
        FunctionItem func = item.getFunctionItem();
        String funcName = functionName(func.getFunctionType());
        if(!isAggregateFunction(func.getFunctionType())) // funções não de agrupamento
            throw new FlworExpressionException("Aggregate function do not recognized: "+funcName);
        String tableName = null,aliasName = item.getColumnAlias(); boolean letAdded = false;
        if(aliasName==null) aliasName = funcName;
        if(func.isAsterisk()) { // se é *, só pode ser COUNT - considera apenas uma tabela
            tableName = this.usedTables.get(0).getTableName();
            for(int i=0;i<this.selectStmt.getGroupByClause().getColumnRef().size();i++){
                ColumnRef col = this.selectStmt.getGroupByClause().getColumnRef().get(i);
                if(col.getTableName().equals(tableName)) { // obtendo o campo do distinct no group by
                    let = addAggregateLet(aliasName,this.tableTags.get(0),col.getColumnName(),columnRefToAlias(col),null,funcName,true);
                    letAdded = true; 
                }
            }
        } else { // só tem um campo
            if(func.getConstValue()!=null) {// verifica se pode ser uma constante
                let = addAggregateLet(aliasName,null,null,null,(String)func.getConstValue(),funcName,true);
                letAdded = true; 
            } else { // é coluna
                // procura se a tabela da coluna possui campo que está no group by
                tableName = func.getColumnRef().getTableName();
                String columnNameFunc = func.getColumnRef().getColumnName();
                String tagName = tableToTagLoaded(tableName);
                for(int j=0;j<this.selectStmt.getGroupByClause().getColumnRef().size() && !letAdded;j++) { // procura dentro do group by
                    ColumnRef col = this.selectStmt.getGroupByClause().getColumnRef().get(j);
                    if(col.getTableName().equals(tableName)) { // obtendo o campo do distinct no group by
                        let = addAggregateLet(aliasName,tagName,col.getColumnName(),columnRefToAlias(col),columnNameFunc,funcName,true);
                        letAdded = true; 
                    }
                }
                if(!letAdded) {
                    // coluna dentro de função agregada que não está no group by
                    int pos = -1; int j=0;
                    for(;j<this.forExpression.size()&&pos==-1;j++) 
                         pos = this.forExpression.get(j).indexOf("/"+tableName+"/"); // se pos>-1, a tabela está já está nos For fazendo referência a campos
                    if(pos==-1){ // não achou nos For como referência
                        let = addAggregateLet(aliasName,"$"+tableName,null,null,columnNameFunc,funcName,true);
                        letAdded = true;
                    } else {
                        // determinando o campo de referência
                        String fieldName = this.forExpression.get(--j).substring(pos+tableName.length()+2);
                        int endPosition1 = fieldName.indexOf(" "); int endPosition2 = fieldName.indexOf(")");
                        int endPosition = endPosition1>endPosition2?endPosition1:endPosition2;
                        if(endPosition!=-1)
                            fieldName = fieldName.substring(0,endPosition);
                        fieldName = fieldName.trim();
                        // determinando o alias
                        pos = this.forExpression.get(j).indexOf("for $"); // tem que existir
                        if(pos!=-1){
                            String fieldAliasName = this.forExpression.get(j).substring(pos+5);
                            fieldAliasName = fieldAliasName.substring(0,fieldAliasName.indexOf(" in ")).trim();
                            let = addAggregateLet(aliasName,tagName,fieldName,fieldAliasName,columnNameFunc,funcName,true);
                            letAdded = true;
                        }
                    }
                }
            }
        }
        if(returnValue) return let;
        if(!letAdded)
            throw new FlworExpressionException("Invalid table name to build Let expression: "+tableName);
        return null;
    }

    void addDistinctValuesFor(String tableName,String tagName,String columnNameDistinct,String aliasColumnNameDistinct) throws FlworExpressionException {
        // adiciona o For do campo específico e Let da tabela, caso necessário
        this.forExpression.add("for $"+(aliasColumnNameDistinct!=null?aliasColumnNameDistinct:columnNameDistinct)+" in distinct-values("+tagName+
                                 (columnNameDistinct==null?"":("/"+columnNameDistinct))+")");
        String whereExpression = null;
        if(this.xqWhereInitial!=null)
            whereExpression = getWhereExpression(xqWhereInitial); // todas as condições
        if(whereExpression!=null && whereExpression.indexOf("$"+tableName+"/")!=-1){ // tem referência no Where - apenas para construir o Let
            // verificando se já não existe um Let antes para essa tabela
            for(int i=0;i<this.letExpression.size();i++){
                String let = this.letExpression.get(i);
                int pos = let.indexOf("let $"+tableName+" ");
                if(pos!=-1){ // se existe, verifica se já tem condição
                    pos = let.indexOf("]"); 
                    if(pos!=-1) // se já tem condição, adiciona nova condição às existentes
                        let = let.substring(0,pos)+(columnNameDistinct==null?"":(" and "+columnNameDistinct+"=$"+(aliasColumnNameDistinct==null?columnNameDistinct:aliasColumnNameDistinct)))+let.substring(pos);
                    else // se não tem condição ainda, adiciona esta primeira
                        let = let.trim() + (columnNameDistinct==null?"":("["+columnNameDistinct+"=$"+(aliasColumnNameDistinct==null?columnNameDistinct:aliasColumnNameDistinct)+"]"));
                    this.letExpression.set(i,let); // alterando
                    return;
                }
            }
            // se não existir ainda um Let para a tabela
            this.letExpression.add("let $"+tableName+" := "+tagName+ // adiciona o Let da tabela concatenando com o campo específico - visa atendee comparações no Where
                                     (columnNameDistinct==null?"":
                                      ("["+columnNameDistinct+" = "+"$"+
                                       (aliasColumnNameDistinct!=null?aliasColumnNameDistinct:columnNameDistinct)+"]")));
        }
    }

    String addAggregateLet(String aliasName,String tagName,String columnNameDistinct,String aliasColumnNameDistinct,String columnNameFunc,String funcName,boolean returnValue){
        String let = "let $"+aliasName+" := "+(funcName==null?"":(funcName + "("))+(tagName==null?"":tagName)+
                         (columnNameDistinct==null?"":("["+columnNameDistinct+" = "+"$"+(aliasColumnNameDistinct!=null?aliasColumnNameDistinct:columnNameDistinct)+"]"))+
                         (columnNameFunc==null?"":((columnNameDistinct==null&&tagName==null?"":"/")+columnNameFunc))
                        + (funcName==null?"":")");
        if(returnValue)
            return let;
        else
            this.letExpression.add(let);
        return null;
    }
    
    void addSingleLet(String aliasName,String expression){
        this.letExpression.add("let $"+aliasName+" := "+expression);
    }

    String functionName(int fName) throws FlworExpressionException {
        switch(fName) {
            case 0: return "sum"; // FunctionItem.SUM
            case 1: return "avg"; // FunctionItem.AVG
            case 2: return "count"; // FunctionItem.COUNT
            case 3: return "max"; // FunctionItem.MAX
            case 4: return "min"; // FunctionItem.MIN
            case 5: return "upper-case"; // FunctionItem.UPPER
            case 6: return "lower-case"; // FunctionItem.LOWER
            case 7: return "isnull"; //FunctionItem.ISNULL
        }
        throw new FlworExpressionException("Invalid function name (reference value must be between 0 and 7):"+fName);
    }

    void buildXQWhere() {
        // contruindo o objeto XQWhere
        ArrayList wCond=null,hCond=null;
        if (this.selectStmt.getWhereClause()!=null)
            wCond = this.selectStmt.getWhereClause().getConditItemList();
        if (this.selectStmt.getHavingClause()!=null)
            hCond = this.selectStmt.getHavingClause().getConditItemList();
        // Adicionando no XQWhere
        if(wCond!=null || hCond!=null) { // existe condição
            this.xqWhere = new XQWhere();
            this.xqWhereInitial = new XQWhere(); // atualiza da mesma forma e nãos e altera mais - padrão inicial/original
            if(wCond!=null) // existe where
                for(Object obj: wCond){
                    this.xqWhere.getConditItemList().add(obj);
                    this.xqWhere.inUse.add(false);
                    this.xqWhereInitial.getConditItemList().add(obj);
                    this.xqWhereInitial.inUse.add(false);
                }
            if(this.selectStmt.getGroupByClause()!=null && hCond!=null) {// com agrupamento e having - não existe having sem agrupamento
                this.xqWhere.getConditItemList().add("and"); // concatenando having com where
                this.xqWhere.inUse.add(false); // sincronizando arraysList
                this.xqWhereInitial.getConditItemList().add("and");
                this.xqWhereInitial.inUse.add(false);
                for(Object obj: hCond){
                    this.xqWhere.getConditItemList().add(obj);
                    this.xqWhere.inUse.add(false);
                    this.xqWhereInitial.getConditItemList().add(obj);
                    this.xqWhereInitial.inUse.add(false);
                }
            }
        } else {
            this.xqWhere = null;
            this.xqWhereInitial = null;
        }
    }
    
   String getWhereExpression(XQWhere xqw) throws FlworExpressionException {
        // monta a expressão Where do FLWOR a partir do XQWhere
        String where = "";
        for(int i=0;xqw!=null && i<xqw.getConditItemList().size();i++){
            Object cond = xqw.getConditItemList().get(i);
            if(!xqw.inUse.get(i)) // apenas as que NÃO estiverem em uso
                where += " " + mountConditExpression(cond);
        }
        if(where.trim().length()==0)
            return null;
        else
            return where.trim();
    }
    
    String mountConditExpression(Object cond) throws FlworExpressionException {
        if(cond instanceof String)
            return (String)cond;
        else if(cond instanceof RelatComp) {
            return relatCompExpression((RelatComp)cond);
        } else if(cond instanceof BetweenComp) {
            return betweenCompExpression((BetweenComp)cond);
        } else if(cond instanceof InComp) {
            return inCompExpression((InComp)cond);
        } else if(cond instanceof IsNullComp) {
            return isNullCompExpression((IsNullComp)cond);
        } else if(cond instanceof XQJoinCondition) {
            return joinConditionExpression((XQJoinCondition)cond);
        } else 
            throw new FlworExpressionException("Can not build Where with this condition: "+cond.toString());
    }
    
    String joinConditionExpression(XQJoinCondition jc) {
        if(!jc.notOperator) // mais comum
            return jc.tableName;
        else
            return "not "+jc.tableName;
    }
   
    String isNullCompExpression(IsNullComp inc) throws FlworExpressionException {
        // Considera o "is null" como o campo/tag não aparecendo no elemento pai (não é tag vazia)
        String exp = columnItemExpression(inc.getColumnItem(),false);
        if (inc.isNotOperator())
            exp = "(not(" + exp + "))";
        return exp;
    }

    String inCompExpression(InComp ic) throws FlworExpressionException {
        String exp = columnItemExpression(ic.getColumnItem(),false) + " = (" +
                     (String)ic.getConstValue().get(0); // sempre vai existir pelo menos um elemento
        for(int i=1;i<ic.getConstValue().size();i++) 
            exp += "," + (String)ic.getConstValue().get(i);
        exp += ")";
        if (ic.isNotOperator())
            exp = "(not(" + exp + "))";
        return exp;
    }

    String betweenCompExpression(BetweenComp bc) throws FlworExpressionException {
        // column item
        String var = columnItemExpression(bc.getColumnItem(),false);
        // valores
        String val1 = (String)bc.getConstValue1(); String val2 = (String)bc.getConstValue2();
        String exp = "(" + var + " >= " + val1 + " and " + var + " <= " + val2 + ")";
        if (bc.isNotOperator())
            exp = "(not" + exp + ")";
        return exp;
    }

    String relatCompExpression(RelatComp rc) throws FlworExpressionException {
        // left side
        String exp = columnItemExpression(rc.getLeftColumnItem(),false);
        // operator
        exp += condOperExpression(rc.getRelatOper());
        // right side
        if(rc.getRightColumnItem()==null) // é valor constante
            exp += (String)rc.getConstValue();
        else // é coluna ou função
            exp += columnItemExpression(rc.getRightColumnItem(),false);
        return exp;
    }
    
    String columnItemExpression(ColumnItem item,boolean orderBy) throws FlworExpressionException {
        if(item.getColumItemType()==ColumnItem.COLUMN) // é coluna
            return columnExpression(item.getColumnRef(),orderBy);
        else // é função
            return functionExpression(item.getFunctionItem(),orderBy);
    }
    
    String columnExpression(ColumnRef column,boolean orderBy) throws FlworExpressionException {
        // se é na construção do order by, tem que olhar se é group by - se sim, existe previamente $nome_do_alias_do_campo
        boolean groupBy = selectStmt.getGroupByClause()!=null;
        return "$"+((orderBy && groupBy)?columnRefToAlias(column):(column.getTableName()+"/"+column.getColumnName()));
    }
    
    String condOperExpression(int operator) throws FlworExpressionException {
        switch(operator) {
            case 0: return " = "; // RelatComp.EQUALS - eq só para mesmo tipo
            case 1: return " != "; // RelatComp.NOT_EQUALS - ne só para mesmo tip
            case 2: return " < "; // RelatComp.LESS - lt só para mesmo tip
            case 3: return " <= "; // RelatComp.LESS_OR_EQUALS - le só para mesmo tip
            case 4: return " > "; // RelatComp.GREATER - gt só para mesmo tip
            case 5: return " >= "; // RelatComp.GREATER_OR_EQUALS - ge só para mesmo tip
        }
        throw new FlworExpressionException("Invalid conditional operator (reference value must be between 0 and 5):"+operator);
    }

    String functionExpression(FunctionItem f,boolean orderBy) throws FlworExpressionException {
        // Apenas as funções predeterminadas:
        // “SUM” | “AVG” | “COUNT” | “MAX” | “MIN”  - estes apenas para agregagação/agrupamento, mas podem aparecer sem group by
        // e “UPPER” | “LOWER” | “ISNULL” - isnull só no mysql
        if(f.getFunctionType()==FunctionItem.UPPER) 
            return "upper-case(" + ((f.getColumnRef()!=null)?columnExpression(f.getColumnRef(),orderBy):(String)f.getConstValue()) + ")";
        else if(f.getFunctionType()==FunctionItem.LOWER) 
            return "lower-case(" + ((f.getColumnRef()!=null)?columnExpression(f.getColumnRef(),orderBy):(String)f.getConstValue()) + ")";
        else if(f.getFunctionType()==FunctionItem.ISNULL)
            return (f.getColumnRef()!=null)?columnExpression(f.getColumnRef(),orderBy):(String)f.getConstValue();
        else
            throw new FlworExpressionException("Function not defined (reference value must be between 0 and 7):"+f.getFunctionType());
    }

    void buildXmlns() {
        // acrescenta definições necessárias de namaspace de acordo com o arquivo de configuração
         NodeList nlNS = this.configDoc.getElementsByTagName("namespaces"); 
         if (nlNS!=null) {
             Element tagNS = (Element)nlNS.item(0); // só existe uma tag namespaces (pode não existir)
             if(tagNS!=null) {
                 NodeList children = tagNS.getElementsByTagName("namespace"); // lista nodes de tags filhas (namespace) de elementos namespaces
                 for(int i=0;i<children.getLength();i++){
                     Element child = (Element)children.item(i);
                     this.namespaces.add("declare namespace "+child.getAttribute("prefix")+" = '"+child.getAttribute("url")+"';");
                 }
             }
         }
    }

    void buildColumnItemList() throws FlworExpressionException {
        // cria Lets auxiliares para sair no return, se necessário
        ArrayList<ColumnItem> columnItens = selectStmt.getColumnItemList().getColumnItem();
        if(selectStmt.getGroupByClause()==null) { // sem group by 
             for(int i=0;i<columnItens.size();i++){
                 ColumnItem item = columnItens.get(i);
                 if(item.getColumItemType()==ColumnItem.COLUMN){
                     ColumnRef col = item.getColumnRef();
                     String aliasName = item.getColumnAlias()==null?(col.getTableName()+"_"+col.getColumnName()):item.getColumnAlias(); // se não tiver alias - o que não deve acontecer - péga o nome da coluna
                     String expression = "data($"+col.getTableName()+"/"+col.getColumnName()+")"; // senão traz todo o nodo
                     addSingleLet(aliasName,expression);  
                 } else { // função, que também pode ser única e agregada
                     if(!isAggregateFunction(item.getFunctionItem().getFunctionType())) {// agregada é tratada antes
                         String funcExp = functionExpression(item.getFunctionItem(),false);
                         String aliasName = item.getColumnAlias()==null?functionName(item.getFunctionItem().getFunctionType()):item.getColumnAlias(); // se não tiver alias - o que não deve acontecer - péga a o nome da funcao
                         addSingleLet(aliasName,funcExp);
                     }
                 }
             }
        } else { // com group by 
            for(int i=0;i<columnItens.size();i++){
                ColumnItem item = columnItens.get(i);
                if(item.getColumItemType()==ColumnItem.FUNCTION){ // função,  só considera se não for de agrpamento, pois agregada já é tratada antes) - coluna em group by também já é tratada antes
                    if(!isAggregateFunction(item.getFunctionItem().getFunctionType())) {// não é agregada
                        String funcExp = functionExpression(item.getFunctionItem(),false);
                        String aliasName = item.getColumnAlias()==null?functionName(item.getFunctionItem().getFunctionType()):item.getColumnAlias(); // se não tiver alias - o que não deve acontecer - péga a o nome da funcao
                        addSingleLet(aliasName,funcExp);
                    } else  // é agregada, monta um let para cada função agreagada do select list, por enquanto só a referência
                         aggregateLetExpression.add(buildAggregateLet(columnItens.get(i),true)); // colocar com referencia ($table) - só no final
                }
            }
        }
    }
    
    void buildLetToWhereReferences() throws FlworExpressionException {
        // adicionando Let de tabelas que estão presentes no FromClause, que ainda não aparecem como Let ou For
        // mas aparecem nas expressões condicionais, o que torna obrigatória a adição da sua referência
        for(int i=0;i<this.selectStmt.getFromClause().getTableRef().size();i++){
            String tableName = this.selectStmt.getFromClause().getTableRef().get(i).getTableName();
            int pos = -1;
            if(this.xqWhereInitial!=null){
                String whereExpression = getWhereExpression(xqWhereInitial); // aqui está sem os objetos strings (se isWhereForXPath) - true = só os que ainda não foram usados
                pos = whereExpression.indexOf("$"+tableName+"/");
            }
            if(pos!=-1){ // achou referência no Where
                pos = -1;
                for(int j=0;j<this.letExpression.size()&&pos==-1;j++) 
                    pos = this.letExpression.get(j).indexOf("let $"+tableName+" "); // se pos>-1, a tabela está no FROM e está nos Let
                if(pos==-1) // não achou nos Let
                    for(int j=0;j<this.forExpression.size()&&pos==-1;j++) 
                        pos = this.forExpression.get(j).indexOf("for $"+tableName+" "); // se pos>-1, a tabela está no FROM e está nos For
                if(pos==-1) {// não achou também nos For de maneira direta
                    int j=0;
                    for(;j<this.forExpression.size()&&pos==-1;j++) 
                        pos = this.forExpression.get(j).indexOf("/"+tableName+"/"); // se pos>-1, a tabela está já está nos For fazendo referência a campos
                    if(pos!=-1){ // achou nos For como referência
                        // determinando o campo de referência
                        String fieldName = this.forExpression.get(--j).substring(pos+tableName.length()+2);
                        int endPosition1 = fieldName.indexOf(" "); int endPosition2 = fieldName.indexOf(")");
                        int endPosition = endPosition1>endPosition2?endPosition1:endPosition2;
                        if(endPosition!=-1)
                        fieldName = fieldName.substring(0,endPosition);
                        fieldName = fieldName.trim();
                        // determinando o alias
                        pos = this.forExpression.get(j).indexOf("for $"); // tem que existir
                        if(pos!=-1){
                            String fieldAliasName = this.forExpression.get(j).substring(pos+5);
                            fieldAliasName = fieldAliasName.substring(0,fieldAliasName.indexOf(" in ")).trim();
                            addAggregateLet(tableName,tableToTagLoaded(tableName),fieldName,fieldAliasName,null,null,false);
                        }
                    }
                }
                if(pos==-1) // não achou também nos For de maneira indireta, como referência
                    addSingleLet(tableName,tableToTagLoaded(tableName)); // cria um Let simples e direto para a tabela
            }
        }
    }
    

    void buildWhereJoinConditions() throws FlworExpressionException {
        // Detecta condições joins necessárias para algumas tabelas que posseum relacionamento expresso
        if(this.xqWhereInitial!=null){
            for(Object cond: this.xqWhereInitial.getConditItemList()){
                if(cond instanceof RelatComp) {
                    RelatComp rc = (RelatComp)cond;
                    if(rc.getRelatOper()==RelatComp.EQUALS && rc.getRightColumnItem()!=null &&
                       rc.getLeftColumnItem().getColumItemType()==ColumnItem.COLUMN &&
                       rc.getRightColumnItem().getColumItemType()==ColumnItem.COLUMN ) {//  existe uma relação entre tabelas (um join)
                        String tableName[] = new String[2];
                        tableName[0]=rc.getLeftColumnItem().getColumnRef().getTableName();
                        tableName[1]=rc.getRightColumnItem().getColumnRef().getTableName();
                        // procurando as tabelas nos For - se achar não precisa do where adicional para joins
                        for(String table:tableName){
                            boolean found = false;
                            for(String foR:this.forExpression) {
                                if(foR.indexOf(tableToTagLoaded(table))!=-1) {// existe referência no for
                                    found = true; break;
                                }
                            }
                            if(!found) {// se não achou, certamente está ou estará apenas no Let - pois é uma condição - e precisa do where adicional para joins                            
                                boolean joinFound = false;
                                for(int j=0;this.xqWhere!=null && j<this.xqWhere.getConditItemList().size();j++){ // procura se já adicionou antes
                                    if(this.xqWhere.getConditItemList().get(j) instanceof XQJoinCondition){
                                        XQJoinCondition jc = (XQJoinCondition)this.xqWhere.getConditItemList().get(j);
                                        if(jc.tableName.equals("$"+table)){
                                            joinFound = true; break;
                                        }
                                    }
                                }
                                if(!joinFound) { // não achou condição de prevenção de join, vai criar
                                    if(this.xqWhere==null)
                                        this.xqWhere = new XQWhere();
                                    else {
                                        this.xqWhere.getConditItemList().add("and");
                                        this.xqWhere.inUse.add(false); // mantendo a sincronia
                                    }
                                    this.xqWhere.getConditItemList().add(new XQJoinCondition("$"+table)); // previne LEFT e RIGHT JOINs entre as tabelas
                                    this.xqWhere.inUse.add(false); // mantendo a sincronia
                                }
                            } // if(!found)
                        }
                    } // fim if para ver se existe um join
                }
            } // fim for para  as condições
        }
    }

    void buildOrderBy() throws FlworExpressionException {
        if(this.selectStmt.getOrderByClause()!=null) {
            // verifica condição que existe um For com distinct-values: group by ou distinct
            boolean forWithDistinctValues = this.selectStmt.getGroupByClause()!=null || this.selectStmt.getColumnItemList().getSelectType()==SelectClause.DISTINCT;
            String sOrderType = this.selectStmt.getOrderByClause().getOrderType()==OrderByClause.ASC_ORDER?" ascending":" descending";
            ArrayList<ColumnItem> item = this.selectStmt.getOrderByClause().getColumnItem();
            // considera que order by com campos que já foram definidos em um for com distinct-values, pega o seu alias 
            String order = "order by " + ((forWithDistinctValues&&item.get(0).getColumItemType()==ColumnItem.COLUMN)?("$"+ columnRefToAlias(item.get(0).getColumnRef())):columnItemExpression(item.get(0),true)) + sOrderType;
            for(int i=1;i<item.size();i++)
                order += "," + ((forWithDistinctValues&&item.get(i).getColumItemType()==ColumnItem.COLUMN)?("$"+columnRefToAlias(item.get(i).getColumnRef())):columnItemExpression(item.get(i),true)) + sOrderType;
            this.orderByExpression.add(order);
        }
    }
    
    void buildReturn() throws FlworExpressionException {
        this.returnExpression.add("return <line>"); String var = null,varName = null;
        for(int i=0;i<this.selectStmt.getColumnItemList().getColumnItem().size();i++) { // todos os itens de retorno estão especificados no SelectClause
            ColumnItem item = this.selectStmt.getColumnItemList().getColumnItem().get(i);
            if(item.getColumnAlias()!=null){
                var = item.getColumnAlias();                
                varName = var;
            } else { // não possui alias
                if(item.getColumItemType()==ColumnItem.COLUMN){
                    var = item.getColumnRef().getTableName()+"_"+item.getColumnRef().getColumnName(); // essa é a referência que vai achar
                    varName = item.getColumnRef().getColumnName();
                } else {// função
                    var = functionName(item.getFunctionItem().getFunctionType());
                    varName = var;
                }
            }
            this.returnExpression.add("<"+varName+">{$"+var+"}</"+varName+">");
        } 
        this.returnExpression.add("</line>");
    }

    void buildXQuery() throws FlworExpressionException {
        this.aXQuery = new ArrayList<String>();
        // verificando definições de namespace
        for(String xmlns : this.namespaces)
            this.aXQuery.add(xmlns);
        // montando FLWOR xquery
        for(int i=0;i<this.forExpression.size();i++)
            this.aXQuery.add(this.forExpression.get(i));
        for(int i=0;i<this.letExpression.size();i++)
            this.aXQuery.add(this.letExpression.get(i));
        if(this.whereAdjusted!=null) // fez ajuste no where para referencias de campos
            this.aXQuery.add(this.whereAdjusted); 
        else if(this.xqWhere!=null)
            this.aXQuery.add("where "+getWhereExpression(this.xqWhere).trim()); // todos que estão lá no where
        for(int i=0;i<this.orderByExpression.size();i++)
            this.aXQuery.add(this.orderByExpression.get(i));
        for(int i=0;i<this.returnExpression.size();i++)
            this.aXQuery.add(this.returnExpression.get(i));
    }

    String tableToTag(String tableName){
        NodeList nlEnt = this.configDoc.getElementsByTagName("entities"); 
        Element tagEnt = (Element)nlEnt.item(0); // só existe uma tag entities
        NodeList children = tagEnt.getElementsByTagName("entity"); // lista nodes de tags filhas de elementos entity
        for(int i=0;i<children.getLength();i++){
            Element child = (Element)children.item(i);
            if(child.getAttribute("tableName").equalsIgnoreCase(tableName)) // procurando a tabela
                return child.getAttribute("tagName");
        }
        return null; // se não achou
    }
    
    Boolean isComplexStructTable(String tableName){
        NodeList nlEnt = this.configDoc.getElementsByTagName("entities"); 
        Element tagEnt = (Element)nlEnt.item(0); // só existe uma tag entities
        NodeList children = tagEnt.getElementsByTagName("entity"); // lista nodes de tags filhas de elementos entity
        for(int i=0;i<children.getLength();i++){
            Element child = (Element)children.item(i);
            if(child.getAttribute("tableName").equalsIgnoreCase(tableName)) {// procurando a tabela
                NodeList fields = child.getElementsByTagName("field"); // verifica se possui filhos (definição de fields;
                return fields.getLength()>0?true:false;
            }
        }
        return null; // se não achou
    }

    String tableToTagLoaded(String tableName) throws FlworExpressionException {
        for(int i=0;i<this.usedTables.size();i++) 
            if(this.usedTables.get(i).getTableName().equals(tableName)) // obtendo a tag 
                return this.tableTags.get(i);
        throw new FlworExpressionException("Invalid table name to get tag name:"+tableName);
    }
    
    String tagToTableLoaded(String tagName) throws FlworExpressionException {
        for(int i=0;i<this.usedTables.size();i++) 
            if(this.tableTags.get(i).equals(tagName)) // obtendo a tag 
                return this.usedTables.get(i).getTableName();
        throw new FlworExpressionException("Invalid tag name to get table name:"+tagName);
    }
    
    String columnRefToAlias(ColumnRef col) throws FlworExpressionException {
        // coluna (apenas coluna -> ColumnRef) para alias
        ArrayList<ColumnItem> item = this.selectStmt.getColumnItemList().getColumnItem();
        for(int i=0;i<item.size();i++) // procura nos itens do select
            if(item.get(i).getColumnRef().equals(col)) {
                if (item.get(i).getColumnAlias()==null)
                    return item.get(i).getColumnRef().getTableName()+"_"+item.get(i).getColumnRef().getColumnName();
                else
                    return item.get(i).getColumnAlias();
            }
        throw new FlworExpressionException("Invalid column reference to get alias name: "+col.getTableName()+"."+col.getColumnName());
    }
    
    static boolean hasAggregateFunctions(SelectClause columnItemList){
        // verificando se existe pelo menos uma função agregada na lista do select itens
        ArrayList<ColumnItem> columnItens = columnItemList.getColumnItem();
        for(ColumnItem item: columnItens)
            if(item.getColumItemType()==ColumnItem.FUNCTION && isAggregateFunction(item.getFunctionItem().getFunctionType()))
                return true;
        return false;
    }

    static boolean isAggregateFunction(int functionType){
        if(functionType>=0 && functionType<=4) // SUM, AVG, COUNT, MAX, MIN
            return true;
        else 
            return false;
    }

    public ArrayList<String> getAXQuery() {
        return aXQuery;
    }

}
