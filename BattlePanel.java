import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
public class BattlePanel extends JPanel {
    private Main game;
    private Player player;
    private List<Enemy> enemies;
    private JComboBox<String> targetBox;
    private JTextArea log;
    private JTextPane styledPane;
    private StyledDocument doc;
    private JLabel stats;
    Random rand = new Random();
    private int lastPlayerAction = 0;
    private JPanel buttons; // moved to field so we can toggle buttons on game over
    private JButton restartBtn; // shown when player dies
    private boolean hasFled = false;
    private BufferedImage backgroundImage; // Background image for battle
    private boolean useImageBackground = false; // Flag to determine if image background should be used

    public BattlePanel(Main game, Player player, Enemy enemy) {
        this(game, player, List.of(enemy));
    }
    public BattlePanel(Main game, Player player, List<Enemy> enemies) {
        this.game = game;
        this.player = player;
        this.enemies = new ArrayList<>(enemies);
        setLayout(new BorderLayout());
        
        // Choose and load a background image based on the enemy
        String backgroundImagePath = chooseBackgroundForEnemies(enemies);
        if (backgroundImagePath != null) {
            try {
                backgroundImage = ImageIO.read(new File(backgroundImagePath));
                useImageBackground = backgroundImage != null;
            } catch (IOException e) {
                System.err.println("Failed to load background image: " + e.getMessage());
                useImageBackground = false;
            }
        }

        // Keep everything transparent so only the image shows through
        setOpaque(false);
        stats = new JLabel(updateStatsForEnemies(), SwingConstants.CENTER);
        stats.setForeground(new Color(230,205,70));
        stats.setFont(GameFonts.press(20f));
        stats.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        stats.setOpaque(false);
        add(stats, BorderLayout.NORTH);
        styledPane = new JTextPane();
        styledPane.setEditable(false);
        styledPane.setOpaque(false);
        styledPane.setBackground(new Color(0, 0, 0, 0));
        styledPane.setForeground(Color.WHITE);
        styledPane.setFont(GameFonts.press(25f));
        styledPane.setBorder(null);
        doc = styledPane.getStyledDocument();
        createStyles(doc);
        JScrollPane scroll = new JScrollPane(styledPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
        log = new ForwardingTextArea();
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        targetBox = new JComboBox<>();
        targetBox.setFont(GameFonts.press(20f));
        targetBox.setOpaque(false);
        targetBox.setBackground(new Color(0, 0, 0, 0));
        targetBox.setForeground(Color.WHITE);
        updateTargetBox();
        rightPanel.add(new JLabel("Target:"), BorderLayout.NORTH);
        rightPanel.add(targetBox, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton attackBtn = styledBtn("Attack");
        JButton defendBtn = styledBtn("Defend");
        JButton healBtn   = styledBtn("Heal");
        JButton fleeBtn = styledBtn("Flee");
        restartBtn = styledBtn("Restart");
        restartBtn.setVisible(false);
        buttons.add(attackBtn);
        buttons.add(defendBtn);
        buttons.add(healBtn);
        buttons.add(fleeBtn);
        buttons.add(restartBtn);
        add(buttons, BorderLayout.SOUTH);
        attackBtn.addActionListener(e -> doTurn(1));
        defendBtn.addActionListener(e -> doTurn(2));
        healBtn.addActionListener(e -> doTurn(3));
        fleeBtn.addActionListener(e -> doTurn(4));
        restartBtn.addActionListener(e -> {
            // Reset game to main menu
            game.resetGame();
        });
       
        
        if (!this.enemies.isEmpty()) {
            Enemy first = this.enemies.get(0);
            
            String[] appearanceTexts = {
                "%s appears!\n",
                "%s has appeared!\n",
                "You encounter %s!\n",
                "Suddenly, %s jumps in front of you!\n",
                "%s blocks your path!\n",
                "From the shadows, %s emerges!\n",
                "You hear a rustle… it's %s!\n",
                "Prepare yourself! %s appears!\n",
                "%s is approaching!\n",
                "Enemy spotted! It's %s!\n"
            };
            
            int index = rand.nextInt(appearanceTexts.length);
            log.setText(String.format(appearanceTexts[index], first.getName()));
        
            // Boss-specific intro
            if (first instanceof BossEnemy) {
                log.append("\n\"This world will be mine.\"\n");
                log.append("\nRenz eyes you coldly — read his moves in the log.\n");
            } else if (first instanceof BossEnemyWitch) {
                log.append("\n\"Care to dance a little?\"\n");
                log.append("\nGleih eyes you coldly — read his moves in the log.\n");
            } 
        }
    }
    private JButton styledBtn(String txt){
        JButton btn = new JButton(txt);
        // Give breathing room between text and border
        var innerPadding = BorderFactory.createEmptyBorder(8, 20, 8, 20);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240,220,140), 2),
            innerPadding));
        btn.setFocusPainted(false);
        btn.setForeground(new Color(240,220,140));
        btn.setFont(GameFonts.press(20f));
        btn.setMargin(new Insets(6, 18, 6, 18));
        return btn;
    }

    // Decide which battle backdrop to show based on the enemies present
    private String chooseBackgroundForEnemies(List<Enemy> enemies) {
        for (Enemy e : enemies) {
            String name = e.getName().toLowerCase();
            if (e instanceof BossEnemyFinal || name.contains("void")) {
                return "void.png";
            }
            if (e instanceof BossEnemy || name.contains("king") || name.contains("renz")) {
                return "king.png";
            }
            if (e instanceof BossEnemyWitch || name.contains("witch") || name.contains("gleih")) {
                return "witch.png";
            }
        }
        // Generic fallback image for other enemy types
        return "orcslimegoblin.png";
    }
    
    private void doTurn(int action) {
        if (!player.isAlive() || enemies.isEmpty() || hasFled) return; // Prevent actions if the player has fled
    
        lastPlayerAction = action;
        switch (action) {
            case 1 -> { 
                int idx = targetBox.getSelectedIndex();
                if (idx >= 0 && idx < enemies.size()) {
                    Enemy chosen = enemies.get(idx);
                    player.attack(chosen, log);
    
                    if (chosen instanceof BossEnemyWitch bw && bw.isPunishActive()) bw.clearPunish();
                    else if (chosen instanceof BossEnemy boss && boss.isPunishActive()) boss.clearPunish();
                } else {
                    Enemy chosen = enemies.get(0);
                    player.attack(chosen, log);
                }
            }
            case 2 -> player.defend(log);
            case 3 -> player.heal(log);
            case 4 -> {
                boolean containsBoss = enemies.stream().anyMatch(e -> e instanceof BossEnemy || e instanceof BossEnemyWitch || e instanceof BossEnemyFinal);

                int fleeChance = containsBoss ? 5 : 40; // Reduced chance if boss present

                log.append("\n\n>> You attempt to flee...");
                if (rand.nextInt(100) <fleeChance) {  
                    log.append("\n>> You successfully fled the battle!");
                    hasFled = true; // Mark as fled
                    disablePlayerControls(); // Disable further actions
                    Timer t = new Timer(3000, e -> game.returnToMap()); // Return to map after 3 seconds
                    t.setRepeats(false);
                    t.start();
                    return;
                } else {
                    log.append("\n>> You failed to flee!");
                }
            }
        }
        stats.setText(updateStatsForEnemies());
        updateTargetBox();
        // Enemy turn - only alive enemies attack
        for (int i = 0; i < enemies.size(); i++) {
            if (!player.isAlive()) break;
            Enemy e = enemies.get(i);
    
            // Skip dead enemies
            if (!e.isAlive()) continue;
    
            if (e instanceof BossEnemyWitch witch) {
                witch.bossTurn(player, log, lastPlayerAction);
                MinionEnemy minion = witch.getPendingSummon();
                if (minion != null) {
                    enemies.add(minion);
                    log.append("\n\nA foul spawn has been summoned and joins the battle!");
                    updateTargetBox();
                }
            } else if (e instanceof BossEnemy boss) {
                boss.bossTurn(player, log, lastPlayerAction);
                stats.setText(updateStatsForEnemies());
                updateTargetBox();
            }else if(e instanceof BossEnemyFinal finalBoss) {
                finalBoss.bossTurn(player, log, lastPlayerAction);
                stats.setText(updateStatsForEnemies());
                updateTargetBox();
            } 
            else {
                e.attack(player, log);
            }
            stats.setText(updateStatsForEnemies());
            updateTargetBox();
        }
    
        // Remove dead enemies from the list
        List<Enemy> dead = new ArrayList<>();
        for (Enemy e : enemies) if (!e.isAlive()) dead.add(e);
        for (Enemy d : dead) {
            enemies.remove(d);
        }
        if (enemies.isEmpty()) {
            int reward = rand.nextInt(10) + 2000;
        
            boolean eumDefeated = false;
            boolean wasBoss = false;
            boolean gleihDefeated = false;
            boolean renzDefeated = false; // NEW: Track if Renz was defeated
        
            for (Enemy e : dead) {
                System.out.println("DEBUG BattlePanel: Checking dead enemy: " + e.getName()); // DEBUG
                if (e instanceof BossEnemyWitch) {
                    gleihDefeated = true;
                    break; 
                } else if (e instanceof BossEnemy) {
                    wasBoss = true;
                    // NEW: Check if it's specifically Renz
                    if (e.getName().contains("Renz") || e.getName().contains("Corrupted King")) {
                        System.out.println("DEBUG BattlePanel: RENZ DETECTED!"); // DEBUG
                        renzDefeated = true;
                    }
                } else if(e instanceof BossEnemyFinal){
                    // FINAL BOSS DEFEATED
                    endFinalBossBattle();
                    return;
                }
            }
        
            if (gleihDefeated) {
                log.append("\n\n>> DING DONG, The Dancing Witch is Dead!");
                log.append("\n\n>> VICTORY!");
                log.append("\n>> Darkness rises from Gleih's fallen form...");
                disablePlayerControls();
                Timer t = new Timer(5000, e -> {
                    game.startBossBattle3();
                });
                t.setRepeats(false);
                t.start();
                return;
                
            } else if (wasBoss) {
                log.append("\n\n>> The Corrupted King collapses... The final blow!");
                log.append("\n\n>> VICTORY!");
                player.weapons.add(new Weapon("Blade of Oblivion", 100, 9999, false));
                player.armors.add(new Armor("Aegis of Eternity", 100, 9999, false));
                log.append("\nYou scavenged The Blade of Oblivion and The Aegis of Eternity from the Fallen King!, both are added to inventory.");
                disablePlayerControls();
                if (renzDefeated) {
                    game.onRenzDefeated();
                } else {
                    System.out.println("DEBUG BattlePanel: renzDefeated is FALSE <RECHECK PO>"); // DEBUG
                }
            } else if (eumDefeated) {
                log.append("\n\n>> Eum lets out a final, deafening scream...");
                log.append("\n\n>> The battlefield grows silent.");
                log.append("\n>> Your journey has reached its end.");
                disablePlayerControls();
                JButton endButton = new JButton("End Game");
                endButton.setFont(new Font("Serif", Font.BOLD, 18));
    
                endButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(
                        this,
                        "Thank you for playing.\n\nThe light endures.",
                        "THE END",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    System.exit(0);
                });
    
                this.add(endButton);
                this.revalidate();
                this.repaint();
                return;
            }
            else {
                log.append("\n\n>> VICTORY!");
            }
        
            log.append("\n You have obtained " + reward + " coins! ");
            game.addCoins(reward);
            disablePlayerControls();
            Timer t2 = new Timer(5000, ev -> game.returnToMap());
            t2.setRepeats(false);
            t2.start();
            return;
        }
        updateTargetBox();
        stats.setText(updateStatsForEnemies());
        if(!player.isAlive()) {
            log.append("\n\n>> GAME OVER");
            // Show restart button and disable other actions
            SwingUtilities.invokeLater(() -> {
                for (Component c : buttons.getComponents()) {
                    if (c instanceof JButton b) {
                        if (b != restartBtn) b.setVisible(false);
                    }
                }
                // Optionally hide target selector as well
                if (targetBox != null) targetBox.setVisible(false);
                restartBtn.setVisible(true);
                restartBtn.setEnabled(true);
                buttons.revalidate();
                buttons.repaint();
            });
        }
    }
    private String updateStatsForEnemies(){
        StringBuilder sb = new StringBuilder();
        sb.append("Hero HP: ").append(player.getHealth()).append(" Potions Left:").append(player.potionAmount);
        sb.append(" | Enemies: ");
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            sb.append(e.getName()).append("(").append(e.getHealth()).append(")");
            if (i < enemies.size()-1) sb.append(" ");
        }
        return sb.toString();
    }
    
    private void createStyles(StyledDocument d) {
        Style def = d.addStyle("default", null);
        StyleConstants.setFontFamily(def, GameFonts.press(30f).getFamily());
        StyleConstants.setFontSize(def, 24);
        StyleConstants.setForeground(def, Color.WHITE);
        Style playerStyle = d.addStyle("player", def);
        StyleConstants.setForeground(playerStyle, Color.YELLOW);
        Style enemyStyle = d.addStyle("enemy", def);
        StyleConstants.setForeground(enemyStyle, new Color(255, 140, 0)); // orange
        Style bossStyle = d.addStyle("boss", def);
        StyleConstants.setForeground(bossStyle, Color.RED);
        StyleConstants.setBold(bossStyle, true);
        Style bossHeavy = d.addStyle("bossHeavy", bossStyle);
        StyleConstants.setForeground(bossHeavy, new Color(165, 20, 20));
        Style phase = d.addStyle("phase", def);
        StyleConstants.setForeground(phase, new Color(160, 32, 240));
        StyleConstants.setBold(phase, true);
        Style system = d.addStyle("system", def);
        StyleConstants.setForeground(system, new Color(100, 225, 100));
        StyleConstants.setBold(system, true);
    }
    private void appendStyledByHeuristics(String raw) {
        if (raw == null || raw.isEmpty()) return;
        String[] parts = raw.split("(?<=\\n)");
        for (String part : parts) {
            String trimmed = part.stripLeading();
            try {
                
                if (trimmed.startsWith("You")) {
                    doc.insertString(doc.getLength(), part, doc.getStyle("player"));
                    continue;
                }
                
                if (trimmed.contains("VICTORY") || trimmed.contains("You have obtained") || trimmed.startsWith(">>")) {
                    doc.insertString(doc.getLength(), part, doc.getStyle("system"));
                    continue;
                }
                
                Enemy matched = null;
                boolean matchedIsBoss = false;
                for (Enemy e : enemies) {
                    String n = e.getName();
                    if (n != null && !n.isEmpty() && trimmed.contains(n)) {
                        matched = e;
                        if (e instanceof BossEnemy) matchedIsBoss = true;
                        break;
                    }
                }
                if (matched != null) {
                    String up = trimmed.toUpperCase();
                    if (matchedIsBoss) {
                        
                        if (up.contains("PUNISH") || up.contains("HEAVY") || up.contains("DELIVERS") || up.contains("PUNISHES")) {
                            doc.insertString(doc.getLength(), part, doc.getStyle("bossHeavy"));
                        } else if (up.contains("ENTERS") || up.contains("PHASE")) {
                            doc.insertString(doc.getLength(), part, doc.getStyle("phase"));
                        } else {
                            
                            int idx = part.indexOf(matched.getName());
                            if (idx >= 0) {
                                String before = part.substring(0, idx);
                                String name = part.substring(idx, idx + matched.getName().length());
                                String after = part.substring(idx + matched.getName().length());
                                if (!before.isEmpty()) doc.insertString(doc.getLength(), before, doc.getStyle("boss"));
                                doc.insertString(doc.getLength(), name, doc.getStyle("boss"));
                                if (!after.isEmpty()) doc.insertString(doc.getLength(), after, doc.getStyle("boss"));
                            } else {
                                doc.insertString(doc.getLength(), part, doc.getStyle("boss"));
                            }
                        }
                    } else {
                        
                        doc.insertString(doc.getLength(), part, doc.getStyle("enemy"));
                    }
                    continue;
                }
                boolean startsWithEnemy = false;
                for (Enemy e : enemies) {
                    if (trimmed.startsWith(e.getName())) {
                        startsWithEnemy = true;
                        break;
                    }
                }
                if (startsWithEnemy) {
                    doc.insertString(doc.getLength(), part, doc.getStyle("enemy"));
                    continue;
                }
                doc.insertString(doc.getLength(), part, doc.getStyle("default"));
            } catch (BadLocationException ex) {
                styledPane.setText(styledPane.getText() + part);
            }
        }
        SwingUtilities.invokeLater(() -> styledPane.setCaretPosition(doc.getLength()));
    }
    private class ForwardingTextArea extends JTextArea {
        public ForwardingTextArea() {
            super();
        }
        @Override
        public void append(String str) {
            appendStyledSafely(str);
        }
        @Override
        public void setText(String text) {
            try {
                doc.remove(0, doc.getLength());
            } catch (BadLocationException ex) {
                // ignore
            }
            appendStyledSafely(text);
        }
        private void appendStyledSafely(String s) {
            if (s == null) return;
            appendStyledByHeuristics(s);
        }
    }
    private void updateTargetBox() {
        SwingUtilities.invokeLater(() -> {
            int selectedIndex = targetBox.getSelectedIndex(); 
            targetBox.removeAllItems();
            for (Enemy e : enemies) {
                if(e.getHealth()<=0){
                    targetBox.addItem(e.getName() + " (HP: DEAD! ) ");                    
                 } else{    
                    targetBox.addItem(e.getName() + " (HP: " + e.getHealth() + ")");
                 }
            }
            if (targetBox.getItemCount() > 0) {
                if (selectedIndex >= 0 && selectedIndex < targetBox.getItemCount()) {
                    targetBox.setSelectedIndex(selectedIndex);
                } else {
                    targetBox.setSelectedIndex(0);
                }
            }
        });
    }

    public JTextArea getLog() {
        return log;
    }
    private void endFinalBossBattle() {
        disablePlayerControls();
        log.append("\n\n>> Eum lets out a final, deafening scream...");
        log.append("\n>> The shadows collapse in on themselves.");
        log.append("\n\n>> Eum, The VoidMother, HAS FINALLY FALLEN.");
        
            // Lura's final moments
        log.append("\n\n>> Lura staggers, his light flickering...");
        log.append("\n\"It seems... this is as far as I go.\"");
        log.append("\n\"You must finish what I could not.\"");
        log.append("\n\"Live... not just survive.\"");
        log.append("\n\n>> Lura smiles one last time before fading into light.");
    
        
        Timer delayTimer = new Timer(2000, e -> {
            log.append("\n\n>> The battlefield grows silent.");
            log.append("\n>> Your journey has reached its end.");
            JButton endButton = new JButton("End Game");
            endButton.setFont(new Font("Serif", Font.BOLD, 18));
        
            endButton.addActionListener(ev -> {
                JOptionPane.showMessageDialog(
                    this,
                    "Thank you for playing.\n\nThe light endures.",
                    "THE END",
                    JOptionPane.INFORMATION_MESSAGE
                );
                System.exit(0);
            });
        
            this.add(endButton);
            this.revalidate();
            this.repaint();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    private void disablePlayerControls() {
        SwingUtilities.invokeLater(() -> {
            if (targetBox != null) targetBox.setEnabled(false);
            if (buttons != null) {
                for (Component c : buttons.getComponents()) {
                    c.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (useImageBackground && backgroundImage != null) {
            // Draw the background scaled to the panel size
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
