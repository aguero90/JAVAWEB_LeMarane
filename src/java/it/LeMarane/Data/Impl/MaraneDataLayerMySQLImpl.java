package it.LeMarane.Data.Impl;

import it.LeMarane.Data.Model.Admin;
import it.LeMarane.Data.Model.Comment;
import it.LeMarane.Data.Model.Image;
import it.LeMarane.Data.Model.MaraneDataLayer;
import it.LeMarane.Data.Model.Post;
import it.univaq.f4i.iw.framework.data.DataLayerException;
import it.univaq.f4i.iw.framework.data.DataLayerMysqlImpl;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author alex
 */
public class MaraneDataLayerMySQLImpl extends DataLayerMysqlImpl implements MaraneDataLayer {

    // PreparedStatement per le SELECT (corrispondono alle get di MaraneDataLayer.java)
    private PreparedStatement sAdminByID, sAdminByUsernameAndPassword, sAdminByPost, sAdmins;
    private PreparedStatement sPostbyID, sPostByComment, sPostsByAdmin, sPostsByImage, sPosts;
    private PreparedStatement sCommentbyID, sCommentsByPost, sComments;
    private PreparedStatement sImagebyID, sImagesByPost, sImages;

    // PreparedStatemente per le INSERT (corrispondono alle store di MaraneDataLayer.java)
    private PreparedStatement iAdmin, iPost, iComment, iImage;

    // PreparedStatemente per le INSERT (corrispondono alle store di MaraneDataLayer.java)
    private PreparedStatement uAdmin, uPost, uComment, uImage;

    // PreparedStatement per le DELETE (corrispondono alle delete di MaraneDataLayer.java)
    private PreparedStatement dAdmin, dPost, dComment, dImage;

    // PreparedStatement per le relazioni
    private PreparedStatement sPostImage, dPostImage;

    // Per DB e connessione
    public MaraneDataLayerMySQLImpl(DataSource datasource) throws SQLException, NamingException {
        super(datasource);
    }

