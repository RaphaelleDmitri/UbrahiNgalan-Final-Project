import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    public Player player;
    private List<Enemy> enemies;
    MainMenuPanel mainMenu;
    private JPanel gamePanel; 
    BattlePanel battlePanel;
    ShopPanel shopPanel;
    NPCConversation npcPanel;

    private List<NPC> npcList;  // MUST be initialized

    public Main() {

        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        setTitle("A Java RPG");
        setSize(1820, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        //enemy list
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));
        
        // Boss 1
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        // Boss 2
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 5, 0));
        


        List<NPC> npcList = new ArrayList<>();

        NPC elder = new NPC("Village Elder", "start");

        // Add dialogue nodes
        elder.addNode(new DialogueNode(
        "start",
    "Greetings, traveler.",
        Arrays.asList("Who are you?", "Any work?", "Goodbye."),
        Arrays.asList("intro", "quest", null),
        Arrays.asList("I am the elder.", "Yes, I have a task for you.", "Farewell.")
        ));

    elder.addNode(new DialogueNode(
        "intro",
        "I am the elder of this village.",
        Arrays.asList("Tell me more.", "Thanks.", "Goodbye."),
        Arrays.asList("quest", "start", null),
        Arrays.asList("There are monsters in the woods.", "Safe travels.", "Farewell.")
    ));

    elder.addNode(new DialogueNode(
        "quest",
        "There are monsters in the woods.",
        Arrays.asList("I'll handle it.", "That's dangerous.", "Goodbye."),
        Arrays.asList(null, "start", null),
        Arrays.asList("Thank you, brave one.", "Be careful!", "Farewell." )
        //figure a way to exit the conversation gracefully
    ));

        npcList.add(elder);
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
        setContentPane(npcPanel);
        revalidate();
    }

    public void addPotion() {
        player.potionAmount++;
    }

    public void addCoins(int reward) {
        player.coins += reward;
    }

    public void returnToMap() {
        setContentPane(gamePanel);
        // Ensure the map panel regains focus so keybindings work again
        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            revalidate();
            repaint();
        });
    }

    public void startBossBattle() {
        Enemy template = enemies.get(1);
        BossEnemy renz = new BossEnemy(template.name, template.health, template.attackPower, template.defense, 0, 0);
        startBattle(renz);
    }

    public void startBossBattle2() {
        Enemy template = enemies.get(4);
        BossEnemyWitch gleih = new BossEnemyWitch(template.name, template.health, template.attackPower, template.defense, 0, 0);
        startBattle(gleih);
    }

    public static void main(String[] args) {
        new Main();
    }
}
