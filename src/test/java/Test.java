import com.github.EnterpriseJavaSolutions.EnterpriseAuthSDK;
import com.github.EnterpriseJavaSolutions.exceptions.AuthException;
import com.github.EnterpriseJavaSolutions.models.User;

public class Test {
    static EnterpriseAuthSDK sdk = new EnterpriseAuthSDK("https://enterprise-auth.vercel.app");

    public static void main(String[] args) {
        System.out.println("Testing error handling...");
        try {
            User user = sdk.login("invalid creds", "invalid creds");
        } catch(AuthException e) {
            e.printStackTrace();
        }
        System.out.println("Testing normal behaviour...");
        try {
            User user = sdk.login("sdk", "sdk");
            System.out.println("HWID: " + user.hwid);
            System.out.println("ID: " + user.id);
            System.out.println("Username: " + user.username);
            System.out.println("Testing HWID setting...");
            if(!user.hwidSet) {
                user.setHWID("this is my hwid awaawawawawawa");
            }
            System.out.println("HWID: " + user.hwid);
        } catch(AuthException e) {
            e.printStackTrace();
        }
    }
}
