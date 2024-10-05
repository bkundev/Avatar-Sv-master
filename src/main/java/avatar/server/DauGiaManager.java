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



    public  class AuctionScheduler {
        private Timer timer;

        public void startScheduling() {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Calendar now = Calendar.getInstance();
                    System.out.println(now.get(Calendar.DAY_OF_WEEK));
                    if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && now.get(Calendar.HOUR_OF_DAY) == 20) {
                        startAuction();
                    }
                }
            }, 0, 24 * 60 * 60 * 1000); // Chạy kiểm tra mỗi ngày một lần
        }
    }


    public void startAuction() {
        // ...
        long duration = 2 * 60 * 1000;
        endTime = System.currentTimeMillis() + duration;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateNpcAuctionInfo();
            }
        }, 0, 1000);
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
                    long timeRemaining = getTimeRemaining(); // Lấy thời gian còn lại
                    int highestBid = getHighestBid(); // Lấy giá cao nhất
                    User highestBidder = getHighestBidder(); // Lấy người đấu giá cao nhất
                    // Kiểm tra nếu thời gian còn lại đã hết
                    if (timeRemaining <= 0) {
                        // Kết thúc đấu giá và trao giải cho người có giá cao nhất
                        endAuction();
                        return;
                    }

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
    public void endAuction() {
        // Lấy người đấu giá cao nhất
        User highestBidder = getHighestBidder();
        int highestBid = getHighestBid();

        if (highestBidder != null) {
            // Trao giải cho người có giá thầu cao nhất
            System.out.println("Đấu giá đã kết thúc. Người chơi " + highestBidder.getUsername() + " đã thắng với giá " + highestBid);
            // Thêm logic trao phần thưởng ở đây
        } else {
            System.out.println("Đấu giá đã kết thúc nhưng không có người tham gia.");
        }

        // Hủy bộ đếm để ngừng cập nhật
        if (timer != null) {
            timer.cancel();
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

    public String getTimeToNextAuction() {
        LocalDateTime now = LocalDateTime.now();
        // Tìm ngày Chủ nhật tới
        LocalDateTime nextSunday = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).withHour(20).withMinute(0);

        // Tính toán thời gian còn lại
        long timeRemaining = Duration.between(now, nextSunday).toMillis();

        if (timeRemaining > 0) {
            return formatDuration(timeRemaining);
        } else {
            // Nếu đã qua Chủ nhật 8h tối, tính cho Chủ nhật tuần sau
            nextSunday = nextSunday.plusWeeks(1);
            return formatDuration(Duration.between(now, nextSunday).toMillis());
        }
    }

    private String formatDuration(long milliseconds) {
        Duration duration = Duration.ofMillis(milliseconds);
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        return String.format("%d Ngày %d giờ %d phút", days, hours, minutes);
    }
}
