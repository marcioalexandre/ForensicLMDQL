package com.desenvolvatec.xpathplus.front;

import com.desenvolvatec.xpathplus.framework.XPathPlusExecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Program {
	public static void main(String[] args) {
            System.out.println("######################################################");
            System.out.println("#               XPathPlusJava Processor              #");
            System.out.println("######################################################");
            System.out.println();
            System.out.print("XPathPlusJava: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                XPathPlusExecutor executor = new XPathPlusExecutor(null,true,true);
                String input = in.readLine();
                while (input != "quit"){
                    try{
                        executor.execute(input);
                        System.out.println();
                        //System.out.println(executor.getContext().getResult());
                        executor.getContext().getResult();
                        System.out.println();
                    } catch (Exception e) {
                        System.out.println("Command Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    System.out.println("XpathPlusJava: ");
                    input = in.readLine();
                }
            } catch (Exception e) {
                System.out.println("Processor Error: "+e.getMessage());
                e.printStackTrace();
            }
	}
}