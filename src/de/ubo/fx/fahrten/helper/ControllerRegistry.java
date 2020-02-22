package de.ubo.fx.fahrten.helper;

import de.ubo.fx.fahrten.gui.CloseRequestable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ulric on 09.03.2017.
 */
public class ControllerRegistry {
    private static ControllerRegistry ourInstance = new ControllerRegistry();
    private final Collection<CloseRequestable> contollerColl;

    private ControllerRegistry() {
        contollerColl = new ArrayList<>(20);
    }

    public static ControllerRegistry getInstance() {
        return ourInstance;
    }

    public Collection<CloseRequestable> getContollerColl() {
        return contollerColl;
    }

    public void add(CloseRequestable controller) {
        getContollerColl().add(controller);
    }

    public void closeControllers() {
        for (CloseRequestable controller : getContollerColl()) {
            controller.closeRequest();
        }
    }

}
