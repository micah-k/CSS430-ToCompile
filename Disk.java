import java.io.*;

public class Disk extends Thread {
    private static final boolean DEBUG = true;

    public static final int blockSize = 512;
    private final int trackSize = 10;
    private final int transferTime = 20;
    private final int delayPerTrack = 1;
    private int diskSize;

    private byte data[];

    private int command;
    private final int IDLE = 0;
    private final int READ = 1;
    private final int WRITE = 2;
    private final int SYNC = 3;
    private boolean readyBuffer;

    private byte[] buffer;
    private int currentBlockId;
    private int targetBlockId;

    public Disk(int totalBlocks) {
        if( DEBUG )
            System.out.println("DISK THREAD:" + getName());

        diskSize = (totalBlocks > 0) ? totalBlocks : 1;
        data = new byte[diskSize * blockSize];
        command = IDLE;
        readyBuffer = false;
        buffer = null;
        currentBlockId = 0;
        targetBlockId = 0;
        try {
            FileInputStream ifstream = new FileInputStream("DISK");
            int readableSize = (ifstream.available() < data.length) ?
                    ifstream.available() : data.length;
            ifstream.read(data, 0, readableSize);
            ifstream.close();
        } catch (FileNotFoundException e) {
            SysLib.cerr("threadOS: DISK created\n");
        } catch (IOException e) {
            SysLib.cerr(e.toString() + "\n");
        }
    }

    public synchronized boolean read(int blockId, byte buffer[]) {
        String thread_name= null;
        if( DEBUG ) {
            thread_name = Thread.currentThread().getName();
            System.out.println(thread_name + ": disk.read (about to check command/readyBuffer)");
        }

        if (blockId < 0 || blockId > diskSize) {
            SysLib.cerr("threadOS: a wrong blockId for read\n");
            return false;
        }

        if (command == IDLE && readyBuffer == false) {
            this.buffer = buffer;
            targetBlockId = blockId;
            command = READ;
            if( DEBUG )
                System.out.println(thread_name + ": disk.read: transmitting (command==READ, readyBuffer is false)");
            notify();
            if(DEBUG) {
                System.out.println(thread_name + ": READ: notify'd disk thread");
            }
            return true;
        } else{
            if(DEBUG)
                System.out.println(thread_name + ": READ: disk not ready, returning false (command="+command+" readyBuffer="+readyBuffer);
            return false;
        }
    }

    public synchronized boolean write(int blockId, byte buffer[]) {
        String thread_name= null;
        if( DEBUG ) {
            thread_name = Thread.currentThread().getName();
            System.out.println(thread_name + ": disk.write (about to check command/readyBuffer)");
        }
        if (blockId < 0 || blockId > diskSize) {
            SysLib.cerr("threadOS: a wrong blockId for write\n");
            return false;
        }

        if (command == IDLE && readyBuffer == false) {
            this.buffer = buffer;
            targetBlockId = blockId;
            command = WRITE;
            if( DEBUG )
                System.out.println(thread_name + ": disk.write: transmitting (command==WRITE, readyBuffer is false)");
            notify();
            if(DEBUG)
                System.out.println(thread_name+": WRITE: notify'd disk thread");
            return true;
        } else{
            if(DEBUG)
                System.out.println(thread_name + ": WRITE: disk not ready, returning false (command="+command+" readyBuffer="+readyBuffer);
            return false;
        }
    }

    public synchronized boolean sync() {

        if (command == IDLE && readyBuffer == false) {
            command = SYNC;
            notify();
            // System.out.println( "DISK sync notified the disk" );
            return true;
        } else
            return false;
    }

    public synchronized boolean testAndResetReady() {
        if(DEBUG)
            System.out.println( Thread.currentThread().getName()+": testAndResetReady: command: "+command+" readyBuffer:"+readyBuffer);
        if (command == IDLE && readyBuffer == true) {
            if(DEBUG)
                System.out.println( Thread.currentThread().getName()+": testAndResetReady: command still IDLE, changing readyBuffer to false");
            readyBuffer = false;
            return true;
        } else
            return false;
    }

    public synchronized boolean testReady() {
        if (command == IDLE && readyBuffer == true) {
            return true;
        } else
            return false;
    }

    private synchronized void waitCommand() {

        if(DEBUG)
            System.out.println( Thread.currentThread().getName()+": waitCommand pre-loop: command: "+command+" readyBuffer:"+readyBuffer);
        while (command == IDLE) {
            try {
                if(DEBUG)
                    System.out.println(getName()+": waitCommand: about to wait (command still IDLE) readyBuffer:" + readyBuffer);
                wait();
                if(DEBUG)
                    System.out.println(getName()+": waitCommand: finished wait, now resuming command="+command + " readyBuffer: " + readyBuffer);
            } catch (InterruptedException e) {
                SysLib.cerr(e.toString() + "\n");
            }
            readyBuffer = false;
        }
    }

    private void seek() {
        int seekTime = transferTime + delayPerTrack
                * Math.abs(targetBlockId / trackSize - currentBlockId / trackSize);
        try {
            if(DEBUG)
                System.out.println(getName()+": seek: about to sleep for "+ seekTime +"ms");
            Thread.sleep(seekTime);
        } catch (InterruptedException e) {
            SysLib.cerr(e.toString() + "\n");
        }
        currentBlockId = targetBlockId;
    }

    private synchronized void finishCommand() {
        command = IDLE;
        readyBuffer = true;
        if(DEBUG)
            System.out.println(getName()+": finishCommand (command now IDLE, readyBuffer now true)");
        SysLib.disk(); // a disk interrupt
    }

    public void run() {

        while (true) {
            waitCommand();
            seek();
            if(DEBUG)
                System.out.println( "Disk: command = " + command );

            switch (command) {
                case READ:
                    System.arraycopy(data, targetBlockId * blockSize,
                            buffer, 0,
                            blockSize);
                    break;
                case WRITE:
                    System.arraycopy(buffer, 0,
                            data, targetBlockId * blockSize,
                            blockSize);
                    break;
                case SYNC:
                    try {
                        FileOutputStream ofstream = new FileOutputStream("DISK");
                        ofstream.write(data);
                        ofstream.close();
                    } catch (FileNotFoundException e) {
                        SysLib.cerr(e.toString());
                    } catch (IOException e) {
                        SysLib.cerr(e.toString());
                    }
                    // SysLib.cerr( "threadOS: DISK synchronized\n" );
                    break;
            }
            finishCommand();
        }
    }
}
