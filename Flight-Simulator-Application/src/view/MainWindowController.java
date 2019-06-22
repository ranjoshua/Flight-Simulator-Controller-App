package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Scanner;

import ViewModel.ViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainWindowController implements Initializable, View, Observer {


	private ViewModel vm;

	public StringProperty scriptPath;
	static Stage primaryStage;
	public DoubleProperty aileron;
	public DoubleProperty elevator;	
	
	@FXML
	Slider Rudder;
	@FXML
	Slider Throttle;
	@FXML
	TextArea TextBox;
	@FXML
	MapDisplayer MapDisplayer;
	@FXML
	JoystickDisplayer JoystickDisplayer;
	@FXML
	ToggleGroup RadioGroup;
	@FXML
	RadioButton Manual;
	@FXML
	RadioButton Autopilot;
	@FXML
	Button LoadScript;
	@FXML
	Button StartButton;
	@FXML
	Button StopButton;
	
	
	public void setViewModel(ViewModel vm) {
		this.vm = vm;
		vm.aileron.bind(this.aileron); // DATA BINDING
		vm.elevator.bind(this.elevator); // DATA BINDING
		vm.rudder.bind(this.Rudder.valueProperty());
		vm.throttle.bind(this.Throttle.valueProperty());	
		vm.script.bind(this.TextBox.textProperty());
		vm.path.bind(this.scriptPath);
		
		MapDisplayer.getLat().bind(vm.lat);
        MapDisplayer.getLon().bind(vm.lon);
        MapDisplayer.getHeading().bind(vm.heading);
        
		MapDisplayer.getSolution().textProperty().bind(vm.solution);
		MapDisplayer.getSolution().textProperty().addListener((e) -> {
			if (MapDisplayer.getProblem() != null) 
				MapDisplayer.drawPath();
		});
	
	}
	
	
	public void connect() {
		ConnectDialog myDialog = new ConnectDialog(primaryStage);
		myDialog.sizeToScene();
		myDialog.show();
		myDialog.getLogin().setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				getVm().connect(myDialog.getIpFld().getText(), Integer.valueOf(myDialog.getPortFld().getText()));
				myDialog.close();
			}
		});
	}

	public void calculatePath() {
		ConnectDialog myDialog = new ConnectDialog(primaryStage);
		myDialog.sizeToScene();
		myDialog.show();
		myDialog.getLogin().setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				StringBuilder sb = new StringBuilder();
				sb.append(MapDisplayer.getProblem());
				sb.append("end" + "\n");
				int x = (int)MapDisplayer.getcRow();
				int y = (int)MapDisplayer.getcCol();
				if (x < 0 || y < 0) {
					x = 0;
					y = 0;
				}
				sb.append(x+","+y+"\n");
				sb.append(MapDisplayer.getTarget());
				getVm().calculatePath(myDialog.getIpFld().getText(), Integer.valueOf(myDialog.getPortFld().getText()), sb.toString());
				myDialog.close();
			}
		});
	}

	public void openTextFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Flight Instruction File");
		fc.setInitialDirectory(new File("./resources"));
		fc.setSelectedExtensionFilter(new ExtensionFilter("TEXT files", "*.txt"));
		File chosen = fc.showOpenDialog(null);
		scriptPath.set(chosen.getAbsolutePath());
		if (chosen != null) {
			StringBuilder sb = new StringBuilder();
			try {
				Files.lines(Paths.get(chosen.getAbsolutePath()), Charset.defaultCharset()).forEach((l) -> {
					sb.append(l);
					sb.append('\n');
				});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			TextBox.setText(sb.toString());
		}
	}

	public void openMapFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Flight Instruction File");
		fc.setInitialDirectory(new File("./resources"));
		fc.setSelectedExtensionFilter(new ExtensionFilter("CSV files", "*.csv"));
		File chosen = fc.showOpenDialog(null);
		if (chosen != null) {
			try {
				String input;
				int mat[][], numRows = 0, numCols = 0;
				double airplainRow, airplainCol, cellSize;
				Scanner s = new Scanner(chosen);
				input = s.nextLine();
				airplainRow = Double.parseDouble(input.split(",")[0]);
				airplainCol = Double.parseDouble(input.split(",")[1]);
				cellSize = Double.parseDouble(s.nextLine().split(",")[0]);
				StringBuilder sb = new StringBuilder();
				while (s.hasNextLine()) {
					sb.append(s.nextLine() + "\n");
					numRows++;
				}			
				String matrix = sb.toString();
				MapDisplayer.setProblem(matrix);
				numCols = matrix.split("\n")[0].split(",").length;
				mat = new int[numRows][numCols];
				int[] values = Arrays.stream(matrix.split("\\W")).mapToInt(Integer::parseInt).toArray();
				int i, j, k = 0;
				for (i = 0; i < numRows; i++)
					for (j = 0; j < numCols; j++) {
						mat[i][j] = values[k];
						k++;
					}
				MapDisplayer.setMax(Arrays.stream(values).max().getAsInt());
				MapDisplayer.setFixedData(mat, airplainRow, airplainCol, cellSize);
				s.close();
				vm.getPlanePos();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		primaryStage.setOnCloseRequest((e) -> {
			this.vm.stopInterpreter();
			this.vm.stopDataServer();
			this.vm.closeClientConnection();
		});

		scriptPath = new SimpleStringProperty();
		aileron = new SimpleDoubleProperty(0);
		elevator = new SimpleDoubleProperty(0);
		RadioGroup.selectToggle(Manual);
		LoadScript.disableProperty().bind(Bindings.equal(Manual, RadioGroup.selectedToggleProperty()));
		TextBox.disableProperty().bind(Bindings.equal(Manual, RadioGroup.selectedToggleProperty()));

		Rudder.disableProperty().bind(Bindings.equal(Autopilot, RadioGroup.selectedToggleProperty()));
		Throttle.disableProperty().bind(Bindings.equal(Autopilot, RadioGroup.selectedToggleProperty()));
		StartButton.disableProperty().bind(Bindings.equal(Manual, RadioGroup.selectedToggleProperty()));
		StopButton.disableProperty().bind(Bindings.equal(Manual, RadioGroup.selectedToggleProperty()));
		JoystickDisplayer.disableProperty().bind(Bindings.equal(Autopilot, RadioGroup.selectedToggleProperty()));

		MapDisplayer.drawEmpty();
		MapDisplayer.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			updateTargetPosition(e.getX(), e.getY());
		});
		
		double r = Math.min(JoystickDisplayer.getWidth(), JoystickDisplayer.getHeight()) / 8;
		JoystickDisplayer.setPosition(JoystickDisplayer.getWidth() / 2 - r, JoystickDisplayer.getHeight() / 2 - r);

		JoystickDisplayer.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
			JoystickDisplayer.setPosition(JoystickDisplayer.getWidth() / 2 - r, JoystickDisplayer.getHeight() / 2 - r);
			this.aileron.set(0);
			this.elevator.set(0);
		});

		JoystickDisplayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			double ovalX = JoystickDisplayer.getWidth() / 2, ovalY = JoystickDisplayer.getHeight() / 2;
			double x = e.getX() - r, y = e.getY() - r;
			double distance = Math
					.sqrt((ovalY - e.getY()) * (ovalY - e.getY()) + (ovalX - e.getX()) * (ovalX - e.getX()));
			if (distance > r * 2.8)
				return;
			JoystickDisplayer.setPosition(x, y);
			updateAileronAndElevator(x, y);
		});

		StartButton.setOnMouseClicked(e -> {
			this.vm.runInterpreter();
		});
		StopButton.setOnMouseClicked(e -> {
			this.vm.stopInterpreter();
		});
		
	}

	private void updateTargetPosition(double x, double y) {
		if (MapDisplayer.getMapData() == null)
			return;
		int numRows = MapDisplayer.getMapData().length;
		int numCols = MapDisplayer.getMapData()[0].length;
		// Normalize the (x,y) position to values in [0,a], [0,b] ranges.
		int targetRow, targetCol;
		targetRow = (int) Math.abs(numCols * ((x) / (340)));
		targetCol = (int) Math.abs(numRows * ((y) / (340)));
		MapDisplayer.setTarget(targetRow, targetCol);
	}

	private void updateAileronAndElevator(double x, double y) {
		double a = 0, b = 0;
		if (x >= 112 && x <= 113)
			a = 0;
		else if (x <= 50)
			a = -1;
		else if (x >= 165)
			a = 1;
		else // a = 2*( (x-50)/(165-50) ) - 1; // NORMALIZE BETWEEN [-1,1] RANGE.
			a = 2 * ((x - 50) / (115)) - 1;

		if (y >= 112 && y <= 113)
			b = 0;
		else if (y <= 57)
			b = -1;
		else if (y >= 165)
			b = 1;
		else // b = 2*( (y-50)/(165-57) ) - 1; // NORMALIZE BETWEEN [-1,1] RANGE.
			b = 2 * ((y - 50) / (115)) - 1;

		this.aileron.set(a);
		this.elevator.set(b);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		MapDisplayer.fixNewPlanePositionData();
	}

	public ViewModel getVm() {
		return vm;
	}

}
