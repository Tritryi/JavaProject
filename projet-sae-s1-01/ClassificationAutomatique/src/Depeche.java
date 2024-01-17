import java.util.ArrayList;

public class Depeche {
    // INITIALISATION DES VARIABLES
    private String id;
    private String date;
    private String categorie;
    private String contenu;
    private ArrayList<String> mots;

    // CONSTRUCTEURS
    public Depeche(String id, String date, String categorie, String contenu) {
        this.id = id;
        this.date = date;
        this.categorie = categorie;
        this.contenu = contenu;
        this.mots = decoupeEnMots(contenu);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setMots(ArrayList<String> mots) {
        this.mots = mots;
    }

    // ACCESSEURS
    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getContenu() {
        return contenu;
    }

    public ArrayList<String> getMots() {
        return mots;
    }

    // MÉTHODES
    // Méthode de récupération mot à mot
    private ArrayList<String> decoupeEnMots(String contenu) {
        String chaine = contenu.toLowerCase();
        chaine = chaine.replace('\n', ' ');
        chaine = chaine.replace('.', ' ');
        chaine = chaine.replace(',', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('\"', ' ');
        chaine = chaine.replace('(', ' ');
        chaine = chaine.replace(')', ' ');
        String[] tabchaine = chaine.split(" ");
        ArrayList<String> resultat = new ArrayList<String>();
        for (int i = 0; i < tabchaine.length; i++) {
            if (!tabchaine[i].equals("")) {
                resultat.add(tabchaine[i]);
            }
        }
        return resultat;
    }

    // Méthode d'affichage des dépêches
    public void afficher() {
        System.out.println("|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
        System.out.println("Depêche " + id);
        System.out.println("Date : " + date);
        System.out.println("Catégorie : " + categorie);
        System.out.println(contenu);
        System.out.println();
        System.out.println("|━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━|");
    }
}
