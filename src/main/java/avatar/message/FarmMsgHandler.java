package avatar.message;

import java.io.IOException;
import java.util.List;

import avatar.constants.NpcName;
import avatar.handler.NpcHandler;
import avatar.item.Item;
import avatar.model.Menu;
import avatar.model.Npc;
import avatar.network.Message;
import avatar.network.Session;
import avatar.service.FarmService;
import avatar.constants.Cmd;

public class FarmMsgHandler extends MessageHandler {

    private FarmService service;

    public FarmMsgHandler(Session client) {
        super(client);
        this.service = new FarmService(client);
    }

    @Override
    public void onMessage(Message mss) {
        if (mss == null) {
            return;
        }
        if (this.client.user == null) {
            return;
        }
        try {
            System.out.println("FarmMsgHandler: " + mss.getCommand()+" ( " +Cmd.CmdCode.get(mss.getCommand()) +")");
            switch (mss.getCommand()) {
                case Cmd.SET_BIG_FARM: {
                    this.service.setBigFarm(mss);
                    break;
                }
                case Cmd.GET_BIG_FARM: {
                    this.service.getBigFarm(mss);
                    break;
                }
                case Cmd.GET_IMAGE_FARM: {
                    this.service.getImageData();
                    break;
                }
                case Cmd.GET_TREE_INFO: {
                    this.service.getTreeInfo(mss);
                    break;
                }
                case Cmd.INVENTORY: {
                    this.service.getInventory(mss);
                    break;
                }
                case Cmd.JOIN: {
                    this.service.joinFarm(mss);
                    break;
                }
                case Cmd.GET_IMG_FARM: {
                    this.service.getImgFarm(mss);
                    break;
                }
                case Cmd.REQUEST_FRIENDLIST: {
                    this.service.serverDialog("Ăn trộm đang xây dựng");
                    break;
                }
                case Cmd.REQUEST_CHARGE_MONEY_INFO: {
                    this.service.serverDialog("Pay To Win hả");
                    break;
                }
                case Cmd.GET_CARD: {
                    this.service.serverDialog("LAI BUON chưa có vốn mua vui lòng quay lại sau");
                    break;
                }
                case Cmd.UPDATE_FARM_CATTLE: {
                    this.service.serverDialog("Mở rộng nông trại đang xây dựng vui lòng quay lại sau");
                    break;
                }
                case Cmd.UPDATE_FARM_FISH: {
                    this.service.serverDialog("Mở rộng nuôi cá đang xây dựng");
                    break;
                }
                case Cmd.COOKING: {
                    this.service.serverDialog("Hủy Nấu Ăn Nhanh tutu");
                    break;
                }
                case Cmd.COMMUNICATE: {
                   // this.service.serverDialog("lãi buôn");
                    NpcHandler.handlerCommunicate(Npc.ID_ADD+NpcName.LAI_BUON,this.client.user);
                    break;
                }
                default:
                    super.onMessage(mss);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
