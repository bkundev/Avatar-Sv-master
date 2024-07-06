package avatar.model;

import avatar.item.Item;
import avatar.network.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import avatar.network.Session;
import avatar.play.Map;
import avatar.play.MapManager;
import avatar.play.NpcManager;
import avatar.play.Zone;
import avatar.server.ServerManager;
import avatar.server.UserManager;
import avatar.server.Utils;
import com.sun.source.tree.ReturnTree;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import avatar.play.Zone;

import static avatar.model.Npc.ID_ADD;

public class Boss extends User {

    public Boss() {
        super();
    }
    @Getter
    @Setter
    private List<String> textChats;

    private Thread autoChatBot = new Thread(() -> {
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

    public void addBossToZone(Zone zone, short x, short y) {
        List<Integer> availableItems = Arrays.asList(2401, 4552, 6314, 6432);
        Utils random = null;
        this.setId(999+ID_ADD);
        int randomItemId = availableItems.get(random.nextInt(availableItems.size()));
        this.setUsername("BOSS");
        this.setX(x);
        this.setY(y);
        this.addItemToWearing((new Item(randomItemId)));
        this.addChat("muỗi");
        zone.add(this);

        getMapService().addPlayer(this);
        //ServerManager.joinAreaMessage(this.client.user, mss);//send qua lst us

        // Cập nhật thông tin zone của boss
        this.setZone(zone);
        // Thông báo thêm boss thành công
        System.out.println("add boss khu :"+zone.getId());
    }
    @Builder
    public void addChat(String chat) {
        textChats.add(chat);
        User user = new User();
    }


    @Override
    public void sendMessage(Message ms) {

    }
}
