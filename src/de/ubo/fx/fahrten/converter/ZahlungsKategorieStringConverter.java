package de.ubo.fx.fahrten.converter;

import de.ubo.fx.fahrten.business.ZahlungsKategorie;
import javafx.util.StringConverter;

/**
 * Created by ulric on 06.02.2017.
 */
public class ZahlungsKategorieStringConverter extends StringConverter<ZahlungsKategorie> {
    /** {@inheritDoc} */
    @Override public ZahlungsKategorie fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        return ZahlungsKategorie.valueOf(value);
    }

    /** {@inheritDoc} */
    @Override public String toString(ZahlungsKategorie value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return value.getBeschreibung();
    }
}
