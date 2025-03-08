package data;

import java.util.HashMap;

public class Produit implements IData {
    private int idProduit;
    private int numLotAchat;
    private String nom;
    private String description;
    private String categorie;
    private double prixVente;
    private int actif;      // 1 = actif, 0 = inactif
    private int seuilMin;   // Seuil minimal de stock

    private String values;
    private HashMap<String, fieldType> map;

    // Constructeur complet
    public Produit(int idProduit, int numLotAchat, String nom, String description, 
                   String categorie, double prixVente, boolean actif, int seuilMin) {
        this.idProduit = idProduit;
        this.numLotAchat = numLotAchat;
        this.nom = (nom != null) ? nom : "";
        this.description = (description != null) ? description : "";
        this.categorie = (categorie != null) ? categorie : "";
        this.prixVente = prixVente;
        this.actif = actif ? 1 : 0;
        this.seuilMin = seuilMin;
        getStruct();
        getValues();
    }

    

    @Override
    public void getStruct() {
        map = new HashMap<>();
        map.put("idproduit", fieldType.NUMERIC);
        map.put("numlotachat", fieldType.NUMERIC);
        map.put("nom", fieldType.VARCHAR);
        map.put("description", fieldType.VARCHAR);
        map.put("categorie", fieldType.VARCHAR);
        map.put("prixvente", fieldType.FLOAT8);
        map.put("actif", fieldType.INT4);           
        map.put("seuil_min", fieldType.NUMERIC);    
    }

    @Override
    public String getValues() {
        String safeNom = nom.replace("'", "''");
        String safeDesc = description.replace("'", "''");
        String safeCat = categorie.replace("'", "''");
        values = "(" + idProduit + "," + numLotAchat + ",'" + safeNom 
                 + "','" + safeDesc + "','" + safeCat + "'," + prixVente 
                 + "," + actif + "," + seuilMin + ")";
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
        fieldValues.put("idproduit", idProduit);
        fieldValues.put("numlotachat", numLotAchat);
        fieldValues.put("nom", nom);
        fieldValues.put("description", description);
        fieldValues.put("categorie", categorie);
        fieldValues.put("prixvente", prixVente);
        fieldValues.put("actif", actif);
        fieldValues.put("seuil_min", seuilMin);
        return fieldValues;
    }

    @Override
    public String toString() {
        return "Produit [idProduit=" + idProduit + ", numLotAchat=" + numLotAchat 
               + ", nom=" + nom + ", description=" + description 
               + ", categorie=" + categorie + ", prixVente=" + prixVente
               + ", actif=" + (actif == 1) + ", seuilMin=" + seuilMin + "]";
    }

    // Getters et Setters
    public int getActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif ? 1 : 0; }

    public int getSeuilMin() { return seuilMin; }
    public void setSeuilMin(int seuilMin) { this.seuilMin = seuilMin; }

  
    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public int getNumLotAchat() { return numLotAchat; }
    public void setNumLotAchat(int numLotAchat) { this.numLotAchat = numLotAchat; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getPrixVente() { return prixVente; }
    public void setPrixVente(double prixVente) { this.prixVente = prixVente; }
}