import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    public Player player;
    private List<Enemy> enemies;
    MainMenuPanel mainMenu;
    private MapPanel gamePanel;  // Changed from JPanel to MapPanel
    BattlePanel battlePanel;
    ShopPanel shopPanel;
    NPCConversation npcPanel;

    private List<NPC> npcList;  // MUST be initialized
    private boolean renzDefeated = false; // Track if Renz was defeated

    public Main() {

        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        setTitle("A Java RPG");
        setSize(1820, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        //enemy list
        //linked list nisya nga saket lng ulo kag ara iya
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));


        // Boss 1
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        // Boss 2
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 0, 0));
        // Boss 3
        //Eum, The VoidMother, health 800, attack 70, defense 30
        enemies.add(new Enemy("Eum, The VoidMother", 700, 40, 20, 0, 0));
        
        // Initialize NPCs and dialogue nodes
        initNPCs();
        npcPanel = new NPCConversation(this, player, npcList);
        // -----------------------------------------------------
        showMainMenu();
        setVisible(true);
    }

    public Enemy getEnemy(int index) {
        if(index >= 0 && index < enemies.size()) return enemies.get(index);
        return null;
    }

    public Enemy createEnemy(int index) {
        if(index < 0 || index >= enemies.size()) return null;

        Enemy t = enemies.get(index);
        return new Enemy(t.name, t.health, t.attackPower, t.defense, 0, 0);
    }

    public List<Enemy> getEnemies() { return enemies; }

    public void showMainMenu() {
        mainMenu = new MainMenuPanel(this);
        setContentPane(mainMenu);
        revalidate();
    }

    public void showGamePanel() {
        gamePanel = new MapPanel(this);
        setContentPane(gamePanel);
        revalidate();
    }

    public void startBattle(Enemy enemy) {        
        battlePanel = new BattlePanel(this, player, enemy);
        setContentPane(battlePanel);
        revalidate();
    }

    public void startConversation() {
        npcPanel = new NPCConversation(this, player, npcList);
        setContentPane(npcPanel);
        revalidate();
    }

    public void addPotion() {
        player.potionAmount++;
    }

    public void addCoins(int reward) {
        player.coins += reward;
    }

    // Reset the game state and return to main menu
    public void resetGame() {
        // Recreate player (fresh inventory/stats)
        player = new Player("Hero", 100, 10, 5, 3, 100000, this);

        // Recreate enemy templates (same as constructor)
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", 60, 5, 10, 0, 0));
        enemies.add(new Enemy("Orc", 80, 8, 15, 0, 0));
        enemies.add(new Enemy("Slime", 20, 2, 6, 0, 0));
        enemies.add(new Enemy("Renz, the Corrupted King", 500, 30, 20, 0, 0));
        enemies.add(new Enemy("Gleih, the Dancing Witch", 250, 50, 10, 0, 0));

        // Reset boss defeat flag
        renzDefeated = false;

        // Initialize NPCs and dialogue nodes
        initNPCs();
        npcPanel = new NPCConversation(this, player, npcList);

        // Clear any existing panels so state is rebuilt when needed
        battlePanel = null;
        shopPanel = null;
        gamePanel = null;

        // Show main menu to the player
        showMainMenu();
    }

    // Initialize NPCs and their dialogue nodes (storyline-connected)
    private void initNPCs() {
        npcList = new ArrayList<>();

        // Village Elder: introduces the main quest and the three tyrants
        NPC elder = new NPC("Village Elder", "start");
        elder.addNode(new DialogueNode(
            "start",
            "You've arrived at last. The gods sent you to save our world from three tyrants.",
            Arrays.asList("Tell me about these tyrants.", "What should I do first?", "I'll find my own way."),
            Arrays.asList("tyrants", "first", null),
            Arrays.asList("Wise choice to learn of your foes.", 
                          "A good question. Listen carefully.",
                          "Foolish, but brave. Farewell.")
        ));
        elder.addNode(new DialogueNode(
            "tyrants",
            "Renz the Corrupted King rules the Castle. Gleih the Dancing Witch hides in the Spire. Eum the Voidmother brings the darkness.",
            Arrays.asList("Which one first?", "How can I defeat them?", "I understand."),
            Arrays.asList("first", "strategy", null),
            Arrays.asList("Renz. His defeat will reveal the Spire.",
                          "Gain strength by fighting lesser monsters. Collect gold for better gear.",
                          "May the gods guide you.")
        ));
        elder.addNode(new DialogueNode(
            "first",
            "Start with Renz in the Castle to the north. Gather strength from monsters along the way.",
            Arrays.asList("How much stronger?", "Any dangers?", "I'll go now."),
            Arrays.asList("strength", "dangers", null),
            Arrays.asList("Gather at least a few thousand gold and better armor.",
                          "Monsters grow fiercer as you venture further. Be cautious.",
                          "Good luck, hero.")
        ));
        elder.addNode(new DialogueNode(
            "strategy",
            "Buy weapons and armor from our shops. Fight monsters to earn gold. Practice your combat skills.",
            Arrays.asList("Where is the shop?", "What type of gear helps most?", "Got it."),
            Arrays.asList(null, "gear", null),
            Arrays.asList("The merchant is north of here.",
                          "A strong sword and sturdy armor. Do not neglect your defense.",
                          "Wisdom speaks through your questions.")
        ));
        elder.addNode(new DialogueNode(
            "strength",
            "You will know when you are ready. Trust your instincts in battle.",
            Arrays.asList("Anything else?", "I'm ready.", null),
            Arrays.asList("else", null, null),
            Arrays.asList("Defeat Renz and the Spire will rise. Then face Gleih inside.",
                          "Good. May your blade strike true.")
        ));
        elder.addNode(new DialogueNode(
            "dangers",
            "Goblins, Orcs, and Slimes inhabit these lands. They are manageable at first, but beware as you go deeper.",
            Arrays.asList("Any other warnings?", "I'll be careful.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("There are ruins of civilizations past. Stay on the roads.",
                          "Farewell then.")
        ));
        elder.addNode(new DialogueNode(
            "gear",
            "Swords and armor. Potions too — they restore your health in battle. Spend your gold wisely.",
            Arrays.asList("Where again?", "Thanks.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Follow the main path north.",
                          "Go, and return victorious.")
        ));
        elder.addNode(new DialogueNode(
            "else",
            "After Renz falls, seek out the Old Knight. He knows of the previous hero, Lura.",
            Arrays.asList("Who is Lura?", "I will.", null),
            Arrays.asList("lura", null, null),
            Arrays.asList("One who fought before you and fell to Eum. More later.",
                          "Go with purpose.")
        ));
        elder.addNode(new DialogueNode(
            "lura",
            "A hero from another time. He failed against Eum and was imprisoned. Perhaps you will succeed where he could not.",
            Arrays.asList("That's ominous.", "I will break his chains.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("The path is long and difficult. But you were chosen for a reason.",
                          "That is the spirit we need.")
        ));

        // Old Knight Garron: wise mentor about Lura and combat
        NPC knight = new NPC("Old Knight Garron", "start");
        knight.addNode(new DialogueNode(
            "start",
            "Ah, another hero. I once fought alongside Lura before the Voidmother took him.",
            Arrays.asList("Tell me of Lura.", "What happened to you?", "Move along."),
            Arrays.asList("lura", "happened", null),
            Arrays.asList("A valiant warrior. We were brothers in arms until Eum's curse.",
                          "I survived. Barely. Lura did not.",
                          "As you wish.")
        ));
        knight.addNode(new DialogueNode(
            "lura",
            "Lura was the bravest among us. He fought Eum when she first came. We thought he could defeat her.",
            Arrays.asList("But he failed?", "Is he truly dead?", "What can I learn?"),
            Arrays.asList("failed", "dead", "learn"),
            Arrays.asList("He fell to her darkness. His body was... changed.",
                          "His spirit may yet linger. Eum is cruel that way.",
                          "Never fight alone. Find allies. Find strength in numbers.")
        ));
        knight.addNode(new DialogueNode(
            "failed",
            "He was strong, but Eum feeds on power and turns it against her foes. He could not overcome her might.",
            Arrays.asList("How do I face her then?", "Was he trapped?", "I understand."),
            Arrays.asList("how", "trapped", null),
            Arrays.asList("That, I do not know. You must find your own way.",
                          "Yes. She bound him to herself. A terrible fate.",
                          "Learn from his sacrifice.")
        ));
        knight.addNode(new DialogueNode(
            "dead",
            "In body, yes. But his essence... sometimes I feel it lingering in the darkness. If anyone can free him, it may be you.",
            Arrays.asList("I will try.", "That sounds impossible.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Good. That is all I ask.",
                          "Many things seem so until they are done.")
        ));
        knight.addNode(new DialogueNode(
            "learn",
            "Fight the lesser foes first. Hone your blade. Gather strength. Then face Renz in his throne.",
            Arrays.asList("Any more wisdom?", "Thank you.", null),
            Arrays.asList("wisdom", null, null),
            Arrays.asList("When the Spire rises, do not hesitate. Gleih grows stronger the longer she waits.",
                          "Go with honor.")
        ));
        knight.addNode(new DialogueNode(
            "happened",
            "I was near Lura when Eum appeared. The darkness was overwhelming. I fell, but lived. He did not.",
            Arrays.asList("I'm sorry.", "How can I prevent your fate?", null),
            Arrays.asList(null, "prevent", null),
            Arrays.asList("Your sympathy is kind. Now use it as fuel for vengeance.",
                          "Become stronger. Faster. Smarter. Do not face Eum without preparation.")
        ));
        knight.addNode(new DialogueNode(
            "prevent",
            "Gather allies. Prepare every weapon and potion. Study your enemy. Lura rushed in with only courage.",
            Arrays.asList("Where can I find allies?", "I will prepare thoroughly.", null),
            Arrays.asList("allies", null, null),
            Arrays.asList("There are others who hate the tyrants. The priestess knows more.",
                          "Wise. Farewell.")
        ));
        knight.addNode(new DialogueNode(
            "how",
            "That is beyond my knowing. Perhaps the priestess of Tine can guide you. She communes with the goddess.",
            Arrays.asList("Where is she?", "I see.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("At the shrine, east of the main road.",
                          "Go then. Time is not our friend.")
        ));
        knight.addNode(new DialogueNode(
            "trapped",
            "Eum binds her victims to her will. It is a curse beyond my knowledge. The priestess may know the nature of it.",
            Arrays.asList("I must speak with her.", "How horrible.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Yes. Seek her counsel.",
                          "It is. That is why you must stop Eum.")
        ));
        knight.addNode(new DialogueNode(
            "wisdom",
            "Remember: Lura was not weak. Eum is just that powerful. Do not underestimate her. And above all, do not fight alone if you can avoid it.",
            Arrays.asList("I won't.", "I understand.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Then we may yet see hope in our lifetimes.",
                          "May the old gods watch over you.")
        ));

        // Priestess of Tine: speaks of the goddess, fate, and the final choice
        NPC priestess = new NPC("Priestess of Tine", "start");
        priestess.addNode(new DialogueNode(
            "start",
            "The goddess Tine sent word of your coming. You are the one chosen to save our world.",
            Arrays.asList("Why me?", "What must I do?", "I'm not sure I'm worthy."),
            Arrays.asList("why", "do", "worthy"),
            Arrays.asList("That is a mystery even to me. Tine sees what mortals cannot.",
                          "Defeat the three tyrants and restore balance to our world.",
                          "Tine does not choose the unworthy. She sees your potential.")
        ));
        priestess.addNode(new DialogueNode(
            "why",
            "The goddess works in mysterious ways. She brought you here from another world for a reason that will become clear.",
            Arrays.asList("Another world?", "When will it be clear?", "I accept my fate."),
            Arrays.asList("world", "when", null),
            Arrays.asList("Yes. You did not originate here. This land is not your home.",
                          "When you have beaten all three and the darkness lifts.",
                          "Wise acceptance. Tine smiles upon you.")
        ));
        priestess.addNode(new DialogueNode(
            "do",
            "First, defeat Renz the Corrupted King. This will awaken the Spire. Inside, you will face Gleih. After her defeat, Eum will manifest.",
            Arrays.asList("And after Eum?", "This sounds impossible.", "I will do it."),
            Arrays.asList("after", null, null),
            Arrays.asList("Then Tine herself will appear before you with an offer.",
                          "Tine would not have chosen you if it were beyond your reach.")
        ));
        priestess.addNode(new DialogueNode(
            "worthy",
            "Those who doubt themselves are the ones most worth saving. Your humility is a strength.",
            Arrays.asList("Thank you.", "Then I'll begin.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Go with my blessing.",
                          "May Tine's light guide your steps.")
        ));
        priestess.addNode(new DialogueNode(
            "world",
            "You come from a place beyond the stars, where gods do not walk. Tine has granted you passage here to prove yourself.",
            Arrays.asList("Can I return?", "Why pull me from my home?", "I see."),
            Arrays.asList("return", "home", null),
            Arrays.asList("That choice will be yours at the end.",
                          "Because this world was in greater need than yours.",
                          "Many have accepted their fate with less grace.")
        ));
        priestess.addNode(new DialogueNode(
            "when",
            "After the final battle, Tine will reveal her plan. Whether you go home or remain here is a choice only you can make.",
            Arrays.asList("I don't know what I'll choose.", "I want to go home.", "I want to stay."),
            Arrays.asList(null, null, null),
            Arrays.asList("That is why it will be your choice, not hers.",
                          "That is understandable. But wait until you see what you fight for.",
                          "Noble. But again, choose only after the battle is won.")
        ));
        priestess.addNode(new DialogueNode(
            "after",
            "Tine will speak to you herself. She will thank you and offer you a choice: return to your world or remain to help rebuild this one.",
            Arrays.asList("Both options sound difficult.", "I'm ready for anything.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Yes. Both carry their own weight. Choose with your heart.",
                          "That is the spirit of a true hero. Go now.")
        ));
        priestess.addNode(new DialogueNode(
            "return",
            "Perhaps. That depends on whether you survive and what you decide when the time comes. Tine always offers choices.",
            Arrays.asList("I miss my home.", "I'll decide when the time comes.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Then fight all the harder to see it again.",
                          "Wise. Farewell for now.")
        ));
        priestess.addNode(new DialogueNode(
            "home",
            "To save this world from eternal darkness. And perhaps, in saving it, you will find a new home here.",
            Arrays.asList("I understand.", "I hope so.", null),
            Arrays.asList(null, null, null),
            Arrays.asList("Then go. We are counting on you.",
                          "Tine hopes so too.")
        ));

        npcList.add(elder);
        npcList.add(knight);
        npcList.add(priestess);
    }

    // Method called when Corrupted King Renz is defeated
    public void onRenzDefeated() {
        System.out.println("DEBUG: onRenzDefeated() called!"); // DEBUG
        renzDefeated = true; // Mark that Renz was defeated
    }

    
    // Return to map and spawn Spire if Renz was defeated
    public void returnToMap() {
        System.out.println("DEBUG: returnToMap() called"); // DEBUG
        System.out.println("DEBUG: renzDefeated = " + renzDefeated); // DEBUG
        System.out.println("DEBUG: gamePanel = " + gamePanel); // DEBUG
        
        setContentPane(gamePanel);
        revalidate();
        repaint();
        
        

        // Spawn spire after returning to map if Renz was defeated
        if (renzDefeated && gamePanel != null) {
            System.out.println("DEBUG: Spawning spire now!"); // DEBUG
            SwingUtilities.invokeLater(() -> {
                gamePanel.spawnSpire();
                renzDefeated = false; // Reset flag
            });
        }
        
        // CRITICAL: Request focus multiple times to ensure it works
        SwingUtilities.invokeLater(() -> {
            gamePanel.setFocusable(true);
            gamePanel.requestFocusInWindow();
        });
        
        // Double-check focus after a short delay
        Timer focusTimer = new Timer(100, e -> {
            gamePanel.requestFocusInWindow();
        });
        focusTimer.setRepeats(false);
        focusTimer.start();
    }

    public void startBossBattle() {
        Enemy template = enemies.get(3);
        BossEnemy renz = new BossEnemy(template.name, template.health, template.attackPower, template.defense, 0, 0);
        startBattle(renz);
    }

    public void startBossBattle2() {
        Enemy template = enemies.get(4);
        BossEnemyWitch gleih = new BossEnemyWitch(template.name, template.health, template.attackPower, template.defense, 0, 0);
        startBattle(gleih);
    }
    public void startBossBattle3() {
        Enemy template = enemies.get(5);
        BossEnemyFinal eum = new BossEnemyFinal(template.name, template.health, template.attackPower, template.defense, 0, 0);
        
        // Start the battle first
        startBattle(eum);
        
        // Call intro using the log from the BattlePanel
        if (battlePanel != null && battlePanel.getLog() != null) {
            eum.intro(battlePanel.getLog(), player);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
    
}