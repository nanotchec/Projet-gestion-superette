package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException; 
import java.sql.Statement; 
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import data.IData;
import data.fieldType;
import data.Vente;
import data.Contact;
import data.Achat;

public class Gestion {
    private Connection conn;
    
    
    public Gestion(Connection conn) {
        this.conn = conn;
    }

    //=====================================================
    //                STRUCTURE & AFFICHAGE
    //=====================================================
    /**
     * Retourne la structure d'une table (colonne -> type) 
     * et peut l'afficher si display = true.
     */
    public HashMap<String, fieldType> structTable(String table, boolean display) throws SQLException {
        HashMap<String, fieldType> struct = new HashMap<>();

        String query = "SELECT * FROM " + table + " LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i).toLowerCase();
                String columnType = rsmd.getColumnTypeName(i).toLowerCase();

                fieldType type;
                switch (columnType) {
                    case "numeric":
                        type = fieldType.NUMERIC;
                        break;
                    case "varchar":
                        type = fieldType.VARCHAR;
                        break;
                    case "float8":
                        type = fieldType.FLOAT8;
                        break;
                    case "int4":
                        type = fieldType.INT4;
                        break;
                    case "date":
                        type = fieldType.DATE;
                        break;
                    default:
                        type = null;
                        break;
                }

                struct.put(columnName, type);

                if (display) {
                    System.out.println(columnName + " - " + columnType);
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la structure de la table " 
                               + table + ": " + e.getMessage());
            throw e;
        }
        return struct;
    }

    /**
     * Affiche le contenu d'une table sous forme de tableau aligné.
     */
    public void displayTable(String table) throws SQLException {
        String query = "SELECT * FROM " + table;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();
            List<List<String>> rows = new ArrayList<>();
            List<String> headers = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                headers.add(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String val = rs.getString(i);
                    row.add((val != null ? val : "NULL"));
                }
                rows.add(row);
            }

            int[] colWidths = new int[columnCount];
            for (int i = 0; i < columnCount; i++) {
                colWidths[i] = headers.get(i).length(); 
            }

            for (List<String> row : rows) {
                for (int i = 0; i < columnCount; i++) {
                    int length = row.get(i).length();
                    if (length > colWidths[i]) {
                        colWidths[i] = length;
                    }
                }
            }

            // Afficher l'en-tête
            for (int i = 0; i < columnCount; i++) {
                System.out.printf("%-" + (colWidths[i] + 2) + "s", headers.get(i));
            }
            System.out.println();

            // Ligne de séparation
            for (int i = 0; i < columnCount; i++) {
                for (int j = 0; j < colWidths[i] + 2; j++) {
                    System.out.print("-");
                }
            }
            System.out.println();

            // Afficher les lignes
            for (List<String> row : rows) {
                for (int i = 0; i < columnCount; i++) {
                    System.out.printf("%-" + (colWidths[i] + 2) + "s", row.get(i));
                }
                System.out.println();
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage de la table " + table + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Exécute une requête SQL arbitraire (hors insertion).
     */
    public void execute(String query) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("Requête exécutée avec succès: " + query);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête : " + e.getMessage());
            throw e;
        }
    }

    //=====================================================
    //            INSERTION GÉNÉRIQUE (idata)
    //=====================================================
    /**
     * Méthode d'insertion générique pour toute classe qui implémente IData.
     * S'occupe également du "stock dynamique" pour les achats et ventes.
     */
    public void insert(IData data, String table) throws SQLException {
        HashMap<String, fieldType> tableStruct;
        try {
            tableStruct = structTable(table, false);
        } catch (SQLException e) {
            System.err.println("Impossible d'obtenir la structure de la table " + table + ": " + e.getMessage());
            return;
        }

        // Vérification de la correspondance structure/table
        data.getStruct();
        if (!data.check(tableStruct)) {
            System.err.println("La structure de l'instance ne correspond pas à celle de la table " + table + ".");
            return;
        }

        HashMap<String, Object> fieldValues = data.getFieldValues();

        // Déterminer la clé primaire ou composite
        String primaryKey = "";
        if ("produit".equalsIgnoreCase(table)) {
            primaryKey = "idproduit";
        } else if ("fournisseur".equalsIgnoreCase(table)) {
            primaryKey = "siret";
        } else if ("contrat".equalsIgnoreCase(table)) {
            primaryKey = "idcontrat";
        } else if ("achat".equalsIgnoreCase(table)) {
            primaryKey = "num_lot";
        } else if ("vente".equalsIgnoreCase(table)) {
            // clé composite
            primaryKey = "num_ticket,num_lot";
        } else if ("contact".equalsIgnoreCase(table)) {
            primaryKey = "id_contact";
        } else {
            System.err.println("La table " + table + " n'est pas gérée pour l'insertion automatique.");
            return;
        }

        // Vérifier si l'enregistrement existe déjà
        boolean exists = false;
        if ("vente".equalsIgnoreCase(table)) {
            String[] keys = primaryKey.split(",");
            String checkQuery = "SELECT * FROM " + table + " WHERE " + keys[0] + " = ? AND " + keys[1] + " = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkQuery)) {
                pstmtCheck.setObject(1, fieldValues.get(keys[0]));
                pstmtCheck.setObject(2, fieldValues.get(keys[1]));
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    exists = true;
                }
                rs.close();
            }
        } else {
            String checkQuery = "SELECT * FROM " + table + " WHERE " + primaryKey + " = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkQuery)) {
                pstmtCheck.setObject(1, fieldValues.get(primaryKey));
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    exists = true;
                }
                rs.close();
            }
        }

        if (exists) {
            if ("vente".equalsIgnoreCase(table)) {
                System.err.println("La vente (num_ticket=" + fieldValues.get("num_ticket") 
                                   + ", num_lot=" + fieldValues.get("num_lot") + ") existe déjà.");
            } else {
                System.err.println("L'élément existe déjà dans la table " + table + ". Aucune mise à jour automatique.");
            }
        } else {
            // Construction de la requête INSERT
            StringBuilder columns = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();

            for (String col : fieldValues.keySet()) {
                columns.append(col).append(",");
                placeholders.append("?,");
            }

            if (columns.length() > 0)  columns.setLength(columns.length() - 1);
            if (placeholders.length() > 0)  placeholders.setLength(placeholders.length() - 1);

            String insertQuery = "INSERT INTO " + table + " (" + columns.toString() 
                                 + ") VALUES (" + placeholders.toString() + ")";

            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertQuery)) {
                int index = 1;
                for (String col : fieldValues.keySet()) {
                    Object val = fieldValues.get(col);
                    fieldType type = tableStruct.get(col.toLowerCase());

                    if (type == fieldType.DATE) {
                        try {
                            // Utiliser LocalDate pour parser la date
                            LocalDate date = LocalDate.parse(val.toString());
                            pstmtInsert.setDate(index++, java.sql.Date.valueOf(date));
                        } catch (Exception e) {
                            System.err.println("Format de date invalide pour le champ " + col + ".");
                            return;
                        }
                    } else {
                        pstmtInsert.setObject(index++, val);
                    }
                }
                pstmtInsert.executeUpdate();
                System.out.println(capitalize(table) + " inséré avec succès.");

                // Logique pour achat / vente => mise à jour stock dynamique
                if ("achat".equalsIgnoreCase(table)) {
                    int numLot = (int) fieldValues.get("num_lot");
                    updateStockAfterAchat(numLot); 
                }
                else if ("vente".equalsIgnoreCase(table)) {
                    int numTicket = (int) fieldValues.get("num_ticket");
                    int numLot = (int) fieldValues.get("num_lot");
                    String datev = (String) fieldValues.get("datev");
                    double prixv = (double) fieldValues.get("prixv");
                    int quant = (int) fieldValues.get("quant");

                    Vente vente = new Vente(numTicket, numLot, datev, prixv, quant);
                    updateStockAfterVente(vente);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'insertion dans la table " + table + ": " + e.getMessage());
            }
        }
    }

    //=====================================================
    //       MISE À JOUR DU STOCK (DYNAMIQUE)
    //=====================================================
    /**
     * Méthode appelée après l'insertion d'un achat.
     * Dans la gestion dynamique
     */
    private void updateStockAfterAchat(int numLot) throws SQLException {
        System.out.println("Achat inséré (numLot=" + numLot + "). Stock mis à jour de façon dynamique.");
    }

    /**
     * Méthode appelée après l'insertion d'une vente.
     */
    private void updateStockAfterVente(Vente vente) throws SQLException {
        System.out.println("Vente insérée (numTicket=" + vente.getNumTicket() 
                           + ", numLot=" + vente.getNumLot() + "). Stock mis à jour dynamique.");
    }

    //=====================================================
    //     CALCUL DU STOCK ACTUEL (DYNAMIQUE)
    //=====================================================
    /**
     * Affiche la quantité de stock actuelle pour chaque produit 
     * = somme(achat.quant) - somme(vente.quant).
     */
    public void displayCurrentStock() throws SQLException {
        String query =
            "SELECT p.idproduit, p.nom, "
          + "       COALESCE(SUM(a.quant), 0) - COALESCE(SUM(v.quant), 0) AS stock_actuel "
          + "FROM produit p "
          + "LEFT JOIN contrat c ON p.idproduit = c.id_produit "
          + "LEFT JOIN achat a ON c.idcontrat = a.id_contrat "
          + "LEFT JOIN vente v ON a.num_lot = v.num_lot "
          + "GROUP BY p.idproduit, p.nom "
          + "ORDER BY p.idproduit";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== Stock Actuel (Calcul Dynamique) ===");
            System.out.printf("%-10s %-20s %-15s%n", "ID Produit", "Nom", "Stock Actuel");
            System.out.println("------------------------------------------------");
            while (rs.next()) {
                int idProd = rs.getInt("idproduit");
                String nom = rs.getString("nom");
                double stockActuel = rs.getDouble("stock_actuel");
                System.out.printf("%-10d %-20s %-15.2f%n", idProd, nom, stockActuel);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage du stock actuel : " + e.getMessage());
            throw e;
        }
    }

    //=====================================================
    //     CHECK STOCK LEVELS (SOUS LE SEUIL_MIN)
    //=====================================================
    /**
     * Vérifie les stocks et liste les produits en dessous de leur seuil_min (dynamique).
     */
    public void checkStockLevels() throws SQLException {
        String query =
          "SELECT p.idproduit, p.nom, p.seuil_min, "
        + "       COALESCE(SUM(a.quant), 0) - COALESCE(SUM(v.quant), 0) AS stock_calc "
        + "FROM produit p "
        + "LEFT JOIN contrat c ON p.idproduit = c.id_produit "
        + "LEFT JOIN achat a ON c.idcontrat = a.id_contrat "
        + "LEFT JOIN vente v ON a.num_lot = v.num_lot "
        + "GROUP BY p.idproduit, p.nom, p.seuil_min "
        + "HAVING COALESCE(SUM(a.quant), 0) - COALESCE(SUM(v.quant), 0) < p.seuil_min "
        + "ORDER BY p.idproduit";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== Produits en dessous du seuil_min (calcul dynamique) ===");
            int count = 0;
            while (rs.next()) {
                int idP = rs.getInt("idproduit");
                String nomProd = rs.getString("nom");
                double sMin = rs.getDouble("seuil_min");
                double stockCalc = rs.getDouble("stock_calc");
                System.out.println("Produit " + idP + " (" + nomProd + ") : stock=" 
                                   + stockCalc + ", seuil=" + sMin
                                   + " => Besoin de commande ?");
                count++;
            }
            if (count == 0) {
                System.out.println("Aucun produit n'est en dessous de son seuil minimal.");
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur checkStockLevels : " + e.getMessage());
            throw e;
        }
    }

    //=====================================================
    //        COMMANDE ORDER => SIMPLIFIE UN ACHAT
    //=====================================================
    /**
     * Passe une commande => transformée immédiatement en Achat 
     * (contrat doit exister).
     */
    public void orderProduct(int idProduit, int quantCommande, int idContrat, double prixUnitaire) throws SQLException {
        int newNumLot = generateLotNumber(); 
        LocalDate today = LocalDate.now();
        LocalDate dateLimit = today.plusDays(365); // 1 an par défaut

        String todayStr = today.toString();
        String dateLimitStr = dateLimit.toString();

        Achat achat = new Achat(newNumLot, quantCommande, todayStr, dateLimitStr, idContrat, prixUnitaire);
        insert(achat, "achat");
    }

    private int generateLotNumber() throws SQLException {
        String sql = "SELECT COALESCE(MAX(num_lot),0) AS maxlot FROM achat";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int max = rs.getInt("maxlot");
                rs.close();
                return max + 1;
            }
            rs.close();
            return 1;
        }
    }

    //=====================================================
    //                AUTRES RAPPORTS
    //=====================================================
    /**
     * Rapport des ventes du jour (sans bénéfice).
     */
    public void reportDailySales() throws SQLException {
        String query = 
          "SELECT SUM(v.quant * v.prixv) AS total_jour, COUNT(*) AS nb_ventes "
        + "FROM vente v "
        + "WHERE v.datev = CURRENT_DATE";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                double totalJour = rs.getDouble("total_jour");
                int nbVentes = rs.getInt("nb_ventes");
                System.out.println("=== Rapport des ventes du jour ===");
                System.out.println("Total des ventes du jour : " + totalJour);
                System.out.println("Nombre de ventes du jour : " + nbVentes);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des ventes du jour : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Rapport des ventes du jour (avec bénéfices).
     */
    public void reportDailySalesWithBenef() throws SQLException {
        String query = 
          "SELECT "
        + "  SUM(v.quant * v.prixv) AS total_vente, "
        + "  SUM(v.quant * a.prixunitaire) AS total_achat, "
        + "  SUM(v.quant * (v.prixv - a.prixunitaire)) AS benef, "
        + "  COUNT(*) AS nb_ventes "
        + "FROM vente v "
        + "JOIN achat a ON v.num_lot = a.num_lot "
        + "WHERE v.datev = CURRENT_DATE";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                double totalVente = rs.getDouble("total_vente");
                double totalAchat = rs.getDouble("total_achat");
                double benef = rs.getDouble("benef");
                int nbVentes = rs.getInt("nb_ventes");
                System.out.println("=== Rapport des ventes du jour (avec bénéfices) ===");
                System.out.println("Total des ventes du jour  : " + totalVente);
                System.out.println("Coût d'achat du jour      : " + totalAchat);
                System.out.println("Bénéfice du jour          : " + benef);
                System.out.println("Nombre de ventes du jour  : " + nbVentes);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des ventes du jour (benef) : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Rapport des ventes du mois (sans bénéfice).
     */
    public void reportMonthSales() throws SQLException {
        String query = 
          "SELECT SUM(v.quant * v.prixv) AS total_mois, COUNT(*) AS nb_ventes "
        + "FROM vente v "
        + "WHERE EXTRACT(MONTH FROM v.datev) = EXTRACT(MONTH FROM CURRENT_DATE) "
        + "  AND EXTRACT(YEAR FROM v.datev) = EXTRACT(YEAR FROM CURRENT_DATE)";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                double totalMois = rs.getDouble("total_mois");
                int nbVentes = rs.getInt("nb_ventes");
                System.out.println("=== Rapport des ventes du mois en cours ===");
                System.out.println("Total des ventes du mois : " + totalMois);
                System.out.println("Nombre de ventes du mois : " + nbVentes);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des ventes du mois : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Rapport des ventes du mois (avec bénéfices).
     */
    public void reportMonthSalesWithBenef() throws SQLException {
        String query =
          "SELECT "
        + "  SUM(v.quant * v.prixv) AS total_vente, "
        + "  SUM(v.quant * a.prixunitaire) AS total_achat, "
        + "  SUM(v.quant * (v.prixv - a.prixunitaire)) AS benef, "
        + "  COUNT(*) AS nb_ventes "
        + "FROM vente v "
        + "JOIN achat a ON v.num_lot = a.num_lot "
        + "WHERE EXTRACT(MONTH FROM v.datev) = EXTRACT(MONTH FROM CURRENT_DATE) "
        + "  AND EXTRACT(YEAR FROM v.datev) = EXTRACT(YEAR FROM CURRENT_DATE)";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                double totalVente = rs.getDouble("total_vente");
                double totalAchat = rs.getDouble("total_achat");
                double benef = rs.getDouble("benef");
                int nbVentes = rs.getInt("nb_ventes");
                System.out.println("=== Rapport des ventes du mois (avec bénéfices) ===");
                System.out.println("Total des ventes du mois  : " + totalVente);
                System.out.println("Coût d'achat du mois      : " + totalAchat);
                System.out.println("Bénéfice du mois          : " + benef);
                System.out.println("Nombre de ventes du mois  : " + nbVentes);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors du rapport des ventes du mois (benef) : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Top 10 des produits les plus vendus (quantité).
     */
    public void reportTop10Sales() throws SQLException {
        String query =
          "SELECT p.idproduit, p.nom, SUM(v.quant) AS total_qte "
        + "FROM vente v "
        + "JOIN achat a ON v.num_lot = a.num_lot "
        + "JOIN contrat c ON a.id_contrat = c.idcontrat "
        + "JOIN produit p ON c.id_produit = p.idproduit "
        + "GROUP BY p.idproduit, p.nom "
        + "ORDER BY total_qte DESC "
        + "LIMIT 10";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== Top 10 produits les plus vendus (quantité) ===");
            int rank = 1;
            while (rs.next()) {
                int idProd = rs.getInt("idproduit");
                String nomProd = rs.getString("nom");
                double totalQte = rs.getDouble("total_qte");
                System.out.println(rank + ". Produit " + idProd + " (" + nomProd 
                                   + ") : " + totalQte + " vendus");
                rank++;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors du top 10 des ventes : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Top 10 des produits les plus rentables (bénéfices).
     */
    public void reportTop10SalesBenef() throws SQLException {
        String query =
          "SELECT p.idproduit, p.nom, "
        + "       SUM(v.quant * (v.prixv - a.prixunitaire)) AS total_benef "
        + "FROM vente v "
        + "JOIN achat a ON v.num_lot = a.num_lot "
        + "JOIN contrat c ON a.id_contrat = c.idcontrat "
        + "JOIN produit p ON c.id_produit = p.idproduit "
        + "GROUP BY p.idproduit, p.nom "
        + "ORDER BY total_benef DESC "
        + "LIMIT 10";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== Top 10 produits les plus rentables (bénéfices) ===");
            int rank = 1;
            while (rs.next()) {
                int idProd = rs.getInt("idproduit");
                String nomProd = rs.getString("nom");
                double benef = rs.getDouble("total_benef");
                System.out.println(rank + ". Produit " + idProd + " (" + nomProd 
                                   + ") : " + benef + " de bénéfices");
                rank++;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur top 10 bénéfices : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Affiche les lots qui vont périmer dans X jours.
     */
    public void displayExpiringLots(int days) throws SQLException {
        String query = 
          "SELECT a.num_lot, a.datelimitc, p.nom "
        + "FROM achat a "
        + "JOIN contrat c ON a.id_contrat = c.idcontrat "
        + "JOIN produit p ON c.id_produit = p.idproduit "
        + "WHERE a.datelimitc < CURRENT_DATE + INTERVAL '" + days + " days'";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== Lots périmant dans " + days + " jours ===");
            while (rs.next()) {
                int numLot = rs.getInt("num_lot");
                String dateLimit = rs.getString("datelimitc");
                String nomProd = rs.getString("nom");
                System.out.println("Lot " + numLot + " (Produit: " + nomProd + ") périme le " + dateLimit);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de lister les lots périmés : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Affiche le prix d'achat moyen par produit.
     */
    public void displayAverageBuyPrice() throws SQLException {
        String query =
          "SELECT p.idproduit, p.nom, AVG(a.prixunitaire) AS avg_price "
        + "FROM achat a "
        + "JOIN contrat c ON a.id_contrat = c.idcontrat "
        + "JOIN produit p ON c.id_produit = p.idproduit "
        + "GROUP BY p.idproduit, p.nom";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== Prix d’achat moyen par produit ===");
            while (rs.next()) {
                int idProd = rs.getInt("idproduit");
                String nomProd = rs.getString("nom");
                double avgP = rs.getDouble("avg_price");
                System.out.println("Produit " + idProd + " (" + nomProd 
                                   + ") => prix d'achat moyen = " + avgP);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Erreur pour prix d'achat moyen : " + e.getMessage());
            throw e;
        }
    }

    //=====================================================
    //                GESTION DES CONTACTS
    //=====================================================
    public void insertContact(Contact contact) throws SQLException {
        insert(contact, "contact");
    }

    public boolean checkSiretExists(String siret) throws SQLException {
        String checkQuery = "SELECT * FROM fournisseur WHERE siret = ?";
        try (PreparedStatement pstmtCheck = conn.prepareStatement(checkQuery)) {
            pstmtCheck.setString(1, siret);
            ResultSet rs = pstmtCheck.executeQuery();
            boolean exists = rs.next();
            rs.close();
            return exists;
        }
    }

    //=====================================================
    // UTILITAIRE : Capitalize la première lettre
    //=====================================================
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}