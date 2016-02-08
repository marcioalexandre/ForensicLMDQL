package com.desenvolvatec.xpathplus.framework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Context {

    private String output = "";
    
    public void addResult (String value){
        output += value + "\n";
    }

    public void getResult() throws IOException {
        String oldResult = output;
        this.output = "";
        FileWriter writer = new FileWriter(new File("saida.txt"));
        PrintWriter result = new PrintWriter(writer);
        result.println(oldResult);
        result.close();
        writer.close();
        //return oldResult;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
