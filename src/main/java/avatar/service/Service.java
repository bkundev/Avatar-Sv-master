package avatar.service;

import avatar.constants.Cmd;
import avatar.db.DbManager;
import avatar.model.Food;
import avatar.model.User;
import avatar.network.Message;
import avatar.network.Session;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Service {

    private static final Logger logger = Logger.getLogger(Service.class);
    protected Session session;

    public Service(Session cl) {
        this.session = cl;
    }

    public void removeItem(int userID, short itemID) {
        try {
            Message ms = new Message(Cmd.REMOVE_ITEM);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userID);
            ds.writeShort(itemID);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("removeItem() ", ex);
        }


    }

    public void serverDialog(String message) {
        try {
            Message ms = new Message(Cmd.SET_MONEY_ERROR);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(message);
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTextBoxPopup(int userId, int menuId, String message, int type) {
        try {
            Message ms = new Message(Cmd.TEXT_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(userId);
            ds.writeByte(menuId);
            ds.writeUTF(message);
            ds.writeByte(type);
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serverMessage(String message) {
        try {
            Message ms = new Message(Cmd.SERVER_MESSAGE);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(message);
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            logger.error("serverMessage ", e);
        }
    }

    public void serverInfo(String message) {
        try {
            Message ms = new Message(Cmd.SERVER_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(message);
            ds.flush();
            sendMessage(ms);
        } catch (IOException e) {
            logger.error("serverMessage ", e);
        }
    }

    public void weather(byte weather) {
        try {
            System.out.println("weather: " + weather);
            Message ms = new Message(Cmd.WEATHER);
            DataOutputStream ds = ms.writer();
            ds.writeByte(weather);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            logger.error("weather() ", ex);
        }
    }

    public List<User> getTop10PlayersByXuFromBoss() {
        List<User> topPlayers = new ArrayList<>();
        String sql = "SELECT u.username, p.xu_from_boss " +
                "FROM players p " +
                "JOIN users u ON p.user_id = u.id " +
                "ORDER BY p.xu_from_boss DESC " +
                "LIMIT 10";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                int xuFromBoss = rs.getInt("xu_from_boss");

                // In ra giá trị đọc từ ResultSet để kiểm tra
                System.out.println("Username: " + username + ", Xu From Boss: " + xuFromBoss);

                User player = new User(username, xuFromBoss);
                topPlayers.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ khi truy vấn thất bại
        }
        return topPlayers;
    }
    public List<User> getTopPhaoLuong() {
        List<User> topPlayers = new ArrayList<>();
        String sql = "SELECT u.username, p.TopPhaoLuong " +
                "FROM players p " +
                "JOIN users u ON p.user_id = u.id " +
                "ORDER BY p.TopPhaoLuong DESC " +
                "LIMIT 10";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                int TopPhaoLuong  = rs.getInt("TopPhaoLuong");

                // In ra giá trị đọc từ ResultSet để kiểm tra
                System.out.println("Username: " + username + ", phao luong " + TopPhaoLuong);

                User player = new User(username,0,TopPhaoLuong );
                topPlayers.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ khi truy vấn thất bại
        }
        return topPlayers;
    }

    public List<User> getAllPlayersByPhaoLuong() {
        List<User> allPlayers = new ArrayList<>();
        String sql = "SELECT u.username, p.TopPhaoLuong " +
                "FROM players p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.TopPhaoLuong > 0 " + // Chỉ lấy những người có TopPhaoLuong > 0
                "ORDER BY p.TopPhaoLuong DESC";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                int topPhaoLuong = rs.getInt("TopPhaoLuong");

                // Tạo đối tượng User từ dữ liệu truy vấn và thêm vào danh sách
                User player = new User(username, 0, topPhaoLuong);
                allPlayers.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ khi truy vấn thất bại
        }
        return allPlayers;
    }

    public List<User> getAllPlayersByxu_fromboss() {
        List<User> allPlayers = new ArrayList<>();
        String sql = "SELECT u.username, p.xu_from_boss " +
                "FROM players p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.xu_from_boss > 0 " + // Chỉ lấy những người có TopPhaoLuong > 0
                "ORDER BY p.xu_from_boss DESC";

        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                int xu_from_boss = rs.getInt("xu_from_boss");

                User player = new User(username, xu_from_boss);
                allPlayers.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ khi truy vấn thất bại
        }
        return allPlayers;
    }

    public int getUserRankPhaoLuong(User currentUser) {
        List<User> allPlayers = getAllPlayersByPhaoLuong();

        // Đảm bảo currentUser có giá trị TopPhaoLuong hợp lệ
        if (currentUser.getTopPhaoLuong() <= 0) {
            return -1; // Chỉ ra rằng người dùng không có mặt trong danh sách
        }

        int rank = 1;
        for (User player : allPlayers) {
            if (currentUser.getTopPhaoLuong() >= player.getTopPhaoLuong()) {
                return rank; // Trả về vị trí nếu giá trị của người dùng lớn hơn hoặc bằng
            }
            rank++;
        }

        return -1; // Nếu người dùng không có mặt trong danh sách
    }

    public int getUserRankXuBoss(User currentUser) {
        List<User> allPlayers = getAllPlayersByxu_fromboss();

        // Đảm bảo currentUser có giá trị TopPhaoLuong hợp lệ
        if (currentUser.getXu_from_boss() <= 0) {
            return -1; // Chỉ ra rằng người dùng không có mặt trong danh sách
        }

        int rank = 1;
        for (User player : allPlayers) {
            if (currentUser.getXu_from_boss() >= player.getXu_from_boss()) {
                return rank; // Trả về vị trí nếu giá trị của người dùng lớn hơn hoặc bằng
            }
            rank++;
        }

        return -1; // Nếu người dùng không có mặt trong danh sách
    }

    public void sendMessage(Message ms) {
        session.sendMessage(ms);
    }
}
