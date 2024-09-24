package avatar.message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import avatar.constants.NpcName;
import avatar.handler.NpcHandler;
import avatar.item.Item;
import avatar.message.minigame.BauCuaMsgHandler;
import avatar.model.Menu;
import avatar.model.Npc;
import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;
import avatar.server.ServerManager;
import avatar.server.UserManager;
import avatar.service.FarmService;
import avatar.constants.Cmd;

public class CasinoMsgHandler extends MessageHandler {
    private BauCuaMsgHandler service;

    public CasinoMsgHandler(Session client) {
        super(client);
        this.service = new BauCuaMsgHandler(client);
    }

    @Override
    public void onMessage(Message mss) {
        try {
            System.out.println("casino mess: " + mss.getCommand());
            switch (mss.getCommand()) {
                case 61:
                    service.joinCasino(mss);
                    break;
                case 6:
                    requestRoomList();
                    break;
                case 7:
                    BoardList(mss);
                    break;
                case 8:
                    joinBoard(mss,this.client.user);
                case 20:
                    Start(mss);
                default:
                    System.out.println("casino mess: " + mss.getCommand());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestRoomList() throws IOException {//ms 6
        Message ms = new Message(Cmd.REQUEST_ROOMLIST);
        DataOutputStream ds = ms.writer();



        for (int i = 0; i < 3; i++) {
            ds.writeByte(43+i);//id
            ds.writeByte(2);//roomfree
            ds.writeByte(0+i);//roomWait
            ds.writeByte(0+i);//lv
        }
        ds.flush();
        this.client.user.sendMessage(ms);
    }

    private void BoardList(Message ms) throws IOException {//ms 7
        byte id = ms.reader().readByte();
        ms = new Message(Cmd.REQUEST_BOARDLIST);
        DataOutputStream ds = ms.writer();
        ds.writeByte(id);
        for (int i = 0; i < 2; i++) {
            ds.writeByte(0+i);//board id
            ds.writeByte(80);//num3
            ds.writeByte(0);//num4
            ds.writeInt(0);//money
        }
        ds.flush();

        this.client.user.sendMessage(ms);

    }


    private void joinBoard(Message ms, User us) throws IOException {//ms 8
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        String pass = ms.reader().readUTF();
        ms = new Message(Cmd.JOIN_BOARD);
        DataOutputStream ds = ms.writer();


        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeInt(us.getId()); // ID user hoặc ID bàn
        ds.writeInt(0); // số tiền

        List<User> avatars = UserManager.users;
        //ds.writeByte(avatars.size()); // Số lượng avatars

        for (User avatar : avatars) {
            for (int i = 0; i < 5; i++)
            {
                if(i == 0)
                {
                    ds.writeInt(avatar.getId()); // IDDB
                    ds.writeUTF(avatar.getUsername()); // Username
                    ds.writeInt(0); // Số tiền

                    ds.writeByte(avatar.getWearing().size()); // Số phần mặc
                    for (Item item : avatar.getWearing()) {
                        ds.writeShort(item.getId()); // ID item
                    }

                    ds.writeInt(10); // Kinh nghiệm
                    ds.writeBoolean(false); // Trạng thái sẵn sàng
                    ds.writeShort(avatar.getIdImg()); // ID hình ảnh
                }else{
                    ds.writeInt(0); // IDDB
                    ds.writeUTF(""); // Username
                    ds.writeInt(0); // Số tiền

                    ds.writeByte(avatar.getWearing().size()); // Số phần mặc
                    for (Item item : avatar.getWearing()) {
                        ds.writeShort(item.getId()); // ID item
                    }

                    ds.writeInt(10); // Kinh nghiệm
                    ds.writeBoolean(true); // Trạng thái sẵn sàng
                    ds.writeShort(avatar.getIdImg()); // ID hình ảnh
                }
            }
        }
        ds.flush();
        this.client.user.sendMessage(ms);

    }

    private void Start(Message ms) throws IOException {
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        ms = new Message(Cmd.START);
        DataOutputStream ds = ms.writer();

        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeByte(10); // ID user hoặc ID bàn
        ds.flush();
        this.client.user.sendMessage(ms);

    }



}
