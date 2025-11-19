public class Option {
    private String text;
    private String nextNodeId;

    public Option(String text, String nextNodeId) {
        this.text = text;
        this.nextNodeId = nextNodeId;
    }

    public String getText() { return text; }
    public String getNextNodeId() { return nextNodeId; }
}
