FROM java:8
COPY *.jar /app.jar
CMD ["--server.port=80 --spring.profiles.active=pro"]
EXPOSE 80
ENTRYPOINT ["java","-jar","app.jar"]