package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.*;
import de.ubo.fx.fahrten.converter.ExtendedDoubleStringConverter;
import de.ubo.fx.fahrten.converter.HausStringConverter;
import de.ubo.fx.fahrten.helper.*;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
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

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ulric on 01.08.2016.
 */
public class AusgabenBuchungController implements Initializable, CloseRequestable {
    private final static Logger LOGGER = Logger.getLogger(AusgabenBuchungController.class.getName());
    private final UpdateManager<AusgabePosition> updateManager = new UpdateManager<>(500);
    public TableView<Zuordnung> zuordnungTableView;
    public TableColumn buchungCol;
    public TableColumn<Zuordnung, String> datumCol;
    public TableColumn<Zuordnung, String> empfaengerCol;
    public TableColumn<Zuordnung, String> verwendungCol;
    public TableColumn<Zuordnung, Double> betragCol;
    public TableColumn<Zuordnung, String> buchungsKategorieCol;
    public TableColumn<Zuordnung, Haus> hausCol;
    public TableColumn<Zuordnung, AusgabeArt> ausgabenKategorieCol;
    public TableColumn<Zuordnung, Double> anteilCol;
    public TableColumn<Zuordnung, String> nebenkostenCol;
    public TableColumn<Zuordnung, String> referenzCol;
    public Button searchButton;
    public Button saveButton;
    public ChoiceBox<String> jahrChoiceBox;
    public ChoiceBox<Monat> monatChoiceBox;
    public ChoiceBox<BuchungsKategorie> kategorieChoiceBox;
    private ObservableList<String> jahrOL;
    private ObservableList<Integer> jahrIntOL;
    private ObservableList<Haus> hausOL;
    private ObservableList<ZahlungsKategorie> zahlungsKategorieOL;
    private ObservableList<Zuordnung> zuordnungOL;

    private UpdateManager<AusgabePosition> getUpdateManager() {
        return updateManager;
    }

