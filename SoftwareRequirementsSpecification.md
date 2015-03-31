# SRS #

## Product Description ##
Our product is a tool for browsing and analyzing the links of co-authorship in academic paper citations. Aside from providing an interface wherein the user can explore the relationships between co-authors, our tool will be used to answer various questions about the underlying graph, such as: which papers have n specific authors collaborated on? How many co-authorship links does it take to get from author A to author B? As well as other queries.

### Target audience? ###
The main audience is expected to be the members of academic institutions as well as research organizations -- however, any person interested in browsing the academic authorship web should be able to use our software to satisfy their curiosity.

### What problem is it solving? ###
Traditionally the problem of correlating authorship in the published field             (outside of your immediate co-authors) has been conducted using ad-hoc methods. The current academic system relies heavily on connections made with co-authors across universities and disciplines as a measure of quality. Co-authorship links can provide insight into connections throughout the academic, research, and public systems. What is needed is a tool to analyze and explore these links through a user interface.

### What alternatives exist, and what are their strengths and weaknesses? ###
Google Scholar is a search engine for scholarly articles. Some of the use cases for our product could be fulfilled using Scholar -- for example, finding all articles written by a specific author. Advantages of Google Scholar include: its web-based, lightweight interface that can be accessed from anywhere; the large number of articles available for searching; and the ability to filter on fields. Google Scholar uses data from crawling the web and other 3rd party sources. Google Scholar cannot be used to answer complex questions about the co-authorship graph. For example, Google Scholar is unable to answer questions like "What is the Erdos number of author X?". Google Scholar is intended for search, not analysis of the co-authorship graph.
Academic Search Complete (EBSCO) is a commercial academic search engine. The class of commercial academic search engine contains many similar products, such as Web of Science and Academic OneFile. Fundamentally, they are both search engines for a set of academic articles from a list of journals and academic databases. EBSCO offers filtering on fields. You may restrict your search according to company, subject, geography, industry, and publication. Like Google Scholar, it cannot answer complex co-authorship graph questions.
Microsoft Academic is a suite of tools for searching academic articles and citations. Included in this tool is a powerful generic search engine that allows multiple author search as well as visualization. The Visual Explorer displays the graph of the 20st closest co-authors of a given author. The co-authors are placed relative to the number of co-authorships with the chosen author. This graph view is very similar to what we're looking to implement below. The system uses data from crawling the web for the Microsoft Bing search engine. Microsoft Academic provides some analysis of the graph but is unrelated to co-authorship. For example, this product offers multiple indices to measure how often an author is cited by his or her peers. Microsoft Academic does not answer questions of co-authorship relations (such as Erdos number, co-authorship lists, etc).

### How will your system be different, from the user's point of view? ###
The product will provide a filter by which users can use to choose from the given list of authors. By providing both author view and article view, the search will be made easier, because users with only one of the two pieces of info will be able to find out the article or author(s) of their interest.
Using a graph view feature, the product will deliver a very intuitive representation of the co-authorship network. Each node in the graph will contain the author's name, and connected to each node are the co-authors.
Another handy feature is that the user can upload a text file containing list of authors they are interested in, and the product will generate the co-authorship network with the given name.
Many features are designed for user's convenience; features such as simple search, intuitive representation of data, and article view are all designed for easy access and use by non-technical users.


### Major Features ###
  * [Core](Core.md) Search for a group of authors and determine which articles all or any of these authors have worked on together.
  * [Core](Core.md) Search for relations between two authors so that the user can determine the "distance" between them -- that is, how many links of co-authorship must be followed to get from one author to the other. This type of query may be used more simply to determine whether the two authors have worked together.
  * [Core](Core.md) List all collaborators for a single author.
  * View the data-set on an article basis, including features like search by article title.
  * View the data-set in a visual graph view, which displays the links of co-authorship as edges between nodes. (Button will generate this.) Part of this feature includes facilities for panning, zooming, and restricting the graph view so that it can be navigated in an intuitive manner.
Note: Features marked [Core](Core.md) have been assessed to be integral to the usefulness of the program. Development will focus on implementing core features before all others.

### Minor Features ###
  * Upload a text file containing a newline-delimited list of authors which can be applied as a filter to restrict the data-set to include only those authors.
  * Hyperlinks between different views -- clicking on an author name in "article view" should take the user to "author view", and visa versa.
  * Help in the form of an HTML manual.

### What external documentation will you provide that will enable users to understand and use your product? ###
Help will be provided in the form of an HTML manual. The desktop application will have a "Help" menu with a menu item that should open the manual in the user's web browser of choice. In addition, pressing F1 should open the manual (in accordance with user's expectations).
The manual will have several sections, including: a brief overview of the product's features; a FAQ; and a detailed description of the UI outlining several use cases.

### What will the UI look like?`*` ###

**UI Mock 1**

