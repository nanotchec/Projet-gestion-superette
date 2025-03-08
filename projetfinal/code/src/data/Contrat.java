package data;

import java.util.HashMap;

public class Contrat implements IData {
    private int idContrat;
    private String dated;       
    private String datef;       
    private double prix;
    private int quantmin;
    private int idProduit;
    private String siret;
    private int engageliv;      // 0 ou 1

    private HashMap<String, fieldType> map;
    private String values;

    public Contrat(int idContrat, String dated, String datef, double prix, int quantmin, int idProduit, String siret, int engageliv) {
        this.idContrat = idContrat;
        this.dated = (dated != null) ? dated.trim() : "";
        this.datef = (datef != null) ? datef.trim() : "";
        this.prix = prix;
        this.quantmin = quantmin;
        this.idProduit = idProduit;
        this.siret = (siret != null) ? siret.trim() : "";
        this.engageliv = engageliv;
        getStruct();
        
    }

    @Override
    public void getStruct() {
        map = new HashMap<>();
        map.put("idcontrat", fieldType.NUMERIC);
        map.put("dated", fieldType.DATE);  
        map.put("datef", fieldType.DATE);   
        map.put("prix", fieldType.FLOAT8);
        map.put("quantmin", fieldType.NUMERIC);
        map.put("id_produit", fieldType.NUMERIC);
        map.put("siret", fieldType.VARCHAR);
        map.put("engageliv", fieldType.INT4);
    }

    @Override
    public String getValues() {
       
        String safeDated = dated.replace("'", "''");
        String safeDatef = datef.replace("'", "''");
        String safeSiret = siret.replace("'", "''");

        values = "(" + idContrat + ",'" + safeDated + "','" + safeDatef + "'," + prix + "," + quantmin + "," + idProduit + ",'" + safeSiret + "'," + engageliv + ")";
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
        fieldValues.put("idcontrat", idContrat);
        fieldValues.put("dated", dated);
        fieldValues.put("datef", datef);
        fieldValues.put("prix", prix);
        fieldValues.put("quantmin", quantmin);
        fieldValues.put("id_produit", idProduit);
        fieldValues.put("siret", siret);
        fieldValues.put("engageliv", engageliv);
        return fieldValues;
    }

    @Override
    public String toString() {
        return "Contrat [idContrat=" + idContrat + ", dated=" + dated + ", datef=" + datef + ", prix=" + prix
                + ", quantmin=" + quantmin + ", idProduit=" + idProduit + ", siret=" + siret + ", engageliv=" + engageliv + "]";
    }
}