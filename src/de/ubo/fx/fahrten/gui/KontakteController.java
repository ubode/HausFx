package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.business.Adresse;
import de.ubo.fx.fahrten.business.Buchung;
import de.ubo.fx.fahrten.business.Person;
import de.ubo.fx.fahrten.helper.*;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by ulrichbode on 16.02.16.
 */
public class KontakteController implements Initializable, CloseRequestable {
    private static Image MALE_IMAGE;
    private static Image FEMALE_IMAGE;
    private static Image INAKTIV_IMAGE;
    private static String[] LEERE_TEL_NR = {"",""};
    private static Adresse LEERE_ADRESSE = new Adresse(null, "","","","","","...");

    static {
        URL urlMaleImg = ResourceManager.getInstance().get(ResourceManager.ResourceEnum.USER_MALE_PNG);
        MALE_IMAGE = new Image(urlMaleImg.toExternalForm(), false);
        URL urlFemaleImg = ResourceManager.getInstance().get(ResourceManager.ResourceEnum.USER_FEMALE_PNG);
        FEMALE_IMAGE = new Image(urlFemaleImg.toExternalForm(), false);
        URL urlInaktivImg = ResourceManager.getInstance().get(ResourceManager.ResourceEnum.USER_INAKTIV_PNG);
        INAKTIV_IMAGE = new Image(urlInaktivImg.toExternalForm(), false);
    }

    public Tab datenTab;
    public TableView<Person> personenTableView;
    public TableColumn<Person, String> nameColumn;
    public TableColumn<Person, ImageView> iconColumn;
    public TableView<Buchung> buchungsTableView;
    public TableColumn<Buchung, String> datumCol;
    public TableColumn<Buchung, String>  empfaengerCol;
    public TableColumn<Buchung, String> verwendungCol;
    public TableColumn<Buchung, String> kategorieCol;
    public TableColumn<Buchung, String> betragCol;
    public TableColumn<Buchung, String> buchNrCol;
    public TextField nameTextField;
    public TextField vornameTextField;
    public TextField telVorwahlTextField;
    public TextField telefonTextField;
    public TextField handyVorwahlTextField;
    public TextField handyTextField;
    public TextField faxVorwahlTextField;
    public TextField faxTextField;
    public TextField telDienstVorwahlTextField;
    public TextField telDienstTextField;
    public TextField handyDienstVorwahlTextField;
    public TextField handyDienstTextField;
    public TextField faxDienstVorwahlTextField;
    public TextField faxDienstTextField;
    public TextField emailTextField;
    public TextField bemerkungTextField;
    public TextField strasseTextField;
    public TextField hausnummerTextField;
    public TextField plzTextField;
    public TextField ortTextField;
    public TextField landTextField;
    public CheckBox aktivCheckBox;
    public CheckBox selAktivCheckBox;
    public ChoiceBox<String> anredeChoiceBox;
    public ChoiceBox<String> titelChoiceBox;
    public ChoiceBox<Adresse> adresseChoiceBox;
    public ChoiceBox<Integer> jahrChoiceBox;
    public DatePicker geburtstagDatePicker;
    public Button saveButton;
    public Button loeschButton;
    public Button neuButton;
    public Button abbruchButton;
    private Person aktuellePerson;
    private ObservableList<Person> personenObservableList;
    private UpdateManager<Person> personUpdateManager = new UpdateManager<>(50);
    private Comparator<Person> personenComparator = Comparator.comparing(Person::getName);
    private Comparator<Buchung> buchungsComparator = Comparator.comparing(Buchung::getDatum);
    private ChangeListener<String> personFieldChangeListener = (observable, oldValue, newValue) -> handlePersonFieldChanged(observable, oldValue, newValue);
    private ChangeListener<String> adressFieldChangeListener = (observable, oldValue, newValue) -> handleAdresseFieldChanged(observable, oldValue, newValue);
    private ChangeListener<Boolean> aktivCheckBoxListener = (observable, oldValue, newValue) -> handleAktivCheckBoxChanged(observable, oldValue, newValue);
    private ChangeListener<Boolean> selAktivCheckBoxListener = (observable, oldValue, newValue) -> handleSelAktivCheckBoxChanged(observable, oldValue, newValue);

