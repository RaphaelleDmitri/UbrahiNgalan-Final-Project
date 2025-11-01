//gin import nisya sa mappanel == can be used for future references

import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private Main game;
    private JLabel info;

    public GamePanel(Main game){
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(new Color(25,25,25));

        // Info label
        info = new JLabel("You are standing in a dark forest...", SwingConstants.CENTER);
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Consolas", Font.BOLD, 18));
        add(info, BorderLayout.NORTH);

        JPanel directions = new JPanel();
        directions.setBackground(new Color(25,25,25));

        JButton btnNorth = new JButton("North");
        JButton btnSouth = new JButton("South");
        JButton btnEast  = new JButton("East");
        JButton btnWest  = new JButton("West");

        styleButton(btnNorth); styleButton(btnSouth); 
        styleButton(btnEast); styleButton(btnWest);

        directions.add(btnNorth);
        directions.add(btnSouth);
        directions.add(btnEast);
        directions.add(btnWest);

        add(directions, BorderLayout.SOUTH);

        btnNorth.addActionListener(ev -> move());
        btnSouth.addActionListener(ev -> move());
        btnEast.addActionListener(ev -> move());
        btnWest.addActionListener(ev -> move());
    }

    private void styleButton(JButton btn){
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(new Font("Consolas", Font.BOLD, 16));
        btn.setFocusPainted(false);
    }

    private void move(){
        int r = (int)(Math.random() * 3);
        if(r == 0){
            game.startBattle();
        } else {
            info.setText("You move forward... nothing happened.");
        }
    }
}
