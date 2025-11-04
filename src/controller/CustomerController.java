package controller;

import model.Customer;
import java.util.ArrayList;


public class CustomerController {
    // Step 1: make a list to hold all customers
    private ArrayList<Customer> customers = new ArrayList<>();

    // Step 2: add a customer to list
    public void addCustomer(Customer c) {
        customers.add(c);
         System.out.println("Added: " + c.getName());
    }

    // Step 3: search a customer by ID
    public Customer searchCustomer(String id) {
        for (Customer c : customers) {
            if (c.getCustomerID().equals(id)) {
                return c; // found
            }
        }
        return null; // not found
    }

    // Step 4: edit a customer's info
    public void editCustomer(String id, String newEmail, String newPhone) {
        Customer c = searchCustomer(id);
        if (c != null) {
            c.setEmail(newEmail);
            c.setPhoneNumber(newPhone);
        }
    }

    // Step 5: delete a customer
    public void deleteCustomer(String id) {
        Customer c = searchCustomer(id);
        if (c != null) {
            customers.remove(c);
        }
    }

    // Step 6: show all customers (for testing)
    public void showAllCustomers() {
        for (Customer c : customers) {
            System.out.println(c.getName() + " - " + c.getEmail());
        }
    }

    // Getter for customers list
    public ArrayList<Customer> getCustomers() {
        return customers;
    }
}


