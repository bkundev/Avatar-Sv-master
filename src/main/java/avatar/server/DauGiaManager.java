package avatar.server;

import avatar.constants.NpcName;
import avatar.model.Npc;
import avatar.model.User;
import avatar.play.MapManager;
import avatar.play.NpcManager;
import avatar.play.Zone;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DauGiaManager {
    private static DauGiaManager instance; // Singleton instance

    public static Map<Integer, Integer> userBids = new HashMap<>(); // Lưu ID người chơi và số tiền đặt
    private int highestBid = 0; // Giá cao nhất hiện tại
    private User highestBidder; // Người chơi đặt giá cao nhất
    private long endTime; // Thời gian kết thúc phiên đấu giá
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Quản lý thời gian đấu giá// Biến lưu thời gian kết thúc đấu giá


    public long getEndTime() {
        return endTime;
    }
    private DauGiaManager() {
    }

    public static DauGiaManager getInstance() {
        if (instance == null) {
            instance = new DauGiaManager();
        }
        return instance;
    }

    public void addBid(User user, int bidAmount) {
        if (System.currentTimeMillis() > endTime) {
            System.out.println("Phiên đấu giá đã kết thúc.");
            user.getAvatarService().serverDialog("Phiên đấu giá đã kết thúc. Bạn không thể đặt giá.");
            return;
        }

        if (bidAmount <= 0) {
            System.out.println("Số tiền đặt không hợp lệ.");
            user.getAvatarService().serverDialog("Số tiền đặt không hợp lệ. Vui lòng nhập một số lớn hơn 0.");
            return;
        }

        if (bidAmount > user.getXu()) {
            System.out.println("Người chơi không đủ xu để đặt.");
            user.getAvatarService().serverDialog("Bạn không đủ xu để đặt giá.");
            return;
        }

        int userId = user.getId();
        int currentBid = DauGiaManager.userBids.getOrDefault(userId, 0);
        int totalBid = currentBid + bidAmount;
        DauGiaManager.userBids.put(userId, totalBid);

        if (totalBid > highestBid) {
            highestBid = totalBid;
            highestBidder = user;
            user.getAvatarService().serverDialog("Bạn hiện đang là người đấu giá cao nhất với giá " + highestBid);
        } else {
            user.getAvatarService().serverDialog("Giá hiện tại cao hơn giá bạn đặt.");
        }
        // Gọi hàm để hiển thị thông tin đấu giá sau khi thêm bid mới
        updateNpcAuctionInfo();
    }


    public long getTimeRemaining() {
        long timeRemaining = endTime - System.currentTimeMillis();
        return timeRemaining > 0 ? timeRemaining / 1000 : 0; // Trả về giây
    }

    public int getHighestBid() {
        return highestBid;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    // Hàm cập nhật thông tin đấu giá lên NPC
    public void updateNpcAuctionInfo() {
        avatar.play.Map m = MapManager.getInstance().find(9);
        if (m != null) {
            List<Zone> zones = m.getZones();
            for (Zone z : zones) {
                if (z != null) {
                    Npc npc = NpcManager.getInstance().find(z.getMap().getId(), z.getId(), NpcName.DAU_GIA + Npc.ID_ADD);
                    if (npc == null) {
                        continue; // Bỏ qua nếu không tìm thấy NPC
                    }
                    long timeRemaining = getTimeRemaining(); // Lấy thời gian còn lại
                    int highestBid = getHighestBid(); // Lấy giá cao nhất
                    User highestBidder = getHighestBidder(); // Lấy người đấu giá cao nhất

                    npc.setTextChats(List.of(
                            MessageFormat.format("Giá cao nhất hiện tại là {0} bởi người chơi {1}. Thời gian còn lại: {2} giây",
                                    highestBid,
                                    highestBidder != null ? highestBidder.getUsername() : "Chưa có",
                                    timeRemaining)
                    ));
                }
            }
        }
    }

}
