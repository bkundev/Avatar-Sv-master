package avatar.message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import avatar.constants.NpcName;
import avatar.handler.NpcHandler;
import avatar.item.Item;
import avatar.message.minigame.BauCuaMsgHandler;
import avatar.model.BoardInfo;
import avatar.model.Menu;
import avatar.model.Npc;
import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;
import avatar.server.BoardManager;
import avatar.server.ServerManager;
import avatar.server.UserManager;
import avatar.service.FarmService;
import avatar.constants.Cmd;

import static avatar.server.BoardManager.boardList;

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
                    break;
                case 15:
                    leaveBoard(mss,this.client.user);
                    break;
                case 20:
                    Start(mss);
                    break;
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
            ds.writeByte(1);//roomfree//vàng 0 đỏ 2 xanh
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

        List<BoardInfo> boardInfos = BoardManager.getInstance().boardList;


        for(BoardInfo a : boardInfos)
        {
            ds.writeByte(a.boardID);
            ds.writeByte(a.nPlayer);
            if(a.isPass){ds.writeByte(1);}
            else{ds.writeByte(0);}
            ds.writeInt(0);
        }
        ds.flush();

        this.client.user.sendMessage(ms);

    }


    private void joinBoard(Message ms, User us) throws IOException {//ms 8
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        String pass = ms.reader().readUTF();

        BoardInfo board = BoardManager.getInstance().find(boardID);
        BoardManager.getInstance().increaseMaxPlayer(boardID,us);

        ms = new Message(Cmd.JOIN_BOARD);
        DataOutputStream ds = ms.writer();


        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeInt(us.getId()); // ID user
        ds.writeInt(0); // số tiền

        List<User> BoardUs = board.getLstUsers();

        for (int i = 0; i < BoardUs.size(); i++)
        {
            if(BoardUs.get(i).getId() > 0)
            {
                ds.writeInt(BoardUs.get(i).getId()); // IDDB
                ds.writeUTF(BoardUs.get(i).getUsername()); // Username
                ds.writeInt(0); // Số tiền

                ds.writeByte(BoardUs.get(i).getWearing().size()); // Số phần mặc
                for (Item item : BoardUs.get(i).getWearing()) {
                    ds.writeShort(item.getId()); // ID item
                }

                ds.writeInt(10); // Kinh nghiệm
                ds.writeBoolean(false); // Trạng thái sẵn sàng
                ds.writeShort(BoardUs.get(i).getIdImg()); // ID hình ảnh
            }
        }
        for (int i = BoardUs.size(); i < 5; i++) {
            ds.writeInt(-1); // IDDB placeholder for empty slots
        }

        ds.flush();
        this.client.user.sendMessage(ms);
    }

    private void leaveBoard(Message ms, User us) throws IOException {//ms 8
        byte roomID = ms.reader().readByte();

        byte boardID = ms.reader().readByte();

        BoardInfo board = BoardManager.getInstance().find(boardID);
        board.nPlayer--;
//        ms = new Message(Cmd.LEAVE_BOARD);
//        DataOutputStream ds = ms.writer();
//
//
//        ds.flush();
//        this.client.user.sendMessage(ms);
    }

    private void Start(Message ms) throws IOException {//20
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
