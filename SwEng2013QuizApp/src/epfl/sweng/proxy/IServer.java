package epfl.sweng.proxy;

import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerEvent;

/**
 * Network communivation interface
 */
public interface IServer {

	/**
	 * Send a HTTP GET Request
	 * @param reqContext The request (URL + Headers + Entity) to send
	 * @param event Callback containing serverResponse (Entity + StatusCode)
	 */
	void doHttpGet(RequestContext reqContext, ServerEvent event);
	/**
	 * Send a HTTP POST Request
	 * @param reqContext The request (URL + Headers + Entity) to send
	 * @param event Callback containing serverResponse (Entity + StatusCode)
	 */
	void doHttpPost(RequestContext reqContext, ServerEvent event);
	/**
	 * Ajoute un gestionnaire à cet emetteur.
	 */
	void addListener(EventListener listener);
	/**
	 * Retire un gestionnaire de cet émetteur.
	 */
    void removeListener(EventListener listener);
	
}
