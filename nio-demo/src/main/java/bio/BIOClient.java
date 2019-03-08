package bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by diao on 2019/3/8
 */
public class BIOClient implements Runnable {

    private String host;

    private int port;

    private Charset charset = Charset.forName("UTF-8");

    public BIOClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket s = new Socket(host,port); OutputStream out = s.getOutputStream();){
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入：");
            String mess = sc.nextLine();
            out.write(mess.getBytes(charset));
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BIOClient client = new BIOClient("localhost",9010);
        client.run();
    }
}
