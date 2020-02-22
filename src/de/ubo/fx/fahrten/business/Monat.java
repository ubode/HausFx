package de.ubo.fx.fahrten.business;

/**
 * Created by ulric on 08.08.2016.
 */
public enum Monat {
    JANUAR(1, "Januar"),
    FEBRUAR(2, "Februar"),
    MAERZ(3, "März"),
    APRIL(4, "April"),
    MAI(5, "Mai"),
    JUNI(6, "Juni"),
    JULI(7, "Juli"),
    AUGUST(8, "August"),
    SEPTEMBER(9, "September"),
    OKTOBER(10, "Oktober"),
    NOVEMBER(11, "November"),
    DEZEMBER(12, "Dezember"),
    ;

    int index;
    String name;

    Monat(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

}

