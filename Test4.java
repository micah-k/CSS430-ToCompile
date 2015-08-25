import java.util.*;

public class Test4 extends Thread
{
    boolean enabled;
    int testSelection;

    int[] blockAddrs;
    byte[] readBytes;
    byte[] writtenBytes;
    Random r;


    private void readSelect(int blockId, byte[] buffer)
    {
        if (enabled)
            SysLib.cread(blockId, buffer);
        else 
            SysLib.rawread(blockId, buffer);
    }


  
    private void writeSelect(int blockId, byte[] buffer)
    {
        if (enabled)
            SysLib.cwrite(blockId, buffer);
        else
            SysLib.rawwrite(blockId, buffer);
    }

    public Test4(String[] args)
    {
        enabled = args[0].equals("-enabled");
        testSelection = Integer.parseInt(args[1]);
        r = new Random();
        readBytes = new byte[512];
        writtenBytes = new byte[512];
        blockAddrs = new int[200];
    }

    private void fillBlockAddrs()
    {
        for (int i = 0; i < 200; i++)
        {
            switch (testSelection)
            {
                case 1: 
                    blockAddrs[i] = Math.abs(r.nextInt()) % 512;
                    break;
                case 2: 
                    blockAddrs[i] = Math.abs(r.nextInt()) % 10;
                    break;
                case 3: 
                    blockAddrs[i] = (Math.abs(r.nextInt() % 10) == 9) ?
                            Math.abs(r.nextInt()) % 512 :
                            Math.abs(r.nextInt()) % 10;
                    break;
                case 4: 
                    blockAddrs[i] = (i % 11) * 80 + 1;
                    if(
                    break;
                default:
                    blockAddrs[i] = 1; // If you got here I don't even care.
            }
        }

    }

    // Thanks to fillBlockAddrs(), I only have to write one function for all four tests.
    // And to think I spent so long writing four nearly identical functions for this.
    private void testAccesses()
    {
        int i;
        int byteNum;

        fillBlockAddrs();

        for (i = 0; i < 200; i++)
        {
            for (byteNum = 0; byteNum < 512; byteNum++)
            {
                writtenBytes[byteNum] = (byte)(r.nextInt() % 64);
            }
            writeSelect(blockAddrs[i], writtenBytes);
            readSelect(blockAddrs[i], readBytes);
            for (byteNum = 0; byteNum < 512; byteNum++)
            {
                if (readBytes[byteNum] != writtenBytes[byteNum])
                {
                    SysLib.cerr("Iteration " + i +": Error in block " +
                        blockAddrs[i] + ", byte " + byteNum + ". Expected " +
                        writtenBytes[byteNum] + " got " + readBytes[byteNum] + "\n");
                    SysLib.exit();
                }
            }
        }
    }

    public void run()
    {
        long submissionTime = new Date().getTime();

        SysLib.flush();
        testAccesses();

        long completionTime = new Date().getTime();

        String test;
        switch(testSelection)
        {
            case 1:
                test = "random accesses";
                break;
            case 2:
                test = "localized accesses";
                break;
            case 3:
                test = "mixed accesses";
                break;
            case 4:
                test = "adversary accesses";
                break;
            default:
                test = "\\\\BAD TEST ERROR\\\\";
        }
        SysLib.cout("Test4, " + test + " with cache " +
                (enabled ? "enabled" : "disabled") + 
                ":\n\tturnaround time = " + (completionTime - submissionTime) + "\n");

        SysLib.exit();
    }

}
