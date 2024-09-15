package avatar.model;

import avatar.db.DbManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GiftCode {


    public void loadItemImageData() {
        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM `avatar_img_data`;");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("item_id");
                int bigImageID = rs.getInt("image_id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public boolean isValidGiftCode(String code) throws SQLException {
        String sql = "SELECT * FROM giftcode WHERE code = ? AND start_time <= NOW() AND end_time >= NOW() AND num > 0";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next(); // trả về true nếu mã hợp lệ;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }
}