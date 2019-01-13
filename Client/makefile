CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude	 
LDFLAGS:=-lboost_system -pthread -lboost_filesystem -lboost_thread

all:BGSclient
BGSclient : bin/Task.o bin/echoClient.o  bin/connectionHandler.o
	g++ -o bin/BGSclient bin/Task.o bin/echoClient.o  bin/connectionHandler.o  $(LDFLAGS) 




bin/Task.o : src/Task.cpp 
	g++ $(CFLAGS)  -o bin/Task.o src/Task.cpp
	
bin/echoClient.o : src/echoClient.cpp 
	g++ $(CFLAGS)  -o bin/echoClient.o src/echoClient.cpp
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp
	
.PHONY: clean
clean: 
	rm -f bin/*
