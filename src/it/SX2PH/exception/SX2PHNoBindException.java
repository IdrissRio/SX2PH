package it.SX2PH.exception;

@SuppressWarnings("serial")
public class SX2PHNoBindException extends Throwable {
	String errorMessage;
	
	public SX2PHNoBindException(String message) {
		errorMessage=message;
	}
	
	public void printErrorMessage() {
		System.err.println(errorMessage);
	}
}
