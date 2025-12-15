import javax.swing.JTextArea;
import java.util.Random;

public class BossEnemy extends Enemy {
    private Random rand = new Random();

    // Pattern state
    private int patternIndex = 0;
    private boolean chargeActive = false;
    private boolean punishActive = false; 
    private boolean punishUntilAttack = true; 
    private boolean phase2 = false;

    // pattern array: 0 = Rapid Jab, 1 = Weak Jab, 2 = Charge (next heavy), 3 = False Windup (bait)
    private final int[] pattern = new int[] {0, 1, 2, 3};

    public BossEnemy(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    public void bossTurn(Character target, JTextArea log, int lastPlayerAction) {
        if (lastPlayerAction == 2 || lastPlayerAction == 3) {
            // player defended or healed last turn -> activate punish
            punishActive = true;
            log.append("\n" + name + " laughs at your cowardice to cower.");
        }

        // Phase check (phase 2 triggers at <=50% health)
        if (!phase2 && this.health <= (maxHealth / 2)) {
            phase2 = true;
            log.append("\n>> " + name + " becomes more furious!");
        }

        // If punish is active, perform a punishment attack (higher damage).
        if (punishActive) {
            performPunishment((Character) target, log);
            // if punishUntilAttack is true, do NOT clear punishActive here;
            // it will be cleared by BattlePanel when player performs action == 1 (attack).
            return;
        }

        int move = pattern[patternIndex];

        switch (move) {
            case 0 -> rapidJab(target, log);       // small multi-hit flavour
            case 1 -> weakJab(target, log);        // small single hit
            case 2 -> chargeUp(log);               // sets chargeActive -> next attack heavy
            case 3 -> falseWindup(target, log);    // bait small damage
            default -> rapidJab(target, log);
        }

        // If chargeActive was set earlier, handle heavy strike now
        if (chargeActive && move != 2) {
            // In case we charged earlier in pattern, perform the charged strike here
            heavyStrike((Character) target, log);
            chargeActive = false;
        }

        if (phase2) {
            quickHit(target, log);
            quickHit(target, log);

            // chance for extra heavy strike
            if (rand.nextInt(100) < 30) {
                heavyStrike((Character) target, log);
            }
        }

        patternIndex = (patternIndex + 1) % pattern.length;
    }

    // small helper attacks
    private void rapidJab(Character target, JTextArea log) {
        int dmg = attackPower + rand.nextInt(12); // smaller fast hits
        dmg = Math.max(0, dmg - (target.defense / 4));
        target.health -= dmg;
        log.append("\n\n" + name + " lashes out with rapid slashes for " + dmg + " damage!");
    }

    private void quickHit(Character target, JTextArea log) {
        int dmg = 3 + rand.nextInt(10); // 3-6
        dmg = Math.max(0, dmg - (target.defense / 4));
        target.health -= dmg;
        log.append("\n\n" + name + " hits quickly for " + dmg + " damage!");
    }

    private void weakJab(Character target, JTextArea log) {
        int dmg = 2 + rand.nextInt(10); // 2-6
        dmg = Math.max(0, dmg - (target.defense / 4));
        target.health -= dmg;
        log.append("\n\n" + name + " strikes you for " + dmg + " damage!");
    }

    private void chargeUp(JTextArea log) {
        chargeActive = true;
        log.append("\n\n" + name + " winds up a powerful strike (charging)!");
    }

    private void falseWindup(Character target, JTextArea log) {
        int dmg = 1 + rand.nextInt(3); // 1-3 minimal, a bait
        dmg = Math.max(0, dmg - (target.defense / 4));
        target.health -= dmg;
        log.append("\n\n" + name + " misses his strike, it barely grazes you for " + dmg + " damage!");
    }

    private void heavyStrike(Character target, JTextArea log) {
        int base = attackPower + 15 + rand.nextInt(12); // heavy
        int dmg = Math.max(0, base - (target.defense / 5));
        target.health -= dmg;
        log.append("\n\n" + name + " delivers a HEAVY strike for " + dmg + " damage!");
    }

    private void performPunishment(Character target, JTextArea log) {
        int base = (attackPower/2) + rand.nextInt(3);
        int dmg = Math.max(0, base - Math.max(0, target.defense / 8)); 
        target.health -= dmg;
        log.append("\n\n" + name + " Takes advantage of your cowardice, dealing " + dmg + " damage!");
    }

    public void clearPunish() {
        punishActive = false;
    }

    public boolean isPunishActive() {
        return punishActive;
    }

    public boolean isPhase2() {
        return phase2;
    }

}
