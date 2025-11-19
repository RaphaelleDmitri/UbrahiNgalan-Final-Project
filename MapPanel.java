import java.awt.*;
import java.util.Random;
import javax.swing.*;
public class MapPanel extends JPanel {
    private Main game;
    private JButton[][] tiles;
    private int playerX = 2; // starting position || CAN BE CHANGED
    private int playerY = 2;
    
    private final int ROWS = 12;
    private final int COLS = 12;

    private JLabel info; 
    Random rand = new Random();
    public MapPanel(Main game){
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Add at the top of MapPanel constructor, after setBackground
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BorderLayout());
    leftPanel.setBackground(Color.DARK_GRAY);

        // Stats button
    JButton statsBtn = new JButton("Stats");
    statsBtn.setFont(new Font("Consolas", Font.BOLD, 18));
    statsBtn.setBackground(new Color(60, 60, 60));
    statsBtn.setForeground(new Color(255, 255, 155));
    statsBtn.setFocusPainted(false);

        
    JPanel statsBtnPanel = new JPanel();
    statsBtnPanel.setBackground(Color.DARK_GRAY);
    statsBtnPanel.add(statsBtn);

    leftPanel.add(statsBtnPanel, BorderLayout.NORTH);
    add(leftPanel, BorderLayout.WEST);

    
    statsBtn.addActionListener(e -> {
    
    StatPanel statPanel = new StatPanel(game.player);
        add(statPanel, BorderLayout.EAST);
        revalidate();
        repaint();
    });

        
        info = new JLabel("You are at the starting location.", SwingConstants.CENTER);
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Consolas", Font.BOLD, 16));
        info.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        add(info, BorderLayout.NORTH);

