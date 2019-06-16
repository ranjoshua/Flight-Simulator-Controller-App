package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
	private double sizeOfPlane = 0;
	private StringProperty problem;
	private StringProperty targetFileName;


	public MapDisplayer() {
		setSolution(new Label());
		this.airplaneFileName = new SimpleStringProperty();
		this.targetFileName = new SimpleStringProperty();
		this.problem = new SimpleStringProperty();
		cRow = 0;
		cCol = 0;
		targetRow = -1;
		targetCol = -1;
		max = 1;
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
		sizeOfPlane = cellSize;
		redraw();
	}

	/*
	 * we would like to call this function every time something has changed in the
	 * map.
	 */

	/*
	 * The RGB values for the colors: Red 255, 0, 0 Yellow 255, 255, 0 Green 0, 255,
	 * 0
	 */
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
		if (cellSize < 1)
			gc.drawImage(airplane, Math.abs(cCol * w)*cellSize, Math.abs(cRow * h)*cellSize, w * 15, h * 15);
		else
			gc.drawImage(airplane, cCol * w, cRow * h, w, h);
		if (targetRow >= 0 && targetCol >= 0) {
			if (cellSize < 1)
				gc.drawImage(target, targetRow * w, targetCol * h, w * 8, h * 8);
			else
				gc.drawImage(target, targetRow * w, targetCol * h, w, h);
		}
	}

	public void drawPath() {
		System.out.println("Cheapest Path: " + solution.textProperty().get());
		String[] path = solution.textProperty().get().split(",");
		double printRow = cRow, printCol = cCol;
		GraphicsContext gc = getGraphicsContext2D();
		double width = getWidth();
		double height = getHeight();
		double h = height / mapData.length;
		double w = width / mapData[0].length;
		gc.setStroke(Color.BLUE);
		//if (cellSize < 1)
		//	gc.setLineWidth(50);
		//else
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
		if (cRow != row || cCol != col) {
			this.cRow = row;
			this.cCol = col;
			redraw();
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

}
