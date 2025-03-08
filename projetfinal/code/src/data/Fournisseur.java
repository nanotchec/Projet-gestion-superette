package data;

import java.util.HashMap;

public class Fournisseur implements IData {
    private String siret;
    private String nomSociete;
    private String adresse;
    private String email;
    private int actif; // 1 = actif, 0 = inactif

    private String values;
    private HashMap<String, fieldType> map;

    public Fournisseur(String siret, String nomSociete, String adresse, String email, boolean actif) {
      
        this.siret = (siret != null) ? siret : "";
        this.nomSociete = (nomSociete != null) ? nomSociete : "";
        this.adresse = (adresse != null) ? adresse : "";
        this.email = (email != null) ? email : "";
        this.actif = actif ? 1 : 0;
        getStruct();
        getValues();
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        if (siret == null) siret = "";
        this.siret = siret;
    }

    public String getNomSociete() {
        return nomSociete;
    }

    public void setNomSociete(String nomSociete) {
        if (nomSociete == null) nomSociete = "";
        this.nomSociete = nomSociete;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        if (adresse == null) adresse = "";
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) email = "";
        this.email = email;
    }

    public boolean isActif() {
        return actif == 1;
    }

    public void setActif(boolean actif) {
        this.actif = actif ? 1 : 0;
    }

    @Override
    public void getStruct() {
        map = new HashMap<>();
        map.put("siret", fieldType.VARCHAR);
        map.put("nomsociete", fieldType.VARCHAR);
        map.put("adresse", fieldType.VARCHAR);
        map.put("email", fieldType.VARCHAR);
        map.put("actif", fieldType.INT4);
    }

    @Override
    public String getValues() {
        String safeSiret = siret.replace("'", "''");
        String safeNomSoc = nomSociete.replace("'", "''");
        String safeAdresse = adresse.replace("'", "''");
        String safeEmail = email.replace("'", "''");
        values = "('" + safeSiret + "','" + safeNomSoc + "','" + safeAdresse + "','" + safeEmail + "'," + actif + ")";
        return values;
    }

    @Override
    public HashMap<String, fieldType> getMap() {
        return map;
    }

    @Override
    public boolean check(HashMap<String, fieldType> tableStruct) {
        return map.equals(tableStruct);
    }

    @Override
    public HashMap<String, Object> getFieldValues() {
        HashMap<String, Object> fieldValues = new HashMap<>();
        fieldValues.put("siret", siret);
        fieldValues.put("nomsociete", nomSociete);
        fieldValues.put("adresse", adresse);
        fieldValues.put("email", email);
        fieldValues.put("actif", actif);
        return fieldValues;
    }

    @Override
    public String toString() {
        return "Fournisseur [siret=" + siret + ", nomSociete=" + nomSociete + ", adresse=" + adresse 
               + ", email=" + email + ", actif=" + (actif==1) + "]";
    }
}