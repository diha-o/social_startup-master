package pc.dd.sex_startup.Main.Chat;

/**
 * Created by UserData on 19.08.2016.
 */

public class ChatMessage {
    public boolean left;
    public String message;
    public String image_url;
    public String nikname;

    public ChatMessage(boolean left, String message, String image_url,String nikname) {
        super();
        this.left = left;
        this.message = message;
        this.image_url= image_url;
        this.nikname = nikname;
    }
}