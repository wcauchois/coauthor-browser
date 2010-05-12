package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;

public class DoubleClickListener extends MouseAdapter {	
	private List<String> queries;
	private JList list;
	private CoauthorDataServiceInterface CDSI;
	private String flag;
	
	public DoubleClickListener(JList list, CoauthorDataServiceInterface CDSI, String flag) {
		this.list = list;
		this.CDSI = CDSI;
		this.flag = flag;
		queries = new ArrayList<String>();
	}
	
	public void mouseClicked(MouseEvent evt) {
		if(evt.getClickCount() == 2) {
			int index = list.locationToIndex(evt.getPoint());
			list.setSelectedIndex(index);
			String selection = (String) list.getSelectedValue(); 
			queries.add(selection);
			if (flag == "author") 
				Services.getBrowser().go(new AuthorSearchResultsMain(queries));
			else
				Services.getBrowser().go(new ArticleSearchResults(selection));
		}
	}
}