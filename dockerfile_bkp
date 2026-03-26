# Usamos una imagen oficial de Java como base
FROM openjdk:17-jdk-slim

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo JAR generado por Maven en el directorio de trabajo
COPY target/app-microservice-auth.jar app.jar

# Configuramos la variable de entorno para indicar a Spring Boot que se debe ejecutar como una aplicación JAR
ENV SPRING_PROFILES_ACTIVE=prod

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para iniciar la aplicación
CMD ["java", "-jar", "app.jar"]
```

### Construir y ejecutar los contenedores Docker