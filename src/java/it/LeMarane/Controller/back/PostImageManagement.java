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
public class PostImageManagement extends MaraneBaseController {

    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }

    private void action_goToPostImageManagement(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());

        if (SecurityLayer.checkSession(request) != null) {
            try {
                Admin admin = getDataLayer().getAdmin(SecurityLayer.checkNumeric((request.getSession().getAttribute("userid")).toString()));
                request.setAttribute("admin", admin);
            } catch (NumberFormatException ex) {
                //User id is not a number
            }

            request.setAttribute("posts", getDataLayer().getPosts());
            request.setAttribute("images", getDataLayer().getImages());
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            result.activate("back/storePostImage.ftl.html", request, response);
        } else {
            action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
            // redirigere al login
        }
    }

    private void action_storePostImage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
        }

        if (checkPostImageParameter(request, response)) {
            Post p = getDataLayer().getPost(SecurityLayer.checkNumeric(request.getParameter("p")));

            for (String s : request.getParameterValues("images")) {
                p.getImages().add(getDataLayer().getImage(SecurityLayer.checkNumeric(s)));
            }
            getDataLayer().storePost(p);

            action_goToPostImageManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getParameter("submitStorePostImage") != null) {
                action_storePostImage(request, response);
            } else {
                action_goToPostImageManagement(request, response);
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

    private boolean checkPostImageParameter(HttpServletRequest request, HttpServletResponse response) {
        return request.getParameter("p") != null && request.getParameterValues("images") != null
                && request.getParameterValues("images").length > 0;
    }

}
