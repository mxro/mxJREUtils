/*******************************************************************************
 * Copyright 2011 Max Erik Rohde http://www.mxro.de
 * 
 * All rights reserved.
 ******************************************************************************/
package mx.jreutils;

import static mx.gwtutils.MxroGWTUtils.emptyOrNull;
import static mx.gwtutils.MxroGWTUtils.flip;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class MxJREUtils {

	/**
	 * Checks to see if a specific port is available.
	 * 
	 * @Author From apache Mina project
	 * 
	 * @param port
	 *            the port to check for availability
	 */
	public static boolean portAvailable(final int port) {
		if (port < 1 || port > 30000) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}
	
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (final IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}
	
			if (ss != null) {
				try {
					ss.close();
				} catch (final IOException e) {
					/* should not be thrown */
				}
			}
		}
	
		return false;
	}

	public static int nextAvailablePort(int start) {
		while (!portAvailable(start)) {
			start++;
		}
		return start;
	}
	
	/**
	 * 
	 * Exceptions:<br/>
	 * 
	 * <li>Default ports will not be made part of the path. eg.
	 * http://www.mxro.de:80/test/test1.xml?page=1&parameter=space+1#home will
	 * return de/mxro/www/http/test/test1.xml_page_1_parameter_space_1_home and
	 * not de/mxro/www/http/<strong>80</strong>/test/test1.
	 * xml_page_1_parameter_space_1_home</li>
	 * 
	 * 
	 */
	public final static String getFileForURI(final String suri) {

		try {
			final URI uri = new URI(suri);

			assert uri.isAbsolute() : "Cannot determine directory for node: URI must be absolute: <"
					+ suri + ">";

			// example
			// http://www.mxro.de:80/test/test1.xml?page=1&parameter=space+1#home
			final String scheme = uri.getScheme(); // eg <http>
			final String host = uri.getHost(); // eg <www.mxro.de>
			String port = String.valueOf(uri.getPort()); // eg <80>
			if (port.equals("-1"))
				port = "";
			final String path = uri.getPath(); // eg </test/test1.xml>
			final String query = uri.getQuery(); // eg <page=1>
			final String fragment = uri.getFragment(); // eg <home>

			assert !emptyOrNull(host) : "Cannot determine directory for node: host must be specified in URI: <"
					+ suri + ">";

			final StringBuffer dir = new StringBuffer();

			// reverse the host eg www.mxro.de becomes de/mxro/www/
			for (final String hostPart : flip(Arrays.asList(host.split("\\.")))) {
				dir.append(hostPart + "/");
			}

			if (!emptyOrNull(scheme))
				dir.append(scheme + "/");

			if (!emptyOrNull(port)
					&& !(port.equals("80") && scheme.equals("http")))
				dir.append(port + "/");

			if (!emptyOrNull(path) && path.length() > 2)
				dir.append(path.substring(1));

			if (!emptyOrNull(query))
				dir.append("_"
						+ query.replaceAll("=", "_").replaceAll("&", "_")
								.replaceAll("\\+", "_"));

			if (!emptyOrNull(fragment))
				dir.append("_" + fragment);

			return dir.toString();

		} catch (final URISyntaxException e) {
			throw new IllegalArgumentException(
					"Cannot determine directory for node: no valid URI: <"
							+ suri + ">");
		}

	}

}
