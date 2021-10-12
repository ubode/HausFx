package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.*;
import de.ubo.fx.fahrten.converter.ExtendedDoubleStringConverter;
import de.ubo.fx.fahrten.converter.WohnungStringConverter;
import de.ubo.fx.fahrten.converter.ZahlungsKategorieStringConverter;
import de.ubo.fx.fahrten.helper.*;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import de.ubo.haus.business.Mietvertrag;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.reverseOrder;

/**
 * Created by ulric on 01.08.2016.
 */
public class MietenBuchungController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(MietenBuchungController.class.getName());
    private final UpdateManager<Mietzahlung> updateManager = new UpdateManager<>(500);
    public TableView<Zuordnung> zuordnungTableView;
    public TableColumn buchungCol;
    public TableColumn<Zuordnung, String> datumCol;
    public TableColumn<Zuordnung, String> empfaengerCol;
    public TableColumn<Zuordnung, Double> betragCol;
    public TableColumn<Zuordnung, String> kategorieCol;
    public TableColumn<Zuordnung, Wohnung> wohnungCol;
    public TableColumn<Zuordnung, String> mieterCol;
    public TableColumn<Zuordnung, Double> anteilCol;
    public TableColumn<Zuordnung, ZahlungsKategorie> verwendungCol;
    public TableColumn<Zuordnung, Integer> jahrCol;
    public Button searchButton;
    public Button saveButton;
    public ChoiceBox<String> jahrChoiceBox;
    public ChoiceBox<Monat> monatChoiceBox;
    public ChoiceBox<BuchungsKategorie> kategorieChoiceBox;
    private ObservableList<String> jahrOL;
    private ObservableList<Integer> jahrIntOL;
    private ObservableList<Wohnung> wohnungOL;
    private ObservableList<ZahlungsKategorie> zahlungsKategorieOL;
    private ObservableList<Zuordnung> zuordnungOL;

    private UpdateManager<Mietzahlung> getUpdateManager() {
        return updateManager;
    }

    private void fillZuordnungTable() {

        Collection<Buchung> buchungsColl = holeBuchungen();
        ArrayList<Buchung> buchungsList = new ArrayList<>(buchungsColl);
        Collections.sort(buchungsList);

        zuordnungOL.clear();
        for (Buchung buchung : buchungsList) {
            Collection<Mietzahlung> mietzahlungen = holeMietzahlungen(buchung);
            if (mietzahlungen.isEmpty()) {
                Zuordnung zuordnung = new Zuordnung(buchung);
                zuordnungOL.add(zuordnung);
            } else {
                for (Mietzahlung mietzahlung : mietzahlungen) {
                    Zuordnung zuordnung = new Zuordnung(buchung);
                    zuordnung.setMietzahlung(mietzahlung);
                    zuordnung.setMietVertrag(mietzahlung.getMietVertrag());
                    zuordnung.setZahlungsKategorie(mietzahlung.getZahlungsKategorie());
                    zuordnung.setJahr(mietzahlung.getJahr());
                    zuordnung.setAnteil(mietzahlung.getBetrag());
                    zuordnungOL.add(zuordnung);
                }
            }

        }

        zuordnungTableView.setItems(zuordnungOL);

    }

    private Collection<Buchung> holeBuchungen() {
        String jahr = jahrChoiceBox.getSelectionModel().getSelectedItem();
        int monatInd = monatChoiceBox.getSelectionModel().getSelectedItem().getIndex();
        String kategorie = kategorieChoiceBox.getSelectionModel().getSelectedItem().getsuchBegriff();

        Calendar cal = new GregorianCalendar();
        cal.set(Integer.valueOf(jahr), monatInd - 1, 1, 0, 0,0);
        cal.add(Calendar.DATE, -1);
        String vonDate = DatumHelper.getDatumInternational(cal.getTime());
        // cal.add(Calendar.DATE, +1);
        cal.set(Integer.valueOf(jahr), monatInd, 1, 23, 59,59);
        cal.add(Calendar.DATE, -1);
        String bisDate = DatumHelper.getDatumInternational(cal.getTime());

        Bedingung bed1 = new Bedingung(Kriterium.DATUM, Operator.GROESSER, vonDate);
        Bedingung bed2 = new Bedingung(Kriterium.DATUM, Operator.KLEINER_GLEICH, bisDate);
        Bedingung bed3;
        if(kategorie.contains("*")) {
            bed3 = new Bedingung(Kriterium.KATEGORIE, Operator.REGEX, kategorie);
        } else {
            bed3 = new Bedingung(Kriterium.KATEGORIE, Operator.GLEICH, kategorie);
        }
        Collection<Bedingung> bedingungen = Arrays.asList(bed1, bed2, bed3);

        return HausJpaPersistence.getInstance().selectBuchungen(bedingungen);
    }

    private Collection<Mietzahlung> holeMietzahlungen(Buchung buchung) {

        Collection<Mietzahlung> mietzahlungen = HausJpaPersistence.getInstance().selectMietzahlung(buchung);
        return mietzahlungen;
    }

    public void handleSearchAction(ActionEvent event) {
        LOGGER.info("Verträge Suchen ausgelöst");

        ordneMietvertraegeZu();

        zuordnungTableView.refresh();
        checkButtons();
    }

    /**
     * Durchlauf der Verträge: suche nach Buchungen, die auf den Vertrag zutreffen
     */
    private void ordneMietvertraegeZu() {
        String jahr = jahrChoiceBox.getSelectionModel().getSelectedItem();
        int jahrInd = Integer.valueOf(jahr);
        int monatInd = monatChoiceBox.getSelectionModel().getSelectedItem().getIndex();

        Collection<MietVertrag> vertraege = HausJpaPersistence.getInstance().selectMietvertraege(jahrInd, monatInd - 1);

        Collection<Zuordnung> result;
        for (MietVertrag vertrag : vertraege) {
            if (vertrag.getRegularExpression() == null) {
                String name = vertrag.getMieter().getName();
                String vorname = vertrag.getMieter().getVorname();
                result = sucheBuchung(name, vorname);
            } else {
                result = sucheBuchung(vertrag.getRegularExpression());
            }

            for (Zuordnung zuordnung : result) {
                // schon als Mietzahlung verbucht ==> keine Aktion
                if (zuordnung.getMietzahlung() == null) {
                    zuordnung.setMietVertrag(vertrag);
                    if (result.size() == 1 || vertrag.getGesamtkosten() == zuordnung.getBuchung().getBetrag()) {
                        zuordnung.setZahlungsKategorie(ZahlungsKategorie.MIETZINS);
                        zuordnung.setJahr(jahrInd);
                    }
                    registriereDbUpdate(zuordnung);
                }
            }
        }
    }

    /**
     * Die selektierte Buchung soll gesplittet werden
     */
    private void splitteBuchung() {
        Zuordnung zuordnung = zuordnungTableView.getSelectionModel().getSelectedItem();
        Zuordnung zuordnungNeu = zuordnung.clone();
        zuordnungOL.add(zuordnungNeu);
    }

    public void handleSaveAction(ActionEvent event) {
       getUpdateManager().saveUpdates();
       fillZuordnungTable();
       zuordnungTableView.refresh();
    }

    public void handleWohnungChangedAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Wohnung geändert");

        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        Wohnung wohnung = (Wohnung) event.getNewValue();
        Collection<MietVertrag> vertraege = HausJpaPersistence.getInstance().selectMietvertraege(wohnung);

        if (vertraege.size() == 1)
            zuordnung.setMietVertrag(vertraege.iterator().next());
        else {
            MietVertrag mietvertrag = waehleVertrag(vertraege);
            zuordnung.setMietVertrag(mietvertrag);
        }
        /*
        for (MietVertrag vertrag: vertraege) {
            if (vertrag.getEnde() == null) {
                zuordnung.setMietVertrag(vertrag);
            }
        }
        */

        Mietzahlung mietzahlung;
        if (zuordnung.getMietzahlung() == null) {
            mietzahlung = new Mietzahlung(zuordnung.getMietVertrag(), zuordnung.getBuchung(), zuordnung.getZahlungsKategorie(), zuordnung.getJahr());
            zuordnung.setMietzahlung(mietzahlung);
            getUpdateManager().addInsert(mietzahlung);
        } else {
            mietzahlung = zuordnung.getMietzahlung();
            mietzahlung.setMietVertrag(zuordnung.getMietVertrag());
            getUpdateManager().addUpdate(mietzahlung);
        }

        zuordnungTableView.refresh();
    }

    /**
     * Dialog zur Auswahl einer Person aus der Collection
     * @param vertraege Collection der Auswahl an Mietverträgen
     * @return der gewählte Mietvertrag
     */
    private MietVertrag waehleVertrag(Collection<MietVertrag> vertraege) {
        List vertraegeList = new ArrayList(vertraege);
        Collections.sort(vertraegeList, Comparator.comparing((MietVertrag::getBeginn), reverseOrder()));

        ChoiceDialog<MietVertrag> choiceDialog = new ChoiceDialog<>(null, vertraegeList);
        choiceDialog.setTitle("Wähle passenden Mietvertrag");
        choiceDialog.setHeaderText("Es gibt mehrere Mietverträge für die Wohnung. Bitte wählen Sie einen Vertrag aus.");
        choiceDialog.setContentText("Mietverträge");

        choiceDialog.showAndWait();
        MietVertrag mv = choiceDialog.resultProperty().getValue();

        return mv; /*choiceDialog.getSelectedItem();*/
    }

    public void handleAnteilChangedAction(TableColumn.CellEditEvent event) {
        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        Double anteil = (Double) event.getNewValue();

        if (zuordnung.getMietzahlung() != null && zuordnung.getBetrag() > anteil) {
            zuordnung.setAnteil(anteil);
            zuordnung.getMietzahlung().setBetrag(anteil);
            getUpdateManager().addUpdate(zuordnung.getMietzahlung());

            /* neue Mietzahlung mit Restbetrag als Anteil aufnehmen */
            Zuordnung neueZuordnung = zuordnung.clone();
            Double neuerAnteil = zuordnung.getBetrag() - anteil;
            neueZuordnung.setAnteil(neuerAnteil);
            neueZuordnung.getMietzahlung().setBetrag(neuerAnteil);
            zuordnungOL.add(neueZuordnung);
            getUpdateManager().addInsert(neueZuordnung.getMietzahlung());
        }

        zuordnungOL.sort(Zuordnung::compareTo);
        zuordnungTableView.refresh();
    }

    public void handleZahlungskategorieChangedAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Zahlungskategorie geändert");

        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        ZahlungsKategorie zahlungsKategorie = (ZahlungsKategorie) event.getNewValue();
        zuordnung.setZahlungsKategorie(zahlungsKategorie);
        zuordnung.getMietzahlung().setZahlungsKategorie(zahlungsKategorie);

        Integer jahr = Integer.valueOf(jahrChoiceBox.getSelectionModel().getSelectedItem());
        if (zahlungsKategorie.equals(ZahlungsKategorie.NEBENKOSTENABRECHNUNG)) {
            jahr += -1;
        }
        zuordnung.setJahr(jahr);
        zuordnung.getMietzahlung().setJahr(jahr);

        getUpdateManager().addUpdate(zuordnung.getMietzahlung());

        zuordnungTableView.refresh();
    }

    public void handleJahrChangedAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Jahr geändert");

        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        Integer jahr = (Integer) event.getNewValue();
        zuordnung.setJahr(jahr);
        zuordnung.getMietzahlung().setJahr(jahr);

        getUpdateManager().addUpdate(zuordnung.getMietzahlung());

        zuordnungTableView.refresh();
    }

    /**
     * Suchen von Buchungen über Nach- und Vorname
     * @param name
     * @param vorname
     * @return Collection der treffenden Buchungen
     */
    private Collection<Zuordnung> sucheBuchung(String name, String vorname) {
        Collection<Zuordnung> result;
        name = name.toUpperCase();
        vorname = vorname.toUpperCase();

        String regularExpression = ".*" + vorname + ".*" + name + ".*";
        result = sucheBuchung(regularExpression);

        if (result.isEmpty()) {
            regularExpression = ".*" + name + ".*" + vorname + ".*";
            result = sucheBuchung(regularExpression);
        }

        if (result.isEmpty()) {
            regularExpression = ".*" + vorname.substring(0, 1) + ".*" + name + ".*";
            result = sucheBuchung(regularExpression);
        }

        if (result.isEmpty()) {
            regularExpression = ".*" + name + ".*" + vorname.substring(0, 1) + ".*";
            result = sucheBuchung(regularExpression);
        }

        if (result.isEmpty()) {
            regularExpression = ".*" + name + ".*";
            result = sucheBuchung(regularExpression);
        }

        return result;
    }

    /**
     * Suchen von Buchungen über einen regulären Ausdruck
     * @param regularExpression
     * @return Collection der treffenden Buchungen
     */
    private Collection<Zuordnung> sucheBuchung(String regularExpression) {
        Collection<Zuordnung> result = new ArrayList<>(5);
        String patternStr = regularExpression.toUpperCase();
        Pattern pattern = Pattern.compile(patternStr);

        for (Zuordnung zuordnung : zuordnungOL) {
            Matcher matcher = pattern.matcher(zuordnung.getEmpfaenger().toUpperCase());
            if (matcher.matches()) {
                result.add(zuordnung);
            }
        }
        return result;
    }

    /**
     * Übergebene Zuordnung als Mietzahlung im Updatemanager registrieren
     * @param zuordnung
     */
    private void registriereDbUpdate(Zuordnung zuordnung) {
        Mietzahlung mietzahlung;
        if (zuordnung.getMietzahlung() == null) {
            mietzahlung = new Mietzahlung(zuordnung.getMietVertrag(), zuordnung.getBuchung(), zuordnung.getZahlungsKategorie(), zuordnung.getJahr());
            zuordnung.setMietzahlung(mietzahlung);
            getUpdateManager().addInsert(mietzahlung);
        } else {
            mietzahlung = zuordnung.getMietzahlung();
            mietzahlung.setBuchung(zuordnung.getBuchung());
            mietzahlung.setMietVertrag(zuordnung.getMietVertrag());
            mietzahlung.setZahlungsKategorie(zuordnung.getZahlungsKategorie());
            mietzahlung.setJahr(zuordnung.getJahr());
            getUpdateManager().addUpdate(mietzahlung);
        }

    }

    private Collection<Zuordnung> sucheBetrag(double betrag, Collection<Zuordnung> zuordnungen) {
        Collection<Zuordnung> result = new ArrayList<>(5);
        for (Zuordnung zuordnung: zuordnungen) {
            if (zuordnung.getBetrag() > betrag - 0.5d) {
                result.add(zuordnung);
            }
        }
        return result;
    }

    private void checkButtons() {

        Zuordnung zuordnung = zuordnungTableView.getSelectionModel().getSelectedItem();

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
        zuordnungOL = FXCollections.observableArrayList();
        zahlungsKategorieOL = FXCollections.observableArrayList();
        wohnungOL = FXCollections.observableArrayList();
        zahlungsKategorieOL.addAll(ZahlungsKategorie.values());
        wohnungOL.addAll(HausJpaPersistence.getInstance().selectAllWohnungen());

        initializeChoiceBoxes();
        initializeTableView();
        initializeTableViewColumns();

        fillZuordnungTable();

        checkButtons();

    }

    /**
     * Zeigt die übergebene Meldung an
     *
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
     *
     * @param message Meldung, die gezeigt werden soll
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void initializeChoiceBoxes() {
        initializeJahrChoiceBox();
        initializeMonatChoiceBox();
        initializeKategorieChoiceBox();

    }

    public void initializeJahrChoiceBox() {
        jahrOL = FXCollections.observableArrayList();
        jahrIntOL = FXCollections.observableArrayList();

    /* Fuellen der ChoiceBox */
        LocalDate date = LocalDate.now();
        int jahr = date.getYear();

        for (Integer i = jahr; i > (jahr - 5); i--) {
            jahrOL.add(String.valueOf(i));
            jahrIntOL.add(i);
        }

        jahrChoiceBox.setItems(jahrOL);
        jahrChoiceBox.getSelectionModel().select(0);
        jahrChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fillZuordnungTable());

    }

    public void initializeMonatChoiceBox() {

    /* Fuellen der ChoiceBox */
        LocalDate date = LocalDate.now();
        int indexAktuellerMonat = date.getMonthValue();

        monatChoiceBox.getItems().addAll(Monat.values());
        monatChoiceBox.getSelectionModel().select(indexAktuellerMonat - 1);
        monatChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fillZuordnungTable());

        monatChoiceBox.setConverter(
                new StringConverter<Monat>() {
                    @Override
                    public String toString(Monat monat) {
                        return monat.getName();
                    }

                    @Override
                    public Monat fromString(String string) {
                        throw new UnsupportedOperationException("Not supported");
                    }
                }
        );

    }

    private void initializeKategorieChoiceBox() {

        kategorieChoiceBox.getItems().add(BuchungsKategorie.MIETE);
        kategorieChoiceBox.getItems().add(BuchungsKategorie.MIETKAUTION);
        kategorieChoiceBox.getItems().add(BuchungsKategorie.ALLE);

        kategorieChoiceBox.getSelectionModel().select(0);

        kategorieChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fillZuordnungTable());

        kategorieChoiceBox.setConverter(
                new StringConverter<BuchungsKategorie>() {
                    @Override
                    public String toString(BuchungsKategorie kategorie) {
                        return kategorie.getBeschreibung();
                    }

                    @Override
                    public BuchungsKategorie fromString(String string) {
                        throw new UnsupportedOperationException("Not supported");
                    }
                }
        );

    }

    private void initializeTableView() {
        //ausgabePositionTableView.prefHeightProperty().bind(ausgabePositionenScrollPane.heightProperty());
        //ausgabePositionTableView.prefWidthProperty().bind(ausgabePositionenScrollPane.widthProperty());
    }

    private void initializeTableViewColumns() {
       //buchungCol.setStyle("-fx-background-color: wheat;");

       /** Zurdnung Spalte Datum */
       //datumCol.setStyle("-fx-background-color: beige;");
       datumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zuordnung zuordnung = (Zuordnung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(DatumHelper.getDatumInternational(zuordnung.getDatum()));
                    return property;
                });

        /** Zurdnung Spalte Empfänger */
        //empfaengerCol.setStyle("-fx-background-color: beige;");
        empfaengerCol.setCellValueFactory(new PropertyValueFactory<>("empfaenger"));

        /** Zurdnung Spalte Betrag */
        //betragCol.setStyle("-fx-alignment: CENTER-RIGHT; -fx-background-color: beige;");
        betragCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        betragCol.setCellFactory(TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter()));
        betragCol.setCellValueFactory(new PropertyValueFactory<>("betrag"));

        /** Zurdnung Spalte BuchungsKategorie */
        //kategorieCol.setStyle("-fx-background-color: beige;");
        kategorieCol.setCellValueFactory(new PropertyValueFactory<>("kategorie"));

        /** Zurdnung Spalte Wohnungsnummer */
        wohnungCol.setCellFactory(ComboBoxTableCell.forTableColumn(new WohnungStringConverter(), wohnungOL));
        wohnungCol.setCellValueFactory(new PropertyValueFactory<>("wohnung"));

        /** Zurdnung Spalte Mieter */
        mieterCol.setCellFactory(TextFieldTableCell.forTableColumn());
        mieterCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zuordnung zuordnung = (Zuordnung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zuordnung.getMieter() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zuordnung.getMieter().getName() + ", " + zuordnung.getMieter().getVorname());
                    }
                    return property;
                });

        /** Zurdnung Spalte Betrag */
        anteilCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        anteilCol.setCellFactory(TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter()));
        anteilCol.setCellValueFactory(new PropertyValueFactory<>("anteil"));

        /** Zurdnung Spalte Verwendung */
        verwendungCol.setCellFactory(ComboBoxTableCell.forTableColumn(new ZahlungsKategorieStringConverter(), zahlungsKategorieOL));
        verwendungCol.setCellValueFactory(new PropertyValueFactory<>("zahlungsKategorie"));

        /** Zurdnung Spalte Jahr */
        jahrCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        jahrCol.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), jahrIntOL));
        jahrCol.setCellValueFactory(new PropertyValueFactory<>("jahr"));
    }

    @Override
    public void closeRequest() {
        zuordnungOL.clear();
        fillZuordnungTable();
        Stage stage = (Stage) zuordnungTableView.getScene().getWindow();
        stage.close();
    }

    public class Zuordnung implements Cloneable, Comparable<Zuordnung> {
        private Date datum;
        private String empfaenger;
        private Double betrag;
        private String kategorie;
        private Wohnung wohnung;
        private Person mieter;
        private Double anteil;
        private ZahlungsKategorie zahlungsKategorie;
        private Integer jahr;
        private MietVertrag mietVertrag;
        private Mietzahlung mietzahlung;
        private Buchung buchung;

        public Zuordnung() {
        }

        public Zuordnung(Buchung buchung) {
            this.buchung = buchung;
            this.betrag = buchung.getBetrag();
            this.datum = buchung.getDatum();
            this.empfaenger = buchung.getEmpfaenger();
            this.kategorie = buchung.getKategorie();
        }

        public Double getAnteil() {
            return anteil;
        }

        public void setAnteil(Double anteil) {
            this.anteil = anteil;
        }

        public Integer getJahr() {
            return jahr;
        }

        public void setJahr(Integer jahr) {
            this.jahr = jahr;
        }

        public Mietzahlung getMietzahlung() {
            return mietzahlung;
        }

        public void setMietzahlung(Mietzahlung mietzahlung) {
            this.mietzahlung = mietzahlung;
        }

        public String getKategorie() {
            return kategorie;
        }

        public void setKategorie(String kategorie) {
            kategorie = kategorie;
        }

        public Wohnung getWohnung() {
            return wohnung;
        }

        public void setWohnung(Wohnung wohnung) {
            this.wohnung = wohnung;
        }

        public Date getDatum() {
            return datum;
        }

        public void setDatum(Date datum) {
            this.datum = datum;
        }

        public String getEmpfaenger() {
            return empfaenger;
        }

        public void setEmpfaenger(String empfaenger) {
            this.empfaenger = empfaenger;
        }

        public Double getBetrag() {
            return betrag;
        }

        public void setBetrag(Double betrag) {
            this.betrag = betrag;
        }

        public Person getMieter() {
            return mieter;
        }

        public void setMieter(Person mieter) {
            this.mieter = mieter;
        }

        public ZahlungsKategorie getZahlungsKategorie() {
            return zahlungsKategorie;
        }

        public void setZahlungsKategorie(ZahlungsKategorie zahlungsKategorie) {
            this.zahlungsKategorie = zahlungsKategorie;
        }

        public MietVertrag getMietVertrag() {
            return mietVertrag;
        }

        public void setMietVertrag(MietVertrag mietVertrag) {
            this.mietVertrag = mietVertrag;
            this.mieter = mietVertrag.getMieter();
            this.wohnung = mietVertrag.getWohnung();
        }

        public Buchung getBuchung() {
            return buchung;
        }

        public void setBuchung(Buchung buchung) {
            this.buchung = buchung;
        }

        public Zuordnung clone() {
            Zuordnung clone = new Zuordnung(this.getBuchung());
            clone.setMietVertrag(this.getMietVertrag());
            clone.setJahr(this.getJahr());
            clone.setMietzahlung(this.getMietzahlung().clone());
            return clone;
        }

        @Override
        public int compareTo(Zuordnung o) {
            return this.getBuchung().compareTo(o.getBuchung());
        }
    }

}
