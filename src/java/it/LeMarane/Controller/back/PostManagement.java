package it.LeMarane.Controller.back;

import it.LeMarane.Controller.MaraneBaseController;
import it.LeMarane.Data.Model.Admin;
import it.LeMarane.Data.Model.Post;
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
public class PostManagement extends MaraneBaseController {

    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }

    private void action_goToPostManagement(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());

        if (SecurityLayer.checkSession(request) != null) {
            try {
                Admin admin = getDataLayer().getAdmin(SecurityLayer.checkNumeric((request.getSession().getAttribute("userid")).toString()));
                request.setAttribute("admin", admin);
            } catch (NumberFormatException ex) {
                //User id is not a number
            }

            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            result.activate("back/storePost.ftl.html", request, response);
        } else {
            action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
            // redirigere al login
        }
    }

    private void action_storePost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
        }

        if (checkPostParameter(request, response)) {
            Post p = getDataLayer().createPost();
            p.setTitle(SecurityLayer.stripSlashes(request.getParameter("title")));
            p.setText(SecurityLayer.stripSlashes(request.getParameter("text")));
            p.setAdmin(getDataLayer().getAdmin(SecurityLayer.checkNumeric(request.getAttribute("userid").toString())));
            getDataLayer().storePost(p);
            action_goToPostManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
    }

    private void action_removePost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }

            getDataLayer().removePost(getDataLayer().getPost(SecurityLayer.checkNumeric(request.getParameter("r"))));
            action_goToPostManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }

    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getParameter("submitStorePost") != null) {
                action_storePost(request, response);
            } else if (request.getParameter("r") != null) {
                action_removePost(request, response);
            } else {
                action_goToPostManagement(request, response);
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

    private boolean checkPostParameter(HttpServletRequest request, HttpServletResponse response) {
        return request.getParameter("title") != null && request.getParameter("title").length() > 0
                && request.getParameter("text") != null && request.getParameter("text").length() > 0;
    }

}
