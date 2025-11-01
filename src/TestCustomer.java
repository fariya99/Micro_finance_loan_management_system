import controller.CustomerController;
import model.Customer;

public class TestCustomer {
    public static void main(String[] args) {
        CustomerController controller = new CustomerController();

        // Add a few customers
        Customer c1 = new Customer("C001", "Maryam", "maryam@gmail.com", "0312-1111111", "42101-1234567-8", "Karachi");
        Customer c2 = new Customer("C002", "Ayesha", "ayesha@gmail.com", "0321-2222222", "42101-9876543-1", "Lahore");

        controller.addCustomer(c1);
        controller.addCustomer(c2);

        // Show all customers
        controller.showAllCustomers();

        // Edit a customer's phone and email
        controller.editCustomer("C001", "maryam.new@gmail.com", "0300-1234567");

        // Search for one customer
        Customer found = controller.searchCustomer("C001");
        if (found != null) {
            found.displayCustomerDetails();
        }

        // Delete one customer
        controller.deleteCustomer("C002");

        // Show updated list
        controller.showAllCustomers();
    }
}
