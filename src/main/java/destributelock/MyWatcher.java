package destributelock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class MyWatcher implements Watcher {

    private int count;
    // 计数器
    private CountDownLatch countDownLatch;

    public void process(WatchedEvent event) {
//        System.out.println(event.toString());
//        System.out.println("countDownLatch:" + countDownLatch);
        System.out.println(Thread.currentThread().getName() + ":" + (++count));
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
