package edu.washington.cs.cse403d.coauthor.client.utils;

import java.util.Iterator;

/**
 * Utilities to perform operations on strings that I couldn't find in the
 * standard class library.
 * @author William Cauchois
 */

// TODO(wcauchois): is this the right place to put these?

public class StringUtils {
	/**
	 * Join a collection of strings into a single string, where the individual
	 * strings are separated by the specified delimiter.
	 * @param delimiter what to put in between the strings (like a comma).
	 * @param parts the set of strings to join
	 * @return a string consisting of the strings in parts separated by the
	 *         specified delimiter.
	 */
	public static String join(String delimiter, Iterable<String> parts) {
		StringBuffer buf = new StringBuffer();
		Iterator<String> iter = parts.iterator();
		while(iter.hasNext()) {
			buf.append(iter.next());
			if(iter.hasNext())
				buf.append(delimiter);
		}
		return buf.toString();
	}
	/**
	 * Truncate a string so that if it is over a certain length it will be
	 * "elided" by replacing the overflow with an ellipsis ("...").
	 * @param original the string to elide.
	 * @param maxLength the maximum length of the result.
	 * @return original if original.length() <= maxLength, otherwise the
	 *         original string will be elided.
	 */
	public static String elide(String original, int maxLength) {
		if(original.length() > maxLength) {
			String elided = original.substring(0, original.length() - 3);
			return elided + "...";
		} else
			return original;
	}
}
