package Model;

public interface Model {

	void connectToSimulator(String ip, int port);
	void sendToSimulator(String command);
	void calculatePath(String ip, int port, String problem);
	String getSolution();
	void interpret(String script);
	void startDataReaderServer();
	void stopDataReaderServer();
	void closeClientConnection();
	void interpretPath(String path);
	void stopInterpreter();
	void getPlanePos();
}
