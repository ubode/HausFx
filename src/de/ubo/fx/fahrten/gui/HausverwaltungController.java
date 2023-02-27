package de.ubo.fx.fahrten.gui;

import de.ubo.fx.fahrten.helper.ControllerRegistry;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by ulrichbode on 13.02.16.
 */
public class HausverwaltungController implements Initializable, CloseRequestable {

    private final static Logger LOGGER = Logger.getLogger(HausverwaltungController.class.getName());
    public StackPane hauptPane;
    public AnchorPane bildHintergrund;
    public Label datenbank;
    private Region wohnungenParent;
    private Region fahrtenParent;
    private Region stationenParent;
    private Region streckenParent;
    private Region ausgabenKatalogParent;
    private Region ausgabenBuchungParent;
    private Region mietenBuchungParent;
    private Region buchungsKatalogParent;
    private Region quickenParent;
    private Region excelParent;
    private Region kontakteParent;
    private Region excelXslxParent;
    private Stage wohnungenStage;
    private Stage ausgabenKatalogStage;
    private Stage ausgabenBuchungStage;
    private Stage mietenBuchungStage;
    private Stage buchungsKatalogStage;
    private Stage quickenStage;
    private Stage excelStage;
    private Stage fahrtenStage;
    private Stage streckenStage;
    private Stage kontakteStage;
    private Stage excelXlsxStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ControllerRegistry.getInstance().add(this);

