package it.univaq.f4i.iw.framework.data;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Giuseppe Della Penna
 */
public class DataLayerMysqlImpl implements DataLayer {

    protected DataSource datasource;
    protected Connection connection;

    public DataLayerMysqlImpl(DataSource datasource) throws SQLException, NamingException {
        this.datasource = datasource;
        this.connection = null;
    }

    @Override
    public void init() throws DataLayerException {
        try {            
            //connessione al database locale
            //database connection
            connection = datasource.getConnection();       
        } catch (SQLException ex) {
            throw new DataLayerException("Error initializing data layer", ex);
        }
    }

    @Override
    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            //
        }
    }
}
