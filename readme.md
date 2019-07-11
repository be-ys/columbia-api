
# Columbia API
Columbia API is the server of the Columbia project. 

Columbia is a glossary. The main aim is to provide a flexible and adaptive platform to enable users creating contexts and definitions on theses contexts. This is a way to help users to better understanding the meaning of the words, depending on the context. Each context could be managed by one or more moderators.
To quickly resume how it works, administrator create contexts, and moderators are creating some terms, and each term has a list of definitions; maximum of one per context.

Columbia is a Java project, running a Springboot server, who run a webserver. This webserver expose a lot of endpoints (full documentation [is available on Swagger](https://app.swaggerhub.com/apis-docs/Artheriom/essai/1.1.0-oas3#/)) who enables to manage each part of the project (users, newsletters, terms, contexts, definitions, history, ...). This project needs a MySQL/MariaDB server to run, or H2. 

## List of features
* Fully customizable
* Right management
* Newsletter system
* Automatic history of a definition, a term, or a context
* Search engine based on exact word, and metaphones
* Availability to define synonyms, antonyms, related words, sources and bibliography for each definition
* Contexts, subcontexts, subsubcontexts, subsubsubcontexts,  [...]
* OAuth2 or independent login (or both !)
* REST API Level 2, following the Richardson Maturity Model, which allows you to use endpoints whatever you want
* Code covered (74% code coverage with Unit Tests, 89% with Integration Tests)

# Installation
## Through Docker
1.	Download the file `Dockerfile` and `src/ressources/application.properties` , and copy them in a folder.
2.	Edit the `application.properties` file with your own configuration
3.	Run the Dockerfile: `docker build --no-cache -t="columbia_api:latest" ./`
4.	Create and run a container: `docker create --name="columbia_api" -p 8080:8080 columbia_api:latest` 
## Standalone (through a release)
1.	Download the jarfile
2.	Download file `src/ressources/application.properties`, edit him and copy him on the same directory than the jarfile
3.	Run `java -jar ./jarfile.jar`
## Standalone (building)
1.	Download the repository
2.	Edit the configuration as you like
3.	Compile the project (main class: `com.almerys.columbia.api.Application`)
4.	In folder “target”, you will find and .jar version of the app. Run with `java -jar ./jarfile.jar`

# Acknowledgment
Program by [@Artheriom](https://github.com/Artheriom/) and [@leChaps](https://github.com/lechaps), created for [@be-ys](https://github.com/be-ys). This program was built for internal usage and was ported to opensource. For this reason, some parts of the code may be different than the original.

Special thanks to all the peoples and teams who created awesome libraries for Java, and the Spring team.
# Licence
Distributed under [MIT](https://opensource.org/licenses/MIT) Licence
