package dataAccessPackage;

import exceptionPackage.AddOrdreException;
import exceptionPackage.AllOrdresException;
import exceptionPackage.GeneralException;
import modelPackage.OrdrePreparation;

import java.util.ArrayList;

public interface DataAccess
{
    void addOrdre (OrdrePreparation ordrePreparation) throws AddOrdreException;
    ArrayList<OrdrePreparation> getAllOrdres() throws AllOrdresException;
    ArrayList<String> getAllRecetteNom () throws GeneralException;
    ArrayList<String> getCodeBarres () throws GeneralException;
    ArrayList<String> getMatriculesCui () throws GeneralException;
    ArrayList<String> getMatriculesRes () throws GeneralException;


}
