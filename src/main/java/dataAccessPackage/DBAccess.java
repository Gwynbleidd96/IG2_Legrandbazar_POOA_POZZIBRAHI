package dataAccessPackage;

import exceptionPackage.*;
import modelPackage.OrdrePreparation;
import modelPackage.Recherche2;
import modelPackage.Recherche3;
import modelPackage.Reservation;

import java.awt.desktop.SystemSleepEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class DBAccess implements DataAccess
{
    private OrdrePreparation ordrePreparation;
    private Reservation reservation;
    private Connection connection;
    private PreparedStatement statement;

    public void addOrdre (OrdrePreparation ordrePreparation) throws AddOrdreException
    {
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new AddOrdreException("Erreur connexion !");

        try
        {
            String sql = "insert into ordrepreparation (Date, NumeroSequentiel, QuantitePrevue, QuantiteProduite, DateVente, DatePreparation, Remarque, estUrgent, Nom, CodeBarre, Matricule_Cui, Matricule_Res) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = new java.sql.Date(ordrePreparation.getDate().getTimeInMillis());
            statement.setDate(1, sqlDate);
            statement.setInt(2, ordrePreparation.getNumeroSequentiel());
            statement.setInt(3, ordrePreparation.getQuantitePrevue());

            if (ordrePreparation.getQuantiteProduite() != null)
            {
                statement.setInt(4, ordrePreparation.getQuantiteProduite());
            }
            else
            {
                statement.setNull(4, Types.INTEGER);
            }

            if (ordrePreparation.getDateVente() != null)
            {
                sqlDate.setTime(ordrePreparation.getDateVente().getTimeInMillis());
                statement.setDate(5, sqlDate);
            }
            else
            {
                statement.setNull(5, Types.TIMESTAMP);
            }

            if (ordrePreparation.getDatePreparation() != null)
            {
                sqlDate.setTime(ordrePreparation.getDatePreparation().getTimeInMillis());
                Timestamp datePrepa = new Timestamp(sqlDate.getTime());
                statement.setTimestamp(6,datePrepa);

                }
            else
            {
                statement.setNull(6, Types.TIMESTAMP);
            }

            if (ordrePreparation.getRemarque() != null)
            {
                statement.setString(7, ordrePreparation.getRemarque());
            }
            else
            {
                statement.setNull(7, Types.VARCHAR);
            }

            statement.setBoolean(8, ordrePreparation.getEstUrgent());
            statement.setString(9, ordrePreparation.getNom());

            if (ordrePreparation.getCodeBarre() != null)
            {
                statement.setInt(10, ordrePreparation.getCodeBarre());
            }
            else
            {
                statement.setNull(10, Types.INTEGER);
            }

            if (ordrePreparation.getMatricule_Cui() != null)
            {
                statement.setInt(11, ordrePreparation.getMatricule_Cui());
            }
            else
            {
                statement.setNull(11, Types.INTEGER);
            }

            statement.setInt(12, ordrePreparation.getMatricule_Res());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new AddOrdreException("SQL erreur !");
        }
    }

    public ArrayList<OrdrePreparation> getAllOrdres () throws AllOrdresException, ModelException
    {

        ArrayList<OrdrePreparation> ordres = new ArrayList<>();

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new AllOrdresException("Erreur connexion !");


        try {
            String sql = "SELECT * FROM dbgrandbazar.ordrepreparation order by NumeroSequentiel";
            statement = connection.prepareStatement(sql);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête
            int quantiteProduite, matricule_Cui, codeBarre;
            String remarque;

            while(data.next()){

                ordrePreparation = new OrdrePreparation();
                // LES CHAMPS OBLIGATOIRES
                //DATE
                java.sql.Date sqlDate;

                GregorianCalendar calendar = new GregorianCalendar();
                // Peut pas utiliser le même calendar pour les 3 dates, pq ? Ou refaire un calendar = new GregorianCalendar avant l'utilisation
                GregorianCalendar calendarVente = new GregorianCalendar();
                GregorianCalendar calendarPreparation = new GregorianCalendar();

                sqlDate = data.getDate("Date");
                calendar.setTime(sqlDate);
                ordrePreparation.setDate(calendar);
                // NumeroSequentiel
                ordrePreparation.setNumeroSequentiel(data.getInt("NumeroSequentiel"));
                // QuantitePrevue
                ordrePreparation.setQuantitePrevue(data.getInt("QuantitePrevue"));
                // EstUrgent
                ordrePreparation.setEstUrgent(data.getBoolean("EstUrgent"));
                // Nom (recette)
                ordrePreparation.setNom(data.getString("Nom"));
                // Matricule_Res
                ordrePreparation.setMatricule_Res(data.getInt("Matricule_Res"));
                // LES CHAMPS FACULTATIFS

                // CodeBarre
                codeBarre = data.getInt("CodeBarre");
                if(! data.wasNull()){
                    ordrePreparation.setCodeBarre(codeBarre);
                }
                // QuantiteProduite
                quantiteProduite = data.getInt("QuantiteProduite");
                if(! data.wasNull()){
                    ordrePreparation.setQuantiteProduite(quantiteProduite);
                }
                // DateVente
                sqlDate = data.getDate("DateVente");
                if(! data.wasNull()){
                    calendarVente.setTime(sqlDate);
                    ordrePreparation.setDateVente(calendarVente);
                }

                Timestamp datePrepa = data.getTimestamp("DatePreparation");
                if(! data.wasNull()){
                    calendarPreparation.setTime(datePrepa);
                    ordrePreparation.setDatePreparation(calendarPreparation);
                }
                // Remarque
                remarque = data.getString("Remarque");
                if(! data.wasNull()){
                    ordrePreparation.setRemarque(remarque);
                }
                // Matricule_Cui
                matricule_Cui = data.getInt("Matricule_Cui");
                if(! data.wasNull()){
                    ordrePreparation.setMatricule_Cui(matricule_Cui);
                }
                ordres.add(ordrePreparation);

            }

        } catch (SQLException e) {
            throw new AllOrdresException(e.getMessage());
        }
        return ordres;
    }

    @Override
    public ArrayList<String> getAllRecetteNom() throws GeneralException {

        ArrayList<String> allRecetteNom = new ArrayList<>();

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","récupérer les noms de recettes");

        try {
            String sql = "SELECT Nom FROM dbgrandbazar.recette";
            statement = connection.prepareStatement(sql);
            ResultSet data = statement.executeQuery();

            while(data.next()) {
                allRecetteNom.add(data.getString("Nom"));
            }
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"récupérer les noms de recettes");
        }
        return allRecetteNom;
    }

    public ArrayList<String> getCodeBarres () throws GeneralException {
        ArrayList<String> allCodeBarres = new ArrayList<>();
        String typeArticle;
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","récupérer les code barres");

        try {
            String sql = "Select CodeBarre, libelle from typearticle where EstUnePreparation=true;";
            statement = connection.prepareStatement(sql);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête

            while(data.next()) {
                typeArticle = data.getString("codeBarre");
                typeArticle += " ("+data.getString("Libelle")+")";
                allCodeBarres.add(typeArticle);
            }
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"récupérer les code barres");
        }
        return allCodeBarres;
    }

    public ArrayList<String> getMatriculesCui () throws GeneralException {
        ArrayList<String> allMatriculesCui = new ArrayList<>();
        String cuisinier;
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","récupérer les cuisiniers");

        try {
            String sql = "Select * From cuisinier inner join membredupersonnel on Matricule_Cui = Matricule;";
            statement = connection.prepareStatement(sql);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête

            while(data.next()) {
                cuisinier = ""+data.getInt("Matricule_Cui");
                cuisinier += " ("+data.getString("Nom");
                cuisinier += " "+data.getString("Prenom")+")";
                allMatriculesCui.add(cuisinier);
            }
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"récupérer les cuisiniers");
        }
        return allMatriculesCui;
    }

    public ArrayList<String> getMatriculesRes () throws GeneralException {
        ArrayList<String> allMatriculeRes = new ArrayList<>();
        String responsableVente;
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","récupérer les responsables des ventes");

        try {
            String sql = "Select * From responsablevente inner join membredupersonnel on MatriculeRes = Matricule;";
            statement = connection.prepareStatement(sql);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête

            while(data.next()) {
                responsableVente = ""+data.getInt("MatriculeRes");
                responsableVente += " ("+data.getString("Nom");
                responsableVente += " "+data.getString("Prenom")+")";
                allMatriculeRes.add(responsableVente);
            }
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"récupérer les responsables des ventes");
        }
        return allMatriculeRes;    }

        public Integer getNumeroSequentiel () throws GeneralException
        {
            ArrayList<Integer> numerosSquentiel = new ArrayList<>();
            Integer numeroSequentielMax = 0;

            if ((connection = SingletonConnection.getInstance()) == null)
                throw  new GeneralException("Erreur connexion !","récupérer les numeros sequentiel");

            try
            {
                String sql = "SELECT numeroSequentiel FROM dbgrandbazar.ordrePreparation";
                statement = connection.prepareStatement(sql);
                ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête
                while(data.next()) {
                    numerosSquentiel.add(data.getInt("numeroSequentiel"));
                }
            } catch (SQLException e) {
                throw new GeneralException(e.getMessage(),"récupérer les numeros sequentiel");
            }

            for (Integer numeros : numerosSquentiel)
        {
            if (numeros > numeroSequentielMax)
            {
                numeroSequentielMax = numeros;
            }
        }

            return numeroSequentielMax;
        }

    @Override
    public void updateOrdre(OrdrePreparation ordrePreparation) throws UpdateOrdreException
    {
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new UpdateOrdreException("Erreur connexion !");

        try
        {
            String sql = "update ordrepreparation set Date = ?, QuantitePrevue = ?, QuantiteProduite = ?, DateVente = ?, DatePreparation = ?, Remarque = ?, estUrgent = ?, Nom = ?, CodeBarre = ?, Matricule_Cui = ?, Matricule_Res = ? where NumeroSequentiel = ?";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = new java.sql.Date(ordrePreparation.getDate().getTimeInMillis());
            statement.setDate(1, sqlDate);
            statement.setInt(2, ordrePreparation.getQuantitePrevue());

            if (ordrePreparation.getQuantiteProduite() != null) {
                statement.setInt(3, ordrePreparation.getQuantiteProduite());
            }
            else
            {
                statement.setNull(3, Types.INTEGER);
            }

            if (ordrePreparation.getDateVente() != null)
            {
                sqlDate.setTime(ordrePreparation.getDateVente().getTimeInMillis());
                statement.setDate(4, sqlDate);
            }
            else
            {
                statement.setNull(4, Types.TIMESTAMP);
            }

            if (ordrePreparation.getDatePreparation() != null)
            {
                sqlDate.setTime(ordrePreparation.getDatePreparation().getTimeInMillis());
                Timestamp datePrepa = new Timestamp(sqlDate.getTime());
                statement.setTimestamp(5, datePrepa);
            }
            else
            {
                statement.setNull(5, Types.TIMESTAMP);
            }

            if (ordrePreparation.getRemarque() != null)
            {
                statement.setString(6, ordrePreparation.getRemarque());
            }
            else
            {
                statement.setNull(6, Types.VARCHAR);
            }

            statement.setBoolean(7, ordrePreparation.getEstUrgent());
            statement.setString(8, ordrePreparation.getNom());

            if (ordrePreparation.getCodeBarre() != null)
            {
                statement.setInt(9, ordrePreparation.getCodeBarre());
            }
            else
            {
                statement.setNull(9, Types.INTEGER);
            }

            if (ordrePreparation.getMatricule_Cui() != null)
            {
                statement.setInt(10, ordrePreparation.getMatricule_Cui());
            }
            else
            {
                statement.setNull(10, Types.INTEGER);
            }

            statement.setInt(11, ordrePreparation.getMatricule_Res());
            statement.setInt(12, ordrePreparation.getNumeroSequentiel());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new UpdateOrdreException("SQL error");
        }
    }
    public void supprimerOrdre(int numeroSequentiel) throws GeneralException {
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","Supprimer un ordre");


        try {
            String sql = "DELETE FROM `dbgrandbazar`.`ordrepreparation` WHERE NumeroSequentiel=?;";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, numeroSequentiel);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"Supprimer un ordre");
        }

    }

    public ArrayList<Reservation> getForeignKeyReservation(int numeroSequentie) throws GeneralException {
        ArrayList<Reservation> reservations = new ArrayList<>();

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","récupérer les foreignKey reservations");


        try {
            String sql = "SELECT * FROM dbgrandbazar.reservation where numeroSequentiel=?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1,numeroSequentie);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête

            while(data.next()){

                reservation = new Reservation();
                // LES CHAMPS OBLIGATOIRES

                // NumeroSequentiel
                reservation.setNumeroSequentiel(data.getInt("numeroSequentiel"));
                // QuantitePrevue
                reservation.setCodeBarre(data.getInt("codeBarre"));
                // EstUrgent
                reservation.setQuantiteReserve(data.getInt("quantiteReserve"));
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"récupérer les foreignKey reservations");
        }
        return reservations;
    }

    public void supprimerForeignKeyReservation(int numeroSequentiel) throws GeneralException {
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","Supprimer les clés étrangéres Reservation");


        try {
            String sql = "DELETE FROM `dbgrandbazar`.`reservation` WHERE numeroSequentiel=?;";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, numeroSequentiel);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"Supprimer les clés étrangéres Reservation");
        }

    }

    @Override
    public ArrayList<OrdrePreparation> getOrdresRecettesCuisiniers(Integer matri_Cui, String recette) throws GetOrdresRecettesCuisiniersException, ModelException{
        ArrayList<OrdrePreparation> ordres = new ArrayList<>();

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GetOrdresRecettesCuisiniersException("Erreur connexion !");


        try {
            String sql = "select * \n" +
                    "from ordrepreparation o join recette r on (o.Nom = r.Nom)\n" +
                    "join cuisinier c on (o.Matricule_Cui = c.Matricule_Cui) \n" +
                    "where o.Nom is not null\n" +
                    "and o.Matricule_Cui is not null\n" +
                    "and o.Matricule_Cui = ?\n" +
                    "and o.Nom = ?\n" +
                    "order by o.NumeroSequentiel";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, matri_Cui);
            statement.setString(2, recette);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête
            int quantiteProduite, codeBarre;
            String remarque;

            while(data.next())
            {

                ordrePreparation = new OrdrePreparation();
                java.sql.Date sqlDate;

                GregorianCalendar calendar = new GregorianCalendar();
                GregorianCalendar calendarVente = new GregorianCalendar();
                GregorianCalendar calendarPreparation = new GregorianCalendar();

                sqlDate = data.getDate("Date");
                calendar.setTime(sqlDate);
                ordrePreparation.setDate(calendar);
                // NumeroSequentiel
                ordrePreparation.setNumeroSequentiel(data.getInt("NumeroSequentiel"));
                // QuantitePrevue
                ordrePreparation.setQuantitePrevue(data.getInt("QuantitePrevue"));
                // EstUrgent
                ordrePreparation.setEstUrgent(data.getBoolean("EstUrgent"));
                // Nom (recette)
                ordrePreparation.setNom(data.getString("Nom"));
                // Matricule_Res
                ordrePreparation.setMatricule_Res(data.getInt("Matricule_Res"));
                // LES CHAMPS FACULTATIFS

                // CodeBarre
                codeBarre = data.getInt("CodeBarre");
                if (!data.wasNull())
                {
                    ordrePreparation.setCodeBarre(codeBarre);
                }
                // QuantiteProduite
                quantiteProduite = data.getInt("QuantiteProduite");
                if(! data.wasNull()){
                    ordrePreparation.setQuantiteProduite(quantiteProduite);
                }
                // DateVente
                sqlDate = data.getDate("DateVente");
                if(! data.wasNull()){
                    calendarVente.setTime(sqlDate);
                    ordrePreparation.setDateVente(calendarVente);
                }
                // DatePreparation
                sqlDate = data.getDate("DatePreparation");
                if(! data.wasNull()){

                    calendarPreparation.setTime(sqlDate);
                    ordrePreparation.setDatePreparation(calendarPreparation);
                }

                // Remarque
                remarque = data.getString("Remarque");
                if(! data.wasNull()){
                    ordrePreparation.setRemarque(remarque);
                }
                // Matricule_Cui
                ordrePreparation.setMatricule_Cui(data.getInt("Matricule_Cui"));
                ordres.add(ordrePreparation);
            }

        } catch (SQLException e) {
            throw new GetOrdresRecettesCuisiniersException(e.getMessage());
        }
        return ordres;
    }

    @Override
    public ArrayList<Recherche2> getRecherche2(GregorianCalendar dateDeb, GregorianCalendar dateFin) throws GeneralException, ModelException {
        ArrayList<Recherche2> recherches2 = new ArrayList<>();
        Recherche2 recherche2;

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !", "GetRecherche2");


        try {
            String sql = "select o.NumeroSequentiel, o.Date as 'DateOrdre', o.QuantitePrevue, o.EstUrgent, o.Matricule_Res, ti.Date as 'DateTicket'\n" +
                    "from ordrepreparation o join typearticle t on (o.CodeBarre = t.CodeBarre)\n" +
                    "join ligneticket l on (l.CodeBarre = t.CodeBarre)\n" +
                    "join ticket ti on (ti.NumTicket = l.NumTicket)\n" +
                    "where o.Date between ? and ?\n" +
                    "order by o.NumeroSequentiel;";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = new java.sql.Date(dateDeb.getTimeInMillis());
            statement.setDate(1, sqlDate);
            sqlDate.setTime(dateFin.getTimeInMillis());
            statement.setDate(2, sqlDate);

            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête

            //Création des variables qui iront garnir recherche2
            Integer numeroSequentiel;
            Integer quantitePrevue;
            Boolean estUrgent;
            Integer matri_Res;

            while(data.next())
            {
                GregorianCalendar dateOrdre = new GregorianCalendar();
                GregorianCalendar dateTicket = new GregorianCalendar();
                //dateOrdre
                sqlDate = data.getDate("DateOrdre");
                dateOrdre.setTime(sqlDate);
                // NumeroSequentiel
                numeroSequentiel = data.getInt("NumeroSequentiel");
                // QuantitePrevue
                quantitePrevue = data.getInt("QuantitePrevue");
                // EstUrgent
                estUrgent = data.getBoolean("EstUrgent");
                // Matricule_Res
                matri_Res = data.getInt("Matricule_Res");
                //dateTicket
                sqlDate = data.getDate("DateTicket");
                dateTicket.setTime(sqlDate);

                recherche2 = new Recherche2(numeroSequentiel, dateOrdre, quantitePrevue, estUrgent, matri_Res, dateTicket);
                recherches2.add(recherche2);
            }

        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(), "GetRecherche2");
        }
        return recherches2;
    }

    public ArrayList<String> getClients () throws GeneralException
    {
        ArrayList<String> clients = new ArrayList<>();
        String chaineClient;
        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !","récupérer les clients");

        try {
            String sql = "select c.Nom, c.Prenom, c.NumClient" +
                        " from dbgrandbazar.client c";
            statement = connection.prepareStatement(sql);
            ResultSet data = statement.executeQuery();

            while(data.next()) {
                chaineClient = "";
                chaineClient += data.getInt("NumClient");
                chaineClient += " " + data.getString("Nom") + " " + data.getString("Prenom");
                clients.add(chaineClient);
            }
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(),"récupérer les clients");
        }
        return clients;
    }

    public ArrayList<Recherche3> getRecherche3 (Integer numClient) throws GeneralException, ModelException
    {
        ArrayList<Recherche3> recherches3 = new ArrayList<>();
        Recherche3 recherche3;

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !", "GetRecherche3");

        try
        {
            String sql = "select ty.Libelle, t.Date, l.Quantite\n" +
                    "from client c join ticket t on (c.NumClient = t.NumClient)\n" +
                    "join ligneticket l on (t.NumTicket = l.NumTicket)\n" +
                    "join typearticle ty on (l.CodeBarre = ty.CodeBarre)\n" +
                    "where c.NumClient = ?\n" +
                    "order by c.NumClient;";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, numClient);

            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête
            ResultSetMetaData meta = data.getMetaData(); //

            String libelle;
            Integer quantite;
            GregorianCalendar dateTicket;
            java.sql.Date sqlDate;

            while (data.next())
            {
                libelle = data.getString("Libelle");
                dateTicket = new GregorianCalendar();
                sqlDate = data.getDate("Date");
                dateTicket.setTime(sqlDate);
                quantite = data.getInt("Quantite");

                recherche3 = new Recherche3(libelle, dateTicket, quantite);
                recherches3.add(recherche3);
            }
        }
        catch (SQLException e)
        {
            throw new GeneralException(e.getMessage(), "GetRecherche3");
        }

        return  recherches3;
    }

    public ArrayList<GregorianCalendar>getDatesPreparationDuJour(int jour) throws GeneralException {
        ArrayList<GregorianCalendar> datesPreparationDuJour = new ArrayList<>();

        if ((connection = SingletonConnection.getInstance()) == null)
            throw  new GeneralException("Erreur connexion !", "les dates de préparation du jour");


        try {
            String sql = "SELECT DatePreparation FROM dbgrandbazar.ordrepreparation where dayofweek(DatePreparation) = ? and DatePreparation between date_sub(curdate(), interval 21 day) and curdate()";
            // rajouter le choix a l'utilisateur le nombre de jours dans la moyenne
            statement = connection.prepareStatement(sql);
            statement.setInt(1, jour);
            ResultSet data = statement.executeQuery(); // contient les lignes de résultat de la requête
            ResultSetMetaData meta = data.getMetaData(); // Contient les meta données (nb colonnes, ...)


            Timestamp datePreparation;
            java.sql.Date sqlDate ;
            GregorianCalendar dataPrepa;
            while(data.next()){
                dataPrepa = new GregorianCalendar();
                datePreparation = data.getTimestamp("DatePreparation");
                dataPrepa.setTime(datePreparation);
                datesPreparationDuJour.add(dataPrepa);
            }
        } catch (SQLException e) {
            throw new GeneralException(e.getMessage(), "les dates de préparation du jour");
        }
        return datesPreparationDuJour;
    }

}