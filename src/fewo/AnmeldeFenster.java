package fewo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class AnmeldeFenster extends JFrame {
    JTextField mailField, passwordField;
    JButton signinButton;

    private String mail, password;

    public AnmeldeFenster() {
        mail = new String();
        password = new String();

        mailField = new JTextField("");
        passwordField = new JTextField("");

        // signinButton
        signinButton = new JButton("Anmelden");
        signinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mailField.getText().length() > 0 && passwordField.getText().length() > 0 && passwordField.getText().equals(getPassword(mailField.getText()))) {
                    System.out.println("passwort akzeptiert");
                    this.notifyAll();
                    AnmeldeFenster.this.dispose();
                } else {
                    System.out.println("passwort abgelehnt");
                    AnmeldeFenster.this.dispose();
                }
            }
        });
        // this
        this.setLayout(new GridLayout(5, 1));
        this.add(new JLabel("Mail:"));
        this.add(mailField);
        this.add(new JLabel("Passwort:"));
        this.add(passwordField);
        this.add(signinButton);


        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    String getPassword(String mail) {
        String l = new String();

        Connection conn;
        ResultSet rset = null;
        Statement stmt;
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());                // Treiber laden
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, "dbsys29", "dbsys29");                        // Verbindung erstellen

            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);            // Transaction Isolations-Level setzen
            conn.setAutoCommit(false);                                                    // Kein automatisches Commit
            stmt = conn.createStatement();
            PreparedStatement ps;

            // Statement-Objekt erzeugen

            ps = conn.prepareStatement("SELECT k.passwort FROM dbsys38.kunde k " +
                    "WHERE k.mail = ? ");

            ps.setString(1, mail);


        } catch (SQLException se) {                                                        // SQL-Fehler abfangen
            System.out.println();
            System.out
                    .println("SQL Exception occurred while establishing connection to DBS: "
                            + se.getMessage());
            System.out.println("- SQL state  : " + se.getSQLState());
            System.out.println("- Message    : " + se.getMessage());
            System.out.println("- Vendor code: " + se.getErrorCode());
            System.out.println();
            System.out.println("EXITING WITH FAILURE ... !!!");
            System.out.println();
        }
        System.out.println("l = " + l);
        return l;
    }
}
