package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.HausJpaPersistence;

import java.util.Collection;
import java.util.Map;

/**
 * Created by ulric on 27.12.2016.
 */
public class PersonManager {
    private static PersonManager instance = new PersonManager();
    private Map<Ziel, Person> personen;

    public static PersonManager getInstance() {
        return instance;
    }

    public Person suchePerson(String ident) {
        Collection<Person> suchPersonen;

        if (ident.contains(",")) {
            String[] teile = ident.split(",");
            suchPersonen = HausJpaPersistence.getInstance().selectPerson(teile[0].trim(), teile[1].trim());
            if (suchPersonen.size() == 1) return suchPersonen.iterator().next();
        } else {
            suchPersonen = HausJpaPersistence.getInstance().selectPerson(ident.trim());
            if (suchPersonen.size() == 1) return suchPersonen.iterator().next();
        }

        return null;
    }
}
