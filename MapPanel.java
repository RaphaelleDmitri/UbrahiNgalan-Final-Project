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
    private boolean castleSpawned = false; // Boolean to track if Castle has spawned
    // Track whether the Village Elder has been permanently placed so we don't clear him later
    private boolean elderPlaced = false;

    public MapPanel(Main game){
        this.game = game;
    
        try {
            backgroundImage = ImageIO.read(new File("map.png"));
        } catch (IOException e) {
            System.out.println("Map background image not found, using color instead");
        }
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

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
        tiles[0][1].setText("SHOP");
        //tiles[0][1].setForeground(Color.WHITE);
        

        tiles[0][1].setText("SHOP");
        tiles[0][1].setForeground(new Color(255, 223, 0)); // Bright gold
        tiles[0][1].setFont(GameFonts.pressBold(18f));


        tiles[0][1].setText("SHOP");
        tiles[0][1].setForeground(Color.BLUE); // Change text color to blue
        tiles[0][1].setBackground(Color.DARK_GRAY); // Change background color to dark gray
        tiles[0][1].setOpaque(true);
        tiles[0][1].setContentAreaFilled(true);
        tiles[0][1].setFont(GameFonts.pressBold(20f)); // Change font size

        tiles[0][3].setText("INN");
        tiles[0][3].setForeground(Color.GREEN); // Change text color to green
        tiles[0][3].setBackground(Color.LIGHT_GRAY); // Change background color to light gray
        tiles[0][3].setOpaque(true);
        tiles[0][3].setContentAreaFilled(true);
        tiles[0][3].setFont(GameFonts.pressBold(22f));

        
        

        // Place the Priestess of Tine in the safe zone (player's initial destination)
        tiles[0][0].setText("Priestess of Tine");
        tiles[0][0].setForeground(new Color(144, 238, 144)); // Light green for NPC
        tiles[0][0].setFont(GameFonts.pressBold(18f));
        // tiles[0][4].setText("Spire"); // REMOVED - Spire now spawns after defeating Renz
        

        // Only the Village Elder NPC should appear in the safe zone at start.
        // The elder is placed at tiles[0][0] above; do not spawn other NPCs here.

    
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
    moveInstr.setFont(GameFonts.pressBold(16f));
    controls.add(moveInstr, BorderLayout.CENTER);
    
    // Return to Main Menu button
    JButton returnHomeBtn = new JButton("Main Menu");
    returnHomeBtn.setFont(GameFonts.press(18f));
    returnHomeBtn.setBackground(new Color(60, 60, 60));
    returnHomeBtn.setForeground(new Color(255, 255, 155));
    returnHomeBtn.setFocusPainted(false);
    returnHomeBtn.addActionListener(e -> {
        game.setContentPane(new MainMenuPanel(game));
        game.revalidate();
    });

    // Inventory button with border
    //Inventory Button
    JButton inventoryBtn = new JButton("Inventory");
    inventoryBtn.setFont(GameFonts.press(18f));
    inventoryBtn.setBackground(new Color(60, 60, 60));
    inventoryBtn.setForeground(new Color(255, 255, 155));
    inventoryBtn.setFocusPainted(false);
    JPanel inventoryBtnPanel = new JPanel();
    inventoryBtnPanel.setOpaque(false);
    inventoryBtnPanel.add(inventoryBtn);

    inventoryBtn.addActionListener(e -> {
        toggleSidePanel(new InventoryPanel(game.player));
    });

    // Wrap Main Menu in a panel to mirror Inventory spacing and pull from edge
    JPanel returnBtnPanel = new JPanel();
    returnBtnPanel.setOpaque(false);
    returnBtnPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
    returnBtnPanel.add(returnHomeBtn);
    controls.add(returnBtnPanel, BorderLayout.EAST);
    controls.add(inventoryBtnPanel, BorderLayout.WEST);
    add(controls, BorderLayout.SOUTH);

    // Enable arrow key movement
    setupKeyBindings();
    setFocusable(true);
    requestFocusInWindow();

    SwingUtilities.invokeLater(() -> requestFocusInWindow());

        //add option to  move player with wasd controls
        updatePlayerPosition();
    }
       
    private void styleButton(JButton btn){
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(GameFonts.pressBold(16f));
        btn.setFocusPainted(false);
    }

    // Method to change the Castle to Ruins after defeating Renz
    public void changeCastleToRuins() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (tiles[i][j].getText().equals("CASTLE")) {
                    tiles[i][j].setText("RUINS");
                    tiles[i][j].setForeground(new Color(128, 128, 128)); // Gray
                    tiles[i][j].setFont(GameFonts.pressBold(16f));
                }
            }
        }
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
            
            info.setText("A mysterious Spire has appeared on the map!");
            // Remove any existing story NPC tiles so only the Spire remains
            clearStoryNPCs();
            updatePlayerPosition(); // Refresh the map visually
        }
    }

    // Place the Old Knight on the map (called after Renz is defeated)
    public void placeOldKnight() {
        // Clear existing story NPCs
        clearStoryNPCs();

        // Try to place near the player's current position first, otherwise find any empty tile
        boolean placed = false;
        for (int dx = -2; dx <= 2 && !placed; dx++) {
            for (int dy = -2; dy <= 2 && !placed; dy++) {
                int nx = playerX + dx;
                int ny = playerY + dy;
                if (nx >= 0 && nx < ROWS && ny >= 0 && ny < COLS) {
                    String t = tiles[nx][ny].getText();
                    if ((t.isEmpty() || t.equals(".")) && !(nx == playerX && ny == playerY)) {
                        tiles[nx][ny].setText("Old Knight Garron");
                        tiles[nx][ny].setForeground(new Color(144, 238, 144));
                        tiles[nx][ny].setFont(GameFonts.pressBold(18f));
                        placed = true;
                    }
                }
            }
        }
        if (!placed) {
            for (int i = 0; i < ROWS && !placed; i++) {
                for (int j = 0; j < COLS && !placed; j++) {
                    String t = tiles[i][j].getText();
                    if ((t.isEmpty() || t.equals("."))) {
                        tiles[i][j].setText("Old Knight Garron");
                        tiles[i][j].setForeground(new Color(144, 238, 144));
                        tiles[i][j].setFont(GameFonts.pressBold(18f));
                        placed = true;
                    }
                }
            }
        }
        updatePlayerPosition();
    }

    // Spawn the Priestess near the player; called after Knight conversation ends
    public void spawnPriestess() {
        // Remove existing story NPCs
        clearStoryNPCs();

        boolean placed = false;
        for (int dx = -1; dx <= 1 && !placed; dx++) {
            for (int dy = -1; dy <= 1 && !placed; dy++) {
                int nx = playerX + dx;
                int ny = playerY + dy;
                if (nx >= 0 && nx < ROWS && ny >= 0 && ny < COLS) {
                    String t = tiles[nx][ny].getText();
                    if ((t.isEmpty() || t.equals(".")) && !(nx == playerX && ny == playerY)) {
                        tiles[nx][ny].setText("Priestess of Tine");
                        tiles[nx][ny].setForeground(new Color(240, 200, 240));
                        tiles[nx][ny].setFont(GameFonts.pressBold(18f));
                        placed = true;
                    }
                }
            }
        }
        if (!placed) updatePlayerPosition();
        else updatePlayerPosition();
    }

    // Spawn the Village Elder randomly in the safe zone (top 5x5 area, excluding top-left corner); called after Priestess conversation
    public void spawnElder() {
        // If the Elder has already been placed once, don't spawn another copy
        if (elderPlaced) {
            return;
        }

        // Remove existing story NPCs
        clearStoryNPCs();

        boolean placed = false;
        // Safe zone: i=0 to 4, j=0 to 4, but not [0][0]
        while (!placed) {
            int i = rand.nextInt(5); // 0-4
            int j = rand.nextInt(5); // 0-4
            if (!(i == 0 && j == 0)) { // Exclude top-left
                String t = tiles[i][j].getText();
                if (t.equals(".") || t.isEmpty()) {
                    tiles[i][j].setText("Village Elder");
                    tiles[i][j].setForeground(new Color(144, 238, 144)); // Light green for NPC
                    tiles[i][j].setFont(GameFonts.pressBold(18f));
                    placed = true;
                }
            }
        }
        // Once the Elder has been spawned, mark him as permanent so later story NPC clears don't remove him
        elderPlaced = true;
        updatePlayerPosition();
    }

    // Spawn the Castle randomly on the map; called after talking to both Priestess and Elder
    public void spawnCastle() {
        if (!castleSpawned) {
            int randomXSpawn = rand.nextInt(6) + 5;
            int randomYSpawn = rand.nextInt(6) + 5;
            tiles[randomXSpawn][randomYSpawn].setText("CASTLE");
            tiles[randomXSpawn][randomYSpawn].setForeground(new Color(186, 85, 211)); // Purple
            tiles[randomXSpawn][randomYSpawn].setFont(GameFonts.pressBold(16f));
            castleSpawned = true;
            updatePlayerPosition();
        }
    }

    // Clear any story NPC labels from the map (KNIGHT, PRIESTESS, generic NPC).
    // If the Village Elder has already been placed, keep him on the map.
    private void clearStoryNPCs() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String t = tiles[i][j].getText();
                if ((!elderPlaced && t.equals("Village Elder")) ||
                    t.equals("Old Knight Garron") ||
                    t.equals("Priestess of Tine") ||
                    t.equals("NPC")) {
                    tiles[i][j].setText(".");
                    tiles[i][j].setForeground(Color.WHITE);
                    tiles[i][j].setFont(GameFonts.press(16f));
                }
            }
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
                } 
            } else if(tileName.equals("Ruins")){
                info.setText("The castle lies in ruins. The Corrupted King is no more.");
            } else if(tileName.equals("Village Elder")){
                    info.setText("You approach the Village Elder.");
                    game.startConversation("Village Elder");
                } else if(tileName.equals("Old Knight Garron")){
                    info.setText("You approach Old Knight Garron.");
                    game.startConversation("Old Knight Garron");
                } else if(tileName.equals("Priestess of Tine")){
                    info.setText("You approach the Priestess of Tine.");
                    game.startConversation("Priestess of Tine");
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
                    } else {
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

        private void updatePlayerPosition() {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
        
                    JButton tile = tiles[i][j];
                    String text = tile.getText();
        
                    // Reset visuals
                    tile.setOpaque(false);
                    tile.setContentAreaFilled(false);
        
                    if (text == null || text.isEmpty() || text.equals(".")) {
                        continue;
                    }
        
                    // 🏪 SHOP
                    if (text.equals("SHOP")) {
                        tile.setBackground(new Color(101, 67, 33));                  // brown
                        tile.setForeground(Color.BLACK);
                    }
                    // 🏨 INN
                    else if (text.equals("INN")) {
                        tile.setBackground(new Color(101, 67, 33));                  // brown
                        tile.setForeground(Color.BLACK);
                    }
                    // 🏰 CASTLE / RUINS / SPIRE
                    else {
                        tile.setBackground(new Color(60, 120, 60)); // default building green
                    }
        
                    tile.setOpaque(true);
                    tile.setContentAreaFilled(true);
                }
            }
        
            // 👤 Player tile always overrides
            JButton playerTile = tiles[playerX][playerY];
            playerTile.setBackground(Color.WHITE);
            playerTile.setOpaque(true);
            playerTile.setContentAreaFilled(true);
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