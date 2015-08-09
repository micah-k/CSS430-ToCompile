import java.util.*;

public class Cache
{
    private int bSize;
    private int victim;
    private Vector<byte[]> cache;

    private class Entry
    {
        public int frame;
        public boolean refbit;
        public boolean dirtybit;

        public Entry()
        {
            frame = -1;
            refbit = false;
            dirtybit = false;
        }
    }

    private Entry[] pageTable = null;


    public Cache(int blockSize, int cacheBlocks)
    {
        bSize = blockSize;
        victim = 0;
        cache = new Vector<byte[]>();
        pageTable = new Entry[cacheBlocks];

        for (int i = 0; i < cacheBlocks; i++)
        {
            byte[] block = new byte[blockSize];
            cache.add(block);

            pageTable[i] = new Entry();
        }
    }

    private int findFreePage()
    {
        for (int i = 0; i < pageTable.length; i++)  // For each page in the table,
        {
            if (pageTable[i].frame == -1)           // If the page is invalid,
                return i;                           // Return its position.
        }
        return -1;
    }

    private int nextVictim()
    {
        // Entry.refbit is a bool that shows that a block has been recently used.
        // Entry.dirtybit is a bool that shows that a block has been modified.
        int wrapMark = victim; // Place marker to indicate full rotation.
        boolean allowDirty = false;
        boolean falsifyRefBit = false;
        while (true)
        {
            victim++;
            if (victim >= pageTable.length) victim = 0;

            if (!pageTable[victim].refbit && (!pageTable[victim].dirtybit || allowDirty))
                return victim;

            if (victim == wrapMark)     // If we've completed a cycle around the cache,
            {
                if (allowDirty) falsifyRefBit = true;   // If we've completed the second cycle, start removing second chances.
                allowDirty = !allowDirty;               // Allow overwriting of dirty bits
            }

            if (falsifyRefBit) pageTable[victim].refbit = false;
        }
    }

    private void writeBack(int victimEntry)
    {
        if (pageTable[victimEntry].frame != -1)
        {
            SysLib.rawwrite(pageTable[victimEntry].frame, cache.elementAt(victimEntry));
            pageTable[victimEntry].dirtybit = false;
        }
    }

    public synchronized boolean read(int blockId, byte buffer[])
    {
        if (blockId < 0)
            return false;

        for (int i = 0; i < pageTable.length; i++)
        {
            if (pageTable[i].frame == blockId)
            {
                byte[] block = cache.elementAt(i);
                System.arraycopy(block, 0, buffer, 0, bSize);
                pageTable[i].refbit = true;
                return true;
            }
        }

        int freePage = findFreePage();
        if (freePage == -1) freePage = nextVictim();
        if (pageTable[freePage].dirtybit) writeBack(freePage);

        SysLib.rawread(blockId, buffer);

        byte[] block = new byte[bSize];
        System.arraycopy(buffer, 0, block, 0, bSize);
        cache.set(freePage, block);
        pageTable[freePage].frame = blockId;
        pageTable[freePage].refbit = true;
        return true;
    }

    public synchronized boolean write(int blockId, byte buffer[])
    {
        if (blockId < 0)
            return false;

        for (int i = 0; i < pageTable.length; i++)
        {
            if (pageTable[i].frame == blockId)
            {
                byte[] block = new byte[bSize];
                System.arraycopy(buffer, 0, block, 0, bSize);
                cache.set(i, block);
                pageTable[i].refbit = true;
                pageTable[i].dirtybit = true;
            }
        }

        int freePage = findFreePage();
        if (freePage == -1) freePage = nextVictim();
        if (pageTable[freePage].dirtybit) writeBack(freePage);

        SysLib.rawwrite(blockId, buffer);

        byte[] block = new byte[bSize];
        System.arraycopy(buffer, 0, block, 0, bSize);
        cache.set(freePage, block);
        pageTable[freePage].frame = blockId;
        pageTable[freePage].refbit = true;
        pageTable[freePage].dirtybit = true;
        return true;
    }

    public synchronized void sync()
    {
        for (int i = 0; i < pageTable.length; i++)
        {
          if (pageTable[i].dirtybit) writeBack(i);
        }
        SysLib.sync();
    }

    public synchronized void flush()
    {
        for (int i = 0; i < pageTable.length; i++)
        {
            if (pageTable[i].dirtybit) writeBack(i);
            pageTable[i].refbit = false;
            pageTable[i].frame = -1;
        }
        SysLib.sync();
    }
}
