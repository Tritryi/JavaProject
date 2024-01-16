import java.util.ArrayList;

public class UtilitairePaireChaineEntier {
    public static int indicePourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        for (PaireChaineEntier paire : listePaires) {
            if (paire.getChaine().equals(chaine)) {
                return paire.getEntier();
            }
        }
        return -1;
    }

    public static int entierPourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        for (PaireChaineEntier paire : listePaires) { // Parcours de chaque élément de la liste
            if (paire.getChaine().equals(chaine)) { // Vérifie la correspondance entre la saisie et la chaine
                return paire.getEntier(); // Retourne l’entier associé à la chaîne de caractères chaine dans listePaires
            }
        }
        return 0; // Si elle n'est pas présente, retourne 0
    }

    public static String chaineMax(ArrayList<PaireChaineEntier> listePaires) {
        if(listePaires.isEmpty()){
            return null;
        }
        PaireChaineEntier chaineMax = listePaires.get(0);

        for(PaireChaineEntier paire : listePaires){
            if(paire.getEntier()>chaineMax.getEntier()){
                chaineMax=paire;
            }
        }
        return chaineMax.getChaine();
    }

    public static float moyenne(ArrayList<PaireChaineEntier> listePaires) {
        if (listePaires.isEmpty()) {
            return 0; // Si la liste est vide, la moyenne est 0 pour éviter une division par zéro
        }
        int somme = 0;
        int nombreCategories = listePaires.size();

        for (PaireChaineEntier paire : listePaires) {
            somme += paire.getEntier();
        }
        return ((float) somme / nombreCategories)*100;
    }
}
