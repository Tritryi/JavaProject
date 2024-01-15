import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Classification {


    private static ArrayList<Depeche> lectureDepeches(String nomFichier) {
        //creation d'un tableau de dépêches
        ArrayList<Depeche> depeches = new ArrayList<>();
        try {
            // lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                String id = ligne.substring(3);
                ligne = scanner.nextLine();
                String date = ligne.substring(3);
                ligne = scanner.nextLine();
                String categorie = ligne.substring(3);
                ligne = scanner.nextLine();
                String lignes = ligne.substring(3);
                while (scanner.hasNextLine() && !ligne.equals("")) {
                    ligne = scanner.nextLine();
                    if (!ligne.equals("")) {
                        lignes = lignes + '\n' + ligne;
                    }
                }
                Depeche uneDepeche = new Depeche(id, date, categorie, lignes);
                depeches.add(uneDepeche);
            }
            scanner.close();
            // Exception liée à la lecture du fichier
        } catch (IOException e) {
            e.printStackTrace();
        }
        return depeches;
    }

    // Méthode d'affichage du lexique
    private static void afficherLexique(Categorie categorie) {
        System.out.println("Lexique de la catégorie " + categorie.getNom() + " :");
        System.out.println();
        ArrayList<PaireChaineEntier> lexique = categorie.getLexique(); // Chargement du lexique en mémoire
        for (PaireChaineEntier paire : lexique) { // Parcours de chaque paire de la liste du lexique
            System.out.println("Chaine : " + paire.getChaine() + " | Entier : " + paire.getEntier());
        }
    }

    public static void classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
    }


    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        return resultat;

    }

    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
    }

    public static int poidsPourScore(int score) {
        return 0;
    }

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {

    }

    public static void main(String[] args) {
        System.out.println("chargement des dépêches"); // Chargement des dépêches en mémoire
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");

        for (int i = 0; i < depeches.size(); i++) {
            depeches.get(i).afficher();
        }

        // Création des catégories et initialisation des lexiques
        ArrayList<Categorie> categories = new ArrayList<>();
        Categorie environnementSciences = new Categorie("ENVIRONNEMENT-SCIENCES");
        Categorie culture = new Categorie("CULTURE");
        Categorie economie = new Categorie("ECONOMIE");
        Categorie politique = new Categorie("POLITIQUE");
        Categorie sports = new Categorie("SPORTS");

        // Initialisation des lexiques
        environnementSciences.initLexique("ENVIRONNEMENT-SCIENCE-lexique.txt");
        culture.initLexique("CULTURE-lexique.txt");
        economie.initLexique("ECONOMIE-lexique.txt");
        politique.initLexique("POLITIQUE-lexique.txt");
        sports.initLexique("SPORT-lexique.txt");

        // Ajout des catégories à la liste
        categories.add(environnementSciences);
        categories.add(culture);
        categories.add(economie);
        categories.add(politique);
        categories.add(sports);

        // Afficher les lexiques pour chaque catégorie
        for (Categorie categorie : categories) {
            afficherLexique(categorie);
        }

        // Vecteur de scores dont le nom est la catégorie
        Depeche dep = depeches.get(0);
        ArrayList<PaireChaineEntier> score = new ArrayList<>();
        for(Categorie c : categories){
            PaireChaineEntier p;
            int sco =0;
            score.add(p);
        }
        
        
        System.out.println("Score de la dépêche : " +dep);

        // Catégorie ayant le score maximal 
        
        String catMax = UtilitairePaireChaineEntier.chaineMax(categories);
        
        

        // Saisie d'un mot par l'utilisateur
        Scanner lecteur = new Scanner(System.in);
        System.out.print("Saisissez un mot contenu dans le lexique : ");
        String mot = lecteur.nextLine();

        // Afficher le poids associé pour chaque catégorie
        for (Categorie categorie : categories) {
            int poids = UtilitairePaireChaineEntier.entierPourChaine(categorie.getLexique(), mot);
            System.out.println("Le poids associé au mot '" + mot + "' dans la catégorie "
                    + categorie.getNom() + " est : " + poids);
        }
    }
}
