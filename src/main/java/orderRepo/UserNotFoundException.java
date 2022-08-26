package orderRepo;

class UserNotFoundException extends RuntimeException {

	UserNotFoundException(Long id) {
		super("Invalid userId: " + id);
	}
}
