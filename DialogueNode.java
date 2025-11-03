class DialogueNode {
    private String npcMessage;                  // what NPC says at this node
    private String[] playerChoices;             // options player can pick
    private Map<Integer, String> nextNodes;     // mapping option -> next node id (null = end)

    public DialogueNode(String npcMessage, String[] playerChoices, Map<Integer, String> nextNodes) {
        this.npcMessage = npcMessage;
        this.playerChoices = playerChoices;
        this.nextNodes = nextNodes;
    }

    public String getNPCResponse(int idx) { return npcMessage; }
    public String getPlayerChoice(int idx) {
        if (idx >= 1 && idx <= playerChoices.length) return playerChoices[idx - 1];
        return "...";
    }

    public String getNextNode(int idx) { return nextNodes.get(idx); }
}