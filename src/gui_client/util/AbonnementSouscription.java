package gui_client.util;

import entite.*;
import service.*;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Classe utilitaire pour gérer la logique de souscription aux abonnements
 * Centralise toutes les règles métier et validations concernant les abonnements
 */
public class AbonnementSouscription {

    private static AbonnementSouscription instance;

    // Services
    private AbonnementService abonnementService;
    private PaiementService paiementService;
    private MoyenDePaiementService moyenDePaiementService;
    private MembreService membreService;

    private AbonnementSouscription() {
        this.abonnementService = new AbonnementService();
        this.paiementService = new PaiementService();
        this.moyenDePaiementService = new MoyenDePaiementService();
        this.membreService = new MembreService();
    }

    public static AbonnementSouscription getInstance() {
        if (instance == null) {
            instance = new AbonnementSouscription();
        }
        return instance;
    }

    /**
     * Vérifie si un membre peut souscrire à un nouvel abonnement
     * @param membre Le membre qui souhaite souscrire
     * @return ResultatValidation contenant le résultat et les détails
     */
    public ResultatValidation peutSouscrire(Membre membre) {
        try {
            // Vérifier s'il y a un abonnement actif
            Abonnement abonnementActif = abonnementService.getAbonnementActif(membre.getId());

            if (abonnementActif == null) {
                return new ResultatValidation(true, "Aucun abonnement actif. Souscription autorisée.");
            }

            // Calculer les jours restants
            long joursRestants = ChronoUnit.DAYS.between(LocalDateTime.now(), abonnementActif.getDateFin());

            if (joursRestants <= 0) {
                return new ResultatValidation(true, "Abonnement expiré. Souscription autorisée.");
            }

            if (joursRestants <= 7) {
                return new ResultatValidation(true,
                    "Renouvellement autorisé (il vous reste " + joursRestants + " jour(s)).",
                    abonnementActif, true);
            }

            return new ResultatValidation(false,
                "Vous avez déjà un abonnement actif qui expire dans " + joursRestants + " jour(s).",
                abonnementActif, false);

        } catch (Exception e) {
            return new ResultatValidation(false, "Erreur lors de la vérification : " + e.getMessage());
        }
    }

    /**
     * Effectue le processus complet de souscription à un abonnement
     * @param membre Le membre souscripteur
     * @param typeAbonnement Le type d'abonnement choisi
     * @param moyenPaiement Le moyen de paiement sélectionné
     * @return ResultatSouscription contenant le résultat de l'opération
     */
    public ResultatSouscription souscrireAbonnement(Membre membre, TypeAbonnement typeAbonnement, MoyenDePaiement moyenPaiement) {
        try {
            // Vérifier si la souscription est autorisée
            ResultatValidation validation = peutSouscrire(membre);

            if (!validation.isAutorise() && !validation.isRenouvellementProche()) {
                return new ResultatSouscription(false, validation.getMessage());
            }

            // Date actuelle pour le paiement
            LocalDateTime datePaiement = LocalDateTime.now();

            // Créer le paiement
            Paiement paiement = new Paiement(datePaiement, typeAbonnement.getMontant(), moyenPaiement);
            paiementService.ajouter(paiement);

            // Calculer les dates d'abonnement
            LocalDateTime dateDebut, dateFin;

            if (validation.isRenouvellementProche() && validation.getAbonnementActif() != null) {
                // Renouvellement : commencer à la fin de l'abonnement actuel
                dateDebut = validation.getAbonnementActif().getDateFin();
                dateFin = dateDebut.plusMonths(1);
            } else {
                // Nouvel abonnement : commencer maintenant
                dateDebut = datePaiement;
                dateFin = dateDebut.plusMonths(1);
            }

            // Créer l'abonnement
            Abonnement nouvelAbonnement = new Abonnement(dateDebut, dateFin, typeAbonnement, paiement, membre);
            abonnementService.ajouter(nouvelAbonnement);

            String message = validation.isRenouvellementProche() ?
                "Renouvellement effectué avec succès !" :
                "Souscription effectuée avec succès !";

            return new ResultatSouscription(true, message, nouvelAbonnement, paiement);

        } catch (Exception e) {
            return new ResultatSouscription(false, "Erreur lors de la souscription : " + e.getMessage());
        }
    }

