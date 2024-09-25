package avatar.server;
import avatar.message.CasinoMsgHandler;
import avatar.model.BoardInfo;
import avatar.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class BoardManager {

    private static final BoardManager instance = new BoardManager();

    public static BoardManager getInstance() {
        return instance;
    }

    public static final List<BoardInfo> boardList = new ArrayList<>();

    public BoardManager() {
    }


    public BoardInfo find(byte id) {
        synchronized (this) {
            for (BoardInfo board : boardList) {
                if (board.boardID == id) {
                    return board;
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    public void increaseMaxPlayer(int id,User user) {
        synchronized (this) {
            for (BoardInfo board : boardList) {
                if (board.boardID == id) {
                   board.nPlayer += 1;
                   board.lstUsers.add(user);
                }
            }
        }
    }

    public void initBoards() {
        for (int i = 0; i < 2; i++) {
            BoardInfo board = new BoardInfo();
            board.boardID = (byte) i;
            board.nPlayer = 80;//số ng chia 16
            board.maxPlayer = 5; // Đặt maxPlayer là 5
            board.isPass = false;
            board.isPlaying = false;
            board.money = 0;
            board.strMoney = "1000";
            boardList.add(board);
            System.out.println("create board : " + board.boardID);
        }
    }

}
