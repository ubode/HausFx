package de.ubo.fx.fahrten.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by ulrichbode on 09.02.16.
 */
public class UpdateManager<T extends  Persistable> {
    private final static Logger LOGGER = Logger.getLogger(HausJpaPersistence.class.getName());
    private final static int MAX_UPDATES_PRO_TRANSAKTION = 50;
    private final Map<T, Change> updates;
    public UpdateManager(int capacity) {
        updates = new HashMap<>(capacity);
    }

    private Map<T, Change> getUpdates() {
        return updates;
    }

    /**
     * Liefert die vor dem Save zu prüfenden Updates
     * @return Collection der Inserts und Updates ohne Deletes
     */
    public Collection<T> updatesToBeChecked() {
        Collection<T> updates = new ArrayList<>(50);
        for (Map.Entry<T, Change> entry : getUpdates().entrySet()) {
            if (entry.getValue().equals(Change.INSERT) || entry.getValue().equals(Change.UPDATE)) {
                updates.add(entry.getKey());
            }
        }
        return updates;
    }

    /**
     * Registrieren des Einfügens des Objektes object
     * @param object eingefügtes Objekt
     */
    public void addInsert(T object) {
        if(getUpdates().get(object) == null) {
            getUpdates().put(object, Change.INSERT);
        }
    }

    /**
     * Markieren der Löschung des Objektes object
     * @param object gelöschtes Objekt
     */
    public void addDelete(T object) {

        Change change = getUpdates().get(object);

        if (change == null) {
            getUpdates().put(object, Change.DELETE);
        } else {
            switch (change) {
                case INSERT:
                    getUpdates().remove(object);
                    break;
                case UPDATE:
                    getUpdates().put(object, Change.DELETE);
                    break;
                case DELETE:
                /* das kann ja wohl nicht sein*/
                    break;
            }
        }
    }

    /**
     * Markieren der Änderung des Objektes object
     * @param object geändertes Objekt
     */
    public void addUpdate(T object) {

        Change change = getUpdates().get(object);

        if (change == null) {
            getUpdates().put(object, Change.UPDATE);
        } else {
            switch (change) {
                case INSERT:
                case UPDATE:
                    /* Objekt behält seinen Status */
                    break;
                case DELETE:
                    /* das kann ja wohl nicht sein*/
                    break;
            }
        }
    }

    public void saveUpdates() {

        LOGGER.info("save Updates");

        HausJpaPersistence persistence = HausJpaPersistence.getInstance();
        int updateZaehler = 1;
        persistence.getEntityManager().getTransaction().begin();

        for (Map.Entry<T, Change> entry : getUpdates().entrySet()) {
            T object = entry.getKey();
            switch (entry.getValue()) {
                case INSERT:
                    object.setId(null);
                    persistence.insertEntity(object);
                    break;
                case DELETE:
                    persistence.deleteEntity(object);
                    break;
                case UPDATE:
                    persistence.updateEntity(object);
                    break;
            }

            // Nach MAX_UPDATES_PRO_TRANSAKTION Transaktion committen und neue Transaktion beginnen
            if (++updateZaehler > MAX_UPDATES_PRO_TRANSAKTION) {
                LOGGER.info("Transaktion committen");
                persistence.getEntityManager().getTransaction().commit();
                persistence.getEntityManager().clear();
                persistence.getEntityManager().getTransaction().begin();
                updateZaehler = 1;
            }
        }

        persistence.getEntityManager().getTransaction().commit();
        // persistence.getEntityManager().clear();

        getUpdates().clear();

    }

    public void clear() {
        getUpdates().clear();
    }

    public boolean isEmpty() {
        return getUpdates().isEmpty();
    }

    protected enum Change {INSERT, DELETE, UPDATE};

}
