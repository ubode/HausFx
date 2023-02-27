package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.Ausgabe;
import de.ubo.fx.fahrten.business.Buchung;
import de.ubo.fx.fahrten.business.BuchungsKategorie;
import de.ubo.fx.fahrten.business.Haus;
import de.ubo.fx.fahrten.helper.Bedingung;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.helper.Kriterium;
import de.ubo.fx.fahrten.helper.Operator;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ulric on 01.08.2016.
 */
public class BuchungsKatalogController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(BuchungsKatalogController.class.getName());
    private final UpdateManager<Ausgabe> updateManager = new UpdateManager<>(500);
    public ComboBox argumentEinsComboBox;
    public ComboBox argumentZweiComboBox;
    public ComboBox argumentDreiComboBox;
    public ComboBox argumentVierComboBox;
    public TableView<Buchung> buchungTableView;
    public TableColumn datumCol;
    public TableColumn hausCol;
    public TableColumn empfaengerCol;
    public TableColumn verwendungCol;
    public TableColumn kategorieCol;
    public TableColumn betragCol;
    public TableColumn buchNrCol;
    public Button suchenButton;
    public ChoiceBox<Kriterium> kriteriumEinsChoiceBox;
    public ChoiceBox<Kriterium> kriteriumZweiChoiceBox;
    public ChoiceBox<Kriterium> kriteriumDreiChoiceBox;
    public ChoiceBox<Kriterium> kriteriumVierChoiceBox;
    public ChoiceBox<Operator> operatorEinsChoiceBox;
    public ChoiceBox<Operator> operatorZweiChoiceBox;
    public ChoiceBox<Operator> operatorDreiChoiceBox;
    public ChoiceBox<Operator> operatorVierChoiceBox;
    public ChoiceBox<Haus> hausChoiceBox;
    public Label summeLabel;
    private ObservableList<Haus> haeuser;
    private ObservableList<Buchung> buchungen;
    private ObservableList<Kriterium> kriterienEins;
    private ObservableList<Kriterium> kriterienZwei;
    private ObservableList<Kriterium> kriterienDrei;
    private ObservableList<Kriterium> kriterienVier;
    private ObservableList<Operator> operatorenEins;
    private ObservableList<Operator> operatorenZwei;
    private ObservableList<Operator> operatorenDrei;
    private ObservableList<Operator> operatorenVier;
    private Haus haus;

    private Comparator<Buchung> buchungsComparator = (buchung1, buchung2) ->
            buchung1.getDatum().compareTo(buchung2.getDatum());

    public Haus getHaus() {
        return haus;
    }

    public void setHaus(Haus haus) {
        this.haus = haus;
    }

    private UpdateManager<Ausgabe> getUpdateManager() {
        return updateManager;
    }

    public void handleSuchenAction(ActionEvent event) {
        LOGGER.info("SuchenButton ausgelöst");

        Collection<Bedingung>  bedingungen = new ArrayList<>(5);

        int index = hausChoiceBox.getSelectionModel().getSelectedIndex();
        Haus haus = hausChoiceBox.getSelectionModel().getSelectedItem();
        if (index > 0 && haus != null) {
            bedingungen.add(new Bedingung(Kriterium.HAUS, Operator.GLEICH, haus) );
        }

        ermittleBedingung(bedingungen, kriteriumEinsChoiceBox, operatorEinsChoiceBox, argumentEinsComboBox);
        ermittleBedingung(bedingungen, kriteriumZweiChoiceBox, operatorZweiChoiceBox, argumentZweiComboBox);
        ermittleBedingung(bedingungen, kriteriumDreiChoiceBox, operatorDreiChoiceBox, argumentDreiComboBox);
        ermittleBedingung(bedingungen, kriteriumVierChoiceBox, operatorVierChoiceBox, argumentVierComboBox);

        Collection<Buchung> buchungsColl = HausJpaPersistence.getInstance().selectBuchungen(bedingungen);
        ArrayList<Buchung> buchungsList = new ArrayList<>(buchungsColl);
        Collections.sort(buchungsList, buchungsComparator);

        String summenString = berechneSummenString(buchungsColl);

        buchungen.clear();
        buchungen.addAll(buchungsList);
        buchungTableView.setItems(buchungen);
        summeLabel.setText(summenString);

        pruefeButtons();
    }

    private String berechneSummenString(Collection<Buchung> buchungsColl) {
        double summe = 0.0;
        for (Buchung buchung: buchungsColl) {
            summe += buchung.getBetrag();
        }
        DecimalFormat betragFormat = new DecimalFormat("###,##0.00 €");
        return "Summe Beträge:  " + betragFormat.format(summe);
    }

    private void ermittleBedingung(Collection<Bedingung>  bedingungen, ChoiceBox<Kriterium> kriteriumChoiceBox, ChoiceBox<Operator> operatorChoiceBox, ComboBox argumentComboBox) {
        Kriterium kriterium = kriteriumChoiceBox.getSelectionModel().getSelectedItem();
        Operator operator = operatorChoiceBox.getSelectionModel().getSelectedItem();
        Object arg = null;

        switch (kriterium) {
            case HAUS:
                arg = argumentComboBox.getValue();
                break;
            case KATEGORIE:
                arg = argumentComboBox.getEditor().getCharacters().toString();
                break;
            case DATUM:
                Object arg1 = argumentComboBox.getValue();
                String arg2 = argumentComboBox.getEditor().getCharacters().toString();
                arg = arg2.isEmpty() ? arg1 : arg2;
                break;
            case BETRAG:
            case EMPFAENGER:
            case VERWENDUNG:
                arg = argumentComboBox.getEditor().getCharacters().toString();
                break;
        }

        if (pruefeKriterium(kriterium, operator, arg)  == true) {
            bedingungen.add(new Bedingung(kriterium, operator, arg));
        }
    }

    private boolean pruefeKriterium(Kriterium kriterium, Operator operator, Object arg) {
        if (kriterium == null || kriterium.ordinal() == 0 || operator == null || arg == null) return false;
        return true;
    }
    
    private void pruefeButtons() {
        /**
         *

        int selectedIndex = hausChoiceBox.getSelectionModel().getSelectedIndex();

        if (dateiNameTextField.getText().isEmpty() || selectedIndex == 0) {
            importButton.setDisable(true);
        } else {
            importButton.setDisable(false);
        }

        if (ausgabePositionen.isEmpty()) {
            buchungButton.setDisable(true);
            sichernButton.setDisable(true);
        } else {
            buchungButton.setDisable(false);
            sichernButton.setDisable(false);
        }
         */
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        buchungen = FXCollections.observableArrayList();

        initializeChoiceBoxes();
        initializeTableView();
        initializeTableViewColumns();

        zeigeBuchungenLaufendesJahr();

        pruefeButtons();

    }

    private void zeigeBuchungenLaufendesJahr() {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        String suchArgument = String.valueOf(year) + "-01-01";

        kriteriumEinsChoiceBox.setValue(Kriterium.DATUM);
        operatorEinsChoiceBox.setValue(Operator.GROESSER_GLEICH);
        argumentEinsComboBox.setValue(suchArgument);

        suchenButton.fire();
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
    /**
     * Zeigt die übergebene Meldung an
     * @param message Meldung, die gezeigt werden soll
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void initializeChoiceBoxes(){
        initializeHausChoiceBox();
        initializeKriterienChoiceBoxes();
        initializeOperatorenChoiceBoxes();

    }

    private void initializeKriterienChoiceBoxes(){

        StringConverter<Kriterium> kriteriumKonverter = new StringConverter<Kriterium>() {
            @Override
            public String toString(Kriterium kriterium) {
                return kriterium.getName();
            }

            @Override
            public Kriterium fromString(String string) {
                throw new UnsupportedOperationException("Not supported");
            }
        };

        kriterienEins = FXCollections.observableArrayList();
        kriterienEins.addAll(Kriterium.values());
        kriterienEins.remove(Kriterium.HAUS);
        kriteriumEinsChoiceBox.setConverter(kriteriumKonverter);
        kriteriumEinsChoiceBox.setItems(kriterienEins);
        kriteriumEinsChoiceBox.getSelectionModel().select(0);
        kriteriumEinsChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleKriteriumSelected(1, newValue));

        kriterienZwei = FXCollections.observableArrayList();
        kriterienZwei.addAll(Kriterium.values());
        kriterienZwei.remove(Kriterium.HAUS);
        kriteriumZweiChoiceBox.setConverter(kriteriumKonverter);
        kriteriumZweiChoiceBox.setItems(kriterienZwei);
        kriteriumZweiChoiceBox.getSelectionModel().select(0);
        kriteriumZweiChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleKriteriumSelected(2, newValue));

        kriterienDrei = FXCollections.observableArrayList();
        kriterienDrei.addAll(Kriterium.values());
        kriterienDrei.remove(Kriterium.HAUS);
        kriteriumDreiChoiceBox.setConverter(kriteriumKonverter);
        kriteriumDreiChoiceBox.setItems(kriterienDrei);
        kriteriumDreiChoiceBox.getSelectionModel().select(0);
        kriteriumDreiChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleKriteriumSelected(3, newValue));

        kriterienVier = FXCollections.observableArrayList();
        kriterienVier.addAll(Kriterium.values());
        kriterienVier.remove(Kriterium.HAUS);
        kriteriumVierChoiceBox.setConverter(kriteriumKonverter);
        kriteriumVierChoiceBox.setItems(kriterienVier);
        kriteriumVierChoiceBox.getSelectionModel().select(0);
        kriteriumVierChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleKriteriumSelected(4, newValue));

    }

    private void handleKriteriumSelected(int i, Kriterium kriterium) {

        switch (i) {
            case 1: kriteriumChanged(kriterium, operatorEinsChoiceBox, argumentEinsComboBox);
                break;
            case 2: kriteriumChanged(kriterium, operatorZweiChoiceBox, argumentZweiComboBox);
                break;
            case 3: kriteriumChanged(kriterium, operatorDreiChoiceBox, argumentDreiComboBox);
                break;
            case 4: kriteriumChanged(kriterium, operatorVierChoiceBox, argumentVierComboBox);
                break;
        }
    }

    private void kriteriumChanged(Kriterium kriterium, ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {

        switch (kriterium) {
            case KEIN_KRITERIUM: prepareKeinKriterium(operatorChoiceBox, argumentComboBox);
                break;
            case DATUM: prepareDatumBoxes(operatorChoiceBox, argumentComboBox);
                break;
            case EMPFAENGER: prepareEmpfaengerBoxes(operatorChoiceBox, argumentComboBox);
                break;
            case KATEGORIE: prepareKategorieBoxes(operatorChoiceBox, argumentComboBox);
                break;
            case BETRAG: prepareBetragBoxes(operatorChoiceBox, argumentComboBox);
                break;
            case VERWENDUNG: prepareVerwendungBoxes(operatorChoiceBox, argumentComboBox);
                break;
        }
    }

    private void prepareKeinKriterium(ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {
        operatorChoiceBox.getItems().remove(0, operatorChoiceBox.getItems().size());
        // operatorChoiceBox.setDisable(true);
        argumentComboBox.setValue("");
        argumentComboBox.getItems().remove(0, argumentComboBox.getItems().size());
        // argumentComboBox.setDisable(true);
    }

    private void prepareDatumBoxes(ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {
        operatorChoiceBox.getItems().remove(0, operatorChoiceBox.getItems().size());
        operatorChoiceBox.getItems().addAll(Operator.values());
        operatorChoiceBox.getItems().remove(Operator.REGEX);
        operatorChoiceBox.getSelectionModel().select(Operator.GROESSER);
        argumentComboBox.getItems().remove(0, argumentComboBox.getItems().size());
        argumentComboBox.setValue("JJJJ-MM-TT");
    }

    private void prepareEmpfaengerBoxes(ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {
        operatorChoiceBox.getItems().remove(0, operatorChoiceBox.getItems().size());
        operatorChoiceBox.getItems().add(Operator.GLEICH);
        operatorChoiceBox.getSelectionModel().select(0);
        argumentComboBox.getItems().remove(0, argumentComboBox.getItems().size());
        argumentComboBox.setValue("");
    }

    private void prepareKategorieBoxes(ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {
        operatorChoiceBox.getItems().remove(0, operatorChoiceBox.getItems().size());
        operatorChoiceBox.getItems().add(Operator.GLEICH);
        operatorChoiceBox.getSelectionModel().select(0);

        argumentComboBox.getItems().remove(0, argumentComboBox.getItems().size());
        for (BuchungsKategorie buchungsKategorie : BuchungsKategorie.values()) {
            argumentComboBox.getItems().add(buchungsKategorie.getBeschreibung());
        }
        argumentComboBox.getSelectionModel().select(0);
    }

    private void prepareBetragBoxes(ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {
        operatorChoiceBox.getItems().remove(0, operatorChoiceBox.getItems().size());
        operatorChoiceBox.getItems().addAll(Operator.values());
        operatorChoiceBox.getItems().remove(Operator.REGEX);
        operatorChoiceBox.getSelectionModel().select(0);
        argumentComboBox.getItems().remove(0, argumentComboBox.getItems().size());
        argumentComboBox.setValue("0,00");
    }

    private void prepareVerwendungBoxes(ChoiceBox<Operator> operatorChoiceBox, ComboBox<String> argumentComboBox) {
        operatorChoiceBox.getItems().remove(0, operatorChoiceBox.getItems().size());
        operatorChoiceBox.getItems().add(Operator.GLEICH);
        operatorChoiceBox.getSelectionModel().select(0);
        argumentComboBox.getItems().remove(0, argumentComboBox.getItems().size());
        argumentComboBox.setValue("");
    }



    private void initializeOperatorenChoiceBoxes(){

        StringConverter<Operator> operatorKonverter = new StringConverter<Operator>() {
            @Override
            public String toString(Operator operator) {
                return operator.getName();
            }

            @Override
            public Operator fromString(String string) {
                throw new UnsupportedOperationException("Not supported");
            }
        };

        operatorEinsChoiceBox.setConverter(operatorKonverter);
        operatorZweiChoiceBox.setConverter(operatorKonverter);
        operatorDreiChoiceBox.setConverter(operatorKonverter);
        operatorVierChoiceBox.setConverter(operatorKonverter);
        operatorEinsChoiceBox.setValue(Operator.KEIN_OPERATOR);
        operatorZweiChoiceBox.setValue(Operator.KEIN_OPERATOR);
        operatorDreiChoiceBox.setValue(Operator.KEIN_OPERATOR);
        operatorVierChoiceBox.setValue(Operator.KEIN_OPERATOR);

    }

    public void handleHausSelectionction(Haus haus) {
        setHaus(haus);
        pruefeButtons();
    }

    private void initializeHausChoiceBox(){
        Collection<Haus> haeuserColl = HausJpaPersistence.getInstance().selectAllHaeuser();
        haeuser = FXCollections.observableArrayList();

        Haus aufforderung = new Haus();
        aufforderung.setName("   ... Haus");
        haeuser.add(aufforderung);
        haeuser.addAll(haeuserColl);
        hausChoiceBox.setItems(haeuser);
        hausChoiceBox.getSelectionModel().select(aufforderung);

        hausChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleHausSelectionction(newValue));

        hausChoiceBox.setConverter(
                new StringConverter<Haus>() {
                    @Override
                    public String toString(Haus haus) {
                        return haus.getName();
                    }

                    @Override
                    public Haus fromString(String string) {
                        throw new UnsupportedOperationException("Not supported");
                    }
                }
        );

        // momentan kein Auswahlkriterium
        hausChoiceBox.setVisible(false);

    }

    private void initializeTableView(){
        //ausgabePositionTableView.prefHeightProperty().bind(ausgabePositionenScrollPane.heightProperty());
        //ausgabePositionTableView.prefWidthProperty().bind(ausgabePositionenScrollPane.widthProperty());
    }

    private void initializeTableViewColumns() {
        datumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getDatumInternational());
                    return property;
                });

        buchNrCol.setCellFactory(TextFieldTableCell.forTableColumn());

        buchNrCol.setCellValueFactory(
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

        betragCol.setStyle( "-fx-alignment: CENTER-RIGHT;");
        betragCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getBetragFormatiert());
                    return property;
                });

        kategorieCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getKategorie());
                    return property;
                });

        verwendungCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getVerwendung());
                    return property;
                });

        /**
        hausCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Buchung buchung = (Buchung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(buchung.getAusgabe().getHaus().getName());
                    return property;
                });
         **/
    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) buchungTableView.getScene().getWindow();
        stage.close();
    }
}
