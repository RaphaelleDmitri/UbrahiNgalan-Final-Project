import java.awt.*;
import javax.swing.*;
import java.util.Random;
public class BattlePanel extends JPanel {
    private Main game;
    private Player player;
    private Enemy enemy;
    private JTextArea log;
    private JLabel stats;
    Random rand = new Random();

    public BattlePanel(Main game, Player player, Enemy enemy){
        this.game = game;
        this.player = player;
        this.enemy = enemy;

        setLayout(new BorderLayout());
        setBackground(new Color(25,25,25));

        stats = new JLabel(updateStats(), SwingConstants.CENTER);
        stats.setForeground(new Color(230,205,70));
        stats.setFont(new Font("Consolas", Font.BOLD, 20));
        stats.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(stats, BorderLayout.NORTH);

        log = new JTextArea();
        log.setEditable(false);
        log.setBackground(new Color(40,40,40));
        log.setForeground(Color.WHITE);
        log.setFont(new Font("Consolas", Font.PLAIN, 25));
        log.setBorder(BorderFactory.createLineBorder(new Color(200,200,100), 2));
        add(new JScrollPane(log), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(25,25,25));

        JButton attackBtn = styledBtn("Attack");
        JButton defendBtn = styledBtn("Defend");
        JButton healBtn   = styledBtn("Heal");

        buttons.add(attackBtn);
        buttons.add(defendBtn);
        buttons.add(healBtn);
        add(buttons, BorderLayout.SOUTH);

        attackBtn.addActionListener(e -> doTurn(1));
        defendBtn.addActionListener(e -> doTurn(2));
        healBtn.addActionListener(e -> doTurn(3));

        log.setText("A wild " + enemy.getName() + " appears!\n");
    }

    private JButton styledBtn(String txt){
        JButton btn = new JButton(txt);
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(new Font("Consolas", Font.BOLD, 32));
        btn.setFocusPainted(false);
        return btn;
    }

    private void doTurn(int action){
        if(!player.isAlive() || !enemy.isAlive()) return;

        switch(action){
            case 1 -> player.attack(enemy, log);
            case 2 -> player.defend(log);
            case 3 -> player.heal(log);
        }
        
        
        if(enemy.isAlive()) enemy.attack(player, log);
        

        stats.setText(updateStats());

        int reward = rand.nextInt(10 ) + 2000;

        if(!player.isAlive()) log.append("\n\n>> GAME OVER");
        else if(!enemy.isAlive()) {
            log.append("\n\n>> VICTORY!");
            log.append("\n You have obtained " + reward + " coins! ");
            game.addCoins(reward);
            // timer para balik sa map
            Timer t = new Timer(1500, e -> game.returnToMap());
            t.setRepeats(false);
            t.start();
        }
    }

    private String updateStats(){
        return "Hero HP: " + player.getHealth() +  " Potions Left:"+ player.potionAmount+" | " + enemy.getName() + " HP: " + enemy.getHealth();
    }
}