    private void fillPersonTextFields(Person person) {

        aktuellePerson = person;
        removePersonChangeListener();

        nameTextField.setText(person.getName());
        vornameTextField.setText(person.getVorname());
        String[] telTeile = splitTelefonNummer(person.getTelefonPrivat());
        telVorwahlTextField.setText(telTeile[0]);
        telefonTextField.setText(telTeile[1]);
        String[] handyTeile = splitTelefonNummer(person.getHandyPrivat());
        handyVorwahlTextField.setText(handyTeile[0]);
        handyTextField.setText(handyTeile[1]);
        String[] faxTeile = splitTelefonNummer(person.getFaxPrivat());
        faxVorwahlTextField.setText(faxTeile[0]);
        faxTextField.setText(faxTeile[1]);
        String[] telDienstTeile = splitTelefonNummer(person.getTelefonDienst());
        telDienstVorwahlTextField.setText(telDienstTeile[0]);
        telDienstTextField.setText(telDienstTeile[1]);
        String[] handyDienstTeile = splitTelefonNummer(person.getHandyDienst());
        handyDienstVorwahlTextField.setText(handyDienstTeile[0]);
        handyDienstTextField.setText(handyDienstTeile[1]);
        String[] faxDienstTeile = splitTelefonNummer(person.getFaxDienst());
        faxDienstVorwahlTextField.setText(faxDienstTeile[0]);
        faxDienstTextField.setText(faxDienstTeile[1]);
        emailTextField.setText(person.getEmail());
        bemerkungTextField.setText(person.getBemerkung());
        if (person.getAdresse() == null) {
            strasseTextField.setText("");
            hausnummerTextField.setText("");
            plzTextField.setText("");
            ortTextField.setText("");
            landTextField.setText("");
        } else {
            strasseTextField.setText(person.getAdresse().getStrasse());
            hausnummerTextField.setText(person.getAdresse().getHausnummer());
            plzTextField.setText(person.getAdresse().getPostleitzahl());
            ortTextField.setText(person.getAdresse().getOrt());
            landTextField.setText(person.getAdresse().getLand());
        }

        aktivCheckBox.setSelected(person.isAktiv());
        anredeChoiceBox.setValue(person.getAnrede());
        titelChoiceBox.setValue(person.getTitel());
        adresseChoiceBox.getSelectionModel().clearSelection();
        geburtstagDatePicker.getEditor().setText(person.getGeburtstagFormatiert());

        enablePersonFields();
        addPersonChangeListener();
        checkButtons();

    }

    private void clearPersonFields() {
        removePersonChangeListener();
        nameTextField.setText("");
        vornameTextField.setText("");
        telVorwahlTextField.setText("");
        telefonTextField.setText("");
        handyVorwahlTextField.setText("");
        handyTextField.setText("");
        faxVorwahlTextField.setText("");
        faxTextField.setText("");
        telDienstVorwahlTextField.setText("");
        telDienstTextField.setText("");
        handyDienstVorwahlTextField.setText("");
        handyDienstTextField.setText("");
        faxDienstVorwahlTextField.setText("");
        faxDienstTextField.setText("");
        emailTextField.setText("");
        bemerkungTextField.setText("");
        strasseTextField.setText("");
        hausnummerTextField.setText("");
        plzTextField.setText("");
        ortTextField.setText("");
        landTextField.setText("");
        anredeChoiceBox.setValue("");
        adresseChoiceBox.setValue(null); //getSelectionModel().clearSelection();
        titelChoiceBox.setValue("");
        geburtstagDatePicker.getEditor().setText("");

        aktuellePerson = null;
        enablePersonFields();
        addPersonChangeListener();
        checkButtons();
    }

    private void clearBuchungsTab() {
        buchungsTableView.getItems().clear();
    }

