package com.desenvolvatec.xpathplus.front;

import com.desenvolvatec.xpathplus.framework.XPathPlusExecutor;

import com.desenvolvatec.xpathplus.xmldb.StartPoint;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;

import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TelaTestes extends JFrame {
    private JTextField tfExp = new JTextField();
    private JLabel jLabel1 = new JLabel();
    private JButton btSub = new JButton();
    private JScrollPane spRes = new JScrollPane();
    private JTextArea taRes = new JTextArea();
    private JCheckBox chkFilter = new JCheckBox();
    private JTextField tfConfigFile = new JTextField();
    private JCheckBox chkConfig = new JCheckBox();
    private JComboBox cbBase = new JComboBox(new String[]{"INSTANCE","TAXONOMY","OPERATOR DEFINITION"});

    public TelaTestes() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout( null );
        this.setSize(new Dimension(904, 601));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width) 
            frameSize.width = screenSize.width;
        this.setLocation( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setTitle( "Testes XPath+" );
        this.addWindowListener(new WindowAdapter() {
                    public void windowOpened(WindowEvent e) {
                        this_windowOpened(e);
                    }
                });
        tfExp.setBounds(new Rectangle(20, 40, 640, 20));
        jLabel1.setText("Expressão XPath+ :");
        jLabel1.setBounds(new Rectangle(20, 20, 130, 15));
        jLabel1.setFont(new Font("Dialog", 1, 11));
        btSub.setText("Submete");
        btSub.setBounds(new Rectangle(785, 40, 90, 20));
        btSub.setToolTipText("Submete e avalia a expressão XPath+, trazendo seus resultados");
        btSub.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)  {
                        btSub_actionPerformed(e);
                    }
                });
        spRes.setBounds(new Rectangle(20, 75, 860, 480));
        spRes.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        taRes.setEditable(false);
        taRes.setWrapStyleWord(true);
        taRes.setLineWrap(true);
        chkFilter.setText("Filtro Arcos");
        chkFilter.setBounds(new Rectangle(570, 15, 90, 20));
        chkFilter.setSelected(true);
        tfConfigFile.setBounds(new Rectangle(670, 40, 100, 20));
        tfConfigFile.setText("xpathplus.xml");
        tfConfigFile.setEnabled(false);
        chkConfig.setText("Config.");
        chkConfig.setBounds(new Rectangle(670, 15, 75, 19));
        chkConfig.setFont(new Font("Dialog", 1, 11));
        chkConfig.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        chkConfig_stateChanged(e);
                    }
                });
        cbBase.setBounds(new Rectangle(415, 15, 145, 20));
        cbBase.setSelectedIndex(0);
        cbBase.setMaximumRowCount(3);
        cbBase.setToolTipText("Escolha a base de dados");
        cbBase.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        cbBase_itemStateChanged(e);
                    }
                });
        spRes.getViewport().add(taRes, null);
        this.getContentPane().add(cbBase, null);
        this.getContentPane().add(chkConfig, null);
        this.getContentPane().add(tfConfigFile, null);
        this.getContentPane().add(chkFilter, null);
        this.getContentPane().add(spRes, null);
        this.getContentPane().add(btSub, null);
        this.getContentPane().add(jLabel1, null);
        this.getContentPane().add(tfExp, null);
    }

    private void btSub_actionPerformed(ActionEvent e) {
        try{
            XPathPlusExecutor executor = null;
            if(chkConfig.isSelected()) { // vai pegar informações do arquivo de configuração
                if(cbBase.getSelectedIndex()==StartPoint.OPERATOR_DEFINITION)
                    executor = new XPathPlusExecutor(tfConfigFile.getText().trim(),chkFilter.isSelected(),true,cbBase.getSelectedIndex()); 
                else // ´pelo startpoint
                    executor = new XPathPlusExecutor(tfConfigFile.getText().trim()); 
            } else // sem arquivo de configuração
                executor = new XPathPlusExecutor(tfExp.getText().trim(),chkFilter.isSelected(),true,cbBase.getSelectedIndex()); 
            taRes.setText(executor.getContext().getOutput()); // jogando no textarea
            executor.getContext().setOutput(""); // limpando a variável de armazenamento de resultados
        } catch (Exception ex) {
            ex.printStackTrace();
            taRes.setText(ex.getMessage());
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Erro na Expressão XPath+",JOptionPane.ERROR_MESSAGE);
        }

    }

    private void this_windowOpened(WindowEvent e) {
        tfExp.requestFocus();
    }

    private void chkConfig_stateChanged(ChangeEvent e) {
        if(chkConfig.isSelected()){
            chkFilter.setEnabled(false);
            cbBase.setEnabled(false);
            tfExp.setEnabled(false);
            tfConfigFile.setEnabled(true);
            tfConfigFile.requestFocus();
        } else {
            chkFilter.setEnabled(true);
            cbBase.setEnabled(true);
            tfConfigFile.setEnabled(false);
            tfExp.setEnabled(true);
            tfExp.requestFocus();
            cbBase.setSelectedIndex(0); // se tiver marcado op.def., volta para instance
        }
    }
    
    private void cbBase_itemStateChanged(ItemEvent e) {
        if(cbBase.getSelectedIndex()==StartPoint.OPERATOR_DEFINITION)
            chkConfig.setSelected(true);
        else
            chkConfig.setSelected(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new TelaTestes().setVisible(true);
    }

}
