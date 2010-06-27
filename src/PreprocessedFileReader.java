package com.buttware.dmlint;
import org.antlr.runtime.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;

class PreprocessedFileStream extends ANTLRStringStream {
	PreprocessedFileStream(String filename) {
		super(read(filename));
	}

	private static String read(String filename) {
		HashMap<String, String> defines = new HashMap<String, String>();
		defines.put("DM_VERSION", "300"); // TODO: maybe make this smarter
		return read(filename, defines, new HashSet<String>());
	}

	private static String read(String filename, Map<String, String> defines, Set<String> included) {
		File f = new File(filename);
		try {
			String canonpath = f.getCanonicalPath();
			if(included.contains(canonpath) || canonpath.endsWith(".dmp") || canonpath.endsWith(".dmf")) {
				// already included or is a map or an interface file
				return "";
			}
			included.add(canonpath);
		} catch(IOException e) {
			System.out.println(e);
			return "";
		}


		try {
			StringBuilder s = new StringBuilder();
			Scanner scanner = new Scanner(f);
			while(scanner.hasNextLine()) {
				s.append(preprocessLine(scanner.nextLine(), f.getParent(), defines, included));
				s.append("\n");
			}
			return s.toString();
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return "";
		}
	}

	static final Pattern DEFINE_PATTERN = Pattern.compile("\\#define \\s+ (\\w+) (\\s+(.*?))? \\s*(//.*)?$", Pattern.COMMENTS);
	static final Pattern INCLUDE_PATTERN = Pattern.compile("\\#include \\s+ \"(.+)\"", Pattern.COMMENTS);

	private static String preprocessLine(String line, String curdir, Map<String, String> defines,
			Set<String> included) {
		// See if it's a #define.
		Matcher m = DEFINE_PATTERN.matcher(line);
		if(m.matches()) {
			defines.put(m.group(1), m.group(3));
			return "";
		}

		// See if it's an #include.
		m = INCLUDE_PATTERN.matcher(line);
		if(m.matches()) {
			// Although this is not how C++'s #include works (it tries parent dirs and so on), it is how DM's works.
			return read(curdir + '/' + m.group(1), defines, included);
		}
		return line;
	}

	// for debugging - probably a little inefficient, requires copying a several-meg string
	public String getString() {
		return new String(data);
	}
}
