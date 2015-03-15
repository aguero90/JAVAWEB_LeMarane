package it.LeMarane.Data.Impl;

import it.LeMarane.Data.Model.Comment;
import it.LeMarane.Data.Model.MaraneDataLayer;
import it.LeMarane.Data.Model.Post;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author alex
 */
public class CommentMySQLImpl implements Comment {

    private int ID;
    private String author;
    private String text;
    private Date date;
    protected boolean dirty;

    protected MaraneDataLayer dataLayer; // per le query

    private int postID;      // chiave esterna
    private Post post;       // relazione

    public CommentMySQLImpl(MaraneDataLayer dl) {

        ID = 0;
        author = "";
        text = "";
        date = null;
        dirty = false;

        dataLayer = dl;

        postID = 0;
        post = null;
    }

    public CommentMySQLImpl(MaraneDataLayerMySQLImpl dataLayer, ResultSet rs) throws SQLException {
        this(dataLayer);
        ID = rs.getInt("ID");
        author = rs.getString("author");
        text = rs.getString("text");
        date = new Date(rs.getTimestamp("date").getTime());

        postID = rs.getInt("postID");
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
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
    public void copyFrom(Comment comment) {
        ID = comment.getID();
        author = comment.getAuthor();
        date = comment.getDate();
        text = comment.getText();

        if (comment.getPost() != null) {
            postID = comment.getPost().getID();
        } else {
            postID = 0;
        }
        post = null;

        dirty = true;
    }

    /*====================
     RELAZIONI
     =====================*/
    @Override
    public Post getPost() {

        if (post == null && postID > 0) {
            post = dataLayer.getPost(postID);
        }

        return post;
    }

    @Override
    public void setPost(Post post) {
        this.post = post;
        postID = post.getID();
        dirty = true;
    }

    @Override
    public String toString() {
        return "CommentMysqlImpl{" + "ID=" + ID + ", author=" + author + ", text=" + text + ", date=" + date + ", dirty=" + dirty + ", postID=" + postID + ", post=" + post + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.ID;
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
        final CommentMySQLImpl other = (CommentMySQLImpl) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }

}
