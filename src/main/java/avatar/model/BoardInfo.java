package avatar.model;

import java.util.ArrayList;
import java.util.List;

public class BoardInfo
{
    public byte boardID;// id

    public byte nPlayer;//số người

    public byte maxPlayer;//max người

    public boolean isPass;

    public boolean isPlaying;

    public int money;

    public String strMoney;

    public List<User> lstUsers;

    public BoardInfo() {
        lstUsers = new ArrayList<>(); // Khởi tạo danh sách người dùng
    }

    public List<User> getLstUsers() {
        return lstUsers;
    }


    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    public synchronized void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    // Phương thức để cập nhật danh sách người chơi
    public void setLstUsers(List<User> lstUsers) {
        this.lstUsers = lstUsers;
        this.nPlayer = (byte)lstUsers.size(); // Cập nhật số người chơi
    }
}