    @Override
    public void init() throws DataLayerException {
        try {
            // Per aprire la connessione
            super.init();
            // Precompiliamo tutte le query utilizzate nella classe
            sAdminByID = connection.prepareStatement("SELECT * FROM e_admin WHERE ID=?");
            sAdminByUsernameAndPassword = connection.prepareStatement("SELECT * FROM e_admin WHERE username=? AND password=?");
            sAdminByPost = connection.prepareStatement("SELECT adminID FROM e_post WHERE ID=?");
            sAdmins = connection.prepareStatement("SELECT ID FROM e_admin");

            sPostbyID = connection.prepareStatement("SELECT * FROM e_post WHERE ID=?");
            sPostByComment = connection.prepareStatement("SELECT postID FROM e_comment WHERE ID=?");
            sPostsByAdmin = connection.prepareStatement("SELECT ID FROM e_post WHERE adminID=?");
            sPostsByImage = connection.prepareStatement("SELECT postID FROM r_post_image WHERE imageID=?");
            sPosts = connection.prepareStatement("SELECT ID FROM e_post");

            sCommentbyID = connection.prepareStatement("SELECT * FROM e_comment WHERE ID=?");
            sCommentsByPost = connection.prepareStatement("SELECT ID FROM e_comment WHERE postID=?");
            sComments = connection.prepareStatement("SELECT ID FROM e_comment");

            sImagebyID = connection.prepareStatement("SELECT * FROM e_image WHERE ID=?");
            sImagesByPost = connection.prepareStatement("SELECT imageID FROM r_post_image WHERE postID=?");
            sImages = connection.prepareStatement("SELECT ID FROM e_image");

            //notare l'ultimo parametro extra di questa chiamata a
            //prepareStatement: lo usiamo per assicurarci che il JDBC
            //restituisca la chiave generata automaticamente per il
            //record inserito
            iAdmin = connection.prepareStatement("INSERT INTO e_admin (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            iPost = connection.prepareStatement("INSERT INTO e_post (title, text, date, adminID) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            iComment = connection.prepareStatement("INSERT INTO e_comment (author, text, date, postID) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            iImage = connection.prepareStatement("INSERT INTO e_image (URL, description, name, banner) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            uAdmin = connection.prepareStatement("UPDATE e_admin SET username=?, password=? WHERE ID=?");
            uPost = connection.prepareStatement("UPDATE e_post SET title=?, text=?, date=? adminID=? WHERE ID=?");
            uComment = connection.prepareStatement("UPDATE e_comment SET author=?, text=?, date=?, postID=? WHERE ID=?");
            uImage = connection.prepareStatement("UPDATE e_image SET URL=?, description=?, name=?; banner=? WHERE ID=?");

            dAdmin = connection.prepareStatement("DELETE FROM e_admin WHERE ID=?");
            dPost = connection.prepareStatement("DELETE FROM e_post WHERE ID=?");
            dComment = connection.prepareStatement("DELETE FROM e_comment WHERE ID=?");
            dImage = connection.prepareStatement("DELETE FROM e_image WHERE ID=?");

            sPostImage = connection.prepareStatement("INSERT INTO r_post_image (postID, imageID) VALUES (?, ?)");
            dPostImage = connection.prepareStatement("DELETE FROM r_post_image WHERE postID=? AND imageID=?");

        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /* CREATE
     =============================================================================================*/
    @Override
    public Admin createAdmin() {
        return new AdminMySQLImpl(this);
    }

    @Override
    public Post createPost() {
        return new PostMySQLImpl(this);
    }

    @Override
    public Comment createComment() {
        return new CommentMySQLImpl(this);
    }

    @Override
    public Image createImage() {
        return new ImageMySQLImpl(this);
    }

    /* GET
     =============================================================================================*/
    // ADMIN
    @Override
    // sAdminByID = "SELECT * FROM e_admin WHERE ID=?"
    public Admin getAdmin(int ID) {
        ResultSet rs = null;
        Admin result = null;
        try {
            sAdminByID.setInt(1, ID);
            rs = sAdminByID.executeQuery();
            if (rs.next()) { // se la select ha restituito almeno una riga
                result = new AdminMySQLImpl(this, rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    @Override
    // sAdminByUsernameAndPassword = "SELECT * FROM e_admin WHERE username=? AND password=?"
    public Admin getAdmin(String username, String password) {
        ResultSet rs = null;
        Admin result = null;
        try {
            sAdminByUsernameAndPassword.setString(1, username);
            sAdminByUsernameAndPassword.setString(2, password);
            rs = sAdminByUsernameAndPassword.executeQuery();
            if (rs.next()) { // se la select ha restituito almeno una riga
                result = new AdminMySQLImpl(this, rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    @Override
    //sAdminByPost = "SELECT adminID FROM e_post WHERE ID=?"
    public Admin getAdmin(Post post) {
        Admin result = null;
        ResultSet rs = null;
        try {
            sAdminByPost.setInt(1, post.getID());
            rs = sAdminByPost.executeQuery();
            if (rs.next()) {
                result = getAdmin(rs.getInt("adminID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    @Override
    // sAdmins = "SELECT ID FROM e_admin"
    public List<Admin> getAdmins() {
        List<Admin> result = new ArrayList();
        ResultSet rs = null;
        try {
            rs = sAdmins.executeQuery();
            while (rs.next()) { // finchè non consumo tutti i risultati
                result.add(getAdmin(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    // POST
    @Override
    // sPostbyID = "SELECT * FROM e_post WHERE ID=?"
    public Post getPost(int ID) {
        Post result = null;
        ResultSet rs = null;
        try {
            sPostbyID.setInt(1, ID);
            rs = sPostbyID.executeQuery();
            if (rs.next()) {
                result = new PostMySQLImpl(this, rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sPostByComment = "SELECT postID FROM e_comment WHERE ID=?"
    public Post getPost(Comment comment) {
        Post result = null;
        ResultSet rs = null;
        try {
            sPostByComment.setInt(1, comment.getID());
            rs = sPostByComment.executeQuery();
            if (rs.next()) {
                result = getPost(rs.getInt("postID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sPostsByAdmin = "SELECT ID FROM e_post WHERE adminID=?"
    public List<Post> getPosts(Admin admin) {
        List<Post> result = new ArrayList();
        ResultSet rs = null;
        try {
            sPostsByAdmin.setInt(1, admin.getID());
            rs = sPostsByAdmin.executeQuery();
            while (rs.next()) {
                result.add(getPost(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sPostsByImage = "SELECT postID FROM r_post_image WHERE imageID=?"
    public List<Post> getPosts(Image image) {
        List<Post> result = new ArrayList();
        ResultSet rs = null;
        try {
            sPostsByImage.setInt(1, image.getID());
            rs = sPostsByImage.executeQuery();
            while (rs.next()) {
                result.add(getPost(rs.getInt("postID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sPosts = "SELECT ID FROM e_post"
    public List<Post> getPosts() {
        List<Post> result = new ArrayList();
        ResultSet rs = null;
        try {
            rs = sPosts.executeQuery();
            while (rs.next()) {
                result.add(getPost(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    // COMMENT
    @Override
    // sCommentbyID = "SELECT * FROM e_comment WHERE ID=?"
    public Comment getComment(int ID) {
        Comment result = null;
        ResultSet rs = null;
        try {
            sCommentbyID.setInt(1, ID);
            rs = sCommentbyID.executeQuery();
            if (rs.next()) {
                result = new CommentMySQLImpl(this, rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sCommentsByPost = "SELECT ID FROM e_comment WHERE postID=?"
    public List<Comment> getComments(Post post) {
        List<Comment> result = new ArrayList();
        ResultSet rs = null;
        try {
            sCommentsByPost.setInt(1, post.getID());
            rs = sComments.executeQuery();
            while (rs.next()) {
                result.add(getComment(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sComments = "SELECT ID FROM e_comment"
    public List<Comment> getComments() {
        List<Comment> result = new ArrayList();
        ResultSet rs = null;
        try {
            rs = sComments.executeQuery();
            while (rs.next()) {
                result.add(getComment(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    // IMAGE
    @Override
    // sImagebyID = "SELECT * FROM e_image WHERE ID=?"
    public Image getImage(int ID) {
        Image result = null;
        ResultSet rs = null;
        try {
            sImagebyID.setInt(1, ID);
            rs = sImagebyID.executeQuery();
            if (rs.next()) {
                result = new ImageMySQLImpl(this, rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // sImagesByPost = "SELECT imageID FROM r_post_image WHERE postID=?"
    public List<Image> getImages(Post post) {
        List<Image> result = new ArrayList();
        ResultSet rs = null;
        try {
            sImagesByPost.setInt(1, post.getID());
            rs = sImagesByPost.executeQuery();
            while (rs.next()) {
                result.add(getImage(rs.getInt("imageID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    @Override
    // this.sImages = "SELECT ID FROM e_image"
    public List<Image> getImages() {
        List<Image> result = new ArrayList();
        ResultSet rs = null;
        try {
            rs = sImages.executeQuery();
            while (rs.next()) {
                result.add(getImage(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    /* STORE
     =============================================================================================*/
    @Override
    // uAdmin = "UPDATE e_admin SET username=?, password=? WHERE ID=?"
    // iAdmin = "INSERT INTO e_admin (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS
    public void storeAdmin(Admin admin) {
        ResultSet rs = null;
        int ID = admin.getID();
        try {
            if (ID > 0) { // update
                // se non è stato modificato, non fare nulla
                if (!admin.isDirty()) {
                    return;
                }
                // Altrimenti fail l'update sul DB
                uAdmin.setString(1, admin.getUsername());
                uAdmin.setString(2, admin.getPassword());
                uAdmin.setInt(3, ID);
                uAdmin.executeUpdate();
            } else { // insert
                // this.iAdmin = connection.prepareStatement("INSERT INTO admin (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                iAdmin.setString(1, admin.getUsername());
                iAdmin.setString(2, admin.getPassword());
                // abbiamo finito di compilare la query
                if (iAdmin.executeUpdate() == 1) {
                    rs = iAdmin.getGeneratedKeys();
                    if (rs.next()) {
                        ID = rs.getInt(1);
                    }
                }
            }
            if (ID > 0) {
                admin.copyFrom(getAdmin(ID)); // aggiorno il post
            }
            admin.setDirty(false); // dico che è pulito
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();

                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    // uPost = "UPDATE e_post SET title=?, text=?, date=? adminID=? WHERE ID=?"
    // iPost = "INSERT INTO e_post (title, text, date, adminID) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
    public void storePost(Post post) {
        ResultSet rs = null;
        int ID = post.getID();
        try {
            if (ID > 0) { // post esiste già nel DB => faccio l'update
                if (!post.isDirty()) { // se non è stato modificato nulla allora non fare nulla
                    return;
                }
                // altrimenti fai l'update sul DB
                uPost.setString(1, post.getTitle());
                uPost.setString(2, post.getText());
                uPost.setTimestamp(3, new java.sql.Timestamp(post.getDate().getTime()));
                if (post.getAdmin() != null) {
                    uPost.setInt(4, post.getAdmin().getID());
                } else {
                    uPost.setNull(4, java.sql.Types.INTEGER);
                }
                uPost.setInt(5, ID);
                uPost.executeUpdate();
            } else { // post non esiste nel DB => faccio l'insert
                iPost.setString(1, post.getTitle());
                iPost.setString(2, post.getText());
                iPost.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
                if (post.getAdmin() != null) {
                    iPost.setInt(4, post.getAdmin().getID());
                } else {
                    iPost.setNull(4, java.sql.Types.INTEGER);
                }
                if (iPost.executeUpdate() == 1) { // allora ha inserito 1 riga
                    rs = iPost.getGeneratedKeys(); // per ottenere le chiavi generate automaticamente
                    // che hanno l'auto increment
                    // restituisce un ResultSet contenente
                    // tutte le chiavi generate
                    if (rs.next()) { // se ha restituito la chiave
                        ID = rs.getInt(1);
                    }
                }
            }
            // store relationship
            List<Image> oldImages = getImages(post);
            List<Image> newImages = post.getImages();
            if (newImages != null) {
                for (Image i : oldImages) {
                    if (!newImages.contains(i)) {
                        removePostImage(post, i);
                    }
                }
                for (Image i : newImages) {
                    if (!oldImages.contains(i)) {
                        storePostImage(post, i);
                    }
                }
            }
            if (ID > 0) { // ho eseguito il blocco di codice ID = rs.getInt(1);
                post.copyFrom(getPost(ID)); // aggiorna il post a quello appena salvato
            }
            post.setDirty(false); // considera il post pulito
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    // iComment = "INSERT INTO e_comment (author, text, date, postID) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
    // uComment = "UPDATE e_comment SET author=?, text=?, date=?, postID=? WHERE ID=?"
    public void storeComment(Comment comment) {
        ResultSet rs = null;
        int ID = comment.getID();
        try {
            if (ID > 0) { // update
                // se non è stato modificato, non fare nulla
                if (!comment.isDirty()) {
                    return;
                }
                // Altrimenti fail l'update sul DB
                uComment.setString(1, comment.getAuthor());
                uComment.setString(2, comment.getText());
                uComment.setTimestamp(3, new java.sql.Timestamp(comment.getDate().getTime()));
                if (comment.getPost() != null) {
                    uComment.setInt(4, comment.getPost().getID());
                } else {
                    uComment.setNull(4, java.sql.Types.INTEGER);
                }
                uComment.setInt(5, comment.getID());
                uComment.executeUpdate();
            } else { // insert
                // assumiamo che se un commento è postato da un Admin, Comment.author corrisponderà a Admin.Username
                iComment.setString(1, comment.getAuthor());
                iComment.setString(2, comment.getText());
                iComment.setTimestamp(3, new java.sql.Timestamp(comment.getDate().getTime()));
                if (comment.getPost() != null) {
                    iComment.setInt(4, comment.getPost().getID());
                } else {
                    iComment.setNull(4, java.sql.Types.INTEGER);
                }
                // abbiamo finito di compilare la query
                if (iComment.executeUpdate() == 1) {
                    rs = iComment.getGeneratedKeys();
                    if (rs.next()) {
                        ID = rs.getInt(1);
                    }
                }
            }
            if (ID > 0) {
                comment.copyFrom(getComment(ID)); // aggiorno il post
            }
            comment.setDirty(false); // dico che è pulito
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();

                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    // uImage = "UPDATE e_image SET URL=?, description=?, name=?; banner=? WHERE ID=?"
    // iImage = "INSERT INTO e_image (URL, description, name, banner) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
    public void storeImage(Image image) {
        ResultSet rs = null;
        int ID = image.getID();
        try {
            if (ID > 0) { // update
                // se non è stato modificato, non fare nulla
                if (!image.isDirty()) {
                    return;
                }
                // Altrimenti fail l'update sul DB
                uImage.setString(1, image.getURL());
                uImage.setString(2, image.getDescription());
                uImage.setString(3, image.getName());
                uImage.setBoolean(4, image.isBanner());
                uImage.setInt(5, ID);
                uImage.executeUpdate();
            } else { // insert
                iImage.setString(1, image.getURL());
                iImage.setString(2, image.getDescription());
                iImage.setString(3, image.getName());
                iImage.setBoolean(4, image.isBanner());
                // abbiamo finito di compilare la query
                if (iImage.executeUpdate() == 1) {
                    rs = iImage.getGeneratedKeys();
                    if (rs.next()) {
                        ID = rs.getInt(1);
                    }
                }
            }
            // store relationship
            List<Post> oldPosts = getPosts(image);
            List<Post> newPosts = image.getPosts();
            if (newPosts != null) {
                for (Post p : oldPosts) {
                    if (!newPosts.contains(p)) {
                        removePostImage(p, image);
                    }
                }
                for (Post p : newPosts) {
                    if (!oldPosts.contains(p)) {
                        storePostImage(p, image);
                    }
                }
            }
            if (ID > 0) {
                image.copyFrom(getImage(ID)); // aggiorno il post
            }
            image.setDirty(false); // dico che è pulito
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();

                } catch (SQLException ex) {
                    Logger.getLogger(MaraneDataLayerMySQLImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /* DELETE
     =============================================================================================*/
    @Override
    // dAdmin = "DELETE FROM e_admin WHERE ID=?"
    public void removeAdmin(Admin admin) {
        try {
            dAdmin.setInt(1, admin.getID());
            dAdmin.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    // dPost = "DELETE FROM post WHERE ID=?"
    public void removePost(Post post) {
        try {
            dPost.setInt(1, post.getID());
            dPost.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    // dComment = "DELETE FROM comment WHERE ID=?"
    public void removeComment(Comment comment) {
        try {
            dComment.setInt(1, comment.getID());
            dComment.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    // dImage = "DELETE FROM image WHERE ID=?"
    public void removeImage(Image image) {
        try {
            dImage.setInt(1, image.getID());
            dImage.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* RELATIONSHIP
     =============================================================================================*/
    // sPostImage = "INSERT INTO r_post_image (postID, imageID) VALUES (?, ?)"
    private void storePostImage(Post post, Image image) {
        try {
            sPostImage.setInt(1, post.getID());
            sPostImage.setInt(2, image.getID());
            sPostImage.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // sPostImage = "DELETE FROM r_post_image WHERE postID=? AND imageID=?"
    private void removePostImage(Post post, Image image) {
        try {
            dPostImage.setInt(1, post.getID());
            dPostImage.setInt(2, image.getID());
            dPostImage.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MaraneDataLayerMySQLImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
