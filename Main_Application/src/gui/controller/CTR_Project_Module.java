package gui.controller;

import handling.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Main_Application;
import object.ClientStorageObject;
import object.StorageObject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by Eike on 20.05.2017.
 */
public class CTR_Project_Module {

    @FXML
    Label label_time;
    @FXML
    Label label_projName;
    @FXML
    private ToggleButton btn_timer;
    @FXML
    public Label label_client;
    @FXML
    private TextField textArea_comment;
    @FXML
    private Label label_firstChar;
    @FXML
    private VBox vbox_detail;

    private String name;
    private ClientStorageObject client;
    private int mainSec = 0;
    private int newSec = 0;
    private int index;
    private LocalDate date =LocalDate.now();
    private int maxTimeHours = 0;
    private boolean running = false;
    private Timeline mainTime = new Timeline();

    private ArrayList<StorageObject> storageObjects = new ArrayList<>();

    public CTR_Project_Module(ClientStorageObject client, String name, int maxTimeHours, int index) {
        this.name = name;
        this.client = client;
        this.maxTimeHours = maxTimeHours;
        this.index = index;
    }

    public void initialize() {
        label_projName.setText(name);
        label_client.setText(client.getName());
        char p = client.getName().charAt(0);
        char pUpper = Character.toUpperCase(p);

        label_firstChar.setText(String.valueOf(pUpper));
        label_firstChar.setStyle("-fx-background-color: " + Manager.getHexColorString(client.getColor()) + ";" + Manager.getCSSTextColorByBrightness(client.getColor()));
        initTrackingData();
        //wenn StorageObjekte vorhanden sind, errechne die gesamtzeit daraus
        if(storageObjects.size() != 0) {
            getWholeTime();
            textArea_comment.setText(storageObjects.get(storageObjects.size()-1).getComment());
        }
        label_time.setText(Manager.printTime(mainSec));
    }

    public void initAtRuntime() {

        label_projName.setText(name);
        label_client.setText(client.getName());
        char p = client.getName().charAt(0);
        char pUpper = Character.toUpperCase(p);
        label_firstChar.setText(String.valueOf(pUpper));
        label_firstChar.setStyle("-fx-background-color: " + Manager.getHexColorString(client.getColor()) + ";" + Manager.getCSSTextColorByBrightness(client.getColor()));

        //wenn StorageObjekte vorhanden sind, errechne die gesamtzeit daraus
        if(storageObjects.size() != 0) {
            getWholeTime();
            textArea_comment.setText(storageObjects.get(storageObjects.size()-1).getComment());
        }
        System.out.println("mainSec 2:" + mainSec);
        label_time.setText(Manager.printTime(mainSec));
    }

    public void initTrackingData() {
        vbox_detail.getChildren().clear();
        for(StorageObject store : storageObjects) {
            if(LocalDate.now().equals(store.getDate())){
                Label label = new Label();
                label.setText(Manager.printTime(store.getSec()) + "   -   " + store.getComment());
                vbox_detail.getChildren().add(label);
            }
        }
    }


