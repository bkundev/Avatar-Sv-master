package avatar.server;

import avatar.constants.NpcName;
import avatar.model.Npc;
import avatar.model.User;
import avatar.play.MapManager;
import avatar.play.NpcManager;
import avatar.play.Zone;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DauGiaManager {
    private static DauGiaManager instance; // Singleton instance
    private Timer timer;
    public static Map<Integer, Integer> userBids = new HashMap<>(); // Lưu ID người chơi và số tiền đặt
    private int highestBid = 0; // Giá cao nhất hiện tại
    private User highestBidder; // Người chơi đặt giá cao nhất
    private long endTime; // Thời gian kết thúc phiên đấu giá
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Quản lý thời gian đấu giá// Biến lưu thời gian kết thúc đấu giá
    private long duration = 30 * 60 * 1000;//30p cho1 phiên


    public long getEndTime() {
        return endTime;
    }
    private DauGiaManager() {
    }
    public void setHighestBidder(User user) {
        this.highestBidder = user;
    }
    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }



    public void startAuction() {
        // ...
        long duration = 2 * 60 * 1000; // 30 phút tính bằng millisecond
        endTime = System.currentTimeMillis() + duration;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                updateNpcAuctionInfo();
            }
        }, 0, 1000); // Cập nhật mỗi giây
    }
    public static DauGiaManager getInstance() {
        if (instance == null) {
            instance = new DauGiaManager();
        }
        return instance;
    }


    public long getTimeRemaining() {
        synchronized (this) { // Bảo vệ việc truy cập endTime
            long timeRemaining = endTime - System.currentTimeMillis();
            return timeRemaining > 0 ? timeRemaining / 1000 : 0;
        }
    }

    public int getHighestBid() {
        return highestBid;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

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
                    int highestBid = getHighestBid(); // Lấy giá cao nhất
                    User highestBidder = getHighestBidder(); // Lấy người đấu giá cao nhất

                    npc.setTextChats(List.of(
                            MessageFormat.format("Giá cao nhất hiện tại là {0} bởi người chơi {1}. Thời gian còn lại: {2} giây",
                                    highestBid,
                                    highestBidder != null ? highestBidder.getUsername() : "Chưa có",
                                    getFormattedTimeRemaining())
                    ));
                    System.out.println(MessageFormat.format("Giá cao nhất hiện tại là {0} bởi người chơi {1}. Thời gian còn lại: {2} giây",
                            highestBid,
                            highestBidder != null ? highestBidder.getUsername() : "Chưa có",
                            getFormattedTimeRemaining()));
                }
            }
        }
    }


    public String getFormattedTimeRemaining() {
        long timeRemaining = endTime - System.currentTimeMillis();
        if (timeRemaining > 0) {
            Date date = new Date(timeRemaining);
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            return sdf.format(date);
        } else {
            return "00:00"; // Hoặc xử lý trường hợp phiên đấu giá đã kết thúc
        }
    }
}
