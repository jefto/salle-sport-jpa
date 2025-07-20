-- Script SQL à exécuter dans PostgreSQL pour ajouter la colonne manquante

-- Ajouter la colonne id_seance à la table membre
ALTER TABLE membre ADD COLUMN id_seance INTEGER;

-- Ajouter la contrainte de clé étrangère vers la table seance
ALTER TABLE membre ADD CONSTRAINT fk_membre_seance
    FOREIGN KEY (id_seance) REFERENCES seance(id_seance);

-- Optionnel : Ajouter un index pour améliorer les performances
CREATE INDEX idx_membre_seance ON membre(id_seance);

-- Vérifier que la colonne a été ajoutée
\d membre;