    public void startClock() throws IOException {
        //Uhr wird gestoppt
        if(running) {
            clockStop();
        }
        //Uhr wird gestartet
        else {
            System.out.println("index: " + index);
            //überprüfen ob andere Uhre noch laufen und diese stoppen
            for(CTR_Project_Module projects : Manager.projectList) {
                if(projects.running) {
                    projects.clockStop();
                }
            }
            newSec = 0;
            mainTime.setCycleCount(Timeline.INDEFINITE);

            KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
                mainSec++;
                newSec++;
                label_time.setText(Manager.printTime(mainSec));
            });
            mainTime.getKeyFrames().add(frame);
            mainTime.play();
            running = true;
            btn_timer.setText("\uF00E");
            //btn_timer.setId("");
            btn_timer.setStyle("-fx-text-fill: rgb(65, 63, 84)");
           // main_hbox.setStyle("-fx-background-color: rgba(140, 255, 152, 0.7);");
        }
    }

    private void clockStop() throws IOException {
        mainTime.stop();
        running = false;
        mainTime.getKeyFrames().clear();
        saveData();
        initTrackingData();

        CSV_ProjectHandler.csvWriter();
        btn_timer.setText("\uF00F");
        btn_timer.setStyle("-fx-text-fill: rgb(238, 99, 82)");
    }

    //Speichert die getrackted Zeit entsprechend in einem StorageObjekt
    private void saveData() {
        StorageObject lastObj = storageObjects.get(storageObjects.size() - 1);
        String comment = textArea_comment.getText();

        //wenn noch kein Eintrag vorhanden ist, lege einen an
        if(storageObjects.size() == 0){
            storageObjects.add(new StorageObject(LocalDate.now(), newSec, comment));
            System.out.println("erster Eintrag");
        }
        //Wenn Datum ungleich zum letzten Eintrag ist, einen neuen Anlegen
        else if (!lastObj.getDate().equals(LocalDate.now())) {
            storageObjects.add(new StorageObject(LocalDate.now(), newSec, comment));
            System.out.println("Datum ungleich");
        }
        //Wenn das Datum gleich ist, aber der Kommentar unterschiedlich, lege auch einen neuen Eintrag an, aber keine neue Gruppe
        else if(lastObj.getDate().equals(LocalDate.now()) && !lastObj.getComment().equals(comment)) {
            storageObjects.add(new StorageObject(LocalDate.now(), newSec, comment));
            System.out.println("Datum gleich aber Kommentar ungleich");
        }
        // Wenn Datum als auch Kommentar gleich sind keinen neuen Eintrag sondern Zeit addieren
        else {
            lastObj.setSeconds(lastObj.getSec() + newSec);
            System.out.println("Zeit hinzugefügt");
        }
    }

    //Iteriert durch alle StorageObjekte und addiert die Zeit
    private void getWholeTime() {
        mainSec = 0;
        for(StorageObject store : storageObjects) {
            if(store.getComment().equals("Zeit auf Null gesetzt")) {
                mainSec = 0;
                System.out.println("mainsec auf null gesetzt");
            }
            mainSec = mainSec + store.getSec();
        }
    }

    public void deleteProject() throws IOException {
        if(Alert_Windows.confirmDialog("Projekt löschen", "Das Projekt wird gelöscht", "Möchtest du das Projekt wirklich löschen?")) {
            System.out.println("index: " + index);
            Manager.projectList.remove(index);
            Manager.projectUIList.remove(index);
            Main_Application.ctr_dashboard.removeProject(index);
            for(int i=0; i<Manager.projectList.size(); i++) {
                Manager.projectList.get(i).setIndex(i);
            }
            CSV_ProjectHandler.csvWriter();
        }
    }

    public void resetTime() {
        if(Alert_Windows.confirmDialog("Zeit auf Null setzen", "Die Gesamtzeit auf Null zurücksetzen", "Möchtest du das wirklich die Gesamtzeit auf Null setzen?")) {
            mainSec = 0;
            addStorageObject(new StorageObject(date, mainSec,"Zeit auf Null gesetzt"));
            label_time.setText(Manager.printTime(mainSec));
        }
    }

    public void toArchiv() throws IOException {
        if(Alert_Windows.confirmDialog("Projekt abschließen", "Das Projekt wird in das Archiv verschoben", "Möchtest du das Projekt wirklich abschließen?")) {
            Archiv_Handler.writeToArchiv(this);
            Manager.projectList.remove(index);
            Manager.projectUIList.remove(index);
            Main_Application.ctr_dashboard.removeProject(index);
            for(int i=0; i<Manager.projectList.size(); i++) {
                Manager.projectList.get(i).setIndex(i);
            }
            CSV_ProjectHandler.csvWriter();
            Archiv_Handler.loadArchiv();
        }
    }

    public void showProjectEdit() throws IOException {
        Stage stageProjectEdit = new Stage();
        stageProjectEdit.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/project_edit.fxml"));
        CTR_Project_Edit ctr_project_edit = new CTR_Project_Edit(this, stageProjectEdit);
        fxmlLoader.setController(ctr_project_edit);
        Parent projectEdit = fxmlLoader.load();

        Scene projectEditScene = new Scene(projectEdit);

        stageProjectEdit.setTitle("Projekt bearbeiten");
        stageProjectEdit.initOwner(Main_Application.primaryStage);
        stageProjectEdit.setScene(projectEditScene);
        stageProjectEdit.showAndWait();
    }

    public ArrayList<LocalDate> getAllDates() {
        ArrayList<LocalDate> dates = new ArrayList<>();
        for(StorageObject store : storageObjects) {
            dates.add(store.getDate());
        }
        return dates;
    }

    public String getName() { return name; }

    public String getClientName() { return client.getName(); }

    public ClientStorageObject getClient() {
        return client;
    }

    public int getMainSec() {
        return mainSec;
    }

    public int getNewSec() { return newSec; }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMaxTimeHours() {
        return maxTimeHours;
    }

    public void addStorageObject(StorageObject storageObject) {
        storageObjects.add(storageObject);
        initTrackingData();
    }

    public int getSecondsByDate(LocalDate date) {
        int time = 0;
        for(StorageObject store : storageObjects) {
            if(store.getDate().equals(date)) {
                time += store.getSec();
            }
        }
        return time;
    }

    public ArrayList<StorageObject> getStorageObjects() {
        return storageObjects;
    }

    public void setStorageObjects(ArrayList<StorageObject> storageObjects) {
        this.storageObjects = storageObjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClient(ClientStorageObject client) {
        this.client = client;
    }

    public void setMainSec(int mainSec) {
        this.mainSec = mainSec;
    }

    public void setMaxTimeHours(int maxTimeHours) {
        this.maxTimeHours = maxTimeHours;
    }

}
