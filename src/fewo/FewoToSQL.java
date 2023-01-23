package fewo;

import oracle.jdbc.OracleDriver;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class FewoToSQL {
    private final static String LAENDER[] = {"Deutschland", "Frankreich", "Italien", "Schweden"};
    private final static String AUSSTATTUNGEN[] = {"Sauna", "Pool", "Fernseher", "SAUNA"};

    private final static SimpleDateFormat dateformat = new SimpleDateFormat("dd.mm.yyyy");
    private final static String DATEREGEX = "([0-2][0-9]|3[0-1]).(0[0-9]|1[0-2]).[0-9]{4}";

    static class IDBewwertung {
        String id;
        Double bewertung;

        public IDBewwertung(String id, Double bewertung) {
            this.id = id;
            this.bewertung = bewertung;
        }

        public Double getBewertung() {
            return this.bewertung;
        }

        public String getID() {
            return this.id;
        }

        public String toString() {
            return String.format("ID: %s Bewertung: %.1f\n", id, bewertung);
        }
    }

    public static Map<String, IDBewwertung> fewoSuche(String land, Date arrival, Date departure, String ausstattung) {
        Map<String, IDBewwertung> l = new TreeMap();

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
            if (ausstattung != null) {


                ps = conn.prepareStatement("SELECT DISTINCT f.fewoID, f.fewoname ,av.bewertung as bewertung FROM dbsys38.fewo f, dbsys38.addresse a, dbsys38.hatausstattung hat, dbsys38.buchung b, dbsys38.avgBewertung av " +
                        "WHERE f.addrID = a.addrID " +
                        "AND a.landname = ? " +
                        "AND hat.fewoID = f.fewoID " +
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

                ps.setDate(3, arrival);
                ps.setDate(4, departure);
                ps.setDate(5, arrival);
                ps.setDate(6, departure);
                ps.setDate(7, arrival);
                ps.setDate(8, departure);
                ps.setDate(9, arrival);
                ps.setDate(10, arrival);
                ps.setDate(11, departure);
                ps.setDate(12, departure);

            } else {

                ps = conn.prepareStatement("SELECT DISTINCT f.fewoID, f.fewoname, av.bewertung as bewertung FROM dbsys38.hatausstattung hat, dbsys38.fewo f, dbsys38.addresse a, dbsys38.buchung b, dbsys38.avgBewertung av " +
                        "WHERE f.addrID = a.addrID " +
                        "AND hat.fewoid = f.fewoid " +
                        "AND a.landname = ? " +
                        "AND f.fewoID = av.fewoID " +
                        "AND f.fewoID NOT IN " +
                        "(SELECT f.fewoID FROM dbsys38.fewo f, dbsys38.buchung b " +
                        "WHERE f.fewoID = b.fewoID " +
                        "AND (b.arrival <= ? AND b.departure >= ?) " +
                        "OR (b.arrival >= ? AND b.departure <= ?) " +
                        "OR (b.arrival <= ? AND b.departure <= ? AND b.departure >= ?) " +
                        "OR (b.arrival >= ? AND b.arrival <= ? AND b.departure >= ?) ) ");

                ps.setString(1, land);
                ps.setDate(2, arrival);
                ps.setDate(3, departure);
                ps.setDate(4, arrival);
                ps.setDate(5, departure);
                ps.setDate(6, arrival);
                ps.setDate(7, departure);
                ps.setDate(8, arrival);
                ps.setDate(9, arrival);
                ps.setDate(10, departure);
                ps.setDate(11, departure);
            }

            rset = ps.executeQuery();

            while (rset.next()) {


                String s = rset.getString("fewoname");
                String s1 = rset.getString("bewertung");
                String s2 = rset.getString("fewoID");
                System.out.printf("ID: %s Name: %s Bewertung: %.1f\n", s2, s, Double.parseDouble(s1));
                IDBewwertung idbw = new IDBewwertung(s2, Double.parseDouble(s1));
                l.put(s, idbw);
            }
            stmt.close();                                                                // Verbindung trennen
            //conn.commit();
            conn.close();
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
        return l;
    }

    //Buchungsnr/Buchungsdatum/Arrival/Departure/BewertungsID/Bewertung/Rechnungsnr/FewoID/Mail


    public static boolean fewoBuchung(Date arrival, Date departure, String fewoId, String mail) {

        Connection conn;
        ResultSet rset;
        int success = 0;
        Statement stmt;
        try {
            DriverManager.registerDriver(new OracleDriver());                // Treiber laden
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, "dbsys29", "dbsys29");                        // Verbindung erstellen

            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);            // Transaction Isolations-Level setzen
            conn.setAutoCommit(false);                                                    // Kein automatisches Commit
            stmt = conn.createStatement();
            PreparedStatement ps;

            rset = stmt.executeQuery("SELECT MAX(b.buchungsnr) as buchungsnr FROM dbsys38.Buchung b");
            rset.next();
            String buchungsnr = rset.getString("buchungsnr");
            buchungsnr = String.format("%8d", Integer.parseInt(buchungsnr) + 1).replace(" ", "0");
            rset = stmt.executeQuery("SELECT MAX(b.rechnungsnr) as rechnungsnr FROM dbsys38.Buchung b");
            rset.next();
            String rechnungsnr = rset.getString("rechnungsnr");
            rechnungsnr = String.format("%8d", Integer.parseInt(rechnungsnr) + 1).replace(" ", "0");


            // Statement-Objekt erzeugen
            ps = conn.prepareStatement("INSERT INTO dbsys38.Buchung(Buchungsnr, Buchungsdatum, Arrival, Departure, Rechnungsnr, FewoID, Mail) Values " +
                    "(?,?,?,?,?,?,?)");
            Date buchungsdatum = new Date(LocalDate.now().getYear() - 1900, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());

            ps.setString(1, buchungsnr);    //Buchungsnr
            ps.setDate(2, buchungsdatum);   //Buchungsdatum
            ps.setDate(3, arrival);         //Arrival
            ps.setDate(4, departure);       //Departure
            ps.setString(5, rechnungsnr);   //Rechnungsnr
            ps.setString(6, fewoId);        //FewoID
            ps.setString(7, mail);          //Mail

            success = ps.executeUpdate();
            stmt.close();                                                                // Verbindung trennen
            conn.commit();
            conn.close();
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
        return 0 < success;

    }
}
// Max.Mustermann@gmail.com