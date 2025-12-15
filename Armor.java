public class Armor {
    String name;
    int defense;
    int price;
    boolean tradable; 

    public Armor(String name, int defense, int price, boolean tradable) {
        this.name = name;
        this.defense = defense;
        this.price = price;
        this.tradable = tradable;
    }

    @Override
public String toString() {
    return name + " (+" + defense + " DEF)";
}

}