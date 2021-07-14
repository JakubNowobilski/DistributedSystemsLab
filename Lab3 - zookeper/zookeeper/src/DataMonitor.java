import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class DataMonitor {
    private ZooKeeper zk;
    private String znode;
    private String exec;
    private Process process;

    public DataMonitor(ZooKeeper zk, String znode, String exec){
        this.zk = zk;
        this.znode = znode;
        this.exec = exec;
        this.process = null;
    }

    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getPath().equals(this.znode) && watchedEvent.getType() == Watcher.Event.EventType.NodeCreated){
            try {
                this.process = new ProcessBuilder(this.exec).start();
                System.out.println("[" + exec + "] - process started");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(watchedEvent.getPath().equals(this.znode) && watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted){
            if(this.process != null){
                this.process.destroy();
                System.out.println("[" + exec + "] - process terminated");
            }
        }
        else if(watchedEvent.getPath().startsWith("/z") && watchedEvent.getType() == Watcher.Event.EventType.NodeCreated) {
            System.out.println("Children count: " + this.getChildrenCount());
        }
    }

    private int getChildrenCount(){
        try {
            if(this.zk.exists(this.znode, false) != null){
                return this.zk.getAllChildrenNumber(this.znode);
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
