class NPC {
    private String name;
    private Map<String, DialogueNode> dialogueNodes;
    private String startNode;

    public NPC(String name, String startNode, Map<String, DialogueNode> dialogueNodes) {
        this.name = name;
        this.startNode = startNode;
        this.dialogueNodes = dialogueNodes;
    }

    public String getName() { return name; }
    public String getStartNode() { return startNode; }
    public DialogueNode getNode(String nodeId) { return dialogueNodes.get(nodeId); }
}