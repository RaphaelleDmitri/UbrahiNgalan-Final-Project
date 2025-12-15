import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MapPanel extends JPanel {
    
    private Image backgroundImage;
    private JPanel sidePanel = null;
    private Main game;
    private JButton[][] tiles;
    private JPanel gridPanel;
    private int playerX = 2; // starting position || CAN BE CHANGED
    private int playerY = 2;
    private final int ROWS = 12;
    private final int COLS = 12;
    private JLabel info; 
    Random rand = new Random();
    private boolean spireSpawned = false; // Boolean to track if Spire has spawned
    private boolean renzDefeated = false; // Boolean to track if Renz has been defeated

    public MapPanel(Main game){
        this.game = game;
    
        try {
            backgroundImage = ImageIO.read(new File("map.png"));
        } catch (IOException e) {
            System.out.println("Map background image not found, using color instead");
        }
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BorderLayout());
    leftPanel.setOpaque(false);

        // Stats button
    JButton statsBtn = new JButton("Stats");
    statsBtn.setFont(GameFonts.press(18f));
    statsBtn.setBackground(new Color(60, 60, 60));
    statsBtn.setForeground(new Color(255, 255, 155));
    statsBtn.setFocusPainted(false);

        
    JPanel statsBtnPanel = new JPanel();
    statsBtnPanel.setOpaque(false);
    statsBtnPanel.add(statsBtn);

    leftPanel.add(statsBtnPanel, BorderLayout.NORTH);
    add(leftPanel, BorderLayout.WEST);

        //Inventory Button
    JButton inventoryBtn = new JButton("Inventory");
    inventoryBtn.setFont(GameFonts.press(18f));
    inventoryBtn.setBackground(new Color(60, 60, 60));
    inventoryBtn.setForeground(new Color(255, 255, 155));
    inventoryBtn.setFocusPainted(false);

    JPanel inventoryBtnPanel = new JPanel();
    inventoryBtnPanel.setOpaque(false);
    inventoryBtnPanel.add(inventoryBtn);
    leftPanel.add(inventoryBtnPanel, BorderLayout.SOUTH);

    inventoryBtn.addActionListener(e -> {
        toggleSidePanel(new InventoryPanel(game.player));
    });
    
    statsBtn.addActionListener(e -> {
        toggleSidePanel(new StatPanel(game.player));
    });
    

        // info label
        info = new JLabel("You are at the starting location.", SwingConstants.CENTER);
        info.setForeground(Color.WHITE);
        info.setFont(GameFonts.press(18f));
        info.setBorder(BorderFactory.createEmptyBorder(7,0,7,0));
        add(info, BorderLayout.NORTH);

        // map
        gridPanel = new JPanel(new GridLayout(ROWS, COLS, 1, 1));
        gridPanel.setOpaque(false);
           
        tiles = new JButton[ROWS][COLS];

        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
            JButton tile = new JButton();
            tile.setEnabled(false);
            tile.setFont(GameFonts.press(16f));
            tile.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100, 80), 1)); // Semi-transparent border
            tile.setForeground(Color.WHITE);
            tile.setOpaque(false);
            tile.setContentAreaFilled(false);
            tiles[i][j] = tile;
            gridPanel.add(tile);
            }
        }

        // trial buildings
<<<<<<< HEAD
        tiles[0][1].setText("Shop");
        //tiles[0][1].setForeground(Color.WHITE);
        tiles[0][6].setText("TestArea");
=======
        tiles[0][1].setText("SHOP");
        tiles[0][1].setForeground(new Color(255, 223, 0)); // Bright gold
        tiles[0][1].setFont(GameFonts.pressBold(18f));

