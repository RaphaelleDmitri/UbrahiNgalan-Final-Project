import javax.swing.*;

public class Main extends JFrame {
    Player player;
    Enemy enemy;

    MainMenuPanel mainMenu;
    private JPanel gamePanel; 
    BattlePanel battlePanel;

    public Main() {
        player = new Player("Hero", 100, 15, 5);

        setTitle("Java RPG");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


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
        enemy = new Enemy("Goblin", 40, 3, 3);
        battlePanel = new BattlePanel(this, player, enemy);
        setContentPane(battlePanel);
        revalidate();
    }

    public void returnToMap() {
        setContentPane(gamePanel);
        revalidate();
    }


    public static void main(String[] args) {
        new Main();
    }
}
