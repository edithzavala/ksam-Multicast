FROM java:8
VOLUME /tmp
EXPOSE 8083
ADD /build/libs/ksam-adapter.jar ksam-adapter.jar
ENTRYPOINT ["java","-jar","ksam-adapter.jar"]
