package smg.models;

public class Suggestion {
    private String id;
    private String content;

    public Suggestion(String id, String content){
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
