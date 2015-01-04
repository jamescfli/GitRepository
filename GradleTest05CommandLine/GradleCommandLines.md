### Executing multiple tasks
	* gradle dist test
### Excluding tasks
	* gradle dist -x test
### Continuing the build when a failure occurs
	* —-continue
### Abbreviated task name
	* gradle di
### Abbreviated camel case task name, compileTest
	* gradle cT
### Obtaining information about your build
	* Listing projects: gradle -q projects
	* Providing a description for a project: description = 'The shared API for the application' in build.gradle
	* Listing tasks: gradle -q tasks
	* Obtaining more information about tasks: gradle -q tasks --all
	* Obtaining detailed help for tasks: gradle -q help --task libs
	* Listing project dependencies: gradle -q dependencies api:dependencies webapp:dependencies
	* Filtering dependency report by configuration: gradle -q api:dependencies --configuration testCompile
	* Getting the insight into a particular dependency: gradle -q webapp:dependencyInsight --dependency groovy --configuration compile
	* Listing project properties: gradle -q api:properties
### Profiling a build
	* gradle -q dist --profile
### Dry Run
	* gradle -m clean compile