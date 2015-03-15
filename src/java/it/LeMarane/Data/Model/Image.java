package it.LeMarane.Data.Model;

import java.util.List;

/**
 *
 * @author alex
 */
public interface Image {

    int getID();

    String getURL();

    void setURL(String URL);

    String getDescription();

    void setDescription(String description);

    String getName();

    void setName(String name);

    boolean isBanner();

    void setBanner(boolean banner);

    boolean isDirty();

    void setDirty(boolean dirty);

    void copyFrom(Image image);

    /*====================
     RELAZIONI
     =====================*/
    List<Post> getPosts();

    void setPosts(List<Post> posts);
}
