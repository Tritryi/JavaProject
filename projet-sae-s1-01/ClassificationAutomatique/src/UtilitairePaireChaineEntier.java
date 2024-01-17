import java.util.ArrayList;

public class UtilitairePaireChaineEntier {
    // MÉTHODES
    // Méthode retournant la position d'un contenu, par l'indice, dans une liste. Agit comme une sorte de pointeur de parcours de chaine pour trouver les différentes catégories dans la liste
    public static int indicePourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        for (int i = 0; i < listePaires.size(); i++) {
            PaireChaineEntier paire = listePaires.get(i);
            if (paire.getChaine().equals(chaine)) { // Vérifie si la chaîne de l'élément actuel correspond à la chaîne recherchée
                return i; // Retourne l'indice de chaine dans listePaires
            }
        }
        return -1; // Si chaine n'est pas présente, retourne -1
    }

    // Méthode pour trouver l'entier associé à une chaine de caractère
    public static int entierPourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        for (PaireChaineEntier paire : listePaires) {
            if (paire.getChaine().equals(chaine)) { // Vérifie la correspondance entre la saisie et la chaine
                return paire.getEntier(); // Retourne l’entier associé à la chaîne de caractères chaine
            }
        }
        return 0; // Si elle n'est pas présente, retourne 0
    }

    // Méthode recherchant la catégorie avec le score maximal
    public static String chaineMax(ArrayList<PaireChaineEntier> listePaires) {
        if (listePaires.isEmpty()) {
            return null; // Si la liste est vide, retourne null
        }
        PaireChaineEntier chaineMax = listePaires.get(0);
        for (PaireChaineEntier paire : listePaires) {
            if (paire.getEntier() > chaineMax.getEntier()) {
                chaineMax = paire;
            }
        }
        return chaineMax.getChaine(); // Retourne la chaîne associée au score maximal
    }

    // Méthode appliquant la moyenne de recherche sur les catégories
    public static float moyenne(ArrayList<PaireChaineEntier> listePaires) {
        if (listePaires.isEmpty()) {
            return 0; // Si la liste est vide, la moyenne est 0 pour éviter une division par zéro
        }
        float somme = 0;
        for (PaireChaineEntier paire : listePaires) {
            somme += paire.getEntier();
        }
        return somme / 5; // Retourne la moyenne des entiers, divisé par 5 étant le nombre de catégorie présente
    }
}
