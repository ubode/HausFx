package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.EntfernungsManager;
import de.ubo.fx.fahrten.business.Fahrt;
import de.ubo.fx.fahrten.business.Ziel;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

public class FahrtenbuchController implements Initializable, CloseRequestable {

    private final static Logger LOGGER = Logger.getLogger(FahrtenbuchController.class.getName());
    public DatePicker eingabeDatePicker;
    public TextField routeErgebnis;
    public TextField kmErgebnis;
    public TextField datumErgebnis;
    public TextField anlassErgebnis;
    public TableView fahrtenTableView;
    public TableColumn datumCol;
    public TableColumn routeCol;
    public TableColumn anlassCol;
    public TableColumn kmCol;
    public RadioButton handwerker;
    public RadioButton mieter;
    public RadioButton uebergabe;
    public RadioButton besichtigung;
    public RadioButton schluessel;
    public RadioButton interessenten;
    public RadioButton hausUndGrund;
    public RadioButton ablesung;
    public Button plusButton;
    public Button minusButton;
    public Button saveButton;
    public Button clearButton;
    public ChoiceBox<String> jahrChoiceBox;
    public SplitPane fahrtenbuchSplitPane;
    public GridPane buttonGridPane;


    private String text;
    private Integer[][] strecken;
    private ArrayList<String> orte = new ArrayList<>(10);
    private ArrayList<Integer> indices = new ArrayList<>(10);
    private int index;
    private int[] werte;
    private ObservableList<Fahrt> fahrten;
    private ObservableList<String> jahre;
    private UpdateManager<Fahrt> updateManager = new UpdateManager<>(50);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Comparator<Fahrt> fahrtenComparator = Comparator.comparing(Fahrt::getDatum);
    private List<Ziel> ziele;

    {
        setText("");

        setStrecken(new Integer[13][13]);
        werte = new int[]{0, 1, 1, 7, 14, 27, 24, 1, 1, 1, 6, 16, 18, 0, 2, 8, 14, 26, 25, 2, 2, 2, 5, 16, 19, 0, 7, 14, 28, 23, 1, 1, 1, 5, 16, 19, 0, 13, 29, 27, 7, 7, 7,
                16, 15, 14, 0, 39, 18, 14, 14, 14, 19, 2, 7, 0, 53, 28, 27, 28, 30, 40, 44, 0, 23, 23, 23, 26, 18, 14, 0, 1, 1, 5, 16, 21, 0, 1, 5, 16, 21, 0, 5, 16, 21, 0, 16, 23, 0, 4, 0};

        int k = 0;

        for (int i = 0; i < 13; i++) {
            for (int j = i; j < 13; j++) {
                getStrecken()[i][j] = werte[k];
                getStrecken()[j][i] = werte[k];
                k++;
            }
        }

        ziele = new ArrayList<>(HausJpaPersistence.getInstance().selectZiele());
        Collections.sort(ziele);

    }

    public FahrtenbuchController() {
    }

    public List<Ziel> getZiele() {
        return ziele;
    }

    private UpdateManager<Fahrt> getUpdateManager() {
        return updateManager;
    }

    public TextField getAnlassErgebnis() {
        return anlassErgebnis;
    }

    public ObservableList getFahrten() {
        return fahrten;
    }

    public TextField getDatumErgebnis() {
        return datumErgebnis;
    }

    public TextField getKmErgebnis() {
        return kmErgebnis;
    }

