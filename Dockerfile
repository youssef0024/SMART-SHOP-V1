# ÉTAPE 1 : Build avec Maven et Java 17 (ton JDK actuel)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copie du pom.xml et téléchargement des dépendances pour mettre en cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie du code source et compilation
COPY src ./src
RUN mvn clean package -DskipTests

# ÉTAPE 2 : Image d'exécution légère
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copie du JAR généré depuis l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Port exposé par ton backend
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]