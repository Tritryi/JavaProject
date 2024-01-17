import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Categorie {
    // INITIALISATION DES VARIABLES
    private String nom; // Le nom de la catégorie p.ex : sport, politique,...
    private ArrayList<PaireChaineEntier> lexique; // Le lexique de la catégorie

    // CONSTRUCTEURS
    public Categorie(String nom) {
        this.nom = nom;
    }

    // ACCESSEURS
    public String getNom() {
        return nom;
    }

    public ArrayList<PaireChaineEntier> getLexique() {
        return lexique;
    }

    // MÉTHODES
    // Méthode d'initialisation du lexique de la catégorie à partir du contenu d'un fichier texte
    public void initLexique(String nomFichier) {
        lexique = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner lecteur = new Scanner(file);
            while (lecteur.hasNextLine()) {
                String ligne = lecteur.nextLine(); // Parcours ligne par ligne
                // Vérifie si le séparateur ':' est présent
                int separateur = ligne.indexOf(':'); // Indique le séparateur recherché en question donc le ':'
                if (separateur != -1) { // Application de la méthode indexOf(':') qui renvoie -1 si ':' n'est pas trouvé
                    String chaine = ligne.substring(0, separateur); // Extrait les caractères de la position 0 au séparateur
                    String entierString = ligne.substring(separateur + 1).trim(); // Supprime les espaces avant et après
                    try {
                        int entier = Integer.parseInt(entierString); // Convertit 'entierString' en un entier en utilisant la méthode parseInt
                        PaireChaineEntier paire = new PaireChaineEntier(chaine, entier);
                        lexique.add(paire);
                    } catch (NumberFormatException e) {
                        // Gère l'exception ou signalez une erreur
                        System.err.println("Erreur de format pour la chaîne : " + entierString);
                    }
                } else { // Si le séparateur ':' n'est pas présent, indique la ligne problématique en question
                    System.out.println("Aucun séparateur ':' dans la ligne - " + ligne);
                }
            }
            lecteur.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode qui calcule le score d'une dépêche en fonction des mots dans son contenu
    public int score(Depeche d) {
        int score = 0;
        int i = 0;
        while (i < d.getMots().size()) {
            score = score + UtilitairePaireChaineEntier.entierPourChaine(lexique, d.getMots().get(i)); // Ajout du score associé au mot actuel à la variable score
            i++;
        }
        return score; // Retourne le score total de la dépêche
    }
}
