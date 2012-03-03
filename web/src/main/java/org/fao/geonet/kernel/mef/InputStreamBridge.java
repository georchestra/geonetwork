
package org.fao.geonet.kernel.mef;

import java.io.IOException;
import java.io.InputStream;

class InputStreamBridge extends InputStream
{
	// --------------------------------------------------------------------------
	// ---
	// --- Constructor
	// ---
	// --------------------------------------------------------------------------

	public InputStreamBridge(InputStream is)
	{
		this.is = is;
	}

	// --------------------------------------------------------------------------
	// ---
	// --- Bridging methods
	// ---
	// --------------------------------------------------------------------------

	public int read() throws IOException { return is.read(); }

	public int available() throws IOException { return is.available(); }

	// --- this *must* be empty to work with zip files
	public void close() throws IOException {}

	public synchronized void mark(int readlimit) { is.mark(readlimit); }

	public synchronized void reset() throws IOException { is.reset(); }

	public boolean markSupported() {	return is.markSupported(); }

	// --------------------------------------------------------------------------
	// ---
	// --- Variables
	// ---
	// --------------------------------------------------------------------------

	private InputStream is; 
}