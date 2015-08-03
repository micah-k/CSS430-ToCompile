/**
 * Created by Michael on 7/22/2015.
 */
import java.util.Vector;
public class QueueNode {
    private Vector<int> tids;

    public QueueNode()
    {
        tids = new Vector<int>();
    }

    public synchronized int sleep()
    {
        if (size() == 0)
        {
            try
            {
                wait(); // Wait...
            }
            catch (InterruptedException e){}  // ...until interrupted.
        }

        return tids.remove(size() - 1); // Once we've come back from waiting, pop a thread ID off the queue. (If we've woken up, there should be one.)
    }

    public synchronized void wakeup(int arg)
    {
        tids.add(arg);
        notify();
    }

    // This is needed outside the class, so I can't just use tids.size().
    public synchronized int size()
    {
        return tids.size();
    }
}
