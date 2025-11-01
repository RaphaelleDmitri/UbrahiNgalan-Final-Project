import javax.swing.*;

public class Main extends JFrame {
    public Player player;
    Enemy enemy;

    MainMenuPanel mainMenu;
    private JPanel gamePanel; 
    BattlePanel battlePanel;
    ShopPanel shopPanel;

    public Main() {
        //name, hp, attack, defense, initial potions, initial coins
        player = new Player("Hero", 100, 15, 5, 3, 10);

        setTitle("A Java RPG");

        setSize(1820, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);


        showMainMenu();

        setVisible(true);
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

    public void startBattle() {
        enemy = new Enemy("Goblin", 40, 3, 3, 0, 0);
        //enemy = new Enemy("Renz, the Ramos", 150, 5, 5, 0);
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
