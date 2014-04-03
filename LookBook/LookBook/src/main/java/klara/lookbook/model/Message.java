package klara.lookbook.model;

public class Message {

    private String text;
    private String authorNick;
    private int time;

    public Message(String authorNick, String text, int time) {
        this.authorNick = authorNick;
        this.text = text;
        this.time = time;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAuthorNick() {
        return authorNick;
    }

    public void setAuthorNick(String authorNick) {
        this.authorNick = authorNick;
    }
}
