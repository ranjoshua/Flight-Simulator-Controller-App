package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class JoystickDisplayer extends Canvas {

	private double x, y;
	private StringProperty buttonFileName;
	private StringProperty arrowFileName;
	
	public JoystickDisplayer() {
		this.buttonFileName = new SimpleStringProperty();
		this.arrowFileName = new SimpleStringProperty();
		x = getWidth() / 2;
		y = getHeight() / 2;
	}

	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		double width = getWidth();
		double height = getHeight();
		double mx = width / 2;
		double my = height / 2;
		double r = Math.min(width, height) / 8;
		gc.clearRect(0, 0, width, height);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(10);
		gc.strokeOval(mx - r * 2.5, my - r * 2.5, r * 5, r * 5);
		
		Image j = null, arrow = null;
		try {
			j = new Image(new FileInputStream(getButtonFileName()));
			arrow = new Image(new FileInputStream(getArrowFileName()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		gc.setFill(Color.GRAY);
		gc.fillOval(mx - r * 2.5, my - r * 2.5, r * 5, r * 5);
		gc.drawImage(arrow, mx - r * 3, my - r * 3, r * 6, r * 6);
		gc.drawImage(j, x, y, width / 4, height / 4);
		gc.setLineWidth(0.5);
		gc.setStroke(Color.BLACK);
		gc.strokeOval(x, y, width / 4, height / 4);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		redraw();
	}

	public String getButtonFileName() {
		return buttonFileName.get();
	}

	public void setButtonFileName(String buttonFileName) {
		this.buttonFileName.set(buttonFileName);
	}

	public String getArrowFileName() {
		return arrowFileName.get();
	}

	public void setArrowFileName(String arrowFileName) {
		this.arrowFileName.set(arrowFileName);
	}

}
