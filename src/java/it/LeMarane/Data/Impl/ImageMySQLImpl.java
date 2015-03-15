package it.LeMarane.Data.Impl;

import it.LeMarane.Data.Model.Image;
import it.LeMarane.Data.Model.MaraneDataLayer;
import it.LeMarane.Data.Model.Post;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author alex
 */
public class ImageMySQLImpl implements Image {

    private int ID;
    private String URL;
    private String description;
    private String name;
    private boolean banner;
    protected boolean dirty;

    protected MaraneDataLayer dataLayer;

    List<Post> posts;

    public ImageMySQLImpl(MaraneDataLayer dl) {

        ID = 0;
        URL = "";
        description = "";
        name = "";
        banner = false;
        dirty = false;

        dataLayer = dl;

        posts = null;
    }

    public ImageMySQLImpl(MaraneDataLayerMySQLImpl dataLayer, ResultSet rs) throws SQLException {
        this(dataLayer);
        ID = rs.getInt("ID");
        URL = rs.getString("URL");
        description = rs.getString("description");
        name = rs.getString("name");
        banner = rs.getBoolean("banner");
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public void setURL(String URL) {
        this.URL = URL;
        dirty = true;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
        dirty = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        dirty = true;
    }

    @Override
    public boolean isBanner() {
        return banner;
    }

    @Override
    public void setBanner(boolean banner) {
        this.banner = banner;
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
    public void copyFrom(Image image) {

        ID = image.getID();
        URL = image.getURL();
        description = image.getDescription();
        name = image.getName();
        banner = image.isBanner();

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
        return "ImageMysqlImpl{" + "ID=" + ID + ", URL=" + URL + ", description=" + description + ", name=" + name + ", banner=" + banner + ", dirty=" + dirty + ", posts=" + posts + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.ID;
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
        final ImageMySQLImpl other = (ImageMySQLImpl) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }

}