    private void disablePersonFields() {
        nameTextField.setDisable(true);
        vornameTextField.setDisable(true);
        telVorwahlTextField.setDisable(true);
        telefonTextField.setDisable(true);
        handyVorwahlTextField.setDisable(true);
        handyTextField.setDisable(true);
        faxVorwahlTextField.setDisable(true);
        faxTextField.setDisable(true);
        telDienstVorwahlTextField.setDisable(true);
        telDienstTextField.setDisable(true);
        handyDienstVorwahlTextField.setDisable(true);
        handyDienstTextField.setDisable(true);
        faxDienstVorwahlTextField.setDisable(true);
        faxDienstTextField.setDisable(true);
        emailTextField.setDisable(true);
        bemerkungTextField.setDisable(true);
        strasseTextField.setDisable(true);
        hausnummerTextField.setDisable(true);
        plzTextField.setDisable(true);
        ortTextField.setDisable(true);
        landTextField.setDisable(true);
        anredeChoiceBox.setDisable(true);
        titelChoiceBox.setDisable(true);
        adresseChoiceBox.setDisable(true);
        geburtstagDatePicker.setDisable(true);
    }

    private void enablePersonFields() {
        nameTextField.setDisable(false);
        vornameTextField.setDisable(false);
        telVorwahlTextField.setDisable(false);
        telefonTextField.setDisable(false);
        handyVorwahlTextField.setDisable(false);
        handyTextField.setDisable(false);
        faxVorwahlTextField.setDisable(false);
        faxTextField.setDisable(false);
        telDienstVorwahlTextField.setDisable(false);
        telDienstTextField.setDisable(false);
        handyDienstVorwahlTextField.setDisable(false);
        handyDienstTextField.setDisable(false);
        faxDienstVorwahlTextField.setDisable(false);
        faxDienstTextField.setDisable(false);
        emailTextField.setDisable(false);
        bemerkungTextField.setDisable(false);
        strasseTextField.setDisable(false);
        hausnummerTextField.setDisable(false);
        plzTextField.setDisable(false);
        ortTextField.setDisable(false);
        landTextField.setDisable(false);
        anredeChoiceBox.setDisable(false);
        titelChoiceBox.setDisable(false);
        adresseChoiceBox.setDisable(false);
        geburtstagDatePicker.setDisable(false);
    }

    private void addPersonChangeListener() {
        nameTextField.textProperty().addListener(personFieldChangeListener);
        vornameTextField.textProperty().addListener(personFieldChangeListener);
        telVorwahlTextField.textProperty().addListener(personFieldChangeListener);
        telefonTextField.textProperty().addListener(personFieldChangeListener);
        handyVorwahlTextField.textProperty().addListener(personFieldChangeListener);
        handyTextField.textProperty().addListener(personFieldChangeListener);
        faxVorwahlTextField.textProperty().addListener(personFieldChangeListener);
        faxTextField.textProperty().addListener(personFieldChangeListener);
        telDienstVorwahlTextField.textProperty().addListener(personFieldChangeListener);
        telDienstTextField.textProperty().addListener(personFieldChangeListener);
        handyDienstVorwahlTextField.textProperty().addListener(personFieldChangeListener);
        handyDienstTextField.textProperty().addListener(personFieldChangeListener);
        faxDienstVorwahlTextField.textProperty().addListener(personFieldChangeListener);
        faxDienstTextField.textProperty().addListener(personFieldChangeListener);
        emailTextField.textProperty().addListener(personFieldChangeListener);
        bemerkungTextField.textProperty().addListener(personFieldChangeListener);
        aktivCheckBox.selectedProperty().addListener(aktivCheckBoxListener);
        strasseTextField.textProperty().addListener(adressFieldChangeListener);
        hausnummerTextField.textProperty().addListener(adressFieldChangeListener);
        plzTextField.textProperty().addListener(adressFieldChangeListener);
        ortTextField.textProperty().addListener(adressFieldChangeListener);
        landTextField.textProperty().addListener(adressFieldChangeListener);
    }

