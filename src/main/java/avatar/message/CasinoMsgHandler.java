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
                case Cmd.REQUEST_ROOMLIST:
                    requestRoomList();
                    break;
                case Cmd.GET_IMG_ICON: {
                    if (this.client.user != null) {
                        this.client.doGetImgIcon(mss);
                        break;
                    }}
                case Cmd.REQUEST_BOARDLIST:
                    BoardList(mss);
                    break;
                case Cmd.JOIN_BOARD:
                    joinBoard(mss,this.client.user);
                    break;
                case Cmd.LEAVE_BOARD:
                    leaveBoard(mss,this.client.user);
                    break;
                case Cmd.START:
                    Start(mss);
                    break;
                case Cmd.READY:
                    Ready(mss,this.client.user);
                    break;
                case Cmd.TO_XONG:
                    toXong(mss,this.client.user);
                    break;
                case 65:
                    haPhom(mss,this.client.user);
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
        List<User> BoardUs = board.getLstUsers();


        if(BoardUs.size() > 1)
        {
            for (int i = 0; i < BoardUs.size(); i++) {
                Message ms1 = new Message(Cmd.SOMEONE_JOINBOARD);
                DataOutputStream ds1 = ms1.writer();
                ds1.writeByte(1);//seat // vi tri
                ds1.writeInt(us.getId());//seat // vi tri
                ds1.writeUTF(us.getUsername());//seat // vi tri
                ds1.writeInt(10000);// tien

                ds1.writeByte(us.getWearing().size()); // Số phần mặc
                for (Item item : us.getWearing()) {
                    ds1.writeShort(item.getId()); // ID item
                }
                ds1.writeInt(10000);// tien
                ds1.writeInt(1);// tien
                ds1.flush();
                BoardUs.get(i).session.sendMessage(ms1);
            }
        }


        ms = new Message(Cmd.JOIN_BOARD);
        DataOutputStream ds = ms.writer();


        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeInt(BoardUs.get(0).getId()); // ID user
        ds.writeInt(0); // số tiền


        for (User user : BoardUs) {
            System.out.println("User ID: " + user.getId() + ", HashCode: " + user.hashCode());
            ds.writeInt(user.getId()); // IDDB
            ds.writeUTF(user.getUsername()); // Username
            ds.writeInt(0); // Số tiền
            System.out.println("Đang xử lý người dùng  ID: " + user.getId());
            ds.writeByte(user.getWearing().size()); // Số phần mặc
            for (Item item : user.getWearing()) {
                ds.writeShort(item.getId()); // ID item
            }

            ds.writeInt(10); // Kinh nghiệm
            ds.writeBoolean(false); // Trạng thái sẵn sàng
            ds.writeShort(user.getIdImg()); // ID hình ảnh
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

        BoardInfo board = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board.getLstUsers();

        ms = new Message(Cmd.START);
        DataOutputStream ds = ms.writer();

        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeByte(10); // ID user hoặc ID bàn
        ds.flush();

        for (User user : BoardUs) {
            user.session.sendMessage(ms);
        }
    }
    private void Ready(Message ms,User us) throws IOException {//ms 6
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        Boolean isReady = ms.reader().readBoolean();

        BoardInfo board = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board.getLstUsers();

        ms = new Message(Cmd.READY);
        DataOutputStream ds = ms.writer();

        ds.writeInt(us.getId());
        ds.writeBoolean(isReady);
        ds.flush();

        for(User u : BoardUs)
        {
            u.session.sendMessage(ms);
        }
    }

    private void toXong(Message ms,User us) throws IOException {//ms 6
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();

        BoardInfo board = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board.getLstUsers();

        List<Byte> moneyPutList = new ArrayList<>();

        while (ms.reader().available() > 0) {
            byte moneyPut = ms.reader().readByte();
            moneyPutList.add(moneyPut);
        }

        ms = new Message(Cmd.TO_XONG);
        DataOutputStream ds = ms.writer();
        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeByte(1);
        for (Byte moneyPut : moneyPutList) {
            ds.writeByte(moneyPut);
        }
        ds.flush();
        for (User user : BoardUs) {
            user.getSession().sendMessage(ms);
        }

        Message ms1 = new Message(Cmd.SET_TURN);
        DataOutputStream ds1 = ms1.writer();
        ds1.writeByte(roomID);
        ds1.writeByte(boardID);
        int index = BoardUs.indexOf(us);
        ds1.writeByte(index);
        ds1.flush();
        for (User user : BoardUs) {
            user.getSession().sendMessage(ms1);
        }
    }

    private void haPhom(Message ms,User us) throws IOException {//ms 6
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        byte indexFrom = ms.reader().readByte();
        byte indexTo = ms.reader().readByte();

        BoardInfo board1 = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board1.getLstUsers();

        ms = new Message(Cmd.HA_PHOM);
        DataOutputStream ds = ms.writer();
        ds.writeByte(roomID);
        ds.writeByte(boardID);
        int index = BoardUs.indexOf(us);
        ds.writeByte(1);
        ds.writeByte(4);
        ds.writeByte(indexFrom);
        ds.writeByte(indexTo);
        ds.flush();
        for (User user : BoardUs) {
            user.getSession().sendMessage(ms);
        }
    }

}
