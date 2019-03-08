package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.*;

/**
 * 阻塞式IO：线程池版
 * 阻塞等待接收客户端的数据时，这段时间占着线程，而池中线程数是有限的，
 * 并发量大时，将导致没有线程处理请求，请求的响应时间长，甚至拒绝服务
 * Created by diao on 2019/3/8
 */
public class BIOServerV3 {

    private static Charset charset = Charset.forName("UTF-8");

    public static void main(String[] args) {
        int port = 9010;
        int threads = 100;
        //创建固定池大小的线程池,事先创建好100个线程
        ExecutorService tpool = Executors.newFixedThreadPool(threads);

        try (ServerSocket ss = new ServerSocket(port)){
            while (true){
                Socket s = ss.accept();
                //丢到线程池中去跑
                tpool.execute(new SocketProcess(s));
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
