package de.ubo.fx.fahrten.persistence;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;
import de.ubo.haus.base.HausProperties;
import de.ubo.haus.business.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

    public class HausDb4oPersistence {

        private static final HausDb4oPersistence HAUS_PERSISTENCE = new HausDb4oPersistence();

        private static final String HAUS_DB_FILENAME = HausProperties.getInstanz().getFileDB4O();

        private static ObjectContainer objectContainer = null;

        private Set<Object> txListe = new HashSet<>();

        private static final Logger LOGGER = Logger.getLogger(HausDb4oPersistence.class.getName());

        /**
         * @return ObjectContainer fuer die Hausverwaltung
         */
        public ObjectContainer getOC() {
            if (objectContainer == null) {
                EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
                configuration.common().objectClass("de.ubo.haus.business.Person").cascadeOnUpdate(true);
                configuration.common().allowVersionUpdates(true);
                objectContainer = Db4oEmbedded.openFile(configuration, HAUS_DB_FILENAME);

//            Db4o.configure().objectClass("de.ubo.haus.business.Person").cascadeOnUpdate(true);
//            Db4o.configure().allowVersionUpdates(true);
//            objectContainer = Db4o.openFile(HAUS_DB_FILENAME);

                LOGGER.info("db4o Open " + HAUS_DB_FILENAME + " OC: " + objectContainer.toString());
            }
            return objectContainer;
        }

        /**
         * Schliessen der DB
         */
        public void close() {
            getOC().close();
        }

        /**
         * Liefert die Persistenz fuer die Hausverwaltung
         * @return Returns the HAUS_PERSISTENCE.
         */
        public static HausDb4oPersistence getInstance() {
            return HAUS_PERSISTENCE;
        }

        /**
         * Liefert den File-Namen des DB-Files
         * @return Returns filename of the database-file.
         */
        public String getFileName() {
            return HAUS_DB_FILENAME;
        }

        /**
         * Liefert die liste aller Mandanten
         * @return Returns Liste der Mandanten.
         */
        @SuppressWarnings("unchecked")
        public List<Mandant> getAllMandanten() {
            List<Mandant> mandantenListe = new ArrayList<Mandant>();
            Query query = getOC().query();
            query.constrain(Mandant.class);
            ObjectSet result = query.execute();

            LOGGER.info("Mandanten result = " + result.getClass());

            for (Object mandant : result) {
                mandantenListe.add((Mandant) mandant);
            }

            return mandantenListe;
        }

        /**
         * Liefert die liste aller Objekte eines Mandanten
         * @return Returns Liste der Objekte eines Mandanten.
         */
        @SuppressWarnings("unchecked")
        public List<Objekt> getAllObjekte(Mandant mandant) {
            List<Objekt> objektListe = new ArrayList<Objekt>();
            Query query = getOC().query();
            query.constrain(Objekt.class);
            query.descend("mandant").constrain(mandant);
            ObjectSet result = query.execute();

            for (Object objekt : result) {
                objektListe.add((Objekt) objekt);
            }

            return objektListe;
        }

        /**
         * Liefert die liste aller BuchungsMerkmale zu einer Person
         * @return Returns Liste der Objekte eines Mandanten.
         */
        public List<BuchungsMerkmal> getBuchungsMerkmale(Person person) {
            List<BuchungsMerkmal> buchungsMerkmale = new ArrayList<BuchungsMerkmal>();
            Query query = getOC().query();
            query.constrain(BuchungsMerkmal.class);
            query.descend("person").constrain(person);
            ObjectSet result = query.execute();

            for (Object buchungsMerkmal : result) {
                buchungsMerkmale.add((BuchungsMerkmal) buchungsMerkmal);
            }

            return buchungsMerkmale;
        }

        /**
         * Speichert das uebergebene Objekt
         */
        public void saveObject(Object object) {
            getOC().store(object);
        }

        /**
         * Fuegt das uebergebene Objekt in die DB ein
         */
        public Person insertPerson(Person person) {
            getOC().store(person);

            //        final String nName = person.getNachname();
            //        final String vName = person.getVorname();
            //
            //        List<Person> list = getOC().query(new Predicate<Person>() {
            //            public boolean match(Person dbPerson) {
            //                return dbPerson.getNachname() == nName
            //                    && dbPerson.getVorname() == vName;
            //            }
            //        });
            //
            //        return list.get(0);

            return person;
        }

        /**
         * Löscht das übergebene Objekt aus der DB
         */
        public void deletePerson(Person person) {
            getOC().delete(person);

        }

        /**
         * Speichert alle Objekte der Transaktionsliste
         * und leert die Liste
         */
        public void commit() {
            for (Object object : txListe) {
                saveObject(object);
                txListe.remove(object);
            }
        }

        /**
         * Laedt alle Objekte der Transaktionsliste neu von der DB
         * und leert die Liste
         */
        public void rollback() {
            getOC().rollback();
            for (Object obj : txListe) {
                LOGGER.info("neu geladen: " + obj.toString());
                getOC().ext().refresh(obj, Integer.MAX_VALUE);
            }
            txListe.clear();
        }

        /**
         * Vermerkt ein Objekt in der Transaktionsliste
         */
        public void addTxObject(Object object) {
            txListe.add(object);
        }

        /**
         * Vermerkt ein Objekt in der Transaktionsliste
         */
        public boolean isDirty() {
            return txListe.size() > 0;
        }

        /**
         * Liefert die liste aller Personen
         * @return Returns Liste der Personen.
         */
        @SuppressWarnings("unchecked")
        public List<Person> getAllPersonen() {
            List<Person> personenListe = new ArrayList<Person>();

            Query query = getOC().query();
            query.constrain(Person.class);
            ObjectSet result = query.execute();

            for (Object person : result) {
                personenListe.add((Person) person);
            }

            return personenListe;
        }

        /**
         * Liefert die liste aller Notizen zu einer Person
         * @return Returns Liste der Objekte eines Mandanten.
         */
        @SuppressWarnings("unchecked")
        public List<Notiz> getNotizen(Person person) {
            List<Notiz> notizen = new ArrayList<Notiz>();
            Query query = getOC().query();
            query.constrain(Notiz.class);
            query.descend("person").constrain(person);
            ObjectSet result = query.execute();

            for (Object notiz : result) {
                notizen.add((Notiz) notiz);
            }

            //        int i = new Double(Math.random() * 10).intValue();
            //        for (int j = 1; j <= i; j++) {
            //            notizen.add(new Notiz(new Date(), "Dies ist die " + j + ". Beispielnotiz"));
            //        }

            return notizen;
        }
    }


