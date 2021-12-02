import java.io.*;
import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ElectionClient{
    static String[] fileList;
    static Scanner in = new Scanner(System.in);
    static int user, accType, canGet, canVote;
    static String pass;
    static boolean check, log, voteCheck;
    static List<String> canList = new ArrayList<>();
    static boolean eFlag, vFlag;

    public static void main(String[] argv) {

        if(argv.length != 1) {
            System.out.println("Usage: java ElectionClient machineName");
            System.exit(0);
        }
        try {
            while(true) {
                String name = "//" + argv[0] + "/FileServer";
                FileInterface fi = (FileInterface) Naming.lookup(name);
                fi.setupCandidates();
                eFlag = fi.getElection();
                vFlag = fi.getVoting();
                if(eFlag){
                    System.out.println("election is open!.");
                }
                else{
                    System.out.println("election is currently closed..");
                }
                if(vFlag){
                    System.out.println("voting is open!");
                }
                else{
                    System.out.println("voting is currently closed..");
                }

                //user logs in with username and password
                System.out.println("CTRL + C to exit at anytime.");
                System.out.print("Enter Username: ");
                user = in.nextInt();
                System.out.print("Enter Password: ");
                pass = in.next();

                //checks to make sure user exists
                check = fi.CheckUser(user);
                if(check) {
                    //attempts to log user in with the entered username and password
                    log = fi.Login(user, pass);
                    if (log) {
                        System.out.println("welcome");
                        accType = fi.getAccountType(user);
                        //switch runs based on account type, either admin, candidate or voter
                        switch (accType) {

                            case 1 -> {
                                System.out.println("Admin");
                                System.out.print("Admin options: \n1. Open/Close Voting\n2. Begin/End Current Election Cycle\nEnter: ");
                                int aNum = in.nextInt();
                                if(aNum == 2){
                                    if(eFlag){
                                        System.out.print("Election is currently open, would you like to end this cycle? Enter 1 for Yes or 2 for No: ");
                                        int cycleNum = in.nextInt();
                                        if(cycleNum == 1){
                                            System.out.println(fi.endElection());
                                        }
                                        else if(cycleNum == 2){
                                            System.out.println("Returning to main menu...");
                                        }
                                        else{System.out.println("Invalid entry, returning to main menu...");}
                                    }
                                    else{
                                        System.out.print("Election is currently closed, would you like to begin a new cycle? Enter 1 for Yes or 2 for No: ");
                                        int cycleNum = in.nextInt();
                                        if(cycleNum == 1){
                                            System.out.println(fi.startElection());
                                        }
                                        else if(cycleNum == 2){
                                            System.out.println("Returning to main menu...");
                                        }
                                        else{System.out.println("Invalid entry, returning to main menu...");}
                                    }
                                }
                                else if(aNum == 1){
                                    if(vFlag){
                                        System.out.print("Voting is currently open, would you like to end this cycle? Enter 1 for Yes or 2 for No: ");
                                        int votingNum = in.nextInt();
                                        if(votingNum == 1){
                                            System.out.println(fi.endVoting());
                                        }
                                        else if(votingNum == 2){
                                            System.out.println("Returning to main menu...");
                                        }
                                        else{System.out.println("Invalid entry, returning to main menu...");}
                                    }
                                    else{
                                        System.out.print("Voting is currently closed, would you like to begin a new cycle? Enter 1 for Yes or 2 for No: ");
                                        int votingNum = in.nextInt();
                                        if(votingNum == 1){
                                            System.out.println(fi.startVoting());
                                        }
                                        else if(votingNum == 2){
                                            System.out.println("Returning to main menu...");
                                        }
                                        else{System.out.println("Invalid entry, returning to main menu...");}
                                    }
                                }
                                else{System.out.println("invalid option entered, returning to main menu...");}
                            }

                            //candidate options should be ability to edit their statement on their profile, and they should be able to vote as well.
                            case 2 -> {
                                if(eFlag && vFlag) {
                                    System.out.println("Candidate");
                                    System.out.print("Candidate Options: \n1. Edit Account Details.\n2. Vote Now!\n3. View Other Candidates.\nEnter Number Here: ");
                                    int cNum = in.nextInt();
                                    switch (cNum) {
                                        case 1 -> {
                                            System.out.println(fi.canEdit(user));
                                            System.out.print("Which of the entries would you like to edit?\n1. Website\n2. Statement\nEnter:");
                                            int eNum = in.nextInt();
                                            System.out.print("\nWhat would you like to change it to? ");
                                            String eStr = in.next();
                                            fi.edit(user, eNum, eStr);
                                        }
                                        case 2 -> {
                                            voteNow(fi);
                                        }
                                        case 3 -> {
                                            viewCandidate(fi);
                                        }
                                        default -> System.out.println("invalid selection.");

                                    }
                                }
                                else if(eFlag && !vFlag){
                                    System.out.println("Candidate");
                                    System.out.print("Candidate Options: \n1. Edit Account Details.\n2. View Other Candidates.\nEnter Number Here: ");
                                    int cNum = in.nextInt();
                                    switch (cNum) {
                                        case 1 -> {
                                            System.out.println(fi.canEdit(user));
                                            System.out.print("Which of the entries would you like to edit?\n1. Website\n2. Statement\nEnter:");
                                            int eNum = in.nextInt();
                                            System.out.print("\nWhat would you like to change it to? ");
                                            String eStr = in.next();
                                            fi.edit(user, eNum, eStr);
                                        }
                                        case 2 -> {
                                            viewCandidate(fi);
                                        }
                                        default -> System.out.println("invalid selection.");

                                    }
                                }
                                else if(!eFlag && !vFlag){
                                    System.out.println("Candidate");
                                    System.out.println("Election is currently closed. Returning to main menu...");
                                }
                                else{System.out.println("Returning to main menu...");}
                            }


                            //voter case
                            case 3 -> {
                                if(eFlag && vFlag) {
                                    System.out.println("Voter");
                                    voteCheck = fi.checkVote(user);
                                    //if user has not yet voted
                                    if (voteCheck) {
                                        System.out.println("eligible to vote.");

                                        System.out.print("What would you like to do? \n1.View Candidates\n2.Vote now!\nEnter: ");
                                        int n = in.nextInt();
                                        if (n == 1) {
                                            viewCandidate(fi);
                                        } else if (n == 2) {
                                            voteNow(fi);
                                        } else {
                                            System.out.println("Invalid choice.");
                                        }

                                        //if user has already voted
                                    } else {
                                        System.out.println("already voted.");
                                    }
                                }
                                else if(eFlag && !vFlag){
                                    System.out.println("Voter");
                                    System.out.print("What would you like to do? \n1.View Candidates\n2.View Voting Results.\n3.Return to Main Menu.\nEnter: ");
                                    int n = in.nextInt();
                                    if (n == 1) {
                                        viewCandidate(fi);
                                    }
                                    else if (n == 2){
                                        System.out.println(fi.voteTable());
                                    }
                                    else if (n == 3) {
                                        System.out.println("Returning to main menu...");
                                    } else {
                                        System.out.println("Invalid choice. Returning to main menu...");
                                    }
                                }
                                else{System.out.println("Returning to main menu...");}
                            }
                            //default case if there is an error in the account type
                            default -> System.out.println("invalid account type.");
                        }

                    } else {
                        System.out.println("incorrect password");
                    }
                }
                else{
                    System.out.println("Username not found.");
                }

            }
        } catch(Exception e) {
            System.err.println("FileServer exception: "+ e.getMessage());
            e.printStackTrace();
        }

    }

    public static void viewCandidate(FileInterface fi) throws IOException {
        System.out.println("List of candidates: ");
        canList = fi.getCandidates();
        for (String s : canList) {
            String rem = s.substring(3, 11);
            System.out.print(s.replace(rem, "") + "\n");
        }
        System.out.print("Enter the number for which candidate you would like to view: ");
        canGet = in.nextInt();
        System.out.println(fi.canInfo(canGet));
    }

    public static void voteNow(FileInterface fi) throws IOException {
        //vote
        System.out.println("List of candidates: ");
        canList = fi.getCandidates();
        for (String s : canList) {
            System.out.print(s + "\n");
        }
        System.out.print("Enter the number for which candidate you would like to vote: ");
        canVote = in.nextInt();
        System.out.println(fi.vote(user, canVote));
    }
}
