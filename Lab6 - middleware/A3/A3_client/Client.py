import sys
import signal
import Ice
import A3


def printNotification(issueNotification):
    print("\tNOTIFICATION")
    print(f'\t[{issueNotification.issue.issueType} - {issueNotification.issue.issueId}]:')
    if issueNotification.dueTime < 0:
        print("\tIssue service is taking longer than expected. It is still being processed")
    else:
        print(f'\tExpected remaining time - {issueNotification.dueTime}s')


def printResult(issueResult):
    print("\tRESULT")
    print(f'\t[{issueResult.issue.issueType} - {issueResult.issue.issueId}]:')
    print(f'\tResult - {issueResult.result}')


class ClientCallbackI(A3.ClientCallback):
    def issueReply(self, issueResult, current):
        print("\nServer is sending results")
        printResult(issueResult)


with Ice.initialize([], "config.client") as communicator:
    signal.signal(signal.SIGINT, lambda signum, frame: communicator.shutdown())

    serverProxy = A3.ServerCallbackPrx.checkedCast(communicator.propertyToProxy("ServerCallback.Proxy"))
    if not serverProxy:
        print("Invalid proxy")
        sys.exit(1)

    adapter = communicator.createObjectAdapter("")

    clientIdentity = sys.argv[1]

    clientProxy = A3.ClientCallbackPrx.uncheckedCast(adapter.add(ClientCallbackI(), Ice.stringToIdentity(clientIdentity)))
    adapter.activate()

    serverProxy.ice_getConnection().setAdapter(adapter)

    serverProxy.ice_getConnection().setCloseCallback(lambda con: {
        print("\nConnection to the server lost."),
        print("Terminating client"),
        communicator.shutdown(),
    })

    print(f'Client {clientIdentity}')
    print("Connected to the server: ")
    print(serverProxy.ice_getConnection().toString())

    print("\nChecking if any issue is awaiting")
    initResult = serverProxy.connectClient(clientProxy)
    if initResult.result != "-1":
        printResult(initResult)
    else:
        initNotification = serverProxy.pendingIssue(clientIdentity)
        if initNotification.dueTime != -1:
            printNotification(initNotification)
        else:
            print("No issue awaiting\n")

    while True:
        issueType = None
        issueNotification = None
        issueResult = None
        line = input("==> ")
        if communicator.isShutdown():
            break
        if line == "TAX":
            issueType = A3.IssueType.TAX
        elif line == "TICKET":
            issueType = A3.IssueType.TICKET
        elif line == "PERMIT":
            issueType = A3.IssueType.PERMIT
        elif line == "PENDING":
            issueNotification = serverProxy.pendingIssue(clientIdentity)
        elif line == "EXIT":
            break
        else:
            continue

        if issueType is not None:
            print("\nRequesting issue " + line)
            try:
                issueResult = serverProxy.issueRequest(clientIdentity, issueType)
            except Ice.Exception:
                print("\tIssue already submitted. Wait for response.")
                continue
            if issueResult.result != "-1":
                printResult(issueResult)
            else:
                issueNotification = serverProxy.pendingIssue(clientIdentity)

        if issueNotification is not None:
            if issueNotification.dueTime != -1:
                printNotification(issueNotification)
            else:
                print("\tNo issue pending")

    communicator.shutdown()


