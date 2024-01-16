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
            System.out.println("Mot : " + paire.getChaine() + " | Entier : " + paire.getEntier());
        }
    }

    public static void  classementDepeche(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier){
        try {
            FileWriter writer = new FileWriter(nomFichier);

            for (Depeche depeche : depeches) {
                // Calcul des scores pour chaque catégorie
                ArrayList<PaireChaineEntier> scores = new ArrayList<>();
                int totalScore = 0;

                for (Categorie categorie : categories) {
                    int score = categorie.score(depeche);
                    scores.add(new PaireChaineEntier(categorie.getNom(), score));
                    totalScore += score;
                }

                // Écrire l'id de la dépêche et la catégorie
                writer.write(depeche.getId() + ":" + depeche.getCategorie() + "\n");

                ArrayList<PaireChaineEntier> total = new ArrayList<>();

                // Calcul du pourcentage pour chaque catégorie
                for (PaireChaineEntier paire : scores) {
                    float pourcentage = ((float) paire.getEntier() / totalScore) * 100;
                    total.add(new PaireChaineEntier(paire.getChaine(), Math.round(pourcentage)));
                    writer.write(paire.getChaine() + ": " + pourcentage + "%\n");
                }

                // Écrire la moyenne pour chaque dépêche
                float moyenne = UtilitairePaireChaineEntier.moyenne(total);
                writer.write("MOYENNE : " + moyenne + "%\n");
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        return resultat;

    }

    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        for(Depeche d : depeches){
            boolean presCat = d.getCategorie().equals(categorie);


            for(String mot : d.getMots()){
                int i = UtilitairePaireChaineEntier.indicePourChaine(dictionnaire, categorie);

                if(i !=-1){
                    PaireChaineEntier p = dictionnaire.get(i);
                    int score = p.getEntier();
                    if(presCat){
                        score++;
                    }
                    else{
                        score--;
                    }
                    PaireChaineEntier nvl = new PaireChaineEntier(p.getChaine(), score);
                    dictionnaire.set(i, nvl);
                }
            }
        }
    }

    public static int poidsPourScore(int score) {
        if(score>10){
            return 3;
        }
        else if(score>5){
            return 2;
        }
        else{
            return 1;
        }
    }

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {
    }

    public static void main(String[] args) {
        System.out.println("Chargement des dépêches"); // Chargement des dépêches en mémoire
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");

        for (int i = 0; i < depeches.size(); i++) {
            depeches.get(i).afficher();
        }

        // Création des catégories et initialisation des lexiques
        ArrayList<Categorie> categories = new ArrayList<>();
        Categorie environnementScience = new Categorie("ENVIRONNEMENT-SCIENCE");
        Categorie culture = new Categorie("CULTURE");
        Categorie economie = new Categorie("ECONOMIE");
        Categorie politique = new Categorie("POLITIQUE");
        Categorie sports = new Categorie("SPORTS");

        // Initialisation des lexiques
        environnementScience.initLexique("ENVIRONNEMENT-SCIENCE-lexique.txt");
        culture.initLexique("CULTURE-lexique.txt");
        economie.initLexique("ECONOMIE-lexique.txt");
        politique.initLexique("POLITIQUE-lexique.txt");
        sports.initLexique("SPORT-lexique.txt");

        // Ajout des catégories à la liste
        categories.add(environnementScience);
        categories.add(culture);
        categories.add(economie);
        categories.add(politique);
        categories.add(sports);

        // Afficher les lexiques pour chaque catégorie
        for (Categorie categorie : categories) {
            afficherLexique(categorie);
            System.out.println();
        }

        // Saisie d'un mot par l'utilisateur
        Scanner lecteur = new Scanner(System.in);
        System.out.print("Saisissez un mot contenu dans le lexique : ");
        String mot = lecteur.nextLine().toLowerCase();
        System.out.println();

        // Afficher le poids associé pour chaque catégorie
        for (Categorie categorie : categories) {
            int poids = UtilitairePaireChaineEntier.entierPourChaine(categorie.getLexique(), mot);
            System.out.println("Le poids associé au mot '" + mot + "' dans la catégorie "
                    + categorie.getNom() + " est : " + poids);
        }
        System.out.println();

        // Afficher le score d'une dépêche sélectionnée
        ArrayList<PaireChaineEntier> tab_score = new ArrayList<>();
        for (Categorie c : categories) {
            System.out.println("Le score de la dépêche 122 dans la catégorie " + c.getNom() + " est de : " + c.score(depeches.get(122)));
            tab_score.add(new PaireChaineEntier(c.getNom(), c.score(depeches.get(122))));
        }
        System.out.println();
        System.out.println("La catégorie ayant le score le plus élevé est : " + UtilitairePaireChaineEntier.chaineMax(tab_score));

        // Afficher les résultats pour chaque catégorie en pourcentage
        classementDepeche(depeches, categories, "resultats.txt");
        try {
            FileInputStream file = new FileInputStream("resultats.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
