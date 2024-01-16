import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Categorie {
    private String nom; // le nom de la catégorie p.ex : sport, politique,...
    private ArrayList<PaireChaineEntier> lexique; // le lexique de la catégorie

    // CONSTRUCTEURS
    public Categorie(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<PaireChaineEntier> getLexique() {
        return lexique;
    }

    // MÉTHODES
    // Initialisation du lexique de la catégorie à partir du contenu d'un fichier texte
    public void initLexique(String nomFichier) {
        // Creation d'un tableau du lexique
        lexique = new ArrayList<>();
        try {
            // Lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                // Vérifie si le séparateur ':' est présent
                int Separateur = ligne.indexOf(':'); // Indique le séparateur recherché en question donc le ':'
                if (Separateur != -1) { // Application de la méthode indexOf(':') qui renvoie -1 si ':' n'est pas trouvé
                    String chaine = ligne.substring(0, Separateur); // Extrait les caractères de la position 0 au séparateur
                    String entierStr = ligne.substring(Separateur + 1).trim(); // Supprime les espaces avant et après
                    int entier = Integer.parseInt(entierStr);
                    PaireChaineEntier paire = new PaireChaineEntier(chaine, entier);
                    lexique.add(paire);
                } else { // Si le séparateur ':' n'est pas présent, indique la ligne problématique en question
                    System.out.println("Aucun séparateur ':' dans la ligne - " + ligne);
                }
            }
            scanner.close();
            // Exception liée à la lecture du fichier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //calcul du score d'une dépêche pour la catégorie
    public int score(Depeche d) {
        int score = 0;
        int i = 0;
        while (i < d.getMots().size()) {
            score = score + UtilitairePaireChaineEntier.entierPourChaine(lexique, d.getMots().get(i));
            i++;
        }

        return score;

    }
}