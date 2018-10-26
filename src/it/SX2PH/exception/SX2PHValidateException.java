package it.SX2PH.exception;

@SuppressWarnings("serial")
public class SX2PHValidateException extends Throwable {
	String errorMessage;
	
	public SX2PHValidateException(String message) {
		errorMessage=message;
	}
	
	public void printErrorMessage() {
		System.err.println(errorMessage);
	}
}
