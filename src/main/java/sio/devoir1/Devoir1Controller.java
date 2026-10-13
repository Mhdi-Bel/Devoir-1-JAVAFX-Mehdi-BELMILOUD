package sio.devoir1;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sio.devoir1.model.Equipe;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class Devoir1Controller implements Initializable
{
    // Clé = Nom du tournoi
    // Value = Une TreeMap qui stocke :
    // Clé = nom de l'équipe
    // Value = l'objet EQUIPE avec ses scores
    private HashMap<String, TreeMap<String, Equipe>> tournois;

    @javafx.fxml.FXML
    private TextField txtScore;
    @javafx.fxml.FXML
    private Label lblResultat;
    @javafx.fxml.FXML
    private TextField txtEquipe;
    @javafx.fxml.FXML
    private TreeView tvTournois;
    @javafx.fxml.FXML
    private ComboBox cbTournois;
    @javafx.fxml.FXML
    private ComboBox cbEquipes;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tournois = new HashMap<>();
        chargerDonnees();
        chargerTreeView();
        // On ajoute le nom de chaque tournoi dans la ComboBox
        for (String nomTournoi : tournois.keySet()) {
            cbTournois.getItems().add(nomTournoi);
        }
    }

    private void chargerDonnees()
    {
        // Voici au départ le jeu d'essais

        // 2 tournois : Robotique et Cyber
        tournois.put("Robotique", new TreeMap<>());
        tournois.put("Cyber", new TreeMap<>());

        // Les 3 équipes du tournoi "Robotique"
        Equipe e1 = new Equipe("Team Eclipse");
        e1.getScores().add(50);
        e1.getScores().add(40);
        e1.getScores().add(30);

        Equipe e2 = new Equipe("Team Alpha");
        e2.getScores().add(20);
        e2.getScores().add(25);
        e2.getScores().add(35);

        Equipe e3 = new Equipe("Team Nova");
        e3.getScores().add(60);
        e3.getScores().add(10);

        tournois.get("Robotique").put(e1.getNom(), e1);
        tournois.get("Robotique").put(e2.getNom(), e2);
        tournois.get("Robotique").put(e3.getNom(), e3);

        // Les 2 équipes du tournoi "Cyber"
        Equipe e4 = new Equipe("Phoenix");
        e4.getScores().add(35);
        e4.getScores().add(45);

        Equipe e5 = new Equipe("Titans");
        e5.getScores().add(50);
        e5.getScores().add(40);

        tournois.get("Cyber").put(e4.getNom(), e4);
        tournois.get("Cyber").put(e5.getNom(), e5);
    }

    private void chargerTreeView()
    {
        TreeItem<String> racine = new TreeItem<>("Tournois");
        racine.setExpanded(true);

        // tournoi
        for (String nomTournoi : tournois.keySet()) {
            TreeItem<String> noeudTournoi = new TreeItem<>(nomTournoi);
            noeudTournoi.setExpanded(true);

            // equipe
            for (Equipe equipe : tournois.get(nomTournoi).values()) {
                TreeItem<String> noeudEquipe = new TreeItem<>(equipe.getNom() + " : " + equipe.getTotalPoints() + " points");
                noeudTournoi.getChildren().add(noeudEquipe);
            }

            racine.getChildren().add(noeudTournoi);
        }

        tvTournois.setRoot(racine);
    }

    private void chargerEquipes()
    {
        cbEquipes.getItems().clear();

        String nomTournoi = (String) cbTournois.getValue();

        for (String nomEquipe : tournois.get(nomTournoi).keySet()) {
            cbEquipes.getItems().add(nomEquipe);
        }
    }

    @javafx.fxml.FXML
    public void ajouterEquipe(ActionEvent actionEvent)
    {
        String nomTournoi = (String) cbTournois.getValue();
        String nomEquipe = txtEquipe.getText();

        // nom vide
        if (nomEquipe.isEmpty()) {
            lblResultat.setText("Veuillez saisir le nom de l'équipe");
        }
        // equipe deja dans le tournoi
        else if (tournois.get(nomTournoi).containsKey(nomEquipe)) {
            lblResultat.setText("Cette équipe existe déjà dans ce tournoi");
        }
        // OK
        else {
            Equipe nouvelleEquipe = new Equipe(nomEquipe);
            tournois.get(nomTournoi).put(nomEquipe, nouvelleEquipe);
            lblResultat.setText("L'équipe a été ajoutée avec succès");

            chargerTreeView();
            chargerEquipes();
        }
    }

    @javafx.fxml.FXML
    public void ajouterScore(ActionEvent actionEvent)
    {
        // pas d'equipe choisis
        if (cbEquipes.getValue() == null) {
            lblResultat.setText("Veuillez sélectionner une équipe");
        }
        // score vide
        else if (txtScore.getText().isEmpty()) {
            lblResultat.setText("Veuillez saisir un score");
        }
        // (bonus) si on entre des lettre
        else if (!txtScore.getText().matches("\\d+")) {
            lblResultat.setText("Veuillez saisir un score valide");
        }
        // OK
        else {
            String nomTournoi = (String) cbTournois.getValue();
            String nomEquipe = (String) cbEquipes.getValue();
            int score = Integer.parseInt(txtScore.getText());

            tournois.get(nomTournoi).get(nomEquipe).getScores().add(score);

            lblResultat.setText("Le score a été ajouté avec succès");

            chargerTreeView();
        }
    }

    @javafx.fxml.FXML
    public void calculTotalTournoi(ActionEvent actionEvent)
    {
        String nomTournoi = (String) cbTournois.getValue();

        int total = 0;
        for (Equipe equipe : tournois.get(nomTournoi).values()) {
            total = total + equipe.getTotalPoints();
        }

        lblResultat.setText("Total " + nomTournoi + " : " + total + " points");
    }

    @javafx.fxml.FXML
    public void meilleureEquipe(ActionEvent actionEvent)
    {
        Equipe meilleure = null;

        for (TreeMap<String, Equipe> equipes : tournois.values()) {
            for (Equipe equipe : equipes.values()) {
                if (meilleure == null || equipe.getTotalPoints() > meilleure.getTotalPoints()) {
                    meilleure = equipe;
                }
            }
        }

        lblResultat.setText("La meilleure équipe est " + meilleure.getNom() + " avec " + meilleure.getTotalPoints() + " points !");
    }

    @javafx.fxml.FXML
    public void renommerEquipe(ActionEvent actionEvent)
    {
        String nomTournoi = (String) cbTournois.getValue();
        String ancienNom = (String) cbEquipes.getValue();
        String nouveauNom = txtEquipe.getText();

        // pas d'equipe choisis
        if (ancienNom == null) {
            lblResultat.setText("Veuillez choisir une équipe");
        }
        // nom vide
        else if (nouveauNom.isEmpty()) {
            lblResultat.setText("Veuillez saisir le nouveau nom de l'équipe");
        }
        // existe deja dans le tournoi
        else if (tournois.get(nomTournoi).containsKey(nouveauNom)) {
            lblResultat.setText("Cette équipe est déjà dans le tournoi");
        }
        // OK
        else {
            Equipe equipe = tournois.get(nomTournoi).remove(ancienNom); // enleve lancienne cles
            equipe.setNom(nouveauNom); // met le nouveau nom
            tournois.get(nomTournoi).put(nouveauNom, equipe); // met le nom en cles

            chargerTreeView();
            chargerEquipes();
        }
    }

    @javafx.fxml.FXML
    public void chargerEquipes(ActionEvent actionEvent)
    {
        chargerEquipes();
    }
}
