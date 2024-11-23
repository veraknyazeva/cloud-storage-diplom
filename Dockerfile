FROM openjdk:17-oracle

EXPOSE 8081

ADD target/cloud-storage-diplom.jar app.jar

CMD ["java", "-jar", "app.jar"]