# SpringBoot Basics with Dockerization


This is a basic SpringBoot application with very basic dockerization. It contains the following:
  - Employee Registration
  - Swagger integration
  - H2 DB integeration
  - Use JPA repository to access db data.
  - Docker build and install
  
  ## Deployment
   There are two ways of deployment:
   1. Build using command gradle clean build. Run as Spring Boot Application.
   2. Deploy on DOCKER
      - Open Command prompt
      - Change the directory to root of this application
      - Build image using command : `docker image build -t empreg` .
      - Run the image in container using command : `docker container run --name empreg -p 8080:8080 -t empreg`
      
  ## Pre-requisites installation on system
  	1. Java
  	2. Docker
  	3. Gradle    
  
