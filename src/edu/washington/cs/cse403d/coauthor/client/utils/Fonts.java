package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.Font;

import javax.swing.JLabel;

/**
 * Provides static methods to construct several different types of
 * application-wide fonts, centralized so that the fonts are consistent across
 * our application.
 * 
 * @author William Cauchois
 */
public class Fonts {
	/**
	 * This is just the font used for JLabels. 
	 */
	public static Font getDialogFont() {
		return new JLabel().getFont();
	}
	/**
	 * A large, bold font suitable for headers of pages.
	 */
	public static Font getHeaderFont() {
		Font baseFont = getDialogFont();
		return baseFont.deriveFont(baseFont.getSize2D() + 10.0f);
	}
}
