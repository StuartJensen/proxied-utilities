package com.pp.proxied.utilities.register;

public class InvalidRegisterEntryException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public InvalidRegisterEntryException(String strMessage)
	{
		super(strMessage);
	}
}
