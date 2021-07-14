# pragma once

module A3{
  enum IssueType{
    TAX,
    TICKET,
    PERMIT
  };

  exception IdentifierInUseException {
    string clientIdentifier;
  };

  exception IssueSubmittedException {}

  exception NoClientFoundException {
    string clientIdentifier;
  }

  struct Issue{
    IssueType issueType;
    string issueId;
  }

  struct IssueNotification{
    Issue issue;
    long dueTime;
  }

  struct IssueResult{
    Issue issue;
    string result;
  }

  interface ClientCallback{
    void issueReply(IssueResult issueResult);
  }

  interface ServerCallback{
    IssueResult connectClient(ClientCallback* client) throws IdentifierInUseException;
    IssueResult issueRequest(string clientIdentity, IssueType issueType) throws IssueSubmittedException, NoClientFoundException;
    IssueNotification pendingIssue(string clientIdentity) throws NoClientFoundException;
  }
}
