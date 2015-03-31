# Architecture #

## System Architecture ##

Our product uses a client/server model. The client runs on the end users machine and connects to a remote data service. We have chosen to develop the client as a desktop application programmed in Java. We chose to use Java because every member on the team has experience with Java. The other alternative was to develop a web application, but we decided that was infeasible since our team does not have enough experience with that set of technologies.

The client is the user's main point of interaction with our system. It is implemented in Java using the Swing user interface toolkit. The UI architecture is largely based around a "browser" paradigm, wherein the user navigates between a set of "pages" -- when the user navigates to a new page (i.e. by clicking a button), she may return to the previous page by clicking the Back button. A Forward button is also available. This parallels the UI for web browsers, which most users will be familiar with. In addition, a "bread crumb bar" provides a list of "bread crumbs" that indicates how the user got to the current page -- each bread crumb may be activated to navigate back to that page. For example, if the user is at the "Search Results" page, the bread crumb bar will display a link to the "Search" page.

This functionality is implemented through a set of classes. `BrowserPage` is an abstract class that encapsulates a "page" in the system -- other pages will derive from this. `BrowserHistory` stores a list of `BrowserPage`s that represents the user's browsing history. `BrowserFrame` provides a user interface (including Back/Forward buttons and the crumb bar) for navigating between pages. `CrumbBar` implements the crumb bar. We derive `MainFrame` from `BrowserFrame` to add a menu to the frame. We also have several pages (descendants of `BrowserPage`) including `StartPage`, `SearchPage`, and `SearchResultsPage`.

This organization is illustrated in the UML diagram below.

<a href='http://www.flickr.com/photos/49666724@N08/4594114365/' title='ui_uml by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4051/4594114365_801c22a4f1.jpg' alt='ui_uml' width='500' height='344' /></a>

## Graph Visualization ##

The visualization component of our application will provide the user with an interactive visual representation of the co-authorship relationships between authors. We are implementing two main visualization features: (co-author visualization) visualizing all co-authors of a single author in a graph and (chain visualization) visualizing a chain of co-authors between two authors. Both the chain and co-author visualization are started when the user presses on the "Visualize" button on the GUI after performing the corresponding chain or co-author search. For both visualization features, the graph will initially display only nodes and links for all the authors that were returned in the GUI search. At any point during execution of either of the aforementioned visualizations, the user can click on any author node in the displayed graph and the graph will be populated with all the co-authors of that author node.

Visualization is implemented with the Prefuse Visualization Toolkit. Prefuse is a well-developed library for creating rich data visualizations in Java. The process of creating an interactive visualization for data with prefuse involves four main steps: (1) loading the node and edge data into prefuse's data tables, (2) generating a Graph object data structure from the nodes and edges data tables, (3) setting up a Visualization class with various renderes and color parameters for the graph nodes, and (4) creating a Display object for the visualization that will set up the Event Listeners for the Visualization. The Display object can then be embedded inside of a Java swing app.

Co-Author visualization will be implemented in the VisualCoAuthorExplorer class, and chain visualization will be implemented in the VisualChainExplorer. Both of these classes are currently undergoing major development; the wiki version of this document will be updated with the final design at the end of this week.

## Data and Database ##

