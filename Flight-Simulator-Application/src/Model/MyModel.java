package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Observable;

import Interpreter.Address;
import Interpreter.DataReaderServer;
import Interpreter.DataSenderClient;
import Interpreter.FGClientHandler;
import Interpreter.Interpreter;

public class MyModel extends Observable implements Model {

	DataSenderClient clientToSimulator;
	DataReaderServer server;
	String solution;
	Thread interpreterThread;
	Thread graphClientThread;
	Thread airplanePos;

	public MyModel() {
		server = new DataReaderServer(5400, 12);
		server.start(new FGClientHandler());
	}

	public void startDataReaderServer() {
		if (!DataReaderServer.isOpen())
			server.start(new FGClientHandler());
		else
			System.out.println("server already open");
	}

	public void stopDataReaderServer() {
		if (server != null)
			server.stop();
	}

	public void closeClientConnection() {
		if (airplanePos != null)
			airplanePos.interrupt();
		airplanePos = null;
		if (clientToSimulator != null)
			clientToSimulator.closeConnection();
		clientToSimulator = null;
	}

	public void connectToSimulator(String ip, int port) {
		clientToSimulator = new DataSenderClient(ip, port);
		Address.setClient(this.clientToSimulator);
		clientToSimulator.start();
	}

	public void sendToSimulator(String command) {
		if (clientToSimulator != null)
			clientToSimulator.sendToServer(command);
	}

	public void getPlanePos() {
		airplanePos = new Thread(() -> {
			while (clientToSimulator == null && DataReaderServer.isOpen())
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {}
			while (clientToSimulator != null) {
				try {
					String[] lat = clientToSimulator.getFromServer("get /position/latitude-deg").split("'");
					String[] lon = clientToSimulator.getFromServer("get /position/longitude-deg").split("'");
					String[] hea = clientToSimulator.getFromServer("get /instrumentation/heading-indicator/indicated-heading-deg").split("'");
					double latitude = Double.parseDouble(lat[lat.length-2]);
					double longtitude = Double.parseDouble(lon[lon.length-2]);
					double heading = Double.parseDouble(hea[hea.length-2]);
					double[] vals = { latitude, longtitude, heading };
					setChanged();
					notifyObservers(vals);
					try {
						Thread.sleep(750);
					} catch (InterruptedException e) {
						return;
					}
				} catch (NullPointerException e) {}
			}
		});
		airplanePos.start();
	}

	public void calculatePath(String ip, int port, String problem) {
		graphClientThread = new Thread(() -> {
			runClient(port, problem);
		});
		graphClientThread.start();
	}

	public String getSolution() {
		return this.solution;
	}

	public void runClient(int port, String problem) {
		Socket s = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			s = new Socket("127.0.0.1", port);
			s.setSoTimeout(3000);
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String[] lines = problem.split("\n");
			Arrays.stream(lines).forEach(out::println);
			out.flush();
			this.solution = in.readLine();
			setChanged();
			notifyObservers();
		} catch (SocketTimeoutException e) {
			System.out.println("\tYour Server takes over 3 seconds to answer (-20)");
		} catch (IOException e) {
			System.out.println("\tYour Server ran into some IOException (-20)");
		} finally {
			try {
				in.close();
				out.close();
				s.close();
			} catch (IOException e) {
				System.out.println("\tYour Server ran into some IOException (-20)");
			}
		}
	}

	@Override
	public void interpret(String script) {
		if (script == null || script.length() == 0)
			return;
		interpreterThread = new Thread(() -> {
			Interpreter.interpret(script.split("\n"));// Call my interpreter to interpret the autopilot instructions
		});
		interpreterThread.start();
	}

	@Override
	public void interpretPath(String path) {
		String arr[] = { path };
		interpreterThread = new Thread(() -> {
			Interpreter.interpret(arr);
		});
		interpreterThread.start();
	}

	@Override
	public void stopInterpreter() {
		if (interpreterThread != null)
			interpreterThread.interrupt();
	}

	public void startEngine() {
		this.clientToSimulator.startEngine();
	}

}
