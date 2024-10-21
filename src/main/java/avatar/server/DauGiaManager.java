package avatar.server;

import avatar.constants.NpcName;
import avatar.item.Item;
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
    private int previousHighestBid = 0; // Biến lưu trữ giá đặt trước đó
    private User highestBidder; // Người chơi đặt giá cao nhất
    private long endTime; // Thời gian kết thúc phiên đấu giá
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Quản lý thời gian đấu giá// Biến lưu thời gian kết thúc đấu giá
    private int auctionCurrency; // 0 là xu, 1 là lượng
    private Item auctionItem;

    private List<Npc> dauGia = new ArrayList<>();
    private List<Npc> NhanViendauGia = new ArrayList<>();

    private DauGiaManager() {
    }
    public void setHighestBidder(User user) {
        this.highestBidder = user;
    }
    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }
    public void setTimeRemaining(long timeInSeconds) {
        synchronized (this) { // Bảo vệ truy cập đồng thời
            if (timeInSeconds > 0) {
                // Thiết lập lại endTime dựa trên thời gian hiện tại và thời gian còn lại
                this.endTime = System.currentTimeMillis() + (timeInSeconds * 1000);
            } else {
                // Nếu timeInSeconds <= 0, có thể kết thúc phiên đấu giá ngay lập tức
                endAuction(); // Gọi hàm kết thúc phiên đấu giá nếu cần
            }
        }
    }
    public void setPreviousHighestBid(int previousBid) {
        this.previousHighestBid = previousBid;
    }

    public String getauctionCurrency (){
        return  this.auctionCurrency == 0 ? "xu" : "lượng";
    }
    public int getAuctionCurrency() {
        return auctionCurrency;
    }
    public Item getAuctionItem() {
        return auctionItem;
    }

    public List<Npc> getDauGia() {
        // Trả về một bản sao để tránh bị sửa đổi trực tiếp
        return new ArrayList<>(dauGia);
    }

    public long getEndTime() {
        return endTime;
    }
    public int getHighestBid() {
        return highestBid;
    }
    public User getHighestBidder() {
        return highestBidder;
    }
    public Item getauctionItem() {
        return auctionItem;
    }

    public int getPreviousHighestBid() {
        return previousHighestBid;
    }
    public long getTimeRemaining() {
        synchronized (this) { // Bảo vệ việc truy cập endTime
            long timeRemaining = endTime - System.currentTimeMillis();
            return timeRemaining > 0 ? timeRemaining / 1000 : 0;

        }
    }



    public void setDauGia(Npc dauGia) {
        if (dauGia != null) {
            if(this.dauGia.size() > 2) {return;}
            this.dauGia.add(dauGia);
        }
    }
    public void setNhanVienDauGia(Npc nhanViendauGia) {
        if (dauGia != null) {
            if(this.NhanViendauGia.size() > 2) {return;}
            this.NhanViendauGia.add(nhanViendauGia);
        }
    }

    public void startAuction(int currencyType, Item item) {
        this.auctionCurrency = currencyType; // Gán loại tiền cho phiên đấu giá
        this.auctionItem = item; // Gán vật phẩm cho phiên đấu giá
        long duration = 5 * 60 * 1000;
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



    public void updateNpcAuctionInfo() {
        long timeRemaining = getTimeRemaining(); // Lấy thời gian còn lại
        int highestBid = getHighestBid(); // Lấy giá cao nhất
        User highestBidder = getHighestBidder(); // Lấy người đấu giá cao nhất
        String currency = auctionCurrency == 0 ? "xu" : "lượng";

        System.out.println("Time remaining: " + timeRemaining);

//        if (timeRemaining <= 30 && highestBidder != null && highestBid > getPreviousHighestBid()) {
//            System.out.println("Resetting time to 30 seconds");
//            setTimeRemaining(30); // Đặt lại thời gian còn lại thành 30 giây
        if (timeRemaining < 30) {
            for (Npc npc : getInstance().NhanViendauGia) {
                npc.setTextChats(List.of(
                        MessageFormat.format("Có người vừa trả giá {0} {1} cho vật phẩm {2} . Thời gian còn lại: {3} giây",
                                highestBid,
                                currency,
                                this.auctionItem.getPart().getName(),
                                getFormattedTimeRemaining()
                        )
                ));
            }
        }
//        }

        // Cập nhật giá trị trước khi hiển thị
 //       setPreviousHighestBid(highestBid); // Cập nhật giá đặt trước đó
        if (timeRemaining < 0){
            for (Npc npc : getInstance().NhanViendauGia) {
                npc.setTextChats(List.of(
                        MessageFormat.format("Có người vừa trả giá {0} {1} cho vật phẩm {2}  . Thời gian còn lại: {3} end",
                                highestBid,
                                currency,
                                this.auctionItem.getPart().getName(),
                                getFormattedTimeRemaining()
                        )
                ));
            }
        }
        for (Npc npc : dauGia) {
            if (timeRemaining <= 0) {
                if (timer != null) {
                    timer.cancel();
                }
                endAuction();
                return;
            }
            npc.setTextChats(List.of(
                    MessageFormat.format("Loại đấu giá: {0}. Vật phẩm: {1}. Giá cao nhất hiện tại là {2} . Thời gian còn lại: {3} giây",
                            currency,
                            this.auctionItem.getPart().getName(), // Tên vật phẩm
                            highestBid,
                            getFormattedTimeRemaining()
                    )
            ));
            System.out.println(MessageFormat.format(
                    "Loại đấu giá: {0}. Vật phẩm: {1}. Giá cao nhất hiện tại là {2} . Thời gian còn lại: {3} giây",
                    currency,
                    auctionItem.getPart().getName(),
                    highestBid,
                    getFormattedTimeRemaining()
            ));
        }


    }

    public void endAuction() {

        User highestBidder = getHighestBidder();
        int highestBid = getHighestBid();
        String currency = auctionCurrency == 0 ? "xu" : "lượng";
        if (highestBidder != null) {
            for (Npc npc : dauGia) {
                npc.setTextChats(List.of(
                        MessageFormat.format("Chúc Mừng {0} đã chiến thắng phiên đấu giá với {1} {2}. Vật phẩm : {3}",
                                highestBidder.getUsername(),
                                highestBid,
                                currency,
                                auctionItem.getPart().getName()
                        )
                ));
            }
            UserManager.users.forEach(user -> {
                user.getAvatarService().serverInfo("Chúc mừng bạn "+ highestBidder.getUsername() +" đã chiến thắng đấu giá " + currency + " vật phẩm : " + auctionItem.getPart().getName()  +
                        " với giá " + highestBid + " " + currency +" . mọi người đều ngưỡng mộ !");
            });
            highestBidder.addItemToChests(this.auctionItem);

        } else {
            System.out.println("Đấu giá đã kết thúc nhưng không có người tham gia.");
        }

        synchronized (userBids) { // Đảm bảo đồng bộ khi nhiều người cùng hoàn tiền
            for (Map.Entry<Integer, Integer> entry : userBids.entrySet()) {
                int userId = entry.getKey();
                int bidAmount = entry.getValue();

                // Kiểm tra xem người chơi có phải là người chiến thắng không
                if (highestBidder != null && userId == highestBidder.getId()) {
                    continue; //
                }

                User user = UserManager.getInstance().find(userId);
                if (user != null) {
                    // Hoàn lại 90% số tiền cược
                    int refundAmount = (int) (bidAmount * 0.9);

                    if (auctionCurrency == 0) {
                        // Hoàn lại xu
                        user.updateXu(+refundAmount);
                    } else {
                        // Hoàn lại lượng
                        user.updateLuong(+refundAmount);
                    }
                    user.getAvatarService().serverInfo("Bạn đã được hoàn lại " + refundAmount + " " + currency + " (90 phần trăm) từ phiên đấu giá.");
                }
            }
        }
        this.userBids.clear();
        this.setHighestBid(0);
        this.setHighestBidder(null);
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

        // Lấy thời gian của Chủ nhật hiện tại lúc 20:00
        LocalDateTime nextSunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).withHour(21).withMinute(0).withSecond(0).withNano(0);

        // Nếu thời gian hiện tại đã qua 8h tối Chủ nhật, tính cho tuần sau
        if (now.isAfter(nextSunday)) {
            nextSunday = nextSunday.plusWeeks(1);
        }

        // Tính toán thời gian còn lại
        long timeRemaining = Duration.between(now, nextSunday).toMillis();

        return formatDuration(timeRemaining);
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
