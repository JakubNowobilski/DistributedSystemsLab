import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor implements Watcher {
    private ZooKeeper zk;
    private DataMonitor dm;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        int timeout = 3000;
        String znode = "/z";
        String host = "localhost:2182";
        String exec = "/usr/bin/google-chrome-stable";
        Executor executor = new Executor(timeout, znode, host, exec);

        System.out.println("Type \"show\" to display node tree: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String brIn = br.readLine();
            if(brIn.equals("exit"))
                break;
            else if(brIn.equals("show"))
                executor.printTree(znode, 0);
        }
    }

    public Executor(int timeout, String znode, String host, String exec) throws IOException, KeeperException, InterruptedException {
        this.zk = new ZooKeeper(host, timeout, null);
        this.zk.addWatch(znode, this, AddWatchMode.PERSISTENT_RECURSIVE);
        this.dm = new DataMonitor(this.zk, znode, exec);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        this.dm.process(watchedEvent);
    }

    public void printTree(String znode, int level){
        try {
            if(this.zk.exists(znode, false) != null) {
                System.out.print(StringUtils.repeat("\t", level));
                System.out.println(znode);
                zk.getChildren(znode, null).forEach(
                        child -> printTree(znode + "/" + child, level + 1)
                );
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
