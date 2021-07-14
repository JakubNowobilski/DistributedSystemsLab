import A3.*;
import com.zeroc.Ice.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerCallbackI implements ServerCallback {
    private List<ClientCallbackPrx> connectedClients;
    private Map<String, IssueResult> finishedIssues;
    private Map<String, IssueNotification> pendingIssues;
    private ExecutorService executorService;
    private int taxCount = 0;
    private int ticketCount = 0;
    private int permitCount = 0;

    ServerCallbackI(){
        this.connectedClients = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
        this.finishedIssues = new HashMap<>();
        this.pendingIssues = new HashMap<>();
    }

    @Override
    public IssueResult connectClient(ClientCallbackPrx client, Current current) throws IdentifierInUseException {
        ClientCallbackPrx clientFixedPrx = client.ice_fixed(current.con);
        String clientIdentity = Util.identityToString(clientFixedPrx.ice_getIdentity());
        if(this.connectedClients.stream().anyMatch(c -> c.ice_getIdentity().equals(clientFixedPrx.ice_getIdentity())))
            throw new IdentifierInUseException(clientIdentity);
        this.connectedClients.add(clientFixedPrx);

        current.con.setCloseCallback(new CloseCallback() {
            @Override
            public void closed(Connection connection) {
                System.out.println("\nClient disconnected: " + clientIdentity);
                connectedClients.remove(clientFixedPrx);
            }
        });

        System.out.println("\nClient connected: " + clientIdentity);
        IssueResult issueResult = finishedIssues.remove(clientIdentity);
        if(issueResult == null){
            System.out.println("\tNo result awaiting for the client.");
            issueResult = new IssueResult(new Issue(), "-1");
        }
        else{
            System.out.println("\tReturning to the client with following result: ");
            printIssueResult(issueResult);
        }
        return issueResult;
    }

    @Override
    public IssueResult issueRequest(String clientIdentity, IssueType issueType, Current current) throws NoClientFoundException, IssueSubmitedException {
        if(this.connectedClients.stream().noneMatch(c -> Util.identityToString(c.ice_getIdentity()).equals(clientIdentity)))
            throw new NoClientFoundException(clientIdentity);
        if(this.pendingIssues.containsKey(clientIdentity))
            throw new IssueSubmitedException();
        Issue issue = new Issue();
        int waitingTime = 0;
        Random rng = new Random();
        String result = rng.nextInt(2) == 0 ? "accepted" : "rejected";
        switch (issueType){
            case TAX:
                this.taxCount++;
                issue.issueType = IssueType.TAX;
                issue.issueId = "TAX_" + this.taxCount;
                waitingTime = rng.nextInt(10) + 10;
                break;
            case TICKET:
                this.ticketCount++;
                issue.issueType = IssueType.TICKET;
                issue.issueId = "TICKET_" + this.ticketCount;
                if(rng.nextInt(5) < 1)
                    waitingTime = rng.nextInt(10) + 20;
                break;
            case PERMIT:
                this.permitCount++;
                issue.issueType = IssueType.PERMIT;
                issue.issueId = "PERMIT_" + this.permitCount;
                waitingTime = rng.nextInt(60) + 60;
                break;
        }
        System.out.println("\nNew issue [" + issueType + "] requested for client: " + clientIdentity);
        System.out.println("\tAssigned issue id: " + issue.issueId);
        IssueResult issueResult = new IssueResult(issue, result);
        if(waitingTime == 0){
            System.out.println("\tReturning immediately to the client with result: " + result);
            return issueResult;
        }
        else{
            long dueTime = System.currentTimeMillis() + (long) waitingTime * 1000;
            this.pendingIssues.put(clientIdentity, new IssueNotification(issue, dueTime));
            int finalWaitingTime = waitingTime * 1000 + rng.nextInt(5000);
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(finalWaitingTime);
                        ClientCallbackPrx clientFixedPrx = getClientPrx(clientIdentity);
                        if(clientFixedPrx != null){
                            System.out.println("\nReturning to the client with following result: ");
                            printIssueResult(issueResult);
                            clientFixedPrx.issueReply(issueResult);
                        }
                        else{
                            System.out.println("\nClient not present. Saving client result.");
                            printIssueResult(issueResult);
                            finishedIssues.put(clientIdentity, issueResult);
                        }
                        pendingIssues.remove(clientIdentity);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("\tReturning to the client in approximately " + waitingTime + "s.");
            return new IssueResult(new Issue(), "-1");
        }
    }

    @Override
    public IssueNotification pendingIssue(String clientIdentity, Current current) throws NoClientFoundException{
        System.out.println("\nClient " + clientIdentity + " requesting notification.");
        if(this.connectedClients.stream().noneMatch(c -> Util.identityToString(c.ice_getIdentity()).equals(clientIdentity)))
            throw new NoClientFoundException(clientIdentity);
        IssueNotification notification = this.pendingIssues.get(clientIdentity);
        if(notification != null){
            long expectedTime = (notification.dueTime - System.currentTimeMillis()) / 1000;
            System.out.println("\tReturning to the client with notification: ");
            IssueNotification retNotification = new IssueNotification(notification.issue, expectedTime);
            printIssueNotification(retNotification);
            return retNotification;
        }
        else{
            System.out.println("\tNo notification pending for the client.");
            return new IssueNotification(new Issue(), -1);
        }
    }

    private ClientCallbackPrx getClientPrx(String clientIdentity){
        return this.connectedClients.stream()
                .filter(c -> Util.identityToString(c.ice_getIdentity()).equals(clientIdentity))
                .findAny()
                .orElse(null);
    }

    private void printIssueResult(IssueResult issueResult){
        System.out.println("\t[" + issueResult.issue.issueType + " - " + issueResult.issue.issueId + "] - " + issueResult.result);
    }

    private void printIssueNotification(IssueNotification issueNotification){
        System.out.println("\t[" + issueNotification.issue.issueType + " - " + issueNotification.issue.issueId + "] - " + issueNotification.dueTime);
    }
}
