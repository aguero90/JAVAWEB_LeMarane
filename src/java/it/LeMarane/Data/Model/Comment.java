package it.LeMarane.Data.Model;

import java.util.Date;

/**
 *
 * @author alex
 */
public interface Comment {

    int getID();

    String getAuthor();

    void setAuthor(String author);

    String getText();

    void setText(String text);

    Date getDate();

    void setDate(Date date);

    boolean isDirty();

    void setDirty(boolean dirty);

    void copyFrom(Comment comment);

    /*====================
     RELAZIONI
     =====================*/
    Post getPost();

    void setPost(Post post);

}
