#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdio.h>

int main(int argc, char** argv){
    enum {RD, WR};
    int n, fdwcgrep[2], fdgrepps[2];
    pid_t pidmainwc, pidwcgrep, pidgrepps;
    char buf[100];

    //Fork to first process
    if((pidmainwc = fork()) < 0)
    {
      perror("Fork error, grep/ps.");
    }

    else if(pidmainwc == 0)        //Ps exec
    {
        //Initialize first pipe, and fork to second process
        if(pipe(fdwcgrep) < 0)
        {
        perror("Pipe error, wc/grep.");
        }
        else if((pidwcgrep = fork()) < 0)
        {
        perror("Fork error, wc/grep.");
        }

        else if(pidwcgrep == 0)         //Grep code
        {
            //Initialize second pipe, and fork to third process
            if(pipe(fdgrepps) < 0)
            {
              perror("Pipe error, grep/ps.");
            }
            else if((pidgrepps = fork()) < 0)
            {
              perror("Fork error, grep/ps.");
            }

            else if(pidgrepps == 0)        //Ps exec
            {
                dup2(fdgrepps[WR], STDOUT_FILENO);
                close(fdgrepps[WR]);
                execlp("ps", "ps", "-A", NULL);
            }

            else                     //Grep exec
            {
                dup2(fdgrepps[RD], STDIN_FILENO);
                close(fdgrepps[RD]);
                dup2(fdwcgrep[WR], STDOUT_FILENO);
                close(fdwcgrep[WR]);
                execlp("grep", "grep", argv[1], NULL);
            }

        }

        else                      //Wc exec
        {
            dup2(fdwcgrep[RD], STDOUT_FILENO);
            close(fdwcgrep[RD]);
            execlp("wc", "wc", "-l", NULL);
        }
    }

    else                     //Main exec
    {
        wait(NULL);
    }
}