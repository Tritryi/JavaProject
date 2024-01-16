public class PaireChaineEntier {
    private String chaine; // le mot clé du lexique
    private int entier; // la notation du mot clé du lexique (de 1 à 3)

    // CONSTRUCTEURS
    public PaireChaineEntier(String chaine, int entier) {
        this.chaine = chaine;
        this.entier = entier;
    }

    public String getChaine() {
        return chaine;
    }

    public int getEntier() {
        return entier;
    }
}
