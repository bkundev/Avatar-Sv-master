package avatar.handler;

import avatar.constants.Cmd;
import avatar.constants.NpcName;
import avatar.item.Item;
import avatar.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigInteger;

import avatar.lucky.DialLucky;
import avatar.lucky.DialLuckyManager;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.util.concurrent.*;
import avatar.network.Message;
import avatar.play.Map;
import avatar.play.MapManager;
import avatar.play.NpcManager;
import avatar.play.Zone;
import avatar.server.ServerManager;
import avatar.server.UserManager;
import avatar.server.Utils;
import avatar.service.AvatarService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static avatar.constants.NpcName.*;

public class NpcHandler {

    public static void handleDiaLucky(User us, byte type) {
        DialLucky dl = DialLuckyManager.getInstance().find(type);
        if (dl != null) {
            if (dl.getType() == DialLuckyManager.MIEN_PHI) {
                Item itm = us.findItemInChests(593);
                if (itm == null || itm.getQuantity() <= 0) {
                    us.getAvatarService().serverDialog("Bạn không có Vé quay số miễn phí!");
                    return;
                }
            }
            if (dl.getType() == DialLuckyManager.XU) {
                if (us.getXu() < 15000) {
                    us.getAvatarService().serverDialog("Bạn không đủ xu!");
                    return;
                }
            }
            if (dl.getType() == DialLuckyManager.LUONG) {
                if (us.getLuong() < 5) {
                    us.getAvatarService().serverDialog("Bạn không đủ lượng!!");
                    return;
                }
            }
        }
        us.setDialLucky(dl);
        dl.show(us);
    }

