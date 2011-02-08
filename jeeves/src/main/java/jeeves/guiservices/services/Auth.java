package jeeves.guiservices.services;

import jeeves.interfaces.Service;

/**
 * Interface for checking login credentials.  If a strategy is present in the guiServices it will be called each access
 * to validate that the current credentials are correct.  This is useful for Auth services like cas 
 *
 * User: jeichar
 * Date: Oct 13, 2010
 * Time: 3:40:46 PM
 */
public interface Auth extends Service{
}
