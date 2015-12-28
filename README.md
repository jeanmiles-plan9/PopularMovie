# PopularMovie

Step 1:
Either create or add the following line to file to gradle.properties(Project Properties)

theMovieDBApiKey = “your_api_key”



Step 2:
In file build.gradle(Module: app)
add to android config section the following 

buildTypes.each {

        it.buildConfigField 'String', 'THE_MOVIEDB_API_KEY', theMovieDBApiKey
    }
    
    
Step 3:
Gradle resync the project.
