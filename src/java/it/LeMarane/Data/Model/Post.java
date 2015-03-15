package it.LeMarane.Data.Model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author alex
 */
public interface Post {

    int getID();

    String getTitle();

    void setTitle(String title);

    String getText();

    void setText(String text);

    Date getDate();

    void setDate(Date date);

    boolean isDirty();

    void setDirty(boolean dirty);

    void copyFrom(Post post);

    /*====================
     RELAZIONI
     =====================*/
    Admin getAdmin();

    void setAdmin(Admin admin);

    List<Image> getImages();

    void setImages(List<Image> images);

    List<Comment> getComments();

    void setComments(List<Comment> comments);
}
