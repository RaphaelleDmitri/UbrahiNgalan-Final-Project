import java.util.Random;
import javax.swing.JTextArea;

public class Player extends Character {
    Random rand = new Random();

    public Player(String name, int health, int attackPower, int defense){
        super(name, health, attackPower, defense);
    }

    @Override
    public void attack(Character target, JTextArea log){
        int dmg = attackPower + rand.nextInt(10);
        target.health -= dmg;
        log.append("\nYou attack " + target.getName() + " for " + dmg + " damage!");
    }

    public void defend(JTextArea log){
        int block = defense + rand.nextInt(15);
        this.defense += block;
        log.append("\nYou raise your shield, temporarily increasing your defenses.");
    }

    public void heal(JTextArea log){
        int healAmount = 5 + rand.nextInt(10);
        health += healAmount;
        log.append("\nYou heal " + healAmount + " HP!"); //nerf healing
    }
}
