import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class ZNodeSetData {
    private static ZooKeeper zooKeeper;
    private static ZookeeperConnection zookeeperConnection;

    // Method to update the data in a znode. Similar to getData but without watcher.
    public static void update(String path, byte[] data) throws
            KeeperException,InterruptedException {
        zooKeeper.setData(path, data, zooKeeper.exists(path,true).getVersion());
    }

    public static void main(String[] args) throws InterruptedException,KeeperException {
        String path= "/testMyFirstZNode";
        byte[] data = "Success".getBytes(); //Assign data which is to be updated.

        try {
            zookeeperConnection = new ZookeeperConnection();
            zooKeeper = zookeeperConnection.connect("localhost");
            update(path, data); // Update znode data to the specified path
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