    private void fillZuordnungTable() {

        Collection<Buchung> buchungsColl = holeBuchungen();
        ArrayList<Buchung> buchungsList = new ArrayList<>(buchungsColl);
        Collections.sort(buchungsList);

        zuordnungOL.clear();
        for (Buchung buchung : buchungsList) {
            Collection<Ausgabe> ausgaben = holeAusgaben(buchung);
            if ( ausgaben == null || ausgaben.isEmpty()) {
                Zuordnung zuordnung = new Zuordnung(buchung);
                zuordnungOL.add(zuordnung);
            } else {
                for (Ausgabe ausgabe : ausgaben) {
                    Zuordnung zuordnung = new Zuordnung(buchung);
                    //zuordnung.setAusgabe(ausgabe);
                    //zuordnung.setZahlungsKategorie(ausgabe.get ZahlungsKategorie());
                    zuordnung.setAnteil(ausgabe.getBetrag());
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
        cal.set(Integer.valueOf(jahr), monatInd - 1, 1);
        cal.add(Calendar.DATE, -1);
        String vonDate = DatumHelper.getDatumInternational(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        String bisDate = DatumHelper.getDatumInternational(cal.getTime());

        Bedingung bed1 = new Bedingung(Kriterium.DATUM, Operator.GROESSER, vonDate);
        Bedingung bed2 = new Bedingung(Kriterium.DATUM, Operator.KLEINER, bisDate);
        Bedingung bed3;
        if(kategorie.contains("*")) {
            bed3 = new Bedingung(Kriterium.KATEGORIE, Operator.REGEX, kategorie);
        } else {
            bed3 = new Bedingung(Kriterium.KATEGORIE, Operator.GLEICH, kategorie);
        }
        Collection<Bedingung> bedingungen = Arrays.asList(bed1, bed2, bed3);

        return HausJpaPersistence.getInstance().selectBuchungen(bedingungen);
    }

    private Collection<Ausgabe> holeAusgaben(Buchung buchung) {

        Collection<Ausgabe> ausgaben = null; //HausJpaPersistence.getInstance().selectAusgabePositionen();
        return ausgaben;
    }

    public void handleSearchAction(ActionEvent event) {
        LOGGER.info("Haus Suche ausgelöst");

        ordneHausZu();

        zuordnungTableView.refresh();
        checkButtons();
    }

    /**
     * Durchlauf der Ausgaben: suche nach dem Hinweis auf Haus, fuer das die Ausgabe getaetigt wurde
     */
    private void ordneHausZu() {
        Collection<Haus> haeuser = HausJpaPersistence.getInstance().selectAllHaeuser();

        Collection<Zuordnung> zuordnungen = zuordnungTableView.getItems();
        for (Zuordnung zuordnung : zuordnungen) {
            String verwendungStr = zuordnung.getBuchung().getVerwendung();
            for (Haus haus : haeuser) {
                if (haus.getRegex() != null) {
                    String patternStr = haus.getRegex().toUpperCase();
                    Pattern pattern = Pattern.compile(patternStr);
                    Matcher matcher = pattern.matcher(verwendungStr.toUpperCase());
                    if (matcher.matches()) {
                        zuordnung.setHaus(haus);
                        registriereDbUpdate(zuordnung);
                    }
                }
            }
        }
    }

    /**
     * Durchlauf der Ausgaben: suche nach Buchungen, die auf die Ausgabe zutreffen
     */
    private void ordneAusgabenZu() {
        String jahr = jahrChoiceBox.getSelectionModel().getSelectedItem();
        int jahrInd = Integer.valueOf(jahr);
        int monatInd = monatChoiceBox.getSelectionModel().getSelectedItem().getIndex();

        Collection<Ausgabe> ausgaben = HausJpaPersistence.getInstance().selectAusgaben(jahrInd, monatInd - 1);

        Collection<Zuordnung> result;
/*        for (Ausgabe ausgabe : ausgaben) {
            if (ausgabe.getRegularExpression() == null) {
                String name = vertrag.getMieter().getName();
                String vorname = vertrag.getMieter().getVorname();
                result = sucheBuchung(name, vorname);
            } else {
                result = sucheBuchung(vertrag.getRegularExpression());
            }

            for (Zuordnung zuordnung : result) {
                *//*zuordnung.setMietVertrag(vertrag);
                if (result.size() == 1) {
                    zuordnung.setZahlungsKategorie(ZahlungsKategorie.MIETZINS);
                    zuordnung.setJahr(jahrInd);
                }
                *//*
                registriereDbUpdate(zuordnung);
            }
        }*/
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

    public void handleHausChangedAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Haus geändert");

        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        Haus haus = (Haus) event.getNewValue();
        zuordnung.getAusgabePosition().getAusgabe().setHaus(haus);
        zuordnung.setHaus(haus);

        registriereDbUpdate(zuordnung);

        zuordnungTableView.refresh();
    }

    public void handleAnteilChangedAction(TableColumn.CellEditEvent event) {
        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        Double anteil = (Double) event.getNewValue();

        /*
        if (zuordnung.getMietzahlung() != null && zuordnung.getBetrag() > anteil) {
            zuordnung.setAnteil(anteil);
            zuordnung.getMietzahlung().setBetrag(anteil);
            getUpdateManager().addUpdate(zuordnung.getMietzahlung());

            /* neue Mietzahlung mit Restbetrag als Anteil aufnehmen */
            /*
            Zuordnung neueZuordnung = zuordnung.clone();
            Double neuerAnteil = zuordnung.getBetrag() - anteil;
            neueZuordnung.setAnteil(neuerAnteil);
            neueZuordnung.getMietzahlung().setBetrag(neuerAnteil);
            zuordnungOL.add(neueZuordnung);
            getUpdateManager().addInsert(neueZuordnung.getMietzahlung());
        }
        */
        zuordnungOL.sort(Zuordnung::compareTo);
        zuordnungTableView.refresh();
    }

    public void handleZahlungskategorieChangedAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Zahlungskategorie geändert");

        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        ZahlungsKategorie zahlungsKategorie = (ZahlungsKategorie) event.getNewValue();
        zuordnung.setZahlungsKategorie(zahlungsKategorie);
        //zuordnung.getMietzahlung().setZahlungsKategorie(zahlungsKategorie);

        //getUpdateManager().addUpdate(zuordnung.getMietzahlung());

        zuordnungTableView.refresh();
    }

    public void handleJahrChangedAction(TableColumn.CellEditEvent event) {
        LOGGER.info("Jahr geändert");

        Zuordnung zuordnung = (Zuordnung) event.getRowValue();
        Integer jahr = (Integer) event.getNewValue();
        //zuordnung.setJahr(jahr);
        //zuordnung.getMietzahlung().setJahr(jahr);

        //getUpdateManager().addUpdate(zuordnung.getMietzahlung());

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
        AusgabePosition position;
        if (zuordnung.getAusgabePosition() == null) {
            //position = new AusgabePosition(zuordnung.getAusgabePosition(), zuordnung.getBuchung(), zuordnung.getZahlungsKategorie(), zuordnung.getJahr());
            //zuordnung.setAusgabePosition(position);
            //getUpdateManager().addInsert(position);
        } else {
            position = zuordnung.getAusgabePosition();
            /*
            position.setBuchung(zuordnung.getBuchung());
            mietzahlung.setMietVertrag(zuordnung.getMietVertrag());
            mietzahlung.setZahlungsKategorie(zuordnung.getZahlungsKategorie());
            mietzahlung.setJahr(zuordnung.getJahr());
            */
            getUpdateManager().addUpdate(position);
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
        hausOL = FXCollections.observableArrayList();
        zahlungsKategorieOL.addAll(ZahlungsKategorie.values());
        hausOL.addAll(HausJpaPersistence.getInstance().selectAllHaeuser());

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

        kategorieChoiceBox.getItems().add(BuchungsKategorie.INSTANDHALTUNG);
        kategorieChoiceBox.getItems().add(BuchungsKategorie.ABGABEN);
        kategorieChoiceBox.getItems().add(BuchungsKategorie.STEUER);
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

       /** Zurdnung Spalte Datum */
       datumCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zuordnung zuordnung = (Zuordnung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    property.setValue(DatumHelper.getDatumInternational(zuordnung.getDatum()));
                    return property;
                });

        /** Zurdnung Spalte Empfänger */
        empfaengerCol.setCellValueFactory(new PropertyValueFactory<>("empfaenger"));

        /** Zurdnung Spalte Verwendung */
        verwendungCol.setCellValueFactory(new PropertyValueFactory<>("buchung.verwendung"));
        verwendungCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zuordnung zuordnung = (Zuordnung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zuordnung.getBuchung() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zuordnung.getBuchung().getVerwendung());
                    }
                    return property;
                });

        /** Zurdnung Spalte Betrag */
        betragCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        betragCol.setCellFactory(TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter()));
        betragCol.setCellValueFactory(new PropertyValueFactory<>("betrag"));

