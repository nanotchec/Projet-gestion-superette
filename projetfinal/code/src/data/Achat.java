package data;

import java.util.HashMap;

public class Achat implements IData {
    private int numLot;
    private int quant;
    private String dateAchat;     
    private String dateLimitC;    
    private int idContrat;
    private double prixUnitaire;

    private HashMap<String, fieldType> map;
    private String values;

    public Achat(int numLot, int quant, String dateAchat, String dateLimitC, int idContrat, double prixUnitaire) {
        this.numLot = numLot;
        this.quant = quant;
        this.dateAchat = (dateAchat != null) ? dateAchat.trim() : "";
        this.dateLimitC = (dateLimitC != null) ? dateLimitC.trim() : "";
        this.idContrat = idContrat;
        this.prixUnitaire = prixUnitaire;
        getStruct();
    }

    @Override
    public void getStruct() {
        map = new HashMap<>();
        map.put("num_lot", fieldType.NUMERIC);
        map.put("quant", fieldType.NUMERIC);
        map.put("dateachat", fieldType.DATE);  
        map.put("datelimitc", fieldType.DATE); 
        map.put("id_contrat", fieldType.NUMERIC);
        map.put("prixunitaire", fieldType.FLOAT8);
    }

    @Override
    public String getValues() {
        String safeDateA = dateAchat.replace("'", "''");
        String safeDateL = dateLimitC.replace("'", "''");
        values = "(" + numLot + "," + quant + ",'" + safeDateA + "','" + safeDateL + "'," + idContrat + "," + prixUnitaire + ")";
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
        fieldValues.put("num_lot", numLot);
        fieldValues.put("quant", quant);
        fieldValues.put("dateachat", dateAchat);
        fieldValues.put("datelimitc", dateLimitC);
        fieldValues.put("id_contrat", idContrat);
        fieldValues.put("prixunitaire", prixUnitaire);
        return fieldValues;
    }

    @Override
    public String toString() {
        return "Achat [numLot=" + numLot + ", quant=" + quant + ", dateAchat=" + dateAchat 
               + ", dateLimitC=" + dateLimitC + ", idContrat=" + idContrat + ", prixUnitaire=" + prixUnitaire + "]";
    }

    // Getters et Setters 
    public int getNumLot() { return numLot; }
    public void setNumLot(int numLot) { this.numLot = numLot; }

    public int getQuant() { return quant; }
    public void setQuant(int quant) { this.quant = quant; }

    public String getDateAchat() { return dateAchat; }
    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }

    public String getDateLimitC() { return dateLimitC; }
    public void setDateLimitC(String dateLimitC) { this.dateLimitC = dateLimitC; }

    public int getIdContrat() { return idContrat; }
    public void setIdContrat(int idContrat) { this.idContrat = idContrat; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}