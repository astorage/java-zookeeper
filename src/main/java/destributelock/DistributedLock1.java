package destributelock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedLock1 implements Lock{

    private ZooKeeper zooKeeper;

    private String ROOT_LOCK = "/locks";

    private String lockName;

    private String WAIT_LOCK;

    private String CURRENT_LOCK;

    private CountDownLatch countDownLatch;

    private int sessionTimeout = 30000;
    private MyWatcher myWatcher;

    private List<Exception> exceptionList = new ArrayList<Exception>();


    public DistributedLock1(String zooKeeperAddr, String lockName) {
        this.lockName = lockName;
        myWatcher = new MyWatcher();
        try {
            zooKeeper = new ZooKeeper(zooKeeperAddr, sessionTimeout, myWatcher);
            Stat stat = zooKeeper.exists(ROOT_LOCK, false);
            if (stat == null) {
                zooKeeper.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void lock() {

        try {
            if (exceptionList.size() > 0) {
                throw new Exception(exceptionList.get(0).getMessage());
            }
            if (this.tryLock()) {
                System.out.println(Thread.currentThread().getName() + " " + "获得了锁" + CURRENT_LOCK);
                return;
            } else {
                // 等待锁
                waitForLock(WAIT_LOCK, sessionTimeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    public boolean tryLock() {
        try {
            String seperator = "_lock_";
            if (lockName.contains(seperator))
                throw new Exception("锁名有误");

            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/" + lockName + seperator, new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> childNodes = zooKeeper.getChildren(ROOT_LOCK, false);
            List<String> lockNameList = new ArrayList<String>();
            for (String nodeName : childNodes) {
                String lockName = nodeName.split(seperator)[0];
                if (lockName.equals(this.lockName)) {
                    lockNameList.add(nodeName);
                }
            }

            Collections.sort(lockNameList);

            if (CURRENT_LOCK.equals(ROOT_LOCK + "/" + lockNameList.get(0))) {
                return true;
            }

            String preNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);

            WAIT_LOCK = lockNameList.get(Collections.binarySearch(lockNameList, preNode) - 1);


        }catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        try {
            System.out.println(Thread.currentThread().getName() + "释放锁 " + CURRENT_LOCK);
            zooKeeper.delete(CURRENT_LOCK, -1);
            CURRENT_LOCK = null;
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public Condition newCondition() {
        return null;
    }

    // 等待锁
    private boolean waitForLock(String prev, long waitTime) throws KeeperException, InterruptedException {
        /**
         * 这个监控很重要 true
         */
        Stat stat = zooKeeper.exists(ROOT_LOCK + "/" + prev, true);

        if (stat != null) {
            System.out.println(Thread.currentThread().getName() + "等待锁 " + ROOT_LOCK + "/" + prev);
            countDownLatch = new CountDownLatch(1);
            myWatcher.setCountDownLatch(countDownLatch);
            // 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
            countDownLatch.await(waitTime, TimeUnit.MILLISECONDS);
            countDownLatch = null;
            System.out.println(Thread.currentThread().getName() + " 等到了锁" + CURRENT_LOCK);
        }
        return true;
    }

}
