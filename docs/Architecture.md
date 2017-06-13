# Architecture
![Architecture Overview](https://github.com/nguyenLisa/abm/raw/master/docs/components.png)

## Web UI 
Bundle: de.fraunhofer.abm.app

This component contains all the web application code and is the interface for 
the user. The user can use the search to retrieve a list of projects from 
Github. With the help of the ProjectAnalysis component, this list can be 
narrowed down by specifying a set of criteria. Afterwards the user can create /
edit collections, which get stored in a RDBMS by the Data Persistence component.
When the user starts a build for a collection, the SuiteBuilder builds the 
project with one of the ProjectBuilders and stores the result in the RDBMS.

## Search / Crawler
Bundles: de.fraunhofer.abm.crawler.api, de.fraunhofer.abm.crawler.github

The Crawler API is used by the search to find projects on popular open source 
project hosters. At the moment the only implementation is the Github crawler. 
The Crawler API is also used to retrieve information about the branches, tags 
and commits of the projects.

## ProjectAnalysis
Bundles: de.fraunhofer.abm.projectanalysis, de.fraunhofer.abm.projectanalyzer.api,
de.fraunhofer.abm.projectanalyzer.*

The ProjectAnalysis component uses the ProjectAnalyzer API to extract project
properties like the used build system, the size of the project or the programming
language. These properties can be used as criteria to narrow down the search 
results of the crawler. This functionality can be extended very easily by adding
a new analyzer component and extending the web form in the Web UI component.

## SuiteBuilder
Bundles: de.fraunhofer.abm.suitebuilder, de.fraunhofer.abm.builder.*

The SuiteBuilder is used to build the collections created by the user. For each
project it tries to find a suitable ProjectBuilder and then runs the build with.
When the whole build process is finished and at least one of the projects built
successfully, a zip archive is created, which can be downloaded by the user.
The build result and the output of each step involved to build a project are 
stored in the RDBMS with the help of the Data Persistence component.

## Data Persistence
Bundles: de.fraunhofer.abm.collection.dao, de.fraunhofer.abm.collection.dao.jpa

The Data Persistence components provide a set of Data Access Objects to store
collections and build results in the MySQL database. The current implementation
uses JPA and Hibernate as a JPA provider.

## User management / Security
Bundles: de.fraunhofer.abm.useradmin.*, de.fraunhofer.abm.security

ABM uses the OSGi UserAdmin API to manage its users. An web interface is 
available at http://localhost:8080/system/console
(credentials are root/password). At the moment there are the three groups Admin,
UserAdmin and RegisteredUser. RegisteredUser is the group for all normal users,
which are registered in the ABM database. The Admin group is for administrators
and allows the user to modify any data in the database. UserAdmin is not yet
used. The idea is, to provide an administration page for the user management,
which can only be accessed by users in the UserAdmin or Admin group.

To authenticate and authorize users, there are the interfaces `Authenticator`
and `Authorizer` in the de.fraunhofer.abm.security bundle. Their implementations
use the previously mentioned UserAdmin API.

