package it.LeMarane.Data.Model;

import it.univaq.f4i.iw.framework.data.DataLayer;
import java.util.List;

/**
 *
 * @author alex
 */
public interface MaraneDataLayer extends DataLayer {

    //  CREATE
    Admin createAdmin();

    Post createPost();

    Comment createComment();

    Image createImage();

    // GET
    Admin getAdmin(int ID);

    Admin getAdmin(String username, String password);

    Admin getAdmin(Post post);

    List<Admin> getAdmins();

    Post getPost(int ID);

    Post getPost(Comment comment);

    List<Post> getPosts(Image image);

    List<Post> getPosts(Admin admin);

    List<Post> getPosts();

    Comment getComment(int ID);

    List<Comment> getComments(Post post);

    List<Comment> getComments();

    Image getImage(int ID);

    List<Image> getImages(Post post);

    List<Image> getImages();

    // STORE
    void storeAdmin(Admin admin);

    void storePost(Post post);

    void storeComment(Comment comment);

    void storeImage(Image image);

    // DELETE
    void removeAdmin(Admin admin);

    void removePost(Post post);

    void removeComment(Comment comment);

    void removeImage(Image image);
}
