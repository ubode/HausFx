package de.ubo.fx.fahrten.converter;

import de.ubo.fx.fahrten.business.Wohnung;
import de.ubo.fx.fahrten.persistence.HausJpaPersistence;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ulric on 06.02.2017.
 */
public class WohnungStringConverter extends StringConverter<Wohnung> {
    private Map<String, Wohnung > wohnungMap;

    {
        wohnungMap = new HashMap<>(100);
        Collection<Wohnung> wohnungCol = HausJpaPersistence.getInstance().selectAllWohnungen();
        for (Wohnung wohnung: wohnungCol) {
            wohnungMap.put(wohnung.getNummer(), wohnung);
        }
    }

    /** {@inheritDoc} */
    @Override public Wohnung fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        return wohnungMap.get(value);
    }

    /** {@inheritDoc} */
    @Override public String toString(Wohnung value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return value.getNummer();
    }
}
