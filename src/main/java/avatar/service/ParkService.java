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
            ds.writeUTF("");// Ghi kết quả thành công hoặc thất bại
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
    public void onStatus() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.STATUS_FISH);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeByte(3);
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
            int idFish2 = 457;
            ds.writeShort(idFish2); // gửi giá trị idFish2
            short timeDelay = 3000; // giá trị ví dụ
            ds.writeShort(timeDelay); // gửi giá trị timeDelay
            byte b2 = 2; // giá trị ví dụ
            ds.writeByte(b2); // gửi giá trị b2, số lượng mảng con

// Tạo mảng 2 chiều array2 và gửi từng phần tử
            byte[][] array2 = new byte[b2][];

// Tạo và điền dữ liệu cho các mảng con trong array2
            for (int j = 0; j < b2; j++) {
                short num3 = 4; // giá trị ví dụ cho kích thước mảng con
                array2[j] = new byte[num3];

                // Điền dữ liệu vào mảng con
                for (int k = 0; k < num3; k++) {
                    array2[j][k] = (byte) (k + 1); // giá trị ví dụ
                }
                ds.writeShort(num3);
                ds.write(array2[j]);
            }
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
            ds.writeShort(457);
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

    public void doFinishFishing() {
        try {
            int userID = this.session.user.getId();
            Message ms = new Message(Cmd.CAU_THANH_CONG);
            DataOutputStream ds = ms.writer();
            boolean isF = true;
            byte[] index = {10, 20, 30}; // Dữ liệu mẫu cho index

            // Ghi dữ liệu vào DataOutputStream
            ds.writeBoolean(isF);
            ds.writeByte((byte) index.length); // Sử dụng byte thay vì sbyte

            for (byte b : index) {
                ds.writeByte(b);
            }
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException ex) {
            logger.error("handleStartFishing() ", ex);
        }
    }


}
