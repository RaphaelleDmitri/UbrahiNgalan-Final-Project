import java.awt.*;
import javax.swing.*;

public class MapPanel extends JPanel {
    private Main game;
    private JButton[][] tiles;
    private int playerX = 2; // starting position || CAN BE CHANGED
    private int playerY = 2;

    private final int ROWS = 10;
    private final int COLS = 10;

    private JLabel info; 

    public MapPanel(Main game){
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Info label
        info = new JLabel("You are at the starting location.", SwingConstants.CENTER);
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Consolas", Font.BOLD, 16));
        info.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        add(info, BorderLayout.NORTH);

        // map
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 2, 2));
        gridPanel.setBackground(Color.BLACK);
        tiles = new JButton[ROWS][COLS];

        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
                JButton tile = new JButton();
                tile.setEnabled(false);
                tile.setFont(new Font("Consolas", Font.BOLD, 12));
                tiles[i][j] = tile;
                gridPanel.add(tile);
            }
        }

        // trial buildings
        tiles[0][1].setText("Shop");
        tiles[0][3].setText("Inn");
        tiles[4][2].setText("Castle");

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

        // Button actions
        btnNorth.addActionListener(e -> movePlayer("North"));
        btnSouth.addActionListener(e -> movePlayer("South"));
        btnEast.addActionListener(e -> movePlayer("East"));
        btnWest.addActionListener(e -> movePlayer("West"));

        updatePlayerPosition();
    }

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
            case "North" -> newX--;
            case "South" -> newX++;
            case "East"  -> newY++;
            case "West"  -> newY--;
        }

        // border checker para d ka gwa sa map
        if(newX < 0 || newX >= ROWS || newY < 0 || newY >= COLS) return;

        playerX = newX;
        playerY = newY;
        updatePlayerPosition();

        String tileName = tiles[playerX][playerY].getText();
        if(tileName.equals("")) {
            // random encounter sa goblin
            int r = (int)(Math.random() * 3); // 0,1,2
            if(r == 0){
                game.startBattle();
            } else {
                info.setText("You walk on the grass. Nothing happened.");
            }
        } else {
            info.setText("You are at: " + tileName);
        }
    }

    private void updatePlayerPosition(){
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
                tiles[i][j].setBackground(Color.BLACK); // grass
                if(!tiles[i][j].getText().isEmpty()){
                    tiles[i][j].setBackground(new Color(60,120,60)); // buildings
                }
            }
        }
        // where player
        tiles[playerX][playerY].setBackground(Color.WHITE);
    }
}
