package avatar.lucky;

import avatar.lib.RandomCollection;
import avatar.model.Gift;
import avatar.model.User;
import avatar.server.Utils;

import java.util.Random;
import java.util.Random;

public class GiftBox {
    private static final byte XU = 1;
    private static final byte XP = 2;
    private static final byte LUONG = 3;

    private final RandomCollection<Byte> randomType = new RandomCollection<>();

    public GiftBox() {
        // Cấu hình tỷ lệ xuất hiện cho các loại quà
        randomType.add(70, XU);   // Tỷ lệ 70% cho XU
        randomType.add(20, XP);   // Tỷ lệ 20% cho XP
        randomType.add(10, LUONG); // Tỷ lệ 10% cho LUONG
    }

    public void open(User us) {
        byte type = randomType.next();

        switch (type) {
            case XU:
                int xu = Utils.nextInt(1, 10) * 1000;
                us.updateXu(xu);
                us.getAvatarService().serverDialog("Bạn nhận được "+ xu +" Xu");
                us.getAvatarService().updateMoney(0);
                break;
            case XP:
                int xp = Utils.nextInt(1, 10) * 10;
                us.updateXP(xp);
                us.getAvatarService().serverDialog("Bạn nhận được "+ xp +" XP");
                us.getAvatarService().updateMoney(0);
                break;
            case LUONG:
                int luong = Utils.nextInt(1, 5);
                us.updateLuong(luong);
                us.getAvatarService().serverDialog("Bạn nhận được "+ luong +" Lượng");
                us.getAvatarService().updateMoney(0);
                break;
        }
    }
}
