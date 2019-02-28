import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperConnection {

    private ZooKeeper zooKeeper;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeper connect(String host) throws Exception{
        zooKeeper = new ZooKeeper(host, 5000, new Watcher() {
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

    public void close() throws Exception{
        zooKeeper.close();
    }
}
