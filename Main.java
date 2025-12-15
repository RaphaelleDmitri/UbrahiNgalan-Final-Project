import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    public Player player;
    private List<Enemy> enemies;
    MainMenuPanel mainMenu;
    private MapPanel gamePanel;  // Changed from JPanel to MapPanel
    BattlePanel battlePanel;
    ShopPanel shopPanel;
    NPCConversation npcPanel;

    private List<NPC> npcList;  // MUST be initialized
    private boolean renzDefeated = false; // Track if Renz was defeated

    public Main() {

        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        setTitle("A Java RPG");
        setSize(1820, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        //enemy list
        //linked list nisya nga saket lng ulo kag ara iya
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));


        // Boss 1
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        // Boss 2
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 0, 0));
        // Boss 3
        //Eum, The VoidMother, health 800, attack 70, defense 30
        enemies.add(new Enemy("Eum, The VoidMother", 700, 40, 20, 0, 0));
        
        // Initialize NPCs and dialogue nodes
            initNPCs();
            npcPanel = new NPCConversation(this, player, npcList);
        // -----------------------------------------------------
        showMainMenu();
        setVisible(true);
    }

    public Enemy getEnemy(int index) {
        if(index >= 0 && index < enemies.size()) return enemies.get(index);
        return null;
    }

    public Enemy createEnemy(int index) {
        if(index < 0 || index >= enemies.size()) return null;

        Enemy t = enemies.get(index);
        return new Enemy(t.name, t.health, t.attackPower, t.defense, 0, 0);
    }

    public List<Enemy> getEnemies() { return enemies; }

    public void showMainMenu() {
        mainMenu = new MainMenuPanel(this);
        setContentPane(mainMenu);
        revalidate();
    }

    public void showGamePanel() {
        gamePanel = new MapPanel(this);
        setContentPane(gamePanel);
        revalidate();
    }

    public void startBattle(Enemy enemy) {        
        battlePanel = new BattlePanel(this, player, enemy);
        setContentPane(battlePanel);
        revalidate();
    }

    public void startConversation() {
        npcPanel = new NPCConversation(this, player, npcList);
        setContentPane(npcPanel);
        revalidate();
    }

    public void addPotion() {
        player.potionAmount++;
    }

    public void addCoins(int reward) {
        player.coins += reward;
    }

    // Reset the game state and return to main menu
    public void resetGame() {
        // Recreate player (fresh inventory/stats)
        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        // Recreate enemy templates (same as constructor)
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 0, 0));

        // Reset boss defeat flag
        renzDefeated = false;

        // Initialize NPCs and dialogue nodes
        initNPCs();
        npcPanel = new NPCConversation(this, player, npcList);

        // Clear any existing panels so state is rebuilt when needed
        battlePanel = null;
        shopPanel = null;
        gamePanel = null;

        // Show main menu to the player
        showMainMenu();
    }

    // Initialize NPCs and their dialogue nodes (moved to NPCDialogues.java)
    private void initNPCs() {
        npcList = NPCDialogues.createNPCs();
    }

    // Method called when Corrupted King Renz is defeated
    public void onRenzDefeated() {
        System.out.println("DEBUG: onRenzDefeated() called!"); // DEBUG
        renzDefeated = true; // Mark that Renz was defeated
    }

    
    // Return to map and spawn Spire if Renz was defeated
    public void returnToMap() {
        System.out.println("DEBUG: returnToMap() called"); // DEBUG
        System.out.println("DEBUG: renzDefeated = " + renzDefeated); // DEBUG
        System.out.println("DEBUG: gamePanel = " + gamePanel); // DEBUG
        
        // Ensure we have a valid game panel instance before setting it
        if (gamePanel == null) {
            gamePanel = new MapPanel(this);
        }
        setContentPane(gamePanel);
        revalidate();
        repaint();
        
        

        // Spawn spire after returning to map if Renz was defeated
        if (renzDefeated && gamePanel != null) {
            System.out.println("DEBUG: Spawning spire now!"); // DEBUG
            SwingUtilities.invokeLater(() -> {
                gamePanel.spawnSpire();
                renzDefeated = false; // Reset flag
            });
        }
        
        // CRITICAL: Request focus multiple times to ensure it works
        SwingUtilities.invokeLater(() -> {
            gamePanel.setFocusable(true);
            gamePanel.requestFocusInWindow();
        });
        
        // Double-check focus after a short delay
        Timer focusTimer = new Timer(100, e -> {
            gamePanel.requestFocusInWindow();
        });
        focusTimer.setRepeats(false);
        focusTimer.start();
    }

    public void startBossBattle() {
        Enemy template = enemies.get(3);
        BossEnemy renz = new BossEnemy(template.name, template.health, template.attackPower, template.defense, 0, 0);
        startBattle(renz);
    }

    public void startBossBattle2() {
        Enemy template = enemies.get(4);
        BossEnemyWitch gleih = new BossEnemyWitch(template.name, template.health, template.attackPower, template.defense, 0, 0);
        startBattle(gleih);
    }
    public void startBossBattle3() {
        Enemy template = enemies.get(5);
        BossEnemyFinal eum = new BossEnemyFinal(template.name, template.health, template.attackPower, template.defense, 0, 0);
        
        // Start the battle first
        startBattle(eum);
        
        // Call intro using the log from the BattlePanel
        if (battlePanel != null && battlePanel.getLog() != null) {
            eum.intro(battlePanel.getLog(), player);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
    
}