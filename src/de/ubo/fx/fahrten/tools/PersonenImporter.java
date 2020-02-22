package de.ubo.fx.fahrten.tools;

import de.ubo.fx.fahrten.business.Adresse;
import de.ubo.fx.fahrten.business.Person;
import de.ubo.fx.fahrten.persistence.HausDb4oPersistence;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ulric on 15.10.2016.
 */
public class PersonenImporter {

    public static void main(String[] args){
        HausDb4oPersistence db4oPersistence = HausDb4oPersistence.getInstance();
        HausJpaPersistence jpaPersistence = HausJpaPersistence.getInstance();
        UpdateManager<Person> updateManager = new UpdateManager<>(200);

        //Adressen in Map sammeln
        Collection<Adresse> adressen = jpaPersistence.selectAllAdressen();
        Map<String, Adresse> adressenMap = new HashMap<>(30);
        for (Adresse adresse: adressen) {
            adressenMap.put(adresse.getStrasse(), adresse);
        }

        List<de.ubo.haus.business.Person> db4OPersonen = db4oPersistence.getAllPersonen();

        for (de.ubo.haus.business.Person person: db4OPersonen) {
            String strasse = person.getAdresse().getStrasse();
            Adresse adresse = adressenMap.get(strasse);
            if (adresse != null) {
                Person personNeu = createPersonNeu(person, adresse);
                updateManager.addInsert(personNeu);
            } else {
                System.out.println("Adresse unbekannt für: " + person.getNachname() + ", " + person.getVorname());
            }
        }

        updateManager.saveUpdates();

        db4oPersistence.close();
        jpaPersistence.getEntityManager().close();

    }

    private static Person createPersonNeu(de.ubo.haus.business.Person person, Adresse adresse) {
        Person personNeu = new Person();
        personNeu.setAdresse(adresse);
        personNeu.setAnrede(person.getAnrede());
        personNeu.setBemerkung(person.getBemerkung());
        personNeu.setEmail(person.getEmail());
        personNeu.setFaxDienst(person.getFaxDienst());
        personNeu.setFaxPrivat(person.getFaxPrivat());
        personNeu.setHandyDienst(person.getHandyDienst());
        personNeu.setHandyPrivat(person.getHandyPrivat());
        personNeu.setName(person.getNachname());
        personNeu.setVorname(person.getVorname());
        personNeu.setTitel(person.getTitel());
        personNeu.setTelefonDienst(person.getTelefonDienst());
        personNeu.setTelefonPrivat(person.getTelefonPrivat());
        personNeu.setGeburtstag(person.getGeburtstag());
        personNeu.setAktiv(true);
        return personNeu;
    }
}
