package com.desenvolvatec.xpathplus.framework;


public class CommandXPathPlus extends Command{
    private String element;
    private String type;
    
    public CommandXPathPlus (String command, String file, String element){
        super(command, file);
        this.element = element;
        if ("**".equalsIgnoreCase(command.substring(command.length()-2, command.length()))){
            this.type = "specificArc";
        } else if (command.contains("::")){
            if ("source".equalsIgnoreCase(command.substring(command.indexOf("/")+6, command.lastIndexOf("::")))){
                this.type = "sourceArc";
            } else if ("destination".equalsIgnoreCase(command.substring(command.indexOf("/")+6, command.lastIndexOf("::")))){
                this.type = "destinationArc";
            }
        } else if (command.contains("...") || command.contains("///")){
            if ("...".equalsIgnoreCase(command.substring(command.indexOf("/")+1, command.indexOf("/")+4))){
                this.type = "destinationArc";
            } else if ("///".equalsIgnoreCase(command.substring(command.indexOf("/")+1, command.indexOf("/")+4))){
                this.type = "sourceArc";
            }
        }
    }

    public String getElement(){
        return this.element;
    }
    
    public String getType(){
        return this.type;
    }
}
