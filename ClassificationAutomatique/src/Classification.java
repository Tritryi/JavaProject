import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Classification {
    // MÉTHODES
    // Méthode de lecture des fichiers de dépêches
    private static ArrayList<Depeche> lectureDepeches(String nomFichier) {
        // Creation d'un tableau de dépêches
        ArrayList<Depeche> depeches = new ArrayList<>();
        try {
            // Lecture du fichier d'entrée
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

    // Méthode de classement des catégories (/100)
    public static void classementDepeche(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        try {
            FileWriter writer = new FileWriter(nomFichier);

            // Structure pour stocker les résultats pour chaque catégorie
            ArrayList<PaireChaineEntier> resultatsGlobaux = new ArrayList<>();

            for (Categorie c : categories) {
                resultatsGlobaux.add(new PaireChaineEntier(c.getNom(), 0));
            }

            for (Depeche depeche : depeches) {
                // Calcul des scores pour chaque depeche
                ArrayList<PaireChaineEntier> scores = new ArrayList<>();

                for (Categorie c : categories) {
                    scores.add(new PaireChaineEntier(c.getNom(), c.score(depeche)));
                }

                String categorieMax = UtilitairePaireChaineEntier.chaineMax(scores);
                writer.write(depeche.getId() + ": " + categorieMax + "\n");

                // Ajout des résultats de cette dépêche aux résultats globaux
                for (PaireChaineEntier paire : resultatsGlobaux) {
                    if (depeche.getCategorie().equals(paire.getChaine())) {
                        paire.incrementEntier();
                    }
                }
            }

            // Calculer les pourcentages et afficher les résultats globaux
            writer.write("\nRésultats Globaux :\n");
            float totalPourcentage = 0;
            for (PaireChaineEntier paire : resultatsGlobaux) {
                float pourcentage = (float) paire.getEntier() / depeches.size() * 100;
                writer.write(paire.getChaine() + ": " + pourcentage + "%\n");
                totalPourcentage += pourcentage;
            }
            float moyenneGlobale = UtilitairePaireChaineEntier.moyenne(resultatsGlobaux) / 100;  // Utilisation de la méthode moyenne
            writer.write("MOYENNE : " + moyenneGlobale * 100 + "%\n");

            // Fermer le fichier après avoir écrit toutes les données
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode de création du Dictionnaire pour les lexiques
    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie, String fichierMotsExclus) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();

        // Liste de mots à exclure (pronom, article, adjectif, etc.)
        ArrayList<String> motsExclus = lireMotsExclus(fichierMotsExclus);

        // Parcours de toutes les dépêches de la catégorie
        for (Depeche depeche : depeches) {
            // Vérification de la catégorie de la dépêche
            if (depeche.getCategorie().equals(categorie)) {
                // Parcours des mots de la dépêche
                for (String mot : depeche.getMots()) {
                    // Vérification si le mot est à exclure
                    if (!motsExclus.contains(mot)) {
                        // Vérification si le mot est déjà présent dans le résultat
                        boolean motPresent = false;
                        for (PaireChaineEntier paire : resultat) {
                            if (paire.getChaine().equals(mot)) {
                                motPresent = true;
                                break;
                            }
                        }

                        // Si le mot n'est pas présent, l'ajouter avec un score initial de 0
                        if (!motPresent) {
                            resultat.add(new PaireChaineEntier(mot, 0));
                        }
                    }
                }
            }
        }
        return resultat;
    }

    // Méthode pour exclure certains mots (pronoms, articles, adjectifs, lettres, etc...)
    private static ArrayList<String> lireMotsExclus(String fichierMotsExclus) {
        ArrayList<String> motsExclus = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(fichierMotsExclus);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                motsExclus.add(scanner.nextLine().trim().toLowerCase());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return motsExclus;
    }

    // Méthode pour calculer le score
    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        for (Depeche d : depeches) {
            boolean presCat = d.getCategorie().equals(categorie);
            for (String mot : d.getMots()) {
                int i = UtilitairePaireChaineEntier.indicePourChaine(dictionnaire, mot);
                if (i != -1) {
                    PaireChaineEntier p = dictionnaire.get(i);
                    int score = p.getEntier();
                    if (presCat) {
                        score++;
                    } else {
                        score--;
                    }
                    PaireChaineEntier nvl = new PaireChaineEntier(p.getChaine(), score);
                    dictionnaire.set(i, nvl);
                }
            }
        }
    }

    // Méthode pour définir le poids d'un mot selon son score
    public static int poidsPourScore(int score) {
        if (score >= 3) {
            return 3;
        } else if (score > 1) {
            return 2;
        } else {
            return 1;
        }
    }

    // Méthode de génération d'un lexique d'une catégorie selon le dictionnaire créé au préalable. Attribut un poids à chaque mot du lexique
    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {
        // Initialisation du dictionnaire avec les mots de la catégorie
        ArrayList<PaireChaineEntier> dictionnaire = initDico(depeches, categorie, "motsExclus.txt");

        // Mise à jour des scores dans le dictionnaire
        calculScores(depeches, categorie, dictionnaire);

        try {
            FileWriter writer = new FileWriter(nomFichier);

            // Écriture dans le fichier lexique avec les poids associés
            for (PaireChaineEntier paire : dictionnaire) {
                int poids = poidsPourScore(paire.getEntier());
                writer.write(paire.getChaine() + ":" + poids + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long mesureTempsExecutionLexiques(ArrayList<Depeche> depeches, ArrayList<Categorie> categories) {
        long tempsTotal = 0;
        for (Categorie categorie : categories) {
            long startTime = System.currentTimeMillis();
            // Appel de la méthode de génération du lexique
            generationLexique(depeches, categorie.getNom(), "Lexique-AA-" + categorie.getNom() + ".txt");
            long endTime = System.currentTimeMillis();
            long tempsExecution = endTime - startTime;
            tempsTotal += tempsExecution;
        }
        return tempsTotal;
    }

    // ÉXECUTION
    // Main, affichage et exécution de l'ensemble des méthodes. Mise en application
    public static void main(String[] args) {
        System.out.println("Chargement des dépêches"); // Chargement des dépêches en mémoire
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");

        for (int i = 0; i < depeches.size(); i++) {
            depeches.get(i).afficher();
        }

        // Création des catégories
        ArrayList<Categorie> categories = new ArrayList<>();
        Categorie environnementSciences = new Categorie("ENVIRONNEMENT-SCIENCES");
        Categorie culture = new Categorie("CULTURE");
        Categorie economie = new Categorie("ECONOMIE");
        Categorie politique = new Categorie("POLITIQUE");
        Categorie sports = new Categorie("SPORTS");

        // Initialisation des lexiques
        environnementSciences.initLexique("ENVIRONNEMENT-SCIENCES-lexique.txt");
        culture.initLexique("CULTURE-lexique.txt");
        economie.initLexique("ECONOMIE-lexique.txt");
        politique.initLexique("POLITIQUE-lexique.txt");
        sports.initLexique("SPORTS-lexique.txt");

        // Ajout des catégories à la liste
        categories.add(environnementSciences);
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
        System.out.println();

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

        // Génération des lexiques pour chaque catégorie
        for (Categorie categorie : categories) {
            String nomFichier = "Lexique-AA-" + categorie.getNom() + ".txt";
            generationLexique(depeches, categorie.getNom(), nomFichier);
            System.out.println("Lexique pour la catégorie " + categorie.getNom() + " généré avec succès.");
        }

        // Temps de création des lexiques
        System.out.println();
        long tempsTotalLexiques = mesureTempsExecutionLexiques(depeches, categories);
        System.out.println("Le temps total d'exécution pour la création des lexiques est : " + tempsTotalLexiques + "ms");
    }
}
