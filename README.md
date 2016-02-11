# PopularMovie

Step 1:
Volley was updated after Stage1 to be registered with git as submodule. 
Clone Example:  (needs recursive option)
git clone git@github.com:jeanmiles-plan9/PopularMovie.git --recursive

Already clone: (needs submodule to be loaded)
cd PopularMovie
git submodule update --init --recursive


Step 2:
Either create or add the following line to file to gradle.properties(Project Properties)

theMovieDBApiKey = “your_api_key”



Step 3:
In file build.gradle(Module: app)
add to android config section the following 

buildTypes.each {

        it.buildConfigField 'String', 'THE_MOVIEDB_API_KEY', theMovieDBApiKey
    }
    
    
Step 4:
Gradle resync the project.
