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


    private static List<List<Item>> SieuNhan = new ArrayList<>();

    private static List<List<Item>> setHaiTac = new ArrayList<>();


    static {
        List<Item> luffySet = new ArrayList<>();
        for (int i = 5409; i <= 5413; i++) {
            luffySet.add(new Item(i));
        }
        setHaiTac.add(luffySet);//nam

        List<Item> namiSet = new ArrayList<>();
        for (int i = 5414; i <= 5418; i++) {
            namiSet.add(new Item(i));
        }
        setHaiTac.add(namiSet);

        List<Item> mihawkSet = new ArrayList<>();
        for (int i = 5419; i <= 5423; i++) {
            mihawkSet.add(new Item(i));
        }
        setHaiTac.add(mihawkSet);//nam

        List<Item> NicoRobin = new ArrayList<>();
        for (int i = 5424; i <= 5427; i++) {
            NicoRobin.add(new Item(i));
        }
        setHaiTac.add(NicoRobin);//nam

        List<Item> Zoro = new ArrayList<>();
        for (int i = 5428; i <= 5432; i++) {
            Zoro.add(new Item(i));
        }
        setHaiTac.add(Zoro);//nam
///hop sieu nhan

        List<Item> gaoDen = new ArrayList<>();
        for (int i = 3937; i <= 3939; i++) {
            gaoDen.add(new Item(i));
        }
        SieuNhan.add(gaoDen);


        List<Item> gaoDO = new ArrayList<>();
        for (int i = 3940; i <= 3942; i++) {
            gaoDO.add(new Item(i));
        }
        SieuNhan.add(gaoDO);//nam


        List<Item> gaoXanh = new ArrayList<>();
        for (int i = 3943; i <= 3945; i++) {
            gaoXanh.add(new Item(i));
        }
        SieuNhan.add(gaoXanh);//nam

    }




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
                boolean ok =  (Utils.nextInt(100) < 80) ? true : false;
                if(ok){
                    rewardItem.setExpired(-1);
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

    public void openHaiTac(User us, Item item) {
        Random random = new Random();
        List<Item> set;

        do {
            int setIndex = random.nextInt(setHaiTac.size());
            set = setHaiTac.get(setIndex);
        } while (!isGenderCompatible(set.get(0), us)); // Kiểm tra giới tính

        for (int i = 0; i < set.size(); i++) {
            set.get(i).setExpired(-1);
            us.addItemToChests(set.get(i));
        }

        us.getAvatarService().serverDialog("Bạn nhận được set " + set.get(0).getPart().getName() +
                String.format(" Số lượng còn lại : %,d", item.getQuantity()));
    }

    public void openSieuNhan(User us, Item item) {
        Random random = new Random();
        int setIndex = random.nextInt(SieuNhan.size());

        List<Item> set = SieuNhan.get(setIndex);

        for (int i = 0; i < set.size(); i++) {
            set.get(i).setExpired(-1);
            us.addItemToChests(set.get(i));
        }
        us.getAvatarService().serverDialog("Bạn nhận được set "+ set.get(0).getPart().getName() + String.format(" Số lượng còn lại : %,d", item.getQuantity()));
    }

    private boolean isGenderCompatible(Item item, User us) {
        return item.getPart().getGender() == us.getGender();
    }
    private RandomCollection<Integer> chooseItemCollection() {
        RandomCollection<RandomCollection<Integer>> itemCollections = new RandomCollection<>();
        itemCollections.add(40, randomItemList1);
        itemCollections.add(30, randomItemList2);
        itemCollections.add(30, randomItemList3);
        return itemCollections.next();
    }


}
