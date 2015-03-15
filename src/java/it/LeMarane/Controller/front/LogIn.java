package it.LeMarane.Controller.front;

import it.LeMarane.Controller.MaraneBaseController;
import it.LeMarane.Data.Model.Admin;
import it.mam.REST.utility.Utility;
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
public class LogIn extends MaraneBaseController {
    
    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }
    
    private void action_goToLogIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());
        
        if (SecurityLayer.checkSession(request) != null) {
            action_error(request, response, "Sei già loggato");
            return;
        }
        
        request.setAttribute("posts", getDataLayer().getPosts());
        request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
        result.activate("front/postList.ftl.html", request, response);
    }
    
    private void action_logIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        if (checkLogInParameter(request, response)) {
            
            Admin a = getDataLayer().getAdmin(
                    SecurityLayer.addSlashes(request.getParameter("username")),
                    Utility.stringToMD5(Utility.stringToMD5(request.getParameter("password"))));
            
            if (a != null) {
                SecurityLayer.createSession(request, a.getUsername(), a.getID());
                
                request.setAttribute("admin", a);
                request.setAttribute("posts", getDataLayer().getPosts());
                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                
                TemplateResult result = new TemplateResult(getServletContext());
                result.activate("front/postList.ftl.html", request, response);
            } else {
                action_error(request, response, "Username o password errati");
            }
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
    }
    
    private void action_logOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        SecurityLayer.disposeSession(request);
        
        request.setAttribute("posts", getDataLayer().getPosts());
        request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
        
        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("front/postList.ftl.html", request, response);
    }
    
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getParameter("submitLogIn") != null) {
                action_logIn(request, response);
            } else if (request.getParameter("logOut") != null) {
                action_logOut(request, response);
            } else {
                action_goToLogIn(request, response);
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
    
    private boolean checkLogInParameter(HttpServletRequest request, HttpServletResponse response) {
        return request.getParameter("username") != null && request.getParameter("username").length() > 0
                && request.getParameter("password") != null && request.getParameter("password").length() > 0;
    }
    
}
