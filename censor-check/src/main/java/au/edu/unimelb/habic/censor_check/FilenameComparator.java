package au.edu.unimelb.habic.censor_check;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator to compare files by filename (ignoring extension and path).
 */
public class FilenameComparator implements Comparator<File> {

	public static final String EXTENSION_REGEX= "\\.[^.]+$";
	
	@Override
	public int compare(File o1, File o2) {
		return o1.getName().replaceFirst(EXTENSION_REGEX, "").compareTo(o2.getName().replaceFirst(EXTENSION_REGEX, ""));
	}

}
