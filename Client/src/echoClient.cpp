#include <stdlib.h>
#include <connectionHandler.h>
#include <Task.h>
#include <iostream>
#include <thread>
#include <mutex>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    bool run = true;
    bool logoutDone = false;
    std::mutex mutex;
    Task inputTask(mutex, connectionHandler, run, logoutDone);
    std::thread th1(&Task::run, &inputTask);
	//From here we will see the rest of the ehco client implementation:
    while (run) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
		std::string line(buf);
        std::string token = line.substr(0, line.find(' '));
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if(token == "LOGOUT"){
            while (!logoutDone);
            logoutDone = false;
        }


    }
    th1.join();
    return 0;
}
