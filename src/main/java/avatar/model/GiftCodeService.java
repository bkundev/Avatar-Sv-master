package avatar.model;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import avatar.item.Item;
import avatar.server.UserManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import avatar.db.DbManager;
public class GiftCodeService {

    public GiftCodeService() {
        loadGiftCodes(); // Tải mã quà tặng khi khởi tạo lớp
    }


    private Map<String, GiftCode> giftCodes = new HashMap<>();


    public void loadGiftCodes() {
        String sql = "SELECT * FROM giftcode";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("code");
                GiftCode giftCode = new GiftCode(
                        rs.getInt("id"),
                        code,
                        rs.getString("message"),
                        rs.getString("data"),
                        rs.getTimestamp("start_time"),
                        rs.getTimestamp("end_time"),
                        rs.getInt("num"),
                        rs.getInt("create_by"),
                        rs.getTimestamp("create_time")
                );
                giftCodes.put(code, giftCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức kiểm tra tính hợp lệ của mã quà tặng
    public boolean isValidGiftCode(String code) {
        GiftCode giftCode = giftCodes.get(code);
        if (giftCode == null) {
            return false; // Mã không tồn tại
        }
        long now = System.currentTimeMillis();
        return giftCode.startTime.getTime() <= now && giftCode.endTime.getTime() >= now && giftCode.num > 0;
    }

    // Cập nhật số lượng mã quà tặng trong cơ sở dữ liệu
    private void updateGiftCodeInDatabase(GiftCode giftCode) throws SQLException {
        String sql = "UPDATE giftcode SET num = ? WHERE code = ?";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, giftCode.num);
            ps.setString(2, giftCode.code);
            ps.executeUpdate();
        }
    }

    // Ghi nhận việc sử dụng mã quà tặng vào bảng giftcode_use
    private void recordGiftCodeUsage(int userId, int giftCodeId) throws SQLException {
        // Kiểm tra xem người dùng đã sử dụng mã quà tặng này chưa
        String checkSql = "SELECT COUNT(*) FROM giftcode_use WHERE user = ? AND giftcode_id = ?";
        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, userId);
            checkPs.setInt(2, giftCodeId);
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // Nếu đã dùng, không thực hiện chèn và có thể thông báo lỗi
                UserManager.getInstance().find(userId).getAvatarService().serverDialog("Bạn đã dùng mã quà tặng đã được sử dụng trước đó");
            }

            // Nếu chưa tồn tại, chèn bản ghi mới
            String insertSql = "INSERT INTO giftcode_use (user, giftcode_id) VALUES (?, ?)";
            try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                insertPs.setInt(1, userId);
                insertPs.setInt(2, giftCodeId);
                insertPs.executeUpdate();
            }
        }
    }



    // Sử dụng mã quà tặng
    public boolean useGiftCode(int userId, String code) {
        GiftCode giftCode = giftCodes.get(code);
        if (giftCode == null) {
            return false; // Mã không tồn tại
        }

        long now = System.currentTimeMillis();
        if (giftCode.startTime.getTime() <= now && giftCode.endTime.getTime() >= now && giftCode.num > 0) {
            // Giảm số lượng mã quà tặng trong bộ nhớ
            giftCode.num -= 1;

            try {
                // Cập nhật cơ sở dữ liệu
                updateGiftCodeInDatabase(giftCode);

                // Ghi nhận việc sử dụng mã quà tặng
                recordGiftCodeUsage(userId, giftCode.id);

                // Phân phối quà tương ứng
                distributeGift(userId, giftCode);

            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Xử lý lỗi nếu không thể cập nhật cơ sở dữ liệu
            }

            return true;
        }
        return false; // Mã không hợp lệ hoặc hết số lượng
    }


    private void distributeGift(int userId, GiftCode giftCode) {

        String MaCode = giftCode.code;


        switch (MaCode) {
            case "denbu":

                Item hopqua = new Item(683,-1,100);
                //hopqua.setExpired(System.currentTimeMillis() + (86400000L * time));
                User us = UserManager.getInstance().find(userId);
                if(us.findItemInChests(683) !=null){
                    int quantity = us.findItemInChests(683).getQuantity();
                    us.findItemInChests(683).setQuantity(quantity+1);
                }else {
                    us.addItemToChests(hopqua);
                }
                Item itemqs = new Item(593, -1, 200);
                us.addItemToChests(itemqs);
                us.getAvatarService().serverDialog("denbu bạn nhận được 100 hộp quà, và 100 thẻ quay số");
//                String[] data = giftCode.data.split(":");// Ví dụ data = "itemId:quantity"
//                int itemId = Integer.parseInt(data[0]);
//                int quantity = Integer.parseInt(data[1]);
//                Item useGift = new Item(itemId,-1,100);
//                UserManager.getInstance().find(userId).addItemToChests(useGift);
                break;
            default:
                System.out.println("Loại quà tặng không hợp lệ.");
                // Có thể thêm thông báo cho người chơi hoặc ghi log lỗi
        }
    }


    private static class GiftCode {
        private int id;
        private String code;
        private String message;
        private String data;
        private java.sql.Timestamp startTime;
        private java.sql.Timestamp endTime;
        private int num;
        private int createBy;
        private java.sql.Timestamp createTime;

        public GiftCode(int id, String code, String message, String data, java.sql.Timestamp startTime,
                        java.sql.Timestamp endTime, int num, int createBy, java.sql.Timestamp createTime) {
            this.id = id;
            this.code = code;
            this.message = message;
            this.data = data;
            this.startTime = startTime;
            this.endTime = endTime;
            this.num = num;
            this.createBy = createBy;
            this.createTime = createTime;
        }

        // Getters and setters
    }
}
