/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/udf/ValUdf.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.udf;

import java.util.Arrays;

import mondrian.olap.Evaluator;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;


/**
 * VB function <code>Val</code>
 *
 * @author Gang Chen
 */
public class ValUdf implements UserDefinedFunction {

    public Object execute(Evaluator evaluator, Argument[] arguments) {
       Object arg0 = arguments[0].evaluate(evaluator);
       /*
        Member member = (Member) arg;
        Level nivel = member.getLevel();
        String nomeNivel = nivel.getUniqueName();
        String legendaNivel = member.getCaption();
        
        String fixedMember = nomeNivel + ".[" + legendaNivel + "]";  */
           	
        Member membro = (Member)arg0;        
        String currentMember = membro.getName();
    
        Object[] retorno = new Object[1];
        //Member[] array= new Member[1];
        retorno[0] = membro;
        //retorno[0] = array;
        
        int x = 0;
        
        
     /*   try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://172.17.98.111:3306/foodmart"; 
	        Connection conexao = DriverManager.getConnection(url, "teclabor", "teclabor"); 
	        
	        
	        String sql = "SELECT ";
	        
	        //PreparedStatement ps = 
	        
	        
	      
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        */
        
        
 
        return Arrays.asList(retorno);  
    }

    public String getDescription() {
        return "VB function Val";
    }

    public String getName() {
        return "Valorrr";
    }

    public Type[] getParameterTypes() {
        return new Type[] { MemberType.Unknown };
    }

    public String[] getReservedWords() {
        return null;
    }

    public Type getReturnType(Type[] parameterTypes) {
        return new SetType(MemberType.Unknown);
    }

    public Syntax getSyntax() {
        return Syntax.Function;
    }

}

// End ValUdf.java
