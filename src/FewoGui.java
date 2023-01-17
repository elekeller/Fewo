package jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.LinkedList;

public class FewoGUI extends JFrame {
    final static String LAENDER[] = {"Deutschland", "Frankreich", "Italien", "Schweden"};
    final static String AUSST[] = {"Sauna", "Fernseher", "Pool", "SAUNA"};
    JPanel suchleiste, ergebnisleiste, landPanel, arrivalPanel, departurePanel, ausstattungenPanel, ausstattungsTitelPanel, ausstattungsPanel, goPanel;
    JComboBox landcombo;
    JTextField arrival, departure;
    JCheckBox sauna, fernseher, pool, sauna1;
    JButton goButton;
    LocalDate arrivalDate, departureDate;
    String landString, ausstString;
    LinkedList<String> ausstListe = new LinkedList();

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
                if (ausstListe.contains(AUSST[1])) ausstListe.remove(AUSST[1]);
                else ausstListe.add(AUSST[1]);
            }
        });
        fernseher = new JCheckBox("Fernseher");
        fernseher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ausstListe.contains(AUSST[2])) ausstListe.remove(AUSST[2]);
                else ausstListe.add(AUSST[2]);
            }
        });
        sauna1 = new JCheckBox("SAUNA");
        sauna1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ausstListe.contains(AUSST[3])) ausstListe.remove(AUSST[3]);
                else ausstListe.add(AUSST[3]);
            }
        });

        ausstattungsTitelPanel = new JPanel();
        ausstattungsTitelPanel.add(new JLabel("Ausstattung"));

        ausstattungenPanel = new JPanel();
        ausstattungenPanel.add(sauna);
        ausstattungenPanel.add(pool);
        ausstattungenPanel.add(fernseher);
        ausstattungenPanel.add(sauna1);

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
                StringBuilder s = new StringBuilder();
                for (String str : ausstListe) {
                    s.append(str).append(", ");
                }
                ausstString = s.toString();
                // SQL-Suche                                                         <<< hier wird die Suchmethode mit den Argumenten landString, arrivalDate,... aufgerufen
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

        // ergebnisleiste
        ergebnisleiste = new JPanel();

        // this
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(suchleiste);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        FewoGUI f = new FewoGUI();
    }
}
