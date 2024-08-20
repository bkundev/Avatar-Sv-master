package avatar.model;

import avatar.constants.Cmd;
import avatar.item.Item;
import avatar.message.MessageHandler;
import avatar.message.ParkMsgHandler;
import avatar.network.Message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import avatar.network.Session;
import avatar.play.Map;
import avatar.play.MapManager;
import avatar.play.NpcManager;
import avatar.play.Zone;
import avatar.server.ServerManager;
import avatar.server.UserManager;
import avatar.server.Utils;
import avatar.service.EffectService;
import avatar.service.ParkService;
import avatar.service.Service;
import com.sun.source.tree.ReturnTree;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import avatar.play.Zone;

import static avatar.model.Npc.ID_ADD;

public class Boss extends User {
    @Getter
    @Setter
    private List<String> textChats;

    public Boss() {
        super();
        autoChatBot.start();
    }
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int TOTAL_BOSSES = 40; // Tổng số Boss muốn tạo
    public static int currentBossId = 1001 + Npc.ID_ADD; // ID bắt đầu cho Boss
    private static int bossCount = 0; // Đếm số lượng Boss đã được tạo
    private Thread autoChatBot = new Thread(() -> {
        while (true) {
            try {
                if (textChats == null) {
                    textChats = new ArrayList<>(); // Hoặc khởi tạo với một giá trị mặc định
                }
                for (String text : textChats) {
                    getMapService().chat(this, text);
                    Thread.sleep(6000);
                }
                if (textChats == null || textChats.size() == 0) {
                    Thread.sleep(10000);
                }
                int[][] pairs = {
                        {12, 3},
                        {19, 18},
                        {23, 24},
                        {25, 26},
                        {23, 24},
                        {38, 39},
                        {40, 41},
                        {42, 43},
                        {49, 50},
                        {51, 51},
                        {63, 64},
                        {69, 70},
                        {71, 72},
                };
                if(this.getHP()>0)
                {
                    Random rand = new Random();
                    int randomIndex = rand.nextInt(pairs.length);
                    int[] selectedPair = pairs[randomIndex];
                    BossSkillRanDomUser((byte)selectedPair[0], (byte)selectedPair[1]);
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt(); // Đảm bảo xử lý gián đoạn
            }
        }
    });

    public synchronized void handleBossDefeat(Boss boss, User us) throws IOException {
        boss.getZone().getPlayers().forEach(u -> {
            EffectService.createEffect()
                    .session(u.session)
                    .id((byte) 45)
                    .style((byte) 0)
                    .loopLimit((byte) 6)
                    .loop((short) 1)//so luong lap lai
                    .loopType((byte) 1)
                    .radius((short) 5)
                    .idPlayer(boss.getId())
                    .send();
        });
        String username = us.getUsername();  // Lấy tên người dùng
        String message = String.format("Khá lắm bạn %s đã kill được", username);
        List<String> newMessages = Arrays.asList(message,"Ta sẽ quay lại sau!!!");
        this.textChats = new ArrayList<>(newMessages);
        for (String chatMessage : textChats) {
            getMapService().chat(boss, chatMessage);
        }
            scheduler.schedule(() -> {
                boss.getZone().leave(boss);
                try {
                    this.createNearbyGiftBoxes(boss, boss.getZone(), boss.getX(), boss.getY(), Boss.currentBossId + 10000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 5, TimeUnit.SECONDS);// 2 giây trễ trước khi boss rời khỏi zone
       // boss.getZone().leave(boss);

    }
    public void addBossToZone(Zone zone, short x, short y) throws IOException {
        if (bossCount >= TOTAL_BOSSES) {
            return; // Dừng nếu đã tạo đủ số lượng Boss
        }
        Boss boss = createBoss(x, y, currentBossId++);
        assignRandomItemToBoss(boss);
        boss.setHP(1000);
        List<String> chatMessages = Arrays.asList("YAAAA", "YOOOO");
        boss.setTextChats(chatMessages);
        boss.session = createSession(boss);
        sendAndHandleMessages(boss);
        moveBoss(boss);
        moveBossXY(boss,282,88);
        bossCount++; // Tăng số lượng Boss đã tạo
        //createNearbyGiftBoxes(boss,zone, x, y, currentBossId + 10000);
        System.out.println("add boss khu :" + zone.getId());
    }

    private Boss createBoss(short x, short y,int id) {
        Boss boss = new Boss();
        boss.setId(id);
        boss.setUsername("o");
        boss.setX(x);
        boss.setY(y);
        return boss;
    }
    private void createGiftBox(Zone zone, short x, short y, int giftId) throws IOException {
        Boss giftBox = createBoss(x, y, giftId);
        assignGiftItemToBoss(giftBox); // Gán item cho hộp quà
        giftBox.session = createSession(giftBox);
        giftBox.setSpam(10);
        sendAndHandleMessages(giftBox);
        moveBoss(giftBox);

        System.out.println("Tạo hộp quà phân thân với ID: " + giftId + " tại vị trí X: " + x + ", Y: " + y);
    }
    public void createNearbyGiftBoxes(Boss boss, Zone zone, short x, short y, int baseGiftId) throws IOException {
        // Tạo hộp quà ở các vị trí gần Boss
        createGiftBox(zone, (short) (boss.getX()+(short)20),(short) (boss.getY()+(short)20),baseGiftId);
        createGiftBox(zone, (short) (boss.getX()-(short)20), (short) (boss.getY()-(short)20), baseGiftId + 1);
        createGiftBox(zone, (short) (boss.getX()+(short)20), (short) (boss.getY()-(short)20), baseGiftId + 2);
        createGiftBox(zone, (short) (boss.getX()-(short)20), (short) (boss.getY()+(short)20), baseGiftId + 3);
    }

    private void assignGiftItemToBoss(Boss boss) {
        // Gán item cụ thể cho hộp quà phân thân, nếu khác với Boss chính
        List<Integer> giftItems = Arrays.asList(5581, 5582, 5583); // Ví dụ các item cho hộp quà
        int randomItemId = giftItems.get(new Random().nextInt(giftItems.size()));
        boss.addItemToWearing(new Item(randomItemId));
    }

    private void assignRandomItemToBoss(Boss boss) {
        List<Integer> availableItems = Arrays.asList(6314, 6432);
        Utils random = new Utils(); // Assuming Utils is instantiated
        int randomItemId = availableItems.get(random.nextInt(availableItems.size()));
        boss.addItemToWearing(new Item(randomItemId));
    }
    private void sendAndHandleMessages(Boss boss) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeByte(0);
            dos.writeInt(1024);
            dos.writeUTF("MicroEmulator");
            dos.writeInt(512);
            dos.writeInt(1080);
            dos.writeInt(1920);
            dos.writeBoolean(true);
            dos.writeByte(0);
            dos.writeUTF("v1.0");
            dos.writeUTF("1");
            dos.writeUTF("2");
            dos.writeUTF("3");
            dos.flush();
            byte[] data = baos.toByteArray();

            MessageHandler handler = new MessageHandler(boss.session);
            handler.onMessage(new Message(Cmd.SET_PROVIDER, data));

            byte[] data2 = new byte[]{9};
            boss.session.getHandler(new Message(Cmd.GET_HANDLER, data2));

            Message ms = new Message(Cmd.AVATAR_JOIN_PARK);
            ParkMsgHandler parkMsgHandler = new ParkMsgHandler(boss.session);
            parkMsgHandler.onMessage(ms);
            System.out.println("add boss khu :" + boss.getZone().getId());
        }
    }
    private void moveBoss(Boss boss) throws IOException {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        try (DataOutputStream dos1 = new DataOutputStream(baos1)) {
            dos1.writeShort(boss.getX());//x
            dos1.writeShort(boss.getY());//y
            dos1.writeByte(2);
            dos1.flush();
            byte[] data1 = baos1.toByteArray();

            ParkMsgHandler parkMsgHandler1 = new ParkMsgHandler(boss.session);
            parkMsgHandler1.onMessage(new Message(Cmd.MOVE_PARK, data1));
            getMapService().chat(this, "ta đến rồi đây");
            System.out.println("boss move : X = " + boss.getX() + ", y = " + boss.getY());
        }
    }
    private void moveBossXY(Boss boss,int x,int y) throws IOException {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        try (DataOutputStream dos1 = new DataOutputStream(baos1)) {
            dos1.writeShort(x);//x
            dos1.writeShort(y);//y
            dos1.writeByte(2);
            dos1.flush();
            byte[] data1 = baos1.toByteArray();

            ParkMsgHandler parkMsgHandler1 = new ParkMsgHandler(boss.session);
            parkMsgHandler1.onMessage(new Message(Cmd.MOVE_PARK, data1));
            System.out.println("boss move : X = " + boss.getX() + ", y = " + boss.getY());
        }
    }
    public Session createSession(Boss boss){
        //Cmd.SET_PROVIDER
        try {
            // Tạo một Socket (thay thế bằng thông tin kết nối thực tế)
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 19128)); // Thay thế bằng địa chỉ IP và cổng thực tế
            int sessionId = 9999; // Ví dụ về ID, có thể là bất kỳ giá trị nào phù hợp
            Session session = new Session(socket, sessionId);
            session.ip = "127.0.0.1";
            session.user = boss;
            session.connected = true;
            session.login = true;
            System.out.println("Session created with ID: " + session.id);
            return session;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Builder
    public void addChat(String chat) {
        textChats.add(chat);
    }

    @Override
    public void sendMessage(Message ms) {

    }
    public void BossSkillRanDomUser(byte skill1,byte skill2){
        Random rand = new Random();
        List<User> players = this.session.user.getZone().getPlayers();
        User randomPlayer = null;
        while (randomPlayer == null) {
            int rplayerIndex = rand.nextInt(players.size());
            User playerss = players.get(rplayerIndex);

            if (playerss.getId() < Npc.ID_ADD) {
                randomPlayer = playerss;
            }
        }
        for (User player : players) {
            EffectService.createEffect()
                    .session(player.session)
                    .id(skill1)
                    .style((byte) 0)
                    .loopLimit((byte) 5)
                    .loop((short) 3)
                    .loopType((byte) 1)
                    .radius((short) 1)
                    .idPlayer(this.session.user.getId())
                    .send();
            EffectService.createEffect()
                    .session(player.session)
                    .id(skill2)
                    .style((byte) 0)
                    .loopLimit((byte) 5)
                    .loop((short) 3)
                    .loopType((byte) 1)
                    .radius((short) 1)
                    .idPlayer(randomPlayer.getId())
                    .send();
        };
    }
}
