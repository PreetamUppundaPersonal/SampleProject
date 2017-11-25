README for project ArchTypePOC
===============================

This module will allow a user or project to create a whole fully functioning automated test framework in under 2 minutes... 
By the end of this, the project will have access to the centralised framework and some sample tests all ready for the team to begin testing.

This module uses the centralised framework where all configuration is carried out. Therefore providing the project with a location where tests can be written and wiping out all the time needed to get a framework installed. 

# Project creation:

* First Clone this project to a location you are confortable with, we will call it '*home*'
* Open command prompt (cmd) and cd to *home*. You should see the POM.xml here
* Run: **mvn clean compile** (if required) 
* Then run: **mvn clean archetype:create-from-project**
* After that is successful, cd to **target\generated-sources\archetype**
* Run: **mvn clean install**; This will place the archetype jar in your very own *.m2\repository\org\apache\maven\archetypes* folder
* Create a brand new folder and name it after your project
* Within cmd, cd to the folder
* Within cmd run: **mvn archetype:generate**
* As you run this command it will prompt you to fill out your *GroupID, ArtifactID, Name etc...*
* Alternatively you can run a single command containing these details e.g.
  **mvn archetype:generate -DgroupId=prototypeTest -DartifactId=prototypeProj -DarchetypeArtifactId=archetypeProj-archetype -DinteractiveMode=false**
* Once this is done and built successfully import the project in your IDE, take a look at your fully functioning project, ready to write automated tests against the UI, API's and Devices...

# **Happy Testing**
