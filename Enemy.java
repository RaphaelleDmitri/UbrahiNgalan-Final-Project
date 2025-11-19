import java.util.Random;
import javax.swing.JTextArea;

public class Enemy extends Character {
    Random rand = new Random();
    public int maxHealth;
    public Enemy(String name, int health, int attackPower, int defense, int potionAmount, int coins){
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    

    @Override
    public void attack(Character target, JTextArea log){
        int dmg = attackPower + rand.nextInt(5) - (target.defense / 4);
        if (dmg < 0){
            dmg = 0;
        }
        
        target.health -= dmg;
        log.append("\n\n" + name + "  dealing  " + dmg + " damage!");
    }

    public void heal(JTextArea log){
        
    }
}
