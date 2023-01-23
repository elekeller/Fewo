package fewo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnmeldeFenster extends JFrame {
    JTextField usernameField, passwordField;
    JButton signinButton;

    private String mail, password;

    public AnmeldeFenster() {
        mail = new String();
        password = new String();

        usernameField = new JTextField("");
        passwordField = new JTextField("");


        // this
        this.setLayout(new GridLayout(5, 1));
        this.add(new JLabel("Benutzername:"));
        this.add(usernameField);
        this.add(new JLabel("Passwort:"));
        this.add(passwordField);


        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
