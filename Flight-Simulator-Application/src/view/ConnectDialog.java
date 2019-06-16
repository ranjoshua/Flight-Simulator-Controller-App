package view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

class ConnectDialog extends Stage {

	private Button login;
	final private TextField ipFld;
	final private TextField portFld;
	
    public ConnectDialog(Stage owner) {
        super();
        initOwner(owner);
        setTitle("Connect");
        Group root = new Group();
        Scene scene = new Scene(root, 360, 150, Color.WHITE);
        setScene(scene);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(5);
        gridpane.setVgap(5);

        Label ipLbl = new Label("IP Address: ");
        gridpane.add(ipLbl, 0, 1);

        Label portLbl = new Label("Port: ");
        gridpane.add(portLbl, 0, 2);
       ipFld = new TextField("127.0.0.1");
        gridpane.add(ipFld, 1, 1);

       portFld = new TextField("5402");
        gridpane.add(portFld, 1, 2);

        this.login = new Button("Connect");
        /*
        login.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                close();
            }
        });
        */
        gridpane.add(login, 1, 3);
        GridPane.setHalignment(login, HPos.RIGHT);
        root.getChildren().add(gridpane);
    }

	public Button getLogin() {
		return login;
	}

	public void setLogin(Button login) {
		this.login = login;
	}

	public TextField getIpFld() {
		return ipFld;
	}

	public TextField getPortFld() {
		return portFld;
	}
}
