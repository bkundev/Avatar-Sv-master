package avatar.message;

import avatar.constants.Cmd;
import avatar.network.Message;
import avatar.network.Session;
import avatar.server.ServerManager;
import avatar.service.ParkService;

public class ParkMsgHandler extends MessageHandler {

    private ParkService service;

    public ParkMsgHandler(Session client) {
        super(client);
        this.service = new ParkService(client);
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
            switch (mss.getCommand()) {
                case Cmd.AVATAR_REQUEST_ADD_FRIEND:
                    this.client.getAvatarService().handleAddFriendRequest(mss);
                    break;
                case Cmd.CHAT_TO:
                    this.client.getAvatarService().chatToUser(mss);
                    break;
                case Cmd.AVATAR_JOIN_PARK:
                    ServerManager.joinAreaMessage(this.client.user, mss);
                    break;
                case Cmd.MOVE_PARK:
                    this.client.user.move(mss);
                    break;

                case Cmd.CHAT_PARK:
                    this.client.user.chat(mss);
                    break;

                case Cmd.AVATAR_FEEL:
                    this.client.user.doAvatarFeel(mss);
                    break;

                case Cmd.REQUEST_DYNAMIC_PART:
                    this.client.getAvatarService().requestPartDynaMic(mss);
                    break;
                case Cmd.REQUEST_JOIN_ANY:  //map trong map hawai
                    this.client.getAvatarService().serverDialog("Công trình Hawai trong đang xây dựng");
                    break;
                case Cmd.START_CAU_CA:  //map trong map hawai
                    this.client.getAvatarService().serverDialog("Cá còn nhỏ chưa câu được vui lòng quay lại sau");
                    break;
                default:
                    System.out.println("ParkMsgHandler: " + mss.getCommand() +"( "+ Cmd.CmdCode.get(mss.getCommand())  +" )");
                    super.onMessage(mss);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
