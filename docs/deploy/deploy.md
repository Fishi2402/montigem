<!-- (c) https://github.com/MontiCore/monticore -->
# Deployment
### Hardware Requirements (TODO)
For a UMLP instance the server should have at least:
* (?) 5GB HDD,
* (?) 2 CPUs
* (?) 4 GB RAM

### Software Requirements
* Any linux OS with (current!) [docker-compose installation](https://docs.docker.com/compose/install/).
  * Windows Server might be possible, but it is VERY hard to run linux-docker-container on windows server.
* Open Port 80 and 443 on TCP
* Domain
  * Let's Encrypt must be allowed to issue certificates for the domain. Might require DNS configuration (see [CAA](https://de.wikipedia.org/wiki/DNS_Certification_Authority_Authorization)). 
### Build Dockerimage
#### Backend
The usage of [jib](https://github.com/GoogleContainerTools/jib/) as gradle-plugin is recommended. 
You can build and publish the dockerimage with one command. No docker installation is required.
````
cd <backendLocation>
gradle jib \ 
      -Djib.to.image=<imageNameOfBackend> \
      -Djib.to.auth.username=<RegistryUser>  \
      -Djib.to.auth.password=<RegistryPassword> 
````
For more options, see the [jib](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin) documentation.

#### Frontend
The frontend dockerimage is using [Caddy](https://caddyserver.com/) to serve static files, proxy to backend and handle SSL certificates

Following files are required in the frontend Directory:
* `Dockerfile`
* `Caddyfile`

In the recommended umlp-setup the gradle task of type ``FrontendSetup`` creates these files in its output directory.

To build the docker-container:
````
cd <frontendLocation>
gradle assemble
docker build .  -t <imageNameOfFrontend>
````
[Push](https://docs.docker.com/engine/reference/commandline/push/) the docker-container to the container-registry on a server (e.g. gitlab)

### Deploy on Server
* copy the `docker-compose.yml` file on the server
  * to use a neo4j database in deployment you can use the `neo4j_docker-compose.yml` 
  * e.g. ``scp ./docker-compose.yml <user<@<domain.de<:~/<folder>`` 
  * Modify the file according to comments
* In gitlab create login token with read access to package registry
* On the server:
    * `docker login -u user -p <token> registry.git.rwth-aachen.de`
    * `docker compose up --detach`