        /** Zurdnung Spalte BuchungsKategorie */
        buchungsKategorieCol.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
        buchungsKategorieCol.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Zuordnung zuordnung = (Zuordnung) ((TableColumn.CellDataFeatures) cellData).getValue();
                    if (zuordnung.getBuchung() == null) {
                        property.setValue("");
                    } else {
                        property.setValue(zuordnung.getBuchung().getKategorie());
                    }
                    return property;
                });

        /** Zurdnung Spalte Haus */
        hausCol.setCellFactory(ComboBoxTableCell.forTableColumn(new HausStringConverter(), hausOL));
        hausCol.setCellValueFactory(new PropertyValueFactory<>("haus"));

        /** Zurdnung Spalte AusgabenKategorie */
        ausgabenKategorieCol.setCellValueFactory(new PropertyValueFactory<>("haus.strasse"));

        /** Zurdnung Spalte Betrag (Anteil) */
        anteilCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        anteilCol.setCellFactory(TextFieldTableCell.forTableColumn(new ExtendedDoubleStringConverter()));
        anteilCol.setCellValueFactory(new PropertyValueFactory<>("anteil"));

        /** Zurdnung Spalte Referenz */
        referenzCol.setCellValueFactory(new PropertyValueFactory<>("referenz"));
    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) zuordnungTableView.getScene().getWindow();
        stage.close();
    }

    public class Zuordnung implements Cloneable, Comparable<Zuordnung> {
        private Date datum;
        private String empfaenger;
        private Double betrag;
        private String buchungsKategorie;
        private Haus haus;
        private Double anteil;
        private ZahlungsKategorie zahlungsKategorie;
        private String referenz;
        private AusgabePosition ausgabePosition;
        private Buchung buchung;
        public Zuordnung() {
        }

        public Zuordnung(Buchung buchung) {
            this.buchung = buchung;
            this.betrag = buchung.getBetrag();
            this.datum = buchung.getDatum();
            this.empfaenger = buchung.getEmpfaenger();
            this.buchungsKategorie = buchung.getKategorie();
        }

        public Double getAnteil() {
            return anteil;
        }

        public void setAnteil(Double anteil) {
            this.anteil = anteil;
        }

        public String getBuchungsKategorie() {
            return buchungsKategorie;
        }

        public void setBuchungsKategorie(String buchungsKategorie) {
            buchungsKategorie = buchungsKategorie;
        }

        public Haus getHaus() {
            return haus;
        }

        public void setHaus(Haus haus) {
            this.haus = haus;
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

        public ZahlungsKategorie getZahlungsKategorie() {
            return zahlungsKategorie;
        }

        public void setZahlungsKategorie(ZahlungsKategorie zahlungsKategorie) {
            this.zahlungsKategorie = zahlungsKategorie;
        }

        public AusgabePosition getAusgabePosition() {
            return ausgabePosition;
        }

        public void setAusgabePosition(AusgabePosition ausgabePosition) {
            this.ausgabePosition = ausgabePosition;
        }

        public Buchung getBuchung() {
            return buchung;
        }

        public void setBuchung(Buchung buchung) {
            this.buchung = buchung;
        }

        public Zuordnung clone() {
            Zuordnung clone = new Zuordnung(this.getBuchung());
            clone.setAusgabePosition(this.getAusgabePosition().clone());
            return clone;
        }

        @Override
        public int compareTo(Zuordnung o) {
            return this.getBuchung().compareTo(o.getBuchung());
        }
    }
}
