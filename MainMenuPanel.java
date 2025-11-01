import java.awt.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(Main game) {
        setLayout(new GridBagLayout());
        setBackground(new Color(30,30,30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,0,15,0);

        JLabel title = new JLabel("A Java RPG by Ubrahi Ngalan");
        title.setFont(new Font("Consolas", Font.BOLD, 40));
        title.setForeground(new Color(230,205,70));
        gbc.gridx = 0; gbc.gridy = 0;
        add(title, gbc);

        

        JButton start = new JButton("START GAME");
        JButton exit  = new JButton("EXIT");
        styleButton(start); styleButton(exit);

        gbc.gridy = 1; add(start, gbc);
        gbc.gridy = 2; add(exit, gbc);

        start.addActionListener(e -> game.showGamePanel());
        exit.addActionListener(e -> System.exit(0));
    }

    private void styleButton(JButton btn){
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(new Font("Consolas", Font.BOLD, 40));
        btn.setFocusPainted(false);
    }
}
