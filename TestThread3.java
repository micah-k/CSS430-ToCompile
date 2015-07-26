import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by Michael on 3/3/2015.
 */


public class TestThread3 extends Thread {
    String name;
    int iterations;
    TestType type;


    private long submissionTime;
    private long completionTime;

    private enum TestType {
        COMPUTE,
        DISK_OPS,
    }

    public TestThread3(String args[]) {
        name = args[0];
        System.out.println(Thread.currentThread().getName() + " TEST THREAD : " + getName()+" : "+name);
        iterations = Integer.parseInt(args[1]);
        type = TestType.valueOf(args[2]);

        submissionTime = new Date().getTime();
    }

    public void run() {

        synchronized (Test3.syncObj)
        {
            try {
                Test3.syncObj.wait();
            } catch( InterruptedException e) {}
        }
        SysLib.cout(Thread.currentThread().getName()+" : " + getName() +" STARTING TestThread3 (" + name + ")\n");
        Random rng = new Random();
        int NUMBER_OF_BLOCKS = 1000;
        int BLOCK_SIZE = 512;
        byte[] randomData = new byte[BLOCK_SIZE];
        Arrays.fill(randomData, (byte)'A');

        long total = 0;
        for (int i = 0; i < iterations; i++) {
            SysLib.cout(Thread.currentThread().getName()+": [" +name+"]: Next iteration ("+(i+1)+" of "+iterations+") TestThread3: \n");
            switch (type) {
                case COMPUTE:
                    total += 1;
                    SysLib.sleep(10);
                    break;
                case DISK_OPS:
                    // pick random block on the disk
                    // write something to that block
                    // read that thing from that block
                    int randomBlock = rng.nextInt(NUMBER_OF_BLOCKS);

                    //System.out.println(Thread.currentThread().getName() +": (" + name + "): about to rawwrite");
                    SysLib.rawwrite(randomBlock, randomData);
                    //System.out.println(Thread.currentThread().getName() + ": (" + name + "): about to rawread");
                    SysLib.rawread(randomBlock, randomData);
                    break;
            }
        }

        completionTime = new Date().getTime();
        SysLib.cout(Thread.currentThread().getName()+" [" + name + "]:" +
                "\n\tturnaround time = " + (completionTime - submissionTime) +
                "\n");

        SysLib.exit();
    }
}
