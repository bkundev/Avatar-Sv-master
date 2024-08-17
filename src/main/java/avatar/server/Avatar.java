package avatar.server;

import avatar.model.Fish;
import avatar.model.GameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class
Avatar {

    private static Thread T;

    public static void main(String[] args) throws InterruptedException {
        //Utils.decodeMapItemFile();
//        Fish selector = new Fish();
//
//        // Mô phỏng lựa chọn trong 2 giờ
//        int attempts = 0;
//        int count457 = 0;
//        long startTime = System.currentTimeMillis();
//
//        while ((System.currentTimeMillis() - startTime) < 2 * 60) { // 2 giờ tính bằng milliseconds
//            int selectedID = selector.getRandomFishID();
//            if (selectedID == 457) {
//                count457++;
//                if (count457 >=3) {Thread.sleep(100000);}
//            }
//            attempts++;
//            System.out.println("ID 457 được chọn " + count457 + " lần trong tổng số " + attempts + " lượt chọn.");
//        }
        Avatar.start();
    }

    public static void start() {

        //getPart insert sql item

        //for (int i = 2000; i < 6676; i++) {
        //   byte[] dat = Avatar.getFile("res/p/part_"+i+".dat");
        //    Utils.decodeItemDataFile(dat,true);
        // }

        //

        T = new Thread(() -> {
            System.out.println("     _                      _                      ____                                      \n    / \\    __   __   __ _  | |_    __ _   _ __    / ___|    ___   _ __  __   __   ___   _ __ \n   / _ \\   \\ \\ / /  / _` | | __|  / _` | | '__|   \\___ \\   / _ \\ | '__| \\ \\ / /  / _ \\ | '__|\n  / ___ \\   \\ V /  | (_| | | |_  | (_| | | |       ___) | |  __/ | |     \\ V /  |  __/ | |   \n /_/   \\_\\   \\_/    \\__,_|  \\__|  \\__,_| |_|      |____/   \\___| |_|      \\_/    \\___| |_|   \n                                                                                             ");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown Server!");
                ServerManager.stop();
            }));
            ServerManager.init();
            ServerManager.start();
        });
        T.start();
    }

    /**
     * convert .png image to byteArray
     * @param url -> img path
     * @return -> arr image
     */
    public static byte[] getFile(String url) {
        try {

            FileInputStream fis = new FileInputStream(url);
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch (IOException e) {
            return null;
        }
    }

    public static int getFileSize(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            int size = fis.available();
            fis.close();
            return size;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void saveFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