    private void removePersonChangeListener() {
        nameTextField.textProperty().removeListener(personFieldChangeListener);
        vornameTextField.textProperty().removeListener(personFieldChangeListener);
        telVorwahlTextField.textProperty().removeListener(personFieldChangeListener);
        telefonTextField.textProperty().removeListener(personFieldChangeListener);
        handyVorwahlTextField.textProperty().removeListener(personFieldChangeListener);
        handyTextField.textProperty().removeListener(personFieldChangeListener);
        faxVorwahlTextField.textProperty().removeListener(personFieldChangeListener);
        faxTextField.textProperty().removeListener(personFieldChangeListener);
        telDienstVorwahlTextField.textProperty().removeListener(personFieldChangeListener);
        telDienstTextField.textProperty().removeListener(personFieldChangeListener);
        handyDienstVorwahlTextField.textProperty().removeListener(personFieldChangeListener);
        handyDienstTextField.textProperty().removeListener(personFieldChangeListener);
        faxDienstVorwahlTextField.textProperty().removeListener(personFieldChangeListener);
        faxDienstTextField.textProperty().removeListener(personFieldChangeListener);
        emailTextField.textProperty().removeListener(personFieldChangeListener);
        bemerkungTextField.textProperty().removeListener(personFieldChangeListener);
        aktivCheckBox.selectedProperty().removeListener(aktivCheckBoxListener);
        strasseTextField.textProperty().removeListener(adressFieldChangeListener);
        hausnummerTextField.textProperty().removeListener(adressFieldChangeListener);
        plzTextField.textProperty().removeListener(adressFieldChangeListener);
        ortTextField.textProperty().removeListener(adressFieldChangeListener);
        landTextField.textProperty().removeListener(adressFieldChangeListener);
    }

    private void fillAktuellePersonFromTextFields() {

        if (aktuellePerson == null) {
            aktuellePerson = new Person();
            Adresse adresse = new Adresse();
            aktuellePerson.setAdresse(adresse);
        }

        aktuellePerson.setName(nameTextField.getText());
        aktuellePerson.setVorname(vornameTextField.getText());
        aktuellePerson.setTelefonPrivat(telVorwahlTextField.getText() + "-" + telefonTextField.getText());
        aktuellePerson.setHandyPrivat(handyVorwahlTextField.getText() + "-" + handyTextField.getText());
        aktuellePerson.setFaxPrivat(faxVorwahlTextField.getText() + "-" + faxTextField.getText());
        aktuellePerson.setTelefonDienst(telDienstVorwahlTextField.getText() + "-" + telDienstTextField.getText());
        aktuellePerson.setHandyDienst(handyDienstVorwahlTextField.getText() + "-" + handyDienstTextField.getText());
        aktuellePerson.setFaxDienst(faxDienstVorwahlTextField.getText() + "-" + faxDienstTextField.getText());
        aktuellePerson.setEmail(emailTextField.getText());
        aktuellePerson.setBemerkung(bemerkungTextField.getText());
        aktuellePerson.setAktiv(aktivCheckBox.isSelected());

        /*
        aktuellePerson.getAdresse().setStrasse(strasseTextField.getText());
        aktuellePerson.getAdresse().setHausnummer(hausnummerTextField.getText());
        aktuellePerson.getAdresse().setPostleitzahl(plzTextField.getText());
        aktuellePerson.getAdresse().setOrt(ortTextField.getText());
        aktuellePerson.getAdresse().setLand(landTextField.getText());
        */
        aktuellePerson.setAnrede(anredeChoiceBox.getValue());
        aktuellePerson.setTitel(titelChoiceBox.getValue());

        LocalDate localDate = geburtstagDatePicker.getValue();
        if (localDate != null) {
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            aktuellePerson.setGeburtstag(Date.from(instant));
        }

        checkButtons();
    }

    private String[] splitTelefonNummer(String telefonNummer) {
        if (telefonNummer == null) return LEERE_TEL_NR;
        String[] teile = telefonNummer.split("[-,/]");
        teile = teile.length < 2 ? LEERE_TEL_NR : teile;
        return teile;
    }

    private void fillPersonTable(Person suchPerson) {
        personenObservableList.clear();

        // cache löschen und neu von der DB holen
        HausJpaPersistence.getInstance().getEntityManager().clear();

        Collection<Person> personenColl = HausJpaPersistence.getInstance().selectAllPersonen(selAktivCheckBox.isSelected());
        ArrayList<Person> personenList = new ArrayList<>(personenColl);
        personenList.sort(personenComparator);

        personenObservableList.addAll(personenList);
        personenTableView.setItems(personenObservableList);

        if (suchPerson != null) {
            int index = sucheIndex(suchPerson);
            if (index > 0) {
                personenTableView.getSelectionModel().select(index);
            }
        }
    }

