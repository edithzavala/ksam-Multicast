FROM java:8
VOLUME /tmp
EXPOSE 8083
EXPOSE 8088
ADD /build/libs/ksam-adapter.jar ksam-adapter.jar
ENTRYPOINT ["java","-jar","ksam-adapter.jar", "--server.port=8088"]
