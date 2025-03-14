package main.java.personnel;

/**
 * Exception levée lorsqu'il est impossible de sauvegarder le gestionnaire.
 */

public class SauvegardeImpossible extends Exception
{
	private static final long serialVersionUID = 6651919630441855001L;	
	Exception exception;
	
	public SauvegardeImpossible(Exception exception)
	{
		this.exception = exception;
	}
	
	public SauvegardeImpossible(String string) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void printStackTrace() 
	{
			super.printStackTrace();
			System.err.println("Causé par : ");
			exception.printStackTrace();			
	}
}
