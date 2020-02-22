package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.Buchung;
import de.ubo.fx.fahrten.business.BuchungsManager;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.io.BuchungsReader;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.persistence.internal.oxm.StrBuffer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by ulric on 01.08.2016.
 */
public class QuickenImportController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(QuickenImportController.class.getName());
    private final UpdateManager<Buchung> updateManager = new UpdateManager<>(500);
    public TextField dateiNameTextField;
    public TableView<Buchung> buchungsTableView;
    public TableColumn datumCol;
    public TableColumn vorgangCol;
    public TableColumn empfaengerCol;
    public TableColumn verwendungCol;
    public TableColumn kategorieCol;
    public TableColumn betragCol;
    public Button importButton;
    public Button sichernButton;
    public BorderPane buchungBorderPane;
    private ObservableList<Buchung> buchungen;

    public UpdateManager<Buchung> getUpdateManager() {
        return updateManager;
    }

    public void handleSichernAction(ActionEvent event) {
        LOGGER.info("SichernButton ausgelöst");
        saveBuchungen();
        pruefeButtons();
    }

    public void handleFileChooserAction(ActionEvent event) {
        LOGGER.info("FileChooserButton ausgelöst");
        FileChooser fileChooser = new FileChooser();
        File initialVerzeichnis = new File("C:/Haus/daten");
        if (initialVerzeichnis.exists()) {
            fileChooser.setInitialDirectory(initialVerzeichnis);
        }
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Quicken Dateien (*.qif)", "*.qif");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        dateiNameTextField.setText(file.getAbsolutePath());

        pruefeButtons();

    }

    public void handleImportAction(ActionEvent event) {
        LOGGER.info("ImportButton ausgelöst");
        String meldung = "";
        List<Buchung> buchungsList = new ArrayList<>();

        String fileName = dateiNameTextField.getText();


        if (pruefeFileName(fileName) == false) {
            meldung = "Unzulässiger Dateiname";
        } else {
            try {
                BuchungsReader reader = new BuchungsReader();
                buchungsList = reader.readBuchungen(fileName);
                Collections.sort(buchungsList);
            } catch (IOException e) {
                meldung = "Fehler beim Öffnen der Datei " + fileName;
            }
        }

        if (meldung.isEmpty()) {
            buchungen.clear();
            buchungen.addAll(buchungsList);
            buchungsTableView.setItems(buchungen);
            meldung = String.valueOf(buchungsList.size()) + " Buchungen importiert";
        }

        showMessage(meldung);
        pruefeButtons();
    }

    private boolean pruefeFileName(String fileName) {

        if (fileName.isEmpty()) {
            return false;
        }

        File file = new File(fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void saveBuchungen() {

        System.out.println("Buchungen in DB sichern");

       Integer[] updateZaehler = BuchungsManager.instance().ermittleZuPerstistierendeBuchungen(buchungen, getUpdateManager());

        getUpdateManager().saveUpdates();

        StrBuffer meldung = new StrBuffer();
        meldung.append("Daten gesichert. Neuaufnahmen: ");
        meldung.append(String.valueOf(updateZaehler[0]));
        meldung.append(" / Änderungen: ");
        meldung.append(String.valueOf(updateZaehler[1]));
        meldung.append(" .");

        showMessage(meldung.toString());

        //initializeFields();
        pruefeButtons();

    }

    private void pruefeButtons() {
        if (dateiNameTextField.getText().isEmpty()) {
            importButton.setDisable(true);
        } else {
            importButton.setDisable(false);
        }

        if (buchungen.isEmpty()) {
            sichernButton.setDisable(true);
        } else {
            sichernButton.setDisable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        buchungen = FXCollections.observableArrayList();

        initializeTableView();
        initializeTableViewColumns();

        pruefeButtons();


    }

    /**
     * Zeigt die übergebene Meldung an
     * @param message Meldung, die gezeigt werden soll
     */
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void initializeTableView(){
        //buchungsTableView.prefHeightProperty().bind(buchungBorderPane.heightProperty());
        //buchungsTableView.maxHeightProperty().bind(buchungBorderPane.getCenter().heightProperty());
    }

    private void initializeTableViewColumns() {
        datumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getDatumFormatiert());
                    return property;
                });

        vorgangCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getBuchungsNummer());
                    return property;
                });

        empfaengerCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getEmpfaenger());
                    return property;
                });

        verwendungCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getVerwendung());
                    return property;
                });

        kategorieCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getKategorie());
                    return property;
                });

        betragCol.setStyle( "-fx-alignment: CENTER-RIGHT;");
        betragCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getBetragFormatiert());
                    return property;
                });
    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) buchungsTableView.getScene().getWindow();
        stage.close();
    }
}
