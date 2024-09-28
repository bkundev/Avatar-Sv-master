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
import avatar.server.Utils;
import avatar.service.EffectService;
import avatar.service.FarmService;
import avatar.constants.Cmd;

import static avatar.server.BoardManager.boardList;
import static avatar.server.BoardManager.users;

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
                case 49:
                    Skip(mss,this.client.user);
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
                ds1.writeByte(BoardUs.indexOf(us));//seat // vi tri
                ds1.writeInt(us.getId());//seat // vi tri
                ds1.writeUTF(us.getUsername());//seat // vi tri
                ds1.writeInt(0);// tien

                ds1.writeByte(us.getWearing().size()); // Số phần mặc
                for (Item item : us.getWearing()) {
                    ds1.writeShort(item.getId()); // ID item
                }
                ds1.writeInt(0);// tien
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
        ds.writeInt(0); // số tiền cược ở phòng


        for (User user : BoardUs) {
            ds.writeInt(user.getId()); // IDDB
            ds.writeUTF(user.getUsername()); // Username
            ds.writeInt(56789); // Số tiền của user
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

    private void leaveBoard(Message ms, User us) throws IOException {
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        BoardInfo board = BoardManager.getInstance().find(boardID);
        board.nPlayer--;
        board.getLstUsers().remove(us);
        ms = new Message(Cmd.SOMEONE_LEAVEBOARD);//14
        DataOutputStream ds = ms.writer();
        ds.writeInt(us.getId());
        ds.writeInt(board.getLstUsers().get(0).getId());
        for (User user : board.getLstUsers()) {
            user.getSession().sendMessage(ms);
        }
    }

    private void Ready(Message ms,User us) throws IOException {//ms 20
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        Boolean isReady = ms.reader().readBoolean();

        BoardInfo board = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board.getLstUsers();

        ms = new Message(Cmd.READY);//16
        DataOutputStream ds = ms.writer();

        ds.writeInt(us.getId());
        ds.writeBoolean(isReady);
        ds.flush();

        for(User u : BoardUs)
        {
            u.session.sendMessage(ms);
        }
    }

    private void Start(Message ms) throws IOException {//20
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        BoardInfo board = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board.getLstUsers();
        for(User u : BoardUs){
            List<Byte> moneyPutList = u.getMoneyPutList();
            moneyPutList.clear();
            u.setHaPhom(false);
            u.setToXong(false);
            u.getMoneyPutList().clear();
        }
        BoardUs.get(0).setToXong(true);
        BoardUs.get(0).setHaPhom(true);

        ms = new Message(Cmd.START);//20
        DataOutputStream ds = ms.writer();

        ds.writeByte(roomID);
        ds.writeByte(boardID);
        ds.writeByte(10); // ID user hoặc ID bàn
        ds.flush();

        for (User user : BoardUs) {
            user.session.sendMessage(ms);
        }
    }


    private void toXong(Message ms,User us) throws IOException {//ms 21
        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();

        BoardInfo board = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board.getLstUsers();
        List<Byte> moneyPutList = us.getMoneyPutList();

        //neesu chua co putlist thi them pustlist moi
        if(us.getMoneyPutList().size() <= 0) {
            while (ms.reader().available() > 0) {
                byte moneyPut = ms.reader().readByte();
                moneyPutList.add(moneyPut);
                us.setMoneyPutList(moneyPutList);
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
            us.getSession().sendMessage(ms);
            us.setToXong(true);
            BoardUs.get(0).getSession().sendMessage(ms);
            System.out.println(us.getUsername() + " đã đặt xong ");
        }



        Boolean allToXong = true;
        for (User user : BoardUs) {
            if (!user.isToXong()){
                ms = new Message(Cmd.TO_XONG);
                DataOutputStream ds = ms.writer();
                ds.writeByte(roomID);
                ds.writeByte(boardID);
                ds.writeByte(BoardUs.indexOf(us));
                for (Byte moneyPut : moneyPutList) {
                    ds.writeByte(moneyPut);
                }
                ds.flush();
                user.getSession().sendMessage(ms);
                System.out.println(user.getUsername() + " chua to xong ");
                allToXong = false;
                break;
            }
        }

        if (allToXong) {
            for (User user : BoardUs) {
                ms = new Message(Cmd.TO_XONG);
                DataOutputStream ds = ms.writer();
                ds.writeByte(roomID);
                ds.writeByte(boardID);
                ds.writeByte(BoardUs.indexOf(us));
                for (Byte moneyPut : moneyPutList) {
                    ds.writeByte(moneyPut);
                }
                ds.flush();
                user.getSession().sendMessage(ms);
            }
        }


    }



    private void haPhom(Message ms,User us) throws IOException, InterruptedException {//ms 65

        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        byte indexFrom = ms.reader().readByte();
        byte indexTo = ms.reader().readByte();

        System.out.println(indexFrom);
        System.out.println(indexTo);
        BoardInfo board1 = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board1.getLstUsers();

        ms = new Message(Cmd.HA_PHOM);//65
        DataOutputStream ds = ms.writer();
        ds.writeByte(roomID);
        ds.writeByte(boardID);

        ds.writeByte(BoardUs.indexOf(us));
        ds.writeByte(indexFrom);
        ds.writeByte(indexTo);
        ds.writeByte(6);// list + list from to
        ds.flush();

        System.out.println(us.getUsername()+"đã tả thành công ");
        for (User user : BoardUs) {
            user.getSession().sendMessage(ms);
        }

        Message ms1 = new Message(Cmd.GAME_RESULT);

        DataOutputStream ds1 = ms1.writer();
        ds1.writeByte(roomID);
        ds1.writeByte(boardID);

        for (int i = 0; i < 3; i++) {
            int xn = Utils.nextInt(5);
            ds1.writeByte(xn);
        }

        ds1.flush();
        for (User user : BoardUs) {
            user.getSession().sendMessage(ms1);
        }

        Message ms2 = new Message(Cmd.WIN);
        DataOutputStream ds2 = ms2.writer();
        ds2.writeByte(roomID);
        ds2.writeByte(boardID);
        ds2.writeByte(1);
        ds2.writeByte(0);
        ds2.writeByte(0);//BCBoardScr.me.onSetPlayer(b5, b6, moneyValue);
        ds2.flush();
        for (User user : BoardUs) {
            user.getSession().sendMessage(ms2);
        }


      //  Thread.sleep(2500);
        Message ms3 = new Message(Cmd.FINISH);
        DataOutputStream ds3 = ms3.writer();
        ds3.writeByte(roomID);
        ds3.writeByte(boardID);
        for (int i = 0; i < 5; i++)
        {
            ds3.writeInt(99999);
        }
        ds3.flush();
    }


    private void Skip(Message ms,User us) throws IOException, InterruptedException {//ms 6

        byte roomID = ms.reader().readByte();
        byte boardID = ms.reader().readByte();
        us.getService().serverDialog("skip dang xay dung");
        BoardInfo board1 = BoardManager.getInstance().find(boardID);
        List<User> BoardUs = board1.getLstUsers();


        for (User user : BoardUs) {
            Message ms3 = new Message(Cmd.FINISH);
            DataOutputStream ds3 = ms3.writer();
            ds3.writeByte(roomID);
            ds3.writeByte(boardID);
            for (int i = 0; i < 5; i++)
            {
                ds3.writeInt(0);// tieen xong van
            }
            ds3.flush();
            user.getService().sendMessage(ms3);
        }
    }



    private void setTurn(List<User> lstus,User us,byte roomID,byte boardID,int index) throws IOException, InterruptedException {

        for (User user : lstus) {
            Message ms1 = new Message(Cmd.SET_TURN);
            DataOutputStream ds1 = ms1.writer();
            ds1.writeByte(roomID);
            ds1.writeByte(boardID);
            ds1.writeByte(index);
            ds1.flush();
            user.getSession().sendMessage(ms1);
            us.setHaPhom(true);
        }

    }

}