Currently the client can search over only data from the [DBLP](http://www.informatik.uni-trier.de/~ley/db/) project. This is due to legal constraints of acquiring the data. This limits our product to the computer sciences citation realm. However, our data store is designed such that adding new data (from dblp or other sources) is trivial. Therefore, this is only a temporary issue. For more details, see Risk 3 below.

The DBLP data is provided to US via an XML file containing entries for all of the authors and articles in the DBLP database. DBLP also provides an XML parser, which we use to read the bibliographic data into our databases. Once the data is loaded into our databases, it is completely static and must be updated manually. For the scope of this class, the data provided by DBLP is sufficient enough to demonstrate our product.

The client application can query the data via a Java RMI binding to a coauthor data service (UML below). This service is publicly available to all clients. The data is stored in two databases: a relational database and a graph database (specifically [MSSQL](http://www.microsoft.com/sqlserver/2008/en/us/default.aspx) and [neo4j](http://neo4j.org/) respectively). The relational database is used for the majority of the queries. The graph database is used only for computing the coauthorship chains on the fly. After extensive research into the solution space of solving shortest path data and given the budget of our solution, we have chosen this hybrid solution. It allows us to continue using a large corpus of data without hitting scalability issues at the price of higher latency for on demand shortest path lookups. The shortest path computations are done over the neo4j graph database. This database has a node for each author and all coauthorship links. The graph then performs a doubly sided breadth first search.

The back end is queryable via the following interface:

<a href='http://www.flickr.com/photos/49666724@N08/4614133935/' title='dataservice_uml by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4047/4614133935_3d0d166f4f.jpg' alt='dataservice_uml' width='500' height='306' /></a>

The schema for the relational database is organized as shown by the ER diagram:

<a href='http://www.flickr.com/photos/49666724@N08/4614751622/' title='dataservice_schema_er by Co-Author Documentation, on Flickr'><img src='http://farm5.static.flickr.com/4005/4614751622_62c56abdc5.jpg' alt='dataservice_schema_er' width='500' height='294' /></a>

Alternatives to our hybrid solution that we tried include:
  * Use only a graph database: neo4j
| **Pros** | **Cons** |
|:---------|:---------|
| Medium lookup performance for shortest path search | Slow index lookup performance |
| Dynamic shortest path searches (no pre computations) | Simple relational queries are more complex |

  * Use only a relational database and precompute all shortest paths
| **Pros** | **Cons** |
|:---------|:---------|
| Low latency for all queries | Space efficiency is awful for shortest path search (exponential with a base of 20) |
| Traditional relational database is more familiar for other developers (future extensibility) | Computing the shortest paths takes days on our current machine |

  * Use only a relational database and compute shortest paths dynamically
| **Pros** | **Cons** |
|:---------|:---------|
| Low latency for simple queries | Very poor performance for shortest paths |
|  | Computing shortest path(s) requires complex T-SQL |

  * Use a relational database, MSSQL, for simple queries and utilize a graph database, neo4j, for shortest path queries. (This is the solution we chose)
| **Pros** | **Cons** |
|:---------|:---------|
| High performance for simple queries | Two databases to keep in sync |
| Medium lookup performance for shortest path search | More resources required for hosting two databases |
| Dynamic shortest path searches (no pre computations) |  |

## Coding Style Guidelines ##

Java coding will follow standard Sun Java conventions as described at http://java.sun.com/docs/codeconv/.

# Process #

## Risk Assessment ##

**Risk 1**: Data look-up latency is too high.
  * Likelihood: Medium
  * Impact: High
  * Assessment Evidence: Current neo4j database prototype yields high latency and high resource usage on attu2.
  * To Reduce Risk: the change of occurrence (and why it's only medium above) is that an alternative data storage solution is being investigated. A traditional relational database will yield higher performance for standard table queries.
  * Detecting the Problem: time the RPCs to the data provider.
  * Mitigation plan: Provide reasonable UI notification to the user while loading data from the data provider.

**Risk 2**: realization of a new major feature which is wanted late in development (or a change of requirements for existing feature)
  * Likelihood: Medium
  * Impact: High
  * Assessment Evidence: With the limited amount of provided requirements for the product it seems possible that a new feature will occur to us later in development. Already during our testing of our features we have thought of new ways to use the data and provide it to the user
  * To Reduce Risk: By completing each feature one at a time and testing each one upon completion, we hope to think of all wanted uses for the data earlier in development. We also have worked hard to think up all features we may want and had a meeting with our customer to discuss what they wanted.
  * Detecting the Problem: If we do think of a new feature, we will have to decide quickly whether it is feasible to complete in the time remaining and whether we feel it would make our product stronger in the market.
  * Mitigation plan: We have worked hard to think up all possible features and will be on the lookout for any others we may have missed.

**Risk 3**: Acquiring New Data Sources and Scaling Storage Solution
  * Likelihood: High
  * Impact: Medium
  * Assessment Evidence: Our current service is only using data from one source: DBLP. Currently, all other sources have no legal way to obtain large quantities of data. Therefore, to expand our service to other fields, new data sources must be acquired (potential partnerships or contracts).
  * To Reduce Risk: Right now our product will ship with only this one data source. However, our back end data store can be easily extended to handle new sources if/when they should be available.
  * Detecting the Problem: Get a response from our customers and see what data they are requesting.
  * Mitigation plan: We will work as quickly as possible to respond to changes in our customers data consumption needs.

**Risk 4**: Limitation in database back-end prevents feature
  * Likelihood: Low
  * Impact: High
  * Assessment Evidence: We are already seeing potential draw backs to our current database back-end (slow search response), so its seems possible that we will find other issues with our set up as it stands today.
  * To Reduce Risk: We are constantly looking at our current method of how to interact with the database and trying to find ways of improving this.
  * Detecting the Problem: actual use of our product and thus of the database's features. With this in mind, we are running a multitude of different tests to keep track of how well it is working.
  * Mitigation plan: We will determine whether it is a problem easily adjustable with our current set up, but if not we will have to decide if we have the time and if it is important enough of an issue to re-built our database back-end.

**Risk 5**: Cannot deliver author graph visualization in time
  * Likelihood: Medium
  * Impact: Low
  * Assessment Evidence: Given the potential complexity of having our product generate a pictorial graph of how the authors connect to one another we feel that it is possible for us to have to struggle to get it to work well.
  * To Reduce Risk: We plan to do extensive research on creating a visual representation of our data and to try to use our team members' experience with graphics.
  * Detecting the Problem: If we feel that we having a working model, we will run extensive tests on its ability to generate a graph of varying size to try and take into account all possible cases. Should one of these cases reveal an error, we will then focus our work on that particular instance.
  * Mitigation plan: We will work on that particular case to try and fix any problems we have discovered, but should it prove too extensive of an error to deal with, we will drop the visual representation feature from our product.

> ## Risk Changes Since SRS ##
Our risks have not changed a great deal from the SRS publication. The changes which are present are not of a large nature:
  * we are not worried about speed in the data lookup instead of data acquisition
  * we worked from the onset to try and think of all major features we may want to have to avoid discovering one late in development
  * now that we have created a database and begun accessing it with our product, we have the new risk of the data changing format which didn't exist in the SRS.
  * Just as in the SRS, we have always had the risk of one of our tools not having the features we need of it, and we will have to continue keeping an eye on how we plan to use our tools so that we don't discover later on that we made a mistake in choosing one over another.
  * The author graph visualization has always been a risk and we understand as soon as we thought about including it as a feature that it holds the potential to be a major problem in development. We will be careful to not let ourselves loose too much time on that feature.


# Project Schedule #


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

When looking at the schedule, our work can be broken up into three areas: data, UI, and features.  These three areas encompass all of the work being done to create our desktop application and make it fully functional.  Our current standing (at the beginning of the eighth week of development) is very promising and shows us to be either on track or ahead of schedule.

> Data: We have finished gathering over one million authors, stored them in our database, and developed a working system to access the data in an efficient and useful way.

> UI: We have designed and developed our entire UI, with all of the originally designed areas fully functional.  We have also begun integrating each area with the features, and have full functionality on 85% of the features.

> Features: All major features have been developed and shown to be working accurately and efficiently.  Only a few small pieces left to be connect with the UI.

As it can be seen from this description, we have reached a point where we have an almost completely working product, with only a few small aspects left to edit.  With the remaining time, we plan on running extensive user tests and to also complete some extra features we have found time to add.


# Team structure #

## Team Member Roles and Responsibilities ##
  * Jeff Prouty (back-end Lead) - Data storage/retrieval/service and implementing the information with the product.
  * Sergey Alekhnovich  (Visualization/Test Lead)- Graph feature development and test organization and implementation.
  * Kevin Bang (Design Lead) – Work with Bill on UI design and implementation.
  * Bill Cauchois (UI Lead) – Work with Kevin on UI development and implementation of features.
  * Miles Sackler (Project Lead) - Coordination between team members and additional development and testing.
(For more specific description of each member's assignments, see schedule at end of document.)

## Team Communications ##
The bulk of our team communications will be conducted through email, phone, and Google Wave. Our weekly meetings will be held in CSE 002 every Monday at 3:30, and as the workload for the project increase, we will add another meeting time to either Thursday or Friday. Additionally, we will use Google Code's wiki feature to maintain our documentation so that everyone on the team is up to date on the status of the project.

## Test Plan Outline ##
In order to ensure the quality of our final product and streamline development, we will follow a rigorous and thorough test plan. Unit tests will be written for all newly-developed functions and classes. This will ensure that we can effectively maintain working code catch bugs as early as possible. System-level test cases will include a series JUnit test classes that verify the functionality of the back-end clients as well as an automated GUI testing framework that ensures that the GUI dialog windows are functioning properly. A group of test subjects will be assembled and tasked with running through a set of pre-written user scenarios.

## Unit Tests ##

### Strategy ###
Unit tests will be written for each new working feature added to the code base.

### Test Coverage/Purpose ###
The purpose of the unit tests is to provide a way of verifying existing method-level functionality, as well as catch any new compatibility issues that may arise in the face of new code commits.
### Test Development Plan ###
Sergey will be responsible for writing up the bulk of the unit tests for the application, with the other members of the team contributing where appropriate.
### Test Frequency ###
The entire unit test suite will be run every time there is a major code commit. This will ensure that bugs related to new commits are caught, reported and dealt with as early as possible. Also, once we have reached a fully functioning prototype (beta version) we will be running tests on the product as a whole.

## System Tests ##
### Strategy ###
System-level test cases will include a series JUnit test classes that verify the functionality of the back-end clients. Additionally, the UI will be tested using a set of manual test cases that will run the application through all likely use scenarios.
### Test Coverage/Purpose ###
System level tests will ensure that the application is functioning correctly when all of the separate code modules are connected.
### Test Development Plan ###
As with unit testing, the bulk of test development will be done by Sergey, with other members of the team helping out when needed. JUnit will be used to test the functionality of the back-end and any other non-UI related functionality.
### Test Frequency ###
System-level tests will be run much more frequently towards the end of the development process when the separate modules of the application are linked up. In the early stages of the development cycle, the number of system-level tests written/conducted will be minimal.

## Usability Tests ##

### Strategy ###
We plan to accomplish usability testing in a few phases, related to our progress in developing the product. "Alpha" testing will test the alpha version of our product. "Beta" testing will test the beta version of our product. We will then proceed to test a more mature version of our product with an emphasis on usability. These phases are described in more detail below.
### Test Phases ###
1. Alpha Testing
The "alpha" version of our product may not include functions associated with back-end capabilities (i.e. returning search results). The purpose of this test is to ensure that all major functions of the UI are working. It will cover the browser navigation, system messages, help messages, breadcrumb bar functionality, and back/forward functionality.
2. Beta Testing
"Beta" testing will commence when the UI has been successfully connected to the back-end, to provide a user experience very similar to the final version. Each function will be tested to ensure that it retrieves and displays the data correctly. Functions to be tested include: result display, searching (including filtering options), and navigation within the results screen.
3. Usability Testing
Where beta and alpha testing have focused on technical aspects of the user interface -- making sure it all works correctly -- this phase of testing, involving a more mature version of the product, focuses on assessing the usability of the product. That is, with this phase we seek to answer the question: is our product easy to use?
  * How long does it take for users to complete a basic search?
  * How long does it take for users to learn the navigation methods of the application? (Notably the back/forward buttons and the breadcrumb bar.)
  * How satisfied are users with the search results?
  * Are the results returned at a satisfactory level of detail?
  * Are the results easy to understand? (Can the users easily find what they need from the results?)

### Use Case UML Sequence Diagrams ###

**Author Search**

<a href='http://www.flickr.com/photos/49666724@N08/4594114277/' title='Author Search UML by Co-Author Documentation, on Flickr'><img src='http://farm4.static.flickr.com/3333/4594114277_1c61b0e69e.jpg' alt='Author Search UML' width='500' height='455' /></a>



**Article Search**

<a href='http://www.flickr.com/photos/49666724@N08/4594729122/' title='Article_Author_Search by Co-Author Documentation, on Flickr'><img src='http://farm2.static.flickr.com/1047/4594729122_fb9ef1ebca.jpg' alt='Article_Author_Search' width='500' height='274' /></a>


## Test Development Plan ##
Alpha testing will be mostly internal -- to be carried out by the UI developers, Bill and Kevin. Beta testing may involve other team members.
Since we do not anticipate having an overly complex user interface, we may not need automatic testing. Manual (but thorough and principled) testing of the functions will suffice. However, where UI elements are backed by a data structure (for example, the user's "browsing history" is stored in a circular array), JUnit tests will be developed to ensure correct operation of that data structure.
We will try to bring in other people for the final usability test. Here it is advantageous to have as many people test the UI as possible, to gauge the usability more accurately at scale. Since our intended audience is academic, we have considered asking grad students and professors that we know whether they would consent to use our product and fill out a survey.

## Test Frequency ##
Testing will, of course, be performed in an informal capacity throughout the course of development. However, we have 3 well-defined testing phases that should be spread out evenly -- if the project remains on track.

## Bug Tracking Mechanism ##
Google Code has a built-in issue tracker that we will be using throughout the course of development. Issues should be documented as soon as they are found, unless a developer is in the process of fixing it.

The following information should be included in a bug report:
  * Type of error encountered (e.g. exception, unexpected behavior, corrupted state).
  * Steps to reproduce the glitch (be specific).
  * Component(s) affected by the bug.
In addition, make sure to cc any developers who should be notified about the bug, and it may be appropriate to assign a developer to fix the bug. By following these measures, we should be able to track and eliminate bugs scientifically.

A huge advantage of using Google Code is that our issue tracker is available publicly, so our process of fixing bugs is transparent and users can contribute their own bug reports. When we begin distributing our program in the alpha and beta phases of testing, we should make sure to refer testers to our issue tracker, which should be fairly easy for them to use. User-submitted bug reports should be handled similarly to developer-submitted bug reports. If you see a bug report that is lacking in some aspect (i.e. it is mis-categorized or not assigned to a developer yet), please add the missing information.

## Documentation Plan ##

Help will be provided in the form of an HTML manual. The desktop application will have a "Help" menu with a menu item that should open the manual in the user's web browser of choice. In addition, pressing F1 should open the manual (in accordance with user's expectations). The manual will have several sections, including: a brief overview of the product's features; a FAQ; and a detailed description of the UI outlining several use cases.