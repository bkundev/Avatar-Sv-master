package avatar.service;

import avatar.item.Item;
import avatar.server.DauGiaManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionScheduler {
    private Timer dailyTimer = new Timer();
    private Timer minuteTimer = new Timer();
    public void startScheduling() {
        dailyTimer = new Timer();
        dailyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();

                // Lấy thời gian Chủ nhật tới lúc 20:00
                Calendar nextSunday = getNextSundayAt8PM();
                long timeUntilAuction = nextSunday.getTimeInMillis() - now.getTimeInMillis();

                // Nếu còn hơn 1 ngày thì kiểm tra mỗi ngày
                if (timeUntilAuction > 24 * 60 * 60 * 1000) {
                    long daysLeft = timeUntilAuction / (24 * 60 * 60 * 1000);
                    System.out.println("Còn " + daysLeft + " ngày đến phiên đấu giá.");

                } else {
                    // Nếu còn ít hơn 1 ngày, bắt đầu kiểm tra từng phút
                    dailyTimer.cancel(); // Dừng kiểm tra mỗi ngày
                    startMinuteChecking(); // Bắt đầu kiểm tra từng phút
                }
            }
        }, 0, 24 * 60 * 60 * 1000); // Kiểm tra mỗi ngày
    }


    private void startMinuteChecking() {
        minuteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();
                Calendar nextSunday = getNextSundayAt8PM();
                long timeUntilAuction = nextSunday.getTimeInMillis() - now.getTimeInMillis();
                long secondsLeft = (timeUntilAuction / 1000) % 60;
                if (secondsLeft <= 0) {
                    // Khi đến thời gian đấu giá, bắt đầu phiên đấu giá
                    DauGiaManager.getInstance().startAuction(0, new Item(2040, -1, 0));
                    System.out.println("Phiên đấu giá đã bắt đầu lúc 20:00 Chủ nhật");
                    minuteTimer.cancel(); // Dừng kiểm tra từng phút
                } else {
                    // Hiển thị thời gian còn lại
                    long hoursLeft = (timeUntilAuction / (60 * 60 * 1000)) % 24;
                    long minutesLeft = (timeUntilAuction / (60 * 1000)) % 60;
                    System.out.println(String.format("Còn %d giờ %d phút %d giây đến phiên đấu giá.", hoursLeft, minutesLeft, secondsLeft));
                }
            }
        }, 0, 1 * 100); // Kiểm tra mỗi phút
    }

    private Calendar getNextSundayAt8PM() {//ch
        Calendar nextSunday = Calendar.getInstance();
        nextSunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        nextSunday.set(Calendar.HOUR_OF_DAY, 14);
        nextSunday.set(Calendar.MINUTE, 27);
        nextSunday.set(Calendar.SECOND, 0);
        nextSunday.set(Calendar.MILLISECOND, 0);

        // Nếu hôm nay đã qua Chủ nhật 8h tối, chuyển sang Chủ nhật tuần sau
        if (nextSunday.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            nextSunday.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return nextSunday;
    }

}