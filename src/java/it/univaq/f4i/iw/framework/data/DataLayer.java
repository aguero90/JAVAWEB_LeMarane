package it.univaq.f4i.iw.framework.data;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface DataLayer {

    void init() throws DataLayerException;

    void destroy() throws DataLayerException;
}
