import javax.swing.JTextArea;

public abstract class Character {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected int defense;
    protected int baseDefense; // Track base defense for reset
    protected int potionAmount;
    protected int coins;

    public Character(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.defense = defense;
        this.baseDefense = defense; // Store base defense
        this.potionAmount = potionAmount;
        this.coins = coins;
    }

    public abstract void attack(Character target, JTextArea log);

    public boolean isAlive() { 
        return health > 0; 
    }

    public int getHealth() { 
        return health; 
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getName() { 
        return name; 
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public int getPotionAmount() {
        return potionAmount;
    }

    public int getCoins() {
        return coins;
    }

    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth); // Cap at max health
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void addPotion() {
        this.potionAmount++;
    }

    // Reset defense to base value (call at start of each battle)
    public void resetDefense() {
        this.defense = this.baseDefense;
    }
}