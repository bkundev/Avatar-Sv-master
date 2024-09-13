package avatar.lucky;

import avatar.item.Item;
import avatar.lib.RandomCollection;
import avatar.model.Gift;
import avatar.model.User;
import avatar.server.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Random;

public class GiftBox {
    private static final byte Items = 0;
    private static final byte XU = 1;
    private static final byte XP = 2;
    private static final byte LUONG = 3;

    private final RandomCollection<Byte> randomType = new RandomCollection<>();
    private final RandomCollection<Integer> randomItemList1 = new RandomCollection<>();
    private final RandomCollection<Integer> randomItemList2 = new RandomCollection<>();
    private final RandomCollection<Integer> randomItemList3 = new RandomCollection<>();

    public GiftBox() {
        randomType.add(45, Items);
        randomType.add(25, XU);   // Tỷ lệ
        randomType.add(15, XP);   // Tỷ lệ
        randomType.add(15, LUONG); // Tỷ lệ 10%

        randomItemList1.add(30, 4081);//nro
        randomItemList1.add(20, 4082);
        randomItemList1.add(10, 4083);
        randomItemList1.add(10, 4084);
        randomItemList1.add(10, 4085);
        randomItemList1.add(10, 4086);
        randomItemList1.add(10, 4087);

        randomItemList2.add(10, 6523);//item
        randomItemList2.add(10, 6524);
        randomItemList2.add(10, 6525);
        randomItemList2.add(10, 6526);
        randomItemList2.add(10, 6527);//kẹo cao xu
        randomItemList2.add(10, 2866);
        randomItemList2.add(10, 2867);
        randomItemList2.add(10, 2868);
        randomItemList2.add(10, 2869);
        randomItemList2.add(10, 3452);


        randomItemList3.add(10, 3449);//item
        randomItemList3.add(10, 3450);
        randomItemList3.add(10, 3451);
        randomItemList3.add(10, 4083);
        randomItemList3.add(10, 4084);
        randomItemList3.add(10, 2343);
        randomItemList3.add(10, 5142);
        randomItemList3.add(10, 5274);
        randomItemList3.add(10, 6413);
        randomItemList3.add(10, 6414);

    }


    public void open(User us, Item item) {
        byte type = randomType.next();

        switch (type) {
            case Items:
                RandomCollection<Integer> chosenItemCollection = chooseItemCollection();
                int idItems = chosenItemCollection.next();
                if( idItems >= 4081 && idItems <= 4087){

                    Item Nro = new Item(idItems,-1,1);
                    if(us.findItemInChests(idItems) !=null){
                        int quantity = us.findItemInChests(idItems).getQuantity();
                        us.findItemInChests(idItems).setQuantity(quantity+1);
                    }else {
                        us.addItemToChests(Nro);
                    }
                    us.getAvatarService().serverDialog("Bạn nhận được "+ Nro.getPart().getName()  + String.format(" Số lượng còn lại: %,d", item.getQuantity()));
                    break;
                }
                Item rewardItem  = new Item(idItems);
                int ok =  (Utils.nextInt(100) < 80) ? 1 : 0;
                if(ok==0){
                    us.addItemToChests(rewardItem);
                    us.getAvatarService().serverDialog("Bạn nhận được "+ rewardItem.getPart().getName()  + String.format(" Vĩnh viễn Số lượng còn lại: %,d", item.getQuantity()));
                    break;
                }
                rewardItem.setExpired(System.currentTimeMillis() + (86400000L * 30));
                us.addItemToChests(rewardItem);
                us.getAvatarService().serverDialog("Bạn nhận được "+ rewardItem.getPart().getName()  + String.format(" 30 ngày, Số lượng còn lại: %,d", item.getQuantity()));
                break;
            case XU:
                int xu = Utils.nextInt(1, 10) * 1000;
                us.updateXu(xu);
                us.getAvatarService().serverDialog("Bạn nhận được "+ xu +" Xu " + String.format("Số lượng còn lại: %,d", item.getQuantity()));
                us.getAvatarService().updateMoney(0);
                break;
            case XP:
                int xp = Utils.nextInt(1, 10) * 10;
                us.updateXP(xp);
                us.getAvatarService().serverDialog("Bạn nhận được "+ xp +" XP "+ String.format("Số lượng còn lại : %,d", item.getQuantity()));
                us.getAvatarService().updateMoney(0);
                break;
            case LUONG:
                int luong = Utils.nextInt(1, 5);
                us.updateLuong(luong);
                us.getAvatarService().serverDialog("Bạn nhận được "+ luong +" Lượng "+ String.format("Số lượng còn lại : %,d", item.getQuantity()));
                us.getAvatarService().updateMoney(0);
                break;
        }
    }


    private RandomCollection<Integer> chooseItemCollection() {
        RandomCollection<RandomCollection<Integer>> itemCollections = new RandomCollection<>();
        itemCollections.add(40, randomItemList1);
        itemCollections.add(30, randomItemList2);
        itemCollections.add(30, randomItemList3);
        return itemCollections.next();
    }


}
