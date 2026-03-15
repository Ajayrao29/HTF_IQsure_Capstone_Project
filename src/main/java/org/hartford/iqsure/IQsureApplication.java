/*
 * ============================================================================
 * FILE: IQsureApplication.java
 * LOCATION: src/main/java/org/hartford/iqsure/
 * PURPOSE: This is the MAIN ENTRY POINT of the entire backend application.
 *          When you run the project, Java starts executing from the main()
 *          method inside this file. It boots up the Spring Boot framework,
 *          which then automatically sets up the database, web server (port 8080),
 *          and all the APIs.
 *
 * WHAT HAPPENS WHEN THIS RUNS:
 *   1. Spring Boot starts the embedded Tomcat web server on port 8080
 *   2. It scans all files in this package (org.hartford.iqsure) for
 *      @Controller, @Service, @Repository, @Component annotations
 *   3. It connects to the database using settings from application.properties
 *      (see: src/main/resources/application.properties)
 *   4. The DataSeeder (see: config/DataSeeder.java) creates a default admin user
 *   5. The app is now ready to receive HTTP requests from the Angular frontend
 * ============================================================================
 */
package org.hartford.iqsure;

// SpringApplication - the class that actually starts the Spring Boot app
import org.springframework.boot.SpringApplication;
// @SpringBootApplication - a shortcut annotation that enables:
//   - @Configuration (this class can define beans/settings)
//   - @EnableAutoConfiguration (Spring auto-configures based on dependencies)
//   - @ComponentScan (Spring scans all sub-packages for annotated classes)
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IQsureApplication {

    // This is where the app starts. Java always looks for "public static void main".
    public static void main(String[] args) {
        // This single line does ALL the heavy lifting:
        // - Creates the Spring application context
        // - Starts the web server
        // - Initializes all beans (services, controllers, etc.)
        SpringApplication.run(IQsureApplication.class, args);
    }

}
