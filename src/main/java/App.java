/**
 * Created by Cagy on 2/21/2018.
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        RecieveGmail gmail = new RecieveGmail();
        while (true) {
            gmail.read();
            Thread.sleep(60000);
        }
    }
}