        // map
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 0, 0));
        gridPanel.setBackground(Color.BLACK);
           
        tiles = new JButton[ROWS][COLS];

        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
                JButton tile = new JButton();
                tile.setEnabled(false);
                tile.setFont(new Font("Consolas", Font.BOLD, 24));
                tile.setForeground(Color.BLACK);
                tiles[i][j] = tile;
                gridPanel.add(tile);
            }
        }


        
        

        // trial buildings


        tiles[0][1].setText("Shop");
        //tiles[0][1].setForeground(Color.WHITE);

        
        tiles[0][3].setText("Inn");
        //tiles[0][3].setForeground(Color.WHITE);

        int randomXSpawn = rand.nextInt(6) + 5;
        int randomYSpawn = rand.nextInt(6) + 5;
        tiles[0][0].setText("Castle");
        tiles[0][4].setText("Spire");
        tiles[randomXSpawn][randomYSpawn].setText("CastlePlaceHolder"); //castle real location
        tiles[randomXSpawn][randomYSpawn].setForeground(Color.WHITE);

        int secretAreaLocationX = randomXSpawn - rand.nextInt(4);
        int secretAreaLocationY = randomYSpawn - rand.nextInt(4);
        
        
        
            int npcAmount = 4;
        for(int k = 0; k < npcAmount; k++){
        int x,y;

        while(true) {
            // spawn around bottom-mid area so it feels distributed adventure exploring
            x = rand.nextInt(ROWS - 6) + 6; // rows 6-11
            y = rand.nextInt(COLS);        // cols 0-11
            String text = tiles[x][y].getText();
            if(text.isEmpty() || text.equals(".")) break;
        }
        tiles[x][y].setText("NPC");
        tiles[x][y].setForeground(Color.WHITE);
        }

        
        


            //tile Colorization
        //    tiles[0][0].setText(".");
        tiles[0][2].setText(".");
        //tiles[0][4].setText(".");

        for(int i =0; i<5;i++){ // ROWS
            for (int j = 0; j<5; j++){ //COLUMNS
                if(j != 1 && i != 0|| j !=3 && i != 0){
                    tiles[i][j].setText(".");
                }
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        //buttons
        JPanel controls = new JPanel();
        controls.setBackground(Color.DARK_GRAY);

        JButton btnNorth = new JButton("North");
        JButton btnSouth = new JButton("South");
        JButton btnEast  = new JButton("East");
        JButton btnWest  = new JButton("West");

        styleButton(btnNorth); styleButton(btnSouth);
        styleButton(btnEast); styleButton(btnWest);

        controls.add(btnNorth);
        controls.add(btnSouth);
        controls.add(btnEast);
        controls.add(btnWest);

        add(controls, BorderLayout.SOUTH);
        // Enable arrow key movement
        setupKeyBindings();
        setFocusable(true);
        requestFocusInWindow();

        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        // Button actions
        btnNorth.addActionListener(e -> movePlayer("Up"));
        btnSouth.addActionListener(e -> movePlayer("Down"));
        btnEast.addActionListener(e -> movePlayer("Right"));
        btnWest.addActionListener(e -> movePlayer("Left"));

        //add option to  move player with wasd controls
        updatePlayerPosition();
    }

        
     //work on this later (randomLocationCreator)
         /* 
        if(playerX == secretAreaLocationX && playerY == secretAreaLocationY){
            int randomArea = rand.nextInt(3);
            String secretName = "???";
            switch(randomArea){
                case 1: secretName = "Church";
                break;
                case 2: secretName = "Cemetery";
                break;
                case 3: secretName = "Medic";
            }
            
            tiles[secretAreaLocationX][secretAreaLocationY].setText(secretName);
        }else {tiles[secretAreaLocationX][secretAreaLocationY].setText("???");}
    */
        
    
        
    private void styleButton(JButton btn){
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(new Font("Consolas", Font.BOLD, 16));
        btn.setFocusPainted(false);
    }

    private void movePlayer(String direction){
        int newX = playerX;
        int newY = playerY;
    
        switch(direction){
            case "Up" -> newX--;
            case "Down" -> newX++;
            case "Right"  -> newY++;
            case "Left"  -> newY--;
        }
    
        // Prevent moving outside the map
        if(newX < 0 || newX >= ROWS || newY < 0 || newY >= COLS) return;
    
        playerX = newX;
        playerY = newY;
        updatePlayerPosition();
    
        String tileName = tiles[playerX][playerY].getText();
        boolean inShop = tileName.equals("Shop");
    
        if(inShop){
            System.out.println("Entering ShopPanel...");
            SwingUtilities.invokeLater(() -> {
                game.setContentPane(new ShopPanel(game, game.player));
                game.revalidate();
                game.repaint();
            });
            return; // exit after opening shop
        }
    
        // Trigger fights or special events
        if(tileName.isEmpty()){ // empty tile → random encounter or item
            int r = rand.nextInt(20); // 0-19
            //int n = rand.nextInt(10); //disable encounters
            if(r ==  1|| r == 2 || r == 3 || r == 4) {
                game.startBattle(game.createEnemy(0));
            }
            else if(r == 5) {
                game.startBattle(game.createEnemy(2));
            }
            else if(r >= 6 && r <= 7) {
                game.startBattle(game.createEnemy(3));
            } else if(r == 9 || r == 8) {
                game.addPotion();
                info.setText("You found a potion left behind by another unfortunate hero.");
            } else {
                info.setText("You walk on the grass. Nothing happened." + " R VALUE: " + r);
            }
        } else { 
            // Non-empty tile → predefined enemy or event
            if(tileName.equals("Castle")){
                game.startBossBattle();  } 
                else if(tileName.equals("NPC")){
                    info.setText("You approach an NPC... (dialogue system coming soon)");
                    game.startConversation();
                } else if(tileName.equals("Spire")){
                    info.setText("The Dancing Witch has spotted your presence.");
                    game.startBossBattle2();
                    }
                    else if(tileName.equals("Inn")){
                        System.out.println("Entering InnPanel...");
                        SwingUtilities.invokeLater(() -> {
                            game.setContentPane(new InnPanel(game, game.player));
                            game.revalidate();
                            game.repaint();
                        });
                    }
            else {
                info.setText("You are at: " + tileName);
            }
        }
    }
    
    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
    
        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
    
        am.put("moveUp", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer("Up");
            }
        });
    
        am.put("moveDown", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer("Down");
            }
        });
    
        am.put("moveLeft", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer("Left");
            }
        });
    
        am.put("moveRight", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer("Right");
            }
        });
    }
    

    private void updatePlayerPosition(){
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
                tiles[i][j].setBackground(Color.BLACK); // grass
                if(!tiles[i][j].getText().isEmpty()){
                    if(!tiles[i][j].getText().equals(".")){
                    tiles[i][j].setBackground(new Color(60,120,60)); // buildings
                    }else{
                        tiles[i][j].setBackground(new Color(128,128,128));
                    }
                }
            }
        }
        // where player
        tiles[playerX][playerY].setBackground(Color.WHITE);

        
    }
}
