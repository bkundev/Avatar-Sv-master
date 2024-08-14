package avatar.service;

import avatar.constants.Cmd;
import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;
import avatar.server.UserManager;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;

public class ParkService extends Service {
    
    private static final Logger logger = Logger.getLogger(AvatarService.class);

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
            boolean isSuccess = true;

            Message response = new Message(Cmd.START_CAU_CA);
            DataOutputStream ds = response.writer();
            ds.writeBoolean(isSuccess);
            ds.writeUTF("content");
            ds.flush();
            this.sendMessage(response);

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
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.CAN_CAU);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            int idFish2 = 456;
            ds.writeShort(idFish2);
            short timeDelay = 3000;
            ds.writeShort(timeDelay);
            byte[] dataArray = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 24, 0, 0, 0, 24, 8, 2, 0, 0, 0, 111, 21, -86, -81, 0, 0, 0, 127, 73, 68, 65, 84, 120, -38, -51, -43, 75, 10, -64, 48, 8, 4, 80, -17, 127, -65, -84, 122, -110, -84, 44, -76, 32, 18, -99, 20, 63, -112, 14, -39, -10, -95, -110, 88, 34, -112, 105, 66, -71, 52, 64, -17, 103, 23, -47, 114, -62, -100, -122, -92, -100, 12, 87, -126, -20, 68, 22, 104, 105, -42, -79, -48, 68, 44, -12, 81, 90, 63, 52, 35, 113, 122, 60, 12, 57, 61, -90, -95, -15, -28, 55, 16, 51, -61, -42, 6, 8, 42, 4, 14, -69, 13, -110, -93, -53, -42, -112, 37, 2, 55, 59, 0, -19, 31, -83, 64, -102, -16, 31, -19, 126, -115, -56, -92, -86, -5, -88, 13, 42, -83, -38, -61, 127, -111, 27, 125, 42, -128, -56, 68, -118, 24, 45, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
            byte[] dataArray1 = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 24, 0, 0, 0, 24, 8, 2, 0, 0, 0, 111, 21, -86, -81, 0, 0, 0, 127, 73, 68, 65, 84, 120, -38, -51, -43, 75, 10, -64, 48, 8, 4, 80, -17, 127, -65, -84, 122, -110, -84, 44, -76, 32, 18, -99, 20, 63, -112, 14, -39, -10, -95, -110, 88, 34, -112, 105, 66, -71, 52, 64, -17, 103, 23, -47, 114, -62, -100, -122, -92, -100, 12, 87, -126, -20, 68, 22, 104, 105, -42, -79, -48, 68, 44, -12, 81, 90, 63, 52, 35, 113, 122, 60, 12, 57, 61, -90, -95, -15, -28, 55, 16, 51, -61, -42, 6, 8, 42, 4, 14, -69, 13, -110, -93, -53, -42, -112, 37, 2, 55, 59, 0, -19, 31, -83, 64, -102, -16, 31, -19, 126, -115, -56, -92, -86, -5, -88, 13, 42, -83, -38, -61, 127, -111, 27, 125, 42, -128, -56, 68, -118, 24, 45, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };

            ds.writeByte((byte) 2);
            ds.writeShort(dataArray.length);
            ds.write(dataArray);
            ds.writeShort(dataArray1.length);
            ds.write(dataArray1);
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }
    public void CauThanhCong() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.CAU_THANH_CONG);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeShort(456);
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }

    public void onInfoFish() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.INFO_FISH);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
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
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }



}
