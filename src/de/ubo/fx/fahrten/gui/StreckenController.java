package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.Entfernung;
import de.ubo.fx.fahrten.business.Ziel;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ulrichbode on 06.03.16.
 */
public class StreckenController implements Initializable, CloseRequestable {

    private final static Logger LOGGER = Logger.getLogger(StreckenController.class.getName());
    public AnchorPane streckenAnchorPane;
    public GridPane streckenGridPane;
    public Button streckenOkButton;
    public Button streckenCancelButton;
    public Button streckenRefreshButton;


    private List<Ziel> ziele;
    private ObservableList<Entfernung> entfernungen;
    private UpdateManager<Entfernung> entfernungUpdateManager = new UpdateManager<>(100);

    public UpdateManager<Entfernung> getEntfernungUpdateManager() {
        return entfernungUpdateManager;
    }

    public List<Ziel> getZiele() {
        return ziele;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        entfernungen = FXCollections.observableArrayList();
        entfernungUpdateManager = new UpdateManager<>(200);
        ladeZiele();
        refreshEntfernungen();
        disableButtons();
    }

    /**
     * Die Ziele werden geladen und nach Position sortiert
     */
    private void ladeZiele() {
        Collection<Ziel> zielColl = HausJpaPersistence.getInstance().selectZiele();
        ziele = new ArrayList<>(zielColl);
        Collections.sort(ziele);
    }

    /**
     * Entfernungs-Matrix leer anlegen.
     * Die Daten werden separat geladen.
     */
    private void createEntfenungsTable() {

        LOGGER.info("Start create Matrix: " + streckenGridPane.getChildren().size());
        streckenGridPane.getChildren().clear();
        LOGGER.info("Nach Löschung Matrix: " + streckenGridPane.getChildren().size());

        Collection<Ziel> zielColl = HausJpaPersistence.getInstance().selectZiele();
        List<Ziel> ziele = new ArrayList<>(zielColl);
        Collections.sort(ziele);

        /* Spalten und Zeilen in vorgegebener Größe anlegen */
        createMatrix(ziele);

        /* Labels und Fields anlegen */
        createTextFields(ziele);
        LOGGER.info("End create Matrix: " + streckenGridPane.getChildren().size());
    }

