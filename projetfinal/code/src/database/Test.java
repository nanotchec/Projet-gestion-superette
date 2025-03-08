package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import data.Produit;
import data.Fournisseur;
import data.Contrat;
import data.Achat;
import data.Vente;
import data.Contact;

public class Test {

    // On stocke la connection ici, pour y accéder dans toutes les méthodes statiques
    private static Connection conn;

    public static void main(String[] args) {
        // On se connecte à la base via la classe Connexion
        Connexion connexion = new Connexion();
        try {
            connexion.connect();
            // Récupération de la connexion
            conn = connexion.getConnection();

            // Instanciation de Gestion avec cette même connexion
            Gestion gestion = new Gestion(conn);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            boolean exit = false;
            while (!exit) {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1. Creation / Destruction de Tables");
                System.out.println("2. Gestion des Tables (INSERT, DISPLAY, etc.)");
                System.out.println("3. Rapports et Statistiques");
                System.out.println("4. Quitter");
                System.out.println("---------------------");
                System.out.print("Choisissez une option: ");

                String choice = reader.readLine().trim();
                switch (choice) {
                    case "1":
                        menuCreationDestruction(reader, gestion);
                        break;
                    case "2":
                        menuGestionTables(reader, gestion);
                        break;
                    case "3":
                        menuRapportsStats(reader, gestion);
                        break;
                    case "4":
                        System.out.println("Fermeture du programme.");
                        exit = true;
                        break;
                    default:
                        System.err.println("Option invalide. Réessayez.");
                        break;
                }
            }

            // Fermeture
            reader.close();
            connexion.disconnect();

        } catch (SQLException | IOException e) {
            System.err.println("Erreur globale : " + e.getMessage());
            e.printStackTrace();
        }
    }

