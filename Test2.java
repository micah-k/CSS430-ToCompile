import java.util.Date;

class Test2 extends Thread {

    public void run() {
        String[] args1 = SysLib.stringToArgs("TestThread2 a 5000 0");
        String[] args2 = SysLib.stringToArgs("TestThread2 b 1000 0");
        String[] args3 = SysLib.stringToArgs("TestThread2 c 3000 0");
        String[] args4 = SysLib.stringToArgs("TestThread2 d 6000 0");
        String[] args5 = SysLib.stringToArgs("TestThread2 e 500  0");
        int tid;
        tid = SysLib.exec(args1);
        tid = SysLib.exec(args2);
        tid = SysLib.exec(args3);
        tid = SysLib.exec(args4);
        tid = SysLib.exec(args5);
        for (int i = 0; i < 5; i++)
            tid = SysLib.join();
        //SysLib.cout( "Test2 finished\n" );
        SysLib.exit();
    }
}
