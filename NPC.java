import java.util.HashMap;
import java.util.Map;

public class NPC {
    private String name;
    private String startNode;
    private Map<String, DialogueNode> dialogueNodes;

    public NPC(String name, String startNode) {
        this.name = name;
        this.startNode = startNode;
        this.dialogueNodes = new HashMap<>();
    }

    public String getName() { return name; }
    public String getStartNode() { return startNode; }

    public void addNode(DialogueNode node) {
        dialogueNodes.put(node.getId(), node);
    }

    public DialogueNode getNode(String nodeId) {
        return dialogueNodes.get(nodeId);
    }
}