    //=====================================================
    // 1. CREATION / DESTRUCTION DE TABLES
    //=====================================================
    private static void menuCreationDestruction(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== CREATION / DESTRUCTION DE TABLES ===");
            System.out.println("1. Creer une table");
            System.out.println("2. Supprimer (DROP) une table");
            System.out.println("3. Retour au menu principal");
            System.out.println("---------------------------------------");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    menuCreationTables(reader, gestion);
                    break;
                case "2":
                    menuDestructionTables(reader, gestion);
                    break;
                case "3":
                    backToMain = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    // Sous-menu : création de tables
    private static void menuCreationTables(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== CREATION DE TABLES ===");
            System.out.println("1. Table PRODUIT");
            System.out.println("2. Table FOURNISSEUR");
            System.out.println("3. Table CONTRAT");
            System.out.println("4. Table ACHAT");
            System.out.println("5. Table VENTE");
            System.out.println("6. Table CONTACT");
            System.out.println("7. Retour");
            System.out.println("-------------------------");
            System.out.print("Choisissez une option: ");
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1":
                    String createProduit = "CREATE TABLE produit ("
                            + "idproduit NUMERIC PRIMARY KEY,"
                            + "numlotachat NUMERIC,"
                            + "nom VARCHAR(50),"
                            + "description VARCHAR(255),"
                            + "categorie VARCHAR(50),"
                            + "prixvente FLOAT8,"
                            + "actif INT4 DEFAULT 1,"
                            + "seuil_min NUMERIC DEFAULT 5)";
                    gestion.execute(createProduit);
                    System.out.println("Table 'produit' créée avec succès.");
                    break;
                case "2":
                    String createFourn = "CREATE TABLE fournisseur ("
                            + "siret VARCHAR(14) PRIMARY KEY,"
                            + "nomsociete VARCHAR(100),"
                            + "adresse VARCHAR(255),"
                            + "email VARCHAR(100),"
                            + "actif INT4 DEFAULT 1)";
                    gestion.execute(createFourn);
                    System.out.println("Table 'fournisseur' créée avec succès.");
                    break;
                case "3":
                    String createContrat = "CREATE TABLE contrat ("
                            + "idcontrat NUMERIC PRIMARY KEY,"
                            + "dated DATE,"
                            + "datef DATE,"
                            + "prix FLOAT8,"
                            + "quantmin NUMERIC,"
                            + "id_produit NUMERIC,"
                            + "siret VARCHAR(14),"
                            + "engageliv INT4,"
                            + "FOREIGN KEY (id_produit) REFERENCES produit(idproduit),"
                            + "FOREIGN KEY (siret) REFERENCES fournisseur(siret))";
                    gestion.execute(createContrat);
                    System.out.println("Table 'contrat' créée avec succès.");
                    break;
                case "4":
                    String createAchat = "CREATE TABLE achat ("
                            + "num_lot NUMERIC PRIMARY KEY,"
                            + "quant NUMERIC,"
                            + "dateachat DATE,"
                            + "datelimitc DATE,"
                            + "id_contrat NUMERIC,"
                            + "prixunitaire FLOAT8,"
                            + "FOREIGN KEY (id_contrat) REFERENCES contrat(idcontrat))";
                    gestion.execute(createAchat);
                    System.out.println("Table 'achat' créée avec succès.");
                    break;
                case "5":
                    String createVente = "CREATE TABLE vente ("
                            + "num_ticket NUMERIC,"
                            + "num_lot NUMERIC,"
                            + "datev DATE,"
                            + "prixv FLOAT8,"
                            + "quant NUMERIC,"
                            + "PRIMARY KEY (num_ticket, num_lot),"
                            + "FOREIGN KEY (num_lot) REFERENCES achat(num_lot))";
                    gestion.execute(createVente);
                    System.out.println("Table 'vente' créée avec succès.");
                    break;
                case "6":
                    String createContact = "CREATE TABLE contact ("
                            + "id_contact NUMERIC PRIMARY KEY,"
                            + "nom VARCHAR(50),"
                            + "prenom VARCHAR(50),"
                            + "fonction VARCHAR(50),"
                            + "mail VARCHAR(100),"
                            + "tel VARCHAR(20),"
                            + "siret VARCHAR(14) REFERENCES fournisseur(siret))";
                    gestion.execute(createContact);
                    System.out.println("Table 'contact' créée avec succès.");
                    break;
                case "7":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    // Sous-menu : destruction (DROP) de tables
    private static void menuDestructionTables(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== DESTRUCTION (DROP) DE TABLES ===");
            System.out.println("1. Table PRODUIT");
            System.out.println("2. Table FOURNISSEUR");
            System.out.println("3. Table CONTRAT");
            System.out.println("4. Table ACHAT");
            System.out.println("5. Table VENTE");
            System.out.println("6. Table CONTACT");
            System.out.println("7. Retour");
            System.out.println("-------------------------");
            System.out.print("Choisissez une option: ");
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1":
                    gestion.execute("DROP TABLE produit");
                    System.out.println("Table 'produit' supprimée avec succès.");
                    break;
                case "2":
                    gestion.execute("DROP TABLE fournisseur");
                    System.out.println("Table 'fournisseur' supprimée avec succès.");
                    break;
                case "3":
                    gestion.execute("DROP TABLE contrat");
                    System.out.println("Table 'contrat' supprimée avec succès.");
                    break;
                case "4":
                    gestion.execute("DROP TABLE achat");
                    System.out.println("Table 'achat' supprimée avec succès.");
                    break;
                case "5":
                    gestion.execute("DROP TABLE vente");
                    System.out.println("Table 'vente' supprimée avec succès.");
                    break;
                case "6":
                    gestion.execute("DROP TABLE contact");
                    System.out.println("Table 'contact' supprimée avec succès.");
                    break;
                case "7":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    //=====================================================
    // 2. GESTION DES TABLES
    //=====================================================
    private static void menuGestionTables(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== GESTION DES TABLES ===");
            System.out.println("1. Gérer PRODUIT");
            System.out.println("2. Gérer FOURNISSEUR");
            System.out.println("3. Gérer CONTRAT");
            System.out.println("4. Gérer ACHAT");
            System.out.println("5. Gérer VENTE");
            System.out.println("6. Gérer CONTACT");
            System.out.println("7. Retour au menu principal");
            System.out.println("--------------------------");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    menuGestionProduit(reader, gestion);
                    break;
                case "2":
                    menuGestionFournisseur(reader, gestion);
                    break;
                case "3":
                    menuGestionContrat(reader, gestion);
                    break;
                case "4":
                    menuGestionAchat(reader, gestion);
                    break;
                case "5":
                    menuGestionVente(reader, gestion);
                    break;
                case "6":
                    menuGestionContact(reader, gestion);
                    break;
                case "7":
                    backToMain = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    //==================== GESTION PRODUIT ====================
    private static void menuGestionProduit(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== GESTION PRODUIT ===");
            System.out.println("1. INSERT");
            System.out.println("2. DISPLAY");
            System.out.println("3. REMOVE");
            System.out.println("4. STRUCT");
            System.out.println("5. UPDATE");
            System.out.println("6. DISABLE");
            System.out.println("7. CHECK_STOCK (vérifier stock sous seuil)");
            System.out.println("8. ORDER (commander produit)");
            System.out.println("9. Retour");
            System.out.println("-------------------------");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    insertProduit(reader, gestion);
                    break;
                case "2":
                    gestion.displayTable("produit");
                    break;
                case "3":
                    removeProduit(reader);
                    break;
                case "4":
                    gestion.structTable("produit", true);
                    break;
                case "5":
                    updateProduit(reader);
                    break;
                case "6":
                    disableProduit(reader);
                    break;
                case "7":
                    gestion.checkStockLevels();
                    break;
                case "8":
                    orderProduit(reader, gestion);
                    break;
                case "9":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    private static void insertProduit(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        try {
            System.out.print("idProduit: ");
            int idProduit = Integer.parseInt(reader.readLine().trim());
            System.out.print("numLotAchat: ");
            int numLotAchat = Integer.parseInt(reader.readLine().trim());
            System.out.print("Nom (max 15 caractères): ");
            String nom = reader.readLine().trim();
            if (nom.length() > 15) {
                System.out.println("Le nom dépasse 15 caractères. Il sera tronqué.");
                nom = nom.substring(0, 15);
            }
            System.out.print("Description: ");
            String desc = reader.readLine().trim();
            System.out.print("Categorie: ");
            String cat = reader.readLine().trim();
            System.out.print("Prix de vente: ");
            double pv = Double.parseDouble(reader.readLine().trim());
            System.out.print("Seuil minimal (défaut 5): ");
            String sMinStr = reader.readLine().trim();
            int sMin = sMinStr.isEmpty() ? 5 : Integer.parseInt(sMinStr);

            Produit p = new Produit(idProduit, numLotAchat, nom, desc, cat, pv, true, sMin);
            gestion.insert(p, "produit");
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide (nombre attendu). " + e.getMessage());
        }
    }

    private static void removeProduit(BufferedReader reader) throws IOException, SQLException {
        System.out.print("Entrez l'id du produit à supprimer: ");
        int idToRemove = Integer.parseInt(reader.readLine().trim());
        String deleteQuery = "DELETE FROM produit WHERE idproduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, idToRemove);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Produit supprimé avec succès.");
            } else {
                System.out.println("Aucun produit trouvé avec cet id.");
            }
        }
    }

    private static void updateProduit(BufferedReader reader) throws IOException, SQLException {
        try {
            System.out.print("idProduit à modifier: ");
            int prodID = Integer.parseInt(reader.readLine().trim());

            System.out.print("Nouvelle description: ");
            String newDesc = reader.readLine().trim();
            System.out.print("Nouvelle catégorie: ");
            String newCat = reader.readLine().trim();
            System.out.print("Nouveau prix de vente: ");
            double newPrix = Double.parseDouble(reader.readLine().trim());
            System.out.print("Nouveau seuil minimal: ");
            int newSeuil = Integer.parseInt(reader.readLine().trim());

            String updateP = "UPDATE produit SET description = ?, categorie = ?, prixvente = ?, seuil_min = ? WHERE idproduit = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateP)) {
                pstmt.setString(1, newDesc);
                pstmt.setString(2, newCat);
                pstmt.setDouble(3, newPrix);
                pstmt.setInt(4, newSeuil);
                pstmt.setInt(5, prodID);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Produit mis à jour avec succès.");
                } else {
                    System.out.println("Aucun produit trouvé avec cet id.");
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide. " + e.getMessage());
        }
    }

    private static void disableProduit(BufferedReader reader) throws IOException, SQLException {
        System.out.print("Entrez l'id du produit à désactiver: ");
        int idProdToDisable = Integer.parseInt(reader.readLine().trim());
        String disableQuery = "UPDATE produit SET actif = 0 WHERE idproduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(disableQuery)) {
            pstmt.setInt(1, idProdToDisable);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit désactivé (actif=0) avec succès.");
            } else {
                System.out.println("Aucun produit trouvé avec cet id.");
            }
        }
    }

