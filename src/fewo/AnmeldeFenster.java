package fewo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                if ()
            }
        });
        // this
        this.setLayout(new GridLayout(5, 1));
        this.add(new JLabel("Mail:"));
        this.add(mailField);
        this.add(new JLabel("Passwort:"));
        this.add(passwordField);


        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
