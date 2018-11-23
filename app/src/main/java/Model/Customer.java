package Model;

public class Customer {


    // this class is for attributes defining a customer
    private String customerFirstName;
    private String customerLastName;
    private String CustomerAge;
    private String customerEmail;
    private String customerUserName;
    private String customerPassword;
    private String customerPhoneNumber;
    public Customer(){

    }
    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public void setCustomerAge(String customerAge) {
        CustomerAge = customerAge;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerUserName(String customerUserName) {
        this.customerUserName = customerUserName;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }



    public Customer(String customerFirstName, String customerLastName, String customerAge,
                    String customerEmail, String customerUserName, String customerPassword
                    ,String customerPhoneNumber) {
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.CustomerAge = customerAge;
        this.customerEmail = customerEmail;
        this.customerUserName = customerUserName;
        this.customerPassword = customerPassword;
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }
    public String getCustomerLastName() {
        return customerLastName;
    }
    public String getCustomerAge() {
        return CustomerAge;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public String getCustomerUserName() {
        return customerUserName;
    }
    public String getCustomerPassword() {
        return customerPassword;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }


}
