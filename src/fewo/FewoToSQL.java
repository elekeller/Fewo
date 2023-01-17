package fewo;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class FewoToSQL {
    private final static String LAENDER[] = {"Deutschland","Frankreich","Italien","Schweden"};
    private final static String AUSSTATTUNGEN[] = {"Sauna","Pool","Fernseher","SAUNA"};

    private final static SimpleDateFormat dateformat = new SimpleDateFormat("dd.mm.yyyy");
    private final static String DATEREGEX = "([0-2][0-9]|3[0-1]).(0[0-9]|1[0-2]).[0-9]{4}";

    public static ResultSet fewoSuche(String land, String arrival, String departure, String ausstattung)
    {
        Connection conn;
        ResultSet rset = null;
        Statement stmt;
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 				// Treiber laden
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, "dbsys29", "dbsys29"); 						// Verbindung erstellen

            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 			// Transaction Isolations-Level setzen
            conn.setAutoCommit(false);													// Kein automatisches Commit
            stmt = conn.createStatement();
            PreparedStatement ps;

            // Statement-Objekt erzeugen
            if(ausstattung != null) {
                ps = conn.prepareStatement("SELECT DISTINCT f.fewoname ,av.bewertung as bewertung FROM dbsys38.fewo f, dbsys38.addresse a, dbsys38.hatausstattung hat, dbsys38.buchung b, dbsys38.avgBewertung av " +
                        "WHERE f.addrID = a.addrID " +
                        "AND a.landname = ? " +
                        "AND hat.ausstbeschreibung = ? " +
                        "AND f.fewoID = av.fewoID " +
                        "AND f.fewoID NOT IN " +
                        "(SELECT f.fewoID FROM dbsys38.fewo f, dbsys38.buchung b " +
                        "WHERE f.fewoID = b.fewoID " +
                        "AND (b.arrival <= ? AND b.departure >= ?) " +
                        "OR (b.arrival >= ? AND b.departure <= ?) " +
                        "OR (b.arrival <= ? AND b.departure <= ? AND b.departure >=?) " +
                        "OR (b.arrival >= ? AND b.arrival <= ? AND b.departure >= ?) ) ");

                ps.setString(1, land);
                ps.setString(2, ausstattung);
                ps.setString(3, arrival);
                ps.setString(4, departure);
                ps.setString(5, arrival);
                ps.setString(6, departure);
                ps.setString(7, arrival);
                ps.setString(8, departure);
                ps.setString(9, departure);
                ps.setString(10, arrival);
                ps.setString(11, arrival);
                ps.setString(12, departure);

            } else {

                ps = conn.prepareStatement("SELECT DISTINCT f.fewoname ,av.bewertung as bewertung FROM dbsys38.hatausstattung hat, dbsys38.fewo f, dbsys38.addresse a, dbsys38.buchung b, dbsys38.avgBewertung av " +
                        "WHERE f.addrID = a.addrID " +
                        "AND hat.fewoid = f.fewoid " +
                        "AND a.landname = ? " +
                        "AND f.fewoID = av.fewoID " +
                        "AND f.fewoID NOT IN " +
                        "(SELECT f.fewoID FROM dbsys38.fewo f, dbsys38.buchung b " +
                        "WHERE f.fewoID = b.fewoID " +
                        "AND (b.arrival <= ? AND b.departure >= ?) " +
                        "OR (b.arrival >= ? AND b.departure <= ?) " +
                        "OR (b.arrival <= ? AND b.departure <= ? AND b.departure >=?) " +
                        "OR (b.arrival >= ? AND b.arrival <= ? AND b.departure >= ?) ) ");

                ps.setString(1, land);
                ps.setString(2, arrival);
                ps.setString(3, departure);
                ps.setString(4, arrival);
                ps.setString(5, departure);
                ps.setString(6, arrival);
                ps.setString(7, departure);
                ps.setString(8, departure);
                ps.setString(9, arrival);
                ps.setString(10, arrival);
                ps.setString(11, departure);
            }

            String statement = ps.toString();
            rset = ps.executeQuery();


            while(rset.next()) {
                String s = rset.getString("fewoname");
                String s1 = rset.getString("bewertung");
                System.out.printf("Name: %s Bewertung: %.1f\n", s, Double.parseDouble(s1));
            }

            stmt.close();																// Verbindung trennen
            //conn.commit();
            conn.close();
        } catch (SQLException se) {														// SQL-Fehler abfangen
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
        return rset;
    }
}