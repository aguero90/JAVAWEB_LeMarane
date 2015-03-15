package it.LeMarane.Data.Impl;

import it.LeMarane.Data.Model.Admin;
import it.LeMarane.Data.Model.Comment;
import it.LeMarane.Data.Model.Image;
import it.LeMarane.Data.Model.MaraneDataLayer;
import it.LeMarane.Data.Model.Post;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author alex
 */
public class PostMySQLImpl implements Post {

    private int ID;
    private String title;
    private String text;
    private Date date;
    protected boolean dirty;

    protected MaraneDataLayer dataLayer; // per le query

    private int adminID;                    // chiave esterna
    private Admin admin;                  // relazione
    private List<Image> images;            // relazione
    private List<Comment> comments;    // relazione

    public PostMySQLImpl(MaraneDataLayer dl) {

        ID = 0;
        title = "";
        text = "";
        date = null;
        dirty = false;

        dataLayer = dl;

        adminID = 0;
        admin = null;
        images = null;
        comments = null;
    }

    public PostMySQLImpl(MaraneDataLayerMySQLImpl dataLayer, ResultSet rs) throws SQLException {
        this(dataLayer);
        ID = rs.getInt("ID");
        title = rs.getString("title");
        text = rs.getString("text");
        date = new Date(rs.getTimestamp("date").getTime());

        adminID = rs.getInt("adminID");
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        dirty = true;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        dirty = true;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
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
    public void copyFrom(Post post) {
        ID = post.getID();
        date = post.getDate();
        text = post.getText();
        title = post.getTitle();

        if (post.getAdmin() != null) {
            adminID = post.getAdmin().getID();
        } else {
            adminID = 0;
        }
        admin = null;
        images = null;
        comments = null;

        dirty = true;
    }

    /*=============================================================================================
     RELAZIONI
     =============================================================================================*/
    @Override
    public Admin getAdmin() {

        if (admin == null && adminID > 0) {
            admin = dataLayer.getAdmin(adminID);
        }

        return admin;
    }

    @Override
    public void setAdmin(Admin admin) {
        this.admin = admin;
        adminID = admin.getID();
        dirty = true;
    }

    @Override
    public List<Image> getImages() {

        if (images == null) {
            images = dataLayer.getImages(this);
        }

        return images;
    }

    @Override
    public void setImages(List<Image> images) {
        this.images = images;
        dirty = true;
    }

    @Override
    public List<Comment> getComments() {

        if (comments == null) {
            comments = dataLayer.getComments(this);
        }

        return comments;
    }

    @Override
    public void setComments(List<Comment> comments) {
        this.comments = comments;
        dirty = true;
    }

    @Override
    public String toString() {
        return "PostMysqlImpl{" + "ID=" + ID + ", title=" + title + ", text=" + text + ", date=" + date + ", dirty=" + dirty + ", adminID=" + adminID + ", admin=" + admin + ", images=" + images + ", comments=" + comments + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.ID;
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
        final PostMySQLImpl other = (PostMySQLImpl) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }

}
