package avatar.minigame;

import avatar.item.Item;
import avatar.model.User;
import avatar.network.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class TaiXiu extends User {

    public static final int ID_ADD = 2000000000;

    @Getter
    @Setter
    private List<String> textChats;

    private Thread autoChat = new Thread(() -> {
        while (true) {
            try {
                for (String text : textChats) {
                    getMapService().chat(this, text);
                    Thread.sleep(6000);
                }
                if (textChats == null || textChats.size() == 0) {
                    Thread.sleep(10000);
                }
            } catch (InterruptedException ignored) {
            }
        }
    });
    
    public void addChat(String chat) {
        textChats.add(chat);
    }

    @Override
    public void sendMessage(Message ms) {

    }
}

