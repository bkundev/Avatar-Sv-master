
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
import static avatar.constants.NpcName.boss;

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
        User boss = z.find(npcId);
        if (npcIdCase > 1000 && npcIdCase<=9999)
        {
            if (boss.isDefeated()) {
                us.getAvatarService().serverDialog("boss đã chết");
                return;
            }
            us.updateXu(-10);
            us.updateXuKillBoss(+10);
            us.getAvatarService().updateMoney(0);
            List<User> lstUs = us.getZone().getPlayers();
            us.skillUidToBoss(lstUs,us.getId(),npcId,(byte)25,(byte)26);
            boss.updateHP(-10,(Boss)boss, us);

        } else if (npcIdCase >= 10000) {
            if(boss.isSpam()){
                us.getAvatarService().serverDialog("hộp này đã nhặt");
                return;
            }
            us.updateSpam(-1,(Boss)boss,us);
        }else {
            switch (npcIdCase) {
                case NpcName.bunma:
                    List<Menu> list1 = new ArrayList<>();
                    Menu Event = Menu.builder().name("Đổi Quà").action(() -> {
                        ShopEventHandler.displayUI(us, bunma,2040,3506,2620,2577,5539,2618, 2619, 3987,3455,3456,3457,4995,3988,3989,3990,5573,6772,6773,6774);
                    }).build();
                    list1.add(Event);
                    list1.add(Menu.builder().name("Góp ....")
                            .action(() -> {
                                GopDiemSK(us);
                            })
                            .build());
                    list1.add(Menu.builder().name("Thành tích bản thân")
                            .action(() -> {
                                us.getAvatarService().serverDialog("Bạn có đang "+us.getScores()+" điểm sự kiện");
                            })
                            .build());
                    list1.add(Menu.builder().name("Bảng xếp hạng kiếm xu từ boss")
                            .action(() -> {
                                List<User> topPlayers = us.getService().getTop10PlayersByXuFromBoss();
                                StringBuilder result = new StringBuilder();
                                int rank = 1; // Biến đếm để theo dõi thứ hạng

                                for (User player : topPlayers) {
                                    if (player.getXu_from_boss() > 0) {
                                        result.append(player.getUsername())
                                                .append(" Top ").append(rank).append(" : ")
                                                .append(player.getXu_from_boss())
                                                .append(" xu\n");
                                        rank++; // Tăng thứ hạng sau mỗi lần thêm người chơi vào kết quả
                                    }
                                }

                                us.getAvatarService().customTab("Top 10", result.toString());
                            })
                            .build());
                    list1.add(Menu.builder().name("Bảng xếp hạng thả pháo lượng")
                            .action(() -> {
                                List<User> topPlayers = us.getService().getTopPhaoLuong();
                                StringBuilder result = new StringBuilder();
                                int rank = 1; // Biến đếm để theo dõi thứ hạng

                                for (User player : topPlayers) {
                                    if (player.getXu_from_boss() > 0) {
                                        result.append(player.getUsername())
                                                .append(" Top ").append(rank).append(" : ")
                                                .append(player.getXu_from_boss())
                                                .append(" \n");
                                        rank++; // Tăng thứ hạng sau mỗi lần thêm người chơi vào kết quả
                                    }
                                }

                                us.getAvatarService().customTab("Top 10", result.toString());
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
                case NpcName.Vegeta:
                    List<Menu> lstVegeta = new ArrayList<>();
                    Menu vegenta = Menu.builder().name("Quà Thẻ VIP").action(() -> {
                        ShopEventHandler.displayUI(us, Vegeta,608,620,2090,6541,2052,2053);
                    }).build();
                    lstVegeta.add(Menu.builder().name("Quà Thẻ VIP cao cấp").action(() -> {
                        ShopEventHandler.displayUI(us, Vegeta, 2034,6161,4299,4300);
                    }).build());
                    lstVegeta.add(vegenta);
                    lstVegeta.add(Menu.builder().name("Thoát").id(npcId).build());
                    us.setMenus(lstVegeta);
                    us.getAvatarService().openMenuOption(npcId, 0, lstVegeta);
                    break;
                case NpcName.CHU_DAU_TU:
                    break;
                case NpcName.em_Thinh:{
                    List<Menu> list = new ArrayList<>();
                    List<Item> Items1 = new ArrayList<>();
                    Menu quaySo1 = Menu.builder().name("vật phẩm").menus(
                                    List.of(
                                            Menu.builder().name("demo item").action(() -> {
                                                for (int i = 2000; i < 6796; i++) {
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
                            .build();
                    list.add(quaySo1);
                    list.add(Menu.builder().name("Xem hướng dẫn").action(() -> {
                        System.out.println("Action for Xem hướng dẫn triggered");
                        us.getAvatarService().customTab("Hướng dẫn", "Để tham gia quay số bạn phải có ít nhất 5 lượng hoặc 25 ngàn xu trong tài khoản và 3 ô trống trong rương\n Bạn sẽ nhận được danh sách những món đồ đặc biệt mà bạn muốn quay. Những món đồ đặc biệt này bạn sẽ không thể tìm thấy trong bất cứ shop nào của thành phố.\n Sau khi chọn được món đồ muốn quay bạn sẽ bắt đầu chỉnh vòng quay để quay\n Khi quay bạn giữ phím 5 để chỉnh lực quay sau đó thả ra để bắt đầu quay\n Khi quay bạn sẽ có cơ hội trúng từ 1 đến 3 món quà\n Quà của bạn nhận được có thể là vật phẩm bất kì, xu, hoặc điểm kinh nghiệm\n Bạn có thể quay được những bộ đồ bán bằng lượng như đồ hiệp sĩ, pháp sư...\n Tuy nhiên vật phẩm bạn quay được sẽ có hạn sử dụng trong một số ngày nhất định.\n Nếu bạn quay được đúng món đồ mà bạn đã chọn thì bạn sẽ được sở hữu món đồ đó vĩnh viễn.\n Hãy thử vận may để sở hữa các món đồ cực khủng nào !!!");
                    }).build());
                    list.add(Menu.builder().name("Thoát").build());
                    us.setMenus(list);
                    us.getAvatarService().openMenuOption(npcId, 0, list);
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
                        //us.addExp(5);
                        us.getService().serverMessage("đang xây dựng");//Bạn nhận được 5 điểm exp + 1 thẻ quay số miễn phí");
                    }).build();
                    list.add(LAI_BUON);
                    list.add(Menu.builder().name("Xem hướng dẫn").action(() -> {
                        us.getAvatarService().customTab("Hướng dẫn", "Đăng nhập mỗi ngày để nhận quà.\nDùng điểm chuyên cần để nhận đucợ những món quà có giá trị trong tương lai");
                    }).build());
                    list.add(Menu.builder().name("Thoát").build());
                    us.setMenus(list);
                    us.getAvatarService().openMenuOption(npcId, 0, list);
                    break;
                }
                case NpcName.THO_CAU:
                    List<Menu> list = new ArrayList<>();
                    Menu thoCau = Menu.builder().name("Câu cá").action(() -> {
                        List<Item> Items1 = new ArrayList<>();
                        Item item = new Item(446,30,0);//câu vip
                        Items1.add(item);
                        Item item1 = new Item(460,2,0);//vé cau
                        Items1.add(item1);
                        Item item2 = new Item(448,30,1);//mồi
                        Items1.add(item2);
                        us.getAvatarService().openUIShop(npcId,"Trùm Câu Cá,",Items1);
                        us.getAvatarService().updateMoney(0);
                    }).build();
                    list.add(thoCau);
                    list.add(Menu.builder().name("Bán cá").action(() -> {
                        try {
                            sellFish(us);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).build());
                    list.add(Menu.builder().name("Xem hướng dẫn").action(() -> {
                        us.getAvatarService().customTab("Hướng dẫn", "Câu cá kiếm được nhiều xu bản auto lên thanhpholo.com");
                    }).build());
                    list.add(Menu.builder().name("Thoát").build());
                    us.setMenus(list);
                    us.getAvatarService().openMenuOption(npcId,0,  list);
                    break;
            }
        }
    }

    public static void sellFish(User us) throws IOException {
        int[] array = {2130,2131,2132,454,455,456,457};
        for (int  i = 0; i < array.length; i++) {
            Item item = us.findItemInChests(array[i]);
            if (item != null && item.getQuantity() > 0) {
                int sell = item.getQuantity()*item.getPart().getCoin();
                String message = String.format("Bạn vừa bán %d %s với giá %d x %d con = %d xu.", item.getQuantity(), item.getPart().getName(),item.getPart().getCoin(),item.getQuantity(), sell);
                us.removeItem(item.getId(), item.getQuantity());
                us.updateXu(+sell);
                us.getAvatarService().updateMoney(0);
                us.getAvatarService().SendTabmsg(message);
            }
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
