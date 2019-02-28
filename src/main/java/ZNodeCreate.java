import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZNodeCreate {
    private static ZooKeeper zooKeeper;

    private static ZookeeperConnection zookeeperConnection;

    public static void createZNode(String path, byte[] data) throws KeeperException, InterruptedException {
        zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    public static void main(String[] args) throws Exception{
        String path = "/testMyFirstZNode";

        byte[] data = "my first zookeeer test".getBytes();

        zookeeperConnection = new ZookeeperConnection();

        zooKeeper = zookeeperConnection.connect("localhost");

        createZNode(path, data);

        zookeeperConnection.close();

    }
}
