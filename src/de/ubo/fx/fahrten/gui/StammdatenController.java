package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.Anlass;
import de.ubo.fx.fahrten.business.AnlassValidator;
import de.ubo.fx.fahrten.business.Ziel;
import de.ubo.fx.fahrten.business.ZielValidator;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static de.ubo.fx.fahrten.business.AnlassValidator.AnlassMeldung;
import static de.ubo.fx.fahrten.business.ZielValidator.ZielMeldung;

/**
 * Created by ulrichbode on 16.02.16.
 */
public class StammdatenController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(StammdatenController.class.getName());
    public TableView anlassTableView;
    public TableColumn anlassPositionCol;
    public TableColumn anlassNameCol;
    public TableColumn anlassTextCol;
    public TableColumn anlassKurzCol;
    public TableView zielTableView;
    public TableColumn zielPositionCol;
    public TableColumn zielNameCol;
    public TableColumn zielTextCol;
    public TableColumn zielKurzCol;
    public TableColumn zielOrtCol;
    public TableColumn zielAnlassNrCol;
    public Button anlassOkButton;
    public Button anlassLoeschButton;
    public Button anlassNeuButton;
    public Button zielOkButton;
    public Button zielLoeschButton;
    public Button zielNeuButton;
    private ObservableList<Anlass> anlaesse;
    private ObservableList<Ziel> ziele;
    private UpdateManager<Anlass> anlassUpdateManager = new UpdateManager<>(50);
    private UpdateManager<Ziel> zielUpdateManager = new UpdateManager<>(50);
    private Anlass selecedAnlass;

    @Override
    public void closeRequest() {
        pruefeAnlassUpdate();
        pruefeZielUpdate();
        Stage stage = (Stage) anlassTableView.getScene().getWindow();
        stage.close();
    }

    private Anlass getSelecedAnlass() {
        return selecedAnlass;
    }

    private void setSelecedAnlass(Anlass selecedAnlass) {
        this.selecedAnlass = selecedAnlass;
    }

    public UpdateManager<Anlass> getAnlassUpdateManager() {
        return anlassUpdateManager;
    }

    public UpdateManager<Ziel> getZielUpdateManager() {
        return zielUpdateManager;
    }

    public void fillAnlassTable() {

        anlaesse.clear();
        Collection<Anlass> anlassColl = HausJpaPersistence.getInstance().selectAnlaesse();
        anlaesse.addAll(anlassColl);
        Collections.sort(anlaesse);
        anlassTableView.setItems(anlaesse);
        anlassTableView.refresh();

        pruefeAnlassButtons();

    }

    public void fillZielTable() {

        ziele.clear();
        Collection<Ziel> zielColl = HausJpaPersistence.getInstance().selectZiele();
        ziele.addAll(zielColl);
        Collections.sort(ziele);
        zielTableView.setItems(ziele);
        zielTableView.refresh();
        pruefeZielButtons();

    }

    public void refreshAnlassTable() {

        anlaesse.clear();
        getAnlassUpdateManager().clear();
        Collection<Anlass> anlassColl = HausJpaPersistence.getInstance().refreshAnlaesse();
        anlaesse.addAll(anlassColl);
        Collections.sort(anlaesse);
        anlassTableView.setItems(anlaesse);
        anlassTableView.refresh();
        pruefeAnlassButtons();

    }

    public void refreshZielTable() {

        ziele.clear();
        getZielUpdateManager().clear();
        Collection<Ziel> zielColl = HausJpaPersistence.getInstance().refreshZiele();
        ziele.addAll(zielColl);
        Collections.sort(ziele);
        zielTableView.setItems(ziele);
        zielTableView.refresh();
        pruefeZielButtons();

    }

    public void handleAnlassButton(ActionEvent event) {

        String id = ((Button) event.getSource()).getId();

        switch (id) {
            case ("anlassNeuButton"):
                createAnlass();
                break;
            case ("anlassLoeschButton"):
                deleteAnlass();
                break;
            case ("anlassOkButton"):
                saveAnlass();
                break;
            default:
                System.out.println("nichts zu tun");
        }
    }

    public void handleZielButton(ActionEvent event) {

        String id = ((Button) event.getSource()).getId();

        switch (id) {
            case ("zielNeuButton"):
                createZiel();
                break;
            case ("zielLoeschButton"):
                deleteZiel();
                break;
            case ("zielOkButton"):
                saveZiel();
                break;
            default:
                System.out.println("nichts zu tun");
        }
    }

    public void handleAnlassCellEdit(CellEditEvent event) {

        int index = anlassTableView.getSelectionModel().getSelectedIndex();
        Anlass anlass = index < 0 ? null : (Anlass) anlassTableView.getItems().get(index);
        String id = ((TableColumn) event.getSource()).getId();
        Object inhalt = event.getNewValue() == null ? "" : event.getNewValue();

        switch (id) {
            case ("anlassPositionCol"):
                anlass.setPosition((Integer) inhalt);
                break;
            case ("anlassNameCol"):
                anlass.setName((String) inhalt);
                break;
            case ("anlassTextCol"):
                anlass.setText((String) inhalt);
                break;
            case ("anlassKurzCol"):
                anlass.setKurzText((String) inhalt);
                break;
            default:
                System.out.println("nichts zu tun");
        }

        getAnlassUpdateManager().addUpdate(anlass);
        pruefeAnlassButtons();

    }

    public void handleZielCellEdit(CellEditEvent event) {

        int index = zielTableView.getSelectionModel().getSelectedIndex();
        Ziel ziel = index < 0 ? null : (Ziel) zielTableView.getItems().get(index);
        String id = ((TableColumn) event.getSource()).getId();
        Object inhalt = event.getNewValue();

        switch (id) {
            case ("zielPositionCol"):
                ziel.setPosition((Integer) inhalt);
                break;
            case ("zielNameCol"):
                ziel.setName((String)inhalt);
                break;
            case ("zielTextCol"):
                ziel.setText((String)inhalt);
                break;
            case ("zielKurzCol"):
                ziel.setKurzText((String)inhalt);
                break;
            case ("zielOrtCol"):
                ziel.setOrt((String)inhalt);
                break;
            case ("zielAnlassNrCol"):
                ziel.setAnlassId((Long) inhalt);
                break;
            default:
                System.out.println("nichts zu tun");
        }

        getZielUpdateManager().addUpdate(ziel);
        pruefeZielButtons();

    }

    public void handleDragEvent(MouseEvent event){
        LOGGER.info("Drag ausgelöst" + event.getSource().toString());

        int index = anlassTableView.getSelectionModel().getSelectedIndex();
        setSelecedAnlass((Anlass) anlassTableView.getItems().get(index));

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("I have a great message for you!");
            alert.showAndWait();

    }

    public void handleDropEvent(MouseEvent event){
        LOGGER.info("Drop ausgelöst" + event.getTarget().toString());

        int index = zielTableView.getSelectionModel().getSelectedIndex();
        Ziel ziel =(Ziel) zielTableView.getItems().get(index);

        if (getSelecedAnlass() != null  && ziel != null) {
            ziel.setAnlassId(getSelecedAnlass().getId());
        }
    }

    private void createAnlass() {

        Anlass anlass = new Anlass(null, 0, "Name", "Text", "Kurztext");
        anlaesse.add(anlass);
        anlassTableView.setItems(anlaesse);

        getAnlassUpdateManager().addInsert(anlass);

        pruefeAnlassButtons();
        anlassTableView.refresh();

    }

    private void saveAnlass() {

        LOGGER.info("save Anlass Updates");

        ActionSwitch nextAction = validateAnlassInput();
        switch (nextAction) {
            case UNDO:
                refreshAnlassTable();
                break;
            case SAVE:
                getAnlassUpdateManager().saveUpdates();
                fillAnlassTable();
                break;
            case WAIT:
                // nothig to do
                break;
        }

        pruefeAnlassButtons();
    }

    private ActionSwitch validateAnlassInput() {

        Map<Anlass, AnlassMeldung> meldungen = AnlassValidator.validateAnlaesse(anlaesse);

        if (meldungen.isEmpty()) {

            return ActionSwitch.SAVE;

        } else {

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Anlass, AnlassMeldung> meldungEntry: meldungen.entrySet()) {
                Anlass anlass = meldungEntry.getKey();
                String text = meldungEntry.getValue().getText();
                sb.append(anlass.getPosition());
                sb.append(" | ");
                sb.append(anlass.getText());
                sb.append(" -> ");
                sb.append(text);
                sb.append("\n");
            }

            if (showAlert(sb.toString())) {
                // Anwender möchte korrigieren
                return ActionSwitch.WAIT;
            } else {
                // Daten erneut laden
                return ActionSwitch.UNDO;
            }
        }
    }

    private ActionSwitch validateZielInput() {

        Map<Ziel, ZielMeldung> meldungen = ZielValidator.validateZiele(ziele);

        if (meldungen.isEmpty()) {

            return ActionSwitch.SAVE;

        } else {

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Ziel, ZielMeldung> meldungEntry : meldungen.entrySet()) {
                Ziel ziel = meldungEntry.getKey();
                String text = meldungEntry.getValue().getText();
                sb.append(ziel.getPosition());
                sb.append(" | ");
                sb.append(ziel.getText());
                sb.append(" -> ");
                sb.append(text);
                sb.append("\n");
            }

            if (showAlert(sb.toString())) {
                // Anwender möchte korrigieren
                return ActionSwitch.WAIT;
            } else {
                // Daten erneut laden
                return ActionSwitch.UNDO;
            }
        }
    }

    /**
     * Zeigt die übergebene Meldung an und fragt, ob Änderungen verworfen
     * werden sollen oder ob Anwender korrigieren will.
     * @param message Meldung, die gezeigt werden soll
     * @return true, wenn Anwender korrigieren möchte
     *         false, wenn Daten neu geladen werden sollen
     */
    private boolean showAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fehler");
        alert.setHeaderText("Die Daten können nicht gespeichert werden.");
        alert.setContentText(message);

        ButtonType buttonTypeOK = new ButtonType("Korrigieren");
        ButtonType buttonTypeReload = new ButtonType("Verwerfen");
        alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeReload);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeReload) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Zeigt die übergebene Meldung an und fragt, ob Änderungen verworfen
     * oder gesichert werden sollen.
     * @param message Meldung, die gezeigt werden soll
     * @return true, wenn Anwender sichern möchte
     *         false, wenn die Änderungen verworfen werden sollen
     */
    private boolean showUnsavedObjects(String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Frage");
        alert.setHeaderText("Ungesicherte Daten!");
        alert.setContentText(message);

        ButtonType buttonTypeOK = new ButtonType("Sichern");
        ButtonType buttonTypeReload = new ButtonType("Verwerfen");
        alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeReload);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeReload) {
            return false;
        } else {
            return true;
        }
    }

    private void deleteAnlass() {

        LOGGER.info("delete Table Entry Anlass aufgerufen");

        int index = anlassTableView.getSelectionModel().getSelectedIndex();
        Anlass deleteAnlass = (Anlass) anlassTableView.getItems().get(index);

        anlassTableView.getItems().remove(index, index + 1);

        /* DB-Änderungen verwalten */
        getAnlassUpdateManager().addDelete(deleteAnlass);

        /* GUI aktualisieren */
        pruefeAnlassButtons();
    }

    private void createZiel() {

        Ziel ziel = new Ziel(null);
        ziel.setName("*");
        ziel.setText("*");
        ziel.setKurzText("*");
        ziel.setPosition(0);
        ziele.add(ziel);
        zielTableView.setItems(ziele);

        getZielUpdateManager().addInsert(ziel);

        pruefeZielButtons();
        zielTableView.refresh();

    }

    private void saveZiel() {

        LOGGER.info("save Ziele Updates");

        ActionSwitch nextAction = validateZielInput();
        switch (nextAction) {
            case UNDO:
                refreshZielTable();
                break;
            case SAVE:
                getZielUpdateManager().saveUpdates();
                fillZielTable();
                break;
            case WAIT:
                // nothig to do
                break;
        }

        pruefeZielButtons();
    }

    private void deleteZiel() {

        LOGGER.info("delete Table Entry Ziel");

        int index = zielTableView.getSelectionModel().getSelectedIndex();
        Ziel deleteZiel = (Ziel) zielTableView.getItems().get(index);

        zielTableView.getItems().remove(index, index + 1);

        /* DB-Änderungen verwalten */
        getZielUpdateManager().addDelete(deleteZiel);

        /* GUI aktualisieren */
        pruefeZielButtons();
    }

    private void pruefeAnlassButtons() {

        int index = anlassTableView.getSelectionModel().getSelectedIndex();
        anlassNeuButton.setDisable(false);

        if (index > 0) {
            anlassLoeschButton.setDisable(false);
        }

        if (getAnlassUpdateManager().isEmpty() == false) {
            anlassOkButton.setDisable(false);
        } else {
            anlassOkButton.setDisable(true);
        }

    }

    private void pruefeZielButtons() {

        int index = zielTableView.getSelectionModel().getSelectedIndex();
        zielNeuButton.setDisable(false);

        if (index > 0) {
            zielLoeschButton.setDisable(false);
        }

        if (getZielUpdateManager().isEmpty() == false) {
            zielOkButton.setDisable(false);
        } else {
            zielOkButton.setDisable(true);
        }

    }

    private void assignFields() {

         /* TableView Anlass */

        // anlassPosition column
        anlassPositionCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, Long>("position")
        );
        anlassPositionCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );

        // anlassName column
        anlassNameCol.setCellValueFactory(
                new PropertyValueFactory<Anlass, String>("name")
        );
        anlassNameCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        // anlassText column
        anlassTextCol.setCellValueFactory(
                new PropertyValueFactory<Anlass, String>("text")
        );
        anlassTextCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        // anlassKurzText column
        anlassKurzCol.setCellValueFactory(
                new PropertyValueFactory<Anlass, String>("kurzText")
        );
        anlassKurzCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );



         /* TableView Ziel */

        // zielPosition column
        zielPositionCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, Long>("position")
        );
        zielPositionCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );

        // zielName column
        zielNameCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, String>("name")
        );
        zielNameCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        // zielText column
        zielTextCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, String>("text")
        );
        zielTextCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        // zielKurzText column
        zielKurzCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, String>("kurzText")
        );
        zielKurzCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );


        // zielOrt column
        zielOrtCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, String>("ort")
        );
        zielOrtCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        // zielAnlassPosition column
        zielAnlassNrCol.setCellValueFactory(
                new PropertyValueFactory<Ziel, Long>("anlassId")
        );
        zielAnlassNrCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new LongStringConverter())
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        assignFields();
        anlaesse = FXCollections.observableArrayList();
        fillAnlassTable();
        ziele = FXCollections.observableArrayList();
        fillZielTable();
    }

    private void pruefeZielUpdate() {
        if (zielUpdateManager.isEmpty()) return;
        boolean sichern = showUnsavedObjects("Es gibt ungesicherte Ziele.");
        if (sichern) {
            zielUpdateManager.saveUpdates();
        } else {
            zielUpdateManager.clear();
        }
    }

    private void pruefeAnlassUpdate() {
        if (anlassUpdateManager.isEmpty()) return;
        boolean sichern = showUnsavedObjects("Es gibt ungesicherte Verträge.");
        if (sichern) {
            anlassUpdateManager.saveUpdates();
        } else {
            anlassUpdateManager.clear();
        }
    }

    private enum ActionSwitch {SAVE, UNDO, WAIT}

}
