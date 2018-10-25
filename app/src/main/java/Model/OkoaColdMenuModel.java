package Model;

public class OkoaColdMenuModel {
    private int image;
    private String desc;
    private String title;

    public OkoaColdMenuModel(int image, String title, String desc) {
        this.image = image;
        this.desc = desc;
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
