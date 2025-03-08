package data;

import java.util.HashMap;

public class Contact implements IData {
    private int idContact;
    private String nom;
    private String prenom;
    private String fonction;
    private String mail;
    private String tel;
    private String siret;

    private HashMap<String, fieldType> map;
    private String values;

    public Contact(int idContact, String nom, String prenom, String fonction, 
                   String mail, String tel, String siret) {
        this.idContact = idContact;
        this.nom = (nom != null) ? nom.trim() : "";
        this.prenom = (prenom != null) ? prenom.trim() : "";
        this.fonction = (fonction != null) ? fonction.trim() : "";
        this.mail = (mail != null) ? mail.trim() : "";
        this.tel = (tel != null) ? tel.trim() : "";
        this.siret = (siret != null) ? siret.trim() : "";
        getStruct();
    }

    @Override
    public void getStruct() {
        map = new HashMap<>();
        map.put("id_contact", fieldType.NUMERIC);
        map.put("nom", fieldType.VARCHAR);
        map.put("prenom", fieldType.VARCHAR);
        map.put("fonction", fieldType.VARCHAR);
        map.put("mail", fieldType.VARCHAR);
        map.put("tel", fieldType.VARCHAR);
        map.put("siret", fieldType.VARCHAR);
    }

    @Override
    public String getValues() {
        String safeNom = nom.replace("'", "''");
        String safePrenom = prenom.replace("'", "''");
        String safeFonc = fonction.replace("'", "''");
        String safeMail = mail.replace("'", "''");
        String safeTel = tel.replace("'", "''");
        String safeSiret = siret.replace("'", "''");
        values = "(" + idContact + ",'" + safeNom + "','" + safePrenom + "','" + safeFonc 
                 + "','" + safeMail + "','" + safeTel + "','" + safeSiret + "')";
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
        HashMap<String, Object> fv = new HashMap<>();
        fv.put("id_contact", idContact);
        fv.put("nom", nom);
        fv.put("prenom", prenom);
        fv.put("fonction", fonction);
        fv.put("mail", mail);
        fv.put("tel", tel);
        fv.put("siret", siret);
        return fv;
    }

    @Override
    public String toString() {
        return "Contact [idContact=" + idContact + ", nom=" + nom 
               + ", prenom=" + prenom + ", fonction=" + fonction
               + ", mail=" + mail + ", tel=" + tel
               + ", siret=" + siret + "]";
    }
}