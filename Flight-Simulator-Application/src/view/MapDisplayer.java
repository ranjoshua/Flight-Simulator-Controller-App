package view;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MapDisplayer extends Canvas {

	private int mapData[][];
	private double cRow, cCol; // Airplain position.
	private double targetRow, targetCol;
	private StringProperty airplaneFileName;
	private double cellSize;
	private Label solution;
	private int max;
	private StringProperty problem;
	private StringProperty targetFileName;
	private DoubleProperty lat, lon, heading;
	public static boolean interpreterOn = false;
	Point planePos;
	static AtomicInteger p = new AtomicInteger(0);
	
	public MapDisplayer() {
		setSolution(new Label());
		this.airplaneFileName = new SimpleStringProperty();
		this.targetFileName = new SimpleStringProperty();
		this.problem = new SimpleStringProperty();
		lat = new SimpleDoubleProperty();
		lon = new SimpleDoubleProperty();
		heading = new SimpleDoubleProperty();
		cRow = 0;
		cCol = 0;
		targetRow = -12;
		targetCol = -12;
		max = 1;
		this.planePos = new Point();
		drawEmpty();
	}

	public int[][] getMapData() {
		return mapData;
	}

	public void setMapData(int mapData[][]) {
		this.mapData = mapData;
		redraw();
	}

	public void setFixedData(int mapData[][], double airplainRow, double airplainCol, double cellSize) {
		this.mapData = mapData;
		this.cRow = airplainRow;
		this.cCol = airplainCol;
		this.cellSize = cellSize;
		redraw();
	}

	public void redraw() {
		if (mapData == null)
			return;

		double width = getWidth();
		double height = getHeight();

		double h = height / mapData.length;
		double w = width / mapData[0].length;

		GraphicsContext gc = getGraphicsContext2D();

		Image airplane = null, target = null;
		try {
			airplane = new Image(new FileInputStream(getAirplaneFileName()));
			target = new Image(new FileInputStream(getTargetFileName()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		gc.clearRect(0, 0, width, height);
		double red = 0, green = 0;

		for (int i = 0; i < mapData.length; i++)
			for (int j = 0; j < mapData[i].length; j++) {

				if (mapData[i][j] <= max / 2) {
					red = 255;
					green = mapData[i][j] * (255 / max) * 2;
				} else {
					red = Math.abs(255 - ((mapData[i][j] - (max / 2)) * (255 / max) * 2));
					green = 255;
				}
				gc.strokeRect(j * w, i * h, w, h);
				gc.setFill(new Color(red / 255, green / 255, 0.286, 1));
				gc.fillRect(j * w, i * h, w, h);
				gc.setFill(Color.BLACK);
				gc.fillText(String.valueOf(mapData[i][j]), j * w + 4, i * h + 15);
			}
		gc.rotate(30);
		gc.drawImage(airplane, Math.abs(cRow)-150, Math.abs(cCol)-30, 35, 35);
		gc.rotate(-30);
		if (targetRow != (-12) && targetCol != (-12)) 
				gc.drawImage(target, targetRow * w, targetCol * h, 20, 20);
		
	}

	public void drawPath() {
		System.out.println("Cheapest Path: " + solution.textProperty().get());
		String[] path = solution.textProperty().get().split(",");
		double printRow = cRow, printCol = cCol;
		if (printRow < 0 || printCol < 0) {
			printRow = 0;
			printCol = 0;
		}
		GraphicsContext gc = getGraphicsContext2D();
		double width = getWidth();
		double height = getHeight();
		double h = height / mapData.length;
		double w = width / mapData[0].length;
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(2.5);
		for (int i = 0; i < path.length; i++) {
			String arrow = path[i];
			switch (arrow) {
			case "Down":
				printCol++;
				gc.strokeOval(w * (printRow), h * printCol, w, h);
				break;
			case "Up":
				printCol--;
				gc.strokeOval(w * (printRow), h * printCol, w, h);
				break;
			case "Right":
				printRow++;
				gc.strokeOval(w * printRow, h * (printCol), w, h);
				break;
			case "Left":
				printRow--;
				gc.strokeOval(w * printRow, h * (printCol), w, h);
				break;
			default:
				break;
			}
		}
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
	}

	public void setAirplainPosition(double row, double col) {
		if (interpreterOn) {
			this.planePos.setLocation(row, col);
			p.incrementAndGet();
			redraw();
		}
	}
	
	public void fixNewPlanePositionData() {
		if (mapData != null) {
			double ln = ((lon.doubleValue() - cRow) + cellSize) / cellSize;
			double lt = (-(lat.doubleValue() - cCol) + cellSize) / cellSize;
			int r = Math.round((float) (mapData.length * ln / getHeight()));
			int c = Math.round((float) (mapData[0].length * lt / getWidth()));
			planePos.setLocation(r, c);
			setAirplainPosition(this.lon.doubleValue(), this.lat.doubleValue());
		}
	}
	
	public double getcRow() {
		return cRow;
	}

	public double getcCol() {
		return cCol;
	}

	public String getAirplaneFileName() {
		return airplaneFileName.get();
	}

	public void setAirplaneFileName(String airplainFileName) {
		this.airplaneFileName.set(airplainFileName);
	}

	public String getTargetFileName() {
		return targetFileName.get();
	}

	public void setTargetFileName(String target) {
		this.targetFileName.set(target);
	}

	public String getProblem() {
		return problem.get();
	}

	public void setProblem(String problem) {
		this.problem.set(problem);
	}

	public Label getSolution() {
		return solution;
	}

	public void setSolution(Label solution) {
		this.solution = solution;
	}

	public void setTarget(int x, int y) {
		this.targetRow = x;
		this.targetCol = y;
		redraw();
	}

	public String getTarget() {
		return "" + (int) targetCol + "," + (int) targetRow;
	}

	public double getCellSize() {
		return cellSize;
	}

	public void setCellSize(double cellSize) {
		this.cellSize = cellSize;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void drawEmpty() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(Color.GRAY);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(30);
		gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		gc.setLineWidth(1);
	}

	public DoubleProperty getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon.set(lon);
	}

	public DoubleProperty getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading.set(heading);
	}

	public DoubleProperty getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat.set(lat);
	}

}