    public void handlerTaiXiu(Message ms) throws IOException {

    }
    public static void handlerCommunicate(int npcId, User us) throws IOException {
        Zone z = us.getZone();
        if (z != null) {
            User u = z.find(npcId);
            if (u == null) {
                return;
            }
        } else {
            return;
        }
        int npcIdCase = npcId - Npc.ID_ADD;
        List<User> players = us.getZone().getPlayers();
        switch (npcIdCase) {
            case NpcName.SuKien:
                List<Menu> list1 = new ArrayList<>();
                Menu Event = Menu.builder().name("Đổi Quà").action(() -> {
                                            ShopEventHandler.displayUI(us, 3506,2620,2577,5539,2618, 2619, 3987,3455,3456,3457,4995,3988,3989,3990,5573);
                                        }).build();
                list1.add(Event);
                list1.add(Menu.builder().name("Góp dây tơ")
                        .action(() -> {
                            GopDiemSK(us);
                        })
                        .build());
                list1.add(Menu.builder().name("Thành tích bản thân")
                        .action(() -> {
                            us.getAvatarService().serverDialog("Bạn có đang "+us.getScores()+" điểm sự kiện");
                        })
                        .build());
                list1.add(Menu.builder().name("Xem hướng dẫn")
                        .action(() -> {
                            us.getAvatarService().customTab("Hướng dẫn", "-Từ Ngày");
                        })
                        .build());
                list1.add(Menu.builder().name("Thoát").id(npcId).build());
                us.setMenus(list1);
                us.getAvatarService().openMenuOption(npcId, 0, list1);
                break;
            case NpcName.CHU_DAU_TU:
                break;
            case boss:{
                List<Menu> list = List.of(
                        Menu.builder().name("damage").action(() -> {
                           // List<User> players = us.getZone().getPlayers();
                            //for (int i = 0; i < players.size(); i++) {
                             //   if(players.get(i).getUsername() == "BOSS")
                              //  {
                                    //List<Integer> availableItems = Arrays.asList(2401, 4552, 6314, 6432);
                                   // Utils random = null;
                                   // int randomItemId = availableItems.get(random.nextInt(availableItems.size()));
                                    //Map m = MapManager.getInstance().find(11);
                                    //Boss boss1 = (Npc) players.get(i);

                                    //bo(npc,10);
                                    ////us.skillUidToBoss(players,us.getId(),npcId, (byte) 25, (byte) 26);
                                    //int bosItem = npc.getWearing().get(0).getId();
                                    //switch (bosItem) {
                                     //   case 2401:
                                           // npc.skill(npc,(byte)27);
                                        //    break;
                                      //  case 4552:
                                      //      npc.skillUidToBoss(players,npc.getId(), us.getId(), (byte) 25, (byte) 26);
                                       //     break;
                                      //  case 6314:
                                        //    npc.skillUidToBoss(players,npc.getId(), us.getId(), (byte) 40, (byte) 41);
                                        //    break;
                                       // case 6432:
                                         //   npc.skillUidToBoss(players,npc.getId(), us.getId(), (byte) 42, (byte) 43);
                                        //    break;
                                    //}
                                    //if (npc.getGlobalHp()<=0){
                                   //     npc.skill(npc,(byte)45);
                                       // List<String> chatMessages = Arrays.asList(
                                        //        "tạm biệt mấy ông chau",
                                       //         "ta chuyển sinh đây"
                                       // );
                                        //npc.setTextChats(chatMessages);
                                        //try {
                                        //    Thread.sleep(3000);
                                      //  } catch (InterruptedException e) {
                                      //      throw new RuntimeException(e);
                                      //  }
                                       // npc.getZone().leave(npc);
                                       // List<Zone> zones = m.getZones();
                                       // random = null;
                                        //Zone randomZone = zones.get(random.nextInt(zones.size())); // Chọn ngẫu nhiên một khu vực từ danh sách
                                       // Npc zomber = Npc.builder()
                                       //         .id(Npc.ID_ADD + boss) // ID ngẫu nhiên cho NPC
                                       //         .name("boss")
                                        //        .wearing(new ArrayList<>())
                                         //       .build();
                                        //zomber.addItemToWearing(new Item(randomItemId));; // Thêm một item mặc định cho NPC
                                       // zomber.addChat("Tao bất tử OK"); // Thêm một thông điệp chat mặc định
                                       // NpcManager.getInstance().add(zomber); // Thêm NPC vào quản lý NPC
                                        // Ngẫu nhiên vị trí xuất hiện trong khu vực
                                        //short randomX = (short) 250;
                                       // short randomY = (short) 50;
                                        // Nhập NPC vào khu vực với vị trí ngẫu nhiên

                                        //randomZone.enter(zomber, randomX, randomY);
                                       // System.out.println("khu :"+randomZone.getId());

                                //}
                        }).build()
                );
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "boss", "hp tao:");
                break;
            }
            //npc binz,admin....
//            case NpcName.Tai_Xiu: {
//                List<Menu> list = new ArrayList<>();
//                Menu taiXiu = Menu.builder().name("Chơi Tài Xỉu").menus(
//                                List.of(
//                                        Menu.builder().name("Cược Tài").action(() -> {
//                                            us.getAvatarService().sendTextBoxPopup(us.getId(), 12, "Nhập Số Tiền Cược Tài", 1);
//                                        }).build(),
//                                        Menu.builder().name("Cược Xỉu").action(() -> {
//                                            us.getAvatarService().sendTextBoxPopup(us.getId(), 13, "Nhập Số Tiền Cược Xỉu", 1);
//                                        }).build(),
//                                        Menu.builder().name("Tất Tay tài").action(() -> {
//                                            us.getAvatarService().sendTextBoxPopup(us.getId(), 14, "Nhập Bừa 1 Số Để Tất Tay Tài 100.000.000 Xu", 1);
//                                        }).build(),
//                                        Menu.builder().name("Tất Tay xỉu").action(() -> {
//                                            us.getAvatarService().sendTextBoxPopup(us.getId(), 15, "Nhập Bừa 1 Số Để Tất Tay Xỉu 100.000.000 Xu", 1);
//                                        }).build()
//                                ))
//                        .id(npcId)
//                        .npcName("Không Tài Thì Xỉu")
//                        .npcChat("xuc xac nao")
//                        .build();
//                list.add(taiXiu);
//                list.add(Menu.builder().name("Lịch sử").action(() -> {
//                    us.getAvatarService().customTab("Lịch sử kết quả", "lịch sử kết quả và lịch sử cược? co cai db chưa làm !!!");
//                }).build());
//                list.add(Menu.builder().name("Hướng dẫn").action(() -> {
//                    us.getAvatarService().customTab("Hướng dẫn", "cứ ôn in bừa");
//                }).build());
//                list.add(Menu.builder().name("Thoát").build());
//                us.setMenus(list);
//                us.getAvatarService().openUIMenu(npcId, 0, list, "tài xỉu", "");
//                break;
//            }
//            case NpcName.binzoet:{
//
//                List<Menu> list = new ArrayList<>();
//                list.add(Menu.builder().name("reset xu luong").action(() -> {
//                    //us.setXu(999999999);
//                    //us.setLuong(9999);
//                    us.getAvatarService().serverDialog("reset cc");
//                }).build());
//                list.add(Menu.builder().name("Thoát").build());
//                us.setMenus(list);
//                us.getAvatarService().openUIMenu(npcId, 0, list, "", "");
//                break;
//            }
            case NpcName.em_Thinh:{
                List<Menu> list = new ArrayList<>();
                List<Item> Items1 = new ArrayList<>();
                Menu quaySo1 = Menu.builder().name("vật phẩm").menus(
                                List.of(
                                        Menu.builder().name("demo item").action(() -> {
                                            for (int i = 2000; i < 6676; i++) {
                                                Item item = new Item((short) i);
                                                Items1.add(item);
                                            }
                                            us.getAvatarService().openUIShop(-49,"em.Thinh",Items1);
                                        }).build()
                                ))
                        .id(npcId)
                        .npcName("donate đi")
                        .npcChat("show Item")
                        .build();
                list.add(quaySo1);
                list.add(Menu.builder().name("Hướng dẫn").action(() -> {
                    us.getAvatarService().customTab("Hướng dẫn", "hãy nạp lần đầu để mở khóa mua =)))");
                }).build());
                list.add(Menu.builder().name("Thoát").build());
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "donate đi", "");
                break;
            }
            case NpcName.QUAY_SO: {
                List<Menu> list = new ArrayList<>();
                Menu quaySo1 = Menu.builder().name("Quay số").menus(
                                List.of(
                                        Menu.builder().name("5 lượng").action(() -> {
                                            System.out.println("Action for 5 lượng triggered");
                                            handleDiaLucky(us, DialLuckyManager.LUONG);
                                        }).build(),
                                        Menu.builder().name("15.000 xu").action(() -> {
                                            System.out.println("Action for 15.000 xu triggered");
                                            handleDiaLucky(us, DialLuckyManager.XU);
                                        }).build(),
                                        Menu.builder().name("Q.S miễn phí").action(() -> {
                                            System.out.println("Action for Q.S miễn phí triggered");
                                            handleDiaLucky(us, DialLuckyManager.MIEN_PHI);
                                        }).build(),
                                        Menu.builder().name("Thoát").action(() -> {
                                            System.out.println("Exit menu triggered");
                                        }).build()
                                ))
                        .id(npcId)
                        .npcName("Quay số")
                        .npcChat("Quay số may mắn đây")
                        .build();
                list.add(quaySo1);
                list.add(Menu.builder().name("Xem hướng dẫn").action(() -> {
                    System.out.println("Action for Xem hướng dẫn triggered");
                    us.getAvatarService().customTab("Hướng dẫn", "Để tham gia quay số bạn phải có ít nhất 5 lượng hoặc 25 ngàn xu trong tài khoản và 3 ô trống trong rương\n Bạn sẽ nhận được danh sách những món đồ đặc biệt mà bạn muốn quay. Những món đồ đặc biệt này bạn sẽ không thể tìm thấy trong bất cứ shop nào của thành phố.\n Sau khi chọn được món đồ muốn quay bạn sẽ bắt đầu chỉnh vòng quay để quay\n Khi quay bạn giữ phím 5 để chỉnh lực quay sau đó thả ra để bắt đầu quay\n Khi quay bạn sẽ có cơ hội trúng từ 1 đến 3 món quà\n Quà của bạn nhận được có thể là vật phẩm bất kì, xu, hoặc điểm kinh nghiệm\n Bạn có thể quay được những bộ đồ bán bằng lượng như đồ hiệp sĩ, pháp sư...\n Tuy nhiên vật phẩm bạn quay được sẽ có hạn sử dụng trong một số ngày nhất định.\n Nếu bạn quay được đúng món đồ mà bạn đã chọn thì bạn sẽ được sở hữu món đồ đó vĩnh viễn.\n Hãy thử vận may để sở hữa các món đồ cực khủng nào !!!");
                }).build());
                list.add(Menu.builder().name("Thoát").build());
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "quay số", "Vòng quay may mắn nhận những vật phẩm quí hiếm đây! Mại dô!");
                break;
            }
            case NpcName.THO_KIM_HOAN: {
                List<Menu> list = new ArrayList<>();
                String npcName = "Thợ KH";
                String npcChat = "Muốn nâng cấp đồ thì vào đây";
                Menu upgrade = Menu.builder().name("Nâng cấp").id(npcId).npcName(npcName).npcChat(npcChat).menus(
                                List.of(
                                        Menu.builder().name("Nâng cấp xu").id(npcId).npcName(npcName).npcChat(npcChat)
                                                .menus(listItemUpgrade(npcId, us, BossShopHandler.SELECT_XU))
                                                .build(),
                                        Menu.builder().name("Nâng cấp lượng").id(npcId).npcName(npcName).npcChat(npcChat)
                                                .menus(listItemUpgrade(npcId, us, BossShopHandler.SELECT_LUONG))
                                                .id(npcId)
                                                .build(),
                                        Menu.builder().name("Thoát").id(npcId).build()
                                )
                        )
                        .build();
                list.add(upgrade);
                list.add(Menu.builder().name("Xem hướng dẫn")
                        .action(() -> {
                            us.getAvatarService().customTab("Hướng dẫn", "Nâng thì nâng không nâng thì cút!");
                        })
                        .build());
                list.add(Menu.builder().name("Thoát").id(npcId).build());
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, npcName, npcChat);
                break;
            }
            case NpcName.LAI_BUON: {
                List<Menu> list = new ArrayList<>();
                Menu LAI_BUON = Menu.builder().name("Điểm Danh").action(() -> {
                    Item item = new Item(593, -1, 1);
                    //us.addItemToChests(item);
                    us.addExp(5);
                    us.getService().serverMessage("Bạn nhận được 5 điểm exp + 1 thẻ quay số miễn phí");
                }).build();
                list.add(LAI_BUON);
                list.add(Menu.builder().name("Xem hướng dẫn").action(() -> {
                    us.getAvatarService().customTab("Hướng dẫn", "Đăng nhập mỗi ngày để nhận quà.\nDùng điểm chuyên cần để nhận đucợ những món quà có giá trị trong tương lai");
                }).build());
                list.add(Menu.builder().name("Thoát").build());
                us.setMenus(list);
                us.getAvatarService().openUIMenu(npcId, 0, list, "Lãi Buôn", "Chào Các Cư Dân Chăm Chỉ ");
                break;
            }
            case NpcName.THO_CAU:
                us.getAvatarService().serverDialog("Chức năng đang được xây dựng, vui lòng thử lại sau");
            break;
        }
    }
    public static void GopDiemSK(User us){
        java.util.Map<Integer, Integer> itemsToProcess = new HashMap<>();
        itemsToProcess.put(3085, 1);
        itemsToProcess.put(3086, 2);
        itemsToProcess.put(3087, 3);
        int addscores = 0;
// Lặp qua từng cặp ID và số lượng
        for (java.util.Map.Entry<Integer, Integer> entry : itemsToProcess.entrySet()) {
            int itemId = entry.getKey();
            int scores = entry.getValue();
            Item item = us.findItemInChests(itemId);
            if (item != null && item.getQuantity() > 0) {
                addscores += item.getQuantity()*scores;
                us.updateScores(+addscores);
                us.removeItem(itemId, item.getQuantity());

            }
        }
        if(addscores > 0){
            us.getAvatarService().serverDialog("Bạn đã đổi thành công : " + addscores + " điểm sự kiện");
        }else {
            us.getAvatarService().serverDialog("Bạn không đủ dây tơ để góp");
        }
    }

    public static List<Menu> listItemUpgrade(int npcId, User us, byte type) {
        String npcName = "Thợ KH";
        String npcChat = "Muốn đồ đang mặc đẹp hơn không? Ta có thể giúp bạn đấy";
        return List.of(
                Menu.builder().name("Quà cầm tay").id(npcId).npcName(npcName).npcChat(npcChat)
                        .menus(List.of(
                                        Menu.builder().name("Hoa hồng phong thần").action(() -> {
                                            BossShopHandler.displayUI(us, type, 5321, 5322, 5323);
                                        }).build(),
                                        Menu.builder().name("Gậy thả thính mê hoặc").action(() -> {
                                            BossShopHandler.displayUI(us, type, 3507, 4218);
                                        }).build(),
                                        Menu.builder().name("Chong chóng thiên thần").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2238, 2239, 2274, 2275, 2404);
                                        }).build(),
                                        Menu.builder().name("Cục vàng huyền thoại").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2217, 2218, 2219, 2220, 2221, 2222, 2223);
                                        }).build(),
                                        Menu.builder().name("Bông hoa cổ tích").action(() -> {
                                            BossShopHandler.displayUI(us, type, 6212, 6213, 6214);
                                        }).build()
                                )
                        )
                        .build(),
                Menu.builder().name("Nón").npcName(npcName).npcChat(npcChat).menus(List.of(
                                Menu.builder().name("Tôi thấy hoa vàng trên cỏ xanh").action(() -> {
                                    BossShopHandler.displayUI(us, type, 3266, 3267, 3268, 3269, 3954);
                                }).build(),
                                Menu.builder().name("Vương miện phép màu").action(() -> {
                                    BossShopHandler.displayUI(us, type, 3422, 3423, 3639, 3640);
                                }).build(),
                                Menu.builder().name("Mũ ảo thuật tinh anh").action(() -> {
                                    BossShopHandler.displayUI(us, type, 2899, 2900, 2901, 2902, 2903, 3037, 3038, 3039);
                                }).build()
                        ))
                        .build(),
                Menu.builder().name("Trang phục").npcName(npcName).npcChat(npcChat)
                        .menus(
                                List.of(
                                        Menu.builder().name("Danh gia vọng tộc").action(() -> {
                                            BossShopHandler.displayUI(us, type, 5392, 5393);
                                        }).build(),
                                        Menu.builder().name("Nữ hoàng sương mai").action(() -> {
                                            BossShopHandler.displayUI(us, type, 5054, 5055);
                                        }).build(),
                                        Menu.builder().name("Bá tước bóng đêm").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2876, 2877);
                                        }).build(),
                                        Menu.builder().name("Napoleon").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2231, 2232);
                                        }).build(),
                                        Menu.builder().name("Elizabeth").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2229, 2230);
                                        }).build()
                                )
                        )
                        .build(),
                Menu.builder().name("Cánh").npcName(npcName).npcChat(npcChat)
                        .menus(
                                List.of(
                                        Menu.builder().name("Cánh tiểu thần phong linh").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2419, 2482, 2483, 2505, 2506, 5252, 5253);
                                        }).build(),
                                        Menu.builder().name("Cửu vỹ hồ ly thần thoại").action(() -> {
                                            BossShopHandler.displayUI(us, type, 4333, 4910, 4911, 4912, 4913, 4914, 4915, 4916, 4334, 4889);
                                        }).build(),
