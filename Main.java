import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    public Player player;
    Enemy enemy, enemy1;
    private List<Enemy> enemies;
    MainMenuPanel mainMenu;
    private JPanel gamePanel; 
    BattlePanel battlePanel;
    ShopPanel shopPanel;

    public Main() {
        //name, hp, attack, defense, initial potions, initial coins
        player = new Player("Hero", 100, 10, 5, 3, 10);

        setTitle("A Java RPG");

        setSize(1820, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 20, 20, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));

        showMainMenu();

        setVisible(true);

        
    }

    public Enemy getEnemy(int index) {
        if(index >= 0 && index < enemies.size()) return enemies.get(index);
        return null; // or throw an exception
    }

    public Enemy createEnemy(int index) {
        if(index < 0 || index >= enemies.size()) return null;
    
        Enemy template = enemies.get(index);
        return new Enemy(template.name, template.health, template.attackPower, template.defense, 0, 0);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

  
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

    

    public void addPotion(){
        player.potionAmount = player.potionAmount + 1;
    }
    public void addCoins(int reward){
        player.coins = player.coins + reward;
    }

    public void returnToMap() {
        setContentPane(gamePanel);
        revalidate();
    }


    public static void main(String[] args) {
        new Main();
    }
}
