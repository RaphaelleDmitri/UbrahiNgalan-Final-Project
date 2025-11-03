import java.awt.*;
import java.util.Random;
import javax.swing.*;
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
        JButton fleeBtn =styledBtn("Flee");

        buttons.add(attackBtn);
        buttons.add(defendBtn);
        buttons.add(healBtn);
        buttons.add(fleeBtn);
        add(buttons, BorderLayout.SOUTH);

        attackBtn.addActionListener(e -> doTurn(1));
        defendBtn.addActionListener(e -> doTurn(2));
        healBtn.addActionListener(e -> doTurn(3));
        fleeBtn.addActionListener(e-> doTurn(4));

        
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
    log.setText(String.format(appearanceTexts[index], enemy.name));
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
            case 4 -> player.flee(log);
        }
    
        if(enemy.isAlive()) {
            enemy.attack(player, log);
            stats.setText(updateStats());
        } else {
            stats.setText(updateStats());
            int reward = rand.nextInt(10) + 2000;
            // Delay showing VICTORY so attack text can show first
            Timer t = new Timer(200, e -> {
                log.append("\n\n>> VICTORY!");
                log.append("\n You have obtained " + reward + " coins! ");
                game.addCoins(reward);
                // delay to return to map
                Timer t2 = new Timer(1500, ev -> game.returnToMap());
                t2.setRepeats(false);
                t2.start();
            });
            t.setRepeats(false);
            t.start();
        }
    
        if(!player.isAlive()) log.append("\n\n>> GAME OVER");
    }

    private String updateStats(){
        return "Hero HP: " + player.getHealth() +  " Potions Left:"+ player.potionAmount+" | " + enemy.getName() + " HP: " + enemy.getHealth();
    }
}
