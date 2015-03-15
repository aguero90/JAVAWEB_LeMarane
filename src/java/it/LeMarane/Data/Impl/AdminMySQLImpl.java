package it.LeMarane.Data.Impl;

import it.LeMarane.Data.Model.Admin;
import it.LeMarane.Data.Model.MaraneDataLayer;
import it.LeMarane.Data.Model.Post;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author alex
 */
public class AdminMySQLImpl implements Admin {

    private int ID;
    private String username;
    private String password;
    protected boolean dirty;

    protected MaraneDataLayer dataLayer; // per le query

    List<Post> posts;

    public AdminMySQLImpl(MaraneDataLayer dl) {

        ID = 0;
        username = "";
        password = "";
        dirty = false;

        dataLayer = dl;
    }

    public AdminMySQLImpl(MaraneDataLayerMySQLImpl dataLayer, ResultSet rs) throws SQLException {
        this(dataLayer);
        ID = rs.getInt("ID");
        username = rs.getString("username");
        password = rs.getString("password");
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
        dirty = true;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void copyFrom(Admin admin) {
        ID = admin.getID();
        username = admin.getUsername();
        password = admin.getPassword();

        posts = null;

        dirty = true;
    }

    /*=============================================================================================
     RELAZIONI
     =============================================================================================*/
    @Override
    public List<Post> getPosts() {

        if (posts == null) {
            posts = dataLayer.getPosts(this);
        }

        return posts;
    }

    @Override
    public void setPosts(List<Post> posts) {
        this.posts = posts;
        dirty = true;
    }

    @Override
    public String toString() {
        return "AdminMysqlImpl{" + "ID=" + ID + ", username=" + username + ", password=" + password + ", dirty=" + dirty + ", posts=" + posts + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdminMySQLImpl other = (AdminMySQLImpl) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }

}
