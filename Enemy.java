import java.util.Random;
import javax.swing.JTextArea;

public class Enemy extends Character {
    Random rand = new Random();

    public Enemy(String name, int health, int attackPower, int defense, int potionAmount, int coins){
        super(name, health, attackPower, defense, potionAmount, coins);
    }

    @Override
    public void attack(Character target, JTextArea log){
        int dmg = attackPower + rand.nextInt(5);
        target.health -= dmg;
        log.append("\n" + name + " attacks you for " + dmg + " damage!");
    }
}
