package it.LeMarane.Controller;

import it.LeMarane.Data.Impl.MaraneDataLayerMySQLImpl;
import it.LeMarane.Data.Model.MaraneDataLayer;
import it.univaq.f4i.iw.framework.data.DataLayerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author alex
 */
public abstract class MaraneBaseController extends HttpServlet {

    private MaraneDataLayer dataLayer;

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException;

    private void processBaseRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            dataLayer = new MaraneDataLayerMySQLImpl((DataSource) getServletContext().getAttribute("datasource"));
            dataLayer.init();
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(MaraneBaseController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MaraneBaseController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataLayerException ex) {
            Logger.getLogger(MaraneBaseController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServletException ex) {
            Logger.getLogger(MaraneBaseController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dataLayer.destroy();
            } catch (DataLayerException ex) {
                Logger.getLogger(MaraneBaseController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public MaraneDataLayer getDataLayer() {
        return dataLayer;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processBaseRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processBaseRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
