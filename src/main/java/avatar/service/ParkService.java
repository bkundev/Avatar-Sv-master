package avatar.service;

import avatar.constants.Cmd;
import avatar.item.Item;
import avatar.model.Fish;
import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;
import avatar.server.UserManager;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public class ParkService extends Service {
    private User us;
    short time;
    private static final Logger logger = Logger.getLogger(AvatarService.class);
    private static final Fish a = new Fish();
    public ParkService(Session cl) {
        super(cl);
    }

    public void handleAddFriendRequest(Message message) {
        try {
            // Đọc ID của người nhận từ thông điệp
            int receiverId = message.reader().readInt();
            int senderId = this.session.user.getId(); // ID của người gửi yêu cầu
            // Tìm kiếm người gửi và người nhận
            User sender = UserManager.getInstance().find(senderId);
            User receiver = UserManager.getInstance().find(receiverId);
            if (sender != null && receiver != null) {
                // Tạo thông báo lời mời kết bạn cho người nhận
                Message friendRequestMessage = new Message(Cmd.ADD_FRIEND);
                DataOutputStream dos = friendRequestMessage.writer();
                dos.writeInt(senderId); // Gửi ID của người gửi
                receiver.getAvatarService().chatTo(sender.getUsername(), ":gui kb 1",1);
                dos.writeUTF(sender.getUsername());  // Tên người gửi
                dos.flush();
                sendMessage(message);
                // Xác nhận gửi lời mời kết bạn đến người gửi
            } else {
                // Xử lý trường hợp người dùng không tồn tạ
                this.session.user.getAvatarService().serverDialog("kb");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleStartFishing(Message ms) {
        try {
            Message response = new Message(Cmd.START_CAU_CA);
            DataOutputStream ds = response.writer();

            Item item = this.session.user.findItemInChests(446);//check cần
            if (item != null) {
                boolean isSuccess = false;
                ds.writeBoolean(isSuccess);
                ds.writeUTF("Bạn phải có cần kâu");
                ds.flush();
                this.sendMessage(response);
            }else if(1==1)
            {
                boolean isSuccess = false;
                ds.writeBoolean(isSuccess);
                ds.writeUTF("content");
                ds.flush();
                this.sendMessage(response);
                Item moi = this.session.user.findItemInChests(4875);//check cần//mồi câu cá trâm
            }
            else
            {
                boolean isSuccess = true;
                ds.writeBoolean(isSuccess);
                ds.writeUTF("");
                ds.flush();
                this.sendMessage(response);
            }

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }

    public void handleQuangCau(Message ms) {
        try {
            int userID = this.session.user.getId();
            Message response = new Message(Cmd.QUANG_CAU);
            DataOutputStream ds = response.writer();
            ds.writeInt(userID);
            ds.flush();
            this.sendMessage(response);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }
    public void onStatusFish() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.STATUS_FISH);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeByte(1);//ca can cau
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }

    public void onCanCau() {
        try {
            Thread.sleep(7000);
            us = UserManager.getInstance().find(this.session.user.getId());
            short idFish = (short) a.getRandomFishID();
            us.setIdFish(idFish);
            time = 3000;
            if(idFish<0)
            {
                time = -1;
                us.setIdFish(idFish);
            }
            Random random = new Random();
            Message ms = new Message(Cmd.CAN_CAU);
            DataOutputStream ds = ms.writer();
            ds.writeInt(us.getId());
            ds.writeShort(us.getIdFish());
            ds.writeShort(time);
            int randomNumber = random.nextInt((12 - 6) + 1) + 4;
            ds.writeByte((byte) randomNumber);
            for (int i = 0; i < randomNumber; i++) {
                int randomIndex = random.nextInt(a.images.length);
                byte[] randomImage = a.images[randomIndex];
                ds.writeShort(randomImage.length);
                ds.write(randomImage);
            }
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void CauThanhCong() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.CAU_THANH_CONG);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeShort(this.session.user.getIdFish());
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }

    public void onInfoFish() {
        try {
            Message ms = new Message(Cmd.INFO_FISH);
            DataOutputStream ds = ms.writer();
            ds.writeInt(us.getId());
            ds.writeByte(1);
            ds.writeByte(1);
            ds.writeInt(1);
            ds.writeShort(457);
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }
    public void CauCaXong() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.CAU_CA_XONG);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeInt(this.session.user.getIdFish());
            ds.flush();
            this.sendMessage(ms);
        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }



}
