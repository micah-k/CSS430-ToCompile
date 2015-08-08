import java.util.Date;

/**
 * Created by Michael on 3/3/2015.
 */


public class TestThread3a extends Thread 
{

    private long submissionTime;
    private long completionTime;

    public void run()
    {
        submissionTime = new Date().getTime();
        synchronized (Test3.syncObj) // So it turns out this was all I needed to make sure the main Test3 code didn't wait for threads that already ended.
        {
            try
            {
                Test3.syncObj.wait();
            }
            catch( InterruptedException e) {}
        }

        SysLib.cout(Thread.currentThread().getName()+" : " + getName() +" STARTING TestThread3a\n");

        long total = 0;
        for (int i = 0; i < 20; i++) {
            total += 1;
            SysLib.sleep(10);
        }

        completionTime = new Date().getTime();
        SysLib.cout(Thread.currentThread().getName()+" [TestThread3a]:" +
                "\n\tturnaround time = " + (completionTime - submissionTime) +
                "\n");

        SysLib.exit();
    }
}
