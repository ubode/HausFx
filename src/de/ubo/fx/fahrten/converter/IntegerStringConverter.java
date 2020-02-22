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
 * <p>{@link StringConverter} implementation for {@link Integer}
 * (and int primitive) values.</p>
 */
public class IntegerStringConverter extends StringConverter<Integer> {
    public static DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

    /** {@inheritDoc} */
    @Override public Integer fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        return Integer.valueOf(value);
    }

    /** {@inheritDoc} */
    @Override public String toString(Integer value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }
}
