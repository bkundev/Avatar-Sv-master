package avatar.service;

import avatar.lib.KeyValue;
import avatar.model.GameData;
import avatar.model.ImageInfo;
import avatar.constants.Cmd;
import avatar.model.User;

import java.io.EOFException;
import java.util.Vector;
import avatar.server.Avatar;
import java.io.IOException;
import java.io.DataOutputStream;
import avatar.network.Message;
import avatar.network.Session;
import java.util.List;
import org.apache.log4j.Logger;

public class FarmService extends Service {

    private static final Logger logger = Logger.getLogger(Service.class);

    public FarmService(Session cl) {
        super(cl);
    }

    int land = 47;
    //trồng cây
    public void plandSeed(Message ms) throws IOException {
        int idUser = ms.reader().readInt();
        int indexCell = ms.reader().readByte();
        int idSeed = ms.reader().readByte();
        ms = new Message(Cmd.PLANT_SEED);
        DataOutputStream ds = ms.writer();
        ds.writeInt(idUser);
        ds.writeInt(indexCell);
        ds.writeInt(3);
        ds.flush();
        this.session.sendMessage(ms);
    }


    //thu hoạch
    public void treeHarvest(Message ms) throws IOException {
        byte indexCell3 = ms.reader().readByte();
        short number2 = ms.reader().readShort();
        ms = new Message(Cmd.TREE_HARVEST);
        DataOutputStream ds = ms.writer();
        ds.writeByte(indexCell3);
        ds.writeShort(number2);
        ds.flush();
        this.session.sendMessage(ms);
    }

   // mở ô đất
    public void doRequestslot(Message ms) throws IOException {
        int id = ms.reader().readInt();//id user
        ms = new Message(Cmd.REQUEST_SLOT);
        DataOutputStream ds = ms.writer();
        ds.writeUTF("Bạn có muốn mở ô đất @ với giá @ xu hoặc @ lượng không ?");
        ds.flush();
        this.session.sendMessage(ms);
    }


//mở ô đất
    public void openLand(Message ms) throws IOException {
        int id = ms.reader().readInt();//id farm
        byte typebuy = ms.reader().readByte();//id user

        ms = new Message(Cmd.OPEN_LAND);
        DataOutputStream ds = ms.writer();
        ds.writeInt(id);
        ds.writeInt(1);
        ds.writeByte(typebuy);
        ds.writeUTF("đã bu");
        ds.writeInt(2);
        ds.writeInt(3);
        ds.writeInt(4);
        ds.flush();
        land++;
        this.session.sendMessage(ms);
    }









    public void setBigFarm(Message ms) throws IOException {


        ms = new Message(51);
        DataOutputStream ds = ms.writer();
        int[] images = {99, 206};
        ds.writeByte(images.length);
        for (int i = 0; i < images.length; ++i) {
            ds.writeShort(i);
            ds.writeShort(images[i]);
        }
        ds.writeInt(15378);
        ds.writeInt(62724);
        ds.flush();
        this.session.sendMessage(ms);


        //apk goc
//        ms = new Message(51);
//        DataOutputStream ds = ms.writer();
//        short b19 = 2;
//        ds.writeByte(b19);
//        short[] array2 = {0, 1};
//        for (short value : array2) {
//            ds.writeShort(value);
//        }
//        short[] array3 = {66, 6};
//        for (short value : array3) {
//            ds.writeShort(value);
//        }
//        ds.writeInt(15378);
//        ds.writeInt(59669);
//        ds.flush();
//        this.session.sendMessage(ms);
    }

