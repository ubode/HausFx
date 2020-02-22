package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.HausJpaPersistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ulrichbode on 26.04.16.
 */
public class EntfernungsManager {
    private static EntfernungsManager instance = new EntfernungsManager();
    private Map<Ziel, Map<Ziel, Entfernung>> entfernungen;

    {
        entfernungen = new HashMap<>();

        Collection<Entfernung> entfernungsColl = HausJpaPersistence.getInstance().selectEntfernungen();

        for (Entfernung entfernung: entfernungsColl) {

            Map<Ziel, Entfernung> entfernungsMap = getEntfernungen().get(entfernung.getStartZiel());

            if (entfernungsMap == null) {
                entfernungsMap = new HashMap<>();
                getEntfernungen().put(entfernung.getStartZiel(), entfernungsMap);
            }

            entfernungsMap.put(entfernung.getEndZiel(), entfernung);

        }
    }

    public Map<Ziel, Map<Ziel, Entfernung>> getEntfernungen() {
        return entfernungen;
    }

    public static EntfernungsManager instance() {
        return instance;
    }

    public int getEntfernung(Ziel vonZiel, Ziel bisZiel){

        Map<Ziel, Entfernung> entfernungsMap = getEntfernungen().get(vonZiel);

        if (entfernungsMap == null) return -1;

        Entfernung entfernung = entfernungsMap.get(bisZiel);

        if (entfernung == null) {
            return -1;
        } else {
            return entfernung.getEntfernung();
        }

    }
}
