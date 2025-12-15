import javax.swing.JTextArea;
import java.util.Random;


public class MinionEnemy extends Enemy {
    private Random rand = new Random();
    public boolean isSummon = true;

    public MinionEnemy(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    @Override
    public void attack(Character target, JTextArea log) {
        int dmg = attackPower + (rand.nextInt(Math.max(1, attackPower / 3)));
        dmg = Math.max(0, dmg - (target.defense / 6));
        target.health -= dmg;
        log.append("\n\n" + name + " strikes you viciously for " + dmg + " damage!");
    }
}
    