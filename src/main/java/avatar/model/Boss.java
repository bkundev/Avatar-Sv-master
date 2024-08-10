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
import avatar.service.Service;
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

    public void addBossToZone(Zone zone, short x, short y) throws IOException {
        Boss boss = new Boss();
        List<Integer> availableItems = Arrays.asList(2401, 4552, 6314, 6432);
        Utils random = null;
        boss.setId(999);
        int randomItemId = availableItems.get(random.nextInt(availableItems.size()));
        boss.setUsername("BOSS");
        boss.setX(x);
        boss.setY(y);
        boss.addItemToWearing((new Item(randomItemId)));

        List<String> chatMessages = Arrays.asList(
                "lại đây nào",
                "quà của bạn đây"
        );
        boss.setTextChats(chatMessages);
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
            boss.session = session;
            System.out.println("Session created with ID: " + session.id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageHandler handler = new MessageHandler(boss.session);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            // Viết các giá trị theo định dạng mong đợi
            dos.writeByte(0); // provider
            dos.writeInt(1024); // memory
            dos.writeUTF("MicroEmulator"); // platform
            dos.writeInt(512); // rmsSize
            dos.writeInt(1080); // width
            dos.writeInt(1920); // height
            dos.writeBoolean(true); // aaaaa
            dos.writeByte(0); // resource
            dos.writeUTF("v1.0"); // version

            // Viết các chuỗi bổ sung (nếu có)
            dos.writeUTF("1");
            dos.writeUTF("2");
            dos.writeUTF("3");

            dos.flush();
            byte[] data = baos.toByteArray();
            dos.close();
        //byte[] data1 = new byte[] {9,};
        handler.onMessage(new Message(Cmd.SET_PROVIDER,data));
        //MessageHandler  getHandler
        byte[] data2 = new byte[] {9};
        boss.session.getHandler(new Message(Cmd.GET_HANDLER,data2));
        Message ms = new Message(Cmd.AVATAR_JOIN_PARK);
        ParkMsgHandler parkMsgHandler = new ParkMsgHandler(boss.session);
        parkMsgHandler.onMessage(ms);
        byte[] data1 = new byte[] {8};
        boss.session.getHandler(new Message(Cmd.GET_HANDLER,data1));
            handler.onMessage(new Message(Cmd.SET_PROVIDER,data));
            //MessageHandler  getHandler
            byte[] data3 = new byte[] {9};
            boss.session.getHandler(new Message(Cmd.GET_HANDLER,data3));
            Message ms1 = new Message(Cmd.AVATAR_JOIN_PARK);
            ParkMsgHandler parkMsgHandler1 = new ParkMsgHandler(boss.session);
            parkMsgHandler1.onMessage(ms1);
        System.out.println("add boss khu :"+zone.getId());
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Builder
    public void addChat(String chat) {
        textChats.add(chat);
    }


    @Override
    public void sendMessage(Message ms) {

    }
}