        LOGGER.getParent().setLevel(Level.INFO);
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("hausLog.txt");
            LOGGER.getParent().addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.warning("hausLog.txt kann nicht geöffnet werden");
        }

        Map<String, Object> props = HausJpaPersistence.getInstance().getEntityManager().getProperties();
        String dbName = props.get("javax.persistence.jdbc.url").toString();
        String[] teile = dbName.split("/");
        String dbPart = teile[3].split("\\?")[0];
        datenbank.setText("db: " + dbPart);
    }

    public void handleMenu(ActionEvent event) {
        LOGGER.info("Menu Item gewählt");

        String id = ((MenuItem) event.getSource()).getId();

        try {
            switch (id) {
                case ("beendenMI"):
                    break;
                case ("wohnungsKatalogMI"):
                    createWohnungenView();
                    break;
                case ("fahrtenMI"):
                    createFahrtenbuchView();
                    break;
                case ("zieleMI"):
                    createZieleView();
                    break;
                case ("streckenMI"):
                    createStreckenView();
                    break;
                case ("mietenBuchungMI"):
                    createMietenBuchungView();
                    break;
                case ("ausgabenBuchungMI"):
                    createAusgabenBuchungView();
                    break;
                case ("buchungsKatalogMI"):
                    createBuchungsKatalogView();
                    break;
                case ("ausgabenKatalogMI"):
                    createAusgabenKatalogView();
                    break;
                case ("quickenImportMI"):
                    createQuickenView();
                    break;
                case ("excelImportMI"):
                    createExcelView();
                    break;
                case ("xslxImportMI"):
                    createExcelXlsxView();
                    break;
                case ("kontakteKatalogMI"):
                    createKontakteView();
                    break;
                default:
                    System.out.println("nichts zu tun");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createWohnungenView() throws IOException {

        if (this.wohnungenStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Wohnungen.fxml"));
            this.wohnungenParent = loader.load();
            wohnungenStage = new Stage();
            wohnungenStage.setTitle("Wohnungen");
            wohnungenStage.setScene(new Scene(wohnungenParent, 1150, 800));
            WohnungenController controller = loader.getController();
            wohnungenStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        wohnungenStage.show();
    }

    private void createFahrtenbuchView() throws IOException {

        if (this.fahrtenStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Fahrtenbuch.fxml"));
            this.fahrtenParent = loader.load();
            fahrtenStage = new Stage();
            fahrtenStage.setTitle("Fahrtenbuch");
            fahrtenStage.setScene(new Scene(fahrtenParent, 1100, 800));
            FahrtenbuchController controller = loader.getController();
            fahrtenStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        fahrtenStage.show();
    }

    private void createZieleView() throws IOException {
        if (this.stationenParent == null) {
            this.stationenParent = FXMLLoader.load(getClass().getResource("Stammdaten.fxml"));
        }
        hauptPane.getChildren().remove(0);
        hauptPane.getChildren().add(0, stationenParent);
        stationenParent.prefWidthProperty().bind(hauptPane.widthProperty());
        stationenParent.prefHeightProperty().bind(hauptPane.heightProperty());
    }

    private void createStreckenView() throws IOException {

        if (this.streckenParent == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Strecken.fxml"));
            this.streckenParent = loader.load();
            streckenStage = new Stage();
            streckenStage.setTitle("Fahrtstrecken");
            streckenStage.setScene(new Scene(streckenParent, 1000, 600));
            StreckenController controller = loader.getController();
            streckenStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        streckenStage.show();
    }

    private void createMietenBuchungView() throws IOException {

        if (this.mietenBuchungStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MietenBuchung.fxml"));
            this.mietenBuchungParent = loader.load();
            mietenBuchungStage = new Stage();
            mietenBuchungStage.setTitle("Buchung Mieten");
            mietenBuchungStage.setScene(new Scene(mietenBuchungParent, 1100, 800));
            MietenBuchungController controller = loader.getController();
            mietenBuchungStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        mietenBuchungStage.show();
    }

    private void createAusgabenBuchungView() throws IOException {

        if (this.ausgabenBuchungStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AusgabenBuchung.fxml"));
            this.ausgabenBuchungParent = loader.load();
            ausgabenBuchungStage = new Stage();
            ausgabenBuchungStage.setTitle("Buchung Ausgaben");
            ausgabenBuchungStage.setScene(new Scene(ausgabenBuchungParent, 1200, 800));
            AusgabenBuchungController controller = loader.getController();
            ausgabenBuchungStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        ausgabenBuchungStage.show();
    }

    private void createBuchungsKatalogView() throws IOException {

        if (this.buchungsKatalogStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BuchungsKatalog.fxml"));
            this.buchungsKatalogParent = loader.load();
            buchungsKatalogStage = new Stage();
            buchungsKatalogStage.setTitle("Katalog Bankbuchungen");
            buchungsKatalogStage.setScene(new Scene(buchungsKatalogParent, 1100, 800));
            BuchungsKatalogController controller = loader.getController();
            buchungsKatalogStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        buchungsKatalogStage.show();
    }

    private void createAusgabenKatalogView() throws IOException {

        if (this.ausgabenKatalogStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AusgabenKatalog.fxml"));
            this.ausgabenKatalogParent = loader.load();
            ausgabenKatalogStage = new Stage();
            ausgabenKatalogStage.setTitle("Katalog Ausgaben");
            ausgabenKatalogStage.setScene(new Scene(ausgabenKatalogParent, 1100, 800));
            AusgabenKatalogController controller = loader.getController();
            ausgabenKatalogStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        ausgabenKatalogStage.show();
    }

    private void createQuickenView() throws IOException {

        if (this.quickenStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("QuickenImport.fxml"));
            this.quickenParent = loader.load();
            quickenStage = new Stage();
            quickenStage.setTitle("Import Quicken");
            quickenStage.setScene(new Scene(quickenParent, 1000, 800));
            QuickenImportController controller = loader.getController();
            quickenStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        quickenStage.show();
    }

    private void createExcelView() throws IOException {

        if (this.excelStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CsvImport.fxml"));
            this.excelParent = loader.load();
            excelStage = new Stage();
            excelStage.setTitle("Import Excel-Ausgaben");
            excelStage.setScene(new Scene(excelParent, 1100, 800));
            CsvImportController controller = loader.getController();
            excelStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        excelStage.show();
    }

    private void createExcelXlsxView() throws IOException {

        if (this.excelXlsxStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("XslxImport.fxml"));
            this.excelXslxParent = loader.load();
            excelXlsxStage = new Stage();
            excelXlsxStage.setTitle("Import Excel-Ausgaben");
            excelXlsxStage.setScene(new Scene(excelXslxParent, 1100, 800));
            XlsxImportController controller = loader.getController();
            excelXlsxStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        excelXlsxStage.show();
    }
    private void createKontakteView() throws IOException {

        if (this.kontakteParent == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Kontakte.fxml"));
            this.kontakteParent = loader.load();
            kontakteStage = new Stage();
            kontakteStage.setTitle("Katalog Kontakte");
            kontakteStage.setResizable(false);
            kontakteStage.setScene(new Scene(kontakteParent, 1100, 600));
            KontakteController controller = loader.getController();
            kontakteStage.setOnCloseRequest(event -> controller.closeRequest());
        }

        kontakteStage.show();
    }


    @Override
    public void closeRequest() {
        Stage stage = (Stage) hauptPane.getScene().getWindow();
        stage.close();
    }
}
