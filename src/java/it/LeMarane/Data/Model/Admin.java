package it.LeMarane.Data.Model;

import java.util.List;

/**
 *
 * @author alex
 */
public interface Admin {

    int getID();

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    boolean isDirty();

    void setDirty(boolean dirty);

    void copyFrom(Admin admin);

    /*=============================================================================================
     RELAZIONI
     =============================================================================================*/
    List<Post> getPosts();

    void setPosts(List<Post> posts);
}