    private static void orderProduit(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        try {
            System.out.print("idProduit: ");
            int idP = Integer.parseInt(reader.readLine().trim());
            System.out.print("Quantité à commander: ");
            int qCmd = Integer.parseInt(reader.readLine().trim());
            System.out.print("idContrat (existant): ");
            int idCont = Integer.parseInt(reader.readLine().trim());
            System.out.print("Prix Unitaire: ");
            double pu = Double.parseDouble(reader.readLine().trim());

            gestion.orderProduct(idP, qCmd, idCont, pu);
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide. " + e.getMessage());
        }
    }

    //==================== GESTION FOURNISSEUR ====================
    private static void menuGestionFournisseur(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== GESTION FOURNISSEUR ===");
            System.out.println("1. INSERT");
            System.out.println("2. DISPLAY");
            System.out.println("3. REMOVE");
            System.out.println("4. STRUCT");
            System.out.println("5. DISABLE");
            System.out.println("6. UPDATE (email)");
            System.out.println("7. Retour");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    insertFournisseur(reader, gestion);
                    break;
                case "2":
                    gestion.displayTable("fournisseur");
                    break;
                case "3":
                    removeFournisseur(reader);
                    break;
                case "4":
                    gestion.structTable("fournisseur", true);
                    break;
                case "5":
                    disableFournisseur(reader);
                    break;
                case "6":
                    updateFournisseur(reader);
                    break;
                case "7":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    private static void insertFournisseur(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        System.out.print("SIRET (14 chars max) : ");
        String siret = reader.readLine().trim();
        System.out.print("Nom de la société : ");
        String nomSoc = reader.readLine().trim();
        System.out.print("Adresse : ");
        String adresse = reader.readLine().trim();
        System.out.print("Email principal : ");
        String email = reader.readLine().trim();

        Fournisseur f = new Fournisseur(siret, nomSoc, adresse, email, true);
        gestion.insert(f, "fournisseur");
    }

    private static void removeFournisseur(BufferedReader reader) throws IOException, SQLException {
        System.out.print("Entrez le SIRET du fournisseur à supprimer: ");
        String siretRem = reader.readLine().trim();
        String deleteQuery = "DELETE FROM fournisseur WHERE siret = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, siretRem);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Fournisseur supprimé avec succès.");
            } else {
                System.out.println("Aucun fournisseur trouvé avec ce SIRET.");
            }
        }
    }

    private static void disableFournisseur(BufferedReader reader) throws IOException, SQLException {
        System.out.print("Entrez le SIRET du fournisseur à désactiver : ");
        String siretDisable = reader.readLine().trim();
        String disableQuery = "UPDATE fournisseur SET actif = 0 WHERE siret = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(disableQuery)) {
            pstmt.setString(1, siretDisable);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Fournisseur désactivé avec succès.");
            } else {
                System.out.println("Aucun fournisseur trouvé avec ce SIRET.");
            }
        }
    }

    private static void updateFournisseur(BufferedReader reader) throws IOException, SQLException {
        System.out.print("Entrez le SIRET du fournisseur à mettre à jour : ");
        String siretUpdate = reader.readLine().trim();
        System.out.print("Nouvel email : ");
        String newEmail = reader.readLine().trim();

        String updateQuery = "UPDATE fournisseur SET email = ? WHERE siret = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, siretUpdate);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Fournisseur mis à jour avec succès.");
            } else {
                System.out.println("Aucun fournisseur trouvé avec ce SIRET.");
            }
        }
    }

    //==================== GESTION CONTRAT ====================
    private static void menuGestionContrat(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== GESTION CONTRAT ===");
            System.out.println("1. INSERT");
            System.out.println("2. DISPLAY");
            System.out.println("3. REMOVE");
            System.out.println("4. STRUCT");
            System.out.println("5. Retour");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    insertContrat(reader, gestion);
                    break;
                case "2":
                    gestion.displayTable("contrat");
                    break;
                case "3":
                    removeContrat(reader);
                    break;
                case "4":
                    gestion.structTable("contrat", true);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    private static void insertContrat(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        try {
            System.out.print("idContrat: ");
            int idContrat = Integer.parseInt(reader.readLine().trim());
            System.out.print("Date debut (YYYY-MM-DD): ");
            String dated = reader.readLine().trim();
            System.out.print("Date fin (YYYY-MM-DD): ");
            String datef = reader.readLine().trim();
            System.out.print("Prix fixe: ");
            double prix = Double.parseDouble(reader.readLine().trim());
            System.out.print("Quantité minimale: ");
            int qMin = Integer.parseInt(reader.readLine().trim());
            System.out.print("idProduit: ");
            int idProd = Integer.parseInt(reader.readLine().trim());
            System.out.print("SIRET du fournisseur: ");
            String siret = reader.readLine().trim();
            System.out.print("Engagement(1 ou 0): ");
            int engageliv = Integer.parseInt(reader.readLine().trim());

            Contrat ctr = new Contrat(idContrat, dated, datef, prix, qMin, idProd, siret, engageliv);
            gestion.insert(ctr, "contrat");
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide. " + e.getMessage());
        }
    }

    private static void removeContrat(BufferedReader reader) throws IOException, SQLException {
        System.out.print("idContrat à supprimer: ");
        int idC = Integer.parseInt(reader.readLine().trim());
        String deleteQuery = "DELETE FROM contrat WHERE idcontrat = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, idC);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Contrat supprimé avec succès.");
            } else {
                System.out.println("Aucun contrat trouvé avec cet id.");
            }
        }
    }

    //==================== GESTION ACHAT ====================
    private static void menuGestionAchat(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== GESTION ACHAT ===");
            System.out.println("1. INSERT");
            System.out.println("2. DISPLAY");
            System.out.println("3. REMOVE");
            System.out.println("4. STRUCT");
            System.out.println("5. Retour");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    insertAchat(reader, gestion);
                    break;
                case "2":
                    gestion.displayTable("achat");
                    break;
                case "3":
                    removeAchat(reader);
                    break;
                case "4":
                    gestion.structTable("achat", true);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    private static void insertAchat(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        try {
            System.out.print("numLot: ");
            int numLot = Integer.parseInt(reader.readLine().trim());
            System.out.print("Quantité: ");
            int quant = Integer.parseInt(reader.readLine().trim());
            System.out.print("Date achat (YYYY-MM-DD): ");
            String dA = reader.readLine().trim();
            System.out.print("Date limite (YYYY-MM-DD): ");
            String dL = reader.readLine().trim();
            System.out.print("idContrat: ");
            int idCont = Integer.parseInt(reader.readLine().trim());
            System.out.print("Prix Unitaire: ");
            double pu = Double.parseDouble(reader.readLine().trim());

            Achat achat = new Achat(numLot, quant, dA, dL, idCont, pu);
            gestion.insert(achat, "achat");
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide. " + e.getMessage());
        }
    }

    private static void removeAchat(BufferedReader reader) throws IOException, SQLException {
        System.out.print("numLot à supprimer: ");
        int numLot = Integer.parseInt(reader.readLine().trim());
        String deleteQuery = "DELETE FROM achat WHERE num_lot = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, numLot);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Achat supprimé avec succès.");
            } else {
                System.out.println("Aucun achat trouvé avec ce numLot.");
            }
        }
    }

    //==================== GESTION VENTE ====================
    private static void menuGestionVente(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== GESTION VENTE ===");
            System.out.println("1. INSERT");
            System.out.println("2. DISPLAY");
            System.out.println("3. REMOVE");
            System.out.println("4. STRUCT");
            System.out.println("5. Retour");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    insertVente(reader, gestion);
                    break;
                case "2":
                    gestion.displayTable("vente");
                    break;
                case "3":
                    removeVente(reader);
                    break;
                case "4":
                    gestion.structTable("vente", true);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    private static void insertVente(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        try {
            System.out.print("numTicket: ");
            int numTicket = Integer.parseInt(reader.readLine().trim());
            System.out.print("numLot: ");
            int numLot = Integer.parseInt(reader.readLine().trim());
            System.out.print("Date vente (YYYY-MM-DD): ");
            String dv = reader.readLine().trim();
            System.out.print("Prix de vente: ");
            double pv = Double.parseDouble(reader.readLine().trim());
            System.out.print("Quantité: ");
            int q = Integer.parseInt(reader.readLine().trim());

            Vente v = new Vente(numTicket, numLot, dv, pv, q);
            gestion.insert(v, "vente");
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide. " + e.getMessage());
        }
    }

    private static void removeVente(BufferedReader reader) throws IOException, SQLException {
        System.out.print("numTicket: ");
        int numTicket = Integer.parseInt(reader.readLine().trim());
        System.out.print("numLot: ");
        int numLot = Integer.parseInt(reader.readLine().trim());

        String deleteQuery = "DELETE FROM vente WHERE num_ticket = ? AND num_lot = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, numTicket);
            pstmt.setInt(2, numLot);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Vente supprimée avec succès.");
            } else {
                System.out.println("Aucune vente trouvée (num_ticket, num_lot) = (" + numTicket + ", " + numLot + ")");
            }
        }
    }

    //==================== GESTION CONTACT ====================
    private static void menuGestionContact(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== GESTION CONTACT ===");
            System.out.println("1. INSERT");
            System.out.println("2. DISPLAY");
            System.out.println("3. REMOVE");
            System.out.println("4. STRUCT");
            System.out.println("5. Retour");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    insertContact(reader, gestion);
                    break;
                case "2":
                    gestion.displayTable("contact");
                    break;
                case "3":
                    removeContact(reader);
                    break;
                case "4":
                    gestion.structTable("contact", true);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }

    private static void insertContact(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        try {
            System.out.print("idContact: ");
            int idC = Integer.parseInt(reader.readLine().trim());
            System.out.print("Nom: ");
            String nom = reader.readLine().trim();
            System.out.print("Prenom: ");
            String pre = reader.readLine().trim();
            System.out.print("Fonction: ");
            String fonc = reader.readLine().trim();
            System.out.print("Mail: ");
            String mail = reader.readLine().trim();
            System.out.print("Tel: ");
            String tel = reader.readLine().trim();
            System.out.print("SIRET du fournisseur: ");
            String siret = reader.readLine().trim();

            if (!gestion.checkSiretExists(siret)) {
                System.err.println("Le SIRET fourni n'existe pas dans la table fournisseur.");
                return;
            }

            Contact ct = new Contact(idC, nom, pre, fonc, mail, tel, siret);
            gestion.insertContact(ct);
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide: " + e.getMessage());
        }
    }

    private static void removeContact(BufferedReader reader) throws IOException, SQLException {
        System.out.print("idContact à supprimer: ");
        int idCont = Integer.parseInt(reader.readLine().trim());
        String deleteQuery = "DELETE FROM contact WHERE id_contact = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, idCont);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Contact supprimé avec succès.");
            } else {
                System.out.println("Aucun contact trouvé avec cet id.");
            }
        }
    }

    //=====================================================
    // 3. RAPPORTS ET STATISTIQUES
    //=====================================================
    private static void menuRapportsStats(BufferedReader reader, Gestion gestion) throws IOException, SQLException {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== RAPPORTS ET STATISTIQUES ===");
            System.out.println("1. Rapport Ventes Jour (sans benef)");
            System.out.println("2. Rapport Ventes Jour (avec benef)");
            System.out.println("3. Rapport Ventes Mois (sans benef)");
            System.out.println("4. Rapport Ventes Mois (avec benef)");
            System.out.println("5. Top 10 Produits Ventes (quantité)");
            System.out.println("6. Top 10 Produits Ventes (bénéfices)");
            System.out.println("7. Afficher les lots expirant bientôt");
            System.out.println("8. Afficher le prix d'achat moyen");
            System.out.println("9. Afficher le stock actuel (calcul dynamique)");
            System.out.println("10. Retour menu principal");
            System.out.println("-----------------------------------");
            System.out.print("Choisissez une option: ");

            String choice = reader.readLine().trim();
            switch (choice) {
                case "1":
                    gestion.reportDailySales();
                    break;
                case "2":
                    gestion.reportDailySalesWithBenef();
                    break;
                case "3":
                    gestion.reportMonthSales();
                    break;
                case "4":
                    gestion.reportMonthSalesWithBenef();
                    break;
                case "5":
                    gestion.reportTop10Sales();
                    break;
                case "6":
                    gestion.reportTop10SalesBenef();
                    break;
                case "7":
                    System.out.print("Nombre de jours avant péremption: ");
                    String dayStr = reader.readLine().trim();
                    try {
                        int d = Integer.parseInt(dayStr);
                        gestion.displayExpiringLots(d);
                    } catch (NumberFormatException e) {
                        System.err.println("Entrée invalide. " + e.getMessage());
                    }
                    break;
                case "8":
                    gestion.displayAverageBuyPrice();
                    break;
                case "9":
                    gestion.displayCurrentStock();
                    break;
                case "10":
                    backToMain = true;
                    break;
                default:
                    System.err.println("Option invalide.");
            }
        }
    }
}