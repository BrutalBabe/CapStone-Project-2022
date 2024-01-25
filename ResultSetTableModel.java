package practice;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class ResultSetTableModel extends AbstractTableModel {
    // Currently displayed result set
    private ResultSet rs;
    // Associated metadata
    private ResultSetMetaData rsMeta;
    // Column count
    private int columnCount;
    // Column nmaes
    private final Vector<String> columnNames = new Vector<String>();
    // Vector of rows
    private final Vector<Object[]> cache = new Vector<Object[]>();
    
    /** Set new result set */
    public void setResultSet(ResultSet rs) throws SQLException {
        if (this.rs != null)
            this.rs.close();
        cache.clear();
        // 'Cache' some metadata data
        rsMeta = rs.getMetaData();
        columnCount = rsMeta.getColumnCount();

        columnNames.clear();
        for(int col = 1; col <= columnCount; col++) {
            columnNames.add(rsMeta.getColumnName(col));
        }
        while(rs.next()) {
            Object rowData[] = new Object[columnCount];
            for(int col = columnCount; col > 0; col--)                 
                rowData[col - 1] = rs.getObject(col); 
            cache.add(rowData);
        }
        // Reload table structure
        fireTableStructureChanged();
    }
    /** Close result set */
    public void close() throws SQLException {
        rs.close();
    }
    /** ## TableModel interface ## */
    public String getColumnName(int column) {
        return columnNames.get(column);
    }
    /** ## TableModel interface ## */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = cache.get(rowIndex);
        return row[columnIndex];
    }
    /** ## TableModel interface ## */
    public int getRowCount() {
        return cache.size();
    }
    /** ## TableModel interface ## */
    public int getColumnCount() {
        return columnCount;
    }   
}