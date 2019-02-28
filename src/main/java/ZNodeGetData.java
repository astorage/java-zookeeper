import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZNodeGetData {

    private static ZooKeeper zooKeeper;

    private static ZookeeperConnection zookeeperConnection;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static Stat znodeExist(String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, true);
    }

    public static void main(String[] args) throws Exception{
        String path = "/testMyFirstZNode";

        zookeeperConnection = new ZookeeperConnection();

        zooKeeper = zookeeperConnection.connect("localhost");

        Stat stat = znodeExist(path);

        if (stat != null) {
           byte[] ba = zooKeeper.getData(path, new Watcher() {
               public void process(WatchedEvent event){
                   if (event.getType() == Event.EventType.None) {
                        switch (event.getState()) {
                            case Expired:
                                countDownLatch.countDown();
                                break;
                        }
                   }else {
                        String path = "/testMyFirstZNode";
                        try {
                            byte[] dataByteArray = zooKeeper.getData(path, false, null);
                            String data = new String(dataByteArray, "utf-8");
                            System.out.println(data);
                            countDownLatch.countDown();
                        }catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                   }
               }
           }, null);

            String data = new String(ba, "UTF-8");
            System.out.println(data);
            countDownLatch.await();

        }else {
            System.out.println("Node does not exists");
        }

//        zookeeperConnection.close();
    }
}
