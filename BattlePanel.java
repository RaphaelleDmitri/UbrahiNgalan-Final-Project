import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.*;

/**
 * BattlePanel (styled JTextPane log + multi-enemy support + target selector)
 *
 * - Supports both old constructor BattlePanel(Main,Player,Enemy) for compatibility
 *   and the new BattlePanel(Main,Player,List<Enemy>) which is preferred.
 *
 * - Uses ForwardingTextArea so existing Player/Enemy code that calls log.append(...)
 *   still works but routes text into the styled JTextPane.
 *
 * - Target selection is provided via targetBox (JComboBox). Player chooses which
 *   enemy to attack when clicking Attack.
 *
 * - Styling heuristics color messages by type (player, normal enemy, boss, heavy boss, phase, system).
 *
 * Keep this file as a full replacement.
 */
public class BattlePanel extends JPanel {
    private Main game;
    private Player player;

    // Active enemies list (witch + minion(s) etc.)
    private List<Enemy> enemies;
    private JComboBox<String> targetBox;

    // Keep log field type JTextArea for compatibility; it's actually a ForwardingTextArea.
    private JTextArea log;

    // Styled pane for rendering colored text:
    private JTextPane styledPane;
    private StyledDocument doc;

    private JLabel stats;
    Random rand = new Random();

    // track last player action: 0 = none, 1=attack,2=defend,3=heal,4=flee
    private int lastPlayerAction = 0;

    // -----------------------
    // Constructors
    // -----------------------
    // Backwards-compatible constructor (single enemy)
    public BattlePanel(Main game, Player player, Enemy enemy) {
        this(game, player, List.of(enemy));
    }

    // Preferred constructor (multiple enemies)
    public BattlePanel(Main game, Player player, List<Enemy> enemies) {
        this.game = game;
        this.player = player;
        // copy list defensively
        this.enemies = new ArrayList<>(enemies);

        setLayout(new BorderLayout());
        setBackground(new Color(25,25,25));

        stats = new JLabel(updateStatsForEnemies(), SwingConstants.CENTER);
        stats.setForeground(new Color(230,205,70));
        stats.setFont(new Font("Consolas", Font.BOLD, 20));
        stats.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(stats, BorderLayout.NORTH);

        // ----- Styled log setup -----
        styledPane = new JTextPane();
        styledPane.setEditable(false);
        styledPane.setBackground(new Color(40,40,40));
        styledPane.setForeground(Color.WHITE);
        styledPane.setFont(new Font("Consolas", Font.PLAIN, 25)); // Consolas 25 as requested
        styledPane.setBorder(BorderFactory.createLineBorder(new Color(200,200,100), 2));
        doc = styledPane.getStyledDocument();

        // Create styles
        createStyles(doc);

        // Wrap styledPane in scroll pane and add to center
        JScrollPane scroll = new JScrollPane(styledPane);
        add(scroll, BorderLayout.CENTER);

        // Create a forwarding JTextArea so existing calls like log.append(...) still work.
        log = new ForwardingTextArea();

        // ----- Target selector (right side) -----
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(25,25,25));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        targetBox = new JComboBox<>();
        targetBox.setFont(new Font("Consolas", Font.BOLD, 20));
        targetBox.setBackground(new Color(60,60,60));
        targetBox.setForeground(new Color(240,220,140));
        updateTargetBox();
        rightPanel.add(new JLabel("Target:"), BorderLayout.NORTH);
        rightPanel.add(targetBox, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // ----- Buttons -----
        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(25,25,25));

        JButton attackBtn = styledBtn("Attack");
        JButton defendBtn = styledBtn("Defend");
        JButton healBtn   = styledBtn("Heal");
        JButton fleeBtn = styledBtn("Flee");

        buttons.add(attackBtn);
        buttons.add(defendBtn);
        buttons.add(healBtn);
        buttons.add(fleeBtn);
        add(buttons, BorderLayout.SOUTH);

        attackBtn.addActionListener(e -> doTurn(1));
        defendBtn.addActionListener(e -> doTurn(2));
        healBtn.addActionListener(e -> doTurn(3));
        fleeBtn.addActionListener(e -> doTurn(4));

        // appearance text (use first enemy for appearance if present)
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

        if (!this.enemies.isEmpty()) {
            int index = rand.nextInt(appearanceTexts.length);
            log.setText(String.format(appearanceTexts[index], this.enemies.get(0).getName()));
        } else {
            log.setText("No enemies? Strange...\n");
        }

