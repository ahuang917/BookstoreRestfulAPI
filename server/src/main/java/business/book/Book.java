package business.book;

public class Book {

	private long bookId;
	private String title;
	private String author;
	private int price;
	private boolean isPublic;
	private long categoryId;


	public Book(long bookId, String title, String author, int price, boolean isPublic, long categoryId) {
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.price = price;
		this.isPublic = isPublic;
		this.categoryId = categoryId;
	}

	public long getBookId() {
		return bookId;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public int getPrice() {
		return price;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public long getCategoryId() {
		return categoryId;
	}

	@Override
	public String toString() {
		return "Book{" +
				"bookId=" + bookId +
				", title='" + title + '\'' +
				", author='" + author + '\'' +
				", price=" + price +
				", isPublic=" + isPublic +
				", categoryId=" + categoryId +
				'}';
	}
}
