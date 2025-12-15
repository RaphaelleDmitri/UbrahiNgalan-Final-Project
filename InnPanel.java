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

        // Left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 20, 20));
        leftPanel.setBackground(new Color(25, 25, 25));
        leftPanel.setPreferredSize(new Dimension(600, 0));


        // Exit button
        JButton exitBtn = styledButton("EXIT INN");
        leftPanel.add(exitBtn);
        exitBtn.setFont(GameFonts.press(26f));


        // Lucky 9 Button
        JButton lucky9Btn = styledButton("Play Lucky 9 (50 Gold)");
        leftPanel.add(lucky9Btn);
        lucky9Btn.setFont(GameFonts.press(20f));

        
        //Roulette Button
        JButton rouletteBtn = styledButton("Play Roulette (100 Gold)");
        leftPanel.add(rouletteBtn);
        rouletteBtn.setFont(GameFonts.press(20f));


        add(leftPanel, BorderLayout.WEST);

        // Log area in the center
        log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setBackground(new Color(40, 40, 40));
        log.setForeground(Color.WHITE);
        log.setFont(GameFonts.press(18f));
        log.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 3));

        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(700, 0));
        add(scroll, BorderLayout.CENTER);

        log.setText("Welcome to the Sodusta Inn!\nYou can gamble your life savings here!");

        //Lucky 9 
        lucky9Btn.addActionListener(e -> {
            log.append("\nYou chose to play Lucky 9!");
            if (player.coins >= 50) {               
                player.coins -= 50;
                int BankerRoll = (int)(Math.random() * 9)+1;

                int roll = (int)(Math.random() * 9)+1;
                
                int diff1 = Math.abs(9 - roll);
                int diff2 = Math.abs(9 - BankerRoll);

                log.append("\n\nBanker rolled a " + BankerRoll + "!");
                log.append("\nYou rolled a " + roll + "!");
                

                if(diff1 < diff2) {
                    
                    player.coins += 100;
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

        rouletteBtn.addActionListener(e -> {
            log.append("\n\nYou chose to play Roulette!");
            log.append("\n\nRed pays 2:1, Black pays 2:1, Even pays 2:1, Odd pays 2:1, Number pays 35:1.");
            log.append("\n\nRed values are: 1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36.");
            log.append("\n\nBlack values are: 2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35.");
        
            if (player.coins < 100) {
                log.append("\n\n\nNot enough gold to play Roulette.");
                return;
            }
        
            // Ask player what bet type they want
            String[] betOptions = {"Pick a Number (0-36)", "Red", "Black", "Even", "Odd"};
            String betType = (String) JOptionPane.showInputDialog(
                this,
                "Choose your bet type:",
                "Roulette Betting",
                JOptionPane.QUESTION_MESSAGE,
                null,
                betOptions,
                betOptions[0]
            );
        
            if (betType == null) {
                log.append("\nRoulette cancelled.");
                return;
            }
        
            player.coins -= 100; // base bet
            log.append("\n\nYou bet 100 gold.");
        
            int chosenNumber = -1;
            boolean choseNumberBet = betType.equals("Pick a Number (0-36)");
        
            // If player chooses NUMBER, ask for number
            if (choseNumberBet) {
                String input = JOptionPane.showInputDialog("Pick a number between 0–36:");
        
                try {
                    chosenNumber = Integer.parseInt(input);
                    if (chosenNumber < 0 || chosenNumber > 36) throw new Exception();
                } catch (Exception ex) {
                    log.append("\nInvalid number — bet cancelled.");
                    player.coins += 100; // refund
                    statPanel.updateStats();
                    return;
                }
        
                log.append("\nYou chose NUMBER: " + chosenNumber);
            } else {
                log.append("\nYou chose: " + betType);
            }
        
            // Spin roulette
            int spin = (int)(Math.random() * 37);
            log.append("\nRoulette spun: " + spin);
        
            // Determine color
            boolean isRed = switch (spin) {
                case 1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36 -> true;
                default -> false; // black or 0
            };
        
            // Check results
            boolean won = false;
            int payout = 0;
        
            if (choseNumberBet) {
                // Straight-up number: 35x payout
                if (spin == chosenNumber) {
                    won = true;
                    payout = 3500; // includes profit
                }
            } else {
                // Red/Black/Even/Odd bets
                switch (betType) {
                    case "Red" -> {
                        if (spin != 0 && isRed) { won = true; payout = 200; }
                    }
                    case "Black" -> {
                        if (spin != 0 && !isRed) { won = true; payout = 200; }
                    }
                    case "Even" -> {
                        if (spin != 0 && spin % 2 == 0) { won = true; payout = 200; }
                    }
                    case "Odd" -> {
                        if (spin != 0 && spin % 2 == 1) { won = true; payout = 200; }
                    }
                }
            }
        
            // Handle results
            if (won) {
                player.coins += payout;
                log.append("\nYOU WON! You receive " + payout + " gold!");
            } else {
                log.append("\nYou lost the round.");
            }
        
            statPanel.updateStats();
        });
        
        exitBtn.addActionListener(e -> game.returnToMap());
    }

    // ... rest of the methods remain the same ...

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(new Color(240, 220, 140));
        btn.setFocusPainted(false);
        return btn;
    }
}
