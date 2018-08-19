package Actors;

public class Admin {
    
    // this class is for attributes defining a admin
    private String adminFirstName;
    private String adminLastName;
    private String adminAge;
    private String adminEmail;
    private String adminUserName;
    private String adminPassword;
    private String adminPhoneNumber;
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
