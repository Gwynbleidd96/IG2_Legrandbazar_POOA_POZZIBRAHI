package viewPackage;

import controllerPackage.ApplicationController;
import exceptionPackage.*;
import modelPackage.OrdrePreparation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.ArrayList;

public class PanneauModification extends JPanel {
    private JPanel panneauModifications, panneauBoutons, panneauDateVente, panneauDatePreparation;
    private ArrayList<OrdrePreparation> ordres;
    private JComboBox ordresJCombo ,codeBarreCombo,matriculeCuiCombo,matriculeResCombo,recetteCombo;
    private JLabel ordresLabel, recetteLabel, quantitePrevueLabel, quantiteProduiteLabel, dateVenteLabel, datePreparationLabel, remarqueLabel, codeBarreLabel, matriculeCuiLabel, matriculeResLabel;
    private JTextField quantitePrevueText, quantiteProduiteText, remarqueText;
    private JButton validation, retour, reinitialiser;
    private PanneauBienvenue panneauBienvenue;
    private JRadioButton urgent, pasUrgent;
    private ButtonGroup buttonGroup;
    private PanneauSpinnerDate spinnerDateVente, spinnerDatePrep;
    private ApplicationController controller;

    private ArrayList<String> recettes, codeBarres, matriculesCui, matriculesRes;
    private OrdrePreparation ordrePreparation;
    private int iOrdre = 0;
    private JCheckBox bouttonDateVente, bouttonDatePreparation;

