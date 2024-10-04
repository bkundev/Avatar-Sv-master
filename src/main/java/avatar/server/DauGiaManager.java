package avatar.server;

import avatar.model.User;

import java.util.HashMap;
import java.util.Map;

public class DauGiaManager {
    private static DauGiaManager instance; // Singleton instance

    public  static Map<Integer, Integer> userBids = new HashMap<>(); // Lưu ID người chơi và số tiền đặt
    private int highestBid = 0; // Giá cao nhất hiện tại
    private User highestBidder; // Người chơi đặt giá cao nhất

    // Đảm bảo chỉ có một instance của DauGiaManager được tạo
    private DauGiaManager() {}

    public static DauGiaManager getInstance() {
        if (instance == null) {
            instance = new DauGiaManager();
        }
        return instance;
    }

    public void addBid(User user, int bidAmount) {
        int userId = user.getId();
        int currentBid = userBids.getOrDefault(userId, 0);
        int totalBid = currentBid + bidAmount;
        userBids.put(userId, totalBid);

        if (totalBid > highestBid) {
            highestBid = totalBid;
            highestBidder = user;
            System.out.println("Người chơi " + highestBidder.getUsername() + " hiện đang đặt giá cao nhất với " + highestBid);
        }
    }

    public int getHighestBid() {
        return highestBid;
    }

    public User getHighestBidder() {
        return highestBidder;
    }
}
