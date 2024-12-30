
import com.google.gson.Gson;

public class GPTMessage {
    private String role;
    private String content;

    public GPTMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String GetRole() {
        return role;
    }
    public String GetContent() {
        return content;
    }
}
