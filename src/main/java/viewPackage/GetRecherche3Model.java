package viewPackage;

import modelPackage.Recherche3;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Date;

public class GetRecherche3Model extends AbstractTableModel
{
    private ArrayList<String> columnNames;
    private ArrayList<Recherche3> contents;

    public GetRecherche3Model(ArrayList<Recherche3> recherches3) {
        columnNames = new ArrayList<>();
        columnNames.add("Libelle");
        columnNames.add("Date");
        columnNames.add("Quantite");
        setContents(recherches3);
    }

    public int getColumnCount( ) { return columnNames.size( ); }
    public int getRowCount( ) { return contents.size( ); }
    public String getColumnName(int column) { return columnNames.get(column);}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Recherche3 recherche3 = contents.get(rowIndex);
        switch (columnIndex) {
            case 0 : return recherche3.getLibelle();
            case 1 : return recherche3.getDateTicket().getTime();
            case 2 : return recherche3.getQuantite();
            default: return null;
        }
    }

    public Class getColumnClass(int column){
        Class c;
        switch (column){
            case 0 : c = String.class;
                break;
            case 1 : c = Date.class;
                break;
            case 2 : c = Integer.class;
                break;
            default : c = String.class;
        }
        return c;
    }

    public void setContents(ArrayList<Recherche3> contents) {
        this.contents = contents;
    }
}
