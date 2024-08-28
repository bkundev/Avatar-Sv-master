/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avatar.server;

import avatar.model.Boss;
import avatar.model.User;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kitakeyos - Hoàng Hữu Dũng
 */
public class UserManager {

    private static final UserManager instance = new UserManager();

    public static UserManager getInstance() {
        return instance;
    }
    
    public static final List<User> users = new LinkedList<>();
    public static final List<Boss> boss = new LinkedList<>();
    public void add(User us) {
        synchronized(users) {
            users.add(us);
        }
    }
    
    public void remove(User us) {
        synchronized(users) {
            users.remove(us);
        }
    }
    
    public User find(int id) {
        synchronized(users) {
            for (User us : users) {
                if (us.getId() == id) {
                    return us;
                }
            }
        }
        return null;
    }
    public void removeBoss(Boss us) {
        synchronized (boss) {
            boss.remove(us);
        }
    }
    public Boss findBoss(int id) {
        synchronized(boss) {
            for (Boss boss : boss) {
                if (boss.getId() == id) {
                    return boss;
                }
            }
        }
        return null;  // Trả về null nếu không tìm thấy Boss nào với ID đó
    }
}
