package it.LeMarane.Controller.front;

import it.LeMarane.Controller.MaraneBaseController;
import it.LeMarane.Data.Model.Admin;
import it.LeMarane.Data.Model.Comment;
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
public class PostCard extends MaraneBaseController {

    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }

    private void action_goToPostCard(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());

        if (SecurityLayer.checkSession(request) != null) {
            try {
                Admin admin = getDataLayer().getAdmin(SecurityLayer.checkNumeric((request.getSession().getAttribute("userid")).toString()));
                request.setAttribute("admin", admin);
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
        }

        request.setAttribute("post", getDataLayer().getPost(SecurityLayer.checkNumeric(request.getParameter("p"))));
        request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
        result.activate("front/postCard.ftl.html", request, response);
    }

    private void action_storeComment(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (checkCommentParameter(request, response)) {
            Comment c = getDataLayer().createComment();
            c.setAuthor(SecurityLayer.addSlashes(request.getParameter("author")));
            c.setText(SecurityLayer.addSlashes(request.getParameter("text")));
            c.setPost(getDataLayer().getPost(SecurityLayer.checkNumeric(request.getParameter("pid"))));
            getDataLayer().storeComment(c);
            action_goToPostCard(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getAttribute("submitComment") != null) {
                action_storeComment(request, response);
            } else {
                action_goToPostCard(request, response);
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

    private boolean checkCommentParameter(HttpServletRequest request, HttpServletResponse response) {
        return request.getParameter("pid") != null && request.getParameter("pid").length() > 0
                && request.getParameter("author") != null && request.getParameter("author").length() > 0
                && request.getParameter("text") != null && request.getParameter("text").length() > 0;
    }

}
