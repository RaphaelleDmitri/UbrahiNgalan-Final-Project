import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;

/**
 * NPCConversation with branching dialogue template
 *
 * - Each NPC has dialogue nodes with multiple options.
 * - Selecting an option moves to the next dialogue node (or ends conversation).
 * - Template-friendly: just change NPCs, nodes, options, and consequences.
 */
public class NPCConversation extends JPanel {
    private Main game;
    private Player player;

    private List<NPC> npcs;
    private JComboBox<String> npcBox;

    private JTextArea log;
    private JTextPane styledPane;
    private StyledDocument doc;

    private JLabel infoLabel;
    private Random rand = new Random();
    private JPanel buttons;
    private JButton[] optionButtons = new JButton[3];
    private BufferedImage backgroundImage;
    private boolean useImageBackground = false;
    
    

    // Tracks current dialogue node per NPC
    private Map<NPC, String> currentNode = new HashMap<>();

    public NPCConversation(Main game, Player player, List<NPC> npcs, String selectedNpcName) {
        this.game = game;
        this.player = player;
        this.npcs = new ArrayList<>(npcs);

        setLayout(new BorderLayout());
        setOpaque(false);

        infoLabel = new JLabel(updateNPCInfo(), SwingConstants.CENTER);
        infoLabel.setForeground(new Color(230, 205, 70));
        infoLabel.setFont(GameFonts.pressBold(20f));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        infoLabel.setOpaque(false);
        add(infoLabel, BorderLayout.NORTH);

        // Styled log setup
        styledPane = new JTextPane();
        styledPane.setEditable(false);
        styledPane.setOpaque(false);
        styledPane.setBackground(new Color(0, 0, 0, 0));
        styledPane.setForeground(Color.WHITE);
        styledPane.setFont(GameFonts.press(25f));
        styledPane.setBorder(null);
        doc = styledPane.getStyledDocument();
        createStyles(doc);

        JScrollPane scroll = new JScrollPane(styledPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        log = new ForwardingTextArea();

        // NPC selector
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        npcBox = new JComboBox<>();
        npcBox.setFont(GameFonts.pressBold(20f));
        npcBox.setOpaque(false);
        npcBox.setBackground(new Color(0, 0, 0, 0));
        npcBox.setForeground(new Color(240, 220, 140));
        updateNPCBox();
        // If a specific NPC was requested, select them
        if (selectedNpcName != null) {
            for (int i = 0; i < this.npcs.size(); i++) {
                if (this.npcs.get(i).getName().equals(selectedNpcName)) {
                    final int idx = i;
                    SwingUtilities.invokeLater(() -> npcBox.setSelectedIndex(idx));
                    break;
                }
            }
        }
        rightPanel.add(new JLabel("NPC:"), BorderLayout.NORTH);
        rightPanel.add(npcBox, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Dialogue buttons (will show actual choice text)
        buttons = new JPanel();
        buttons.setOpaque(false);

        for (int i = 0; i < 3; i++) {
            optionButtons[i] = styledBtn("Option " + (i+1));
            final int idx = i + 1;
            optionButtons[i].addActionListener(e -> handleDialogue(idx));
            buttons.add(optionButtons[i]);
        }
        add(buttons, BorderLayout.SOUTH);

        // NPC selector change listener
        npcBox.addActionListener(e -> {
            int sel = npcBox.getSelectedIndex();
            if (sel >= 0 && sel < npcs.size()) {
                NPC selNpc = npcs.get(sel);
                // Reset to start node for this NPC
                currentNode.put(selNpc, selNpc.getStartNode());
                // Clear log and show fresh dialogue
                log.setText(selNpc.getName() + " speaks:\n");
                displayCurrentDialogue(selNpc);
                infoLabel.setText("Talking to: " + selNpc.getName());
                loadBackgroundForNPC(selNpc);
            }
        });

        // Start conversation with selected NPC or the first NPC
        if (!npcs.isEmpty()) {
            int startIdx = 0;
            if (selectedNpcName != null) {
                for (int i = 0; i < this.npcs.size(); i++) if (this.npcs.get(i).getName().equals(selectedNpcName)) { startIdx = i; break; }
            }
            NPC first = this.npcs.get(startIdx);
            currentNode.put(first, first.getStartNode());
            log.setText(first.getName() + " approaches you...\n");
            // ensure npcBox selection is synced
            final int sel = startIdx;
            SwingUtilities.invokeLater(() -> npcBox.setSelectedIndex(sel));
            displayCurrentDialogue(first);
            infoLabel.setText("Talking to: " + first.getName());
            loadBackgroundForNPC(first);
        }
    }

    private JButton styledBtn(String txt) {
        JButton btn = new JButton(txt);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 220, 140), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setForeground(new Color(240, 220, 140));
        btn.setFont(GameFonts.jettsBold(32f));
        btn.setFocusPainted(false);
        return btn;
    }

    // Load a themed background per NPC so the panel drops solid colors
    private void loadBackgroundForNPC(NPC npc) {
        backgroundImage = null;
        useImageBackground = false;
        if (npc == null || npc.getName() == null) {
            repaint();
            return;
        }

        String lowerName = npc.getName().toLowerCase();
        String imagePath = null;
        if (lowerName.contains("elder")) {
            imagePath = "elder.png";
        } else if (lowerName.contains("knight")) {
            imagePath = "oldknight.png";
        } else if (lowerName.contains("priestess")) {
            imagePath = "priestess.png";
        }

        if (imagePath != null) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
                useImageBackground = backgroundImage != null;
            } catch (IOException ex) {
                System.err.println("Failed to load NPC background: " + ex.getMessage());
            }
        }
        repaint();
    }
    //yessirr
    private void handleDialogue(int option) {
        int idx = npcBox.getSelectedIndex();
        if (idx < 0 || idx >= npcs.size()) return;

        NPC current = npcs.get(idx);
        String nodeId = currentNode.get(current);
        if (nodeId == null) return;

        DialogueNode node = current.getNode(nodeId);
        if (node == null) return;

        // Convert 1-based button index to 0-based array index
        int arrayIdx = option - 1;

        String playerChoice = node.getPlayerChoice(arrayIdx);
        log.append("\nYou: " + playerChoice);

        // Determine next node based on option
        String nextNode = node.getNextNode(arrayIdx);
        if (nextNode == null) {
            // End of conversation
            log.append("\n" + current.getName() + ": " + node.getNPCResponse(arrayIdx));
            log.append("\n\n>> Conversation ended.");
            // Notify game that this NPC conversation ended so progression can continue
            try {
                game.onNPCConversationEnded(current.getName(), nodeId);
            } catch (Exception ex) {
                System.out.println("DEBUG: onNPCConversationEnded not handled: " + ex.getMessage());
            }
            // Wait 3 seconds so the player can read the final lines, then return to the map
            javax.swing.Timer timer = new javax.swing.Timer(3000, ev -> {
                game.returnToMap();
            });
            timer.setRepeats(false);
            timer.start();
            return;
        }

        currentNode.put(current, nextNode);
        displayCurrentDialogue(current);
    }

    private void displayCurrentDialogue(NPC npc) {
        String nodeId = currentNode.get(npc);
        DialogueNode node = npc.getNode(nodeId);
        if (node == null) return;

        // Show NPC's current line (the initial message for this node)
        log.append("\n" + npc.getName() + ": " + node.getNPCText());

        // Update option buttons to show actual player choices
        for (int i = 0; i < optionButtons.length; i++) {
            String choice = node.getPlayerChoice(i);
            if (choice == null || choice.isEmpty()) {
                optionButtons[i].setVisible(false);
            } else {
                optionButtons[i].setText(choice);
                optionButtons[i].setVisible(true);
            }
        }
        buttons.revalidate();
        buttons.repaint();
    }

    private String updateNPCInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Talking to: ");
        for (NPC npc : npcs) {
            sb.append(npc.getName()).append(" ");
        }
        return sb.toString();
    }

    private void createStyles(StyledDocument d) {
        Style def = d.addStyle("default", null);
        StyleConstants.setFontFamily(def, GameFonts.jetts(25f).getFamily());
        StyleConstants.setFontSize(def, 25);
        StyleConstants.setForeground(def, Color.WHITE);

        Style playerStyle = d.addStyle("player", def);
        StyleConstants.setForeground(playerStyle, Color.YELLOW);

        Style npcStyle = d.addStyle("npc", def);
        StyleConstants.setForeground(npcStyle, new Color(0, 140, 255));

        Style system = d.addStyle("system", def);
        StyleConstants.setForeground(system, new Color(100, 225, 100));
        StyleConstants.setBold(system, true);
    }

    private void appendStyledByHeuristics(String raw) {
        if (raw == null || raw.isEmpty()) return;
        String[] parts = raw.split("(?<=\\n)");
        for (String part : parts) {
            try {
                if (part.startsWith("You:")) doc.insertString(doc.getLength(), part, doc.getStyle("player"));
                else if (part.contains(":")) doc.insertString(doc.getLength(), part, doc.getStyle("npc"));
                else doc.insertString(doc.getLength(), part, doc.getStyle("system"));
            } catch (BadLocationException ex) {
                styledPane.setText(styledPane.getText() + part);
            }
        }
        SwingUtilities.invokeLater(() -> styledPane.setCaretPosition(doc.getLength()));
    }

    private class ForwardingTextArea extends JTextArea {
        @Override
        public void append(String str) { appendStyledSafely(str); }
        @Override
        public void setText(String text) {
            try { doc.remove(0, doc.getLength()); } catch (BadLocationException ex) {}
            appendStyledSafely(text);
        }
        private void appendStyledSafely(String s) { if (s != null) appendStyledByHeuristics(s); }
    }

    private void updateNPCBox() {
        SwingUtilities.invokeLater(() -> {
            int selectedIndex = npcBox.getSelectedIndex();
            npcBox.removeAllItems();
            for (NPC npc : npcs) npcBox.addItem(npc.getName());
            if (npcBox.getItemCount() > 0) {
                if (selectedIndex >= 0 && selectedIndex < npcBox.getItemCount())
                    npcBox.setSelectedIndex(selectedIndex);
                else npcBox.setSelectedIndex(0);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (useImageBackground && backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}