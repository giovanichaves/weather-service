# Gardena smart system weather-service

## Building & running

### Maven

1. Using the *Maven Wrapper*. Depending on your OS, simply run the following command to build the application:

        ./mvnw clean install
		
	 or

        ./mvnw.cmd clean install
	 
2. Start application with `java -jar target/weatherservice-1.0-SNAPSHOT.jar --darksky.apiKey=<api-key> --openweather.apiKey=<api-key>`

3. To check that your application is running enter url `http://localhost:8080`

### Gradle  

1. Using the *Gradle Wrapper*. Depending on your OS, simply run the following command to build the application:

        ./gradlew assemble
		
	 or

        ./gradlew.bat assemble
   
2. Start application with `java -jar build/libs/weatherservice-1.0-SNAPSHOT.jar --darksky.apiKey=<api-key> --openweather.apiKey=<api-key>`

3. To check that your application is running enter url `http://localhost:8080`
