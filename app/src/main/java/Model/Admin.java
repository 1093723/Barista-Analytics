package Model;

public class Admin {

    // this class is for attributes defining a admin
    private String adminFirstName;
    private String adminLastName;
    private String adminAge;
    private String adminEmail;
    private String adminUserName;
    private String adminPassword;
    private String adminPhoneNumber;

    public String getAdminFirstName() {
        return adminFirstName;
    }

    public void setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
    }

    public String getAdminLastName() {
        return adminLastName;
    }

    public void setAdminLastName(String adminLastName) {
        this.adminLastName = adminLastName;
    }

    public String getAdminAge() {
        return adminAge;
    }

    public void setAdminAge(String adminAge) {
        this.adminAge = adminAge;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminPhoneNumber() {
        return adminPhoneNumber;
    }

    public void setAdminPhoneNumber(String adminPhoneNumber) {
        this.adminPhoneNumber = adminPhoneNumber;
    }

    public Admin(){

    }

    public Admin(String adminFirstName, String adminLastName, String adminAge,
                 String adminEmail, String adminUserName, String adminPassword,
                 String adminPhoneNumber) {
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
        this.adminAge = adminAge;
        this.adminEmail = adminEmail;
        this.adminUserName = adminUserName;
        this.adminPassword = adminPassword;
        this.adminPhoneNumber = adminPhoneNumber;
    }
    public String getadminFirstName() {
        return adminFirstName;
    }
    public String getadminLastName() {
        return adminLastName;
    }
    public String getadminAge() {
        return adminAge;
    }
    public String getadminEmail() {
        return adminEmail;
    }
    public String getadminUserName() {
        return adminUserName;
    }
    public String getadminPassword() {
        return adminPassword;
    }
    public String getadminPhoneNumber() {
        return adminPhoneNumber;
    }


}
