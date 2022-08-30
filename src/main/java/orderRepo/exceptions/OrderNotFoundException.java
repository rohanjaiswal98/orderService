package orderRepo.exceptions;

public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(Long id) {
		super("Invalid orderId: " + id);
	}
}
