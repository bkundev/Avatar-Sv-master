package avatar.model;

import avatar.common.BossShopItem;
import avatar.db.DbManager;
import avatar.item.Item;
import avatar.item.Part;
import avatar.lucky.DialLucky;
import avatar.lucky.GiftBox;
import avatar.network.Session;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

import avatar.service.*;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import java.io.IOException;

import avatar.network.Message;
import avatar.play.MapService;

import java.util.ArrayList;

import avatar.play.Zone;
import avatar.server.GameString;
import avatar.server.ServerManager;
import avatar.server.UserManager;
import avatar.server.Utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

@Getter
@Setter
public class User {
    private static int chestLevel;
    private static final int[] UPGRADE_COST_COINS = {0, 0,0,20000, 50000, 100000, 200000 ,200000,500000,600000,70000,0,1000000,1200000,1500000,1700000,2000000,2500000,2700000,3000000,4000000,5000000};
    private static final int[] UPGRADE_COST_GOLD = {0, 0, 0, 0, 0, 0, 0, 200,500,600,700,1000,1000 ,1200 ,1500 ,1700 ,2000 ,2500 ,2700 ,3000 ,4000 ,5000 };
    public boolean AutoFish;
    public int bossMapId;
    public int TopPhaoLuong;
    public int spam;
    public int HP;
    public int xu_from_boss;
    private boolean isDefeated;
    private boolean isSpam;
    private int storedXuUpdate; // Biến lưu trữ xu đã cập nhật
    private static final Logger logger = Logger.getLogger(User.class);
    public Session session;
    private int id;
    private String username;
    private String password;
    private short idFish;
    private byte gender;
    public long xu;
    public int luong;
    public int luongKhoa;

    private int dame;

    private int dameToXu;

    public int xeng;
    private short clanID;
    private byte role;
    private byte star;
    private int leverMain;
    private int expMain;
    private int leverFarm;
    private byte leverPercen;
    private int expFarm;
    private byte friendly;
    private byte crazy;
    private byte stylish;
    private byte happy;
    private byte hunger;
    private byte chestSlot;
    private byte chestHomeSlot;
    private int scores;
    private List<Item> wearing;
    public List<Item> chests;
    public List<Item> chestsHome;

    private Zone zone;
    private short x, y;
    private byte direct;
    private List<Menu> menus;
    private DialLucky dialLucky;
    private short idImg = -1;
    private List<Command> listCmd;
    private List<Command> listCmdRotate;
    @Getter
    @Setter
    private boolean loadDataFinish;

    @Getter
    @Setter
    private List<BossShopItem> bossShopItems;
    private List<Part> ShopEvent;


    private List<Byte> boardIDs;
    List<Byte> moneyPutList;

    public User() {
        this.role = -1;
        this.chests = new ArrayList<>();
        this.wearing = new ArrayList<>();
        this.listCmd = new ArrayList<>();
        this.listCmdRotate = new ArrayList<>();
        this.isDefeated = false;
        this.isSpam = false;
        this.dameToXu = 0;
        this.boardIDs = new ArrayList<>();
        this.moneyPutList = new ArrayList<>();
    }

    public List<Byte> getMoneyPutList() {
        return this.moneyPutList;
    }
    public synchronized void updateMoneyPutList(List<Byte> newMoneyPutList) {
        if (this.moneyPutList == null) {
            this.moneyPutList = new ArrayList<>(); // Khởi tạo danh sách nếu chưa có
        }
        this.moneyPutList.clear(); // Xóa danh sách cũ (nếu cần)
        this.moneyPutList.addAll(newMoneyPutList); // Thêm các phần tử mới
    }

    public synchronized void updateMoneyPutListByIndex(byte indexFrom, byte indexTo) {
        if (this.moneyPutList != null && this.moneyPutList.size() > 0) {
            // Kiểm tra xem indexFrom và indexTo có hợp lệ trong danh sách không
            if (indexFrom >= 0 && indexFrom < this.moneyPutList.size() &&
                    indexTo >= 0 && indexTo < this.moneyPutList.size()) {

                // Lấy giá trị tại vị trí indexFrom
                Byte valueToMove = this.moneyPutList.get(indexFrom);

                // Xóa phần tử tại vị trí indexFrom
                this.moneyPutList.remove(indexFrom);

                // Thêm phần tử vào vị trí indexTo
                this.moneyPutList.add(indexTo, valueToMove);
            }
        }
    }

