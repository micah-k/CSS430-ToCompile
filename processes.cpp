#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdio.h>

int main(int argc, char* argv[]){
    enum {RD, WR};
    int n, fdwcgrep[2], fdgrepps[2];
    pid_t pidmainwc, pidwcgrep, pidgrepps;
    char buf[100];

    //Fork to first process
    printf("First fork...\n");
    if((pidmainwc = fork()) < 0)
    {
      perror("Fork error, grep/ps.");
      return -1;
    }
    
    else if(pidmainwc == 0)        //Ps exec
    {
        printf("First fork successful.\n");
        //Initialize first pipe, and fork to second process
        printf("First pipe, second fork...\n");
        if(pipe(fdwcgrep) < 0)
        {
            perror("Pipe error, wc/grep.");
            return -1;
        }

        else if((pidwcgrep = fork()) < 0)
        {
            perror("Fork error, wc/grep.");
            return -1;
        }
        
        else if(pidwcgrep == 0)         //Grep code
        {
            printf("Successful.\n");
            //Initialize second pipe, and fork to third process
            printf("Second pipe, third fork...\n");
            if(pipe(fdgrepps) < 0)
            {
              perror("Pipe error, grep/ps.");
              return -1;
            }

            
            else if((pidgrepps = fork()) < 0)
            {
              perror("Fork error, grep/ps.");
              return -1;
            }

            else if(pidgrepps == 0)        //Ps exec
            {
                printf("Successful.\n");
                printf("Executing ps -A...\n");
                dup2(fdgrepps[WR], STDOUT_FILENO);
                close(fdgrepps[RD]);
                execlp("ps", "ps", "-A", NULL);
            }

            else                     //Grep exec
            {
                wait(NULL);
                printf("Executing grep argv[1]...\n");
                dup2(fdgrepps[RD], STDIN_FILENO);
                close(fdgrepps[WR]);
                dup2(fdwcgrep[WR], STDOUT_FILENO);
                close(fdwcgrep[RD]);
                execlp("grep", "grep", argv[1], NULL);
            }

        }

        else                      //Wc exec
        {
            wait(NULL);
            printf("Executing wc -l...\n");
            dup2(fdwcgrep[RD], STDOUT_FILENO);
            close(fdwcgrep[WR]);
            execlp("wc", "wc", "-l", NULL);
        }
    }

    else                     //Main exec
    {
        wait(NULL);
        printf("Return to parent loop.\n");
    }
}