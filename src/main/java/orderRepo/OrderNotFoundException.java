package orderRepo;

class OrderNotFoundException extends RuntimeException {

	OrderNotFoundException(Long id) {
		super("Could not find product id:" + id);
	}
}
