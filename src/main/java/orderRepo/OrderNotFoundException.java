package orderRepo;

class OrderNotFoundException extends RuntimeException {

	OrderNotFoundException(Long id) {
		super("Invalid orderId: " + id);
	}
}
