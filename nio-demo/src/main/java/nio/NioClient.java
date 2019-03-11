package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by diao on 2019/3/11
 */
public class NioClient {
    static Charset charset = Charset.forName("UTF-8");

    public static void main(String[] args) {
        try (SocketChannel sc = SocketChannel.open();) {
            //连接会阻塞
            boolean connected = sc.connect(new InetSocketAddress("localhost",9200));

            System.out.println("connected=" + connected);

            //写
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入:");
            String mess = scanner.nextLine();
            ByteBuffer bf = ByteBuffer.wrap(mess.getBytes(charset));

            while (bf.hasRemaining()) {
                int writedCount = sc.write(bf);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
