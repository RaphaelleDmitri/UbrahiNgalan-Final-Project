import javax.swing.JTextArea;

public abstract class Character {
    protected String name;
    protected int health;
    protected int attackPower;
    protected int defense;

    public Character(String name, int health, int attackPower, int defense){
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.defense = defense;
    }

    public abstract void attack(Character target, JTextArea log);

    public boolean isAlive(){ return health > 0; }
    public int getHealth(){ return health; }
    public String getName(){ return name; }
}
