package com.desenvolvatec.xpathplus.framework;

public class Command {
	
    private String commandValue;
    private String path;
    private String file;
    
    public Command (String value, String file){
        definePathAndFile(value,fileDefinition(file));
    }
    
    private void definePathAndFile(String value, String file){
        // definindo path e file
        this.commandValue = value;
        if(value.lastIndexOf("\\")==-1) // pode não existir em caso de banco xml
            this.path = value; 
        else // existe, é arquivo avulso
            this.path = value.substring(0, value.lastIndexOf("\\")+1); 
        this.file = file;
    }

    private String fileDefinition(String file){
        if(file.lastIndexOf("\\")!=-1 && file.lastIndexOf(".xml")!=-1) // se for de arquivo avulso xml
            return file.substring(file.lastIndexOf("\\")+1, file.lastIndexOf(".xml")+4);
        else if(file.lastIndexOf("\\")!=-1 && file.lastIndexOf(".xsd")!=-1) // se for de arquivo avulso xsd
            return file.substring(file.lastIndexOf("\\")+1, file.lastIndexOf(".xsd")+4);
        else // se for de banco pode não ter extensão nem "\"
            return file;
    }

    public String getCommand (){
        return this.commandValue;
    }
    
    public String getPath (){
        return this.path;
    }
    
    public String getFile(){
        return this.file;
    }

}
