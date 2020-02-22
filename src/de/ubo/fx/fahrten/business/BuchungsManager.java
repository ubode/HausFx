package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import de.ubo.fx.fahrten.persistence.UpdateManager;

import java.util.*;

/**
 * Created by ulric on 06.08.2016.
 */
public class BuchungsManager {
    private static final BuchungsManager instance = new BuchungsManager();

    public static BuchungsManager instance(){
        return instance;
    }

    public Integer[] ermittleZuPerstistierendeBuchungen(Collection<Buchung> buchungen, UpdateManager<Buchung> updateManagaer){
        Map<String, Buchung> dbBuchungen = new HashMap<>(2000, 0.8f);
        int anzInsert = 0;
        int anzUpdate = 0;

        List<Buchung> buchungList = new ArrayList<>(buchungen);
        Collections.sort(buchungList);
        Buchung ersteBuchung = buchungList.get(0);

        Collection<Buchung> persBuchungCol =  HausJpaPersistence.getInstance().selectBuchungen(ersteBuchung.getDatum());
        for (Buchung buchung: persBuchungCol) {
            dbBuchungen.put(buchung.getBuchungsNummer(), buchung);
        }

        for (Buchung buchung: buchungen) {
            Buchung dbBuchung = dbBuchungen.get(buchung.getBuchungsNummer());
            if (dbBuchung == null) {
                updateManagaer.addInsert(buchung);
                anzInsert++;
            } else {
                String kategorie = buchung.getKategorie();
                String dbKategorie = dbBuchung.getKategorie();
                if (kategorie != null) {
                    if (dbKategorie == null || !dbKategorie.equals(kategorie)) {
                        dbBuchung.setKategorie(kategorie);
                        updateManagaer.addUpdate(dbBuchung);
                        anzUpdate++;
                    }
                }
            }
        }

        Integer[] zaehler = new Integer[] {anzInsert, anzUpdate};

        return zaehler;
    }
}