    /**
     * Spalten und Zeilen füllen
     * 1. Spalte und 1. Zeile enthalten die Beschriftung
     *
     * @param ziele
     */
    private void createTextFields(List<Ziel> ziele) {

        for (int j = 0; j < ziele.size() + 1; j++) {

            for (int i = 0; i < ziele.size() + 1; i++) {

                if (i == 0 || j == 0) {
                    int k = i + j;
                    if (k == 0) {
                        Label label = new Label("Z i e l");
                        GridPane.setConstraints(label, i, j);
                        streckenGridPane.getChildren().add(label);
                    } else {
                        if (i == 0) {
                            Label label = new Label(ziele.get(j - 1).getName());
                            //label.setStyle("-fx-background-color: AQUA;");
                            GridPane.setConstraints(label, i, j);
                            streckenGridPane.getChildren().add(label);
                        }
                        if (j == 0) {
                            Label label = new Label(ziele.get(i - 1).getName());
                            //label.setStyle("-fx-background-color: AQUA;");
                            GridPane.setConstraints(label, i, j);
                            streckenGridPane.getChildren().add(label);
                        }
                    }
                } else {
                    NumberTextField textField = new NumberTextField();
                    textField.setAlignment(Pos.CENTER_RIGHT);
                    GridPane.setConstraints(textField, i, j);
                    streckenGridPane.getChildren().add(textField);

                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        handleTextInput(observable, oldValue, newValue);
                        System.out.println("textfield changed from " + oldValue + " to " + newValue);
                    });

                    if (j == i) {
                        textField.setText("0");
                        textField.setEditable(false);
                        textField.setStyle("-fx-background-color: LIGHTGREY;");
                    }
                }
            }
        }
    }

    private void handleTextInput(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        LOGGER.info(observable.toString());
        enableButtons();
    }

    /**
     * Spalten und Zeilen in richtiger Größe anlegen
     *
     * @param ziele
     */
    private void createMatrix(List<Ziel> ziele) {

        streckenGridPane.getColumnConstraints().clear();
        streckenGridPane.getRowConstraints().clear();

        for (int i = 0; i <= ziele.size(); i++) {
            streckenGridPane.getRowConstraints().add(new RowConstraints(25));
            streckenGridPane.getColumnConstraints().add(new ColumnConstraints(60));
        }
    }

    /**
     * Matrix mit Daten aus der DB füllen
     */
    private void ladeEntfernungen() {

        Collection<Entfernung> entfernungsColl = HausJpaPersistence.getInstance().selectEntfernungen();
        entfernungen.addAll(entfernungsColl);

        for (Entfernung entfernung : entfernungen) {
            int i = getZiele().indexOf(entfernung.getStartZiel()) + 1;
            int j = getZiele().indexOf(entfernung.getEndZiel()) + 1;

            int textFieldIndex = calculateTextFieldIndex(i, j);

            Object o = streckenGridPane.getChildren().get(textFieldIndex);

            if (o instanceof TextField) {
                TextField textField = (TextField) o;
                textField.setText(String.valueOf(entfernung.getEntfernung()));
                textField.setUserData(entfernung);
            } else {
                System.out.println(textFieldIndex + o.toString());
            }
            System.out.println(entfernung.getId());
        }
    }

    /**
     * Berechnung des Child-Indexes des TextFields
     * @param i x-Koordinate
     * @param j y-Koordinate
     * @return Child-Index für das Textfield
     */
    private int calculateTextFieldIndex(int i, int j) {
        int anzSpalten = getZiele().size() + 1;

        return (anzSpalten * j) + i;
    }

    public void handleButton(ActionEvent event) {

        String id = ((Button) event.getSource()).getId();

        switch (id) {
            case ("streckenRefreshButton"):
                refreshEntfernungen();
                break;
            case ("streckenOkButton"):
                saveEntfernungen();
                break;
            case ("streckenCancelButton"):
                cancelEntfernungen();
                break;
            default:
                System.out.println("nichts zu tun");
        }

        disableButtons();
    }

    private void refreshEntfernungen() {
        createEntfenungsTable();
        ladeEntfernungen();
    }

    /**
     * alte Werte wieder herstellen
     */
    private void cancelEntfernungen() {

        int limit = getZiele().size();

        /* Matrix durchiterieren, alte Werte wieder einsetzen */
        for (int i = 1; i < limit; i++) {
            for (int j = 1; j < limit; j++) {

                int textFieldIndex = calculateTextFieldIndex(i, j);

                Object o = streckenGridPane.getChildren().get(textFieldIndex);

                if (o instanceof NumberTextField) {
                    NumberTextField textField = (NumberTextField) o;
                    Object o2 = textField.getUserData();
                    if (o2 == null) {
                        textField.setText("");
                    } else {
                        Entfernung entfernung = (Entfernung) o2;
                        textField.setText(String.valueOf(entfernung.getEntfernung()));
                    }
                }
            }
        }
    }

    /**
     * Sichern der eingegebenen Entfernungsmatrix
     */
    private void saveEntfernungen() {

        int limit = getZiele().size() + 1;

        /* Durchsuchen der Matrix nach Änderungen */
        for (int i = 1; i < limit; i++) {
            for (int j = 1; j < limit; j++) {

                int textFieldIndex = calculateTextFieldIndex(i, j);

                Object o = streckenGridPane.getChildren().get(textFieldIndex);

                if (o instanceof NumberTextField) {
                    NumberTextField textField = (NumberTextField) o;
                    Object o2 = textField.getUserData();
                    if (o2 == null) {
                        String text = textField.getText();
                        if (text != null && !text.isEmpty()) {
                            // neue Entfernung definiert
                            int entKm = Integer.valueOf(text);
                            Ziel startZiel = getZiele().get(i-1);
                            Ziel endZiel = getZiele().get(j-1);
                            Entfernung entfernung = new Entfernung(startZiel, endZiel, entKm);
                            textField.setUserData(entfernung);
                            getEntfernungUpdateManager().addInsert(entfernung);
                        }
                    } else {
                        Entfernung entfernung = (Entfernung) o2;
                        String wertAlt = String.valueOf(entfernung.getEntfernung());
                        String wertNeu = textField.getText();
                        if (wertAlt.equals(wertNeu) == false) {
                            // Änderung eines bestehenden Elemets
                            entfernung.setEntfernung(Integer.parseInt(wertNeu));
                            getEntfernungUpdateManager().addUpdate(entfernung);
                        }
                    }
                }
            }
        }

        getEntfernungUpdateManager().saveUpdates();

        /* Matrix aus der Datenbank neu aufbauen */
        // refreshEntfernungen();
    }

    private void disableButtons() {
        streckenOkButton.disableProperty().set(true);
        streckenCancelButton.disableProperty().set(true);
        streckenRefreshButton.disableProperty().set(true);
    }

    private void enableButtons() {
        streckenOkButton.disableProperty().set(false);
        streckenCancelButton.disableProperty().set(false);
        streckenRefreshButton.disableProperty().set(false);
    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) streckenOkButton.getScene().getWindow();
        stage.close();
    }
}
