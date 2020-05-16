package de.ubo.fx.fahrten.business;

import de.ubo.fx.fahrten.persistence.Persistable;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ulric on 15.10.2016.
 */
@Entity(name = "Person")
@Table(name = "bode_haus.person")
public class Person implements Persistable, Cloneable {

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static String[] TELEFON_LEER = {" ", " "};

    @Basic
    @Column(name = "per_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Basic
    @Column(name = "per_name")
    private String name;

    @Basic
    @Column(name = "per_vorname")
    private String vorname;

    @Basic
    @Column(name = "per_anrede")
    private String anrede;

    @Basic
    @Column(name = "per_titel")
    private String titel;

    @Basic
    @Column(name = "per_telefon_privat")
    private String telefonPrivat;

    @Basic
    @Column(name = "per_telefon_dienst")
    private String telefonDienst;

    @Basic
    @Column(name = "per_handy_privat")
    private String handyPrivat;

    @Basic
    @Column(name = "per_handy_dienst")
    private String handyDienst;

    @Basic
    @Column(name = "per_fax_privat")
    private String faxPrivat;

    @Basic
    @Column(name = "per_fax_dienst")
    private String faxDienst;

    @Basic
    @Column(name = "per_bemerkung")
    private String bemerkung;

    @Basic
    @Column(name = "per_email")
    private String email;

    @Basic
    @Column(name = "per_geburtstag")
    private Date geburtstag;

    @Basic
    @Column(name = "per_aktiv")
    private boolean aktiv;

    @ManyToOne
    @JoinColumn(name = "per_adr_id")
    private Adresse adresse;

    public static String[] splitTelefon(String telefon) {
        if (telefon == null) return TELEFON_LEER;
        String[] result = telefon.split("[-/]");
        if (result.length > 1) {
            return result;
        } else {
            return TELEFON_LEER;
        }
    }

    public Date getGeburtstag() {
        return geburtstag;
    }

    public void setGeburtstag(Date geburtstag) {
        this.geburtstag = geburtstag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getTelefonPrivat() {
        return telefonPrivat;
    }

    public void setTelefonPrivat(String telefonPrivat) {
        this.telefonPrivat = telefonPrivat;
    }

    public String getTelefonDienst() {
        return telefonDienst;
    }

    public void setTelefonDienst(String telefonDienst) {
        this.telefonDienst = telefonDienst;
    }

    public String getHandyPrivat() {
        return handyPrivat;
    }

    public void setHandyPrivat(String handyPrivat) {
        this.handyPrivat = handyPrivat;
    }

    public String getHandyDienst() {
        return handyDienst;
    }

    public void setHandyDienst(String handyDienst) {
        this.handyDienst = handyDienst;
    }

    public String getFaxPrivat() {
        return faxPrivat;
    }

    public void setFaxPrivat(String faxPrivat) {
        this.faxPrivat = faxPrivat;
    }

    public String getFaxDienst() {
        return faxDienst;
    }

    public void setFaxDienst(String faxDienst) {
        this.faxDienst = faxDienst;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getGeburtstagFormatiert() {
        return getGeburtstag() == null ? "--.--.----" : DATE_FORMAT.format(getGeburtstag());
    }

    public boolean isVollstaendig() {
        if (getName() == null || getName().isEmpty()) return false;
        if (getVorname() == null || getVorname().isEmpty()) return false;
        if (getAdresse() == null) return false;
        if (getAdresse().getStrasse() == null ||
                getAdresse().getStrasse().isEmpty() ||
                getAdresse().getOrt() == null ||
                getAdresse().getOrt().isEmpty()) return false;
        return true;
    };

    @Override
    public Person clone() {
        Person clone = new Person();
        clone.setName(getName());
        clone.setVorname(getVorname());
        clone.setAnrede(getAnrede());
        clone.setTitel(getTitel());
        clone.setId(getId());
        clone.setAdresse(getAdresse());
        clone.setGeburtstag(getGeburtstag());
        clone.setBemerkung(getBemerkung());
        clone.setEmail(getEmail());
        clone.setAktiv(isAktiv());
        clone.setFaxDienst(getFaxDienst());
        clone.setFaxPrivat(getFaxPrivat());
        clone.setTelefonDienst(getTelefonDienst());
        clone.setTelefonPrivat(getTelefonPrivat());
        clone.setHandyDienst(getHandyDienst());
        clone.setHandyPrivat(getHandyPrivat());
        return clone;
    }

    public Person() {
        super();
        this.setName("");
        this.setVorname("");
        this.setAnrede("");
        this.setTitel(null);
        this.setId(null);
        this.setAdresse(null);
        this.setGeburtstag(null);
        this.setBemerkung(null);
        this.setEmail(null);
        this.setAktiv(true);
        this.setFaxDienst(null);
        this.setFaxPrivat(null);
        this.setTelefonDienst(null);
        this.setTelefonPrivat(null);
        this.setHandyDienst(null);
        this.setHandyPrivat(null);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Person)) return false;
        Person otherPerson = (Person) other;
        if (!otherPerson.getName().equals(this.getName())) return false;
        if (!otherPerson.getVorname().equals(this.getVorname())) return false;
        if (!otherPerson.getAnrede().equals(this.getAnrede())) return false;
        if (!otherPerson.getAdresse().equals(this.getAdresse())) return false;
        if (otherPerson.getGeburtstag() != null &&
                this.getGeburtstag() != null &&
                !otherPerson.getGeburtstag().equals(this.getGeburtstag())) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(getName());
        stringBuffer.append(", ");
        stringBuffer.append(getVorname());
        return stringBuffer.toString();
    }

}
