/**
 * Created by Michael on 7/22/2015.
 */
import java.util.Vector;
public class QueueNode {
    private Vector<Integer> tids;

    public QueueNode()
    {
        tids = new Vector<Integer>();
    }

    public synchronized int sleep()
    {
        System.out.println("Micah: QueueNode sleeping...");
        if (size() == 0)
        {
            try
            {
                wait(); // Wait...
            }
            catch (InterruptedException e){}  // ...until interrupted.
        }
        System.out.println("Micah: Sleep interrupted up by tid: [" + tids.get(0) + "]");

        return tids.remove(0); // Once we've come back from waiting, pop a thread ID off the queue. (If we've woken up, there should be one.)
    }

    public synchronized void wakeup(int tid)
    {
        System.out.println("Micah: Tid: [" + tid + "] calls wakeup");
        tids.add(tid);
        notify();
    }

    // This is needed outside the class, so I can't just use tids.size().
    // UPDATE: Ignore previous comment; size function calls in SyncQueue removed.
    // Keeping size() in case of unknown dependencies.
    public synchronized int size()
    {
        return tids.size();
    }
}