    private void fillBuchungsTable() {

        buchungsTableView.getItems().clear();
        Collection<Bedingung> bedingungen = new ArrayList<>(5);

        int jahr = jahrChoiceBox.getSelectionModel().getSelectedItem();
        bedingungen.add(new Bedingung(Kriterium.DATUM, Operator.GROESSER_GLEICH, String.valueOf(jahr)));

        Collection<Buchung> buchungsColl = HausJpaPersistence.getInstance().selectBuchungen(bedingungen, aktuellePerson.getName(), aktuellePerson.getVorname());
        ArrayList<Buchung> buchungsList = new ArrayList<>(buchungsColl);
        buchungsList.sort(buchungsComparator);

        buchungsTableView.getItems().addAll(buchungsList);
        buchungsTableView.refresh();
    }


    private Collection<Buchung> ermittleBuchungenZu(Collection<Bedingung> bedingungen, String name, String vorname) {

        Collection<Buchung> buchungsColl;

        String nameRegex = "*" + name + "*";
        Bedingung bedName = new Bedingung(Kriterium.EMPFAENGER, Operator.GLEICH, nameRegex);

        String vornameRegex = "*" + vorname + "*";
        Bedingung bedVorname = new Bedingung(Kriterium.EMPFAENGER, Operator.GLEICH, vornameRegex);

        String initialeRegex = "*" + vorname.substring(0, 1) + "*";
        Bedingung bedInitiale = new Bedingung(Kriterium.EMPFAENGER, Operator.GLEICH, initialeRegex);

        // Suche mit Name + Vorname
        bedingungen.add(bedName);
        bedingungen.add(bedVorname);
        buchungsColl = HausJpaPersistence.getInstance().selectBuchungen(bedingungen);

        // Suche mit Name + 1. Buchstaben des Vornamens
        if (buchungsColl.isEmpty()) {
            bedingungen.remove(bedVorname);
            bedingungen.add(bedInitiale);
            buchungsColl = HausJpaPersistence.getInstance().selectBuchungen(bedingungen);
        }

        // Suche mit nur mit Nachnamen
        if (buchungsColl.isEmpty()) {
            bedingungen.remove(bedInitiale);
            buchungsColl = HausJpaPersistence.getInstance().selectBuchungen(bedingungen);
        }

        return buchungsColl;
    }

    public void handleBuchungenTab() {
        if (aktuellePerson.getId() != null) {
            fillBuchungsTable();
            checkBuchungsTabButtons();
        } else {
            clearBuchungsTab();
        }
    }

    public void handleDatenTab() {
        if (aktuellePerson != null) {
            fillPersonTable(aktuellePerson);
            checkDatenTabButtons();
        }
    }

    public void handlePersonSelected(Person oldPerson, Person newPerson) {

        //Sind noch Änderungen zur aktuellen Person zu speichern?
        if (!personUpdateManager.isEmpty()) {
            if (speichernOderAbbrechen()) {
                speicherePerson();
            } else {
                abbrechenUpdate();
            }
        }

        if (newPerson.getId() != null) {
            fillPersonTextFields(newPerson);
            fillBuchungsTable();
        }
    }

    public void handleNeuButton(ActionEvent event) {
        aktuellePerson = new Person();
        fillPersonTextFields(aktuellePerson);
        clearBuchungsTab();
        personenTableView.getSelectionModel().clearSelection();
        nameTextField.requestFocus();
        checkButtons();
        System.out.println("neue Person anlegen");

    }

    public void handleLoeschButton(ActionEvent event) {
        loeschePerson();
        checkButtons();
        System.out.println("handleLoeschButton");

    }

    public void handleAbbruchButton(ActionEvent event) {
        abbrechenUpdate();
        checkButtons();
        System.out.println("handleAbbruchButton");
    }

    /** Update abbrechen
     * Neuaufnahme: alle Felder löschen
     * Änderung: Zustand der Datenbank wieder herstellen
     */
    private void abbrechenUpdate() {
        personUpdateManager.clear();

        if (aktuellePerson.getId() == null) {
            aktuellePerson = null;
            clearPersonFields();
        } else {
            HausJpaPersistence.getInstance().refreshObject(aktuellePerson);
            fillPersonTextFields(aktuellePerson);
        }
    }

    public void handleSaveButton(ActionEvent event) {
        speicherePerson();
        checkButtons();
        System.out.println("neue Person gesichert");
    }

