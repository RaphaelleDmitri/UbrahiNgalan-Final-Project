import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    public Player player;
    private List<Enemy> enemies;
    MainMenuPanel mainMenu;
    private MapPanel gamePanel;  
    BattlePanel battlePanel;
    ShopPanel shopPanel;
    NPCConversation npcPanel;

    private List<NPC> npcList;  
    private boolean renzDefeated = false; 
    private boolean priestessAvailable = true; 
    private boolean gleihDefeated = false;
    private boolean eumDefeated = false;

    public Main() {

        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        setTitle("A Java RPG");
        setSize(1820, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));


        // Bosses 1-3
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 0, 0)); 
        enemies.add(new Enemy("Eum, The VoidMother", 700, 40, 20, 0, 0));
            
        initNPCs();
        npcPanel = new NPCConversation(this, player, npcList, null);
        
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
        npcPanel = new NPCConversation(this, player, npcList, null);
        setContentPane(npcPanel);
        revalidate();
    }

    // Start conversation
    public void startConversation(String npcName) {
        npcPanel = new NPCConversation(this, player, npcList, npcName);
        setContentPane(npcPanel);
        revalidate();
    }

    public void addPotion() {
        player.potionAmount++;
    }

    public void addCoins(int reward) {
        player.coins += reward;
    }

    // Reset the game 
    public void resetGame() {
        // Recreate player 
        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        // Recreate enemy templates
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 0, 0));

        // Reset boss defeat flag
        renzDefeated = false;
        priestessAvailable = false;
        gleihDefeated = false;
        eumDefeated = false;

        // Initialize NPCs
        initNPCs();
        npcPanel = new NPCConversation(this, player, npcList, null);

        // Clear any existing panels
        battlePanel = null;
        shopPanel = null;
        gamePanel = null;

        showMainMenu();
    }

    
    private void initNPCs() {   
        
        npcList = NPCDialogues.createNPCs(renzDefeated, priestessAvailable, gleihDefeated, eumDefeated, false);
    }

    // Method called when Renz defeated
    public void onRenzDefeated() {
        renzDefeated = true; // Mark that Renz was defeated
        // Change the Castle to Ruins
        if (gamePanel != null) {
            gamePanel.changeCastleToRuins();
        }
        
        initNPCs();
        
        if (gamePanel != null) {
            gamePanel.placeOldKnight();
        }
    }

    
    // Return to map and spawn Spire if Renz was defeated
    public void returnToMap() {

        if (gamePanel == null) {
            gamePanel = new MapPanel(this);
        }
        setContentPane(gamePanel);
        revalidate();
        repaint();
        

        // Note: spire spawn is now gated by NPC conversation flow (Priestess),
        // so don't automatically spawn it here.
        
        SwingUtilities.invokeLater(() -> {
            gamePanel.setFocusable(true);
            gamePanel.requestFocusInWindow();
        });
        

        Timer focusTimer = new Timer(100, e -> {
            gamePanel.requestFocusInWindow();
        });
        focusTimer.setRepeats(false);
        focusTimer.start();
    }


    public void onNPCConversationEnded(String npcName, String lastNodeId) {
        if (npcName.equals("Old Knight Garron")) {
            // After Knight finishes guiding the player, spawn the Spire.
            if (gamePanel != null) {
                gamePanel.spawnSpire();
            }
            // Do NOT respawn the Priestess here; she is only from the earlier story step.
        } else if (npcName.equals("Priestess of Tine")) {
            // After Priestess talks, spawn the Village Elder
            if (gamePanel != null) gamePanel.spawnElder();
        } else if (npcName.equals("Village Elder")) {
            // After talking to both Priestess and Elder, spawn the Castle
            if (gamePanel != null) gamePanel.spawnCastle();
        }
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
        
        
        startBattle(eum);
        
        // Call intro
        if (battlePanel != null && battlePanel.getLog() != null) {
            eum.intro(battlePanel.getLog(), player);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
    
}