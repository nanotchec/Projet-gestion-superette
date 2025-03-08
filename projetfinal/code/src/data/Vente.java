package data;

import java.util.HashMap;

public class Vente implements IData {
    private int numTicket;
    private int numLot;
    private String datev;  // format YYYY-MM-DD
    private double prixv;
    private int quant;

    private HashMap<String, fieldType> map;
    private String values;

    public Vente(int numTicket, int numLot, String datev, double prixv, int quant) {
        this.numTicket = numTicket;
        this.numLot = numLot;
        this.datev = (datev != null) ? datev.trim() : "";
        this.prixv = prixv;
        this.quant = quant;
        getStruct(); // Initialise la structure map
    }

    @Override
    public void getStruct() {
        map = new HashMap<>();
        map.put("num_ticket", fieldType.NUMERIC);
        map.put("num_lot", fieldType.NUMERIC);
        map.put("datev", fieldType.DATE);
        map.put("prixv", fieldType.FLOAT8);
        map.put("quant", fieldType.NUMERIC);
    }

    @Override
    public String getValues() {
        
        String safeDate = datev.replace("'", "''");
        values = "(" + numTicket + "," + numLot + ",'" + safeDate + "'," + prixv + "," + quant + ")";
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
        fv.put("num_ticket", numTicket);
        fv.put("num_lot", numLot);
        fv.put("datev", datev);
        fv.put("prixv", prixv);
        fv.put("quant", quant);
        return fv;
    }

    @Override
    public String toString() {
        return "Vente [numTicket=" + numTicket + ", numLot=" + numLot + ", datev="
               + datev + ", prixv=" + prixv + ", quant=" + quant + "]";
    }

    // Getters et Setters 
    public int getNumTicket() { return numTicket; }
    public void setNumTicket(int numTicket) { this.numTicket = numTicket; }

    public int getNumLot() { return numLot; }
    public void setNumLot(int numLot) { this.numLot = numLot; }

    public String getDatev() { return datev; }
    public void setDatev(String datev) { this.datev = datev; }

    public double getPrixv() { return prixv; }
    public void setPrixv(double prixv) { this.prixv = prixv; }

    public int getQuant() { return quant; }
    public void setQuant(int quant) { this.quant = quant; }
}