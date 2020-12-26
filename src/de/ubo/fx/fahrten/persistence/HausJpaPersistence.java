package de.ubo.fx.fahrten.persistence;

import de.ubo.fx.fahrten.business.*;
import de.ubo.fx.fahrten.helper.Bedingung;
import de.ubo.fx.fahrten.helper.Kriterium;
import de.ubo.fx.fahrten.helper.Operator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * Created by Bode on 22.02.2016.
 */
public class HausJpaPersistence {
    private final static Logger LOGGER = Logger.getLogger(HausJpaPersistence.class.getName());
    private static HausJpaPersistence instance = new HausJpaPersistence();
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;


    private HausJpaPersistence() {

        entityManagerFactory = Persistence.createEntityManagerFactory("HausverwaltungPU");
        entityManager = entityManagerFactory.createEntityManager();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        entityManager.close();
        entityManagerFactory.close();
    }

    public static HausJpaPersistence getInstance() {
        return instance;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Methode liefert sämtliche Ziele des Fahrtenbuchs (komplette Tabelle)
     */
    public Collection<Ziel> selectZiele() {

        Query query = getEntityManager().createQuery("SELECT z FROM Ziel AS z");

        return (Collection<Ziel>) query.getResultList();
    }

    /**
     * Methode erzwingt das erneute Lesen sämtlicher Ziele des Fahrtenbuchs (komplette Tabelle)
     */
    public Collection<Ziel> refreshZiele() {

        Query query = getEntityManager().createQuery("SELECT z FROM Ziel AS z");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        return (Collection<Ziel>) query.getResultList();
    }

    /**
     * Methode erzwingt das erneute Lesen des Objects
     */
    public void refreshObject(Object object) {

        getEntityManager().refresh(object);
    }

    /**
     * Methode liefert sämtliche Anlässe des Fahrtenbuchs (komplette Tabelle)
     */
    public Collection<Anlass> selectAnlaesse() {

        Query query = getEntityManager().createQuery("SELECT z FROM Anlass AS z");
        LOGGER.info(query.toString());

        return (Collection<Anlass>) query.getResultList();
    }

    /**
     * Methode erzwingt das erneute Lesen sämtlicher Anlässe des Fahrtenbuchs (komplette Tabelle)
     */
    public Collection<Anlass> refreshAnlaesse() {

        Query query = getEntityManager().createQuery("SELECT z FROM Anlass AS z");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        LOGGER.info(query.toString());

        return (Collection<Anlass>) query.getResultList();
    }

    /**
     * Methode liefert sämtliche Fahrten zu allen Kalenderjahr
     */
    public Collection<Fahrt> selectAlleFahrten() {

        Query query = getEntityManager().createQuery("SELECT z FROM Fahrt AS z");
        LOGGER.info(query.toString());

        return (Collection<Fahrt>) query.getResultList();
    }

    /**
     * Methode liefert alle Fahrten zum übergebenen Kalenderjahr
     */
    public Collection<Fahrt> selectFahrten(int jahr) {

        StringBuilder sb = new StringBuilder("SELECT f FROM Fahrt AS f where f.datum like '");
        sb.append(String.valueOf(jahr));
        sb.append("%'");

        Query query = getEntityManager().createQuery(sb.toString());
        LOGGER.info(query.toString());

        Collection<Fahrt> fahrten = query.getResultList();

        return fahrten;
    }

    /**
     * Methode liefert sämtliche Häuser
     */
    public Collection<Haus> selectAllHaeuser() {

        Query query = getEntityManager().createQuery("SELECT h FROM Haus AS h");
        LOGGER.info(query.toString());

        Collection<Haus> haeuser = query.getResultList();

        return haeuser;
    }

    /**
     * Methode liefert sämtliche Adressen
     */
    public Collection<Adresse> selectAllAdressen() {

        Query query = getEntityManager().createQuery("SELECT a FROM Adresse AS a");
        LOGGER.info(query.toString());

        Collection<Adresse> adressen = query.getResultList();

        return adressen;
    }

    /**
     * Methode liefert alle im Monat angefallenen Ausgaben
     * Monatsindizes beginnen bei 0
     */
    public Collection<Ausgabe> selectAusgaben(int jahr, int monat) {

        Calendar cal = new GregorianCalendar(jahr, monat, 1);
        cal.add(Calendar.DATE, -1);
        Date anfangMonat = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date endeMonat = cal.getTime();

        String queryStr = "SELECT a FROM Ausgabe AS a where a.datum >= :monatBeginn and a.datum <= :monatEnde";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("monatEnde", endeMonat);
        query.setParameter("monatBeginn", anfangMonat);
        LOGGER.info(query.toString());

        Collection<Ausgabe> ausgaben = query.getResultList();

        return ausgaben;
    }

    /**
     * Methode liefert alle Buchungen zum Monat, in dem das übergebene Datum liegt
     */
    public Collection<Buchung> selectBuchungen(Date datum) {

        String queryStr = "SELECT b FROM Buchung AS b WHERE b.datum >= :von AND b.datum <= :bis";

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(datum);
        int year = calendar.get(Calendar.YEAR);

        Calendar vonCal = new GregorianCalendar(year, 0, 1);
        Calendar bisCal = new GregorianCalendar(year, 11, 31);

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("von", vonCal.getTime());
        query.setParameter("bis", bisCal.getTime());

        LOGGER.info(query.toString());

        Collection<Buchung> buchungen = query.getResultList();

        return buchungen;
    }

    public Collection<Buchung> selectBuchungen(Collection<Bedingung> bedingungen, String name, String vorname) {

        Collection<Buchung> buchungsColl = null;

        String nameRegex = "*" + name + "*";
        Bedingung bedName = new Bedingung(Kriterium.EMPFAENGER, Operator.GLEICH, nameRegex);

        String vornameRegex = "*" + vorname + "*";
        Bedingung bedVorname = new Bedingung(Kriterium.EMPFAENGER, Operator.GLEICH, vornameRegex);

        String initialeRegex = "*" + vorname.substring(0, 1) + "*";
        Bedingung bedInitiale = new Bedingung(Kriterium.EMPFAENGER, Operator.GLEICH, initialeRegex);

        // Suche mit Name + Vorname
        bedingungen.add(bedName);
        bedingungen.add(bedVorname);
        buchungsColl = selectBuchungen(bedingungen);

        // Suche mit Name + 1. Buchstaben des Vornamens
        if (buchungsColl.isEmpty()) {
            bedingungen.remove(bedVorname);
            bedingungen.add(bedInitiale);
            buchungsColl = selectBuchungen(bedingungen);
        }

        // Suche mit nur mit Nachnamen
        if (buchungsColl.isEmpty()) {
            bedingungen.remove(bedInitiale);
            buchungsColl = selectBuchungen(bedingungen);
        }

        return buchungsColl;
    }

    /**
     * Methode liefert alle Entfernungen zwischen zwei Zielen
     */
    public Collection<Entfernung> selectEntfernungen() {

        StringBuilder sb = new StringBuilder("SELECT e FROM Entfernung AS e");

        Query query = getEntityManager().createQuery(sb.toString());
        LOGGER.info(query.toString());

        Collection<Entfernung> entfernungen = query.getResultList();

        return entfernungen;
    }

    /**
     * Methode liefert alle Wohnungen
     */
    public Collection<Wohnung> selectAllWohnungen() {

        StringBuilder sb = new StringBuilder("SELECT w FROM Wohnung AS w");

        Query query = getEntityManager().createQuery(sb.toString());

        Collection<Wohnung> wohnungen = query.getResultList();

        return wohnungen;
    }


    /**
     * Methode liefert alle Wohnungen zu vorgegebenem Haus
     */
    public Collection<Wohnung> selectWohnungen(Haus haus) {

        StringBuilder sb = new StringBuilder("SELECT w FROM Wohnung AS w WHERE w.haus = :haus");

        Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("haus", haus);
        LOGGER.info(query.toString());

        Collection<Wohnung> wohnungen = query.getResultList();

        return wohnungen;
    }

    /**
     * Methode liefert alle Zimmer zu vorgegebenem Wohnung
     */
    public Collection<Zimmer> selectZimmer(Wohnung wohnung) {

        StringBuilder sb = new StringBuilder("SELECT z FROM Zimmer AS z WHERE z.wohnung = :wohnung");

        Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("wohnung", wohnung);
        LOGGER.info(query.toString());

        Collection<Zimmer> zimmerColl = query.getResultList();

        return zimmerColl;
    }

    /**
     * Methode liefert alle Ausgabenarten
     */
    public Collection<AusgabeArt> selectAusgabenArten() {

        StringBuilder sb = new StringBuilder("SELECT a FROM Ausgabenart AS a");

        Query query = getEntityManager().createQuery(sb.toString());
        LOGGER.info(query.toString());

        Collection<AusgabeArt> ausgabearten = query.getResultList();

        return ausgabearten;
    }

    /**
     * Methode löscht alle Ausgaben zum Jahr des übergebenen Datums
     */
    public void deleteAusgaben(Date datum, Haus haus) {

        String queryStr = "DELETE FROM Ausgabe AS a WHERE a.datum >= :von AND a.datum <= :bis AND a.haus = :haus";

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(datum);
        int year = calendar.get(Calendar.YEAR);

        Calendar vonCal = new GregorianCalendar(year, 0, 1);
        Calendar bisCal = new GregorianCalendar(year, 11, 31);

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("von", vonCal.getTime());
        query.setParameter("bis", bisCal.getTime());
        query.setParameter("haus", haus);

        HausJpaPersistence persistence = HausJpaPersistence.getInstance();
        persistence.getEntityManager().getTransaction().begin();

        int anzRows = query.executeUpdate();

        persistence.getEntityManager().getTransaction().commit();

        LOGGER.info(query.toString());
    }

    /**
     * Methode liefert alle Mietverträge zur übergebenen Wohnung
     */
    public Collection<MietVertrag> selectMietvertraege(Wohnung wohnung) {

        String queryStr = "SELECT v FROM MietVertrag AS v where v.wohnung = :wohnung";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("wohnung", wohnung);
        LOGGER.info(query.toString());

        Collection<MietVertrag> vertraege = query.getResultList();

        return vertraege;
    }

    /**
     * Methode liefert die Mietzahlung zur übergebenen Buchung
     */
    public Collection<Mietzahlung> selectMietzahlung(Buchung buchung) {

        //String queryStr = "SELECT m FROM Mietzahlung AS m where m.buchung = :buchung and m.zahlungsKategorie = :kategorie";
        String queryStr = "SELECT m FROM Mietzahlung AS m where m.buchung = :buchung";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("buchung", buchung);
        // query.setParameter("kategorie", ZahlungsKategorie.MIETZINS);
        LOGGER.info(query.toString());

        Collection<Mietzahlung> mietzahlungen = query.getResultList();

        return mietzahlungen;
    }

    /**
     * Methode liefert die Mietzahlungen zum übergebenen Mietvertrag ab übergebenem Datum
     */
    public Collection<Mietzahlung> selectMietzahlungen(MietVertrag mietvertrag, String datumStr) {
        int jahr = Integer.valueOf(datumStr.substring(0, 4));
        int monat = Integer.valueOf(datumStr.substring(5, 7)) - 1;
        int tag = Integer.valueOf(datumStr.substring(8));
        Calendar cal = new GregorianCalendar(jahr, monat, tag);
        Date datum = cal.getTime();

        String queryStr = "SELECT m FROM Mietzahlung AS m where m.mietVertrag = :mietvertrag and m.buchung.datum > :datum and m.zahlungsKategorie = :kategorie";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("mietvertrag", mietvertrag);
        query.setParameter("datum", datum);
        query.setParameter("kategorie", ZahlungsKategorie.MIETZINS);
        LOGGER.info(query.toString());

        Collection<Mietzahlung> mietzahlungen = query.getResultList();

        return mietzahlungen;
    }

    /**
     * Methode liefert die Mietzahlungen zum übergebenen Jahr
     */
    public Collection<Mietzahlung> selectMietzahlungen(int jahr) {

        String queryStr = "SELECT m FROM Mietzahlung AS m where m.jahr = :jahr and m.zahlungsKategorie = :kategorie";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("jahr", jahr);
        query.setParameter("kategorie", ZahlungsKategorie.MIETZINS);
        LOGGER.info(query.toString());

        Collection<Mietzahlung> mietzahlungen = query.getResultList();

        return mietzahlungen;
    }

    /**
     * Methode liefert die Kautionszahlungen zum übergebenen Vertrag
     */
    public Collection<Mietzahlung> selectKautionszahlungen(MietVertrag vertrag) {

        String queryStr = "SELECT m FROM Mietzahlung AS m where m.mietVertrag = :vertrag and m.zahlungsKategorie = :kategorie";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("vertrag", vertrag);
        query.setParameter("kategorie", ZahlungsKategorie.KAUTION);
        LOGGER.info(query.toString());

        return query.getResultList();
    }

    /**
     * Methode liefert alle Kautionszahlungen
     */
    public Collection<Mietzahlung> selectKautionszahlungen() {

        String queryStr = "SELECT m FROM Mietzahlung AS m where m.zahlungsKategorie = :kategorie";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("kategorie", ZahlungsKategorie.KAUTION);
        LOGGER.info(query.toString());

        return query.getResultList();
    }

    /**
     * Methode liefert alle in der Spanne der übergebenen Monate gültigen Mietverträge
     * Monatsindizes beginnen bei 0
     */
    public Collection<MietVertrag> selectAllMietvertraege() {

        String queryStr = "SELECT v FROM MietVertrag AS v";

        Query query = getEntityManager().createQuery(queryStr);
        LOGGER.info(query.toString());

        Collection<MietVertrag> vertraege = query.getResultList();

        return vertraege;
    }

    /**
     * Methode liefert alle im Monat gültigen Mietverträge
     * Monatsindizes beginnen bei 0
     */
    public Collection<MietVertrag> selectMietvertraege(int jahr, int monat) {

        Calendar cal = new GregorianCalendar(jahr, monat, 1);
        cal.add(Calendar.DATE, -1);
        Date anfangMonat = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date endeMonat = cal.getTime();

        String queryStr = "SELECT v FROM MietVertrag AS v where v.beginn < :monatEnde and (v.ende is null or v.ende > :monatBeginn)";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("monatEnde", endeMonat);
        query.setParameter("monatBeginn", anfangMonat);
        LOGGER.info(query.toString());

        Collection<MietVertrag> vertraege = query.getResultList();

        return vertraege;
    }

    /**
     * Methode liefert alle in der Spanne der übergebenen Monate gültigen Mietverträge
     * Monatsindizes beginnen bei 0
     */
    public Collection<MietVertrag> selectMietvertraege(int jahr, int monatVon, int monatBis) {

        Calendar cal = new GregorianCalendar(jahr, monatVon - 1, 1);
        cal.add(Calendar.DATE, -1);
        Date anfangMonat = cal.getTime();
        cal = new GregorianCalendar(jahr, monatBis - 1, 27);
        cal.add(Calendar.MONTH, 1);
        Date endeMonat = cal.getTime();

        String queryStr = "SELECT v FROM MietVertrag AS v where v.beginn < :monatEnde and (v.ende is null or v.ende > :monatBeginn)";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("monatEnde", endeMonat);
        query.setParameter("monatBeginn", anfangMonat);
        LOGGER.info(query.toString());

        Collection<MietVertrag> vertraege = query.getResultList();

        return vertraege;
    }

    /**
     * Aufruf der für das Objekt zuständigen Methode
     * @param object einzufügendes Objekt
     * @return true, wenn ok, sonst false
     */
    public boolean insertEntity(Object object) {
        getEntityManager().persist(object);
        return true;
    }

    /**
     * Aufruf der für das Objekt zuständigen Methode
     * @param object zu löschendes Objekt
     * @return true, wenn ok, sonst false
     */
    public boolean deleteEntity(Object object) {
        getEntityManager().remove(object);
        return true;
    }

    /**
     * Aufruf der für das Objekt zuständigen Methode
     * @param object zu änderndes Objekt
     * @return true, wenn ok, sonst false
     */
    public boolean updateEntity(Object object) {
        return true;
    }

    /**
     * Bereinigen des Caches
     */
    public void clearCache() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    public Collection<Buchung> selectBuchungen(Collection<Bedingung> bedingungen) {
        Object[] parameter = new Object[6];
        int index = 0;
        String queryStr = "SELECT b FROM Buchung AS b ";
        String konjunktion = " WHERE ";

        for (Bedingung bedingung: bedingungen) {
            queryStr += konjunktion + verarbeiteBuchungsBedingung(bedingung, String.valueOf(++index));
            parameter[index] = uebernimmBuchungsParameter(bedingung);
            konjunktion = " AND ";
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        for (int i = 1; i <= index; i++) {
            query.setParameter(i, parameter[i]);
            LOGGER.info("Parameter " + i + ": " + parameter[i].toString());
        }
        Collection<Buchung> buchungen = query.getResultList();
        LOGGER.info("Statement: " + queryStr);

        return buchungen;
    }


    private String verarbeiteBuchungsBedingung(Bedingung bedingung, String index) {
        StringBuilder sb = new StringBuilder();

        switch (bedingung.getKriterium()) {
            case HAUS: sb.append("b.haus"); break;
            case DATUM: sb.append("b.datum"); break;
            case EMPFAENGER: sb.append("b.empfaenger"); break;
            case VERWENDUNG: sb.append("b.verwendung"); break;
            case KATEGORIE: sb.append("b.kategorie"); break;
            case BETRAG: sb.append("b.betrag"); break;
        }

        switch (bedingung.getOperator()) {
            case GLEICH: sb.append(" = ?" + index); break;
            case GROESSER_GLEICH: sb.append(" >= ?" + index); break;
            case GROESSER: sb.append(" > ?" + index); break;
            case KLEINER: sb.append(" < ?" + index); break;
            case KLEINER_GLEICH: sb.append(" <= ?" + index); break;
            case REGEX: sb.append(" REGEXP ?" + index); break;
        }

        return sb.toString();
    }


    private Object uebernimmBuchungsParameter(Bedingung bedingung) {

        switch (bedingung.getKriterium()) {
            case DATUM:
                String datumStr = (String)bedingung.getArgument();
                String jahrStr = datumStr.length() > 3 ? datumStr.substring(0, 4) : "1900";
                String monatStr = datumStr.length() > 6 ? datumStr.substring(5, 7) : "01";
                String tagStr = datumStr.length() > 8 ? datumStr.substring(8) : "01";
                Calendar cal = new GregorianCalendar();
                cal.set(Integer.valueOf(jahrStr), Integer.valueOf(monatStr) - 1, Integer.valueOf(tagStr), 0, 0, 0);
                return cal.getTime();
            case KATEGORIE:
                String kategorieStr = (String)bedingung.getArgument();
                return kategorieStr;
            case BETRAG:
                String betragStr = (String)bedingung.getArgument();
                betragStr = betragStr.replace(',','.');
                return Double.valueOf(betragStr);
        }

        return bedingung.getArgument();
    }

    public Collection<AusgabePosition> selectAusgabePositionen(Collection<Bedingung> bedingungen) {
        Object[] parameter = new Object[6];
        int index = 0;
        String queryStr = "SELECT a FROM AusgabePosition AS a ";
        String konjunktion = " WHERE ";

        for (Bedingung bedingung: bedingungen) {
            queryStr += konjunktion + verarbeiteAusgabeBedingung(bedingung, String.valueOf(++index));
            parameter[index] = uebernimmAusgabeParameter(bedingung);
            konjunktion = " AND ";
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        for (int i = 1; i <= index; i++) {
            query.setParameter(i, parameter[i]);
            LOGGER.info("Parameter " + i + ": " + parameter[i].toString());
        }
        Collection<AusgabePosition> ausgabenpositionen = query.getResultList();
        LOGGER.info("Statement: " + queryStr);

        return ausgabenpositionen;
    }

    /**
     * Methode liefert alle Versorgungsverträge zu vorgegebenem Haus
     */
    public Collection<VersorgungsVertrag> selectVersorgungsVertraege(Haus haus) {

        StringBuilder sb = new StringBuilder("SELECT v FROM Versorgungsvertrag AS v WHERE v.haus = :haus");

        Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("haus", haus);
        LOGGER.info(query.toString());

        Collection<VersorgungsVertrag> vertraege = query.getResultList();

        return vertraege;
    }


    private String verarbeiteAusgabeBedingung(Bedingung bedingung, String index) {
        StringBuilder sb = new StringBuilder();

        switch (bedingung.getKriterium()) {
            case HAUS: sb.append("a.ausgabe.haus"); break;
            case DATUM: sb.append("a.ausgabe.datum"); break;
            case EMPFAENGER: sb.append("a.ausgabe.empfaenger"); break;
            case VERWENDUNG: sb.append("a.beschreibung"); break;
            case KATEGORIE: sb.append("a.ausgabeArt"); break;
            case BETRAG: sb.append("a.betrag"); break;
        }

        switch (bedingung.getOperator()) {
            case GLEICH: sb.append(" = ?" + index); break;
            case GROESSER_GLEICH: sb.append(" >= ?" + index); break;
            case GROESSER: sb.append(" > ?" + index); break;
            case KLEINER: sb.append(" < ?" + index); break;
            case KLEINER_GLEICH: sb.append(" <= ?" + index); break;
            case REGEX: sb.append(" REGEXP ?" + index); break;
        }

        return sb.toString();
    }


    private Object uebernimmAusgabeParameter(Bedingung bedingung) {

        switch (bedingung.getKriterium()) {
            case DATUM:
                String datumStr = (String)bedingung.getArgument();
                String jahrStr = datumStr.length() > 3 ? datumStr.substring(0, 4) : "1900";
                String monatStr = datumStr.length() > 6 ? datumStr.substring(5, 7) : "01";
                String tagStr = datumStr.length() > 8 ? datumStr.substring(8) : "01";
                Calendar cal = new GregorianCalendar();
                cal.set(Integer.valueOf(jahrStr), Integer.valueOf(monatStr) - 1, Integer.valueOf(tagStr));
                return cal.getTime();
            case KATEGORIE:
                String kategorieStr = (String)bedingung.getArgument();
                return AusgabeArt.getByBeschreibung(kategorieStr);
            case BETRAG:
                String betragStr = (String)bedingung.getArgument();
                betragStr = betragStr.replace(',','.');
                return Double.valueOf(betragStr);
        }

        return bedingung.getArgument();
    }

    public Collection<Person> selectAllPersonen() {
        Query query = getEntityManager().createQuery("SELECT p FROM Person AS p");
        LOGGER.info(query.toString());

        return (Collection<Person>) query.getResultList();
    }

    public Collection<Person> selectAllPersonen(Boolean aktiv) {
        Query query = getEntityManager().createQuery("SELECT p FROM Person AS p where p.aktiv = :aktiv");
        query.setParameter("aktiv", aktiv);
        LOGGER.info(query.toString());

        return (Collection<Person>) query.getResultList();
    }

    public Collection<Person> selectPerson(String name, String vorname) {
        Query query = getEntityManager().createQuery("SELECT p FROM Person AS p where p.name = :name and p.vorname = :vorname");
        query.setParameter("name", name);
        query.setParameter("vorname", vorname);
        LOGGER.info(query.toString());

        return (Collection<Person>) query.getResultList();
    }

    public Collection<Person> selectPerson(String nameRegex) {
        Query query = getEntityManager().createQuery("SELECT p FROM Person AS p where p.name REGEXP :name");
        query.setParameter("name", nameRegex);
        LOGGER.info(query.toString());

        return (Collection<Person>) query.getResultList();
    }
}
