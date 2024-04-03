package crl;

import java.io.File;
import java.io.FilenameFilter;

class SaveGameFilenameFilter implements FilenameFilter {

	public boolean accept(File arg0, String arg1) {
		// if (arg0.getName().endsWith(".sav"))
		return arg1.endsWith(".sav");
	}

}
