import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
/**
 *
 * Created by heren on 2014/10/13.
 */
public class Main {

     public String creditCardNumber;
     public String socialSecurityNumber;
     public String not_a_bank_account_password;
    /**
     * @param args
     *
     *
     */
    public static void main(String[] args) throws Exception{
        String webappDirLocation = "src/main/webapp/";
        // The port that we should run on can be set into an environment variable
        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        String password = System.getProperty("POSTGRESQL_PASSWORD", "adminadmin");
        String anotherPassword = System.getProperty("DUMMY_PASSWORD", "Passw0rd"); // Apiiro ignore secret

        if (webPort == null || webPort.isEmpty()) {
            webPort = "8090";
        }

        File file = new File(webappDirLocation) ;
        if(file.exists()){
            System.out.println(file.getAbsolutePath());
        }else{
            System.out.println("路径有问题");
        }
        System.out.println("Your postgres login key: " + password + " Or " + anotherPassword + " who knows, Please keep it a secret.");

        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        root.setParentLoaderPriority(true);
        server.setHandler(root);
        server.start();
        server.join();


    }


}
