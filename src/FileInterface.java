import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileInterface extends Remote {
    boolean CheckUser(int user) throws IOException;
    boolean Login(int user, String pass) throws IOException;
    boolean checkVote(int user) throws IOException;
    int getAccountType(int user) throws IOException;
    List<String> getCandidates() throws IOException;
    String canInfo(int candidate) throws IOException;
    String vote(int user, int vote) throws IOException;
    String canEdit(int user) throws IOException;
    void setupCandidates() throws IOException;
    String edit(int user, int val, String str) throws IOException;
    String startElection() throws IOException;
    String endElection() throws IOException;
    String startVoting() throws IOException;
    String endVoting() throws IOException;
    boolean getElection() throws IOException;
    boolean getVoting() throws IOException;
    String voteTable() throws IOException;
}
