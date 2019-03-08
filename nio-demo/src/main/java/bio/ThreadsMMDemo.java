package bio;

import java.util.concurrent.CountDownLatch;

/**
 * Created by diao on 2019/3/8
 */
public class ThreadsMMDemo {
    public static void main(String[] args) {
        //倒计时锁存器
        /*
        CountDownLatch是通过一个计数器来实现的，计数器的初始值为线程的数量。
        每当一个线程完成了自己的任务后，计数器的值就会减1。当计数器值到达0时，
        它表示所有的线程已经完成了任务，然后在闭锁上等待的线程就可以恢复执行任务。
         */
        CountDownLatch cdl = new CountDownLatch(1);
        try{
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0;i < 5000;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //阻塞
                        cdl.await();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();

            System.out.println("i=" + i);
            //在C:\Program Files\Java\jdk1.8.0_121\bin下的jvisualvm.exe可以查看cpu、堆等的使用情况
            //5000个线程大约占了500M的空间
            //所以不能频繁得创建线程
        }
    }
}
