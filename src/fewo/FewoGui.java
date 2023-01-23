package fewo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;

public class FewoGUI extends JFrame {
    final static String LAENDER[] = {"Deutschland", "Frankreich", "Italien", "Schweden"};
    final static String AUSST[] = {"Sauna", "Fernseher", "Pool", "SAUNA"};
    JPanel suchleiste, ergebnisleiste, ergebnisPanel, buchenPanel, landPanel, arrivalPanel, departurePanel, ausstattungenPanel, ausstattungsTitelPanel, ausstattungsPanel, goPanel;
    JComboBox landcombo;
    JTextField arrival, departure;
    JCheckBox sauna, fernseher, pool, sauna1;
    JButton goButton, buchenButton;
    LocalDate arrivalDate, departureDate;
    String landString;
    JList ergebnisListe;
    LinkedList<String> ausstListe;
    java.util.List<String> idList;
    DefaultListModel<String> model;

    public FewoGUI() {

        // landcombo
        landcombo = new JComboBox(LAENDER);
        landPanel = new JPanel();
        landPanel.setLayout(new GridLayout(2, 1));
        landPanel.add(new JLabel("Land"));
        landPanel.add(landcombo);

        // arrival/departure
        arrivalDate = LocalDate.now();
        arrival = new JTextField(arrivalDate.toString());
        arrivalPanel = new JPanel();
        arrivalPanel.setLayout(new GridLayout(2, 1));
        arrivalPanel.add(new JLabel("Anreisedatum"));
        arrivalPanel.add(arrival);

        departureDate = LocalDate.now();
        departure = new JTextField(departureDate.toString());
        departurePanel = new JPanel();
        departurePanel.setLayout(new GridLayout(2, 1));
        departurePanel.add(new JLabel("Anreisedatum"));
        departurePanel.add(departure);

        // ausstattung
        ausstListe = new LinkedList();
        sauna = new JCheckBox("Sauna");
        sauna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ausstListe.contains(AUSST[0])) ausstListe.remove(AUSST[0]);
                else ausstListe.add(AUSST[0]);
            }
        });
        pool = new JCheckBox("Pool");
        pool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ausstListe.contains("Pool")) ausstListe.remove("Pool");
                else ausstListe.add("Pool");
            }
        });
        fernseher = new JCheckBox("Fernseher");
        fernseher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ausstListe.contains("Fernseher")) ausstListe.remove("Fernseher");
                else ausstListe.add("Fernseher");
            }
        });


        ausstattungsTitelPanel = new JPanel();
        ausstattungsTitelPanel.add(new JLabel("Ausstattung"));

        ausstattungenPanel = new JPanel();
        ausstattungenPanel.add(sauna);
        ausstattungenPanel.add(pool);
        ausstattungenPanel.add(fernseher);


        ausstattungsPanel = new JPanel();
        ausstattungsPanel.setLayout(new GridLayout(2, 1));
        ausstattungsPanel.add(ausstattungsTitelPanel);
        ausstattungsPanel.add(ausstattungenPanel);

        // goButton
        goButton = new JButton("Los");
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                landString = LAENDER[landcombo.getSelectedIndex()];
                arrivalDate = LocalDate.parse(FewoGUI.this.arrival.getText());
                departureDate = LocalDate.parse(FewoGUI.this.departure.getText());

                Map<String, FewoToSQL.IDBewwertung> rs;
                // SQL-Suche                                                         <<< hier wird die Suchmethode mit den Argumenten landString, arrivalDate,... aufgerufen
                if (ausstListe.isEmpty()) {

                    rs = FewoToSQL.fewoSuche(landString, new Date(arrivalDate.getYear() - 1900, arrivalDate.getMonthValue(), arrivalDate.getDayOfMonth()), new Date(departureDate.getYear() - 1900, departureDate.getMonthValue(), departureDate.getDayOfMonth()), null);

                } else {

                    rs = FewoToSQL.fewoSuche(landString, new Date(arrivalDate.getYear() - 1900, arrivalDate.getMonthValue(), arrivalDate.getDayOfMonth()), new Date(departureDate.getYear() - 1900, departureDate.getMonthValue(), departureDate.getDayOfMonth()), ausstListe.getFirst());
                }
                model.clear();
                idList.clear();
                if (rs.size() == 0) {
                    model.add(0, "keine Ergebnisse");
                    buchenButton.setVisible(false);

                } else {
                    int i = 0;

                    for (Map.Entry<String, FewoToSQL.IDBewwertung> entry : rs.entrySet()) {
                        System.out.println("Name: " + entry.getKey() + " Bewertung: " + entry.getValue().getBewertung() + " ID: " + entry.getValue().getID());
                        model.add(i++, String.format("Name: %s Bewertung: %.1f\n", entry.getKey(), entry.getValue().getBewertung()));
                        idList.add(entry.getValue().getID());
                    }
                    buchenButton.setVisible(true);
                }
                FewoGUI.this.ergebnisListe.setModel(model);
            }
        });
        goPanel = new JPanel();
        goPanel.setLayout(new GridLayout(2, 1));
        goPanel.add(new JLabel(""));
        goPanel.add(goButton);


        // suchleiste
        suchleiste = new JPanel();
        suchleiste.setLayout(new GridLayout(1, 5));
        suchleiste.add(landPanel);
        suchleiste.add(arrivalPanel);
        suchleiste.add(departurePanel);
        suchleiste.add(ausstattungsPanel);
        suchleiste.add(goPanel);

        // buchenButton
        buchenButton = new JButton("Buchen");
        buchenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                Date arrival = new Date(arrivalDate.getYear() - 1900, arrivalDate.getMonthValue(), arrivalDate.getDayOfMonth());
                Date departure = new Date(departureDate.getYear() - 1900, departureDate.getMonthValue(), departureDate.getDayOfMonth());
                String fewoID = idList.get(FewoGUI.this.ergebnisListe.getSelectedIndex());

                Thread t = new Thread() {
                    public void run() {
                        System.out.println("he");
                        AnmeldeFenster anmelden = new AnmeldeFenster(arrival, departure, fewoID);
                    }
                };
                t.run();


                //FewoToSQL.fewoSuche(buchungsnr, new Date(arrivalDate.getYear() - 1900, arrivalDate.getMonthValue(), arrivalDate.getDayOfMonth()), new Date(departureDate.getYear() - 1900, departureDate.getMonthValue(), departureDate.getDayOfMonth()), rechnungsnr, fewoID, mail);

            }


        });
        buchenPanel = new JPanel();
        buchenPanel.add(buchenButton);
        buchenButton.setVisible(false);
        // ergebnisleiste
        idList = new LinkedList();
        model = new DefaultListModel();
        model.addElement("keine Ergebnisse");
        ergebnisListe = new JList(model);
        ergebnisleiste = new JPanel();
        ergebnisleiste.setLayout(new GridLayout(1, 2));
        ergebnisleiste.add(ergebnisListe);
        ergebnisleiste.add(buchenPanel);


        // this
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(2, 1));
        this.add(suchleiste);
        this.add(ergebnisleiste);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        FewoGUI f = new FewoGUI();
    }
}
