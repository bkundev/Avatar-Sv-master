package avatar.service;

import avatar.server.DauGiaManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionScheduler {
    private Timer timer;

    public void startScheduling() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();
                System.out.println(now.get(Calendar.DAY_OF_WEEK));// mo dau gia
                //if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && now.get(Calendar.HOUR_OF_DAY) == 20) {
                    DauGiaManager.getInstance().startAuction();
                //}
            }
        }, 0, 24 * 60 * 60 * 1000); // Chạy kiểm tra mỗi ngày một lần
    }
}