<a href='http://www.flickr.com/photos/49666724@N08/4548191293/' title='UI Mock 1 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4068/4548191293_6d5b22c258.jpg' alt='UI Mock 1' width='500' height='386' /></a>

**UI Mock 2**

<a href='http://www.flickr.com/photos/49666724@N08/4548191313/' title='UI Mock 2 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4070/4548191313_dea08655cb.jpg' alt='UI Mock 2' width='500' height='386' /></a>

**UI Mock 3**

<a href='http://www.flickr.com/photos/49666724@N08/4548203099/' title='UI Mocks 3 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4065/4548203099_4589d6a3f3.jpg' alt='UI Mocks 3' width='500' height='384' /></a>

`*`See end of documentation for additional UI drawings.

### Use Cases ###
  * **Author Searches**:
  * **Preconditions**: The user is in the author search view.
  * **Use case scenario**: The user types in the name of one author into the filter bar text field; as the user is typing, the list of names below the filter bar is changing to find the name closest to what the user has typed in the filter bar. When the user is finishes typing, the list of authors below the filter bar will either display the exact author match the user was looking for or "No authors found." The user then clicks on the check box next to the author's name and starts a new author filter search. The process is repeated for however many authors the user wants to obtain information for. Once all desired authors have been selected, the user clicks on the "Search Co-authorships" button and the right pane of the GUI will display the appropriate results. There are three possible cases that will be handled based on how many authors the user has selected:
  * Three or more authors selected:
  * Results pane will display a tabbed table of a list of the articles all the authors have collaborated on together and a list of all the articles at least one of the authors has worked on.
  * Two authors selected:
  * Results pane will display:
  * The shortest distance between the two authors.
  * A window with a tabbed table of the chain of authors linking the searched-for authors together, and a table of the articles linking the two authors.
  * Tabbed table of a list of the articles all the authors have collaborated on together and a list of all the articles at least one of the authors has worked on.
  * One author selected:
  * Results pane will display all the articles the author has worked on and a list of authors this author has collaborated with.
  * If no articles or links are found between the authors, the results pane will display "No co-authorship links found."

  * **Article Search**:
  * **Precondition**: The user is in the article search view.
  * **Use case scenario**: The user types in the name of an article into the search field and clicks on "Search Articles." A list of articles is displayed in the results panel.
  * **Co-authorship graph visualization**:
  * **Precondition**: The user has selected at least one author from the author list panel.
  * **Use case scenario 1**: The user selects "Graph view" from the "View" menu. The program enters Graph View mode with the nodes corresponding to the selected authors highlighted.
  * **Use case scenario 2**: The user right clicks on an author in Author View to open up a context menu, then selects "View in graph". The program enters Graph View mode with the node corresponding to the selected author highlighted.
  * Along with this, we also have many smaller side features.

  * **Text-based Author Filter**
  * **Precondition**: The user is in any of the application views.
  * Use case description: The user selects "Import an author list..." from the Tools menu. A file open dialog pops up and the user selects a newline-delimited text file containing author names.
  * **Postcondition**: Only those authors contained in the text file are displayed in Author View; only those articles co-authored by at least one author from the text file are displayed in Article View.
  * Hyperlinks:
  * **Precondition**: The user has performed a successful search where the results pane displays a list of authors/articles in its fields.
  * **Use case scenario 1**: The user double clicks on an author name in any of the tables of the results panel. The application performs a search on the name of the author and updates the results panel with the results for that author.
  * **Use case scenario 2**: The user double clicks on the name of an article in any of the tables of the results panel. The application performs a search on the name of the article and updates the results panel with the results for that article.

  * **Help Feature**
  * **Precondition**: The user is running the Co-Authorship browser application.
  * **Use case scenario 1**: The user pushes the F1 key on the keyboard, and the html manual opens up in a new windows
  * **Use case scenario 2**:The user clicks on the "Help" dropdown menu at the top of the window and then clicks on "User Manual." The html manual opens up in a new window.

### What programming languages, data sources, version control, bug tracking, and other tools will you use? ###
We plan on using Java to develop a desktop application using the Abstract Windowing Toolkit and Swing. Neo4j is an open-source graph database that we will use to store the underlying data. Initially prototyping has shown that neo4j can be used to efficiently solve path finding operations using typical algorithms. We plan to populate our citation database by scraping existing online databases and other manual means (such as plain text citations in one of the supported citation formats). The supported formats for importing citations will be dependant on the majority of the available sources to connect with. The JUnit framework will be used for unit testing throughout the course of development in order to ensure quality. We will also use Mercurial, a DVCS, and Google Code. Google Code provides features like an issue tracker, hosted downloads, and a wiki (in addition to centralized repository hosting), available on the web from http://code.google.com/p/coauthor-browser. For collaboration, we'll be using Google Wave, e-mail, and instant messaging.


