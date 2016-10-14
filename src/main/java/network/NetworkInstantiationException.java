package network;

public class NetworkInstantiationException extends Exception {
	/**
	 * Remove warnings
	 */
	private static final long serialVersionUID = 6L;

	public NetworkInstantiationException() { super(); }
	  public NetworkInstantiationException(String message) { super(message); }
	  public NetworkInstantiationException(String message, Throwable cause) { super(message, cause); }
	  public NetworkInstantiationException(Throwable cause) { super(cause); }
}