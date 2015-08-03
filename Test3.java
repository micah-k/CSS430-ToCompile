import java.util.Date;

/**
 * Created by Michael on 3/3/2015.
 */
public class Test3 extends Thread
{
    private int numThreadPairs = 3; // Defaults to 3 pairs.
    public static Object syncObj = new Object();

    public Test3(String[] args)
    {
        numThreadPairs = Integer.parseInt(args[0]);
    }

    public void run()  {
        long submissionTime = new Date().getTime();
        
        int tid;
        String name;
        for (int i = 0; i < numThreadPairs; i++)
        {
            SysLib.exec(SysLib.stringToArgs("TestThread3a"));
            SysLib.exec(SysLib.stringToArgs("TestThread3b"));
        }

        try
        {
            Thread.sleep(4000);
            synchronized (Test3.syncObj)
            {
                Test3.syncObj.notifyAll();
            }
        }
        catch(InterruptedException e) {}

        for (int i = 0; i < numThreadPairs * 2; i++)
        {
            SysLib.join();
        }

        long completionTime = new Date().getTime();

        SysLib.cout("Test3: =====================================" +
                "\n\tturnaround time = " + (completionTime - submissionTime) +
                "\n");

        SysLib.exit();
    }

}
