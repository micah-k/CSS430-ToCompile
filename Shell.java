import java.util.*;

public class Shell extends Thread {
    

    public void run() {
        int repetition = 1;
        while(true)
        {
            //Output prompt
            SysLib.cout("shell["+repetition+"]% ");

            //Receive input
            StringBuffer input = new StringBuffer();
            SysLib.cin(input);

            //Break if commanded
            if(input.toString().equals("exit")) {
                break;
            }

            //Parse input into commands and delimiters
            String[] tokens = input.toString().split("\\s");
            String[] commands = new String[tokens.length + 1]; //Extra space on end, so we don't go out of bounds later.
            int curcmd = 0;
            for(int i = 0; i < tokens.length; i++)
            {
                if(tokens[i].equals("&") || tokens[i].equals(";"))
                {
                    curcmd++;                       //Advance to delimiter space,
                    commands[curcmd] = tokens[i];   //Insert delimiter,
                    curcmd++;                       //Advance to next command.
                }

                else
                {
                    if(commands[curcmd] == null)    //If the current command is empty, fill it with just the next token.
                    {
                        commands[curcmd] = tokens[i];
                    }

                    else                            //If there is something in the current command, append the next token.
                    {
                        commands[curcmd] = commands[curcmd] + " " + tokens[i];
                    }
                }
            }

            for(int i = 0; commands[i] != null; i++)    //There's less in the array than its length, so this should only run until the current value is empty.
            {
                if(commands[i].equals(";"))             //Wait for termination
                {
                    SysLib.join();
                }
                else if(!commands[i].equals("&"))       //If this is a concurrent delimiter, we just go to the next process to execute.
                {
                    SysLib.exec(SysLib.stringToArgs(commands[i]));
                }
            }
            repetition++;
        }
    }
}
