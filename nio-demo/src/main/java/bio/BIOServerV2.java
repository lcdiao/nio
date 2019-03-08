package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * 阻塞式IO：多线程版：能处理多个socket连接
 * Created by diao on 2019/3/8
 */
public class BIOServerV2 {

    private static Charset charset = Charset.forName("UTF-8");

    public static void main(String[] args) {
        int port = 9010;
        try (ServerSocket ss = new ServerSocket(port)){
            while (true){
                Socket s = ss.accept();
                //新开一个线程去处理这个连接
                new Thread(new SocketProcess(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SocketProcess implements Runnable {
        Socket s;

        public SocketProcess(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream(), charset));) {
                //接收数据
                String mess = null;
                while ((mess = reader.readLine()) != null) {
                    System.out.println(mess);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
