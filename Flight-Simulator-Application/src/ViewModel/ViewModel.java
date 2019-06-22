package ViewModel;

import java.util.Observable;
import java.util.Observer;

import Model.Model;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import view.MapDisplayer;

public class ViewModel extends Observable implements Observer {

	public DoubleProperty aileron;
	public DoubleProperty elevator;
	public DoubleProperty rudder;
	public DoubleProperty throttle;
	public StringProperty solution;
	public StringProperty script;
	public StringProperty path;
	public DoubleProperty lon, lat, heading;
	Model m;

	public ViewModel(Model m) {
		this.m = m;
		aileron = new SimpleDoubleProperty();
		elevator = new SimpleDoubleProperty();
		rudder = new SimpleDoubleProperty();
		throttle = new SimpleDoubleProperty();
		solution = new SimpleStringProperty();
		script = new SimpleStringProperty();
		path = new SimpleStringProperty();
		lon = new SimpleDoubleProperty();
		lat = new SimpleDoubleProperty();
		heading = new SimpleDoubleProperty();
		
		rudder.addListener((e) -> {
			m.sendToSimulator("set controls/flight/rudder " + rudder.doubleValue());
		});
		throttle.addListener((e) -> {
			m.sendToSimulator("set controls/engines/engine/throttle " + throttle.doubleValue());
		});
		
		elevator.addListener((e) -> {
			m.sendToSimulator("set controls/flight/elevator " + elevator.doubleValue());
		});
		aileron.addListener((e) -> {
			m.sendToSimulator("set controls/flight/aileron " + aileron.doubleValue());
		});
		
	}

	public void runInterpreter() {
		m.interpretPath(this.path.get());
		MapDisplayer.interpreterOn = true;
	}
	
	public void connect(String ip, int port) {
		m.connectToSimulator(ip, port);
	}

	public void calculatePath(String ip, int port, String problem) {
		m.calculatePath(ip, port, problem);
		System.out.println(problem);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == m) {
			double[] values = (double[]) arg;
			if (values != null) {
				this.lat.set(values[0]);
				this.lon.set(values[1]);
				this.heading.set(values[2]);
				setChanged();
				notifyObservers();
			}
			else 
				this.solution.set(m.getSolution());	
		}
	}
	
	public void getPlanePos(){
        m.getPlanePos();
    }
	
	public void openDataServer() {
		m.startDataReaderServer();
	}
	
	public void stopDataServer() {
		m.stopDataReaderServer();
	}
	
	public void closeClientConnection() {
		m.closeClientConnection();
	}
	
	public void stopInterpreter() {
		m.stopInterpreter();
	}
}
