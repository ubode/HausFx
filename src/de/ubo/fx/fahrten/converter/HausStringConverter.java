package de.ubo.fx.fahrten.converter;

import de.ubo.fx.fahrten.business.Haus;
import de.ubo.fx.fahrten.business.Wohnung;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ulric on 06.02.2017.
 */
public class HausStringConverter extends StringConverter<Haus> {
    private Map<String, Haus> hausMap;

    {
        hausMap = new HashMap<>(100);
        Collection<Haus> hausCol = HausJpaPersistence.getInstance().selectAllHaeuser();
        for (Haus haus: hausCol) {
            hausMap.put(haus.getKurzname(), haus);
        }
    }

    /** {@inheritDoc} */
    @Override public Haus fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        return hausMap.get(value);
    }

    /** {@inheritDoc} */
    @Override public String toString(Haus value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return value.getKurzname();
    }
}
