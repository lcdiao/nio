package nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by diao on 2019/3/8
 */
public class NioServer {
    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetDecoder decoder = charset.newDecoder();

    public static void main(String[] args) throws IOException {
        //创建一个selector
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        int port = 9200;
        ssc.bind(new InetSocketAddress(port));

        //2、注册到selector
        //要非阻塞
        ssc.configureBlocking(false);

        //ssc向selector注册。监听连接到来
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        //连接的计数
        int connectionCount = 0;
        //极少量线程
        int threads = 3;
        ExecutorService tpool = Executors.newFixedThreadPool(threads);

        while (true){
            //阻塞等待就绪的事件
            int readyChannelsCount = selector.select();
            //因为select()阻塞可以被中断
            if(readyChannelsCount == 0){
                continue;
            }

            //得到就绪的channel的key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {

                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    //a connection was accepted by a ServerSocketChannel
                    ServerSocketChannel ssssc = (ServerSocketChannel) key.channel();
                    //接收连接
                    SocketChannel cc = ssssc.accept();

                    //请selector帮忙检测数据到了没
                    //设置非阻塞
                    cc.configureBlocking(false);
                    //向selector注册
                    cc.register(selector,SelectionKey.OP_READ,++connectionCount);
                } else if (key.isConnectable()) {
                    //a connection was established with a remote server
                } else if (key.isReadable()) {
                    //a channel is ready for reading
                    //交给线程池去处理数据读
                    tpool.execute(new SocketProcess(key));

                    //取消Selector注册，防止线程池处理不及时,重复选择
                    key.cancel();
                } else if (key.isWritable()) {
                    //a channel is ready for writing
                }

                //处理了，一定要从selectedKey集中移除
                keyIterator.remove();
            }
        }
    }
    static class SocketProcess implements Runnable {
        SelectionKey key;

        public SocketProcess(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            try {
                System.out.println("连接" + key.attachment() + " 发来了:" + readFromChannel());
                //如果连接不需要了，就关闭
                key.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String readFromChannel() throws IOException {
            SocketChannel sc = (SocketChannel)key.channel();

            int bfsize = 1024;
            ByteBuffer rbf = ByteBuffer.allocateDirect(bfsize);

            //定义个更大的buffer
            ByteBuffer bigBf = null;

            //读的次数计数
            int count = 0;
            while ((sc.read(rbf)) != -1) {
                count++;

                ByteBuffer temp = ByteBuffer.allocateDirect(bfsize * (count + 1));
                if(bigBf != null){
                    //将buffer由写转为读模式
                    bigBf.flip();
                    temp.put(bigBf);
                }

                bigBf = temp;

                //将这次读到的数据放入大buffer
                rbf.flip();
                bigBf.put(rbf);
                //为下次读，清理Buffer
                rbf.clear();
            }
            if (bigBf != null){
                bigBf.flip();
                try {
                    //将字节转为字符，返回接收到的字符串
                    return decoder.decode(bigBf).toString();
                } catch (CharacterCodingException e){
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
