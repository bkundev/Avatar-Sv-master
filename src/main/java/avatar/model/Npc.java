package avatar.model;

import avatar.constants.NpcName;
import avatar.handler.GlobalHandler;
import avatar.handler.SicboHandler;
import avatar.item.Item;
import avatar.network.Message;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import avatar.play.NpcManager;
import avatar.play.Zone;
import avatar.server.UserManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class Npc extends User {

    public static final int ID_ADD = 2000000000;
    private static boolean Cuoc;

    @Getter
    @Setter
    private List<String> textChats;
    private static User us;
    private List<User> lstUser = UserManager.users;
    public static List<String> textChatsTaiXiu;
    private Thread autoChatBot = new Thread(() -> {
        while (true) {
            try {
                for (String text : textChats) {
                    getMapService().chat(this, text);
                    Thread.sleep(6000);
                }
                if (textChats == null || textChats.size() == 0) {
                    Thread.sleep(10000);
                }
            } catch (InterruptedException ignored) {

            }
        }
    });
    public Thread autoChatBotTaiXiu = new Thread(() -> {
        BigInteger reset_xuCuoc = new BigInteger("0");
        while (!Thread.currentThread().isInterrupted()){
            try {
                this.NewSicbo();
                for (String text : textChatsTaiXiu) {
                    Thread.sleep(1000);
                    getMapService().chat(this, text);
                    if(text.contains("Tài")) {
                        for (int i = 0; i < lstUser.stream().count(); i++) {
                            if(lstUser.get(i).datCuoc == 0){break;}
                            if(lstUser.get(i).datCuoc == 1)
                            {
                                BigInteger XuCuoc = lstUser.get(i).tienCuoc;
                                BigInteger x2 = new BigInteger("2");
                                BigInteger sum = XuCuoc.multiply(x2);
                                lstUser.get(i).getAvatarService().serverDialog("Chúc Mừng Bạn Đã Bú "+text+" và thắng "+sum+" Tiền.");
                                lstUser.get(i).updateXu(sum);
                            } else {
                                BigInteger XuCuoc = lstUser.get(i).tienCuoc;
                                lstUser.get(i).getAvatarService().serverDialog("Cút Rồi "+ text+ " Bạn đã gãy "+XuCuoc+" Xu");
                            }
                            lstUser.get(i).datCuoc = 0;
                            lstUser.get(i).tienCuoc = reset_xuCuoc;
                        }
                    } else if (text.contains("Xỉu")) {
                        for (int i = 0; i < lstUser.stream().count(); i++) {
                            {
                                if (lstUser.get(i).datCuoc == 2) {
                                    BigInteger XuCuoc = lstUser.get(i).tienCuoc;
                                    BigInteger x2 = new BigInteger("2");
                                    BigInteger sum = XuCuoc.multiply(x2);
                                    lstUser.get(i).getAvatarService().serverDialog("Chúc Mừng Bạn Đã Bú " + text + " và thắng " + sum + " Tiền.");
                                    lstUser.get(i).updateXu(sum);
                                } else {
                                    BigInteger XuCuoc = lstUser.get(i).tienCuoc;
                                    lstUser.get(i).getAvatarService().serverDialog("Cút Rồi " + text + " Bạn đã gãy " + XuCuoc + " Xu");
                                }
                            }
                            lstUser.get(i).datCuoc = 0;
                            lstUser.get(i).tienCuoc = reset_xuCuoc;
                        }
                    }
                }
                Npc.Cuoc = false;
                Npc.textChatsTaiXiu.removeAll(textChatsTaiXiu);
                if (textChatsTaiXiu == null || textChatsTaiXiu.size() == 0) {
                    Thread.sleep(3000);
                }
                break;
            } catch (InterruptedException e) {
                //Thread.currentThread().interrupt();
                break; // Exit the thread loop
            }
        }
    });
    public static void NewSicbo(){
        if(Npc.Cuoc==false){
            Zone zi = us.getZone();
            Random random = new Random();
            int dice1 = random.nextInt(6) + 1;
            int dice2 = random.nextInt(6) + 1;
            int dice3 = random.nextInt(6) + 1;
            int total = dice1 + dice2 + dice3;
            Npc.Cuoc = true;
            System.out.println("Dice 1: " + dice1);
            System.out.println("Dice 2: " + dice2);
            System.out.println("Dice 3: " + dice3);
            System.out.println("Total: " + total);
            List<String> stringList = new ArrayList<>();
            Npc npc = NpcManager.getInstance().find(zi.getMap().getId(), zi.getId(), NpcName.Tai_Xiu + Npc.ID_ADD);
            for (int i = 10; i >= 1; i--) {
                stringList.add("Thời Gian Đặt Cược : "+i+ " S");
                System.out.print(i);
            }
            if (total<=10) {
                stringList.add("Xỉu "+dice1+","+dice2+","+dice3);
                System.out.println("xỉu (Small)");
            } else {
                stringList.add("Tài "+dice1+","+dice2+","+dice3);
                System.out.println("Tài (Big)");
            }
            npc.textChatsTaiXiu = stringList;
        }
    }
    @Builder
    public Npc(int id, String name, short x, short y, ArrayList<Item> wearing) {
        setId(id > ID_ADD ? id : id + ID_ADD);
        setUsername(name);
        setRole((byte) 0);
        setX(x);
        setY(y);
        setWearing(wearing);
        textChats = new ArrayList<>();
        if(name == "Xac.Tai.Xiu"){
            autoChatBotTaiXiu.start();
        }else{

        }
        autoChatBot.start();
    }

    public void addChat(String chat) {
        textChats.add(chat);
    }


    @Override
    public void sendMessage(Message ms) {

    }
}