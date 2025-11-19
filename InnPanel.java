import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

public class InnPanel extends JPanel {
    private Main game;
    private Player player;
    private JTextArea log;
    private StatPanel statPanel; // live stats
    private JButton weaponBtn;
    private JButton armorBtn;

    private LinkedList<Weapon> weaponQueue;
    private LinkedList<Armor> armorQueue;

    public InnPanel(Main game, Player player) {
        this.game = game;
        this.player = player;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 25));

        // Stat panel on the right
        statPanel = new StatPanel(player);
        add(statPanel, BorderLayout.EAST);

        // Initialize queues from player's available items

        // Left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 20, 20));
        leftPanel.setBackground(new Color(25, 25, 25));
        leftPanel.setPreferredSize(new Dimension(600, 0));


        // Exit button
        JButton exitBtn = styledButton("Exit Shop");
        leftPanel.add(exitBtn);

        // Lucky 9 Button
        JButton lucky9Btn = styledButton("Play Lucky 9 (50 Gold)");
        leftPanel.add(lucky9Btn);

        add(leftPanel, BorderLayout.WEST);

        // Log area in the center
        log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setBackground(new Color(40, 40, 40));
        log.setForeground(Color.WHITE);
        log.setFont(new Font("Consolas", Font.BOLD, 28));
        log.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 3));

        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(700, 0));
        add(scroll, BorderLayout.CENTER);

        log.setText("INN TRIAL ");

        //Lucky 9 
        lucky9Btn.addActionListener(e -> {
            log.append("\nYou chose to play Lucky 9!");
            if (player.coins >= 50) {               
                player.coins -= 50;
                int BankerRoll = (int)(Math.random() * 20) + 1;
                int roll = (int)(Math.random() * 20) + 1;
                int diff1 = Math.abs(9 - roll);
                int diff2 = Math.abs(9 - BankerRoll);

                log.append("\n\nBanker rolled a " + BankerRoll + "!");
                log.append("\nYou rolled a " + roll + "!");
                

                if(diff1 < diff2) {
                    
                    player.coins += 50;
                    log.append("\nYou are closer to 9! You win 50 gold!");
                } else

                if(diff1 > diff2) {
                    log.append("\nThe banker is closer to 9, you lost this round.");
                } else {
                    log.append("\nIt's a tie! The bank keeps your bet.");
                }
                statPanel.updateStats();
            } else {
                log.append("\nNot enough gold to play Lucky 9.");
            }
        });
        exitBtn.addActionListener(e -> game.returnToMap());
    }

    // ... rest of the methods remain the same ...

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(new Color(240, 220, 140));
        btn.setFont(new Font("Consolas", Font.BOLD, 26));
        btn.setFocusPainted(false);
        return btn;
    }
}
