import java.util.Random;
import javax.swing.*;

public class BossEnemyFinal extends Enemy {
    private Random rand = new Random();

    private int patternIndex = 0;
    private boolean rageActive = false;
    private boolean phase2 = false;
    private boolean luraSummoned = false;

    private final int[] pattern = new int[]{0, 1, 2, 3};

    private Ally lura; // The previous hero

    public BossEnemyFinal(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    // Show the introductory story text
    public void intro(JTextArea log, Player player) {
        log.append("\n>> Shadows spread across the battlefield… Umbra, Lord of Shadows, emerges!");
        log.append("\n>> Exhausted from the previous fight, Y/N feels weaker and more vulnerable!");
        // Apply exhaustion debuffs
        player.attackPower = (int) (player.attackPower * 0.7); // 30% weaker
        player.defense = (int) (player.defense * 0.8); // 20% weaker
    }

    // Called each boss turn
    public void bossTurn(Player player, JTextArea log, int lastPlayerAction) {
        // Phase 2 trigger
        if (!phase2 && this.health <= maxHealth / 2) {
            phase2 = true;
            rageActive = true;
            log.append("\n>> Umbra grows stronger, the shadows rage with him!");
        }

        // Summon Lura only once when Y/N is about to lose
        if (!luraSummoned && player.health <= player.maxHealth * 0.2) {
            lura = new Ally("Lura, the Previous Hero", 150, 25, 10); // Example ally stats
            luraSummoned = true;
            log.append("\n>> Just as defeat seems certain, a faint light appears… Lura joins the fight!");
        }

        // Execute pattern attack
        int move = pattern[patternIndex];
        switch (move) {
            case 0 -> shadowStrike(player, log);
            case 1 -> flameWave(player, log);
            case 2 -> powerCharge(log);
            case 3 -> misfire(player, log);
        }

        // Rage follow-up attack
        if (rageActive && move != 2) {
            devastatingBlow(player, log);
            rageActive = false;
        }

        // Phase 2 conditional extra attack
        if (phase2) {
            if (rand.nextInt(100) < 50) shadowStrike(player, log);
            if (rand.nextInt(100) < 30) devastatingBlow(player, log);
        }

        patternIndex = (patternIndex + 1) % pattern.length;

        // Lura auto-attacks if summoned
        if (luraSummoned && lura.health > 0) {
            int dmg = lura.attackPower + rand.nextInt(5);
            player.health -= 0; // Lura doesn't attack player; he attacks Umbra
            log.append("\n>> Lura strikes Umbra for " + dmg + " damage!");
            this.health -= dmg;

            if (this.health <= 0) {
                log.append("\n>> Umbra has been defeated!");
                // Lura dies after fulfilling mission
                lura.health = 0;
                log.append("\n>> Lura succumbs to his wounds after ending Umbra's terror.");
            }
        }
    }

    // --- Attack Methods ---
    private void shadowStrike(Character target, JTextArea log) {
        int dmg = attackPower + rand.nextInt(10);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " strikes with shadowy claws for " + dmg + " damage!");
    }

    private void flameWave(Character target, JTextArea log) {
        int dmg = 2 + rand.nextInt(6);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " sends a wave of dark flames dealing " + dmg + " damage!");
    }

    private void powerCharge(JTextArea log) {
        rageActive = true;
        log.append("\n\n" + name + " channels immense energy (preparing a devastating blow)!");
    }

    private void misfire(Character target, JTextArea log) {
        int dmg = 1 + rand.nextInt(4);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " fumbles a spell — barely scratching you for " + dmg + " damage!");
    }

    private void devastatingBlow(Character target, JTextArea log) {
        int dmg = attackPower + 20 + rand.nextInt(15) - target.defense / 5;
        dmg = Math.max(0, dmg);
        target.health -= dmg;
        log.append("\n\n" + name + " unleashes a DEVASTATING BLOW for " + dmg + " damage!");
    }

    public boolean isPhase2() { return phase2; }
}

// Simple ally class for Lura


