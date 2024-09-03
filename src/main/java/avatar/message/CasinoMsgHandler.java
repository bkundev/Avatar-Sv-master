package avatar.message;

import java.io.IOException;
import java.util.List;

import avatar.constants.NpcName;
import avatar.handler.NpcHandler;
import avatar.item.Item;
import avatar.message.minigame.BauCuaMsgHandler;
import avatar.model.Menu;
import avatar.model.Npc;
import avatar.network.Message;
import avatar.network.Session;
import avatar.service.FarmService;
import avatar.constants.Cmd;

public class CasinoMsgHandler extends MessageHandler {
    private BauCuaMsgHandler service;

    public CasinoMsgHandler(Session client) {
        super(client);
        this.service = new BauCuaMsgHandler(client);
    }

    @Override
    public void onMessage(Message mss) {
        try {
            System.out.println("casino mess: " + mss.getCommand());
            switch (mss.getCommand()) {
                case 61:
                    byte subCommand = mss.reader().readByte();
                    switch (subCommand) {
                        case 22:
                            service.joinCasino(mss);
                            break;
                        default:
                            return;
                    }
                    break;
                default:
                    // Xử lý các trường hợp khác
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
