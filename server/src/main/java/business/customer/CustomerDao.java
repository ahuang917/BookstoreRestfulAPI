package business.customer;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface CustomerDao {

    public long create(Connection connection, String customerName, String address,
                       String phone, String email, String ccNumber, LocalDate ccExpDate);

    public List<Customer> findAll();

    public Customer findByCustomerId(long customerId);

}
