package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private OrderDao orderDao;
	private LineItemDao lineItemDao;
	private CustomerDao customerDao;

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setLineItemDao(LineItemDao lineItemDao) {
		this.lineItemDao = lineItemDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.getCustomerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.getBookId()))
				.collect(Collectors.toList());
		return new OrderDetails(order, customer, lineItems, books);
	}

	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);

		// NOTE: MORE CODE PROVIDED NEXT PROJECT
		try (Connection connection = JdbcUtils.getConnection()) {
			LocalDate date = getLocalDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					date, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}

	}


	private void validateCustomer(CustomerForm customerForm) {

    	String name = customerForm.getName();
		String address = customerForm.getAddress();
		String phone = customerForm.getPhone().replaceAll("\\D","");
		String email = customerForm.getEmail();
		String ccNumber = customerForm.getCcNumber().replaceAll("\\D", "");

		if (name == null || name.equals("") || name.length() < 4 || name.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid name field");
		}

		// TODO: Validation checks for address, phone, email, ccNumber
		if (address == null || address.equals("") || address.length() < 4 || address.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid address field");
		}

		if (phone == null || phone.equals("")) {
			throw new ApiException.InvalidParameter("Invalid phone field");
		} else if ( phone.length() != 10 ) {
			throw new ApiException.InvalidParameter("Invalid phone field (digits)");
		}

		if (email == null || email.equals("") || email.contains(" ")) {
			throw new ApiException.InvalidParameter("Invalid email field");
		} else if (!email.contains("@") ) {
			throw new ApiException.InvalidParameter("Invalid email field. Does not have @");
		} else if ( email.charAt(email.length()-1) == '.') {
			throw new ApiException.InvalidParameter("Invalid email field. Cannot end in '.'");
		}

		if (ccNumber == null | ccNumber.equals("") || ccNumber.length() < 14 || ccNumber.length() > 16) {
			throw new ApiException.InvalidParameter("Invalid credit card number field");
		}

		if (expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.InvalidParameter("Invalid expiry date");
		}

	}

	private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {

		// TODO: return true when the provided month/year is before the current month/yeaR
		// HINT: Use Integer.parseInt and the YearMonth class
		YearMonth yearMonth = YearMonth.now();
		YearMonth providedYearMonth = YearMonth.of(Integer.parseInt(ccExpiryYear), Integer.parseInt(ccExpiryMonth));
		return providedYearMonth.isBefore(yearMonth);

	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.InvalidParameter("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.InvalidParameter("Invalid quantity");
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());
			// TODO: complete the required validations
			if (databaseBook.getPrice() != item.getBookForm().getPrice()) {
				throw new ApiException.InvalidParameter("Price of book does not match.");
			}
			if (databaseBook.getCategoryId() != item.getBookForm().getCategoryId()) {
				throw new ApiException.InvalidParameter("Category of book does not match.");
			}
		});
	}

	private LocalDate getLocalDate(String monthString,
								   String yearString) {
		int month = Integer.parseInt(monthString);
		int year = Integer.parseInt(yearString);
		LocalDate init = LocalDate.of(year, month, 1);
		return init.withDayOfMonth(init.lengthOfMonth());
	}

	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, LocalDate date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, customerOrderId, item.getBookId(),
						item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}

	private int generateConfirmationNumber() {
		Random random = new Random();
		return random.nextInt(999999999);
	}

}
