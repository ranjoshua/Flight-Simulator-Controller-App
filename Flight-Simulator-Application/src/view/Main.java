package view;

import Model.MyModel;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			MainWindowController.primaryStage = primaryStage;
			MyModel m = new MyModel(); // Model
			ViewModel vm = new ViewModel(m); // View-Model
			m.addObserver(vm);
			FXMLLoader fxl = new FXMLLoader();
			Pane root = fxl.load(getClass().getResource("MainWindow.fxml").openStream());
			Scene scene = new Scene(root, 1270, 531);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			MainWindowController mwc = fxl.getController(); // View
			mwc.setViewModel(vm);
			vm.addObserver(mwc);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
