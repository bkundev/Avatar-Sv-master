package avatar.message.minigame;

import avatar.constants.Cmd;
import avatar.network.Message;
import avatar.network.Session;
import avatar.service.Service;

import java.io.DataOutputStream;
import java.io.IOException;

public class BauCuaMsgHandler extends Service {
    public BauCuaMsgHandler(Session cl) {
        super(cl);
    }



    public void joinCasino(Message ms) throws IOException {
        this.session.sendMessage(ms);
    }
}
