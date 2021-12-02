import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

public class FileImpl extends UnicastRemoteObject
        implements FileInterface {

    private String name;
    List<String> returnList = new ArrayList<>();
    File userList = new File("Users.txt");

    public FileImpl(String s) throws RemoteException{
        super();
        name = s;
    }

    /*
    Function checks to see if username exists by reading through the txt file Users.txt.
    If the username is found it returns true, otherwise it returns false.
     */
    public boolean CheckUser(int user) throws IOException{
        String username = Integer.toString(user);

        boolean found = false;
        Scanner scanner = new Scanner(userList);

        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.equals(username)){
                found = true;
                break;
            }
        }

        return found;
    }

    /*
    Function to login the user. User will enter their username and password into the client and these values will be passed here.
    Will read from a txt file for now to authenticate.
    We can add in reading from the database later.
     */
    public boolean Login(int user, String pass) throws IOException{
        //Get user file linked to their username
        //Store password on line 2 of file
        //function will check that the entered password is equal to password on line 2 of file, or in database later on
        String username = Integer.toString(user);
        String filename = "UserFiles/"+username + ".txt";
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String password;

        br.readLine();
        password = br.readLine().substring(10);

        return pass.equals(password);
    }

    /*
    Function to check is the user has voted yet.
    We can store a 0 for not voted yet and a 1 for has voted.
    This will get this value and see if the user has voted, if they have they will not be able to again.
     */
    public boolean checkVote(int user) throws IOException{
        String username = Integer.toString(user);
        String file = "UserFiles/"+username + ".txt";
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String vote;
        br.readLine();
        br.readLine();

        vote = br.readLine().substring(7);

        return vote.equals("0");


    }

    /*
    This function will be used to obtain the account type of which there are three.
    Will read the value from the text file/database and return options based on that.
    3 accounts: 1. Administrator 2. Candidate 3. Voter
     */
    public int getAccountType(int user) throws IOException {
        String username = Integer.toString(user);
        String file = "UserFiles/"+username + ".txt";
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String type;
        br.readLine();
        br.readLine();
        br.readLine();

        type = br.readLine().substring(9);

        if (type.equals("1")) {
            return 1;
        } else if (type.equals("2")) {
            return 2;
        } else {
            return 3;
        }
    }

    public void setupCandidates() throws IOException {
        File dir = new File("UserFiles/Candidates");
        List<String> list = Arrays.asList(Objects.requireNonNull(dir.list(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                }
        )));

        for(int i = 0; i < list.size(); i++){
            String s = list.get(i);
            s = s.substring(0, s.length() - 4);
            s = (i+1) + ". "+ s;
            returnList.add(s);
        }
    }

    /*
    Function is used to get the list of all candidates to display to the client
    Reads the list from the subfolder containing candidate info text files
    Returns the list to the client
     */
    public List<String> getCandidates() throws IOException {
        File dir = new File("UserFiles/Candidates");
        returnList.clear();
        List<String> list = Arrays.asList(Objects.requireNonNull(dir.list(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                }
        )));

        for(int i = 0; i < list.size(); i++){
            String s = list.get(i);
            s = s.substring(0, s.length() - 4);
            s = (i+1) + ". "+ s;
            returnList.add(s);
        }
        System.out.println(returnList);
        return returnList;
    }

    /*
    Function to get the info of the candidate selected by the user after the list is displayed
    User enters an integer to select a candidate, this function gets the candidate info from the text file and returns it to the client.
     */
    public String canInfo(int candidate) throws IOException {
        String canChoice = Integer.toString(candidate);
        String canNum;
        String notFound = "Candidate not found.";

        for (String s : returnList) {
            canNum = s.substring(0, 1);
            if (canNum.equals(canChoice)) {
                //get candidate info
                String name = s.substring(3);
                String file = "UserFiles/Candidates/" + name + ".txt";

                String content = Files.readString(Path.of(file), StandardCharsets.US_ASCII);
                return content;
            }
        }
        return notFound;
    }

    /*
    Vote function. This takes the user and their vote as input.
    Adds the vote to the appropriate candidate, and updates the user file vote flag to be 1, to show that they have already voted.
     */
    public String vote(int user, int vote) throws IOException {
        String username = Integer.toString(user);
        String strVote = Integer.toString(vote);
        String notFound = "Candidate entered does not exist.";
        String canNum;
        Writer output1, output2;
        String file = "UserFiles/"+username + ".txt";

        for (String s : returnList) {
            canNum = s.substring(0, 1);
            if (canNum.equals(strVote)) {
                String name = s.substring(3);
                String canFile = "UserFiles/Candidates/" + name + ".txt";
                FileReader fr = new FileReader(canFile);
                BufferedReader br = new BufferedReader(fr);
                String line1 = br.readLine();
                String line2 = br.readLine();
                String line3 = br.readLine();

                String votes = br.readLine().substring(7);
                System.out.println(votes);
                int numVotes = Integer.parseInt(votes);
                numVotes += 1;
                String insert = "Votes: " + numVotes;

                String fileIn = line1 + "\n" + line2 + "\n" + line3 + "\n" + insert + "\n" + br.readLine();
                System.out.println(fileIn);
                PrintWriter pw = new PrintWriter(canFile);
                pw.close();
                output1 = new BufferedWriter(new FileWriter(canFile, true));
                output1.write(fileIn);
                output1.close();

                FileReader reader = new FileReader(file);
                BufferedReader read = new BufferedReader(reader);
                String uline = read.readLine();
                String uline1 = read.readLine();
                read.readLine();
                String vFlag;
                vFlag = "1";
                String flagIn = "Voted: "+vFlag;
                String uline2 = read.readLine();
                String userIn = uline + "\n" + uline1 + "\n" + flagIn + "\n" + uline2;
                PrintWriter writer = new PrintWriter(file);
                writer.close();
                output2 = new BufferedWriter(new FileWriter(file, true));
                output2.write(userIn);
                output2.close();
                return "Thank you for voting.";

            }
        }

        return notFound;
    }

    public String canEdit(int user) throws IOException{

        //need to substring index 3 to 8 to get username from return list to match to user value passed to method
        //if these match then open file and return info to client and ask which they would like to edit
        int subUser;

        for(int i = 0; i < returnList.size(); i++){
            subUser = Integer.parseInt(returnList.get(i).substring(3, 8));
            System.out.println(subUser);
            if(user == subUser){
                System.out.println(returnList.get(i));
                String file = "UserFiles/Candidates/" + returnList.get(i).substring(3) + ".txt";
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line1 = br.readLine();
                String line2 = br.readLine();
                String line3 = br.readLine();
                String retStr = line1 + "\n" + line2 + "\n" + line3;
                return retStr;
            }
        }

        return "file not found";
    }

    public String edit(int user, int val, String str) throws IOException{
        int subUser;
        Writer output;

        for(int i = 0; i < returnList.size(); i++) {
            subUser = Integer.parseInt(returnList.get(i).substring(3, 8));
            System.out.println(subUser);
            if (user == subUser) {
                String file = "UserFiles/Candidates/" + returnList.get(i).substring(3) + ".txt";
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line1 = br.readLine();
                if(val == 1){
                    //edit website
                    br.readLine();
                    String fileReturn = line1 + "\nWebsite: " + str + "\n" + br.readLine() + "\n" + br.readLine() + "\n" + br.readLine();
                    PrintWriter writer = new PrintWriter(file);
                    writer.close();
                    output = new BufferedWriter(new FileWriter(file, true));
                    output.write(fileReturn);
                    output.close();
                    return "Website successfully changed";
                }
                else if(val == 2){
                    //edit statement
                    String line2 = br.readLine();
                    br.readLine();
                    String fileReturn = line1 + "\n" + line2 + "\n" + "Statement: " + str + "\n" + br.readLine() + "\n" + br.readLine();
                    PrintWriter writer = new PrintWriter(file);
                    writer.close();
                    output = new BufferedWriter(new FileWriter(file, true));
                    output.write(fileReturn);
                    output.close();
                    return "Statement successfully changed";
                }
                else{
                    return "invalid choice";
                }
            }
        }

        return null;
    }

    /*
    Function used by the admin to start the election.
    Will read value from administrator file to determine if there is currently an election ongoing.
    If election is currently ongoing, cannot start new one. Other-wise a new election period begins.
     */
    public String startElection() throws IOException{
        boolean flag = getElection();
        Writer output;
        //election is currently open, cannot start new one
        if(flag){
            return "Election is already ongoing, cannot start new election cycle.";
        }
        //election is currently closed, start new one here, need to change flag to 1 to mark it as started and return started
        else{
            String file = "UserFiles/Admin/flags.txt";
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String election = br.readLine().substring(10);
            String line2 = br.readLine();
            String rFile = "Election: 1\n" + line2;
            PrintWriter writer = new PrintWriter(file);
            writer.close();
            output = new BufferedWriter(new FileWriter(file, true));
            output.write(rFile);
            output.close();
            return "Election has been started.";
        }
    }

    /*
    Function used by admin account to end current election cycle.
    Reads value from administrator file to determine if there is an election ongoing.
    If election is currently ongoing, we can end it. We cannot end an election that does not exist.
    Ending the election will also end voting by default.
    This will also delete all candidate files from the candidates folder.
     */
    public String endElection() throws IOException{
        boolean flag = getElection();
        endVoting();
        Writer output;
        //election is currently open, this will close the election
        if(flag){
            String file = "UserFiles/Admin/flags.txt";
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String election = br.readLine().substring(10);
            String line2 = br.readLine();
            String rFile = "Election: 0\n" + line2;
            PrintWriter writer = new PrintWriter(file);
            writer.close();
            output = new BufferedWriter(new FileWriter(file, true));
            output.write(rFile);
            output.close();

            //clear voted flag from user account
            File dir = new File("UserFiles");
            List<String> list = Arrays.asList(Objects.requireNonNull(dir.list(
                    new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".txt");
                        }
                    }
            )));

            for(int i = 0; i < list.size(); i++){
                String s = list.get(i);
                String userfile = "UserFiles/" + s;
                FileReader reader = new FileReader(userfile);
                BufferedReader bread = new BufferedReader(reader);
                String line = bread.readLine();
                String twoLine = bread.readLine();
                bread.readLine();
                String fourLine = bread.readLine();
                String voteReset = line + "\n" + twoLine + "\nVoted: 0\n" + fourLine;
                PrintWriter writer1 = new PrintWriter(userfile);
                writer1.close();
                output = new BufferedWriter(new FileWriter(userfile, true));
                output.write(voteReset);
                output.close();
            }

            //clear votes from candidates
            File candir = new File("UserFiles/Candidates");
            List<String> canlist = Arrays.asList(Objects.requireNonNull(candir.list(
                    new FilenameFilter() {
                        @Override
                        public boolean accept(File candir, String name) {
                            return name.endsWith(".txt");
                        }
                    }
            )));
            System.out.println(canlist);

            for(int i = 0; i < canlist.size(); i++){
                String s = canlist.get(i);
                System.out.println(s);
                String canfile = "UserFiles/Candidates/" + s;
                FileReader reader = new FileReader(canfile);
                BufferedReader reader2 = new BufferedReader(reader);
                String line = reader2.readLine();
                String twoLine = reader2.readLine();
                String threeLine = reader2.readLine();
                reader2.readLine();
                String fourLine = reader2.readLine();
                String voteReset = line + "\n" + twoLine + "\n" + threeLine + "\nVotes: 0\n" + fourLine;
                PrintWriter writer1 = new PrintWriter(canfile);
                writer1.close();
                output = new BufferedWriter(new FileWriter(canfile, true));
                output.write(voteReset);
                output.close();
            }

            return "Election has been closed.";
        }
        //election is currently closed, cannot close a closed
        else{
            return "There is no election ongoing to be ended.";

        }
    }

    //getter function to see if there is an election ongoing.
    public boolean getElection() throws IOException{
        String file = "UserFiles/Admin/flags.txt";
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String election = br.readLine().substring(10);
        int flag = Integer.parseInt(election);
        if(flag == 0){return false;}
        else return flag == 1;
    }

    /*
    Function controlled by the admin to begin voting.
    Until voting has begun no users will be able to vote, they can view candidates but that is all.
     */
    public String startVoting() throws IOException{
        boolean vflag = getVoting();
        boolean eflag = getElection();
        Writer output;
        if(eflag) {
            //voting is currently open, cannot start new one
            if (vflag) {
                return "Voting is already ongoing, cannot open voting.";
            }
            //voting is currently closed, start new one here, need to change flag to 1 to mark it as started and return started
            else {
                String file = "UserFiles/Admin/flags.txt";
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String election = br.readLine();
                String line2 = br.readLine();
                String rFile = election + "\nVoting: 1";
                PrintWriter writer = new PrintWriter(file);
                writer.close();
                output = new BufferedWriter(new FileWriter(file, true));
                output.write(rFile);
                output.close();
                return "Voting has been started.";
            }
        }
        else{
            return "Election is currently closed, voting cannot be started. Start election first to start voting.";
        }
    }

    /*
    Function controlled by admin to end voting.
    Once the admin ends the voting period users can no longer vote.
    Users will be able to see the final votes presented in a table when they login.
     */
    public String endVoting() throws IOException{
        boolean flag = getVoting();
        Writer output;
        //voting is currently open, this will close the voting
        if(flag){
            String file = "UserFiles/Admin/flags.txt";
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String election = br.readLine();
            String voting = br.readLine();
            String rFile = election + "\nVoting: 0";
            PrintWriter writer = new PrintWriter(file);
            writer.close();
            output = new BufferedWriter(new FileWriter(file, true));
            output.write(rFile);
            output.close();
            return "Voting has been closed.";
        }
        //voting is currently closed, cannot close a closed
        else{
            return "There is no voting ongoing to be ended.";

        }
    }

    //getter function to return if voting is currently open or not.
    public boolean getVoting() throws IOException{
        String file = "UserFiles/Admin/flags.txt";
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String voting = br.readLine().substring(8);
        int flag = Integer.parseInt(voting);
        if(flag == 0){return false;}
        else return flag == 1;

    }

    public String voteTable() throws IOException{
        StringBuilder table = new StringBuilder();
        returnList.clear();
        returnList = getCandidates();

        for (String s : returnList) {
            String name = s.substring(3);
            String canFile = "UserFiles/Candidates/" + name + ".txt";
            FileReader fr = new FileReader(canFile);
            BufferedReader br = new BufferedReader(fr);
            String party = br.readLine();
            br.readLine();
            br.readLine();
            String votes = br.readLine();
            String canName = br.readLine();

            table.append(canName).append(", ").append(party).append(": ").append(votes).append("\n");
        }

        return table.toString();
    }
}
