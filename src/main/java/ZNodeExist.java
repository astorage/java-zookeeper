import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZNodeExist {

    private static ZooKeeper zooKeeper;

    private static ZookeeperConnection zookeeperConnection;

    public static Stat znodeExist(String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, true);
    }

    public static void main(String[] args) throws Exception{
        String path = "/testMyFirstZNode";

        zookeeperConnection = new ZookeeperConnection();

        zooKeeper = zookeeperConnection.connect("localhost");

        Stat stat = znodeExist(path);

        if (stat != null) {
            System.out.println("Node exists and the node version is " + stat.getVersion());
        }else {
            System.out.println("Node does not exists");
        }

//        zookeeperConnection.close();
    }
}