//                                        Menu.builder().name("Cánh băng hoả thần thoại").action(() -> {
//                                            BossShopHandler.displayUI(us, type, 3448, 4057, 4375);
//                                        }).build(),
//                                        Menu.builder().name("Cánh hoả thần").action(() -> {
//                                            BossShopHandler.displayUI(us, type, 4311, 4312, 4313);
//                                        }).build(),
                                        Menu.builder().name("Cánh thiên sứ tình yêu").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2148, 2149, 2150, 2151, 2152, 3637);
                                        }).build(),
                                        Menu.builder().name("Cánh thiên sứ").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2142, 2143, 2144, 2145, 2146, 3635);
                                        }).build(),
                                        Menu.builder().name("Cánh địa ngục hắc ám").action(() -> {
                                            BossShopHandler.displayUI(us, type, 3529, 3530, 3531, 3532);
                                        }).build(),
                                        Menu.builder().name("Cánh cổng địa ngục").action(() -> {
                                            BossShopHandler.displayUI(us, type, 3522, 3523, 3524, 3525, 3526, 3527);
                                        }).build(),
//                                        Menu.builder().name("Cánh bướm đêm huyền thoại").action(() -> {
//                                            BossShopHandler.displayUI(us, type, 3366, 3379);
//                                        }).build(),
//                                        Menu.builder().name("Cánh băng giá huyền thoại").action(() -> {
//                                            BossShopHandler.displayUI(us, type, 3365, 3378);
//                                        }).build(),
                                        Menu.builder().name("Cánh phép màu ước mơ").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2793, 2794, 2795, 2796);
                                        }).build(),
                                        Menu.builder().name("Cánh blue vững vàng").action(() -> {
                                            BossShopHandler.displayUI(us, type, 2788, 2789, 2790, 2791);
                                        }).build()
                                )
                        )
                        .build(),
                Menu.builder().name("Thú cưng").npcName(npcName).npcChat(npcChat)
                        .menus(List.of(
//                                Menu.builder().name("Lang thần lãnh nguyên").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 5517, 5518);
//                                }).build(),
//                                Menu.builder().name("Thiên thần hồ điệp").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 5486, 5487);
//                                }).build(),
                                Menu.builder().name("Thiên thần hộ mệnh toàn năng").action(() -> {
                                    BossShopHandler.displayUI(us, type, 5224, 5225, 5226);
                                }).build(),
//                                Menu.builder().name("Tiểu tiên bướm").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 4305, 5058);
//                                }).build(),
//                                Menu.builder().name("Cáo tuyết cửu vỹ").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 4904, 4905);
//                                }).build(),
//                                Menu.builder().name("Ma vương").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 4096, 4731);
//                                }).build(),
//                                Menu.builder().name("Cửu vỹ hồ ly").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 4724, 4728, 4729);
//                                }).build(),
//                                Menu.builder().name("Lợn lém lỉnh").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 4376);
//                                }).build(),
//                                Menu.builder().name("Tuần lộc tinh anh").action(() -> {
//                                    BossShopHandler.displayUI(us, type, 4323, 4324);
//                                }).build(),
                                Menu.builder().name("Bay nax 2.0").action(() -> {
                                    BossShopHandler.displayUI(us, type, 4079, 4080);
                                }).build(),
                                Menu.builder().name("Phương hoàng lửa").action(() -> {
                                    BossShopHandler.displayUI(us, type, 3668, 3771, 3772, 3773, 3854);
                                }).build(),
                                Menu.builder().name("King Kong").action(() -> {
                                    BossShopHandler.displayUI(us, type, 3744);
                                }).build(),
                                Menu.builder().name("Kỳ lân truyền thuyết").action(() -> {
                                    BossShopHandler.displayUI(us, type, 2726, 2727, 2728, 2729, 2730);
                                }).build()
                        ))
                        .build()
        );
    }

    public static void handlerAction(User us, int npcId, byte menuId, byte select) throws IOException {
        Zone z = us.getZone();
        if (z != null) {
            User u = z.find(npcId);
            if (u == null) {
                return;
            }
        } else {
            return;
        }
//        if (menuId == 0 && select == 0) {
//            // Trường hợp đặc biệt khi lần đầu mở menu
//            System.out.println("Initial menu open, displaying options without performing action.");
//            us.getAvatarService().openMenuOption(npcId, menuId,us.getMenus());
//            return;
//        }
//        int npcIdCase = npcId - 2000000000;
        List<Menu> menus = us.getMenus();
        if (menus != null && select < menus.size()) {
            Menu menu = menus.get(select);
            if (menu.isMenu()) {
                us.setMenus(menu.getMenus());
                us.getAvatarService().openUIMenu(npcId, menuId + 1, menu.getMenus(), menu.getNpcName(), menu.getNpcChat());
            } else if (menu.getAction() != null) {
                menu.perform();
            } else {
                switch (menu.getId()) {

                }
            }
            return;
        }
    }
}