>>>>>>> 9bc5240cc9ffc4abfb9e46c510c6bdaf8f22fe66
        
        tiles[0][3].setText("INN");
        tiles[0][3].setForeground(new Color(255, 223, 0)); // Bright gold
        tiles[0][3].setFont(GameFonts.pressBold(18f));

        int randomXSpawn = rand.nextInt(6) + 5;
        int randomYSpawn = rand.nextInt(6) + 5;
        tiles[0][0].setText("CASTLE");
        tiles[0][0].setForeground(new Color(186, 85, 211)); // Purple for mystery
        tiles[0][0].setFont(GameFonts.pressBold(16f));
        // tiles[0][4].setText("Spire"); // REMOVED - Spire now spawns after defeating Renz
        tiles[randomXSpawn][randomYSpawn].setText("CASTLE"); //castle real location
        tiles[randomXSpawn][randomYSpawn].setForeground(new Color(186, 85, 211)); // Purple
        tiles[randomXSpawn][randomYSpawn].setFont(GameFonts.pressBold(16f));

        int secretAreaLocationX = randomXSpawn - rand.nextInt(4);
        int secretAreaLocationY = randomYSpawn - rand.nextInt(4);
        

        //npc ammount
            int npcAmount = 2;
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
        tiles[x][y].setForeground(new Color(144, 238, 144)); // Light green
        tiles[x][y].setFont(GameFonts.pressBold(18f));
        }

            //prioritize later
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

    // movement instruction and return button
    JPanel controls = new JPanel(new BorderLayout());
    controls.setBackground(Color.DARK_GRAY);
    
    // Movement instruction label
    JLabel moveInstr = new JLabel("Use the ARROW KEYS to move", SwingConstants.CENTER);
    moveInstr.setForeground(new Color(240,220,140));
    moveInstr.setBorder(new EmptyBorder(5, 10, 5, 10)); 
    moveInstr.setFont(GameFonts.jettsBold(16f));
    controls.add(moveInstr, BorderLayout.CENTER);
    
    // Return to Main Menu button
    JButton returnHomeBtn = new JButton("Main Menu");
    returnHomeBtn.setFont(GameFonts.press(16f));
    returnHomeBtn.setBackground(new Color(60, 60, 60));
    returnHomeBtn.setForeground(new Color(255, 255, 155));
    returnHomeBtn.setFocusPainted(false);
    returnHomeBtn.setBorder(new EmptyBorder(5, 15, 5, 15));
    returnHomeBtn.addActionListener(e -> {
        game.setContentPane(new MainMenuPanel(game));
        game.revalidate();
    });
    
    controls.add(returnHomeBtn, BorderLayout.EAST);

    add(controls, BorderLayout.SOUTH);

    // Enable arrow key movement
    setupKeyBindings();
    setFocusable(true);
    requestFocusInWindow();

    SwingUtilities.invokeLater(() -> requestFocusInWindow());

        //add option to  move player with wasd controls
        updatePlayerPosition();
    }

        
     //work on this later (randomLocationNamer)
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
        btn.setFont(GameFonts.jettsBold(16f));
        btn.setFocusPainted(false);
    }

    // Method to spawn the Spire after Corrupted King Renz is defeated
    public void spawnSpire() {
        System.out.println("DEBUG: spawnSpire() called, spireSpawned = " + spireSpawned); // DEBUG
        
        if (!spireSpawned) {
            int spireX, spireY;
            
            // Find a random empty location for the Spire
            while (true) {
                spireX = rand.nextInt(ROWS);
                spireY = rand.nextInt(COLS);
                String text = tiles[spireX][spireY].getText();
                
                // Make sure it's an empty tile and not the player's current position
                if ((text.isEmpty() || text.equals(".")) && !(spireX == playerX && spireY == playerY)) {
                    break;
                }
            }
            
            System.out.println("DEBUG: Spawning Spire at [" + spireX + "][" + spireY + "]"); // DEBUG
            tiles[spireX][spireY].setText("SPIRE");
            tiles[spireX][spireY].setForeground(new Color(255, 0, 255)); // Bright magenta
            tiles[spireX][spireY].setFont(GameFonts.pressBold(18f));
            spireSpawned = true;
            renzDefeated = true; // Mark that Renz has been defeated
            
            // Remove the Castle tile so Renz can't be fought again
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (tiles[i][j].getText().equals("CASTLE")) {
                        tiles[i][j].setText("RUINS"); // Change to "Ruins" or empty
                        tiles[i][j].setForeground(new Color(128, 128, 128)); // Gray
                        tiles[i][j].setFont(GameFonts.pressBold(16f));
                    }
                }
            }
            
            info.setText("A mysterious Spire has appeared on the map!");
            updatePlayerPosition(); // Refresh the map visually
        }
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
        boolean inShop = tileName.equals("SHOP");
    
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
                game.startBattle(game.createEnemy(1));
            } else if(r == 9 || r == 8) {
                game.addPotion();
                info.setText("You found a potion left behind by another unfortunate hero.");
            } else {
                info.setText("You walk on the grass. Nothing happened." + " R VALUE: " + r);
            }
        } else { 
            // Non-empty tile → predefined enemy or event
            if(tileName.equals("CASTLE")){
                if (!renzDefeated) {
                    game.startBossBattle();
                } else {
                    info.setText("The castle lies in ruins. The Corrupted King is no more.");
                }
            } else if(tileName.equals("Ruins")){
                info.setText("The castle lies in ruins. The Corrupted King is no more.");
            } else if(tileName.equals("NPC")){
                    info.setText("You approach an NPC... (dialogue system coming soon)");
                    game.startConversation();
                } else if(tileName.equals("SPIRE")){
                    info.setText("The Dancing Witch has spotted your presence.");
                    game.startBossBattle2();
                    }
                    else if(tileName.equals("INN")){
                        System.out.println("Entering InnPanel...");
                        SwingUtilities.invokeLater(() -> {
                            game.setContentPane(new InnPanel(game, game.player));
                            game.revalidate();
                            game.repaint();
                        });
                    }else if(tileName.equals("TestArea")){
                        info.setText("You have entered the Castle!");
                        game.startBossBattle3();
                    }
            else {
                info.setText("You are at: " + tileName);
                System.out.println("Currently on: " + tileName);
            }
        }
        
        }
        private long lastMoveTime = 0; // Track the last move time
        private final int MOVE_DELAY = 100; // Delay in milliseconds

        
        
        private void setupKeyBindings() {
            InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();
        
            im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
            im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
            im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
            im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        
            am.put("moveUp", new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleMove("Up");
                }
            });
        
            am.put("moveDown", new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleMove("Down");
                }
            });
        
            am.put("moveLeft", new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleMove("Left");
                }
            });
        
            am.put("moveRight", new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleMove("Right");
                }
            });
        }

        private void handleMove(String direction) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMoveTime >= MOVE_DELAY) {
                movePlayer(direction);
                lastMoveTime = currentTime;
            }
        }

        private void updatePlayerPosition(){
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
                // Make all tiles transparent by default
                tiles[i][j].setOpaque(false);
                tiles[i][j].setContentAreaFilled(false);
                
                if(!tiles[i][j].getText().isEmpty()){
                    if(!tiles[i][j].getText().equals(".")){
                        // Buildings are visible
                        tiles[i][j].setBackground(new Color(60,120,60));
                        tiles[i][j].setOpaque(true);
                        tiles[i][j].setContentAreaFilled(true);
                    }
                }
            }
        }
        // Player position is visible
        tiles[playerX][playerY].setBackground(Color.WHITE);
        tiles[playerX][playerY].setOpaque(true);
        tiles[playerX][playerY].setContentAreaFilled(true);
         }

         private void toggleSidePanel(JPanel newPanel) {
            boolean closingSamePanel = sidePanel != null && sidePanel.getClass().equals(newPanel.getClass());

            if (sidePanel != null) {
                remove(sidePanel);
                sidePanel = null;
            }

            if (closingSamePanel) {
                // Restore map if we were closing the current panel
                ensureGridAttached();
                gridPanel.setVisible(true);
                revalidate();
                repaint();
                return;
            }

            // Inventory takes the whole gameplay area (except the left button column)
            if (newPanel instanceof InventoryPanel) {
                ensureGridDetached();
                sidePanel = newPanel;
                add(sidePanel, BorderLayout.CENTER);
            } else {
                // Other panels stay as a side drawer while keeping the map visible
                ensureGridAttached();
                gridPanel.setVisible(true);
                sidePanel = newPanel;
                add(sidePanel, BorderLayout.EAST);
            }

            revalidate();
            repaint();
        }

        private void ensureGridAttached() {
            if (gridPanel.getParent() == null) {
                add(gridPanel, BorderLayout.CENTER);
            }
        }

        private void ensureGridDetached() {
            if (gridPanel.getParent() != null) {
                remove(gridPanel);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
         
        
}