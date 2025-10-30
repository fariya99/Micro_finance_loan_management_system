package model;

public class Customer{
    private String CustomerID;
    private String name;
    private String email;
    private String phoneNumber;
    private String cnic;
    private String address;
    //constructor
    public Customer(String customerID, String name, String email, String phoneNumber, String cnic, String address){
        this.CustomerID = customerID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cnic = cnic;
        this.address = address;
    }
    //getter and setter methods
    public String getCustomerId(){return CustomerID;}
    public void setCustomerID(string customerID){this.CustomerID = customerID;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getCnic(){return cnic;}
    public void setCnic(String cnic){this.cnic = cnic;}

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}
    
    // function to add to Add, edit, delete, and search customers.
    private void displayCustomerDetails(){
        System.out.println("Customer Details:");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("CNIC: " + cnic);

    }
    private void editCustomerDetails(String name, String email, String phoneNumber, String cnic){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cnic = cnic;
    }
    private void addCustomer(string name, String email, String phoneNumber, String cnic, string address){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cnic = cnic;
    }

}

