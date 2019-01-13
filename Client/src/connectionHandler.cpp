#include <connectionHandler.h>
#include <algorithm>
#include <string>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(getOpeMessage(line), '\0');
}
 
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    int byteCount = 0;
    short opeCode = 0;
    short messgeOpeCode = 0;
    short numberOfusers = 0;
    short statShort = 0;
    short statShort2 = 0;
    short zeroCount = 0;
    std::string options = "";
    bool runBytes = true;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
		do{
			getBytes(&ch, 1);
			if(byteCount == 0){
                opeCode = (short)((ch & 0xff) << 8);
			}
            if(byteCount == 1){
                opeCode += (short)((ch & 0xff));
            }
            if(opeCode == 10 || opeCode == 11){
                if(byteCount == 2){
                    messgeOpeCode = (short)((ch & 0xff) << 8);
                }
                if(byteCount == 3){
                    messgeOpeCode += (short)((ch & 0xff));
                    if(messgeOpeCode == 1 || messgeOpeCode == 2 || messgeOpeCode == 3 || messgeOpeCode == 5 || messgeOpeCode == 6 || opeCode == 11 ) runBytes = false;
                }
                if((messgeOpeCode == 4 || messgeOpeCode == 8 || messgeOpeCode == 7) && byteCount == 4){
                    numberOfusers = (short)((ch & 0xff) << 8);
                }
                if((messgeOpeCode == 4 || messgeOpeCode == 8 || messgeOpeCode == 7) && byteCount == 5){
                    numberOfusers += (short)((ch & 0xff));
                }
                if(messgeOpeCode == 8 && byteCount == 6){
                    statShort = (short)((ch & 0xff) << 8);
                }
                if(messgeOpeCode == 8 && byteCount == 7){
                    statShort += (short)((ch & 0xff));
                }
                if(messgeOpeCode == 8 && byteCount == 8){
                    statShort2 = (short)((ch & 0xff) << 8);
                }
                if(messgeOpeCode == 8 && byteCount == 9){
                    statShort2 += (short)((ch & 0xff));
                    runBytes = false;
                }
            }
            if(opeCode == 9){
                if(byteCount == 2){
                    messgeOpeCode += (short)(ch & 0xff);
                }
            }
            if(byteCount >= 4 && !((messgeOpeCode == 4 || messgeOpeCode == 7) && byteCount < 6) && opeCode != 9 && messgeOpeCode != 8) {
                if((messgeOpeCode == 4 || messgeOpeCode == 7) && ch == '\0') {
                    ch = ' ';
                    zeroCount++;
                    if(zeroCount == numberOfusers) runBytes = false;
                }
                options.append(1, ch);
            }
            if(byteCount >= 3 && opeCode == 9) {
                if(ch == '\0') {
                    ch = ' ';
                    zeroCount++;
                    if(zeroCount == 2) runBytes = false;
                }
                options.append(1, ch);
            }
			byteCount++;
        }while (runBytes);
	if(opeCode == 10){
	    frame = "ACK " + std::to_string(messgeOpeCode) + options;
	    if(messgeOpeCode == 8){
	        frame = "ACK " + std::to_string(messgeOpeCode) + " " + std::to_string(numberOfusers) + " " + std::to_string(statShort) + " " + std::to_string(statShort2);
	    }
        if(messgeOpeCode == 7 || messgeOpeCode == 4){
            frame = "ACK " + std::to_string(messgeOpeCode) + " " + options;
        }
	}
    if(opeCode == 11){
        frame = "ERROR " + std::to_string(messgeOpeCode);
    }
    if(opeCode == 9){
        std::string notificationType = messgeOpeCode == 1 ? "POST " : "PM ";
        frame = "NOTIFICATION " + notificationType + options;
    }
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	std::string token = frame.substr(1,2);
	if(token != "\003" && token != "\007"){
        return sendBytes(&delimiter,1);
	} else {
        return true;
	}
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

std::string ConnectionHandler::getOpeMessage(const std::string &input){
    std::string delimiter = " ";
    std::string token = input.substr(0, input.find(delimiter));
    std::string output;
    short index = 0;
    if(token == "REGISTER") {
        index = 1;
        output = input.substr(input.find(delimiter)+1,input.length());
        std::replace( output.begin(), output.end(), ' ', '\0');
    }
    if(token == "LOGIN"){
        index = 2;
        output = input.substr(input.find(delimiter)+1,input.length());
        std::replace( output.begin(), output.end(), ' ', '\0');
    }
    if(token == "LOGOUT"){
        index = 3;
        output = "";
    }
    if(token == "FOLLOW"){
        index = 4;
        output = input.substr(input.find(delimiter)+1,input.length());
        bool follow = output.at(0) == '0';
        output = output.substr(output.find(delimiter)+1,output.length());
        std::string followNumberStr = output.substr(0,output.find(delimiter));
        int followTotal = std::stoi(followNumberStr);
        output = output.substr(output.find(delimiter)+1,output.length());
        char followa = (followTotal >> 8) & 0xFF;
        char followb = followTotal & 0xFF;
        std::replace( output.begin(), output.end(), ' ', '\0');
        output = followb + output;
        output = followa + output;
        if(follow) output = '\001' + output;
        else output = '\0' + output;
    }
    if(token == "POST"){
        index = 5;
        output = input.substr(input.find(delimiter)+1,input.length());
    }
    if(token == "PM"){
        index = 6;
        output = input.substr(input.find(delimiter)+1,input.length());
        output.at(output.find(delimiter)) = '\0';
    }
    if(token == "USERLIST"){
        index = 7;
        output = "";
    }
    if(token == "STAT"){
        index = 8;
        output = input.substr(input.find(delimiter)+1,input.length());
    }
    // replace all 'x' to 'y'
    char a = (index >> 8) & 0xFF;
    char b = index & 0xFF;
    output = b + output;
    output = a + output;
    return output;
}

void ConnectionHandler::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = static_cast<char>((num >> 8) & 0xFF);
    bytesArr[1] = static_cast<char>(num & 0xFF);
}