    public PanneauModification() {
        //On créé les différents panneaux
        this.setLayout(new BorderLayout());
        panneauModifications = new JPanel();
        panneauModifications.setLayout(new GridLayout(12, 2, 3, 3));
        this.add(panneauModifications, BorderLayout.CENTER);
        panneauBoutons = new JPanel();
        panneauBoutons.setLayout(new FlowLayout());
        this.add(panneauBoutons, BorderLayout.SOUTH);

        //On créé les labels + les champs
        ordresLabel = new JLabel("Listes des ordres :");
        ordresLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ordresLabel.setToolTipText("Choississez l'ordre que vous voulez modifier ");
        panneauModifications.add(ordresLabel);
        setController(new ApplicationController());

        ordres = new ArrayList<>();
        try {
            ordres = controller.getAllOrdres();
        } catch (AllOrdresException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (ModelException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        ordresJCombo = new JComboBox();
        String dateString = "";
        for (OrdrePreparation ordresCombox : ordres) {
            dateString = ordresCombox.conversionDateVersString(ordresCombox.getDate());
            ordresJCombo.addItem(ordresCombox.getNumeroSequentiel() + " -> " + dateString);
        }
        ordresJCombo.setMaximumRowCount(5);
        panneauModifications.add(ordresJCombo);

        ordresJCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changementOrdre();
                }

            }
        });

        recetteLabel = new JLabel("Recette :");
        recetteLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        recetteLabel.setToolTipText("Nom de la recette à changer");
        panneauModifications.add(recetteLabel);

        recettes = new ArrayList<>();
        try {
            recettes = controller.getAllRecetteNom();
        } catch (GeneralException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        recetteCombo = new JComboBox();
        for (String recetteNom : recettes) {
            recetteCombo.addItem(recetteNom);
        }
        panneauModifications.add(recetteCombo);

        quantitePrevueLabel = new JLabel("Quantité prévue :");
        quantitePrevueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        quantitePrevueLabel.setToolTipText("Quantité prévue à la création de l'ordre");
        panneauModifications.add(quantitePrevueLabel);
        quantitePrevueText = new JTextField();
        panneauModifications.add(quantitePrevueText);

        quantiteProduiteLabel = new JLabel("Quantité produite : ");
        quantiteProduiteLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        quantiteProduiteLabel.setToolTipText("Quantité effectivement produite");
        panneauModifications.add(quantiteProduiteLabel);
        quantiteProduiteText = new JTextField();
        panneauModifications.add(quantiteProduiteText);

        bouttonDateVente = new JCheckBox("Encoder une date de vente");
        bouttonDateVente.setHorizontalAlignment(SwingConstants.RIGHT);
        panneauModifications.add(bouttonDateVente);

        bouttonDateVente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerDateVente.getSpinnerDate().setEnabled(bouttonDateVente.isSelected());
            }
        });


        panneauDateVente = new JPanel();
        dateVenteLabel = new JLabel("Date vente : ");
        dateVenteLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateVenteLabel.setToolTipText("[FACULTATIF] Date de mise en vente");
        panneauDateVente.add(dateVenteLabel);
        spinnerDateVente = new PanneauSpinnerDate();
        spinnerDateVente.getSpinnerDate().setEnabled(false);
        panneauDateVente.add(spinnerDateVente);
        panneauModifications.add(panneauDateVente);

        bouttonDatePreparation = new JCheckBox("Encoder une date de préparation");
        bouttonDatePreparation.setHorizontalAlignment(SwingConstants.RIGHT);
        panneauModifications.add(bouttonDatePreparation);

        bouttonDatePreparation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerDatePrep.getSpinnerDate().setEnabled(bouttonDatePreparation.isSelected());
            }
        });

        panneauDatePreparation = new JPanel();
        datePreparationLabel = new JLabel("Date préparation :");
        datePreparationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        datePreparationLabel.setToolTipText("[FACULTATIF] Date lors de la préparation");
        panneauDatePreparation.add(datePreparationLabel);
        spinnerDatePrep = new PanneauSpinnerDate();
        spinnerDatePrep.getSpinnerDate().setEnabled(false);
        panneauDatePreparation.add(spinnerDatePrep);
        panneauModifications.add(panneauDatePreparation);

        remarqueLabel = new JLabel("Remarque : ");
        remarqueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panneauModifications.add(remarqueLabel);
        remarqueText = new JTextField();
        panneauModifications.add(remarqueText);

        urgent = new JRadioButton("Est urgent", false);
        urgent.setHorizontalAlignment(SwingConstants.LEFT);
        /*urgent.setBackground(Color.RED);*/
        panneauModifications.add(urgent);
        pasUrgent = new JRadioButton("N'est pas urgent", true);
        pasUrgent.setHorizontalAlignment(SwingConstants.RIGHT);
        /*pasUrgent.setBackground(Color.RED);*/
        panneauModifications.add(pasUrgent);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(urgent);
        buttonGroup.add(pasUrgent);

        codeBarreLabel = new JLabel("Code barre :");
        codeBarreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        codeBarreLabel.setToolTipText("Référence vers le type d'article quand il sera mis en vente");
        panneauModifications.add(codeBarreLabel);

        codeBarres = new ArrayList<>();
        try {
            codeBarres = controller.getCodeBarres();
        } catch (GeneralException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        codeBarreCombo = new JComboBox();
        codeBarreCombo.addItem("Pas d'article");
        for (String codeBarre : codeBarres) {
            codeBarreCombo.addItem(codeBarre);
        }
        panneauModifications.add(codeBarreCombo);


        matriculeCuiLabel = new JLabel("Matricule cuisinier :");
        matriculeCuiLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        matriculeCuiLabel.setToolTipText("Référence vers le cuisinier à qui la préparation a été attribué");
        panneauModifications.add(matriculeCuiLabel);

        matriculesCui = new ArrayList<>();
        try {
            matriculesCui = controller.getMatriculesCui();
        } catch (GeneralException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        matriculeCuiCombo = new JComboBox();
        matriculeCuiCombo.addItem("Pas de cuisinier");
        for (String matriculeCui : matriculesCui) {
            matriculeCuiCombo.addItem(matriculeCui);
        }
        panneauModifications.add(matriculeCuiCombo);


        matriculeResLabel = new JLabel("Matricule responsable :");
        matriculeResLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        matriculeResLabel.setToolTipText("Référence vers la responsable des ventes qui a créé l'ordre");
        panneauModifications.add(matriculeResLabel);

        matriculesRes = new ArrayList<>();
        try {
            matriculesRes = controller.getMatriculesRes();
        } catch (GeneralException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        matriculeResCombo = new JComboBox();
        for (String matriculeRes : matriculesRes) {
            matriculeResCombo.addItem(matriculeRes);
        }
        panneauModifications.add(matriculeResCombo);

        //Ajout des boutons au panneauBoutons
        retour = new JButton("Retour");
        panneauBoutons.add(retour);
        validation = new JButton("Validation");
        panneauBoutons.add(validation);
        reinitialiser = new JButton("Réinitialiser");
        panneauBoutons.add(reinitialiser);

        //Ajout des écouteurs aux boutons
        ButtonsAndTextsListener buttonsAndTextsListener = new ButtonsAndTextsListener();
        //JComboxListener jComboxListener = new JComboxListener();  ->>>>>>>>>>>>>>>>>>>>>>>>>>>
        retour.addActionListener(buttonsAndTextsListener);
        validation.addActionListener(buttonsAndTextsListener);
        reinitialiser.addActionListener(buttonsAndTextsListener);
        //ordresJCombo.addItemListener(jComboxListener);   // ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


        if(! ordres.isEmpty()) {
            changementOrdre();  //  --> met les modif pour l'ordre
        }

    }

    private class ButtonsAndTextsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == retour) {
                PanneauModification.this.removeAll();
                PanneauModification.this.panneauBienvenue = new PanneauBienvenue();
                PanneauModification.this.add(panneauBienvenue);
                PanneauModification.this.repaint();
                PanneauModification.this.validate();
            } else {
                if (e.getSource() == validation) {
                    validation();
                } else {
                    if (e.getSource() == reinitialiser) {
                        reinitialiser();
                    }
                }
            }
        }
    }




   /*private class JComboxListener implements ItemListener
    {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                iOrdre = ordresJCombo.getSelectedIndex();
               JOptionPane.showMessageDialog(null, ordres.get(iOrdre).toString(), "Ordre de préparation", JOptionPane.INFORMATION_MESSAGE);
                fenetreModification = new FenetreModification(ordres, iOrdre);
            }
        }
    }*/

    public void setController(ApplicationController controller) {
        this.controller = controller;
    }

    public void reinitialiser() {
        PanneauModification.this.removeAll();
        PanneauModification.this.add(new PanneauModification());
        PanneauModification.this.validate();
    }

    public void validation() {
        Integer quantitePrevue = controller.conversionStringVersInteger(quantitePrevueText.getText());
        if (quantitePrevueText.getText().isEmpty() || quantitePrevue == null || quantitePrevue <= 0)
        {
            JOptionPane.showMessageDialog(null, "Quantité prévue incorrecte !", "Erreur", JOptionPane.ERROR_MESSAGE);
            quantitePrevueText.setBackground(Color.RED);
        }
        else
        {
            quantitePrevueText.setBackground(Color.WHITE);
        }

        Integer quantiteProduite = null;
        if (!quantiteProduiteText.getText().isEmpty())
        {
            quantiteProduite = controller.conversionStringVersInteger(quantiteProduiteText.getText());
            if (quantiteProduite == null || quantiteProduite <= 0)
            {
                JOptionPane.showMessageDialog(null, "Quantité produite incorrecte !", "Erreur", JOptionPane.ERROR_MESSAGE);
                quantiteProduiteText.setBackground(Color.RED);
            }
            else
            {
                quantiteProduiteText.setBackground(Color.WHITE);
            }
        }
        //Modification de l'ordre dans la DB
        ordrePreparation = new OrdrePreparation();
        if (!ordres.isEmpty()) {
            try {
                //Récupération de la date et du numéro séquentiel via le JCombobox
                ordrePreparation.setDate(ordres.get(iOrdre).getDate());
                ordrePreparation.setNumeroSequentiel(ordres.get(iOrdre).getNumeroSequentiel());
                ordrePreparation.setQuantitePrevue(quantitePrevue);
                ordrePreparation.setQuantiteProduite(quantiteProduite);
                if (bouttonDateVente.isSelected()) {
                    ordrePreparation.setDateVente(spinnerDateVente.getDate());
                } else {
                    ordrePreparation.setDateVente(null);
                }
                if (bouttonDatePreparation.isSelected()) {
                    ordrePreparation.setDatePreparation(spinnerDatePrep.getDate());
                } else {
                    ordrePreparation.setDatePreparation(null);
                }

                if (remarqueText.getText().isEmpty()) {
                    ordrePreparation.setRemarque(null);  // Met a null car facultatif
                } else {
                    ordrePreparation.setRemarque(remarqueText.getText());
                }
                ordrePreparation.setEstUrgent(urgent.isSelected());
                ordrePreparation.setNom(recettes.get(recetteCombo.getSelectedIndex()));
                if (codeBarreCombo.getSelectedIndex() == 0 || codeBarreCombo.getSelectedItem().equals("Pas d'article")) {
                    ordrePreparation.setCodeBarre(null);
                } else {
                    //System.out.println(codeBarreCombo.getSelectedItem());
                    char cB = codeBarreCombo.getSelectedItem().toString().charAt(0);
                    Integer cBN = Character.getNumericValue(cB);
                    ordrePreparation.setCodeBarre(cBN);
                }
                if (matriculeCuiCombo.getSelectedIndex() == 0 || matriculeCuiCombo.getSelectedItem().equals("Pas de cuisinier")) {
                    ordrePreparation.setMatricule_Cui(null);
                } else {
                    char matriCui = matriculeCuiCombo.getSelectedItem().toString().charAt(0);
                    Integer matriC = Character.getNumericValue(matriCui);
                    ordrePreparation.setMatricule_Cui(matriC);
                }
                char matriRes = matriculesRes.get(matriculeResCombo.getSelectedIndex()).charAt(0);
                Integer matriR = Character.getNumericValue(matriRes);
                ordrePreparation.setMatricule_Res(matriR);
                controller.updateOrdre(ordrePreparation);

                //Affichage d'un message de confirmation  de la modification + réinitialisation des champs
                JOptionPane.showMessageDialog(null, "Confirmation de la modification de l'ordre !", "Information", JOptionPane.INFORMATION_MESSAGE);
                reinitialiser();
            } catch (ModelException eME) {
                JOptionPane.showMessageDialog(null, eME.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (UpdateOrdreException eUO) {
                JOptionPane.showMessageDialog(null, eUO.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void changementOrdre() {
        OrdrePreparation ordre = new OrdrePreparation();
        ordre = ordres.get(ordresJCombo.getSelectedIndex());

        int i = 0;
               // crer une fonction dans les jCombo peu être pas mal, je trouve pas si une existe
        while (i < recetteCombo.getItemCount()) {
            if (ordre.getNom().equals(recetteCombo.getItemAt(i).toString())) {
                recetteCombo.setSelectedIndex(i);
               break; // est-ce propre ? :O
                 }
            i++;
            }

        quantitePrevueText.setText(ordre.getQuantitePrevue().toString());
        if (ordre.getQuantiteProduite() != null) {
            quantiteProduiteText.setText(ordre.getQuantiteProduite().toString());
        } else {
            quantiteProduiteText.setText("");
        }

        if (ordre.getRemarque() != null) {
            remarqueText.setText(ordre.getRemarque().toString());
        } else {
            remarqueText.setText("");
        }

        if (ordre.getEstUrgent()) {
            urgent.setSelected(true);
        } else {
            pasUrgent.setSelected(true);
        }

        if (ordre.getDateVente() == null) {
            if (bouttonDateVente.isSelected()) {
                bouttonDateVente.doClick();
            }
        } else {
            if (!bouttonDateVente.isSelected()) {
                bouttonDateVente.doClick();
            }
            //System.out.println(ordre.getDateVente().getTime());
            spinnerDateVente.setDate(ordre.getDateVente());  // ICI
        }

        if (ordre.getDatePreparation() == null) {
            if (bouttonDatePreparation.isSelected()) {
                bouttonDatePreparation.doClick();
            }
        } else {
            if (!bouttonDatePreparation.isSelected()) {
                bouttonDatePreparation.doClick();
            }
            spinnerDatePrep.setDate(ordre.getDatePreparation());
        }

        // crer une fonction dans les jCombo peu être pas mal, je trouve pas si une existe déja
        if (ordre.getCodeBarre() != null) {

            i = 1;
            while (i < codeBarreCombo.getItemCount()) {
                String [] motSepare = codeBarreCombo.getItemAt(i).toString().split(" ");
                if (ordre.getCodeBarre().equals(Integer.parseInt(motSepare[0]))) {
                    codeBarreCombo.setSelectedIndex(i);
                    break; // est-ce propre ? :O
                }
                i++;
            }

        }else{
            codeBarreCombo.setSelectedIndex(0);
        }
        // crer une fonction dans les jCombo peu être pas mal, je trouve pas si une existe déja
        if (ordre.getMatricule_Cui() != null) {
            i = 1;
            // crer une fonction dans les jCombo peu être pas mal, je trouve pas si une existe
            while (i < matriculeCuiCombo.getItemCount()) {
                String [] motSepare = matriculeCuiCombo.getItemAt(i).toString().split(" ");
                if (ordre.getMatricule_Cui().equals(Integer.parseInt(motSepare[0]))) {
                    matriculeCuiCombo.setSelectedIndex(i);
                    break; // est-ce propre ? :O
                }
                i++;
            }
            } else{
            matriculeCuiCombo.setSelectedIndex(0);
        }

        if (ordre.getMatricule_Res() != null) {
            i = 0;
            while (i < matriculeResCombo.getItemCount()) {
                String [] motSepare = matriculeResCombo.getItemAt(i).toString().split(" ");
                if (ordre.getMatricule_Res().equals(Integer.parseInt(motSepare[0]))) {
                    matriculeResCombo.setSelectedIndex(i);
                    break; // est-ce propre ? :O
                }
                i++;
            }
        }

    }
}
