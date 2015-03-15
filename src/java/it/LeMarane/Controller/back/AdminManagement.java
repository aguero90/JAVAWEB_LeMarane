package it.LeMarane.Controller.back;

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
public class AdminManagement extends MaraneBaseController {

    private void action_error(HttpServletRequest request, HttpServletResponse response, String message) {
        FailureResult fail = new FailureResult(getServletContext());
        fail.activate(message, request, response);
    }

    private void action_goToAdminManagement(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TemplateResult result = new TemplateResult(getServletContext());

        if (SecurityLayer.checkSession(request) != null) {
            try {
                Admin admin = getDataLayer().getAdmin(SecurityLayer.checkNumeric((request.getSession().getAttribute("userid")).toString()));
                request.setAttribute("admin", admin);
            } catch (NumberFormatException ex) {
                //User id is not a number
            }

            request.setAttribute("admins", getDataLayer().getAdmins());
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            result.activate("back/adminManagement.ftl.html", request, response);
        } else {
            action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
            // redirigere al login
        }
    }

    private void action_storeAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }
        }

        if (checkAdminParameter(request, response) && request.getParameter("password").equals(request.getParameter("passwordConfirm"))) {
            Admin a = getDataLayer().createAdmin();

            a.setUsername(SecurityLayer.addSlashes(request.getParameter("username")));
            a.setPassword(Utility.stringToMD5(Utility.stringToMD5(request.getParameter("password"))));

            getDataLayer().storeAdmin(a);
            action_goToAdminManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }
    }

    private void action_removeAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (SecurityLayer.checkSession(request) == null) {
            try {
                action_error(request, response, "Non hai i permessi necessari per effettuare questa operazione");
                // redirigere al login
                return;
            } catch (NumberFormatException ex) {
                //User id is not a number
            }

            getDataLayer().removeAdmin(getDataLayer().getAdmin(SecurityLayer.checkNumeric(request.getParameter("r"))));
            action_goToAdminManagement(request, response);
        } else {
            action_error(request, response, "Non hai inserito tutti i dati");
        }

    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getParameter("submitStoreAdmin") != null) {
                action_storeAdmin(request, response);
            } else if (request.getParameter("r") != null) {
                action_removeAdmin(request, response);
            } else {
                action_goToAdminManagement(request, response);
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

    private boolean checkAdminParameter(HttpServletRequest request, HttpServletResponse response) {
        return request.getParameter("username") != null && request.getParameter("username").length() > 0
                && request.getParameter("password") != null && request.getParameter("password").length() > 0
                && request.getParameter("passwordConfirm") != null && request.getParameter("passwordConfirm").length() > 0;
    }

}
