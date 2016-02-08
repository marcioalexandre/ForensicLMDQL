package com.desenvolvatec.xpathplus.framework;

public class CommandSpecificElement extends Command{
	private String element;
	
	public CommandSpecificElement (String command, String file, String element){
		super(command, file.substring(file.lastIndexOf("\\")+1, file.lastIndexOf(".xml")+4));
		this.element = element;
	}

	public String getElement(){
		return this.element;
	}
}
