import java.io.*;
import java.rmi.*;

public class ElectionServer {
    public static void main(String argv[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            FileInterface fi = new FileImpl("FileServer");
            Naming.rebind("//127.0.0.1/FileServer", fi);
            System.out.println("Server Ready - service is running");
        } catch (Exception e) {
            System.out.println("FileServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