---


## Group Dynamics ##

### Who will be your project manager? ###
Miles Sackler

### What will be the other members' roles? ###
  * Jeff Prouty (Backend Lead) - Data storage/retrieval/service and implementing the information with the product.
  * Sergey Alekhnovich (Test Lead)- Test organization and implementation.
  * Kevin Bang (Design Lead) - UI design and implementation.
  * Bill Cauchois (Integrator Lead) - Works on integrating the backend and the frontend, with a focus on UI development in association with Kevin.
  * Miles Sackler (Project Lead) - Coordination between team members and additional development and testing.

### Will everyone share in the development, or will you have designated designers, testers, etc.? ###
Each member of the team will have their own specific roles, but that will not restrict their participation. Everyone will have their hand in each aspect of the project, especially in development and testing.

### Why have you chosen these roles? ###
These roles were chosen in accordance with the individual team members' preferences. A consensus was reached regarding each team members' roles.

### Will the roles differ for different parts of the project? ###
The work of each member will be centralized around their specific position resulting in the majority of their time being spent on that area of the project. However, throughout the project every member will be expected to spend time working on areas outside of their title. This is especially true in early development and in general testing. Every member will have a part in the implementation and design of major components of the project and will also be expected to help run tests on their own work as well as on each others' work. Should a disagreement arise between two members, it will be brought to the group as a whole to decide how to resolve the conflict.

### Schedule / Timeline ###

| **Week**	| **Jeff** | **Sergey** | **Kevin** | **Bill** | **Miles** |
|:---------|:---------|:-----------|:----------|:---------|:----------|
|3 |	Research where the data will come from and how to acquire it on mass scale.	| Begin organizing which features should be done first and how they will interact with each other. | Meet with Bill to discuss the UI design and how you will want to develop it. |	Work with Kevin on the UI design and on how it will be created.	| Continue working on the schedule so that everything fits and nothing is rushed. |
| 4 (end of design phase) |	Design and begin implementation on data storage and access. | Once organization is done, begin working on the development of the features.	| Begin work on creating the actual UI. | Learn how to and begin connecting the UI to the features Sergey has been building. | Work with Sergey on feature development and with Bill to integrate it with the UI. |
| 5 |	Finish the structure our data storage and talk with Sergey on how to access it; Insert in a small  testable amount of data. |	Test the completed features on small scale and work with Bill on integrating them with the UI. | 	Finish the beta version of the UI and run tests on its functions. | 	Connect the working features with the UI and continue development of UI features (graph, etc.) | Ensure that integration is working well and assist where needed.    Work on either feature or UI development. |
| 6 |	Test that the data already acquired is efficiently accessible and begin work on mass data acquisition. | 	Continue building and testing features as needed and in order decided upon. Talk with Jeff on making them work with larger amounts of data. | 	Talk with Bill and expand the UI to include the new features he has built. | 	Finish building the UI views/features.  Work on connecting each part of the UI. | 	Work on development of product or UI features and begin work on product testing. |
| 7 |	Complete mass data acquisition and test its accessibility and accuracy. | 	Complete the basic implementation of all features and begin going back and testing for bugs, which will be reported. | Add the final views/features which Bill completed the previous week. | Test each view/feature in its isolated form, making adjustments as needed. | Assist in the completion of product and UI features. Work on testing each feature. |
| 8 |	Once testing is completed, integrate the data into the main product features. | Work with Jeff to allow each feature access to the full data library. | Finish the next version of the UI and go through each part, testing the features and reporting bugs. | 	Integrate the completed product features with the UI system. | Begin initial testing of the product as a whole and report any bugs discovered. |
| 9 |	Complete any final pieces of your individual assignment.   Then, begin work on testing the entire product and reporting any bugs found. |	Complete any final pieces of your individual assignment.   Then, begin work on testing the entire product and reporting any bugs found. |	Complete any final pieces of your individual assignment.   Then, begin work on testing the entire product and reporting any bugs found. | Complete any final pieces of your individual assignment.   Then, begin work on testing the entire product and reporting any bugs found. | Complete any final pieces of your individual assignment.   Then, begin work on testing the entire product and reporting any bugs found. |
| 10 | Resolve any bugs discovered and complete testing of all features and aspects. | Resolve any bugs discovered and complete testing of all features and aspects. | Resolve any bugs discovered and complete testing of all features and aspects. | Resolve any bugs discovered and complete testing of all features and aspects. | Resolve any bugs discovered and complete testing of all features and aspects. |



### Project Tasks ###
  * Group author search.
  * Relations between authors.
  * Whether two authors have worked together.
  * The distance between two authors.
  * Article Search.
  * Co-authorship network visualization
  * Data Storage/backend.
  * GUI.

