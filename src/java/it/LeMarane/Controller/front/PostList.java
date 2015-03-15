package it.LeMarane.Controller.front;

import it.LeMarane.Controller.MaraneBaseController;
import it.LeMarane.Data.Model.Admin;
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
public class PostList extends MaraneBaseController {

    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }

    private void action_goToPostList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());

        if (SecurityLayer.checkSession(request) != null) {
            try {
                Admin admin = getDataLayer().getAdmin(SecurityLayer.checkNumeric((request.getSession().getAttribute("userid")).toString()));
                request.setAttribute("admin", admin);
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
        }

        request.setAttribute("posts", getDataLayer().getPosts());
        request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
        result.activate("front/postList.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            action_goToPostList(request, response);
        } catch (IOException ex) {
            action_error(request, response, "Riprova di nuovo!");
            System.err.println("Errore nella Process Request di NewsList.java: IOException");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
