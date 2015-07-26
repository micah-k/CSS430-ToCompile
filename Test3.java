import java.util.Date;

/**
 * Created by Michael on 3/3/2015.
 */
public class Test3 extends Thread {

    public static Object syncObj = new Object();

    public void run()  {
        long submissionTime = new Date().getTime();
        int NUM_THREAD_PAIRS = 3;
        int ITERATIONS = 2;
        String[] args;
        int tid;
        String name;
        for (int i = 0; i < NUM_THREAD_PAIRS; i++) {
//            name = "COMPUTE-" + i;
//            args = SysLib.stringToArgs("TestThread3 "+ name + " " + ITERATIONS + " COMPUTE");
//            tid = SysLib.exec(args);
//            SysLib.cout("Created " + name + "\n");

            name = "DISK_OPS-" + i;
                    args = SysLib.stringToArgs("TestThread3 " + name +  " " + ITERATIONS + " DISK_OPS");
            tid = SysLib.exec(args);
            SysLib.cout("Created " + name+ "\n");
        }

        try {
            Thread.sleep(4000);
            synchronized (Test3.syncObj) {
                Test3.syncObj.notifyAll();
            }
        }
        catch(InterruptedException e) {}

        for (int i = 0; i < NUM_THREAD_PAIRS; i++)
            tid = SysLib.join();

        long completionTime = new Date().getTime();

        SysLib.cout("Test3: =====================================" +
                "\n\tturnaround time = " + (completionTime - submissionTime) +
                "\n");
        StringBuffer sb = new StringBuffer(100);
//        SysLib.cin(sb);

        SysLib.exit();
    }

}
