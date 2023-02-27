package de.ubo.fx.fahrten.helper;

/**
 * Created by ulric on 08.08.2016.
 */
public enum Operator {
    KLEINER("<"),
    KLEINER_GLEICH("<="),
    GLEICH("="),
    GROESSER_GLEICH(">="),
    GROESSER(">"),
    REGEX("REGEX"),
    KEIN_OPERATOR("")
    ;

    final String name;

    Operator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