    public void handleChoiceBoxAction() {
        fillAktuellePersonFromTextFields();
        System.out.println("handleChoiceBoxAction");
    }

    private void speicherePerson() {
        String meldung = checkPerson(aktuellePerson);
        if (!meldung.isEmpty()) {
            if (!showAlert(meldung)) {
                abbrechenUpdate();
            }
        } else {
            personUpdateManager.saveUpdates();
            fillPersonTable(aktuellePerson);
        }
    }

    private int sucheIndex(Person aktuellePerson) {
        int index = -1;
        for (Person person: personenObservableList) {
            if (person.equals(aktuellePerson)) {
                index = personenObservableList.indexOf(person);
            }
        }
        return index;
    }

    private void loeschePerson() {
        personUpdateManager.addDelete(aktuellePerson);
        if (loeschenOderAbbrechen()) {
            personUpdateManager.saveUpdates();
            clearPersonFields();
            fillPersonTable(null);
        } else {
            abbrechenUpdate();
        }
    }

    public void handleAdresseFieldChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        /*fillAktuellePersonFromTextFields();*/
        System.out.println("handleAdresseFieldChanged");

    }

    public void handlePersonFieldChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (aktuellePerson.getId() == null) {
            personUpdateManager.addInsert(aktuellePerson);
        } else {
            personUpdateManager.addUpdate(aktuellePerson);
        }
        fillAktuellePersonFromTextFields();
        System.out.println("handlePersonFieldChanged");

    }

    public void handleAktivCheckBoxChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        personUpdateManager.addUpdate(aktuellePerson);
        fillAktuellePersonFromTextFields();
        System.out.println("handleAktivCheckBoxChanged");
    }

    public void handleSelAktivCheckBoxChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        fillPersonTable(null);
        System.out.println("handleAktivCheckBoxChanged");
    }

    public void handleGeburtstagChanged(ActionEvent event) {
        if (aktuellePerson.getId() == null) {
            personUpdateManager.addInsert(aktuellePerson);
        } else {
            personUpdateManager.addUpdate(aktuellePerson);
        }
        fillAktuellePersonFromTextFields();
        System.out.println("handleGeburtstagChanged");
    }

    private void handleAdressSelection(Adresse adress) {
        if (aktuellePerson != null && adress != null) {
            aktuellePerson.setAdresse(adress);
            fillPersonTextFields(aktuellePerson);
        }
    }

    private void handleJahrSelection() {
        if (aktuellePerson.getId() != null) {
            fillBuchungsTable();
        }
    }

    private void checkButtons() {

        if (datenTab.isSelected()) {
            checkDatenTabButtons();
        } else {
            checkBuchungsTabButtons();
        }
    }

    private void checkDatenTabButtons() {
        if (aktuellePerson == null) {
            saveButton.setDisable(true);
            abbruchButton.setDisable(true);
        } else {
            abbruchButton.setDisable(personUpdateManager.isEmpty());
            saveButton.setDisable(!aktuellePerson.isVollstaendig() || personUpdateManager.isEmpty());
        }

        neuButton.setDisable(!personUpdateManager.isEmpty());

        loeschButton.setDisable(personenTableView.getSelectionModel().getSelectedIndex() < 0);
    }

    private void checkBuchungsTabButtons() {
        neuButton.setDisable(true);
        saveButton.setDisable(true);
        abbruchButton.setDisable(true);
        loeschButton.setDisable(true);
    }

    private String checkPerson(Person person) {
        if (person.getName().isEmpty()) return "Name fehlt.";
        if (person.getVorname().isEmpty()) return "Vorname fehlt.";
        if (person.getAdresse() == null) return "Adresse unvollständig";
        if (person.getAdresse().getOrt().isEmpty()) return "Ort fehlt.";
        if (person.getAdresse().getStrasse().isEmpty()) return "Strasse fehlt.";
        if (person.getAdresse().getHausnummer().isEmpty()) return "Hausnummer fehlt.";
        if (person.getAdresse().getPostleitzahl().isEmpty()) return "Postleitzahl fehlt.";
        return "";
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
        return result.get() != buttonTypeReload;
    }

    /**
     * Zeigt die übergebene Meldung an und fragt, ob Änderungen verworfen
     * werden sollen oder ob Anwender korrigieren will.
     * @return true, wenn gespeichert werden soll
     *         false, wenn Daten verworfen werden sollen
     */
    private boolean speichernOderAbbrechen() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Frage");
        alert.setHeaderText("Die Daten sind noch nicht gespeichert.");
        alert.setContentText("Sollen die Daten gespeichert werden");

        ButtonType buttonTypeOK = new ButtonType("Ja");
        ButtonType buttonTypeReload = new ButtonType("Nein");
        alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeReload);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() != buttonTypeReload;
    }

    /**
     * Bestätigung der Löschung
     * @return true, wenn gelöscht werden soll
     *         false, wenn nicht gelöscht werden soll
     */
    private boolean loeschenOderAbbrechen() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Frage");
        alert.setHeaderText("Bestätigung der Löschung");
        alert.setContentText("Sollen die Daten gelöscht werden");

        ButtonType buttonTypeOK = new ButtonType("Ja");
        ButtonType buttonTypeReload = new ButtonType("Nein");
        alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeReload);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() != buttonTypeReload;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerRegistry.getInstance().add(this);
        personenObservableList = FXCollections.observableArrayList();
        initializeTableView();
        initializePersonTableViewColumns();
        initializeBuchungTableViewColumns();
        initializeChoiceBoxes();
        fillPersonTable(null);
        disablePersonFields();
        addPersonChangeListener();
        checkButtons();

    }

    private void initializeTableView() {
        personenTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handlePersonSelected(oldValue, newValue));
        selAktivCheckBox.setSelected(true);
        selAktivCheckBox.selectedProperty().addListener(selAktivCheckBoxListener);
    }

    private void initializePersonTableViewColumns() {
        nameColumn.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    Person person = (Person) ((TableColumn.CellDataFeatures) cellData).getValue();
                    StringBuilder sb = new StringBuilder(person.getName());
                    sb.append(", ");
                    sb.append(person.getVorname());
                    property.setValue(sb.toString());
                    return property;
                });
        iconColumn.setCellValueFactory(
                cellData -> {
                    SimpleObjectProperty property = new SimpleObjectProperty();
                    Person person = (Person) ((TableColumn.CellDataFeatures) cellData).getValue();
                    Image icon;
                    if (person.isAktiv()) {
                        icon = person.getAnrede().equals("Frau") ? FEMALE_IMAGE : MALE_IMAGE;
                    } else {
                        icon = INAKTIV_IMAGE;
                    }
                    ImageView imageView = new ImageView(icon);

                    property.setValue(imageView);
                    return property;
                });

    }

    private void initializeChoiceBoxes() {

        // Tab Daten: Anrede
        anredeChoiceBox.getItems().add("");
        anredeChoiceBox.getItems().add("Herr");
        anredeChoiceBox.getItems().add("Frau");
        anredeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleChoiceBoxAction());

        // Tab Daten: Titel
        titelChoiceBox.getItems().add("");
        titelChoiceBox.getItems().add("Dr.");
        titelChoiceBox.getItems().add("Professor");
        titelChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleChoiceBoxAction());

        // Tab Buchungen: Jahr
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(calendar.YEAR);
        for (int i = year; i > 2004 ; i--) {
            jahrChoiceBox.getItems().add(i);
        }
        jahrChoiceBox.setValue(year);
        jahrChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleJahrSelection());

        // Tab Daten: Adressen
        adresseChoiceBox.setConverter(
                new StringConverter<Adresse>() {
                    @Override
                    public String toString(Adresse adresse) {
                        return adresse.getKuerzel();
                    }

                    @Override
                    public Adresse fromString(String string) {
                        throw new UnsupportedOperationException("Not supported");
                    }
                }
        );
        adresseChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleAdressSelection(newValue));

        Collection<Adresse> adressenColl = HausJpaPersistence.getInstance().selectAllAdressen();
        adresseChoiceBox.getItems().add(LEERE_ADRESSE);
        adresseChoiceBox.getItems().addAll(adressenColl);
    }

    private void initializeBuchungTableViewColumns() {
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

        betragCol.setStyle("-fx-alignment: CENTER-RIGHT;");
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

    }

    @Override
    public void closeRequest() {
        Stage stage = (Stage) personenTableView.getScene().getWindow();
        stage.close();
    }
}
