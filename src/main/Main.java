package main;


import gui.controller.CTR_Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    Main_Application mainApp = new Main_Application();
    Updater_Main updater = new Updater_Main();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startScreen = FXMLLoader.load(getClass().getResource("startScreen.fxml"));
        Scene scene = new Scene(startScreen);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TimeStamp laden...");

        CTR_Config ctr_config = new CTR_Config();
        ctr_config.loadConfig();

        primaryStage.show();

        new Thread(() -> Platform.runLater(() -> {
            if(!File_Handler.fileExist("ver")){
                File_Handler.createDir("ver");
            }
            if(!File_Handler.fileExist("data")){
                File_Handler.createDir("data");
            }
            if(CTR_Config.updateConfig) {
                try {
                    update(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mainApp.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                primaryStage.close();
            }
        })).start();

    }


    public void update(Stage primaryStage) throws Exception {
        boolean update = updater.start(mainApp.getBuild());
        if(update) {
            System.out.println("starte neue App");
            Runtime runTime = Runtime.getRuntime();
            runTime.exec("java -jar TimeStamp.jar");
            primaryStage.close();
        } else {
            mainApp.start(new Stage());
            primaryStage.close();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
