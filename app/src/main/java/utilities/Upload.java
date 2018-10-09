package utilities;

public class Upload {
    public String imgUrl;
    public String txtName;

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Upload() {
    }
    public Upload(String imageUrl){
        this.imgUrl = imageUrl;
    }
}
