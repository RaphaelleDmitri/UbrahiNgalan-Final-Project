import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class InnPanel extends JPanel {
    private Main game;
    private Player player;
    private JTextArea log;
    private JButton weaponBtn;
    private JButton armorBtn;
    private JLabel coinsLabel;

    private LinkedList<Weapon> weaponQueue;
    private LinkedList<Armor> armorQueue;
    private BufferedImage backgroundImage;

    public InnPanel(Main game, Player player) {
        this.game = game;
        this.player = player;

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("inn.png"));
        } catch (IOException e) {
            System.err.println("Failed to load inn background image: " + e.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        setOpaque(true);

        // Top panel for coins display
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setOpaque(false);
        
        coinsLabel = new JLabel("Gold: " + player.coins);
        coinsLabel.setForeground(new Color(255, 215, 0));
        coinsLabel.setFont(GameFonts.press(28f));
        topPanel.add(coinsLabel);
        
        add(topPanel, BorderLayout.NORTH);

        // Left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(6, 1, 20, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(600, 0));

        // Exit button
        JButton exitBtn = styledButton("EXIT INN");
        leftPanel.add(exitBtn);
        exitBtn.setFont(GameFonts.press(26f));

        // Lucky 9 Button
        JButton lucky9Btn = styledButton("Play Lucky 9 (50 Gold)");
        leftPanel.add(lucky9Btn);
        lucky9Btn.setFont(GameFonts.press(20f));
        
        // Roulette Button
        JButton rouletteBtn = styledButton("Play Roulette (100 Gold)");
        leftPanel.add(rouletteBtn);
        rouletteBtn.setFont(GameFonts.press(20f));
        
        // Card Sorting Challenge Button
        JButton sortingBtn = styledButton("Card Sorting (75 Gold)");
        leftPanel.add(sortingBtn);
        sortingBtn.setFont(GameFonts.press(20f));

        add(leftPanel, BorderLayout.WEST);

        // Log area in the center
        log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setOpaque(false);
        log.setForeground(Color.WHITE);
        log.setFont(GameFonts.press(18f));
        log.setBorder(null);

        JScrollPane scroll = new JScrollPane(log);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(700, 0));
        add(scroll, BorderLayout.CENTER);

        log.setText("Welcome to the Sodusta Inn!\nYou can gamble your life savings here!");

        // Lucky 9 
        lucky9Btn.addActionListener(e -> {
            log.append("\n\n--- LUCKY 9 ---");
            log.append("\nGet closer to 9 than the banker to win!");
            log.append("\nCost: 50 gold | Win: 100 gold\n");
            
            if (player.coins >= 50) {               
                player.coins -= 50;
                updateCoinsDisplay();
                int BankerRoll = (int)(Math.random() * 9)+1;
                int roll = (int)(Math.random() * 9)+1;
                
                int diff1 = Math.abs(9 - roll);
                int diff2 = Math.abs(9 - BankerRoll);

                log.append("\nBanker rolled a " + BankerRoll + "!");
                log.append("\nYou rolled a " + roll + "!");
                
                if(diff1 < diff2) {
                    player.coins += 100;
                    log.append("\nYou are closer to 9! You win 50 gold!");
                    updateCoinsDisplay();
                } else if(diff1 > diff2) {
                    log.append("\nThe banker is closer to 9, you lost this round.");
                } else {
                    log.append("\nIt's a tie! The bank keeps your bet.");
                }
            } else {
                log.append("\nNot enough gold to play Lucky 9.");
            }
        });

        rouletteBtn.addActionListener(e -> {
            log.append("\n\n--- ROULETTE ---");
            log.append("\nRed pays 2:1, Black pays 2:1");
            log.append("\nEven pays 2:1, Odd pays 2:1");
            log.append("\nNumber pays 35:1");
            log.append("\n\nRed: 1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36");
            log.append("\nBlack: 2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35");
        
            if (player.coins < 100) {
                log.append("\n\nNot enough gold to play Roulette.");
                return;
            }
        
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
        
            player.coins -= 100;
            updateCoinsDisplay();
            log.append("\n\nYou bet 100 gold.");
        
            int chosenNumber = -1;
            boolean choseNumberBet = betType.equals("Pick a Number (0-36)");
        
            if (choseNumberBet) {
                String input = JOptionPane.showInputDialog("Pick a number between 0–36:");
                try {
                    chosenNumber = Integer.parseInt(input);
                    if (chosenNumber < 0 || chosenNumber > 36) throw new Exception();
                } catch (Exception ex) {
                    log.append("\nInvalid number — bet cancelled.");
                    player.coins += 100;
                    updateCoinsDisplay();
                    return;
                }
                log.append("\nYou chose NUMBER: " + chosenNumber);
            } else {
                log.append("\nYou chose: " + betType);
            }
        
            int spin = (int)(Math.random() * 37);
            log.append("\nRoulette spun: " + spin);
        
            boolean isRed = switch (spin) {
                case 1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36 -> true;
                default -> false;
            };
        
            boolean won = false;
            int payout = 0;
        
            if (choseNumberBet) {
                if (spin == chosenNumber) {
                    won = true;
                    payout = 3500;
                }
            } else {
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
        
            if (won) {
                player.coins += payout;
                log.append("\nYOU WON! You receive " + payout + " gold!");
                updateCoinsDisplay();
            } else {
                log.append("\nYou lost the round.");
            }
        });
        
        sortingBtn.addActionListener(e -> {
            log.append("\n\n--- CARD SORTING CHALLENGE ---");
            log.append("\nSort cards in order from smallest to largest!");
            log.append("\nCost: 75 gold");
            log.append("\nRewards based on efficiency:");
            log.append("\n  Perfect sorting: 250 gold");
            log.append("\n  Excellent: 200 gold");
            log.append("\n  Good: 175 gold");
            
            if (player.coins < 75) {
                log.append("\n\nNot enough gold to play Card Sorting Challenge.");
                return;
            }
            
            // Show instructions dialog
            String instructions = "HOW TO PLAY:\n\n" +
                "1. You'll see shuffled cards (numbers 0-9)\n" +
                "2. The YELLOW card is the one you need to insert\n" +
                "3. Click the ↓ button where you want to insert it\n" +
                "4. Continue until all cards are sorted!\n\n" +
                "TIP: Insert each card in the correct position\n" +
                "to minimize moves and maximize rewards!";
            
            int choice = JOptionPane.showConfirmDialog(
                this,
                instructions,
                "Card Sorting Instructions",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (choice == JOptionPane.OK_OPTION) {
                new CardSortingGame(this, player, log, this);
            } else {
                log.append("\n\nCard Sorting cancelled.");
            }
        });
        
        exitBtn.addActionListener(e -> game.returnToMap());
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240,220,140), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setForeground(new Color(240, 220, 140));
        btn.setFocusPainted(false);
        return btn;
    }
    
    public void updateCoinsDisplay() {
        coinsLabel.setText("Gold: " + player.coins);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Draw the background scaled to the panel size
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// Interactive Card Sorting Game Dialog
class CardSortingGame extends JDialog {
    private ArrayList<Integer> cards;
    private ArrayList<Integer> sortedCards;
    private int currentIndex;
    private int moves;
    private int optimalMoves;
    private JPanel cardsPanel;
    private JLabel statusLabel;
    private JLabel movesLabel;
    private Player player;
    private JTextArea mainLog;
    private InnPanel innPanel;
    
    public CardSortingGame(JPanel parent, Player player, JTextArea mainLog, InnPanel innPanel) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Card Sorting Challenge", true);
        this.player = player;
        this.mainLog = mainLog;
        this.innPanel = innPanel;

        player.coins -= 75;
        innPanel.updateCoinsDisplay();
        mainLog.append("\nYou paid 75 gold to play.\n");
        
        // Generate random cards
        int cardCount = 5 + (int)(Math.random() * 4); // 5-8 cards
        cards = new ArrayList<>();
        for (int i = 0; i < cardCount; i++) {
            cards.add(1 + (int)(Math.random() * 9)); // Cards 1-9
        }
        
        sortedCards = new ArrayList<>(cards);
        currentIndex = 1;
        moves = 0;
        optimalMoves = calculateOptimalMoves();
        
        mainLog.append("Starting cards: " + cards);
        
        setupUI();
        setSize(950, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(25, 25, 25));
        
        // Top panel with instructions
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        topPanel.setBackground(new Color(25, 25, 25));
        
        JLabel titleLabel = new JLabel("Order of Cards", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(240, 220, 140));
        titleLabel.setFont(GameFonts.press(24f));
        
        JLabel instructionLabel = new JLabel("Click ↓ to insert the YELLOW card", SwingConstants.CENTER);
        instructionLabel.setForeground(new Color(150, 150, 150));
        instructionLabel.setFont(GameFonts.press(16f));
        
        statusLabel = new JLabel("Insert card: " + sortedCards.get(currentIndex), SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(GameFonts.press(20f));
        
        movesLabel = new JLabel("Moves: 0 | Optimal: " + optimalMoves, SwingConstants.CENTER);
        movesLabel.setForeground(new Color(100, 200, 255));
        movesLabel.setFont(GameFonts.press(18f));
        
        topPanel.add(titleLabel);
        topPanel.add(instructionLabel);
        topPanel.add(statusLabel);
        topPanel.add(movesLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel for cards
        cardsPanel = new JPanel();
        cardsPanel.setBackground(new Color(40, 40, 40));
        cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 30));
        updateCardsDisplay();
        
        add(cardsPanel, BorderLayout.CENTER);
        
        // Bottom panel with hint
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(25, 25, 25));
        JLabel hintLabel = new JLabel("TIP: Place cards in ascending order (small → large)");
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setFont(GameFonts.press(14f));
        bottomPanel.add(hintLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void updateCardsDisplay() {
        cardsPanel.removeAll();
        
        // Add insertion slots and cards
        for (int i = 0; i < sortedCards.size(); i++) {
            final int position = i;
            
            // Add insertion slot button before each card (only for unsorted portion)
            if (i <= currentIndex) {
                JButton slotBtn = new JButton("↓");
                slotBtn.setPreferredSize(new Dimension(50, 130));
                slotBtn.setBackground(new Color(80, 120, 80));
                slotBtn.setForeground(new Color(150, 255, 150));
                slotBtn.setFont(GameFonts.press(30f));
                slotBtn.setFocusPainted(false);
                slotBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 100), 2));
                slotBtn.addActionListener(e -> insertCardAt(position));
                cardsPanel.add(slotBtn);
            }
            
            // Add card
            JPanel cardPanel = createCardPanel(sortedCards.get(i), i == currentIndex);
            cardsPanel.add(cardPanel);
        }
        
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
    
    private JPanel createCardPanel(int value, boolean highlighted) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(100, 130));
        
        if (highlighted) {
            card.setBackground(new Color(255, 220, 80));
            card.setBorder(BorderFactory.createLineBorder(new Color(255, 180, 0), 4));
        } else {
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        }
        
        JLabel valueLabel = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        valueLabel.setFont(GameFonts.press(50f));
        valueLabel.setForeground(Color.BLACK);
        
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void insertCardAt(int position) {
        // Get the card to insert
        int cardToInsert = sortedCards.get(currentIndex);
        
        // Remove from current position
        sortedCards.remove(currentIndex);
        
        // Insert at new position
        sortedCards.add(position, cardToInsert);
        
        moves++;
        movesLabel.setText("Moves: " + moves + " | Optimal: " + optimalMoves);
        
        // Move to next card
        currentIndex++;
        
        // Check if done
        if (currentIndex >= sortedCards.size()) {
            finishGame();
        } else {
            statusLabel.setText("Insert card: " + sortedCards.get(currentIndex));
            updateCardsDisplay();
        }
    }
    
    private void finishGame() {
        // Check if sorted correctly
        boolean correct = true;
        for (int i = 1; i < sortedCards.size(); i++) {
            if (sortedCards.get(i) < sortedCards.get(i-1)) {
                correct = false;
                break;
            }
        }
        
        if (correct) {
            // Calculate reward based on efficiency
            int baseReward = 150;
            int efficiencyBonus = 0;
            String rating = "";
            
            if (moves == optimalMoves) {
                efficiencyBonus = 100;
                rating = "PERFECT";
                statusLabel.setText("★ PERFECT! Optimal sorting! ★");
            } else if (moves <= optimalMoves + 2) {
                efficiencyBonus = 50;
                rating = "EXCELLENT";
                statusLabel.setText("⭐ EXCELLENT! Very efficient! ⭐");
            } else {
                efficiencyBonus = 25;
                rating = "GOOD";
                statusLabel.setText("✓ GOOD! Cards sorted correctly!");
            }
            
            int totalReward = baseReward + efficiencyBonus;
            player.coins += totalReward;
            innPanel.updateCoinsDisplay();
            
            mainLog.append("\n\nResult: " + rating);
            mainLog.append("\nSorted cards: " + sortedCards);
            mainLog.append("\nMoves: " + moves + " (Optimal: " + optimalMoves + ")");
            mainLog.append("\nReward: " + totalReward + " gold!\n");
            
            movesLabel.setText("SUCCESS! Won " + totalReward + " gold!");
            
        } else {
            statusLabel.setText("✗ Cards not sorted correctly. No reward.");
            mainLog.append("\n\nSorting FAILED. Cards were not in order.");
            mainLog.append("\nFinal order: " + sortedCards + "\n");
        }
        
        // Close after delay
        Timer timer = new Timer(3000, e -> dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    private int calculateOptimalMoves() {
        // Calculate minimum moves needed (actual insertion sort moves)
        ArrayList<Integer> temp = new ArrayList<>(cards);
        int count = 0;
        
        for (int i = 1; i < temp.size(); i++) {
            int key = temp.get(i);
            int j = i - 1;
            
            while (j >= 0 && temp.get(j) > key) {
                temp.set(j + 1, temp.get(j));
                j--;
            }
            
            if (j + 1 != i) {
                count++;
            }
            
            temp.set(j + 1, key);
        }
        
        return count;
    }
}