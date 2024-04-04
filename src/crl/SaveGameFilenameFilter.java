package crl;

import java.io.File;
import java.io.FilenameFilter;

class SaveGameFilenameFilter implements FilenameFilter {

	public boolean accept(File arg0, String arg1) {
		return arg1.endsWith(".sav");
	}

}
