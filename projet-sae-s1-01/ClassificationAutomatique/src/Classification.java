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
            Scanner lecteur = new Scanner(file);
            while (lecteur.hasNextLine()) {
                String ligne = lecteur.nextLine();
                String id = ligne.substring(3);
                ligne = lecteur.nextLine();
                String date = ligne.substring(3);
                ligne = lecteur.nextLine();
                String categorie = ligne.substring(3);
                ligne = lecteur.nextLine();
                String lignes = ligne.substring(3);
                while (lecteur.hasNextLine() && !ligne.equals("")) {
                    ligne = lecteur.nextLine();
                    if (!ligne.equals("")) {
                        lignes = lignes + '\n' + ligne;
                    }
                }
                Depeche uneDepeche = new Depeche(id, date, categorie, lignes);
                depeches.add(uneDepeche);
            }
            lecteur.close();
            // Exception liée à la lecture du fichier
        } catch (IOException e) {
            e.printStackTrace();
        }
        return depeches;
    }

    // Méthode de classement des dépêches par le score
    public static void classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        try {
            FileWriter writer = new FileWriter(nomFichier);
            int verification;
            // Initialisation des tableaux pour stocker les valeurs de chaque catégorie
            int[] totals = new int[categories.size()];
            int[] scores = new int[categories.size()];
            for (Depeche depeche : depeches) {
                ArrayList<PaireChaineEntier> score_depeche = new ArrayList<>();
                // Calcul des scores pour chaque catégorie
                for (Categorie categorie : categories) {
                    score_depeche.add(new PaireChaineEntier(categorie.getNom(), categorie.score(depeche)));
                }
                String categorieMax = UtilitairePaireChaineEntier.chaineMax(score_depeche); // Sélection de la catégorie avec le score le plus élevé pour la dépêche actuelle
                writer.write(depeche.getId() + ": " + categorieMax + "\n"); // Indique l'ID de la dépêche et de sa catégorie prédite par le score le plus élevé
                // Vérification si la catégorie prédite est la même que la catégorie réelle
                if (categorieMax.compareTo(depeche.getCategorie()) == 0) {
                    verification = 1;
                } else {
                    verification = 0;
                }
                // Mise à jour des totaux et scores pour chaque catégorie
                for (int i = 0; i < categories.size(); i++) {
                    Categorie categorie = categories.get(i);
                    if (depeche.getCategorie().equals(categorie.getNom())) { // Vérification si la catégorie actuelle correspond à la catégorie réelle de la dépêche
                        scores[i] += verification;
                        totals[i]++;
                    }
                }
            }
            writer.write("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
            writer.write("\n");
            // Écriture des pourcentages pour chaque catégorie
            for (int i = 0; i < categories.size(); i++) {
                pourcentage(writer, categories.get(i).getNom(), scores[i], totals[i]);
            }
            // Calcul de la moyenne
            ArrayList<PaireChaineEntier> moyenneScores = new ArrayList<>();
            for (Categorie categorie : categories) {
                int index = categories.indexOf(categorie);
                moyenneScores.add(new PaireChaineEntier(categorie.getNom(), scores[index]));
            }
            float moyenneResult = UtilitairePaireChaineEntier.moyenne(moyenneScores);
            writer.write("MOYENNE : " + moyenneResult + "%");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour écrire le pourcentage d'une catégorie dans un fichier
    private static void pourcentage(FileWriter writer, String categorie, int score, int total) throws IOException {
        int pourcentage;
        // Calcul du pourcentage en évitant la division par zéro (assure que total n'est pas égal à 0)
        if (total != 0) {
            pourcentage = score * 100 / total;
        } else {
            pourcentage = 0;
        }
        writer.write(categorie + ": " + pourcentage + "%\n");
    }

    // Méthode de création du Dictionnaire pour les lexiques
    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie, String fichierMotsVide) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        ArrayList<String> motsVide = motsVide(fichierMotsVide); // Liste de mots à exclure (pronom, article, adjectif, etc.)
        for (Depeche depeche : depeches) {
            // Vérification de la catégorie de la dépêche
            if (depeche.getCategorie().equals(categorie)) {
                for (String mot : depeche.getMots()) {
                    // Vérification si le mot est à exclure
                    if (!motsVide.contains(mot)) {
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

    // Méthode pour calculer le score de chaque dépêche
    public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        for (Depeche d : depeches) {
            boolean verification = d.getCategorie().equals(categorie); // Vérification si la catégorie de la dépêche correspond à la catégorie en cours de traitement
            for (String mot : d.getMots()) {
                int i = UtilitairePaireChaineEntier.indicePourChaine(dictionnaire, mot);
                // Si le mot est présent dans le dictionnaire
                if (i != -1) {
                    PaireChaineEntier p = dictionnaire.get(i);
                    int score = p.getEntier();
                    // Incrémentation ou décrémentation du score en fonction de la présence de la catégorie
                    if (verification) {
                        score++;
                    } else {
                        score--;
                    }
                    PaireChaineEntier scoremaj = new PaireChaineEntier(p.getChaine(), score);
                    dictionnaire.set(i, scoremaj);
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
        ArrayList<PaireChaineEntier> dictionnaire = initDico(depeches, categorie, "MotsVide.txt"); // Inclus la gestion de mots vide selon le besoin de précision (à compléter au fur et à mesure)
        calculScores(depeches, categorie, dictionnaire); // Mise à jour des scores dans le dictionnaire
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

    // Méthode pour lire un fichier qui contient certains mots vide (pronoms, articles, adjectifs, lettres, etc...) dans un but d'amélioration de la précision
    private static ArrayList<String> motsVide(String fichierMotsVide) {
        ArrayList<String> motsVide = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(fichierMotsVide);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                motsVide.add(scanner.nextLine().trim().toLowerCase());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return motsVide;
    }

    // Méthode de calcul du temps d'exécution nécessaire pour créer les fichiers par l'Apprentissage Automatique
    public static long tempsExecution(ArrayList<Depeche> depeches, ArrayList<Categorie> categories) {
        long temps = 0;
        for (Categorie categorie : categories) {
            long debut = System.currentTimeMillis();
            generationLexique(depeches, categorie.getNom(), "Lexique-AA-" + categorie.getNom() + ".txt");
            long fin = System.currentTimeMillis();
            long execution = fin - debut;
            temps += execution;
        }
        return temps;
    }

    // Méthode d'affichage du lexique
    private static void afficherLexique(Categorie categorie) {
        System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
        System.out.println("Lexique de la catégorie " + categorie.getNom() + " :");
        System.out.println();
        ArrayList<PaireChaineEntier> lexique = categorie.getLexique();
        for (PaireChaineEntier paire : lexique) {
            System.out.println("Mot : " + paire.getChaine() + " | Entier : " + paire.getEntier());
        }
    }

    // ÉXECUTION - [MAIN]
    // Main, affichage et exécution de l'ensemble des méthodes. Mise en application
    public static void main(String[] args) {
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━•| ⊱▲⊰ |•━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("\nChargement des dépêches...\n"); // Chargement des dépêches en mémoire
        System.out.println("██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ 10%\n");
        System.out.println("██████████▒▒▒▒▒▒▒▒▒▒ 50%\n");
        System.out.println("████████████████████ 100%\n");
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");
        System.out.println("|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");

        // Création des catégories
        ArrayList<Categorie> categories = new ArrayList<>();
        Categorie environnementsciences = new Categorie("ENVIRONNEMENT-SCIENCES");
        Categorie culture = new Categorie("CULTURE");
        Categorie economie = new Categorie("ECONOMIE");
        Categorie politique = new Categorie("POLITIQUE");
        Categorie sports = new Categorie("SPORTS");

        // Partie Manuelle - Initialisation des lexiques
        environnementsciences.initLexique("Lexique-Manuel-ENVIRONNEMENT-SCIENCES.txt");
        culture.initLexique("Lexique-Manuel-CULTURE.txt");
        economie.initLexique("Lexique-Manuel-ECONOMIE.txt");
        politique.initLexique("Lexique-Manuel-POLITIQUE.txt");
        sports.initLexique("Lexique-Manuel-SPORTS.txt");

        // Ajout des catégories à la liste
        categories.add(environnementsciences);
        categories.add(culture);
        categories.add(economie);
        categories.add(politique);
        categories.add(sports);

        // Initialisations
        Scanner lecteur = new Scanner(System.in);
        char choix;
        int iddepeche;
        ArrayList<PaireChaineEntier> score = new ArrayList<>();

        // Choix par l'utilisateur de tout afficher ou alors d'afficher le minimum pour une meilleure lisibilité
        System.out.print("Appuyez 'E' pour afficher les dépêches de depeches.txt ou sur une autre touche pour passer à la question suivante : ");
        choix = lecteur.next().toUpperCase().charAt(0);
        lecteur.nextLine();
        System.out.println();
        if (Character.toUpperCase(choix) == 'E') {
            System.out.println("Affichage des dépêches de depeches.txt.");
            System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
            //Afficher toutes les dépêches
            for (int i = 0; i < depeches.size(); i++) {
                depeches.get(i).afficher();
            }
            // Afficher les lexiques pour chaque catégorie
            for (Categorie categorie : categories) {
                afficherLexique(categorie);
            }
        } else {
            System.out.println("Passage à l'étape suivante.");
        }

        // Saisie d'un mot présent dans le lexique par l'utilisateur
        System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
        System.out.print("Saisissez un mot contenu dans un lexique : ");
        String mot = lecteur.nextLine().toLowerCase();
        System.out.println();

        // Afficher le poids associé pour chaque catégorie pour le mot sélectionné
        for (Categorie categorie : categories) {
            int poids = UtilitairePaireChaineEntier.entierPourChaine(categorie.getLexique(), mot);
            System.out.println("Le poids associé au mot '" + mot + "' dans la catégorie "
                    + categorie.getNom() + " est : " + poids);
        }
        System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");

        // Saisie de l'utilisateur d'une dépêche pour connaître son score
        System.out.print("Veuillez saisir un ID de dépêche entre 0 et 499 : ");
        do {
            while (!lecteur.hasNextInt()) { // Vérifie si l'entrée est un entier
                System.out.print("\nVeuillez saisir un ID de dépêche valide (entier entre 0 et 499) : ");
                lecteur.next();
            }
            iddepeche = lecteur.nextInt();
            if (iddepeche < 0 || iddepeche > 499) {
                System.out.print("\nL'ID de dépêche doit être compris entre 0 et 499. Réessayez : ");
            }
        } while (iddepeche < 0 || iddepeche > 499);
        System.out.println();
        for (Categorie c : categories) {
            System.out.println("Le score de la dépêche " + iddepeche + " dans la catégorie " + c.getNom() + " est de : " + c.score(depeches.get(iddepeche)));
            score.add(new PaireChaineEntier(c.getNom(), c.score(depeches.get(iddepeche))));
        }
        System.out.println("\nLa catégorie de la dépêche " + iddepeche + " ayant le score le plus élevé est : " + UtilitairePaireChaineEntier.chaineMax(score) + "\n");
        System.out.println("|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");

        // Génération des lexiques pour chaque catégorie
        for (Categorie categorie : categories) {
            String nomFichier = "Lexique-AA-" + categorie.getNom() + ".txt";
            generationLexique(depeches, categorie.getNom(), nomFichier);
            System.out.println("Lexique pour la catégorie " + categorie.getNom() + " a été généré avec succès.");
        }

        // Afficher le temps de création des lexiques
        System.out.println();
        long tempslexiques = tempsExecution(depeches, categories);
        System.out.println("Le temps total d'exécution pour la création des lexiques est : " + tempslexiques + "ms");

        // Choix par l'utilisateur des tests sur depeches.txt ou test.txt
        do {
            System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
            System.out.print("Appuyez 'D' pour effectuer les tests sur depeches.txt ou 'T' pour effectuer les tests sur test.txt [Attention, les lexiques sont toujours générés sur la base de depeches.txt] : ");
            choix = lecteur.next().toUpperCase().charAt(0);
            lecteur.nextLine();
            System.out.println();
            if (Character.toUpperCase(choix) == 'D') {
                System.out.println("Aucun changement sur les dépêches.\n");
                System.out.println("Passage des Tests sur le fichier depeches.txt.");
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
            } else if (Character.toUpperCase(choix) == 'T') {
                System.out.println("Chargement des nouvelles dépêches...\n"); // Changement des dépêches en mémoire
                System.out.println("██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ 10%\n");
                System.out.println("██████████▒▒▒▒▒▒▒▒▒▒ 50%\n");
                System.out.println("████████████████████ 100%\n");
                depeches = lectureDepeches("./test.txt");
                System.out.println("Passage des Tests sur le fichier test.txt.");
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
            } else {
                System.out.println("Saisie invalide.");
            }
        } while (Character.toUpperCase(choix) != 'D' && Character.toUpperCase(choix) != 'T');

        // Choix par l'utilisateur d'afficher les dépêches de test.txt ou alors de passer à l'étape suivante
        if (choix == 'T') {
            System.out.print("Appuyez 'E' pour afficher les dépêches de text.txt ou sur une autre touche pour passer à la question suivante : ");
            choix = lecteur.next().toUpperCase().charAt(0);
            lecteur.nextLine();
            System.out.println();
            if (Character.toUpperCase(choix) == 'E') {
                System.out.println("Affichage des dépêches de test.txt.");
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
                //Afficher toutes les dépêches
                for (int i = 0; i < depeches.size(); i++) {
                    depeches.get(i).afficher();
                }
            } else {
                System.out.println("Passage à l'étape suivante.");
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
            }
        }

        // Choix par l'utilisateur d'afficher les résultats générés par les lexiques créés de façon manuels ou de façon par apprentissage automatique
        do {
            System.out.print("\nAppuyez sur 'M' pour afficher les résultats manuels, 'A' pour les résultats automatiques, ou 'E' pour quitter : ");
            choix = lecteur.next().toUpperCase().charAt(0);
            if (Character.toUpperCase(choix) == 'M') {
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
                // Partie Manuelle - Initialisation des lexiques
                environnementsciences.initLexique("Lexique-Manuel-ENVIRONNEMENT-SCIENCES.txt");
                culture.initLexique("Lexique-Manuel-CULTURE.txt");
                economie.initLexique("Lexique-Manuel-ECONOMIE.txt");
                politique.initLexique("Lexique-Manuel-POLITIQUE.txt");
                sports.initLexique("Lexique-Manuel-SPORTS.txt");
                classementDepeches(depeches, categories, "Resultats-Manuels.txt");
                try {
                    FileInputStream file = new FileInputStream("Resultats-Manuels.txt");
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
                    }
                    System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
                    scanner.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("\nAppuyez sur 'A' pour afficher les résultats automatiques, ou 'E' pour quitter : ");
                choix = lecteur.next().toUpperCase().charAt(0);
                if (Character.toUpperCase(choix) == 'A') {
                    System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
                    // Partie AA - Initialisation des Lexiques
                    environnementsciences.initLexique("Lexique-AA-ENVIRONNEMENT-SCIENCES.txt");
                    culture.initLexique("Lexique-AA-CULTURE.txt");
                    economie.initLexique("Lexique-AA-ECONOMIE.txt");
                    politique.initLexique("Lexique-AA-POLITIQUE.txt");
                    sports.initLexique("Lexique-AA-SPORTS.txt");
                    classementDepeches(depeches, categories, "Resultats-AA.txt");
                    try {
                        FileInputStream file = new FileInputStream("Resultats-AA.txt");
                        Scanner scanner = new Scanner(file);
                        while (scanner.hasNextLine()) {
                            System.out.println(scanner.nextLine());
                        }
                        System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
                        scanner.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (Character.toUpperCase(choix) == 'E') {
                    System.out.println("\nFin de programme !");
                    System.out.println("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━•| ⊱▼⊰ |•━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                    System.exit(0);
                } else {
                    System.out.println("\nChoix invalide. Fin de programme !");
                    System.out.println("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━•| ⊱▼⊰ |•━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                    System.exit(0);
                }
            } else if (Character.toUpperCase(choix) == 'A') {
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
                // Partie AA - Initialisation des Lexiques
                environnementsciences.initLexique("Lexique-AA-ENVIRONNEMENT-SCIENCES.txt");
                culture.initLexique("Lexique-AA-CULTURE.txt");
                economie.initLexique("Lexique-AA-ECONOMIE.txt");
                politique.initLexique("Lexique-AA-POLITIQUE.txt");
                sports.initLexique("Lexique-AA-SPORTS.txt");
                classementDepeches(depeches, categories, "Resultats-AA.txt");
                try {
                    FileInputStream file = new FileInputStream("Resultats-AA.txt");
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
                    }
                    System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
                    scanner.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("\nAppuyez sur 'M' pour afficher les résultats manuels, ou 'E' pour quitter : ");
                choix = lecteur.next().toUpperCase().charAt(0);
                if (Character.toUpperCase(choix) == 'M') {
                    System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
                    // Partie Manuelle - Initialisation des lexiques
                    environnementsciences.initLexique("Lexique-Manuel-ENVIRONNEMENT-SCIENCES.txt");
                    culture.initLexique("Lexique-Manuel-CULTURE.txt");
                    economie.initLexique("Lexique-Manuel-ECONOMIE.txt");
                    politique.initLexique("Lexique-Manuel-POLITIQUE.txt");
                    sports.initLexique("Lexique-Manuel-SPORTS.txt");
                    classementDepeches(depeches, categories, "Resultats-Manuels.txt");
                    try {
                        FileInputStream file = new FileInputStream("Resultats-Manuels.txt");
                        Scanner scanner = new Scanner(file);
                        while (scanner.hasNextLine()) {
                            System.out.println(scanner.nextLine());
                        }
                        System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
                        scanner.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (Character.toUpperCase(choix) == 'E') {
                    System.out.println("\nFin de programme !");
                    System.out.println("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━•| ⊱▼⊰ |•━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                    System.exit(0);
                } else {
                    System.out.println("\nChoix non invalide. Fin de programme !");
                    System.out.println("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━•| ⊱▼⊰ |•━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                    System.exit(0);
                }
            } else if (Character.toUpperCase(choix) == 'E') {
                System.out.println("\nFin de programme !");
                System.out.println("\n┗━━━━━━━━━━━━━━━━━━━━━━━━━•| ⊱▼⊰ |•━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                System.exit(0);
            } else {
                System.out.println("\nChoix invalide. Veuillez réessayer.");
                System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
            }
        } while (true);
    }
}
