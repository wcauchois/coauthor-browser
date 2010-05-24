package edu.washington.cs.cse403d.coauthor.client.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.washington.cs.cse403d.coauthor.client.Services;

/**
 * A text field, attached to a JList, that will filter the JList according to
 * whatever the user enters in the text field. This widget also incorporates a
 * cancel button which will clear the contents of the text field and reset the
 * filter.
 * @author William Cauchois
 */

// TODO(wcauchois): replace FilterPanel with this?

public class FilterField extends JTextField {
	private static final long serialVersionUID = 3250394717406844585L;
	
	private JList theList;
	private Font italicFont, defaultFont;
	private boolean isPristine;
	private ImageIcon cancelFilterIcon;
	private List<Object> unfilteredData;
	private void clearText() {
		setFont(defaultFont);
		setForeground(Color.BLACK);
		setText("");
		updateResults("");
		isPristine = false;
	}
	private void setTextToFilter() {
		setFont(italicFont);
		setForeground(Color.GRAY);
		setText("Filter");
		updateResults("");
		isPristine = true;
	}
	/**
	 * Construct a FilterField attached to the specified list.
	 * @param list the list whose contents will be filtered.
	 */
	public FilterField(JList list) {
		theList = list;
		ListModel model = theList.getModel();
		unfilteredData = new ArrayList<Object>(model.getSize());
		for(int i = 0; i < model.getSize(); i++)
			unfilteredData.add(model.getElementAt(i));
		
		cancelFilterIcon = Services.getResourceManager().loadImageIcon("CancelFilter.gif");
		defaultFont = getFont();
		italicFont = defaultFont.deriveFont(Font.ITALIC);
		setTextToFilter();
		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				if(isPristine)
					clearText();
			}
			public void focusLost(FocusEvent evt) {
				if(getText().equals(""))
					setTextToFilter();
			}
		});
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent evt) {
				if(!isPristine) updateResults(getText());
			}
			@Override
			public void insertUpdate(DocumentEvent evt) {
				if(!isPristine) updateResults(getText());
			}
			@Override
			public void changedUpdate(DocumentEvent evt) {
				if(!isPristine) updateResults(getText());
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if(getCancelFilterRect().contains(evt.getPoint()))
					clearText();
			}
		});
		final Cursor defaultCursor = getCursor();
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent evt) {
				if(getCancelFilterRect().contains(evt.getPoint()))
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else
					setCursor(defaultCursor);
			}
		});
	}
	private Rectangle getCancelFilterRect() {
		int width = getWidth(), height = getHeight();
		return new Rectangle(width - height, 2, height - 4, height - 4);
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle rect = getCancelFilterRect();
		g.drawImage(cancelFilterIcon.getImage(),
				rect.x, rect.y, rect.width, rect.height, null);
	}
	private void updateResults(String filter) {
		if(filter.length() == 0) {
			theList.setListData(unfilteredData.toArray());
			return;
		}
		filter = filter.toLowerCase();
		List<Object> filteredData = new ArrayList<Object>();
		for(Object o : unfilteredData) {
			if(o.toString().toLowerCase().contains(filter))
				filteredData.add(o);
		}
		theList.setListData(filteredData.toArray());
	}
	public List<Object> getUnfilteredData() {
		return unfilteredData;
	}
	public JList getList() {
		return theList;
	}
}
