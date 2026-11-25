package system.exception;
public class WrongPasswordException extends BusinessException {
    public WrongPasswordException(String message) { super(message); }
}