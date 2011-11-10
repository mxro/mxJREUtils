package mx.jreutils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

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

}
