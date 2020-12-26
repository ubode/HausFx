package de.ubo.fx.fahrten.helper;

import java.io.IOException;
import java.net.URL;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ulric on 11.11.2016.
 */
public class ResourceManager {

    /**
     * Created by ulric on 11.11.2016.
     */
    public static enum ResourceEnum {
        LAYOUT_CSS("resources//css//layout.css"),
        USER_MALE_PNG("resources//16x16//user.png"),
        USER_FEMALE_PNG("resources//16x16//user_female.png"),
        USER_INAKTIV_PNG("resources//16x16//user_inaktiv.png")
        ;
        String adresse;
        ResourceEnum(String adresse) {
            this.adresse = adresse;
        }
        public String getAdresse() {
            return adresse;
        }
    }

    private static ResourceManager instance = new ResourceManager();

    public static ResourceManager getInstance() {
        return instance;
    }

    public URL get(ResourceEnum resource) {
        URL url;
        try {
            Path path = Paths.get("./" + resource.getAdresse());
            String pathReal = path.toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
            url = new URL("file:///" + pathReal);
        } catch (IOException e) {
            url = this.getClass().getResource("..//" + resource.getAdresse());
        }
        System.out.println(url.toExternalForm());
        return url;
    }
}
