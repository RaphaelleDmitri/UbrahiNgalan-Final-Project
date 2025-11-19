import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    
    

    // Tracks current dialogue node per NPC
    private Map<NPC, String> currentNode = new HashMap<>();

    public NPCConversation(Main game, Player player, List<NPC> npcs) {
        this.game = game;
        this.player = player;
        this.npcs = new ArrayList<>(npcs);

        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 25));

        infoLabel = new JLabel(updateNPCInfo(), SwingConstants.CENTER);
        infoLabel.setForeground(new Color(230, 205, 70));
        infoLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(infoLabel, BorderLayout.NORTH);

        // Styled log setup
        styledPane = new JTextPane();
        styledPane.setEditable(false);
        styledPane.setBackground(new Color(40, 40, 40));
        styledPane.setForeground(Color.WHITE);
        styledPane.setFont(new Font("Consolas", Font.PLAIN, 25));
        styledPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 2));
        doc = styledPane.getStyledDocument();
        createStyles(doc);

        JScrollPane scroll = new JScrollPane(styledPane);
        add(scroll, BorderLayout.CENTER);

        log = new ForwardingTextArea();

        // NPC selector
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(25, 25, 25));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        npcBox = new JComboBox<>();
        npcBox.setFont(new Font("Consolas", Font.BOLD, 20));
        npcBox.setBackground(new Color(60, 60, 60));
        npcBox.setForeground(new Color(240, 220, 140));
        updateNPCBox();
        rightPanel.add(new JLabel("NPC:"), BorderLayout.NORTH);
        rightPanel.add(npcBox, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Dialogue buttons
        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(25, 25, 25));

        JButton option1 = styledBtn("Option 1");
        JButton option2 = styledBtn("Option 2");
        JButton option3 = styledBtn("Option 3");

        buttons.add(option1);
        buttons.add(option2);
        buttons.add(option3);
        add(buttons, BorderLayout.SOUTH);

        option1.addActionListener(e -> handleDialogue(1));
        option2.addActionListener(e -> handleDialogue(2));
        option3.addActionListener(e -> handleDialogue(3));

        // Start conversation with first NPC
        if (!npcs.isEmpty()) {
            NPC first = npcs.get(0);
            currentNode.put(first, first.getStartNode());
            log.setText(first.getName() + " approaches you...\n");
            displayCurrentDialogue(first);
        }
    }

    private JButton styledBtn(String txt) {
        JButton btn = new JButton(txt);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(new Color(240, 220, 140));
        btn.setFont(new Font("Consolas", Font.BOLD, 32));
        btn.setFocusPainted(false);
        return btn;
    }

    private void handleDialogue(int option) {
        int idx = npcBox.getSelectedIndex();
        if (idx < 0 || idx >= npcs.size()) return;

        NPC current = npcs.get(idx);
        String nodeId = currentNode.get(current);
        if (nodeId == null) return;

        DialogueNode node = current.getNode(nodeId);
        if (node == null) return;

        String playerChoice = node.getPlayerChoice(option);
        log.append("\nYou: " + playerChoice);

        // Determine next node based on option
        String nextNode = node.getNextNode(option);
        if (nextNode == null) {
            // End of conversation
            log.append("\n" + current.getName() + ": " + node.getNPCResponse(option));
            log.append("\n\n>> Conversation ended.");
            return;
        }

        currentNode.put(current, nextNode);
        displayCurrentDialogue(current);
    }

    private void displayCurrentDialogue(NPC npc) {
        String nodeId = currentNode.get(npc);
        DialogueNode node = npc.getNode(nodeId);
        if (node == null) return;

        log.append("\n" + npc.getName() + ": " + node.getNPCResponse(0)); // 0 = default message
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
        StyleConstants.setFontFamily(def, "Consolas");
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
}