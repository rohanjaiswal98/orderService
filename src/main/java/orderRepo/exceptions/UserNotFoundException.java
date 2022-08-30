package orderRepo.exceptions;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long id) {
		super("Invalid userId: " + id);
	}
}