### Who will work on the design, and how much time do you expect it will take? ###
The designing process has been mainly a group activity up to this point, but in the near future it will begin to split up into pieces related to each members role in the project. This split will have:
  * Jeff will design the data acquisition/storage and how our product will access the data.
  * Sergey will begin work on the most pressing features and will work his way through the features splitting his time between development and test.
  * Kevin will work on the UI construction and implementation, working with Bill on what we want it to look like and how to do it.
  * Bill will work along with Kevin on UI design and construction and will work on integrating it with the features of the product.
  * Miles will work on integration, helping the work from each group mesh, time organization, and with Sergey on feature development and testing.
We expect these different design aspects to have different time lines, but plan on completing the last parts of design within the end of the 4th week. By this time, we expect to have the design phase completed for some areas and to be in the development process.

### Which features are “beta” features? ###
The following features are the beta features of our product:
  * Graph Visualization
  * Article Search
  * Author Name Text File Upload
  * HTML User Manual
  * Hyperlinks Between Views
None of these are integral to the performance of the application but all provide an extra functionality or convenience for the user.


---


## Risk Summary ##

### What are the major risks (at least three of them) regarding completing your project? ###
There are a few major risks which we will have to keep a close eye on avoiding to ensure the success of our project. One of these risks is that a member of our group will get bogged down in a certain part of their assignment. Should this happen, it could prove disastrous to the project as a whole because there are so many places where one person relies on the completion of another member's work. To prevent this, we will work to keep everyone in the group well informed on the progress of everyone else, and should one person feel that they are struggling or wont meet a deadline, then someone else who has time in that week will step forward and assist where needed.

Another risk which we will be sure to not allow time to slip by is in the acquisition of our data. One of the earlier assignments within our schedule is to research where the data will come from and, once discovered, how to retrieve that data. This is a very important feature, because without this data our product will do nothing. Therefore we must ensure early on that we will be able to have the data we need and in the right amount.

A third risk which we will need to look out for is the potential for a lack of capability in the technology we have chosen to use. Should we find early on that one of the tools we have selected is missing a potentially useful feature, then we will have to make a quick decision about how badly we need that feature or if we should just switch tools. If this decision was put off or ignored, it could lead to major issues later on in the project.
What are you most worried about, and why are these the most serious risks?
Graph View Implementation could be very time consuming. This feature is hard to be implemented until most of the other features are completed; most of the major features must be completed in order to get the graph viewing working. In order to reduce the amount of time required to complete the feature, we have chosen an existing toolset, neo4j, to implement the feature. neo4j provides an intuitive graphical representation of the database, which is very similar with what we have in mind for graph view feature. Using neo4j will save a lot of time.
Testing of each feature will be first done individually by each group member, and later as a whole. Other group members will participate in testing also in order to further reduce the risk of disintegration of pieces. Should the time run out, we will cut some extra features including user uploaded name-list.
Feedback will be needed when each of the major feature is completed. If the feature passes our "extensive" test cases, we will have a meeting with the faculty for a demonstration of the feature, receive necessary feedbacks, and implement it.


### What are you most worried about, and why are these the most serious risks? ###
Graph View Implementation could be very time consuming. This feature is hard to be implemented until most of the other features are completed; most of the major features must be completed in order to get the graph viewing working. In order to reduce the amount of time required to complete the feature, we have chosen an existing toolset, neo4j, to implement the feature. neo4j provides an intuitive graphical representation of the database, which is very similar with what we have in mind for graph view feature. Using neo4j will save lots of time.
Testing of each feature will be first done individually by each group member, and later as a whole. Other group members will participate in testing also in order to further reduce the risk of disintegration of pieces. Should the time run out, we will cut some extra features including user uploaded name-list.
Feedback will be needed when each of the major feature is completed. If the feature passes our "extensive" test cases, we will have a meeting with the faculty for a demonstration of the feature, receive necessary feedbacks, and implement it.


---


## Additional UI Mocks (Created by Kevin) ##

**Startup Screen**

<a href='http://www.flickr.com/photos/49666724@N08/4548961798/' title='kevin_ui1 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4029/4548961798_ea04925888.jpg' alt='kevin_ui1' width='364' height='500' /></a>


**Author View**

<a href='http://www.flickr.com/photos/49666724@N08/4548326633/' title='kevin_ui2 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4049/4548326633_1fa174ee4f.jpg' alt='kevin_ui2' width='364' height='500' /></a>


**Author View(Continued)**

<a href='http://www.flickr.com/photos/49666724@N08/4548961826/' title='kevin_ui3 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4042/4548961826_d4b4274f0d.jpg' alt='kevin_ui3' width='364' height='500' /></a>


**Article View**

<a href='http://www.flickr.com/photos/49666724@N08/4548326679/' title='kevin_ui4 by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4008/4548326679_df7c268aa7.jpg' alt='kevin_ui4' width='364' height='500' /></a>