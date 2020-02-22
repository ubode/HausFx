package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.Ausgabe;
import de.ubo.fx.fahrten.business.AusgabePosition;
import de.ubo.fx.fahrten.business.Buchung;
import de.ubo.fx.fahrten.business.Haus;
import de.ubo.fx.fahrten.converter.AusgabenWrapper;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.io.AusgabenReader;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ulric on 01.08.2016.
 */
public class XlsxImportController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(XlsxImportController.class.getName());
    private final UpdateManager<Ausgabe> updateManager = new UpdateManager<>(500);
    public TextField dateiNameTextField;
    public TableView<AusgabePosition> ausgabePositionTableView;
    public TableColumn datumCol;
    public TableColumn vorgangCol;
    public TableColumn empfaengerCol;
    public TableColumn verwendungCol;
    public TableColumn kategorieCol;
    public TableColumn betragCol;
    public TableColumn buchNrCol;
    public Button importButton;
    public Button sichernButton;
    public Button buchungButton;
    public ChoiceBox<Haus> hausChoiceBox;
    public ChoiceBox<Sheet> sheetChoiceBox;
    private ObservableList<AusgabePosition> ausgabePositionen;
    private ObservableList<Haus> haeuser;
    private ObservableList<Sheet> sheets;
    private Map<String, Buchung> buchungMap;
    private Haus haus;

    private Haus getHaus() {
        return haus;
    }

    private void setHaus(Haus haus) {
        this.haus = haus;
    }

    private Map<String, Buchung> getBuchungMap() {
        if (buchungMap == null) {
            erzeugeBuchungMap();
        }
        return buchungMap;
    }

    private UpdateManager<Ausgabe> getUpdateManager() {
        return updateManager;
    }

    public void handleSichernAction(ActionEvent event) {
        LOGGER.info("SichernButton ausgelöst");
        saveAusgaben();
        pruefeButtons();
    }

    public void handleFileChooserAction(ActionEvent event) {
        LOGGER.info("FileChooserButton ausgelöst");
        FileChooser fileChooser = new FileChooser();
        File initialVerzeichnis = new File("D:/Haeuser/Ein-Aus/");
        if (initialVerzeichnis.exists()) {
            fileChooser.setInitialDirectory(initialVerzeichnis);
        }
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel-Datei (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        dateiNameTextField.setText(file.getAbsolutePath());

        ladeSheetChoiceBox();
        pruefeButtons();

    }

    public void handleImportAction(ActionEvent event) {
        LOGGER.info("ImportButton ausgelöst");
        String meldung = "";
        List<Ausgabe> ausgabenList = new ArrayList<>();

        String fileName = dateiNameTextField.getText();


        if (pruefeFileName(fileName) == false) {
            meldung = "Unzulässiger Dateiname";
        } else {
            try {
                AusgabenReader reader = new AusgabenReader();
                ausgabenList = reader.readAusgaben(fileName);
            } catch (IOException e) {
                meldung = "Fehler beim Öffnen der Datei " + fileName;
            }
        }

        if (meldung.isEmpty()) {
            ausgabePositionen.clear();
            for (Ausgabe ausgabe: ausgabenList) {
                ausgabePositionen.addAll(ausgabe.getPositionen());
            }
            ausgabePositionTableView.setItems(ausgabePositionen);
            meldung = String.valueOf(ausgabenList.size()) + " Ausgaben importiert";
        }

        showMessage(meldung);
        pruefeButtons();
    }

    public void handleBuchungsNrAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Buchungen manuell zuordnen");

        String buchNr = (String) event.getNewValue();

        int index = ausgabePositionTableView.getSelectionModel().getSelectedIndex();
        AusgabePosition position = ausgabePositionTableView.getItems().get(index);

        if (buchNr.isEmpty()) {

            position.getAusgabe().setBuchung(null);

        } else {

            Buchung buchung = buchungMap.get(buchNr);
            if (buchung == null) {
                showError("Buchungsnummer unbekannt");
            } else {
                position.getAusgabe().setBuchung(buchung);
            }

        }

        pruefeButtons();
        ausgabePositionTableView.refresh();
    }

    public void handleBuchungAction(ActionEvent event) {
        LOGGER.info("Buchungen zuordnen");

        int indexMitte = ausgabePositionen.size() / 2;
        Date datum = ausgabePositionen.get(indexMitte).getAusgabe().getDatum();

        Collection<Buchung> buchungen = HausJpaPersistence.getInstance().selectBuchungen(datum);

        int anzahlTreffer = ordneBuchungenZu(buchungen);
        showMessage("Es konnten " + anzahlTreffer + " Buchungen zugeordnet werden.");

        pruefeButtons();
        ausgabePositionTableView.refresh();
    }

    public void handleHausSelectionAction(Haus haus) {
        setHaus(haus);
        pruefeButtons();
    }

    public void handleSheetSelectionAction(Sheet sheet) {
        ladeAusgaben(sheet);
        pruefeButtons();
    }

    private void ladeAusgaben(Sheet sheet) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        Collection<Object[]> rowObjects = new ArrayList<>(150);
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Object[] rowValues = new Object[16];
            Row row = rowIterator.next();
            if (isAusgabenZeile(evaluator, row)) {
                rowValues[0] = row.getCell(0).getDateCellValue();

                for (int i = 1; i < 4; i++) {
                    System.out.println("i =" + i + " " + row.getCell(i).toString());
                    try {
                        rowValues[i] = row.getCell(i).getStringCellValue();
                    } catch (Exception e) {
                        Integer refInt = (int) row.getCell(i).getNumericCellValue();
                        rowValues[i] = refInt.toString();
                    }
                }

                for (int i = 4; i < 15; i++) {
                    try {
                        rowValues[i] = row.getCell(i).getNumericCellValue();
                    } catch (Exception e) {
                        rowValues[i] = 0.0d;
                    }
                }

                rowObjects.add(rowValues);
            }
        }

        Collection<Ausgabe> ausgaben = AusgabenWrapper.wrap(rowObjects);
        ausgabePositionen.clear();
        for (Ausgabe ausgabe: ausgaben) {
            ausgabePositionen.addAll(ausgabe.getPositionen());
        }
        ausgabePositionTableView.setItems(ausgabePositionen);
        ausgabePositionTableView.refresh();
    }

    private boolean isAusgabenZeile(FormulaEvaluator evaluator, Row row) {
        Cell cell = row.getCell(15);
        if (cell == null) return false;
        if (evaluator.evaluateFormulaCellEnum(cell).equals(CellType.NUMERIC) == false) return false;
        if (row.getCell(3).getStringCellValue().isEmpty()) return false;

        return true;
    }

    private void ladeSheetChoiceBox(){
        XSSFWorkbook workBook = null;
        Collection<Sheet> sheetCol = new ArrayList<>();
        String fileName = dateiNameTextField.getText();

        try (FileInputStream fileIn = new FileInputStream(fileName)) {
            workBook = new XSSFWorkbook(fileIn);
        } catch (IOException e) {

        }

        for (Sheet sheet: workBook) {
            String sheetName = sheet.getSheetName();
            if (sheetName.contains("A")
                    && (sheetName.contains("K4")
                    || sheetName.contains("H1")
                    || sheetName.contains("H4")
                    || sheetName.contains("S5"))) {
                sheetCol.add(sheet);
            }
        }

        sheets.clear();
        sheets.addAll(sheetCol);
        sheetChoiceBox.setItems(sheets);
        sheetChoiceBox.getSelectionModel().select(0);
    }

    /**
     * Ordnet den Excel-Ausgaben die entsprechende Bankbuchung zu:
     * - alle Bankbuchungen in eine Map. Key: Monat, Tag, Betrag
     * - für alle Excel-Ausgaben entsprechende Buchung suchen
     * - klappt nicht für Buchungen, die über die Häuser verteilt wurden, wie z.B. Steuerberatung, Haus+Grund
     * - zu beachten: die Beträge als Bankbuchung sind negativ, im Excel sind sie positiv eingetragen
     *
     */
    private int ordneBuchungenZu(Collection<Buchung> buchungen) {
        MultiKeyMap<MultiKey,Buchung> buchungMultiKeyMap = new MultiKeyMap();
        buchungMap = new HashMap<>(2000);
        int anzahlTreffer = 0;

        for (Buchung buchung: buchungen) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(buchung.getDatum());
            MultiKey key = new MultiKey(calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), buchung.getBetrag());
            buchungMultiKeyMap.put(key, buchung);
            buchungMap.put(buchung.getBuchungsNummer(), buchung);
        }

        for (AusgabePosition ausgabePosition: ausgabePositionen) {
            Ausgabe ausgabe = ausgabePosition.getAusgabe();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(ausgabe.getDatum());
            double betrag = ausgabe.getBetrag() * -1.0d;
            MultiKey key = new MultiKey(calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), betrag);
            Buchung buchung = buchungMultiKeyMap.get(key);
            if (buchung != null) {
                ausgabe.setBuchung(buchung);
                anzahlTreffer++;
            }
        }

        return anzahlTreffer;
    }

    private void erzeugeBuchungMap() {

        buchungMap = new HashMap<>(2000);
        Date datum = ausgabePositionen.get(0).getAusgabe().getDatum();

        Collection<Buchung> buchungen = HausJpaPersistence.getInstance().selectBuchungen(datum);
        for (Buchung buchung: buchungen) {
            buchungMap.put(buchung.getBuchungsNummer(), buchung);
        }
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

    private void saveAusgaben() {

        System.out.println("Ausgaben in DB sichern");

        Set<Ausgabe> ausgaben = new HashSet<>(200, 0.8f);

        for (AusgabePosition ausgabePos: ausgabePositionen) {
            ausgaben.add(ausgabePos.getAusgabe());
            if (ausgabePos.getAusgabe() == null || ausgabePos.getAusgabe().getId() == null) {
                LOGGER.info("Ausgabe: " + ausgabePos.getAusgabe().getId());
            }
        }

        for (Ausgabe ausgabe: ausgaben) {
            ausgabe.setHaus(getHaus());
            getUpdateManager().addInsert(ausgabe);
            if (ausgabe.getId() == null) {
                LOGGER.info("Ausgabe ohne Id");
            }
        }

        // die erste Ausgabe kann noch im Vorjahr liegen
        Ausgabe ausgabe = ausgaben.iterator().next();
        if (ausgaben.iterator().hasNext()) ausgabe = ausgaben.iterator().next();
        if (ausgaben.iterator().hasNext()) ausgabe = ausgaben.iterator().next();
        Date datum = ausgabe.getDatum();
        HausJpaPersistence.getInstance().deleteAusgaben(datum, ausgabe.getHaus());

        getUpdateManager().saveUpdates();

        String meldung = String.valueOf(ausgaben.size()) + " Ausgaben gesichert";
        showMessage(meldung);

    }

    private void pruefeButtons() {
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

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        ausgabePositionen = FXCollections.observableArrayList();

        initializeHausChoiceBox();
        initializeSheetChoiceBox();
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


    private void initializeHausChoiceBox(){
        Collection<Haus> haeuserColl = HausJpaPersistence.getInstance().selectAllHaeuser();
        haeuser = FXCollections.observableArrayList();

        Haus aufforderung = new Haus();
        aufforderung.setName("   ... wähle Haus");
        haeuser.add(aufforderung);
        haeuser.addAll(haeuserColl);
        hausChoiceBox.setItems(haeuser);
        hausChoiceBox.getSelectionModel().select(aufforderung);

        hausChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleHausSelectionAction(newValue));

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

    }

    private void initializeSheetChoiceBox(){

        sheets = FXCollections.observableArrayList();
        sheetChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleSheetSelectionAction(newValue));
        sheetChoiceBox.setConverter(
                new StringConverter<Sheet>() {
                    @Override
                    public String toString(Sheet sheet) {
                        return sheet.getSheetName();
                    }

                    @Override
                    public Sheet fromString(String string) {
                        throw new UnsupportedOperationException("Not supported");
                    }
                }
        );

    }

    private void initializeTableView(){
        //ausgabePositionTableView.prefHeightProperty().bind(ausgabePositionenScrollPane.heightProperty());
        //ausgabePositionTableView.prefWidthProperty().bind(ausgabePositionenScrollPane.widthProperty());
    }

    private void initializeTableViewColumns() {
        datumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(ausgabePos.getAusgabe().getDatumFormatiert());
                    return property;
                });

        vorgangCol.setStyle( "-fx-alignment: CENTER-RIGHT;");
        vorgangCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(ausgabePos.getAusgabe().getReferenz());
                    return property;
                });

        empfaengerCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    Ausgabe ausgabe = ausgabePos.getAusgabe();
                    String vertragsNr = ausgabe.getVertragsNummer() == null ? "" : " (" + ausgabe.getVertragsNummer() + ")";
                    property.setValue(ausgabePos.getAusgabe().getEmpfaenger() + vertragsNr);
                    return property;
                });

        verwendungCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(ausgabePos.getBeschreibung());
                    return property;
                });

        kategorieCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(ausgabePos.getAusgabeArt().getKurzBeschreibung());
                    return property;
                });

        betragCol.setStyle( "-fx-alignment: CENTER-RIGHT;");
        betragCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(ausgabePos.getBetragFormatiert());
                    return property;
                });

        buchNrCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        buchNrCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    AusgabePosition ausgabePos = (AusgabePosition) ((TableColumn.CellDataFeatures) cellData).getValue();
                    Buchung buchung = ausgabePos.getAusgabe().getBuchung();
                    if (buchung != null) {
                        property.setValue(buchung.getBuchungsNummer());
                    } else {
                        property.setValue("");
                    }
                    return property;
                });

    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) ausgabePositionTableView.getScene().getWindow();
        stage.close();
    }
}
