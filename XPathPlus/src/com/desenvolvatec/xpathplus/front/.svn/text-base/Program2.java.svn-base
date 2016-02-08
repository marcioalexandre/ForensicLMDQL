package com.desenvolvatec.xpathplus.front;

import com.desenvolvatec.xpathplus.framework.XPathPlusExecutor;

import javax.swing.JOptionPane;


public class Program2 {
    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog(null,"Entre com a expressão XPath+:                                                        ",
                                                   "XPATH+ JAVA PROCESSOR",JOptionPane.QUESTION_MESSAGE);
        while (input != null){
            try{
                XPathPlusExecutor executor = new XPathPlusExecutor(null,true,true); // com geração de arquivo e filtro
                executor.execute(input);
                executor.getContext().getResult(); // fechando e gerando arquivo
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,e.getMessage(),"Command Error",JOptionPane.ERROR_MESSAGE);
            }
            input = JOptionPane.showInputDialog(null,"Entre com a expressão XPath+:                                                        ",
                                                "XPATH+ JAVA PROCESSOR",JOptionPane.QUESTION_MESSAGE);
        }
    }
}