    /**
     * Récupère la liste des moyens de paiement disponibles
     * @return Liste des moyens de paiement
     */
    public List<MoyenDePaiement> getMoyensPaiementDisponibles() {
        try {
            return moyenDePaiementService.listerTous();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Récupère l'historique des abonnements d'un membre
     * @param membre Le membre
     * @return Liste des abonnements du membre (triés par date décroissante)
     */
    public List<Abonnement> getHistoriqueAbonnements(Membre membre) {
        try {
            return abonnementService.getAbonnementsByMembre(membre.getId());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Récupère l'abonnement actuellement actif d'un membre
     * @param membre Le membre
     * @return L'abonnement actif ou null si aucun
     */
    public Abonnement getAbonnementActif(Membre membre) {
        try {
            return abonnementService.getAbonnementActif(membre.getId());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Vérifie si un abonnement est encore actif
     * @param abonnement L'abonnement à vérifier
     * @return true si l'abonnement est actif, false sinon
     */
    public boolean isAbonnementActif(Abonnement abonnement) {
        if (abonnement == null) return false;
        return abonnement.getDateFin().isAfter(LocalDateTime.now());
    }

    /**
     * Calcule le nombre de jours restants pour un abonnement
     * @param abonnement L'abonnement
     * @return Nombre de jours restants (peut être négatif si expiré)
     */
    public long getJoursRestants(Abonnement abonnement) {
        if (abonnement == null) return 0;
        return ChronoUnit.DAYS.between(LocalDateTime.now(), abonnement.getDateFin());
    }

    /**
     * Récupère le membre associé à un client
     * @param clientId ID du client
     * @return Le membre ou null si pas trouvé
     */
    public Membre getMembreByClientId(Integer clientId) {
        try {
            return membreService.getMembreByClientId(clientId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Affiche une boîte de dialogue de confirmation pour la souscription
     * @param parent Composant parent
     * @param typeAbonnement Type d'abonnement
     * @param validation Résultat de la validation
     * @return true si l'utilisateur confirme, false sinon
     */
    public boolean confirmerSouscription(java.awt.Component parent, TypeAbonnement typeAbonnement, ResultatValidation validation) {
        String message;
        int messageType;

        if (validation.isRenouvellementProche()) {
            message = "Renouvellement d'abonnement\n\n" +
                     validation.getMessage() + "\n\n" +
                     "Nouveau type : " + typeAbonnement.getLibelle() + "\n" +
                     "Montant : " + typeAbonnement.getMontant() + " FCFA\n\n" +
                     "Le nouvel abonnement commencera à la fin du précédent.\n" +
                     "Confirmer le renouvellement ?";
            messageType = JOptionPane.QUESTION_MESSAGE;
        } else {
            message = "Souscription à un abonnement\n\n" +
                     "Type : " + typeAbonnement.getLibelle() + "\n" +
                     "Montant : " + typeAbonnement.getMontant() + " FCFA\n\n" +
                     "Confirmer la souscription ?";
            messageType = JOptionPane.QUESTION_MESSAGE;
        }

        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            messageType
        );

        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Affiche le résultat d'une souscription
     * @param parent Composant parent
     * @param resultat Résultat de la souscription
     */
    public void afficherResultatSouscription(java.awt.Component parent, ResultatSouscription resultat) {
        if (resultat.isSucces()) {
            String message = resultat.getMessage() + "\n\n";

            if (resultat.getAbonnement() != null) {
                message += "Détails de l'abonnement :\n" +
                          "Type : " + resultat.getAbonnement().getTypeAbonnement().getLibelle() + "\n" +
                          "Date début : " + resultat.getAbonnement().getDateDebut().toLocalDate() + "\n" +
                          "Date fin : " + resultat.getAbonnement().getDateFin().toLocalDate() + "\n";

                if (resultat.getPaiement() != null) {
                    message += "Montant payé : " + resultat.getPaiement().getMontant() + " FCFA\n" +
                              "Moyen de paiement : " + resultat.getPaiement().getMoyenDePaiement().getLibelle();
                }
            }

            JOptionPane.showMessageDialog(parent, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parent, resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classes internes pour les résultats
    public static class ResultatValidation {
        private boolean autorise;
        private String message;
        private Abonnement abonnementActif;
        private boolean renouvellementProche;

        public ResultatValidation(boolean autorise, String message) {
            this.autorise = autorise;
            this.message = message;
            this.renouvellementProche = false;
        }

        public ResultatValidation(boolean autorise, String message, Abonnement abonnementActif, boolean renouvellementProche) {
            this.autorise = autorise;
            this.message = message;
            this.abonnementActif = abonnementActif;
            this.renouvellementProche = renouvellementProche;
        }

        // Getters
        public boolean isAutorise() { return autorise; }
        public String getMessage() { return message; }
        public Abonnement getAbonnementActif() { return abonnementActif; }
        public boolean isRenouvellementProche() { return renouvellementProche; }
    }

    public static class ResultatSouscription {
        private boolean succes;
        private String message;
        private Abonnement abonnement;
        private Paiement paiement;

        public ResultatSouscription(boolean succes, String message) {
            this.succes = succes;
            this.message = message;
        }

        public ResultatSouscription(boolean succes, String message, Abonnement abonnement, Paiement paiement) {
            this.succes = succes;
            this.message = message;
            this.abonnement = abonnement;
            this.paiement = paiement;
        }

        // Getters
        public boolean isSucces() { return succes; }
        public String getMessage() { return message; }
        public Abonnement getAbonnement() { return abonnement; }
        public Paiement getPaiement() { return paiement; }
    }
}
