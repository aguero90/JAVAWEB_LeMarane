package it.LeMarane.Controller.back;

import it.LeMarane.Controller.MaraneBaseController;
import it.LeMarane.Data.Model.Admin;
import it.LeMarane.Data.Model.Image;
import it.univaq.f4i.iw.framework.result.FailureResult;
import it.univaq.f4i.iw.framework.result.SplitSlashesFmkExt;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import it.univaq.f4i.iw.framework.security.SecurityLayer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author alex
 */
public class ImageManagement extends MaraneBaseController {
    
    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }
    
    private void action_goToImageManagement(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());
        
        if (SecurityLayer.checkSession(request) != null) {
            try {
                Admin admin = getDataLayer().getAdmin(SecurityLayer.checkNumeric((request.getSession().getAttribute("userid")).toString()));
                request.setAttribute("admin", admin);
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
            
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            result.activate("back/storeImage.ftl.html", request, response);
        } else {
            action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
            // redirigere al login
        }
    }
    
    private void action_storeImage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
        }
        
        if (checkImageParameter(request, response)) {
            Image i = getDataLayer().createImage();
            i.setName(SecurityLayer.addSlashes(request.getParameter("name")));
            i.setURL(SecurityLayer.addSlashes(request.getParameter("URL")));
            i.setDescription(SecurityLayer.addSlashes(request.getParameter("description")));
            i.setBanner(request.getParameter("isBanner") != null);
            action_goToImageManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
    }
    
    private void action_removeImage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
            
            getDataLayer().removeImage(getDataLayer().getImage(SecurityLayer.checkNumeric(request.getParameter("r"))));
            action_goToImageManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
        
    }
    
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getParameter("submitStoreImage") != null) {
                action_storeImage(request, response);
            } else if (request.getParameter("r") != null) {
                action_removeImage(request, response);
            } else {
                action_goToImageManagement(request, response);
            }
        } catch (IOException ex) {
            action_error(request, response, "Riprova di nuovo!");
            System.err.println("Errore nella Process Request di NewsList.java: IOException");
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
    private boolean checkImageParameter(HttpServletRequest request, HttpServletResponse response) {
        return request.getParameter("name") != null && request.getParameter("name").length() > 0
                && request.getParameter("URL") != null && request.getParameter("URL").length() > 0
                && request.getParameter("description") != null && request.getParameter("description").length() > 0;
    }
    
}