    public TextField getRouteErgebnis() {
        return routeErgebnis;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getOrte() {
        return orte;
    }

    public void setOrte(ArrayList<String> orte) {
        this.orte = orte;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.indices = indices;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Integer[][] getStrecken() {
        return strecken;
    }

    public void setStrecken(Integer[][] strecken) {
        this.strecken = strecken;
    }

    public void handleDateAction(ActionEvent event) {
        String datumString = ((DatePicker) event.getSource()).getEditor().getText();
        getDatumErgebnis().setText(datumString);
        LOGGER.info("Date picked: " + datumString);
        pruefeButtons();
    }

    public void handleJahrChoice(MouseEvent event) {
        LOGGER.info("Jahr gewählt");
        Object o = ((ChoiceBox) event.getSource()).getValue();
        System.out.println(o.toString());
        fillTable(Integer.valueOf(o.toString()));

    }

    public void handleRadioButton(ActionEvent event) {
        LOGGER.info("RadioButton ausgelöst");
        String anlassText = createAnlass();
        getAnlassErgebnis().setText(anlassText);
        pruefeButtons();

    }

    public void handleStreckeButton(ActionEvent event) {
        LOGGER.info("StreckeButton ausgelöst");
        String anlassText = createAnlass();
        getAnlassErgebnis().setText(anlassText);
        pruefeButtons();

    }

    public void fillErgebnis(Fahrt fahrt) {

        LOGGER.info("Table row selected");

        datumErgebnis.setText(fahrt.getDatumFormatiert());
        routeErgebnis.setText(fahrt.getRoute());
        anlassErgebnis.setText(fahrt.getAnlass());
        kmErgebnis.setText(String.valueOf(fahrt.getStrecke()));
        pruefeButtons();

    }

    public void fillTable(int jahr) {
        LOGGER.info("fill Table");
        fahrten.clear();
        Collection<Fahrt> fahrtenColl = HausJpaPersistence.getInstance().selectFahrten(jahr);
        ArrayList<Fahrt> fahrtenList = new ArrayList<>(fahrtenColl);
        Collections.sort(fahrtenList, fahrtenComparator);
        fahrten.addAll(fahrtenList);
        fahrtenTableView.setItems(fahrten);
    }

    public void handleActionButton(ActionEvent event) {
        LOGGER.info("Action ausgelöst");

        int index = fahrtenTableView.getSelectionModel().getSelectedIndex();
        Fahrt fahrt = index < 0 ? null : (Fahrt) fahrtenTableView.getItems().get(index);

        String id = ((Button) event.getSource()).getId();

        switch (id) {
            case ("saveButton"):
                saveUpdates();
                break;
            case ("clearButton"):
                initializeFields();
                break;
            case ("plusButton"):
                if (fahrt != null && fahrt.getId() > 0) {
                    updateTableEntry();
                } else {
                    createTableEntry();
                }
                break;
            case ("minusButton"):
                deleteTableEntry();
                break;
            default:
                LOGGER.info("nichts zu tun");
        }

    }


    private void saveUpdates() {

        LOGGER.info("save Updates");

        getUpdateManager().saveUpdates();

    }

    private void deleteTableEntry() {

        LOGGER.info("delete Table Entry");

        int index = fahrtenTableView.getSelectionModel().getSelectedIndex();
        Fahrt deleteFahrt = (Fahrt) fahrtenTableView.getItems().get(index);

        fahrtenTableView.getItems().remove(index, index + 1);

        /* DB-Änderungen verwalten */
        getUpdateManager().addDelete(deleteFahrt);

        /* GUI aktualisieren */
        initializeFields();
        pruefeButtons();
    }

    private void pruefeButtons() {

        if (datumErgebnis.getText().isEmpty() || routeErgebnis.getText().isEmpty() || anlassErgebnis.getText().isEmpty() || kmErgebnis.getText().isEmpty()) {
            plusButton.setDisable(true);
            minusButton.setDisable(true);
        } else {
            plusButton.setDisable(false);
            minusButton.setDisable(false);
        }

        if (!datumErgebnis.getText().isEmpty() || !routeErgebnis.getText().isEmpty() || !anlassErgebnis.getText().isEmpty() || !kmErgebnis.getText().isEmpty()) {
            clearButton.setDisable(false);
        } else {
            clearButton.setDisable(true);
        }

        if (fahrten.size() > 0) {
            saveButton.setDisable(false);
        } else {
            saveButton.setDisable(true);
        }

    }

    private String createAnlass() {
        StringBuilder anlassText = new StringBuilder();
        if (handwerker.isSelected()) {
            anlassText.append("Absprache mit Handwerker / ");
        }
        if (mieter.isSelected()) {
            anlassText.append("Treffen mit Mieter / ");
        }
        if (uebergabe.isSelected()) {
            anlassText.append("Wohnungsübergabe / ");
        }
        if (besichtigung.isSelected()) {
            anlassText.append("Wohnungsbesichtigung / ");
        }
        if (schluessel.isSelected()) {
            anlassText.append("Schlüsselübergabe / ");
        }
        if (interessenten.isSelected()) {
            anlassText.append("Treffen mit Interessenten / ");
        }
        if (hausUndGrund.isSelected()) {
            anlassText.append("Haus und Grund / ");
        }
        if (ablesung.isSelected()) {
            anlassText.append("Ablesung Zähler / ");
        }

        String anlassString = anlassText.length() > 3 ? anlassText.substring(0, (anlassText.length() - 3)) : "";

        return anlassString;

    }

    private void createTableEntry() {

        LOGGER.info("neuer Tabelleneintrag");

        String datumText = getDatumErgebnis().getText();
        String routeText = getRouteErgebnis().getText();
        String anlassText = getAnlassErgebnis().getText();
        Integer kmInt = Integer.parseInt(getKmErgebnis().getText());

        Fahrt neueFahrt = new Fahrt(null, datumText, routeText, anlassText, kmInt);
        fahrten.add(0, neueFahrt);
        fahrtenTableView.setItems(fahrten);

        getUpdateManager().addInsert(neueFahrt);

        initializeFields();
        pruefeButtons();

    }

    private void updateTableEntry() {

        LOGGER.info("Ändern Tabelleneintrag");

        int index = fahrtenTableView.getSelectionModel().getSelectedIndex();
        Fahrt updateFahrt = (Fahrt) fahrtenTableView.getItems().get(index);

        updateFahrt.setDatum(getDatumErgebnis().getText());
        updateFahrt.setRoute(getRouteErgebnis().getText());
        updateFahrt.setAnlass(getAnlassErgebnis().getText());
        updateFahrt.setStrecke(Integer.parseInt(getKmErgebnis().getText()));

        fahrtenTableView.refresh();
        getUpdateManager().addUpdate(updateFahrt);

        initializeFields();
        pruefeButtons();

    }

    public void initializeFields() {

        eingabeDatePicker.getEditor().setText("");

        getDatumErgebnis().setText("");
        getRouteErgebnis().setText("");
        getKmErgebnis().setText("");
        getAnlassErgebnis().setText("");

        handwerker.setSelected(false);
        mieter.setSelected(false);
        uebergabe.setSelected(false);
        besichtigung.setSelected(false);
        schluessel.setSelected(false);
        interessenten.setSelected(false);
        hausUndGrund.setSelected(false);
        ablesung.setSelected(false);

        getIndices().clear();
        orte.clear();
    }

    private void initializeButtons() {

        int i = 0;
        Iterator<Node> nodeIterator = buttonGridPane.getChildren().iterator();
        Iterator<Ziel> zielIterator = getZiele().iterator();

        while (nodeIterator.hasNext()) {

            Node node = nodeIterator.next();

            if (node instanceof Button) {
                Button button = (Button) node;

                if (zielIterator.hasNext()) {
                    Ziel ziel = zielIterator.next();
                    button.setText(ziel.getKurzText());
                } else {
                    button.setDisable(true);
                    button.setVisible(false);
                }

            }
        }
    }

    public void handleOrtButtonAction(ActionEvent event) {
        LOGGER.info("Ort gewählt");

        String id = ((Button) event.getSource()).getId();
        String alterText = routeErgebnis.getText();

        index = Integer.valueOf(id.substring(4)) - 1;
        text = getZiele().get(index).getOrt();

        String delimiter = "";

        indices.add(index);

        if (!alterText.equals("")) {
            delimiter = " -> ";
        }

        if (orte.size() == 0 || orte.get(orte.size() - 1).equals(text) == false) {
            orte.add(text);
            getRouteErgebnis().setText(alterText + delimiter + text);
        }

        getKmErgebnis().setText(String.valueOf(berechneStrecke()));
        pruefeButtons();

    }

    /* Gesamtstrecke aus Summe der Teilstrecken ermitteln */
    public int berechneStrecke() {

        int strecke = 0;
        int erster = -1;
        int zweiter;

        for (Integer index : getIndices()) {
            zweiter = index;

            if (erster != -1) {
                Ziel vonZiel = getZiele().get(erster);
                Ziel bisZiel = getZiele().get(zweiter);
                int entfernung = EntfernungsManager.instance().getEntfernung(vonZiel, bisZiel);
                strecke += entfernung;
            }

            erster = zweiter;
        }

        return strecke;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        fahrten = FXCollections.observableArrayList();

        jahre = FXCollections.observableArrayList();

        /* Fuellen der ChoiceBox */
        LocalDate date = LocalDate.now();
        int jahr = date.getYear();

        for (int i = jahr - 2; i < jahr + 3; i++) {
            jahre.add(String.valueOf(i));
        }

        jahrChoiceBox.setItems(jahre);

        jahrChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fillTable(Integer.valueOf(newValue)));

        fahrtenTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fillErgebnis((Fahrt) newValue));

        datumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Fahrt fahrt = (Fahrt) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(fahrt.getDatumFormatiert());
                    return property;
                });

        kmCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        kmCol.setCellValueFactory(
                cellData -> {
                    SimpleIntegerProperty property = new SimpleIntegerProperty();
                    Fahrt fahrt = (Fahrt) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(fahrt.getStrecke());
                    return property;
                });

        jahrChoiceBox.setValue(String.valueOf(jahr));

        initializeButtons();
    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) fahrtenbuchSplitPane.getScene().getWindow();
        stage.close();
    }
}