    public void calculateDameToXu() {
        int totalDamage = 30;
        for (Item item : wearing) {
            totalDamage += item.getPart().getLevel();
        }
        this.dameToXu = totalDamage;
    }

    public User(String username, int xuFromBoss) {
        this.username = username;
        this.xu_from_boss = xuFromBoss;
    }
    public User(String username, int xeng, int TopPhaoLuong) {
        this.username = username;
        this.xeng = xeng;
        this.TopPhaoLuong = TopPhaoLuong;
    }
    public AvatarService getAvatarService() {
        return session.getAvatarService();
    }

    public FarmService getFarmService() {
        return session.getFarmService();
    }

    public HomeService getHomeService() {
        return session.getHomeService();
    }

    public ParkService getParkService() {
        return session.getParkService();
    }

    public MapService getMapService() {
        if (zone == null) {
            return NoService.getInstance();
        }
        return zone.getService();
    }


    public Service getService() {
        return session.getService();
    }

    public void sortWearing() {
        this.wearing.sort((o1, o2) -> o1.getPart().getZOrder() - o2.getPart().getZOrder());
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public synchronized void updateXu(long xuUp) {
        this.xu += xuUp;
    }
    public synchronized void updateXuKillBoss(int dame) {
        this.storedXuUpdate += dame; // Lưu xu vào biến tạm thời
    }
    public void applyStoredXuUpdate() {
        //this.updateXu(storedXuUpdate * 5); // Cộng dồn số xu ba lần
        this.Updatexu_from_boss(storedXuUpdate);
        this.storedXuUpdate = 0; // Reset xu đã lưu trữ
    }
    public synchronized void updateXP(int XP) {
        this.expMain += XP;
    }
    public synchronized void Updatexu_from_boss(int xu_from_boss) {
        this.xu_from_boss += xu_from_boss;
    }
    public synchronized void updateLuong(int luongUp) {
        this.luong += luongUp;
        try {
            this.getAvatarService().SendTabmsg("Luong : "+ this.luong);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void updateScores(int ScoresUp) {
        this.scores += ScoresUp;
    }

    public synchronized void updateLuongKhoa(int luongUp) {
        this.luong += luongUp;
    }

    public synchronized void updateXeng(int xengUp) {
        this.xeng += xengUp;
    }

    public synchronized void updateHunger(int hunger) {
        this.hunger += (byte) hunger;
    }
    public synchronized void updateChestSlot(int chestslot) {
        this.chestSlot += (byte) chestslot;
    }

    public synchronized void updateChest_homeSlot(int chestslot) {
        this.chestHomeSlot += (byte) chestslot;
    }

    public synchronized void updateHP(long dame,Boss boss,User us) throws IOException {
        this.HP += dame;
        if (HP <= 0) {
            HP = 0;
            if (!isDefeated) {
                isDefeated = true;
                // Chỉ thực hiện xử lý khi boss chưa bị đánh bại
                boss.handleBossDefeat(boss,us);
            }
        }
    }

    public synchronized void updateSpam(long spams,Boss boss, User us) throws IOException {
        boss.spam += spams;
        System.out.println("Spam " + boss.getSpam());
        if (boss.getSpam() <= 0) {
            boss.spam = 0;
            isSpam = false;
            if (!isSpam) {
                isSpam = true;
                boss.hanlderNhatHopQua(boss,us);
            }
        }
    }
    public boolean isSpam() {
        return isSpam;
    }
    public boolean isDefeated() {
        return isDefeated;
    }



    public int getChestLevel() {
        int chestSlotHome = this.getChestHomeSlot(); // Lấy số ô của rương hiện tại

        if (chestSlotHome <= 10) {
            return 1; // Cấp 1 với 10 ô
        } else if (chestSlotHome <= 15) {
            return 2; // Cấp 2 với 15 ô
        } else if (chestSlotHome <= 20) {
            return 3; // Cấp 3 với 20 ô
        } else if (chestSlotHome <= 25) {
            return 4; // Cấp 4 với 25 ô
        } else if (chestSlotHome <= 30) {
            return 5; // Cấp 4 với 25 ô
        } else if (chestSlotHome <= 35) {
            return 6; // Cấp 4 với 25 ô
        } else if (chestSlotHome <= 40) {
            return 7; // Cấp 4 với 25 ô
        } else if (chestSlotHome <= 45) {
            return 8; // Cấp 4 với 25 ô
        } else if (chestSlotHome <= 50) {
            return 9; // Cấp 4 với 25 ô
        } else if (chestSlotHome <= 55) {
            return 10; // Cấp 4 với 25 ô
        }

        return -1; // Trường hợp không hợp lệ
    }




    public synchronized void updateTopPhaoLuong(int luongThaPhao) {
        this.luong += luongThaPhao;
        this.TopPhaoLuong += 1;
        DbManager.getInstance().executeUpdate("UPDATE `players` SET `TopPhaoLuong` = ? WHERE `user_id` = ? LIMIT 1;",
                this.TopPhaoLuong, this.id);
    }

    public void sendMessage(Message ms) {
        this.session.sendMessage(ms);
    }

    protected void saveData() {
        DbManager.getInstance().executeUpdate("UPDATE `players` SET `gender` = ?, `friendly` = ?, `crazy` = ?, `stylish` = ?, `happy` = ?, `hunger` = ?, `chest_slot` = ? , `chest_home_slot` = ? WHERE `user_id` = ? LIMIT 1;",
                this.gender, this.friendly, this.crazy, this.stylish, this.happy, this.hunger,this.chestSlot,this.chestHomeSlot, this.id);
        DbManager.getInstance().executeUpdate("UPDATE `players` SET `xu` = ?, `luong` = ?, `luong_khoa` = ?, `xeng` = ?, `level_main` = ?, `exp_main` = ?,`scores` = ? , `xu_from_boss` = ? , `TopPhaoLuong` = ? WHERE `user_id` = ? LIMIT 1;",
                this.xu, this.luong, this.luongKhoa, this.xeng, this.leverMain, this.expMain,this.scores,this.xu_from_boss,this.TopPhaoLuong, this.id);
        JSONArray jChests = new JSONArray();
        for (Item item : this.chests) {
            JSONObject obj = new JSONObject();
            obj.put("id", item.getId());
            obj.put("expired", item.getExpired());
            obj.put("quantity", item.getQuantity());
            jChests.add(obj);
        }
        JSONArray jWearing = new JSONArray();
        for (Item item : this.wearing) {
            JSONObject obj = new JSONObject();
            obj.put("id", item.getId());
            obj.put("expired", item.getExpired());
            obj.put("quantity", item.getQuantity());
            jWearing.add(obj);
        }

        JSONArray jChestsHome = new JSONArray();
        for (Item item : this.chestsHome) {
            JSONObject obj = new JSONObject();
            obj.put("id", item.getId());
            obj.put("expired", item.getExpired());
            obj.put("quantity", item.getQuantity());
            jChestsHome.add(obj);
        }

        DbManager.getInstance().executeUpdate("UPDATE `players` SET `chests` = ?, `wearing` = ?, `chests_home` = ? WHERE `user_id` = ? LIMIT 1;",
                jChests.toJSONString(), jWearing.toJSONString(),jChestsHome.toJSONString(), this.id);
        System.out.println("Save data user " + this.getUsername());
    }

    public synchronized boolean login() {
        if (!ServerManager.active) {
            getService().serverMessage("Máy chủ đang bảo trì. Vui lòng quay lại sau : v");
            return false;
        }
        String ACCOUNT_LOGIN = "SELECT * FROM `users` WHERE `username` = ? AND `password` = ? LIMIT 1;";
        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(ACCOUNT_LOGIN);) {
            ps.setString(1, this.username);
            ps.setString(2, Utils.md5(password));
            try (ResultSet red = ps.executeQuery()) {
                if (red.next()) {
                    this.id = red.getInt("id");
                    this.role = (byte) red.getInt("role");
                    boolean active = red.getBoolean("active");
                    if (!active) {
                        getService().serverMessage(GameString.userLoginActive());
                        return false;
                    }
                    JSONObject banData = (JSONObject) ((red.getString("ban") != null)
                            ? JSONValue.parse(red.getString("ban"))
                            : new JSONObject());
                    if (!banData.isEmpty()) {
                        int banType = ((Long) banData.get("type")).intValue();
                        if (banType == 2) {
                            if (banData.get("forever") != null) {
                                getService().serverMessage(GameString.userLoginLockForever());
                                return false;
                            }
                            int minutes = ((Long) banData.get("minutes")).intValue();
                            Date timeNowwww = new Date();
                            Date banStart = Utils.getDate((String) banData.get("start"));
                            Date banEnd = new Date(banStart.getTime() + 60000 * minutes);
                            if (banEnd.after(timeNowwww)) {
                                minutes = (int) ((banEnd.getTime() - timeNowwww.getTime()) / 60000L);
                                getService().serverMessage(GameString.userLoginLock(minutes));
                                return false;
                            }
                        }
                    }
                    User us = UserManager.getInstance().find(this.id);
                    if (us != null) {
                        getService().serverMessage(GameString.userLoginMany());
                        us.getService().serverMessage(GameString.userLoginMany());
                        Utils.setTimeout(() -> {
                            us.session.close();
                        }, 1000);
                        return false;
                    }
                    return true;
                } else {
                    getService().serverMessage(GameString.loginPassFail());
                }
            }
        } catch (SQLException ex) {
            getService().serverMessage(ex.getMessage());
        }
        return false;
    }

    public boolean loadData() {
        String GET_PLAYER_DATA = "SELECT * FROM `players` WHERE `user_id` = ? LIMIT 1;";
        try (Connection connection = DbManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_PLAYER_DATA);) {
            ps.setInt(1, this.id);
            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) {
                    if (res.getInt("user_id") == 7) {
                        //this.id+=(Npc.ID_ADD+1000);
                    }
                    this.leverMain = res.getInt("level_main");
                    this.expMain = res.getInt("exp_main");
                    this.gender = res.getByte("gender");
                    this.chestSlot = res.getByte("chest_slot");
                    this.chestHomeSlot = res.getByte("chest_home_slot");
                    this.xu = res.getLong("xu");
                    this.luong = res.getInt("luong");
                    this.luongKhoa = res.getInt("luong_khoa");
                    this.xeng = res.getInt("xeng");
                    this.clanID = res.getShort("clan_id");                    //res.writeShort(2206);
                    this.friendly = res.getByte("friendly");
                    this.crazy = res.getByte("crazy");
                    this.stylish = res.getByte("stylish");
                    this.happy = res.getByte("happy");
                    this.hunger = res.getByte("hunger");
                    this.star = res.getByte("star");
                    this.scores = res.getInt("scores");
                    this.xu_from_boss = res.getInt("xu_from_boss");
                    this.TopPhaoLuong = res.getInt("TopPhaoLuong");

                    this.chests = new ArrayList<>();
                    JSONArray chests = (JSONArray) JSONValue.parse(res.getString("chests"));
                    for (Object chest : chests) {
                        JSONObject obj = (JSONObject) chest;
                        int id = ((Long) obj.get("id")).intValue();
                        long expired = ((Long) obj.get("expired"));
                        int quantity = 1;
                        if (obj.containsKey("quantity")) {
                            quantity = ((Long) obj.get("quantity")).intValue();
                        }
                        Item item = Item.builder().id(id)
                                .quantity(quantity)
                                .expired(expired)
                                .build();
                        if (item.reliability() > 0) {
                            this.chests.add(item);
                        }
                    }
                    this.wearing = new ArrayList<>();
                    JSONArray wearing = (JSONArray) JSONValue.parse(res.getString("wearing"));
                    for (Object o : wearing) {
                        JSONObject obj = (JSONObject) o;
                        int id = ((Long) obj.get("id")).intValue();
                        long expired = ((Long) obj.get("expired"));
                        int quantity = 1;
                        if (obj.containsKey("quantity")) {
                            quantity = ((Long) obj.get("quantity")).intValue();
                        }
                        Item item = Item.builder().id(id)
                                .quantity(quantity)
                                .expired(expired)
                                .build();
                        if (item.reliability() > 0) {
                            this.wearing.add(item);
                        }
                    }
                    this.chestsHome = new ArrayList<>();
                    JSONArray chestshome = (JSONArray) JSONValue.parse(res.getString("chests_home"));
                    for (Object chest : chestshome) {
                        JSONObject obj = (JSONObject) chest;
                        int id = ((Long) obj.get("id")).intValue();
                        long expired = ((Long) obj.get("expired"));
                        int quantity = 1;
                        if (obj.containsKey("quantity")) {
                            quantity = ((Long) obj.get("quantity")).intValue();
                        }
                        Item item = Item.builder().id(id)
                                .quantity(quantity)
                                .expired(expired)
                                .build();
                        if (item.reliability() > 0) {
                            this.chestsHome.add(item);
                        }
                    }

                    calculateDameToXu();

                    setLoadDataFinish(true);
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            getService().serverMessage(ex.getMessage());
        }
        return false;
    }

    public void initAvatar() {
        sortWearing();
        listCmd.add(new Command("Chức năng", 2));
        listCmdRotate.add(new Command((short) 0, "Hội nhóm", 41, (byte) 1));
        listCmdRotate.add(new Command((short) 4, "Oan Tu Xi", 44, (byte) 1));
        //listCmdRotate.add(new Command((short) 33, "Hô phong hoán vũ", 1053, (byte) 0));
        //listCmdRotate.add(new Command((short) 34, "Triệu hồi bia mộ", 1053, (byte) 0));
        //listCmdRotate.add(new Command((short) 35, "Cánh thần hiển linh", 1055, (byte) 0));
        //listCmdRotate.add(new Command((short) 48, "pháo sinh nhật(5 lượng)", 1115, (byte) 0));
        //listCmdRotate.add(new Command((short) 47, "Pháo hạnh phúc (5 lượng)", 242, (byte) 0));
        listCmdRotate.add(new Command((short) 8, "Pháo thịnh vượng (5 lượng)", 241, (byte) 0));
        //listCmdRotate.add(new Command((short) 9, "triệu hồi con chim k nhớ tên", 1082, (byte) 0));
        //listCmdRotate.add(new Command((short) 10, "Rương chỉ sử dụng không được bỏ(sẽ bị xóa item ở rương gốc)", 1204, (byte) 0));
        listCmdRotate.add(new Command((short) 23, "Cuốc", 869, (byte) 0));
        //listCmdRotate.add(new Command((short) 36, "Hẹn hò", 1096, (byte) 1));
    }

    public void doAction(Message ms) {
        try {
            int idTo = ms.reader().readInt();
            short action = ms.reader().readShort();
            User us = UserManager.getInstance().find(idTo);

            if(this.getZone().getMap().getId() == 16){
                this.getAvatarService().serverDialog("Bạn không thể hành động ở đây !");
                return;
            }
            switch (action) {
                case 101:
                    DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                    int dayIndex = dayOfWeek.getValue(); // 1 = Monday, 7 = Sunday
                    if (dayIndex == 5 || dayIndex == 6) {
                        getMapService().doAction(id, idTo, action);
                        break;
                    }
                    if(gender== us.gender) {
                        this.getAvatarService().serverDialog("làm gì vậy bro, đồng giới thì thứ 6 thứ 7");
                        break;
                    }
                default:
                    getMapService().doAction(id, idTo, action);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getExpMax() {
        return (this.leverMain * (this.leverMain + 1) / 2) * 1000;
    }

    public byte getLeverMainPercen() {
        return (byte) (this.expMain * 100 / getExpMax());
    }

    public void viewChest(Message ms) throws IOException {
        int type = ms.reader().readInt();
        if(type!=id)
        {
            List<Item> _chests = chests.stream().filter(item -> {
                return item.getPart().getZOrder() == 30 || item.getPart().getZOrder() == 40;
            }).collect(Collectors.toList());
            getAvatarService().viewChest(_chests);
            return;
        }
        List<Item> _chests = chests.stream().filter(item -> {
            return item.getPart().getZOrder() != 30 && item.getPart().getZOrder() != 40;
        }).collect(Collectors.toList());
        getAvatarService().viewChest(_chests);
    }

    // hỏi nâng cấp
    public String getUpgradeRequirements() {
        if (chestSlot/5 >= UPGRADE_COST_COINS.length - 1) {
            return "Rương đã đạt cấp tối đa";
        }

        int nextLevel = (chestSlot/5)+1;
        int coinCost = UPGRADE_COST_COINS[nextLevel];
        int goldCost = UPGRADE_COST_GOLD[nextLevel];

        return String.format(
                "Để nâng cấp lên rương cấp %d bạn cần %d xu và %d lượng hoặc thẻ nâng cấp rương.",
                nextLevel-2, coinCost, goldCost);
    }
    // nâng cấp rương
    public String upgradeChest() {
        if (chestSlot/5 >= UPGRADE_COST_COINS.length - 1) {
            return "Rương đã đạt cấp tối đa";
        }

        int nextLevel = (chestSlot/5)+1;
        int coinCost = UPGRADE_COST_COINS[nextLevel];
        int goldCost = UPGRADE_COST_GOLD[nextLevel];

        Item theNangCap = findItemInChests(3861);
        if(theNangCap != null) {
            this.removeItem(3861,1);
            this.updateChestSlot(+5);
            return String.format(
                    "chúc mừng bạn đã nâng cấp thành công rương cấp %d và có %d ô rương.",
                    nextLevel-2, this.getChestSlot()
            );
        }

        if (xu >= coinCost && luong >= goldCost) {
            this.updateXu(-coinCost);
            this.updateLuong(-goldCost);
            this.updateChestSlot(+5);
            this.getAvatarService().updateMoney(0);
            return String.format(
                    "chúc mừng bạn đã nâng cấp thành công rương cấp %d và có %d ô rương.",
                    nextLevel-2, this.getChestSlot()
            );
        }


        return "không đủ xu hoặc lượng";
    }
    public void requestYourInfo(Message ms) {
        try {
            int userId = ms.reader().readInt();
            User us = UserManager.getInstance().find(userId);
            if (us != null) {
                this.getAvatarService().requestYourInfo(us);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doAvatarFeel(Message ms) {
        try {
            if (ms.reader().available() <= 0) {
                return;
            }
            byte idFeel = ms.reader().readByte();
            System.out.println("doAvatarFeel msg 57 = " + idFeel + " ");
            this.getMapService().doAvatarFeel(id, idFeel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        if (zone != null) {
            zone.leave(this);
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DbManager.getInstance().executeUpdate("UPDATE `players` SET `is_online` = ?, `client_id` = ?, `last_online` = ? WHERE `user_id` = ? LIMIT 1;", 0, session.id, timestamp, this.id);
        if (isLoadDataFinish()) {
            saveData();
        }
    }

    @Override
    public String toString() {
        return "User " + this.username;
    }

    public void move(Message ms) {
        try {
            if (ms.reader().available() < 5) {
                return;
            }
            short x = ms.reader().readShort();
            short y = ms.reader().readShort();
            byte direct = ms.reader().readByte();
            if (ms.reader().available() >= 2) {
                ms.reader().readShort();
            }
            this.x = x;
            this.y = y;
            this.direct = direct;
            getMapService().move(this);
            System.out.println("move " + x + ", y = " + y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addExp(int exp) {
        this.expMain += exp;
        int expMax = getExpMax();
        if (this.expMain >= expMax) {
            this.leverMain++;
            this.expMain -= expMax;
        }
    }

    public boolean checkFullSlotChest(){
        System.out.println("chestSlot: " + this.chestSlot);
        System.out.println("chests.size(): " + this.chests.size());
        if(this.getChestSlot() <= this.getChests().size())
        {
            getAvatarService().serverDialog("Rương đồ đã đầy");
            return true;
        }
        return false;
    }

    public void addItemToChests(Item item) {
        synchronized (chests) {

            Item itm = findItemInChests(item.getId());
            if (itm != null) {
                if (itm.getPart().getType() == -2) {
                    itm.increase(item.getQuantity());
                } else {
                    setReliabilityForItem(itm, item);
                }
                this.chests.add(item);
                return;
            } else {
                itm = findItemInWearing(item.getId());
                if (itm != null) {
                    setReliabilityForItem(itm, item);
                    return;
                }
            }
            this.chests.add(item);
        }

    }

    public void addItemToChestsHome(Item item) {
        synchronized (chestsHome) {

            Item itm = findItemInChests(item.getId());
            if (itm != null) {
                if (itm.getPart().getType() == -2) {
                    itm.increase(item.getQuantity());
                } else {
                    setReliabilityForItem(itm, item);
                }
                this.chestsHome.add(item);
                return;
            } else {
                itm = findItemInChests(item.getId());
                if (itm != null) {
                    setReliabilityForItem(itm, item);
                    return;
                }
            }
            this.chestsHome.add(item);
        }

    }


    public void setReliabilityForItem(Item old, Item newI) {
        // item expired == -1;
        if (!old.isForever()) {
            if (newI.isForever() || newI.reliability() > old.reliability()) {
                old.setExpired(newI.getExpired());
            }
        }
    }

    public void removeItemFromChests(Item item) {
        synchronized (chests) {
            this.chests.remove(item);
        }
    }

    public void removeItemFromChestsHome(Item item) {
        synchronized (chestsHome) {
            this.chestsHome.remove(item);
        }
    }


    public void addItemToWearing(Item item) {
        synchronized (wearing) {
            this.wearing.add(item);
            calculateDameToXu();
        }
    }

    public void removeItemFromWearing(Item item) {
        synchronized (wearing) {
            this.wearing.remove(item);
            calculateDameToXu();
        }
    }

    public Item findItemInChests(int id) {
        synchronized (chests) {
            for (Item item : chests) {
                if (item.getId() == id) {
                    return item;
                }
            }
            return null;
        }
    }

    public Item findItemInWearing(int id) {
        synchronized (wearing) {
            for (Item item : wearing) {
                if (item.getId() == id) {
                    return item;
                }
            }
            return null;
        }
    }

    public Item findItemInChestsHome(int id) {
        synchronized (chestsHome) {
            for (Item item : chestsHome) {
                if (item.getId() == id) {
                    return item;
                }
            }
            return null;
        }
    }

    public Item findItemWearingByZOrder(int zOrder) {
        synchronized (wearing) {
            for (Item item : wearing) {
                if (item.getPart().getZOrder() == zOrder) {
                    return item;
                }
            }
            return null;
        }
    }

    public boolean removeItem(int id, int quantity) {
        Item item = findItemInChests(id);
        if (item != null) {
            int q = item.reduce(quantity);
            if (q <= 0) {
                removeItemFromChests(item);
            }
            return true;
        }
        return false;
    }

    public void usingItem(short itemID, byte type) {
        try {
            logger.debug("itemID: " + itemID + " type: " + type);
            if (type == 1) {
                logger.debug("Find Item");
                Item item = findItemInChests(itemID);
                logger.debug("Find Done");
                if (item == null) {
                    getService().serverDialog("Không tìm thấy vật phẩm");
                    return;
                }
                logger.debug("Find Detail Item");
                Part part = item.getPart();
                int gender = part.getGender();
                logger.debug("Item Gender=" + gender);
                if ((gender == 1 || gender == 2) && (this.gender != gender)) {
                    getService().serverDialog("Giới tính không phù hợp");
                    return;
                }
                logger.debug("Get Type");
                short pType = part.getType();
                logger.debug("Type =" + pType);
                if (pType == -1) {
                    int zOrder = part.getZOrder();
                    Item w = findItemWearingByZOrder(zOrder);
                    if (w != null) {
                        removeItemFromWearing(w);
                        addItemToChests(w);
                    }
                    addItemToWearing(item);
                    removeItemFromChests(item);
                    sortWearing();
                    getMapService().usingPart(id, itemID);
                } else if (pType == -2) {
                    if(item.getId()==683)
                    {
                        if((this.chests.size() >= this.getChestSlot())){
                            getService().serverMessage("Bạn phải có ít nhất 1 ô trống");
                            return;
                        }
                        removeItem(item.getId(), 1);
                        GiftBox giftBox = new GiftBox();
                        giftBox.open(this,item);
                    }

                    else {
                        getService().serverMessage(String.format("Số lượng: %,d", item.getQuantity()));
                    }
                } else {
                    item = findItemInWearing(itemID);
                    removeItemFromWearing(item);
                    addItemToChests(item);
                    getMapService().usingPart(id, itemID);
                    //getService().serverDialog("Vật phẩm shop Loi, sẽ sớm fix");
                }
            } else {
                Item item = findItemInWearing(itemID);
                if (item == null) {
                    return;
                }
                int zOrder = item.getPart().getZOrder();
                if (zOrder == 10 || zOrder == 20 || zOrder == 50) {
                    getService().serverDialog("Không thể cất vật phẩm này.");
                    return;
                }
                removeItemFromWearing(item);
                addItemToChests(item);
                getMapService().usingPart(id, itemID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void usingItem(Message ms) {
        try {
            short itemID = ms.reader().readShort();
            byte type = ms.reader().readByte();
            usingItem(itemID, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chat(Message ms) {
        try {
            if (ms.reader().available() < 4) {
                return;
            }
            String message = ms.reader().readUTF();
            getMapService().chat(this, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doRemoveItem(Message ms) {
        try {
            short itemID = ms.reader().readShort();
            byte type = ms.reader().readByte();
            if (type == 0) {
                Item item = findItemInWearing(itemID);
                if (item != null) {
                    int zOrder = item.getPart().getZOrder();
                    if (zOrder == 10 || zOrder == 20 || zOrder == 50) {
                        getAvatarService().serverDialog("error : 001");
                        return;
                    }
                    removeItemFromWearing(item);
                    getMapService().removeItem(id, itemID);
                    if (getStylish() > 0) {
                        setStylish((byte) (getStylish() - 1));
                        getAvatarService().requestYourInfo(this);
                    }
                }
            } else {
                Item item = findItemInChests(itemID);
                if (item != null) {
                    removeItemFromChests(item);
                    getAvatarService().removeItem(id, itemID);
                    if (getStylish() > 0) {
                        setStylish((byte) (getStylish() - 1));
                        getAvatarService().requestYourInfo(this);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyNetWaitMessage() throws IOException {
        synchronized (this.session.obj) {
            this.session.obj.notifyAll();
        }
    }

    public void addItemQuatyToChest(int itemID){
        Item item = new Item(itemID,-1,1);
        addItemToChests(item);
    }
    public void skillUidToBoss(List<User> players,int us ,int npcID,byte skill1,byte skill2){
        for (User player : players) {
            EffectService.createEffect()
                    .session(player.session)
                    .id(skill1)
                    .style((byte) 0)
                    .loopLimit((byte) 5)
                    .loop((short) 1)
                    .loopType((byte) 1)
                    .radius((short) 1)
                    .idPlayer(us)
                    .send();
            EffectService.createEffect()
                    .session(player.session)
                    .id(skill2)
                    .style((byte) 0)
                    .loopLimit((byte) 5)
                    .loop((short) 1)
                    .loopType((byte) 1)
                    .radius((short) 1)
                    .idPlayer(npcID)
                    .send();
        };
    }
}