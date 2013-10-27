/*
 * This code licensed to public domain
 */
package obix.test;

/**
 * TestException
 * 
 * @author Brian Frank
 * @creation 27 Apr 05
 * @version $Revision$ $Date$
 */
public class TestException extends RuntimeException
{
	private static final long serialVersionUID = 7301444450480765923L;

	public TestException(String msg)
	{
		super(msg);
	}

}
