package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.*;
import de.ubo.fx.fahrten.converter.ExtendedDoubleStringConverter;
import de.ubo.fx.fahrten.converter.IntegerStringConverter;
import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.helper.DatumHelper;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ulrichbode on 16.02.16.
 */
public class WohnungenController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(WohnungenController.class.getName());
    private static String RGB_BLUE = "#cceeff";
    private static String RGB_GREEN = "#d4f2d2";
    private static String RGB_GREY = "#dddddd";
    private static String RGB_RED = "#ffd9cc";
    private static String RGB_YELLOW = "#ffffe6";
    private static Background BG_RED = new Background(new BackgroundFill(Color.web(RGB_RED), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background BG_GREEN = new Background(new BackgroundFill(Color.web(RGB_GREEN), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background BG_BLUE = new Background(new BackgroundFill(Color.web(RGB_BLUE), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background BG_YELLOW = new Background(new BackgroundFill(Color.web(RGB_YELLOW), CornerRadii.EMPTY, Insets.EMPTY));
    private static String ALLE_MIETER = "alle Mieter";
    private static NumberFormat BETRAG_FORMATTER = DecimalFormat.getCurrencyInstance(Locale.FRANCE);
    public TreeView<WohnObjekt> wohnungTreeView;
    public TextField baujahrTextField;
    public TextField nameTextField;
    public TextField vornameTextField;
    public TextField telVorwahlTextField;
    public TextField telefonTextField;
    public TextField handyVorwahlTextField;
    public TextField handyTextField;
    public TextField faxVorwahlTextField;
    public TextField faxTextField;
    public TextField strasseTextField;
    public TextField hausnummerTextField;
    public TextField regexTextField;
    public TextField plzTextField;
    public TextField ortTextField;
    public TextField landTextField;
    public TextField nummerTextField;
    public TextField geschossTextField;
    public TextField lageTextField;
    public TextField lagebeschreibungTextField;
    public TextField wohnflaecheTextField;
    public TextField summeSollTextField;
    public TextField summeIstTextField;
    public TextField kautionSummeSollTextField;
    public TextField kautionSummeIstTextField;
    public Button neuButton;
    public Button loeschButton;
    public Button abbruchButton;
    public Button saveButton;
    public Button pruefButton;
    public Button kopierButton;
    public ChoiceBox<String> jahrChoiceBox;
    public ChoiceBox<String> mieterChoiceBox;
    public Tab datenTab;
    public Tab mietenTab;
    public Tab kautionTab;
    public Tab zimmerTab;
    public Tab vertragTab;
    public TableView<MietVertrag> vertragTableView;
    public TableColumn<MietVertrag, String> beginnCol;
    public TableColumn<MietVertrag, String> endeCol;
    public TableColumn<MietVertrag, String> mieterCol;
    public TableColumn<MietVertrag, String> regExpCol;
    public TableColumn<MietVertrag, Double> kautionCol;
    public TableColumn<MietVertrag, Double> mietzinsCol;
    public TableColumn<MietVertrag, Double> nebenkostenCol;
    public TableColumn<MietVertrag, Double> heizkostenCol;
    public TableColumn<MietVertrag, String> gesamtkostenCol;
    public TableColumn<MietVertrag, Boolean> anpassungCol;
    public TableView<Zimmer> zimmerTableView;
    public TableColumn<Zimmer, String> nameCol;
    public TableColumn<Zimmer, Double> flaecheCol;
    public TableColumn<Zimmer, Integer> renovierungCol;
    public TableColumn<Zimmer, String> bemerkungCol;
    public TableView<GuiMietzahlung> mietenTableView;
    public TableColumn<GuiMietzahlung, String> mietenDatumCol;
    public TableColumn<GuiMietzahlung, Double> mietenSollCol;
    public TableColumn<GuiMietzahlung, String> mietenMieterCol;
    public TableColumn<GuiMietzahlung, Double> mietenIstCol;
    public TableColumn<GuiMietzahlung, Double> mietenGrundmieteCol;
    public TableColumn<GuiMietzahlung, Double> mietenNebenkostenCol;
    public TableColumn<GuiMietzahlung, String> mietenAbgleichCol;
    public TableView<GuiMietzahlung> kautionTableView;
    public TableColumn<GuiMietzahlung, String> kautionDatumCol;
    public TableColumn<GuiMietzahlung, String> kautionMieterCol;
    public TableColumn<GuiMietzahlung, Double> kautionSollCol;
    public TableColumn<GuiMietzahlung, Double> kautionIstCol;
    public TableColumn<GuiMietzahlung, String> kautionAbgleichCol;
    public GridPane wohnungDetailPane;
    private UpdateManager<MietVertrag> vertragUpdateManager;
    private UpdateManager<Zimmer> zimmerUpdateManager;
    private ObservableList<MietVertrag> vertraegeOL;
    private ObservableList<Zimmer> zimmerOL;
    private ObservableList<GuiMietzahlung> mietZahlungOL;
    private ObservableList<GuiMietzahlung> kautionsZahlungOL;
    private ObservableList<String> jahreOL;
    private ObservableList<String> mieterNamenOL;
    private ObservableList<Wohnung> wohnungOL;
    private MietVertrag aktuellerVertrag;
    private Zimmer aktuellesZimmer;

    private UpdateManager<MietVertrag> getVertragUpdateManager() {
        return vertragUpdateManager;
    }
    private UpdateManager<Zimmer> getZimmerUpdateManager() {
        return zimmerUpdateManager;
    }

    public void handleNeuButton(ActionEvent event) {

        if (zimmerTab.isSelected()) {
            handleZimmerNeuButton();
        } else if (vertragTab.isSelected()) {
            handleVertragNeuButton();
        }

        checkButtons();

    }

    public void handleVertragNeuButton() {

        aktuellerVertrag = new MietVertrag();
        aktuellerVertrag.setBeginn(null);
        aktuellerVertrag.setEnde(null);
        aktuellerVertrag.setWohnung(wohnungTreeView.getSelectionModel().getSelectedItem().getValue().getWohnung());
        aktuellerVertrag.setMieter(null);
        aktuellerVertrag.setRegularExpression(null);
        aktuellerVertrag.setKaution(0.0d);
        aktuellerVertrag.setMietzins(0.0d);
        aktuellerVertrag.setNebenkosten(0.0d);
        aktuellerVertrag.setHeizkosten(0.0d);
        aktuellerVertrag.setAnpassung(false);
        vertraegeOL.add(aktuellerVertrag);

        getVertragUpdateManager().addInsert(aktuellerVertrag);

        vertragTableView.refresh();

    }

    public void handleZimmerNeuButton() {

        aktuellesZimmer = new Zimmer();
        aktuellesZimmer.setName("Zimmer");
        aktuellesZimmer.setWohnung(wohnungTreeView.getSelectionModel().getSelectedItem().getValue().getWohnung());
        aktuellesZimmer.setBemerkung("");
        aktuellesZimmer.setFlaeche(null);
        aktuellesZimmer.setRenovierung(null);
        getZimmerUpdateManager().addInsert(aktuellesZimmer);

        zimmerOL.add(aktuellesZimmer);
        int rowNumber = zimmerOL.size() - 1;
        zimmerTableView.getFocusModel().focus(rowNumber, nameCol);
        zimmerTableView.getSelectionModel().select(rowNumber, nameCol);
        zimmerTableView.edit(rowNumber, nameCol);

    }

    /**
     * Übertagung der Attribute des Hauses in die Ausgabe
     * @param haus anzuzeigendes Haus
     */
    private void fillHaus(Haus haus) {
        if (haus == null) {
            baujahrTextField.clear();
            nameTextField.clear();
            vornameTextField.clear();
            telVorwahlTextField.clear();
            telefonTextField.clear();
            handyVorwahlTextField.clear();
            handyTextField.clear();
            faxVorwahlTextField.clear();
            faxTextField.clear();
            strasseTextField.clear();
            hausnummerTextField.clear();
            plzTextField.clear();
            ortTextField.clear();
            landTextField.clear();
            regexTextField.clear();
        } else {
            baujahrTextField.setText(String.valueOf(haus.getBaujahr()));
            if (haus.getHausmeister() != null) {
                nameTextField.setText(haus.getHausmeister().getName());
                vornameTextField.setText(haus.getHausmeister().getVorname());
                String[] telefon = Person.splitTelefon(haus.getHausmeister().getTelefonPrivat());
                telVorwahlTextField.setText(telefon[0]);
                telefonTextField.setText(telefon[1]);
                String[] handy = Person.splitTelefon(haus.getHausmeister().getHandyPrivat());
                handyVorwahlTextField.setText(handy[0]);
                handyTextField.setText(handy[1]);
                String[] fax = Person.splitTelefon(haus.getHausmeister().getFaxPrivat());
                faxVorwahlTextField.setText(fax[0]);
                faxTextField.setText(fax[1]);
            } else {
                nameTextField.clear();
                vornameTextField.clear();
                telVorwahlTextField.clear();
                telefonTextField.clear();
                handyVorwahlTextField.clear();
                handyTextField.clear();
                faxVorwahlTextField.clear();
                faxTextField.clear();
            }
            strasseTextField.setText(haus.getAdresse().getStrasse());
            hausnummerTextField.setText(haus.getAdresse().getHausnummer());
            regexTextField.setText(String.valueOf(haus.getRegex()));
            plzTextField.setText(haus.getAdresse().getPostleitzahl());
            ortTextField.setText(haus.getAdresse().getOrt());
            landTextField.setText(haus.getAdresse().getLand());
        }

    }

    /**
     * Die Attribute der Wohnung werden in die Anzeige übertragen
     * @param wohnung anzuzeigende Wohnung
     */
    private void fillWohnung(Wohnung wohnung) {
        if (wohnung == null) {
            nummerTextField.clear();
            geschossTextField.clear();
            lageTextField.clear();
            lagebeschreibungTextField.clear();
            wohnflaecheTextField.clear();
            vertragTableView.getItems().clear();
            mietenTableView.getItems().clear();
            summeSollTextField.clear();
            summeIstTextField.clear();
        } else {
            nummerTextField.setText(wohnung.getNummer());
            geschossTextField.setText(wohnung.getGeschoss());
            lageTextField.setText(wohnung.getLage());
            lagebeschreibungTextField.setText(wohnung.getLageBeschreibung());
            wohnflaecheTextField.setText(wohnung.getWohnflaeche());
        }
    }

    private void fillVertagsTable(Wohnung wohnung) {
        LOGGER.info("fillVertagsTable");
        vertraegeOL.clear();
        vertraegeOL.addAll(ermittleVertraege(wohnung));
        vertragTableView.setItems(vertraegeOL);
        checkVertragButtons();
    }

    private void fillZimmerTable(Wohnung wohnung) {
        LOGGER.info("fillZimmerTable");
        zimmerOL.clear();
        Collection<Zimmer> zimmerColl = HausJpaPersistence.getInstance().selectZimmer(wohnung);
        ArrayList<Zimmer> zimmerList = new ArrayList<>(zimmerColl);
        zimmerList.sort(Comparator.comparing(Zimmer::getName));
        zimmerOL.addAll(zimmerList);
        zimmerOL.add(flaechenSumme(zimmerList));
        zimmerTableView.setItems(zimmerOL);
        checkZimmerButtons();
    }

    /**
     * Berechnung der Gesamtfläche aller Zimmer
     * @param zimmerList Liste der Zimmer
     * @return Gesamtfläche als Zimmer-Objekt
     */
    private Zimmer flaechenSumme(ArrayList<Zimmer> zimmerList) {
        double summeFlaeche = 0;
        for (Zimmer zimmer : zimmerList) {
            summeFlaeche += zimmer.getFlaeche();
        }
        Zimmer summenZimmer = new Zimmer();
        summenZimmer.setName("--- Gesamtfläche ---");
        summenZimmer.setFlaeche(summeFlaeche);
        return summenZimmer;
    }

    public void handleVertragSelected(MietVertrag vertrag) {
        aktuellerVertrag = vertrag;
        checkButtons();
    }

    public void handleZimmerSelected(Zimmer zimmer) {
        aktuellesZimmer = zimmer;
        checkButtons();
    }

    public void handleWohnObjektSelected(TreeItem<WohnObjekt> treeItem) {

        if (treeItem.getValue().getWohnung() != null) {
            nameTextField.setEditable(true);
            wohnungDetailPane.setDisable(false);
            mietenTab.setDisable(false);
            kautionTab.setDisable(false);
            vertragTab.setDisable(false);
            zimmerTab.setDisable(false);
        } else {
            wohnungDetailPane.setDisable(true);
            mietenTab.setDisable(true);
            kautionTab.setDisable(true);
            vertragTab.setDisable(true);
            zimmerTab.setDisable(true);
        }
        wohnungSelected(treeItem);
    }


    public void wohnungSelected(TreeItem treeItem) {

        if (datenTab.isSelected()) {
            fillDatenTab(treeItem);
        }

        if (mietenTab.isSelected()) {
            fillMietenTable();
        }

        if (kautionTab.isSelected()) {
            fillMieterChoiceBox();
            fillKautionsTable();
        }

        checkButtons();
    }

    public void handleVertragCellEdit(TableColumn.CellEditEvent event) {

        int index = vertragTableView.getSelectionModel().getSelectedIndex();
        MietVertrag vertrag = index < 0 ? null : vertragTableView.getItems().get(index);
        String id = ((TableColumn) event.getSource()).getId();
        Object inhalt = event.getNewValue();

        switch (id) {
            case ("beginnCol"):
                vertrag.setBeginn(DatumHelper.stringToDate((String)inhalt));
                MietVertrag.berechneEndeDaten(vertraegeOL);
                break;
            case ("endeCol"):
                vertrag.setEnde(DatumHelper.stringToDate((String)inhalt));
                break;
            case ("mieterCol"):
                Collection<Person> personen = PersonManager.getInstance().suchePersonen((String)inhalt);
                if (personen.size() == 1)
                    vertrag.setMieter(personen.iterator().next());
                else
                    vertrag.setMieter(waehlePerson(personen));
                break;
            case ("regExpCol"):
                vertrag.setRegularExpression((String) inhalt);
                break;
            case ("kautionCol"):
                vertrag.setKaution((Double)inhalt);
                break;
            case ("mietzinsCol"):
                vertrag.setMietzins((Double)inhalt);
                break;
            case ("nebenkostenCol"):
                vertrag.setNebenkosten((Double)inhalt);
                break;
            case ("heizkostenCol"):
                vertrag.setHeizkosten((Double)inhalt);
                break;
            case ("folgeVertragCol"):
                vertrag.setAnpassung((Boolean)inhalt);
                break;
            default:
                System.out.println("nichts zu tun");
        }

        aktuellerVertrag = vertrag;
        getVertragUpdateManager().addUpdate(vertrag);
        vertragTableView.refresh();
        checkButtons();

    }

    public void handleZimmerCellEdit(TableColumn.CellEditEvent event) {

        int index = zimmerTableView.getSelectionModel().getSelectedIndex();
        Zimmer zimmer = index < 0 ? null : zimmerTableView.getItems().get(index);
        String id = ((TableColumn) event.getSource()).getId();
        Object inhalt = event.getNewValue();

        switch (id) {
            case ("nameCol"):
                zimmer.setName((String)inhalt);
                break;
            case ("flaecheCol"):
                zimmer.setFlaeche((Double)inhalt);
                break;
            case ("renovierungCol"):
                zimmer.setRenovierung((Integer)inhalt);
                break;
            case ("bemerkungCol"):
                zimmer.setBemerkung((String) inhalt);
                break;
            default:
                System.out.println("nichts zu tun");
        }

        aktuellesZimmer = zimmer;
        getZimmerUpdateManager().addUpdate(zimmer);
        zimmerTableView.refresh();
        checkButtons();

    }

    public void handleSaveButton(ActionEvent event) {

        if (zimmerTab.isSelected()) {
            saveZimmer();
        } else if (vertragTab.isSelected()) {
            saveVertrag();
        }

        checkButtons();
        System.out.println("neue Mietvertraege gesichert");
    }

    public void handleDeleteButton(ActionEvent event) {

        if (zimmerTab.isSelected()) {
            aktuellesZimmer = zimmerTableView.getSelectionModel().getSelectedItem();
            getZimmerUpdateManager().addDelete(aktuellesZimmer);
            zimmerOL.remove(aktuellesZimmer);
        } else if (vertragTab.isSelected()) {
            aktuellerVertrag = vertragTableView.getSelectionModel().getSelectedItem();
            getVertragUpdateManager().addDelete(aktuellerVertrag);
            vertraegeOL.remove(aktuellerVertrag);
            MietVertrag.berechneEndeDaten(vertraegeOL);
        }

        checkButtons();
    }

    public void handleAbbrechenButton(ActionEvent event) {

        if (vertragTab.isSelected()) {
            cancelVertragUpdate();
        } else if (zimmerTab.isSelected()) {
            cancelZimmerUpdate();
        }

        checkButtons();
        System.out.println("Abbruch Verarbeitung");
    }

    public void handleKopierButton(ActionEvent event) {
        Wohnung wohnungQuelle = holeQuellWohnung();
        if (wohnungQuelle == null) return;

        Collection<Zimmer> zimmerColl = HausJpaPersistence.getInstance().selectZimmer(wohnungQuelle);
        for (Zimmer zimmer : zimmerColl) {
            Zimmer newZimmer = new Zimmer();
            newZimmer.setName(zimmer.getName());
            newZimmer.setFlaeche(zimmer.getFlaeche());
            newZimmer.setWohnung(selektierteWohnung());
            getZimmerUpdateManager().addInsert(newZimmer);
            zimmerOL.add(newZimmer);
        }

        zimmerTableView.refresh();
        zimmerTableView.getSelectionModel().select(0);
        checkButtons();
    }

    public void handlePruefButton(ActionEvent event) {
        if (mietenTab.isSelected()) {
            String jahrStr = jahrChoiceBox.getSelectionModel().getSelectedItem();
            pruefeMietzahlungen(Integer.valueOf(jahrStr));
        } else {
            pruefeKautionen();
        }

        checkButtons();
        System.out.println("Pruefe Mieteingaenge");
    }

    public void handleDatenTab() {

        if (wohnungTreeView != null) initializeItemGraphics(wohnungTreeView.getRoot());

        if (pruefButton != null) {
            pruefButton.setText("Prüfe");
            pruefButton.setDisable(true);
        }

        try {
            TreeItem<WohnObjekt> treeItem = wohnungTreeView.getSelectionModel().getSelectedItem();
            fillDatenTab(treeItem);
        } catch (NullPointerException e) {
            LOGGER.info("Alles gut");
        }
    }

    public void handleMietenTab() {
        pruefeUngesicherteUpdates();
        initializeItemGraphics(wohnungTreeView.getRoot());
        pruefButton.setText("Prüfe Mieteingang");
        fillMietenTable();
        checkButtons();
    }

    public void handleKautionTab() {
        pruefeUngesicherteUpdates();
        initializeItemGraphics(wohnungTreeView.getRoot());
        pruefButton.setText("Prüfe Kautionen");
        fillMieterChoiceBox();
        fillKautionsTable();
        checkButtons();
    }

    public void pruefeUngesicherteUpdates() {
        pruefeVertragUpdate();
        pruefeZimmerUpdate();
    }

    public void handleVertragTab() {

        try {
            WohnObjekt wohnObjekt = wohnungTreeView.getSelectionModel().getSelectedItem().getValue();
            fillVertagsTable(wohnObjekt.getWohnung());
        } catch (Exception e) {
            vertragTableView.getItems().clear();
        }
    }

    public void handleZimmerTab() {

        // Tab gewechselt --> gibt es ungesicherte Verträge?
        pruefeUngesicherteUpdates();

        try {
            WohnObjekt wohnObjekt = wohnungTreeView.getSelectionModel().getSelectedItem().getValue();
            fillZimmerTable(wohnObjekt.getWohnung());
        } catch (Exception e) {
            zimmerTableView.getItems().clear();
        }
    }

    private void saveVertrag() {
        for (MietVertrag vertrag : vertragUpdateManager.updatesToBeChecked()) {
            String meldung = checkVertrag(vertrag);
            if (!meldung.isEmpty()) {
                if (!showAlert(meldung)) {
                    cancelVertragUpdate();
                }
            }
        }
        vertragUpdateManager.saveUpdates();
        Wohnung wohnung = wohnungTreeView.getSelectionModel().getSelectedItem().getValue().getWohnung();
        fillVertagsTable(wohnung);
    }

    private void saveZimmer() {
        for (Zimmer zimmer : zimmerUpdateManager.updatesToBeChecked()) {
            String meldung = checkZimmer(zimmer);
            if (!meldung.isEmpty()) {
                if (!showAlert(meldung)) {
                    cancelVertragUpdate();
                }
            }
        }
        zimmerUpdateManager.saveUpdates();
        Wohnung wohnung = wohnungTreeView.getSelectionModel().getSelectedItem().getValue().getWohnung();
        fillZimmerTable(wohnung);
    }

    private List<MietVertrag> ermittleVertraege() {
        Wohnung wohnung = selektierteWohnung();
        if (wohnung == null) return  new ArrayList<>(1);
        return ermittleVertraege(wohnung);
    }

    private List<MietVertrag> ermittleVertraege(Wohnung wohnung) {
        Collection<MietVertrag> vertraegeColl = HausJpaPersistence.getInstance().selectMietvertraege(wohnung);
        ArrayList<MietVertrag> vertraegeList = new ArrayList<>(vertraegeColl);
        MietVertrag.berechneEndeDaten(vertraegeList);
        return vertraegeList;
    }

    private Wohnung selektierteWohnung() {
        TreeItem<WohnObjekt> treeItem = wohnungTreeView.getSelectionModel().getSelectedItem();
        if (treeItem == null) return null;
        return treeItem.getValue().getWohnung();
    }

    private void checkButtons() {
        if (datenTab.isSelected()) checkDatenButtons();
        if (vertragTab.isSelected()) {
            checkDatenButtons();
            checkVertragButtons();
        }
        if (zimmerTab.isSelected()) {
            checkDatenButtons();
            checkZimmerButtons();
        }
        if (mietenTab.isSelected()) checkMietenButtons();
        if (kautionTab.isSelected()) checkKautionenButtons();
    }

    private void checkDatenButtons() {

        int index = wohnungTreeView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            neuButton.setDisable(true);
        } else {
            neuButton.setDisable(false);
        }
    }

    private void checkVertragButtons() {
        kopierButton.setDisable(true);

        if (vertragUpdateManager.isEmpty()) {
            saveButton.setDisable(true);
            abbruchButton.setDisable(true);
        } else {
            saveButton.setDisable(false);
            abbruchButton.setDefaultButton(false);
        }

        if (aktuellerVertrag == null) {
            loeschButton.setDisable(true);
        } else {
            loeschButton.setDisable(false);
        }
    }

    private void checkZimmerButtons() {

        if (zimmerUpdateManager.isEmpty()) {
            saveButton.setDisable(true);
            abbruchButton.setDisable(true);
        } else {
            saveButton.setDisable(false);
            abbruchButton.setDefaultButton(false);
        }

        if (aktuellesZimmer == null) {
            loeschButton.setDisable(true);
        } else {
            loeschButton.setDisable(false);
        }

        if (selektierteWohnung() == null) {
            kopierButton.setDisable(true);
        } else {
            kopierButton.setDisable(false);
        }
    }

    private void checkMietenButtons() {
        kopierButton.setDisable(true);
        pruefButton.setDisable(false);
        neuButton.setDisable(true);
        loeschButton.setDisable(true);
        abbruchButton.setDisable(true);
        saveButton.setDisable(true);
    }

    private void checkKautionenButtons() {
        kopierButton.setDisable(true);
        pruefButton.setDisable(false);
        neuButton.setDisable(true);
        loeschButton.setDisable(true);
        abbruchButton.setDisable(true);
        saveButton.setDisable(true);
    }

    private String checkVertrag(MietVertrag vertrag) {
        if (vertrag.getBeginn() == null) return "Vertragsbeginn fehlt.";
        if (vertrag.getEnde() != null && vertrag.getEnde().before(vertrag.getBeginn())) return "Vertragsende liegt vor -beginn.";
        if (vertrag.getMieter() == null) return "Mieter fehlt";
        if (vertrag.getWohnung() == null) return "Wohnung fehlt.";
        return "";
    }

    private String checkZimmer(Zimmer zimmer) {
        if (zimmer.getName() == null) return "Zimmerbezeichnung fehlt.";
        return "";
    }

    /**
     * Abgleich der Mietzahlungen des übergebenen Jahres mit den Sollwerten
     * @param jahr zu prüfendes Jahr
     */
    private void pruefeMietzahlungen(int jahr) {

        Map<Long, Double[]> wohnungsMap = erzeugeWohnungsMap();

        aggregiereSollZahlungen(jahr, wohnungsMap);
        aggregiereIstZahlungen(jahr, wohnungsMap);

        pruefeItem(wohnungTreeView.getRoot(), wohnungsMap);

    }

    private void pruefeVertragUpdate() {
        if (vertragUpdateManager.isEmpty()) return;
        boolean sichern = showAlert("Es gibt ungesicherte Verträge.");
        if (sichern) {
            vertragUpdateManager.saveUpdates();
        } else {
            cancelVertragUpdate();
        }
    }

    private void pruefeZimmerUpdate() {
        if (zimmerUpdateManager.isEmpty()) return;
        boolean sichern = showAlert("Es gibt ungesicherte Zimmer.");
        if (sichern) {
            zimmerUpdateManager.saveUpdates();
        } else {
            zimmerUpdateManager.clear();
        }
    }

    /**
     * Die Methode prüft für alle Wohnobjekte im TreeView, ob die Mietzahlungen des Jahres bis
     * zum aktuellen Monat eingegangen sind und kennzeichnet die IO-Wohnungen mit einer grünen,
     * die NIO-Wohnungen mit einer roten Ampel.
     * Die Methode wird rekursiv für alle Unter-Items aufgerufen.
     * @param treeItem das zu prüfende Item
     * @param wohnungsMap Map mit Soll- und Ist-Zahlungen für alle Wohnungen
     * @return true, wenn für das Item alle Prüfungen IO ergeben haben, sonst false
     */
    private boolean pruefeItem(TreeItem<WohnObjekt> treeItem, Map<Long, Double[]> wohnungsMap ) {
        Circle GRUENE_AMPEL = new Circle(6, Color.LIGHTGREEN);
        Circle ROTE_AMPEL = new Circle(6, Color.RED);
        boolean allesIO = true;

        if (treeItem.isLeaf()) {
            WohnObjekt wohnObjekt = treeItem.getValue();
            if (wohnObjekt.getWohnung() == null) return true;

            Long id = wohnObjekt.getWohnung().getId();
            Double[] werte = wohnungsMap.get(id);

            if (werte[0] > werte[1]) {
                treeItem.setGraphic(ROTE_AMPEL);
                allesIO = false;
                LOGGER.info("Miete Soll: " + werte[0] + " Ist: " + werte[1] + " --> " + wohnObjekt.getName());
            } else {
                treeItem.setGraphic(GRUENE_AMPEL);
            }
        } else {
            /* Kinder durchlaufen */
            boolean childrenIO = true;
            for (TreeItem<WohnObjekt> child : treeItem.getChildren()) {
                childrenIO = childrenIO & pruefeItem(child, wohnungsMap);
            }

            if (childrenIO) {
                treeItem.setGraphic(GRUENE_AMPEL);
            } else {
                treeItem.setGraphic(ROTE_AMPEL);
            }
        }

        return allesIO;
    }

    /**
     * Löscht die Grafik am TreeItem und an dessen Children
     * @param item
     */
    private void initializeItemGraphics(TreeItem<WohnObjekt> item) {

        item.setGraphic(new Circle(6, Color.LIGHTBLUE));

        for (TreeItem<WohnObjekt> child : item.getChildren()) {
            initializeItemGraphics(child);
        }
    }

    /**
     * Erzeugt eine Map aller Wohnungen zum Aggregieren der Soll- und Istzahlungen
     * @return Map aller Wohnungen
     */
    private Map<Long,Double[]> erzeugeWohnungsMap() {
        Collection<Wohnung> wohnungen = HausJpaPersistence.getInstance().selectAllWohnungen();
        Map<Long, Double[]> wohnungsMap = new HashMap<>(100);
        for (Wohnung wohnung : wohnungen) {
            wohnungsMap.put(wohnung.getId(), new Double[]{0.0d, 0.0d, 0.0d, 0.0d});

        }
        return wohnungsMap;
    }

    /**
     * In die Map aller Wohnungen werden die erwarteten Soll-Zahlungen eingetragen
     * @param jahr Jahr, für das die Sollzahlungen ermittelt werden sollen
     * @param wohnungsMap Map aller Wohnungen mit Soll- und Ist-Werten
     *
     */
    private void aggregiereSollZahlungen(int jahr, Map<Long, Double[]> wohnungsMap) {

        Calendar cal = new GregorianCalendar();
        int lfdMonat = cal.get(Calendar.MONTH);
        Collection<MietVertrag> mietVertraege = HausJpaPersistence.getInstance().selectMietvertraege(jahr, 0, lfdMonat);

        for (int iMon = 0; iMon <= lfdMonat; iMon++) {
            cal = new GregorianCalendar(jahr, iMon, 1);
            Date anfangMonat = cal.getTime();
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DATE, -1);
            Date endeMonat = cal.getTime();

            for (MietVertrag mietVertrag : mietVertraege) {
                if (mietVertrag.getBeginn().before(endeMonat) && (mietVertrag.getEnde() == null || mietVertrag.getEnde().after(anfangMonat))) {
                    Long wohnungsId = mietVertrag.getWohnung().getId();
                    Double[] werte = wohnungsMap.get(wohnungsId);
                    if (isHalberMonat(mietVertrag, anfangMonat, endeMonat)) {
                        werte[0] += mietVertrag.getGesamtkosten() / 2;
                    } else {
                        werte[0] += mietVertrag.getGesamtkosten();
                    }
                }
            }
        }
    }

    /**
     * Prüfung, ob der Mietvertrag Mitte des Monats beginnt oder endet
     * @param mietVertrag zu prüfender Mietvertrag
     * @param anfangMonat Date des Monatsbeginns
     * @param endeMonat Date des Monatsendes
     * @return true, wenn es so ist
     */
    private boolean isHalberMonat(MietVertrag mietVertrag, Date anfangMonat, Date endeMonat) {

        if (mietVertrag.getEnde() == null) {
            if (mietVertrag.getBeginn().after(anfangMonat)) {
                return true;
            } else {
                return false;
            }
        }

        if (mietVertrag.getBeginn().before(anfangMonat) && mietVertrag.getEnde().before(endeMonat))
            return true;

        if (mietVertrag.getBeginn().after(anfangMonat) && mietVertrag.getEnde().after(endeMonat))
            return true;

        return false;
    }

    /**
     * In die Map aller Wohnungen werden die verbuchten Ist-Zahlungen eingetragen
     * @param jahr Jahr, für das die Istzahlungen ermittelt werden sollen
     * @param wohnungsMap Map aller Wohnungen mit Soll- und Ist-Werten
     *
     */
    private void aggregiereIstZahlungen(int jahr, Map<Long, Double[]> wohnungsMap) {


        Collection<Mietzahlung> mietzahlungen = HausJpaPersistence.getInstance().selectMietzahlungen(jahr);

        for (Mietzahlung mietzahlung: mietzahlungen) {
            Wohnung wohnung = mietzahlung.getMietVertrag().getWohnung();
            Double[] werte = wohnungsMap.get(wohnung.getId());
            werte[1] += mietzahlung.getBetrag();
        }

    }

    /**
     * Abgleich der Kautionszahlungen mit den im Mietvertrag vereinbarten Werten
      */
    private void pruefeKautionen() {

        Map<Long, Double[]> wohnungsMap = erzeugeWohnungsMap();

        aggregiereSollKautionen(wohnungsMap);
        aggregiereIstKautionen(wohnungsMap);

        pruefeItem(wohnungTreeView.getRoot(), wohnungsMap);

    }

    /**
     * In die Map aller Wohnungen werden die erwarteten Soll-Kautionen eingetragen
     * @param wohnungsMap Map aller Wohnungen mit Soll- und Ist-Werten
     *
     */
    private void aggregiereSollKautionen(Map<Long, Double[]> wohnungsMap) {

        Collection<MietVertrag> mietVertraege = HausJpaPersistence.getInstance().selectAllMietvertraege();
        Date heute = new Date();

        for (MietVertrag mietVertrag : mietVertraege) {
            // Mietkaution einer Mietvertragsanpassung nicht berücksichtigen
            if (mietVertrag.isAnpassung() != true) {
                Date vertragEnde = mietVertrag.getEnde();
                Long wohnungsId = mietVertrag.getWohnung().getId();
                Double[] werte = wohnungsMap.get(wohnungsId);

                if (vertragEnde == null || vertragEnde.after(heute)) {
                    // aktueller Mietvertrag
                    werte[0] += mietVertrag.getKaution();
                } else {
                    // alter Mietvertrag
                    werte[2] += mietVertrag.getKaution();
                }
            }
        }

    }

    /**
     * In die Map aller Wohnungen werden die verbuchten Ist-Kautions-Zahlungen eingetragen
     * @param wohnungsMap Map aller Wohnungen mit Soll- und Ist-Werten
     *
     */
    private void aggregiereIstKautionen(Map<Long, Double[]> wohnungsMap) {

        Collection<Mietzahlung> mietzahlungen = HausJpaPersistence.getInstance().selectKautionszahlungen();
        Date heute = new Date();

        for (Mietzahlung mietzahlung: mietzahlungen) {
            Wohnung wohnung = mietzahlung.getMietVertrag().getWohnung();
            Date vertragEnde = mietzahlung.getMietVertrag().getEnde();
            Double[] werte = wohnungsMap.get(wohnung.getId());
            if (vertragEnde == null || vertragEnde.after(heute)) {
                // aktueller Mietvertrag
                werte[1] += mietzahlung.getBetrag();
            } else {
                // alter Mietvertrag
                werte[3] += mietzahlung.getBetrag();
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
        alert.setTitle("Frage");
        alert.setHeaderText("Ungesicherte Daten!");
        alert.setContentText(message);

        ButtonType buttonTypeOK = new ButtonType("Sichern");
        ButtonType buttonTypeReload = new ButtonType("Verwerfen");
        alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeReload);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() != buttonTypeReload;
    }

    /**
     * Dialog zur Auswahl einer Person aus der Collection
     * @param personen Collection der Auswahl an Personen
     * @return die gewählte Person
     */
    private Person waehlePerson(Collection<Person> personen) {

        ChoiceDialog<Person> choiceDialog = new ChoiceDialog<>(null, personen);
        choiceDialog.setTitle("Wähle Person");
        choiceDialog.setHeaderText("Es gibt mehrere Personen zum Auswahlkriterium. Bitte wählen Sie eine Person aus.");
        choiceDialog.setContentText("Person");

        choiceDialog.showAndWait();

        return choiceDialog.getSelectedItem();
    }

    /**
     * Fragt den Benutzer nach der Quelle zum Kopieren der Zimmer
     * @return Wohnung, deren Zimmer kopiert werden sollen
     */
    private Wohnung holeQuellWohnung() {
        wohnungOL.clear();
        wohnungOL.addAll(HausJpaPersistence.getInstance().selectAllWohnungen());

        ChoiceDialog<Wohnung> dialog = new ChoiceDialog<>(null, wohnungOL);
        dialog.setTitle("Quelle wählen");
        dialog.setHeaderText("Von welcher Wohnung sollen die Zimmer kopiert werden?");
        dialog.setContentText("Wohnung: ");


        Optional<Wohnung> result = dialog.showAndWait();
        if (result.isPresent()) return result.get();
        return null;
    }

    /**
     * Update abbrechen, Wohnungsattribute neu übertragen
     */
    private void cancelVertragUpdate() {
        aktuellerVertrag = null;
        vertragUpdateManager.clear();
        fillVertagsTable(selektierteWohnung());
    }

    /**
     * Update abbrechen, Wohnungsattribute neu übertragen
     */
    private void cancelZimmerUpdate() {
        aktuellesZimmer = null;
        zimmerUpdateManager.clear();
        fillZimmerTable(selektierteWohnung());
    }

    private void fillDatenTab(TreeItem<WohnObjekt> treeItem) {

        Haus haus;
        Wohnung wohnung;

        if (treeItem == null) {
            haus = null;
            wohnung = null;
        } else {
            haus = treeItem.getValue().getHaus();
            wohnung = treeItem.getValue().getWohnung();
        }

        fillHaus(haus);
        fillWohnung(wohnung);

        if (vertragTab.isSelected()) fillVertagsTable(wohnung);
        if (zimmerTab.isSelected()) fillZimmerTable(wohnung);

        checkDatenButtons();
    }

    private void fillMietenTable() {
        LOGGER.info("fill MietenTable");
        mietZahlungOL.clear();
        ArrayList<GuiMietzahlung> zahlungenList = new ArrayList<>();
        int jahr = Integer.valueOf(jahrChoiceBox.getSelectionModel().getSelectedItem());
        Double summeSoll = fillSollMieten(jahr, zahlungenList);
        Double summeIst = fillIstMieten(jahr, zahlungenList);

        zahlungenList.sort(Comparator.comparing(GuiMietzahlung::getDatum));

        mietZahlungOL.addAll(zahlungenList);
        mietenTableView.setItems(mietZahlungOL);

        summeIstTextField.setText(ExtendedDoubleStringConverter.decimalFormat.format(summeIst));
        summeSollTextField.setText(ExtendedDoubleStringConverter.decimalFormat.format(summeSoll));

        if (summeSoll > summeIst) {
            summeIstTextField.setBackground(BG_RED);
        } else if (summeSoll < summeIst) {
            summeIstTextField.setBackground(BG_BLUE);
        } else {
            summeIstTextField.setBackground(BG_GREEN);
        }
    }

    private Double fillSollMieten(int jahr, List zahlungenList) {
        Calendar monatsAnfang = new GregorianCalendar();
        monatsAnfang.set(jahr, 0,1);
        double summe = 0.0d;

        Calendar endeCal = new GregorianCalendar();

        while (!monatsAnfang.after(endeCal)) {
            GuiMietzahlung sollZahlung = new GuiMietzahlung();
            Calendar monatsMitte = (Calendar) monatsAnfang.clone();
            monatsMitte.add(Calendar.DATE, 15);
            sollZahlung.setDatum(monatsAnfang.getTime());
            MietVertrag vertragAnfang = searchVertrag(monatsAnfang.getTime());
            MietVertrag vertragMitte = searchVertrag(monatsMitte.getTime());
            MietVertrag vertrag;
            double faktor;

            if (vertragAnfang == vertragMitte) {
                vertrag = vertragAnfang;
                faktor = 1.0d;
            } else {
                faktor = 0.5d;
                if(vertragAnfang == null || vertragAnfang.getGesamtkosten() == 0.0d) {
                    vertrag = vertragMitte;
                } else {
                    vertrag = vertragAnfang;
                }
            }

            if (vertrag != null) {
                sollZahlung.setMieter(vertrag.getMieter());
                sollZahlung.setSoll(vertrag.getGesamtkosten() * faktor);
                sollZahlung.setGrundMiete(vertrag.getMietzins() * faktor);
                sollZahlung.setNebenKosten((vertrag.getHeizkosten() + vertrag.getNebenkosten()) * faktor);
                sollZahlung.setMietVertrag(vertrag);
                summe += vertrag.getGesamtkosten() * faktor;
            }
            zahlungenList.add(sollZahlung);
            monatsAnfang.add(Calendar.MONTH, 1);
        }

        return summe;
    }


    private Double fillIstMieten(int jahr, List<GuiMietzahlung> zahlungenList) {

        List<GuiMietzahlung> istZahlungen = new ArrayList<>(50);
        MietVertrag mietVertragAlt = null;
        double summe = 0.0d;

        for (GuiMietzahlung zahlung : zahlungenList) {
            MietVertrag mietVertragNeu = zahlung.getMietVertrag();
            if (mietVertragNeu != null && (mietVertragAlt == null || mietVertragAlt != mietVertragNeu)) {
                Collection<Mietzahlung> mietzahlungen = ermittleMietzahlungen(jahr, mietVertragNeu);
                for (Mietzahlung mietzahlung : mietzahlungen) {
                    Buchung buchung = mietzahlung.getBuchung();
                    GuiMietzahlung istZahlung = new GuiMietzahlung();
                    istZahlung.setMieter(mietVertragNeu.getMieter());
                    istZahlung.setDatum(buchung.getDatum());
                    istZahlung.setIst(mietzahlung.getBetrag());
                    double faktor = 0.0d;
                    Date mvEnde = mietVertragNeu.getEnde();
                    if (mvEnde == null || istZahlung.getDatum().before(mvEnde)) {
                        faktor = buchung.getBetrag() / mietVertragNeu.getGesamtkosten();
                    }
                    if (faktor >= 0.75d) {
                        istZahlung.setGrundMiete(mietVertragNeu.getMietzins());
                        istZahlung.setNebenKosten(mietzahlung.getBetrag() - mietVertragNeu.getMietzins());
                    } else if (faktor == 0.5d) {
                        istZahlung.setGrundMiete(mietVertragNeu.getMietzins() * faktor);
                        istZahlung.setNebenKosten(((mietVertragNeu.getHeizkosten() + mietVertragNeu.getNebenkosten()) * faktor));
                    } else {
                        istZahlung.setGrundMiete(0.0d); 
                        istZahlung.setNebenKosten(0.0d);
                    }
                    istZahlungen.add(istZahlung);
                    summe += mietzahlung.getBetrag();
                }
                mietVertragAlt = mietVertragNeu;
            }
        }

        zahlungenList.addAll(istZahlungen);
        return summe;
    }


    private void fillKautionsTable() {
        LOGGER.info("fill KautionTable");
        kautionsZahlungOL.clear();
        ArrayList<GuiMietzahlung> zahlungenList = new ArrayList<>();
        String mieterName = mieterChoiceBox.getSelectionModel().getSelectedItem();
        List<MietVertrag> vertraege = ermittleVertraege();
        Double summeSoll = fillSollKaution(vertraege, mieterName, zahlungenList);
        Double summeIst = fillIstKaution(vertraege, mieterName, zahlungenList);

        zahlungenList.sort(Comparator.comparing(GuiMietzahlung::getDatum));

        kautionsZahlungOL.addAll(zahlungenList);
        kautionTableView.setItems(kautionsZahlungOL);

        kautionSummeIstTextField.setText(ExtendedDoubleStringConverter.decimalFormat.format(summeIst));
        kautionSummeSollTextField.setText(ExtendedDoubleStringConverter.decimalFormat.format(summeSoll));

        if (summeSoll > summeIst) {
            kautionSummeIstTextField.setBackground(BG_RED);
        } else if (summeSoll < summeIst) {
            kautionSummeIstTextField.setBackground(BG_BLUE);
        } else {
            kautionSummeIstTextField.setBackground(BG_GREEN);
        }

    }

    private Double fillSollKaution(List<MietVertrag> vertraege, String mieterName, List<GuiMietzahlung> zahlungenList) {
        double summe = 0.0d;
        Date heute = new Date();

        for (MietVertrag vertrag: vertraege) {
            if (vertrag.isAnpassung() == false) {
                if (mieterName.equals(ALLE_MIETER) || vertrag.getMieter().getName().equals(mieterName)) {
                    GuiMietzahlung sollZahlung = new GuiMietzahlung();
                    sollZahlung.setDatum(vertrag.getBeginn());
                    sollZahlung.setMieter(vertrag.getMieter());
                    sollZahlung.setSoll(vertrag.getKaution());
                    summe += vertrag.getKaution();
                    zahlungenList.add(sollZahlung);
                    if (vertrag.getEnde() != null && vertrag.getEnde().before(heute)) {
                        GuiMietzahlung sollRueckzahlung = new GuiMietzahlung();
                        sollRueckzahlung.setDatum(vertrag.getEnde());
                        sollRueckzahlung.setMieter(vertrag.getMieter());
                        sollRueckzahlung.setSoll(vertrag.getKaution() * -1);
                        summe -= vertrag.getKaution();
                        if (sollRueckzahlung.getSoll() != 0) {
                            zahlungenList.add(sollRueckzahlung);
                        }
                    }
                }
            }
        }

        return summe;
    }


    private Double fillIstKaution(List<MietVertrag> vertraege, String mieterName, List<GuiMietzahlung> zahlungenList) {

        List<GuiMietzahlung> istZahlungen = new ArrayList<>(50);
        double summe = 0.0d;

        for (MietVertrag vertrag : vertraege) {
            Collection<Mietzahlung> kautionsZahlungen = HausJpaPersistence.getInstance().selectKautionszahlungen(vertrag);
            for (Mietzahlung mietzahlung : kautionsZahlungen) {
                if (mieterName.equals(ALLE_MIETER) || mietzahlung.getMietVertrag().getMieter().getName().equals(mieterName)) {
                    Buchung buchung = mietzahlung.getBuchung();
                    GuiMietzahlung istZahlung = new GuiMietzahlung();
                    istZahlung.setMieter(vertrag.getMieter());
                    istZahlung.setDatum(buchung.getDatum());
                    istZahlung.setIst(mietzahlung.getBetrag());
                    istZahlungen.add(istZahlung);
                    summe += mietzahlung.getBetrag();
                }
            }
        }

        zahlungenList.addAll(istZahlungen);
        return summe;
    }

    private Collection<Mietzahlung> ermittleMietzahlungen(int jahr, MietVertrag mietvertrag) {
        Collection<Mietzahlung> mietzahlungen;
        String datumStr = String.valueOf(jahr - 1) + "-12-15";
        mietzahlungen = HausJpaPersistence.getInstance().selectMietzahlungen(mietvertrag, datumStr);

        return mietzahlungen;
    }

    private MietVertrag searchVertrag(Date datum) {
        Wohnung wohnung = selektierteWohnung();
        if (wohnung == null) return null;

        for (MietVertrag vertrag: ermittleVertraege(wohnung)) {
          if (vertrag.isGueltigAm(datum))
              return vertrag;
        }
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        initializeWohnungsTree();
        initializeDatenTab();
        initializeMietenTab();
        initializeKautionsTab();
        summeSollTextField.setAlignment(Pos.CENTER_RIGHT);
        summeIstTextField.setAlignment(Pos.CENTER_RIGHT);
        wohnungTreeView.getSelectionModel().selectFirst();
        checkButtons();
    }

    private void initializeDatenTab() {

        /* Mietverträge */
        vertragUpdateManager = new UpdateManager<>(50);
        vertraegeOL = FXCollections.observableArrayList();
        initializeMietvertragTableViewColumns();
        vertragTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleVertragSelected(newValue));

        /* Zimmer */
        zimmerUpdateManager = new UpdateManager<>(50);
        zimmerOL = FXCollections.observableArrayList();
        initializeZimmerTableViewColumns();
        zimmerTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleZimmerSelected(newValue));
        wohnungOL = FXCollections.observableArrayList();

    }

    private void initializeWohnungsTree() {
        TreeItem<WohnObjekt> root = new TreeItem<>(new WohnObjekt("root"));
        Collection<Haus> haeuser = HausJpaPersistence.getInstance().selectAllHaeuser();

        for (Haus haus: haeuser) {
            WohnObjekt hausObjekt = new WohnObjekt(haus.getName());
            hausObjekt.setHaus(haus);
            TreeItem<WohnObjekt> hausItem = new TreeItem<>(hausObjekt);
            root.getChildren().add(hausItem);

            Collection<Wohnung> wohnungen = HausJpaPersistence.getInstance().selectWohnungen(haus);
            for (Wohnung wohnung: wohnungen) {
                String bezeichnung = wohnung.getNummer() + " [" + wohnung.getLageBeschreibung() + "]";
                WohnObjekt wohnObjekt = new WohnObjekt(bezeichnung);
                wohnObjekt.setHaus(haus);
                wohnObjekt.setWohnung(wohnung);
                TreeItem<WohnObjekt> wohnItem = new TreeItem<>(wohnObjekt);
                hausItem.getChildren().add(wohnItem);
            }

            initializeItemGraphics(root);
        }

        wohnungTreeView.setRoot(root);
        wohnungTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleWohnObjektSelected(newValue);
        });
    }

    private void initializeMietvertragTableViewColumns() {

        vertraegeOL.addListener((ListChangeListener<MietVertrag>) c -> {
            while (c.next()) {
                if (c.wasUpdated()) {
                    System.out.println("here we are");
                }
            }
        });

        beginnCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        beginnCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    MietVertrag mietvertrag = (MietVertrag) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (mietvertrag.getBeginn() == null) {
                        property.setValue("JJJJ-MM-TT");
                    } else {
                        property.setValue(DatumHelper.getDatumInternational(mietvertrag.getBeginn()));
                    }
                    return property;
                });

        endeCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    MietVertrag mietvertrag = (MietVertrag) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (mietvertrag.getEnde() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(DatumHelper.getDatumInternational(mietvertrag.getEnde()));
                    }
                    return property;
                });

        // nkCol.setCellFactory(CheckBoxTableCell.forTableColumn(nkCol));
        anpassungCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer param) {
                System.out.println("hier bin ich");
                SimpleBooleanProperty property = new SimpleBooleanProperty();
                property.setValue(vertraegeOL.get(param).isAnpassung());
                return property;
            }
        }));

        anpassungCol.setCellValueFactory(
                cellData -> {
                    SimpleBooleanProperty property = new SimpleBooleanProperty();
                    property.setValue(cellData.getValue().isAnpassung());
                    return property;
        });

        mieterCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        mieterCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    MietVertrag mietvertrag = (MietVertrag) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (mietvertrag.getMieter() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(mietvertrag.getMieter().getName() + ", " + mietvertrag.getMieter().getVorname());
                    }
                    return property;
                });


        regExpCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        regExpCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    MietVertrag mietvertrag = (MietVertrag) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (mietvertrag.getRegularExpression() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(mietvertrag.getRegularExpression());
                    }
                    return property;
                });

        kautionCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        kautionCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        kautionCol.setCellValueFactory(
                new PropertyValueFactory<>("kaution")
        );

        mietzinsCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        mietzinsCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        mietzinsCol.setCellValueFactory(
                new PropertyValueFactory<>("mietzins")
        );

        nebenkostenCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        nebenkostenCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        nebenkostenCol.setCellValueFactory(
                new PropertyValueFactory<>("nebenkosten")
        );

        heizkostenCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        heizkostenCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        heizkostenCol.setCellValueFactory(
                new PropertyValueFactory<>("heizkosten")
        );

        gesamtkostenCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        gesamtkostenCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    MietVertrag mietvertrag = (MietVertrag) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(MietVertrag.getBetragFormatiert(mietvertrag.getGesamtkosten()));
                    return property;
                });

    }

    private void initializeZimmerTableViewColumns() {

        nameCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        nameCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zimmer zimmer = (Zimmer) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zimmer.getName() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zimmer.getName());
                    }
                    return property;
                });

        flaecheCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        flaecheCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        flaecheCol.setCellValueFactory(
                new PropertyValueFactory<>("flaeche")
        );

        renovierungCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        renovierungCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );
        renovierungCol.setCellValueFactory(
                new PropertyValueFactory<>("renovierung")
        );

        bemerkungCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        bemerkungCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zimmer zimmer = (Zimmer) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zimmer.getBemerkung() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zimmer.getBemerkung());
                    }
                    return property;
                });
    }



    private void initializeMietenTab() {
        mietZahlungOL = FXCollections.observableArrayList();
        initializeMietenTableView();
        initializeMietenTableViewColumns();
        initializeJahrChoiceBox();
        checkButtons();
    }

    private void initializeMietenTableView() {
        mietenTableView.setRowFactory(row -> new TableRow<GuiMietzahlung>(){
            @Override
            public void updateItem(GuiMietzahlung item, boolean empty){
                super.updateItem(item, empty);

                if (item == null || empty || item.getGrundMiete() == null) {
                    setStyle("");
                } else {
                    if(item.getGrundMiete() <= 0) {
                        setStyle("-fx-background-color:" + RGB_YELLOW);
                    } else if (item.getIst() != null) {
                        setStyle("-fx-background-color:" + RGB_GREEN);
                    } else {
                        setStyle("-fx-background-color:" + RGB_RED);
                    }
                }
            }
        });
    }

    private void initializeKautionsTab() {
        kautionsZahlungOL = FXCollections.observableArrayList();
        initializeKautionTableViewColumns();
        initializeMieterChoiceBox();
        checkButtons();
    }


    private void initializeJahrChoiceBox() {
        jahreOL = FXCollections.observableArrayList();

    /* Fuellen der ChoiceBox */
        LocalDate date = LocalDate.now();
        int jahr = date.getYear();

        for (int i = jahr - 5; i <= jahr; i++) {
            jahreOL.add(String.valueOf(i));
        }

        jahrChoiceBox.setItems(jahreOL);
        jahrChoiceBox.getSelectionModel().select(5);
        jahrChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fillMietenTable());

    }

    private void initializeMieterChoiceBox() {
        mieterNamenOL = FXCollections.observableArrayList();
        mieterChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                 (observable, oldValue, newValue) -> fillKautionsTable());
        fillMieterChoiceBox();
    }

    private void fillMieterChoiceBox() {
        mieterChoiceBox.getItems().clear();
        mieterNamenOL.clear();

        Set<String> namenSet = new HashSet<>(10);

        /* Fuellen der ChoiceBox */
        for (MietVertrag mietVertrag : vertraegeOL) {
            String mieterName = mietVertrag.getMieter().getName();
            if (!mieterName.equals("Leerstand")) {
                namenSet.add(mieterName);
            }
        }

        mieterChoiceBox.getItems().add(ALLE_MIETER);
        List namenList = new ArrayList(namenSet);
        namenList.sort(Comparator.comparing(String::toString));
        mieterChoiceBox.getItems().addAll(namenList);

        mieterChoiceBox.getSelectionModel().select(0);

    }

    /**
     * Tabelle Mieten initialisieren
     */
    private void initializeMietenTableViewColumns() {

        /* Mietzahlungen Spalte Datum */
        mietenDatumCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        mietenDatumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    GuiMietzahlung mietzahlung = (GuiMietzahlung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(DatumHelper.getDatumInternational(mietzahlung.getDatum()));
                    return property;
                });

        /* Mietzahlungen Spalte Soll */
        mietenSollCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        mietenSollCol.setCellFactory(column -> {
            return new TableCell<GuiMietzahlung, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    TableRow<GuiMietzahlung> currentRow = getTableRow();
                    if (!isEmpty() && getItem() != null && getItem() > 0.0d) {
                            setText(BETRAG_FORMATTER.format(getItem()));
                            // currentRow.setStyle("-fx-background-color:" + RGB_RED);
                    } else {
                        setText("");
                    }
                    setGraphic(null);
                }
            };
        });
        mietenSollCol.setCellValueFactory(
                new PropertyValueFactory<>("soll")
        );

        /* Mietzahlungen Spalte Mieter */
        mietenMieterCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        mietenMieterCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    GuiMietzahlung zahlung = (GuiMietzahlung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zahlung.getMieter() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zahlung.getMieter().getName() + ", " + zahlung.getMieter().getVorname());
                    }
                    return property;
                });

        /* Mietzahlungen Spalte Ist */
        mietenIstCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        mietenIstCol.setCellFactory(column -> {
            return new TableCell<GuiMietzahlung, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    TableRow<GuiMietzahlung> currentRow = getTableRow();
                    if (!isEmpty() && getItem() != null) {
                        setText(BETRAG_FORMATTER.format(getItem()));
 //                       currentRow.setStyle("-fx-background-color:" + RGB_GREEN);
                    } else {
                        setText("");
                    }
                    setGraphic(null);
                }
            };
        });
        mietenIstCol.setCellValueFactory(
                new PropertyValueFactory<>("ist")
        );

        /* Mietzahlungen Spalte Grundmiete */
        mietenGrundmieteCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        mietenGrundmieteCol.setCellFactory(column -> {
            return new TableCell<GuiMietzahlung, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty() && getItem() != null && getItem() > 0.0d) {
                        setText(BETRAG_FORMATTER.format(getItem()));
                    } else {
                        setText("");
                    }
                    setGraphic(null);
                }
            };
        });
        mietenGrundmieteCol.setCellValueFactory(
                new PropertyValueFactory<>("grundMiete")
        );

        /* Mietzahlungen Spalte Nebenkosten */
        mietenNebenkostenCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        mietenNebenkostenCol.setCellFactory(column -> {
            return new TableCell<GuiMietzahlung, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    TableRow<GuiMietzahlung> currentRow = getTableRow();
                    if (isEmpty() || getItem() == null || !(getItem() > 0.0d) ) {
                        setText("");
                        // currentRow.setStyle("-fx-background-color:" + RGB_GREY);
                    } else {
                        setText(BETRAG_FORMATTER.format(getItem()));
                    }
                    setGraphic(null);
                }
            };
        });
        mietenNebenkostenCol.setCellValueFactory(
                new PropertyValueFactory<>("nebenKosten")
        );

    }

    /**
     * Tabelle Kautionszahlungen initialisieren
     */
    private void initializeKautionTableViewColumns() {

        /* Mietzahlungen Spalte Datum */
        kautionDatumCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        kautionDatumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    GuiMietzahlung mietzahlung = (GuiMietzahlung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(DatumHelper.getDatumInternational(mietzahlung.getDatum()));
                    return property;
                });


        /* Kautionen Spalte Mieter */
        kautionMieterCol.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );
        kautionMieterCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    GuiMietzahlung zahlung = (GuiMietzahlung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zahlung.getMieter() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zahlung.getMieter().getName() + ", " + zahlung.getMieter().getVorname());
                    }
                    return property;
                });

        /* Kautionen Spalte Soll */
        kautionSollCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        kautionSollCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        kautionSollCol.setCellValueFactory(
                new PropertyValueFactory<>("soll")
        );


        /* Mietzahlungen Spalte Ist */
        kautionIstCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        kautionIstCol.setCellFactory(
                TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter())
        );
        kautionIstCol.setCellValueFactory(
                new PropertyValueFactory<>("ist")
        );
    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) loeschButton.getScene().getWindow();
        pruefeVertragUpdate();
        pruefeZimmerUpdate();
        stage.close();
    }


    public class WohnObjekt{
        private Haus haus;
        private Wohnung wohnung;
        private String name;

        WohnObjekt(String name) {
            this.name = name;
        }

        public String toString() {
            return this.getName();
        }

        Haus getHaus() {
            return haus;
        }

        public void setHaus(Haus haus) {
            this.haus = haus;
        }

        Wohnung getWohnung() {
            return wohnung;
        }

        void setWohnung(Wohnung wohnung) {
            this.wohnung = wohnung;
        }

        String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class GuiMietzahlung {
        private Date datum;
        private Double soll;
        private Person mieter;
        private Double ist;
        private Double grundMiete;
        private Double nebenKosten;
        private MietVertrag mietVertrag;
        private boolean abgleich;

        GuiMietzahlung() {
            this.datum = null;
            this.soll = null;
            this.mieter = null;
            this.ist = null;

            this.nebenKosten = null;
            this.mietVertrag = null;
            this.abgleich = false;
        }

        public Double getGrundMiete() { return grundMiete; }

        public void setGrundMiete(Double grundMiete) { this.grundMiete = grundMiete; }

        MietVertrag getMietVertrag() {
            return mietVertrag;
        }

        void setMietVertrag(MietVertrag mietVertrag) {
            this.mietVertrag = mietVertrag;
        }

        public Date getDatum() {
            return datum;
        }

        public void setDatum(Date datum) {
            this.datum = datum;
        }

        public Double getSoll() {
            return soll;
        }

        void setSoll(Double soll) {
            this.soll = soll;
        }

        Person getMieter() {
            return mieter;
        }

        void setMieter(Person mieter) {
            this.mieter = mieter;
        }

        public Double getIst() {
            return ist;
        }

        void setIst(Double ist) {
            this.ist = ist;
        }

        public Double getNebenKosten() {
            return nebenKosten;
        }

        void setNebenKosten(Double nebenKosten) {
            this.nebenKosten = nebenKosten;
        }

        public boolean isAbgleich() {
            return abgleich;
        }

        public void setAbgleich(boolean abgleich) {
            this.abgleich = abgleich;
        }

    }
}
