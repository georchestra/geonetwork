package org.fao.geonet.services.group;

import jeeves.exceptions.JeevesException;

public class LdapSyncException extends JeevesException 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5542026340452657347L;

	public LdapSyncException(String message, Object ex)
	{
		super(message, ex);
		id = "sync-error";
	}

}