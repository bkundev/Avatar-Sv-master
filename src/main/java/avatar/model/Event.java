package avatar.model;

import avatar.common.BossShopItem;
import avatar.handler.BossShopHandler;
import avatar.handler.UpgradeItemHandler;
import avatar.item.PartManager;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.text.MessageFormat;

@Getter
@Setter
@SuperBuilder
public class Event extends BossShopItem {
    private int itemNeed;
    @Override
    public String initDialog(BossShop bossShop) {
        return MessageFormat.format(
                "Bạn có muốn đổi {0}+{1} để lấy 1 {2})",
                PartManager.getInstance().findPartById(953).getName(),
                super.getItem().getPart().getName()
        );
    }
}