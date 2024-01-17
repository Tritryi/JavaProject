public class PaireChaineEntier {
    // INITIALISATION DES VARIABLES
    private String chaine; // Le mot clé du lexique
    private int entier; // La notation du mot clé du lexique (de 1 à 3)

    // CONSTRUCTEURS
    public PaireChaineEntier(String chaine, int entier) {
        this.chaine = chaine;
        this.entier = entier;
    }

    // ACCESSEURS
    public String getChaine() {
        return chaine;
    }

    public int getEntier() {
        return entier;
    }
}
