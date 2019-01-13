//
// Created by erezgold on 03/01/19.
//

#ifndef BOOST_ECHO_CLIENT_TASK_H
#define BOOST_ECHO_CLIENT_TASK_H


#include <iostream>
#include <thread>
#include <mutex>
#include "connectionHandler.h"

class Task {
private:
    std::mutex & _mutex;
    ConnectionHandler & _connectionHandler;
    bool & runInput;
    bool & _logOut;
public:
    Task (std::mutex& mutex, ConnectionHandler& connectionHandler, bool& runinput, bool& logout): _connectionHandler(connectionHandler), _mutex(mutex), runInput(runinput), _logOut(logout){
    }
    void run(){
        while(runInput){
            std::lock_guard<std::mutex> lock(_mutex);
            std::string answer;
            if (!_connectionHandler.getLine(answer)) {
                runInput = false;
            }
            std::cout<<answer<<std::endl;
            if (answer == "ACK 3") {
                runInput = false;
            }
            if(answer == "ACK 3" || answer == "ERROR 3")
                _logOut = true;
        }
    }
};

#endif //BOOST_ECHO_CLIENT_TASK_H
