package de.ubo.fx.fahrten.helper;

/**
 * Created by ulric on 08.09.2016.
 */
public class Bedingung {
    private final Kriterium kriterium;
    private final Operator operator;
    private final Object argument;

    public Bedingung(Kriterium kriterium, Operator operator, Object argument) {
        this.kriterium = kriterium;

        if (argument instanceof String && ((String) argument).contains("*")) {
            this.argument = ((String) argument).replace("*", ".{0,}");
            this.operator = Operator.REGEX;
        } else {
            this.argument = argument;
            this.operator =operator;
        }

    }

    public Kriterium getKriterium() {
        return kriterium;
    }

    public Operator getOperator() {
        return operator;
    }

    public Object getArgument() {
        return argument;
    }

}
