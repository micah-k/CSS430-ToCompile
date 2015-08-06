import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by Michael on 3/3/2015.
 */


public class TestThread3b extends Thread {


    private long submissionTime;
    private long completionTime;

    public void run() {

        synchronized (Test3.syncObj)
        {
            try
            {
                Test3.syncObj.wait();
            }
            catch( InterruptedException e) {}
        }
        
        SysLib.cout(Thread.currentThread().getName()+" : " + getName() +" STARTING TestThread3b\n");
        Random rng = new Random();
        int NUMBER_OF_BLOCKS = 1000;
        int BLOCK_SIZE = 512;
        byte[] randomData = new byte[BLOCK_SIZE];
        Arrays.fill(randomData, (byte)'A');

        for (int i = 0; i < 20; i++) {
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

        completionTime = new Date().getTime();
        SysLib.cout(Thread.currentThread().getName()+" [TestThread3b]:" +
                "\n\tturnaround time = " + (completionTime - submissionTime) +
                "\n");

        SysLib.exit();
    }
}
