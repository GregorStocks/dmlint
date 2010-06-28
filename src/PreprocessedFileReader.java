package com.buttware.dmlint;
import org.antlr.runtime.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;

class PreprocessedFileStream extends ANTLRStringStream {
	PreprocessedFileStream(String filename) {
		super(removeInterpolation(new StringReader(preCanonicalize(read(filename))), false));
	}

	// Convert all text("...", arg) to interpolated strings. Because it's illegal to have text(text(str, arg)), and
	// there's code which uses text("str [arg]"). Don't ask me why, even though I wrote some of it.
	private static String preCanonicalize(String s) {
		// Actually, since we don't strictly care if our lexed version is actual legal DM, we can skip this step.
		return s;
		/*
		Matcher m = Pattern.compile("text \\s* \\( \\s* (\" .*? \") \\s* (.*?) \\s* \\)", Pattern.COMMENTS).matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			// TODO: this is real inefficient
			String result = m.group(1);
			String[] args = m.group(2).split(","); // this doesn't work at ALL
			for(String arg : args) {
				result = result.replaceFirst("\\[\\]", "[" + arg + "]");
			}
			m.appendReplacement(sb, result);
		}
		m.appendTail(sb);
		return sb.toString();
		*/
	}

	// Convert all interpolated strings to text("...", arg) form. This is easier than doing it in the lexer.
	// Ignore the possibility of ] without a corresponding [ except inside of "" strings - although it may be legal to say
	// something like var/x = '].txt', it's rare enough that I don't care.
	private static String removeInterpolation(StringReader r, boolean inString) {
		StringBuffer out = new StringBuffer();
		if(inString) {
			Vector<String> interpolations = new Vector<String>();
			try {
				boolean escaped = false;
				int c;
				while((c = r.read()) != -1) {
					if(escaped) {
						escaped = false;
						out.append("\\");
						out.append((char) c);
					} else if(c == '"') {
						break;
					} else if(c == '[') {
						// BYOND requires that all [ in a string must have a matching ], so this is safe. Behavior is
						// undefined with line continuations.
						String s = removeInterpolation(r, false);
						// Hack for when the text contains []
						if(!s.equals("")) {
							interpolations.add(s);
						}
						out.append("[]");
					} else if(c == '\\') {
						escaped = true;
					} else {
						out.append((char) c);
					}
				}
			} catch(IOException e) {
				System.out.println("Okay, this should never ever happen. Seriously.");
				System.out.println(e);
			}
			// If the last character is an unescaped backslash, I'm totally fine with silently ignoring it. Screw you,
			// guy who ends a file with an unescaped backslash.
			if(interpolations.size() == 0) {
				return "\"" + out + "\"";
			} else {
				return "text(\"" + out + "\", " + join(interpolations, ", ") + ")";
			}
		} else {
			try {
				boolean escaped = false;
				int c;
				while((c = r.read()) != -1) {
					if(escaped) {
						escaped = false;
						out.append("\\");
						out.append((char) c);
					} else if(c == '"') {
						// entering a string, remove interpolation from it
						out.append(removeInterpolation(r, true));
					} else if(c == ']') {
						// no top-level ] are allowed without a corresponding [, so this is safe. 
						return out.toString();
					} else if(c == '[') {
						out.append("[" + removeInterpolation(r, false) + "]");
					} else if(c == '\\') {
						escaped = true;
					} else {
						out.append((char) c);
					}
				}
			} catch(IOException e) {
				System.out.println("Okay, this should never ever happen. Seriously.");
				System.out.println(e);
			}
			return out.toString();
		}
	}

    private static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

	private static String read(String filename) {
		HashMap<String, String> defines = new HashMap<String, String>();
		defines.put("DM_VERSION", "300"); // TODO: Maybe make this smarter.
		return read(filename, defines, new HashSet<String>());
	}

	private static final Pattern TEXT_STRIP_PATTERN = Pattern.compile("\\{\".*?\"\\}", Pattern.DOTALL);

	private static String read(String filename, Map<String, String> defines, Set<String> included) {
		File f = new File(filename);
		try {
			String canonpath = f.getCanonicalPath();
			if(included.contains(canonpath) || canonpath.endsWith(".dmp") || canonpath.endsWith(".dmf")) {
				// Already included, or is a map or an interface file.
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
			// Replace all {"strings"} with the empty string.
			// Although this is generally called at least twice on any given line of code, it's safe to apply this
			// twice.
			return TEXT_STRIP_PATTERN.matcher(s.toString()).replaceAll("\"\"");
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return "";
		}
	}

	private static final Pattern DEFINE_PATTERN = Pattern.compile("\\#define \\s+ (\\w+) \\s* (.*?) \\s*(//.*)?$",
			Pattern.COMMENTS);
	private static final Pattern INCLUDE_PATTERN = Pattern.compile("\\#include \\s+ \"(.+)\"", Pattern.COMMENTS);
	private static final Pattern PREPROCESSOR_PATTERN = Pattern.compile("\\s*\\#", Pattern.COMMENTS);

	private static String preprocessLine(String line, String curdir, Map<String, String> defines,
			Set<String> included) {
		// See if it's a #define.
		Matcher m = DEFINE_PATTERN.matcher(line);
		if(m.matches()) {
			defines.put(m.group(1), m.group(2));
			return "";
		}
		// See if it's an #include.
		m = INCLUDE_PATTERN.matcher(line);
		if(m.matches()) {
			// Although this is not how C++'s #include works (it tries parent dirs and so on), it is how DM's works.
			return read(curdir + '/' + m.group(1), defines, included);
		}
		// Other preprocessor directives - ignore for now
		m = PREPROCESSOR_PATTERN.matcher(line);
		if(m.lookingAt()) {
			return "";
		}
		return doReplacement(line, defines);
	}

	private static String doReplacement(String line, Map<String, String> defines) {
		// This might not do what's expected in all situations, but it seems to work.
		// TODO: test if it actually seems to work.
		for(String key : defines.keySet()) {
			line = line.replace(key, defines.get(key));
		}
		return line;
	}

	// For debugging - probably a little inefficient, requires copying a several-meg string.
	public String getString() {
		return new String(data);
	}
}
