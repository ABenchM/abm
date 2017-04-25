# Web UI 
Bundle de.fraunhofer.abm.app

This component contains all the web application code and is the interface for the user.
The user can use the search to retrieve a list of projects from Github. With the help of the
ProjectAnalysis component, this list can be narrowed down by specifying a set of criteria.
Afterwards the user can create / edit collections, which get stored in a RDBMS by the Data Persistence component.
When the user starts a build for a collection, the SuiteBuilder builds the project with one of the ProjectBuilders
and stores the result in the RDBMS.

# Search / Crawler
Bundles: de.fraunhofer.abm.crawler.api, de.fraunhofer.abm.crawler.github

The Crawler API is used by the search to find projects on popular open source project hosters. At the moment the only
implementation is the Github crawler. The Crawler API is also used to retrieve information about the branches, tags and
commits of the projects.

