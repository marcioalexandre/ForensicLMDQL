import com.desenvolvatec.sql2xquery.xquery.XQuery;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;


public class Tela extends JFrame {

    JTextArea taSQL = new JTextArea();
    JScrollPane spSQL = new JScrollPane();
    JTextArea taRes = new JTextArea();
    JScrollPane spRes = new JScrollPane();
    JButton btExec = new JButton();
    JLabel lbRes = new JLabel();
    
    Connection conn = null;
    private JLabel lbTProc = new JLabel();

    public Tela() {
        // só DB2
        try { // estabelecendo conexão
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            conn = DriverManager.getConnection("jdbc:db2:foodmart","foodmart", "foodmart");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERRO NA CONEXÃO COM DB2",JOptionPane.ERROR_MESSAGE);
        }
        // Tela
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout( null );
        this.setSize(new Dimension(1024, 710));
        this.setTitle("Teste de ResultSet");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width) 
            frameSize.width = screenSize.width;
        this.setLocation( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        spSQL.setBounds(new Rectangle(10, 15, 990, 210));
        taSQL.setLineWrap(true);
        taSQL.setWrapStyleWord(true);
        taSQL.setToolTipText("Entre com a expressão SQL");
        spRes.setBounds(new Rectangle(10, 280, 990, 370));
        taRes.setEditable(false);
        taRes.setWrapStyleWord(false);
        taRes.setLineWrap(false);
        taRes.setForeground(Color.white);
        taRes.setBackground(Color.blue);
        btExec.setText("Executar");
        btExec.setBounds(new Rectangle(10, 235, 105, 25));
        btExec.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        btExec_actionPerformed(e);
                    }
                });
        lbRes.setText("Resultados");
        lbRes.setBounds(new Rectangle(725, 260, 275, 15));
        lbRes.setFont(new Font("Dialog", 1, 11));
        lbRes.setHorizontalAlignment(SwingConstants.RIGHT);
        lbTProc.setBounds(new Rectangle(10, 655, 990, 15));
        lbTProc.setFont(new Font("Dialog", 1, 11));
        spSQL.getViewport().add(taSQL);
        spRes.getViewport().add(taRes);
        this.getContentPane().add(lbTProc, null);
        this.getContentPane().add(lbRes, null);
        this.getContentPane().add(btExec, null);
        this.getContentPane().add(spRes, null);
        this.getContentPane().add(spSQL, null);
    }
    
    private void btExec_actionPerformed(ActionEvent e)  {
        lbTProc.setText("");
        taRes.setText("");
        ResultSet rs = null;
        try {
            long tini = Calendar.getInstance().getTimeInMillis();
            File f = new File("sql2xquery.xml");
            rs = new XQuery(f).executeSQLQuery(conn,taSQL.getText());
            long tfin = Calendar.getInstance().getTimeInMillis();
            while(rs.next()){
                taRes.append(rs.getMetaData().getColumnName(1)+":"+rs.getString(1)); // sempre tem um
                for(int i=2;i<=rs.getMetaData().getColumnCount();i++)
                    taRes.append("; "+rs.getMetaData().getColumnName(i)+":"+rs.getString(i));
                taRes.append("\n");
            }
            long tfin2 = Calendar.getInstance().getTimeInMillis();
            lbTProc.setText("Tempo de Processamento: "+formatDouble(tfin-tini,0,true)+" ms (+ exibição: "+formatDouble(tfin2-tfin,0,true)+" ms)");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,ex.getMessage(),"ERRO NA EXECUÇÃO DA QUERY",JOptionPane.ERROR_MESSAGE);
        }
            
    }

    static String formatDouble(double num, int decimalPrecision, boolean separaMilhar){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        df.setMinimumFractionDigits(decimalPrecision);
        df.setMaximumFractionDigits(decimalPrecision);
        df.setGroupingUsed(separaMilhar);
        return df.format(num);
    }

    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tela tela = new Tela();
        tela.setVisible(true);
    }

}

