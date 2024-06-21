package avatar.model;

import avatar.item.Item;
import avatar.network.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import avatar.play.Map;
import avatar.play.MapManager;
import avatar.play.Zone;
import avatar.server.ServerManager;
import avatar.service.EffectService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class Npc extends User {

    public static final int ID_ADD = 2000000000;
    public User user;
    private static int globalHp = 1000;
    public static AtomicInteger currentNpcCount = new AtomicInteger(0);
    private int id;
    private int hp;
    // Phương thức để giảm HP toàn cục của NPC khi bị tấn công
    public static synchronized void decreaseGlobalHp(int damage) {
        globalHp -= damage;
        if (globalHp <= 0) {
            globalHp = 0;
        }
    }
    public static synchronized int getGlobalHp() {
        return globalHp;
    }
    public static synchronized void resetGlobalHp(int hp) {
        globalHp = hp; // Reset lại HP khi NPC được khởi tạo lại hoặc đặt lại
    }

    // Ví dụ hàm để xử lý khi NPC bị giết và chuyển sang khu vực khác
    public void handleNpcKilled(Npc npc) {
        Zone npcZone = npc.getZone(); // Lấy khu vực hiện tại của NPC
        if (npcZone != null) {
            Map m = MapManager.getInstance().find(11);//map cv
            if (m != null) {
                List<Zone> zones = m.getZones();
                Zone randomZone = zones.get(new Random().nextInt(zones.size())); // Chọn ngẫu nhiên một khu vực từ danh sách

                // Ngẫu nhiên vị trí xuất hiện trong khu vực mới
                short randomX = (short) 250;
                short randomY = (short) 50;

                // Cập nhật thông tin và chuyển NPC sang khu vực mới
                npcZone.leave(npc); // Loại bỏ NPC khỏi khu vực hiện tại
                npc.resetGlobalHp(1000*2); // Reset HP cho NPC (hoặc giá trị mặc định)

                randomZone.enter(npc, randomX, randomY); // Nhập NPC vào khu vực mới với vị trí ngẫu nhiên

                // Cập nhật số lượng NPC hiện tại trong khu vực (không cần cập nhật nếu sử dụng AtomicInteger)
                currentNpcCount.getAndDecrement(); // Giảm số lượng NPC hiện tại trong khu vực hiện tại
            }
        }
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

    @Builder
    public Npc(int id, String name, short x, short y, ArrayList<Item> wearing) {
        setId(id > ID_ADD ? id : id + ID_ADD);
        setUsername(name);
        setRole((byte) 0);
        setX(x);
        setY(y);
        setWearing(wearing);
        textChats = new ArrayList<>();
        autoChatBot.start();
    }

    public void addChat(String chat) {
        textChats.add(chat);
    }


    @Override
    public void sendMessage(Message ms) {

    }
    public void NpcMove(Npc npc ,short x, short y){
        npc.setX(x);
        npc.setX(y);
        npc.setDirect((byte)2);
        npc.getMapService().move(npc);
    }

    public void skill(Npc npc,byte id){
        List<User> players = npc.getZone().getPlayers();
        for (User player : players) {
            EffectService.createEffect()
                    .session(player.session)
                    .id(id)
                    .style((byte) 0)
                    .loopLimit((byte) 5)
                    .loop((short) 5)
                    .loopType((byte) 1)
                    .radius((short) 50)
                    .idPlayer(npc.getId())
                    .send();
        };
    }
}