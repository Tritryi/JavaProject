import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ClassificationKNN {
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

    // Méthode pour calculer le nombre de termes communs entre deux dépêches (hors mots vides)
    private static int termesCommuns(Depeche dep1, Depeche dep2) {
        ArrayList<String> motsVide = motsVide("MotsVide.txt");
        int count = 0;

        for (String mot1 : dep1.getMots()) {
            if (!motsVide.contains(mot1) && dep2.getMots().contains(mot1)) {
                count++;
            }
        }

        return count;
    }

    public static void classementDepechesAvecKNN(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier, int k) {
        try {
            FileWriter writer = new FileWriter(nomFichier);
            // Initialisation des tableaux pour stocker les valeurs de chaque catégorie
            int[] totals = new int[categories.size()];
            int[] scores = new int[categories.size()];
            int verification;

            for (Depeche depeche : depeches) {
                ArrayList<PaireChaineEntier> score_depeche = new ArrayList<>();

                // Calcul des scores pour chaque catégorie
                for (Categorie categorie : categories) {
                    score_depeche.add(new PaireChaineEntier(categorie.getNom(), categorie.score(depeche)));
                }

                // Ajout de la comparaison KNN
                ArrayList<Depeche> knnVoisins = trouverKNNVoisins(depeche, depeches, k);
                String categorieKNN = categorieMajoritaireKNN(knnVoisins, categories);

                writer.write(depeche.getId() + ": " + categorieKNN + "\n");

                // Vérification si la catégorie prédite est la même que la catégorie réelle
                verification = (categorieKNN.compareTo(depeche.getCategorie()) == 0) ? 1 : 0;

                // Mise à jour des totaux et scores pour chaque catégorie
                for (int i = 0; i < categories.size(); i++) {
                    Categorie categorie = categories.get(i);
                    if (depeche.getCategorie().equals(categorie.getNom())) {
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

    private static ArrayList<Depeche> trouverKNNVoisins(Depeche depeche, ArrayList<Depeche> depeches, int k) {
        // Créez une liste pour stocker les distances entre la dépêche donnée et les autres dépêches
        ArrayList<PaireDepecheDouble> distances = new ArrayList<>();

        // Calculez la distance (similarity) entre la dépêche donnée et chaque autre dépêche
        for (Depeche autreDepeche : depeches) {
            if (!depeche.equals(autreDepeche)) {
                double distance = termesCommuns(depeche, autreDepeche); // À remplacer si nécessaire
                distances.add(new PaireDepecheDouble(autreDepeche, distance));
            }
        }

        // Triez la liste des distances par ordre croissant
        distances.sort(Comparator.comparing(PaireDepecheDouble::getValeur));

        // Sélectionnez les K plus proches voisins
        ArrayList<Depeche> voisins = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            voisins.add(distances.get(i).getPaire());
        }

        return voisins;
    }

    private static String categorieMajoritaireKNN(ArrayList<Depeche> voisins, ArrayList<Categorie> categories) {
        // Comptez le nombre d'occurrences de chaque catégorie parmi les voisins
        HashMap<String, Integer> occurrences = new HashMap<>();
        for (Depeche voisin : voisins) {
            occurrences.merge(voisin.getCategorie(), 1, Integer::sum);
        }

        // Trouvez la catégorie avec le nombre maximum d'occurrences
        String categorieMajoritaire = Collections.max(occurrences.entrySet(), Map.Entry.comparingByValue()).getKey();

        return categorieMajoritaire;
    }

    // Classe auxiliaire pour stocker une paire de Depeche et Double
    private static class PaireDepecheDouble {
        private final Depeche paire;
        private final double valeur;

        public PaireDepecheDouble(Depeche paire, double valeur) {
            this.paire = paire;
            this.valeur = valeur;
        }

        public Depeche getPaire() {
            return paire;
        }

        public double getValeur() {
            return valeur;
        }
    }

    public static void main(String[] args) {
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");

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

        Scanner lecteur = new Scanner(System.in);
        System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|\n");
        System.out.print("Veuillez saisir la valeur de K pour la méthode des K plus proches voisins : ");
        int k = lecteur.nextInt();
        System.out.println("\nCalcul des résultats avec la méthode KNN (K=" + k + ")...");

        // Classement des dépêches avec la méthode KNN
        try {
            FileWriter knnWriter = new FileWriter("Resultats-KNN.txt");
            knnWriter.write("Résultats de la méthode KNN (K=" + k + ")\n");

            for (Depeche depeche : depeches) {
                // Trouver les K plus proches voisins de la dépêche actuelle
                ArrayList<Depeche> voisins = trouverKNNVoisins(depeche, depeches, k);

                // Trouver la catégorie majoritaire parmi les voisins
                String categorieKNN = categorieMajoritaireKNN(voisins, categories);

                // Écrire le résultat dans le fichier
                knnWriter.write(depeche.getId() + ": " + categorieKNN + "\n");
            }

            knnWriter.close();
            System.out.println("Les résultats KNN ont été enregistrés dans le fichier 'Resultats-KNN.txt'.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        classementDepechesAvecKNN(depeches, categories, "Resultats-KNN.txt", k);
        try {
            FileInputStream file = new FileInputStream("Resultats-KNN.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            System.out.println("\n|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
