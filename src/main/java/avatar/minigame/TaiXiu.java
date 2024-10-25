package avatar.minigame;

import avatar.model.Npc;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaiXiu {
    private static TaiXiu instance; // Singleton instance
    private List<Npc> TaiXiu = new ArrayList<>();

    public static TaiXiu getInstance() {
        if (instance == null) {
            instance = new TaiXiu();
        }
        return instance;
    }
    private TaiXiu() {
    }
    // Thêm một NPC vào danh sách
    public void setNpcTaiXiu(Npc npc) {
        if (npc != null && this.TaiXiu.size() == 0) {  // Đảm bảo chỉ có 1 NPC
            this.TaiXiu.add(npc);
            System.out.println("NPC đã được thêm vào TaiXiu, kích thước hiện tại: " + TaiXiu.size());
            autoChat.start();  // Bắt đầu luồng autoChat khi đã thêm NPC
        }
    }
    public List<Npc> getNpcTaiXiu() {
        return new ArrayList<>(TaiXiu);
    }

    public Thread autoChat = new Thread(() -> {
        while (true) {
            try {
                long startTime = System.currentTimeMillis();
                int countdown = 40;

                // Đếm ngược cho thời gian ván đang diễn ra
                while (countdown > 0) {
                    if (!getNpcTaiXiu().isEmpty()) {
                        Npc npc = TaiXiu.get(0);
                        npc.setTextChats(List.of(
                                MessageFormat.format("Demo Ván đang diễn ra. Thời gian còn lại: {0} giây", countdown)
                        ));
                    }
                    Thread.sleep(1000);
                    countdown = 40 - (int) ((System.currentTimeMillis() - startTime) / 1000);
                }

                String result = calculateResult();
                long preStart = System.currentTimeMillis();
                int preCountdown = 5;

                while (preCountdown > 0) {
                    if (!getNpcTaiXiu().isEmpty()) {
                        Npc npc = TaiXiu.get(0);
                        npc.setTextChats(List.of(
                                MessageFormat.format("Kết quả: {0}, Ván mới sẽ bắt đầu sau: {1} giây", result, preCountdown)
                        ));
                    }
                    Thread.sleep(1000);
                    preCountdown = 5 - (int) ((System.currentTimeMillis() - preStart) / 1000);
                }

            } catch (InterruptedException ignored) {
            }
        }
    });


    private String calculateResult() {
        Random random = new Random();
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;
        int dice3 = random.nextInt(6) + 1;
        int total = dice1 + dice2 + dice3;

        String result = (total >= 11 && total <= 17) ? "Tài" : "Xỉu";
        return "Kết quả: " + dice1 + ", " + dice2 + ", " + dice3 + " = " + total + " " + result;
    }

}
