import java.util.List;

public class DialogueNode {
    private String id;
    private String npcText;
    private List<String> playerChoices;
    private List<String> nextNodes;
    private List<String> npcResponses;

    public DialogueNode(String id, String npcText,
                        List<String> playerChoices,
                        List<String> nextNodes,
                        List<String> npcResponses) {
        this.id = id;
        this.npcText = npcText;
        this.playerChoices = playerChoices;
        this.nextNodes = nextNodes;
        this.npcResponses = npcResponses;
    }

    public String getId() { return id; }

    public String getNPCText() { return npcText; }

    public String getNPCResponse(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= npcResponses.size()) return npcText;
        return npcResponses.get(optionIndex);
    }

    public String getPlayerChoice(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= playerChoices.size()) return "";
        return playerChoices.get(optionIndex);
    }

    public String getNextNode(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= nextNodes.size()) return null;
        return nextNodes.get(optionIndex);
    }
}