    public void getBigFarm(Message ms) throws IOException {
        short imageID = ms.reader().readShort();
        String folder = this.session.getResourcesPath() + "bigFarm/";
        byte[] dat = Avatar.getFile(folder + imageID + ".png");
        if (dat == null) {
            return;
        }
        ms = new Message(54);
        DataOutputStream ds = ms.writer();
        ds.writeShort(imageID);
        ds.writeShort(dat.length);
        ds.writeShort(dat.length);
        for (int i = 0; i < dat.length; ++i) {
            ds.writeByte(dat[i]);
        }
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getImageData() {
        try {
            List<ImageInfo> imageInfos = GameData.getInstance().getFarmImageDatas();
            Message ms = new Message(Cmd.GET_IMAGE_FARM);
            DataOutputStream ds = ms.writer();
            ds.writeShort(imageInfos.size());
            for (ImageInfo imageInfo : imageInfos) {
                ds.writeShort(imageInfo.getId());
                ds.writeShort(imageInfo.getBigImageID());
                ds.writeByte(imageInfo.getX());
                ds.writeByte(imageInfo.getY());
                ds.writeByte(imageInfo.getW());
                ds.writeByte(imageInfo.getH());
            }
            ds.flush();
            this.sendMessage(ms);

        } catch (IOException e) {
            logger.debug("getImageData: " + e.getMessage());
        }
    }

    public void getTreeInfo(Message ms) throws IOException {
        byte[] dat = Avatar.getFile("res/data/farm_info.dat");
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.GET_TREE_INFO);
        DataOutputStream ds = ms.writer();
        ds.write(dat);
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getInventory(Message ms) throws IOException {
        User us = session.user;
        Vector<KeyValue<Integer, Integer>> hatgiong = new Vector<>();
        hatgiong.add(new KeyValue(34, 10));
        Vector<KeyValue<Integer, Integer>> nongsan = new Vector<>();
        nongsan.add(new KeyValue(9, 23684));//23684 kho hàng(nông sản) hoa hướng dương
        nongsan.add(new KeyValue(50, 4000));//trứng gà
        Vector<KeyValue<Integer, Integer>> phanbon = new Vector<>();
        phanbon.add(new KeyValue<Integer, Integer>(118, 70));
        phanbon.add(new KeyValue<Integer, Integer>(119, 78));//kho giống(phân bón thức ăn)
        Vector<KeyValue<Integer, Integer>> nongsandacbiet = new Vector<>();
        nongsandacbiet.add(new KeyValue(255, 20));//thit ca
        nongsandacbiet.add(new KeyValue(215, 680));//khe
        nongsandacbiet.add(new KeyValue(214, 4));//tinh dau huong duong
        ms = new Message(60);
        DataOutputStream ds = ms.writer();
        ds.writeByte(hatgiong.size());
        for (KeyValue<Integer, Integer> i : hatgiong) {
            ds.writeByte(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeByte(nongsan.size());
        for (KeyValue<Integer, Integer> i : nongsan) {
            ds.writeByte(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeInt(Math.toIntExact(this.session.user.getXu()));
        ds.writeByte(us.getLeverFarm());
        ds.writeByte(us.getLeverPercen());
        ds.writeByte(phanbon.size());
        for (KeyValue<Integer, Integer> i : phanbon) {
            ds.writeShort(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeByte(nongsandacbiet.size());
        for (KeyValue<Integer, Integer> i : nongsandacbiet) {
            ds.writeShort(i.getKey());
            ds.writeShort(i.getValue());
        }
        ds.writeByte(1);
        ds.writeInt(64000);
        ds.writeBoolean(true);
        ds.writeShort(us.getLeverFarm());
        ds.writeByte(us.getLeverPercen());
        ds.writeByte(nongsan.size());
        for (KeyValue<Integer, Integer> i : nongsan) {
            ds.writeShort(i.getKey());
            ds.writeInt(i.getValue());
        }
        ds.writeByte(nongsandacbiet.size());
        for (KeyValue<Integer, Integer> i : nongsandacbiet) {
            ds.writeShort(i.getKey());
            ds.writeInt(i.getValue());
        }
        ds.flush();
        this.session.sendMessage(ms);
    }

    private void writeInfoCell(DataOutputStream ds) throws IOException {
        ds.writeShort(2880);
        ds.writeByte(40);
        ds.writeByte(0);
        ds.writeBoolean(false);
        ds.writeBoolean(false);
        ds.writeBoolean(false);
    }

    private void writeInfoAnimal(DataOutputStream ds) throws IOException {
        ds.writeInt(2000);
        ds.writeByte(100);
        ds.writeByte(0);
        ds.writeByte(20);
        ds.writeBoolean(true);
        ds.writeBoolean(false);
        ds.writeBoolean(true);
    }


    public void joinFarm(Message ms) throws IOException {
        int userId = ms.reader().readInt();
        boolean exitsTree = true;
        ms = new Message(61);
        DataOutputStream ds = ms.writer();
        ds.writeInt(userId);
        ds.writeByte(land);//số ô đất
        for (int i = 0; i < land; ++i) {
            if (exitsTree) {
                ds.writeByte(8);//id cây
                this.writeInfoCell(ds);
            } else {
                ds.writeByte(-1);
            }
        }
        ds.writeByte(10);
        for (int i = 0; i < 10; ++i) {
            ds.writeByte(50 + i % 7);
            this.writeInfoAnimal(ds);
        }
        ds.writeByte(10);
        ds.writeByte(8);
        ds.writeShort(5000);//lv cây khế
        ds.writeShort(43);
        ds.writeShort(46);
        ds.writeShort(180);//số khế có thể thu
        ds.writeShort(170);
        ds.writeShort(0);
        ds.writeShort(0);
        for (int i = 0; i < land; ++i) {
            ds.writeByte(1);
        }
        ds.writeShort(1);
        ds.writeShort(5);
        ds.flush();
        this.session.sendMessage(ms);
    }

    public void getImgFarm(Message ms) throws IOException {
        short imageID = ms.reader().readShort();
        String folder = session.getResourcesPath() + "farm/";
        byte[] dat = Avatar.getFile(folder + imageID + ".png");
        if (dat == null) {
            return;
        }
        ms = new Message(Cmd.GET_IMG_FARM);
        DataOutputStream ds = ms.writer();
        ds.writeShort(imageID);
        ds.writeShort(dat.length);
        ds.write(dat);
        ds.flush();
        this.session.sendMessage(ms);
    }
}
