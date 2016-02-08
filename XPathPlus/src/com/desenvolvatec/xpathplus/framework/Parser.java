package com.desenvolvatec.xpathplus.framework;

public class Parser {
	private Command commandResult;

	public void setCommand(String command){

//		Sintaxe atual:
//			/*** - Retorna todos os arcos (ex: c:\xlinktest\teste.xml/***)
//			/elemento** - Retorna os arcos de determinado nó (ex: c:\xlinktest\teste.xml/ativo**)
//		    /link-source::elemento - Retorna todos os arcos que possuam "elemento" como From (ex: c:\xlinktest\teste.xml/link-source::ativo)
//			/link-destination::elemento - Retorna todos os arcos que possuam "elemento" como To(ex: c:\xlinktest\teste.xml/link-destination::passivo)
//			Expressão XPath - Retorna a String relativa ao resultado (ex: c:\xlinktest\teste.xml//ativo[@*[namespace-uri()='http://www.w3.org/1999/xlink' and local-name()='label']])
//     							c:\xlinktest\teste.xml//passivo
            try {	
        	
		if ("/***".equalsIgnoreCase(command.substring(command.length()-4, command.length()))){
                    String file = command.substring(0, command.length()-4);
                    this.commandResult = new CommandAllArcs (command, file);
		} else {
                    if ("**".equalsIgnoreCase(command.substring(command.length()-2, command.length()))){
                        String element = command.substring(command.lastIndexOf("/")+1, command.length()-2);
                        String file = command.substring(0, command.lastIndexOf("/"));
                        this.commandResult = new CommandXPathPlus (command, file, element);
                    } else if (("/link".equalsIgnoreCase(command.substring(command.indexOf("/"), command.indexOf("/")+5)))){
                        String element = command.substring(command.lastIndexOf("::")+2, command.length());
                        String file = command.substring(0, command.lastIndexOf("/"));
                        this.commandResult = new CommandXPathPlus (command, file, element);
                    } else if ("/...".equalsIgnoreCase(command.substring(command.indexOf("/"), command.indexOf("/")+4))) {
                        String element = command.substring(command.lastIndexOf("...")+3, command.length());
                        String file = command.substring(0, command.lastIndexOf("/"));
                        this.commandResult = new CommandXPathPlus (command, file, element);
                    } else if ("////".equalsIgnoreCase(command.substring(command.indexOf("/"), command.indexOf("/")+4))){
                        String element = command.substring(command.lastIndexOf("///")+3, command.length());
                        String file = command.substring(0, command.lastIndexOf("/"));
                        this.commandResult = new CommandXPathPlus (command, file, element);
                    } else if (command.contains("/")){
                        String expression = command.substring(command.indexOf("/"), command.length());
                        String file = command.substring(0, command.indexOf("/"));
                        this.commandResult = new CommandXPath (command, file, expression);
                    } else {
                        this.commandResult = null;
                        throw new IllegalArgumentException("Parser error (setCommand): Invalid XPathPlus syntax");
                    }
		}
            }catch(Exception e){
                e.printStackTrace();
                throw new IllegalArgumentException("Parser error (setCommand): Invalid XPathPlus syntax elements");
            }
	}

	public Command getCommand(){
		return commandResult;	
	}
}
