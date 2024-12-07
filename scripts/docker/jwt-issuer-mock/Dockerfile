FROM amazoncorretto:21-alpine

# vars
ENV WIREMOCK_VERSION 3.9.1

# new directories
RUN mkdir /var/wiremock
RUN mkdir /var/wiremock/mappings

# default directory
WORKDIR /var/wiremock

# download wiremock stand alone build
RUN wget -O /var/wiremock/wiremock-standalone.jar https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/$WIREMOCK_VERSION/wiremock-standalone-$WIREMOCK_VERSION.jar

ADD ./stubs.json /var/wiremock/mappings

# expose wiremock ports
EXPOSE 8080 8443

# start wiremock
CMD java -jar /var/wiremock/wiremock-standalone.jar -global-response-templating