        // If first enemy is a boss, show intro (will be colored via forwarding)
        if (!this.enemies.isEmpty() && this.enemies.get(0) instanceof BossEnemy) {
            log.append("\n\"" + "This world will be mine." + "\"\n");
            log.append("\nRenz eyes you coldly — read his moves in the log.\n");
        }
    }

    private JButton styledBtn(String txt){
        JButton btn = new JButton(txt);
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(new Font("Consolas", Font.BOLD, 32));
        btn.setFocusPainted(false);
        return btn;
    }

    // -------------------------
    // Core turn logic
    // -------------------------
    private void doTurn(int action){
        if(!player.isAlive() || enemies.isEmpty()) return;

        // Player acts
        lastPlayerAction = action;
        switch(action){
            case 1 -> { // attack: target chosen from targetBox
                int idx = targetBox.getSelectedIndex();
                if (idx >= 0 && idx < enemies.size()) {
                    Enemy chosen = enemies.get(idx);
                    player.attack(chosen, log);

                    // if chosen is boss and punish active -> clear punish if applicable
                    if (chosen instanceof BossEnemyWitch bw && bw.isPunishActive()) bw.clearPunish();
                    else if (chosen instanceof BossEnemy boss && boss.isPunishActive()) boss.clearPunish();
                } else {
                    // fallback: attack first enemy
                    Enemy chosen = enemies.get(0);
                    player.attack(chosen, log);
                }
            }
            case 2 -> player.defend(log);
            case 3 -> player.heal(log);
            case 4 -> player.flee(log);
        }

        stats.setText(updateStatsForEnemies());

        // Enemy turn (process in stable order). We'll iterate over a *copy* of list indices to avoid concurrent modification.
        int initialEnemyCount = enemies.size();
        // Use for-loop with index because enemies list can change as bosses summon minions
        for (int i = 0; i < enemies.size(); i++) {
            // if player died mid-loop, stop
            if (!player.isAlive()) break;

            Enemy e = enemies.get(i);
            if (e instanceof BossEnemyWitch witch) {
                // Boss-specific turn
                witch.bossTurn(player, log, lastPlayerAction);

                // Check pending summon from witch
                MinionEnemy minion = witch.getPendingSummon();
                if (minion != null) {
                    // Add minion to active enemies (end of list)
                    enemies.add(minion);
                    log.append("\n\nA foul spawn has been summoned and joins the battle!");
                    updateTargetBox();
                }
            } else if (e instanceof BossEnemy boss) {
                // generic boss with bossTurn (if implemented)
                try {
                    // attempt to call bossTurn via reflection? but if BossEnemy has bossTurn, cast:
                    boss.bossTurn(player, log, lastPlayerAction);
                } catch (Exception ex) {
                    // fallback to normal attack
                    e.attack(player, log);
                }
            } else {
                // normal enemy
                e.attack(player, log);
            }
            stats.setText(updateStatsForEnemies());
        }

        // Remove dead enemies (and drop items / coins if you want; preserve your previous reward logic)
        List<Enemy> dead = new ArrayList<>();
        for (Enemy e : enemies) if (!e.isAlive()) dead.add(e);
        for (Enemy d : dead) {
            enemies.remove(d);
            // Optionally: reward on each kill (kept minimal here; you can add special boss rewards below)
            // game.addCoins( ??? ) // keep values unchanged as requested
        }

        // Check victory
        if (enemies.isEmpty()) {
            int reward = rand.nextInt(10) + 2000;
        
            boolean wasBoss = false;
            boolean gleihDefeated = false;
        
            for (Enemy e : dead) {
                if (e instanceof BossEnemyWitch) {
                    gleihDefeated = true;
                    break; // Gleih-specific message takes priority
                } else if (e instanceof BossEnemy) {
                    wasBoss = true;
                }
            }
        
            if (gleihDefeated) {
                log.append("\n\n>> DING DONG, The Dancing Witch is Dead!");
                log.append("\n\n>> VICTORY!");
                log.append("\nYou found something... a legendary armor?");
            } else if (wasBoss) {
                log.append("\n\n>> The Corrupted King collapses... The final blow!");
                log.append("\n\n>> VICTORY!");
                log.append("\nYou found something... a legendary weapon?");
            } else {
                log.append("\n\n>> VICTORY!");
            }
        
            log.append("\n You have obtained " + reward + " coins! ");
            game.addCoins(reward);
        
            // delay to return to map so player can read
            Timer t2 = new Timer(10000, ev -> game.returnToMap());
            t2.setRepeats(false);
            t2.start();
            return;
        }

        // Update UI (target list + stats)
        updateTargetBox();
        stats.setText(updateStatsForEnemies());

        if(!player.isAlive()) {
            log.append("\n\n>> GAME OVER");
        }
    }

    // Update stats label to show hero + all enemy HPs (keeps format compact)
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

    // -------------------------
    // Styled document helpers
    // -------------------------
    private void createStyles(StyledDocument d) {
        // base/default style
        Style def = d.addStyle("default", null);
        StyleConstants.setFontFamily(def, "Consolas");
        StyleConstants.setFontSize(def, 25);
        StyleConstants.setForeground(def, Color.WHITE);

        // player (yellow)
        Style playerStyle = d.addStyle("player", def);
        StyleConstants.setForeground(playerStyle, Color.YELLOW);

        // normal enemy (orange)
        Style enemyStyle = d.addStyle("enemy", def);
        StyleConstants.setForeground(enemyStyle, new Color(255, 140, 0)); // orange

        // boss normal (red)
        Style bossStyle = d.addStyle("boss", def);
        StyleConstants.setForeground(bossStyle, Color.RED);
        StyleConstants.setBold(bossStyle, true);

        // boss heavy / punish (darker red)
        Style bossHeavy = d.addStyle("bossHeavy", bossStyle);
        StyleConstants.setForeground(bossHeavy, new Color(165, 20, 20));

        // phase (purple)
        Style phase = d.addStyle("phase", def);
        StyleConstants.setForeground(phase, new Color(160, 32, 240)); // purple
        StyleConstants.setBold(phase, true);

        // system / reward (green)
        Style system = d.addStyle("system", def);
        StyleConstants.setForeground(system, new Color(100, 225, 100));
        StyleConstants.setBold(system, true);
    }

    // Heuristic-based append routing inspects a raw string and chooses style; now checks against active enemies.
    private void appendStyledByHeuristics(String raw) {
        if (raw == null || raw.isEmpty()) return;

        // Split incoming raw by newlines, keep line breaks
        String[] parts = raw.split("(?<=\\n)"); // keep trailing newline with each chunk
        for (String part : parts) {
            String trimmed = part.stripLeading();
            try {
                // Player actions
                if (trimmed.startsWith("You")) {
                    doc.insertString(doc.getLength(), part, doc.getStyle("player"));
                    continue;
                }

                // System / victory lines
                if (trimmed.contains("VICTORY") || trimmed.contains("You have obtained") || trimmed.startsWith(">>")) {
                    doc.insertString(doc.getLength(), part, doc.getStyle("system"));
                    continue;
                }

                // Try match against any active enemy name (prefer bosses)
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
                        // boss lines: heavy/punish keywords -> darker red
                        if (up.contains("PUNISH") || up.contains("HEAVY") || up.contains("DELIVERS") || up.contains("PUNISHES")) {
                            doc.insertString(doc.getLength(), part, doc.getStyle("bossHeavy"));
                        } else if (up.contains("ENTERS") || up.contains("PHASE")) {
                            doc.insertString(doc.getLength(), part, doc.getStyle("phase"));
                        } else {
                            // highlight boss name in line
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
                        // normal enemy -> orange
                        doc.insertString(doc.getLength(), part, doc.getStyle("enemy"));
                    }
                    continue;
                }

                // Lines that begin with an enemy name (fallback)
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

                // fallback: default white
                doc.insertString(doc.getLength(), part, doc.getStyle("default"));

            } catch (BadLocationException ex) {
                // worst case: append plain text
                styledPane.setText(styledPane.getText() + part);
            }
        }

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> styledPane.setCaretPosition(doc.getLength()));
    }

    // -------------------------
    // Inner forwarding JTextArea
    // -------------------------
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

    // -------------------------
    // Utility UI helpers
    // -------------------------
    private void updateTargetBox() {
        SwingUtilities.invokeLater(() -> {
            int selectedIndex = targetBox.getSelectedIndex(); // remember previous selection
            targetBox.removeAllItems();
            for (Enemy e : enemies) {
                
                if(e.getHealth()<=0){
                    targetBox.addItem(e.getName() + " (HP: DEAD! ) ");                    
                 } else{    
                    targetBox.addItem(e.getName() + " (HP: " + e.getHealth() + ")");
                 }
            }
            // restore selection if possible
            if (targetBox.getItemCount() > 0) {
                if (selectedIndex >= 0 && selectedIndex < targetBox.getItemCount()) {
                    targetBox.setSelectedIndex(selectedIndex);
                } else {
                    targetBox.setSelectedIndex(0);
                }
            }
        });
    }
}
