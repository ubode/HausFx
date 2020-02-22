/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package de.ubo.fx.fahrten.converter;

import javafx.util.StringConverter;

import java.text.DecimalFormat;

/**
 * <p>{@link StringConverter} implementation for {@link Double}
 * (and double primitive) values.</p>
 * @since JavaFX 2.1
 */
public class ExtendedDoubleStringConverter extends StringConverter<Double> {
    public static DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

    /** {@inheritDoc} */
    @Override public Double fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        value = value.replace(',', '.');

        return Double.valueOf(value);
    }

    /** {@inheritDoc} */
    @Override public String toString(Double value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return decimalFormat.format(value);
    }
}
