package de.ubo.fx.fahrten.gui;
/**
 * Created by ulrichbode on 13.02.16.
 * neue Versionskontrolle gitHub
 */

import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.helper.ResourceManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class HausMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        Parent root = FXMLLoader.load(getClass().getResource("Hausverwaltung.fxml"));

        URL url = ResourceManager.getInstance().get(ResourceManager.ResourceEnum.LAYOUT_CSS);
        root.getStylesheets().add(url.toExternalForm());

        primaryStage.setTitle("Hausverwaltung");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ControllerRegistry.getInstance().closeControllers();
            }
        });

        primaryStage.show();

    }
}
