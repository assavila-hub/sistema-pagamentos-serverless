# Usa a imagem oficial do Java 17 da Amazon (Amazon Corretto), otimizada para AWS
FROM amazoncorretto:17-alpine
# Define o diretório de trabalho dentro do contentor
WORKDIR /app
# Copia o ficheiro JAR gerado pelo Maven para dentro do contentor
# (O nome do ficheiro pode variar, o asterisco garante que apanha o ficheiro gerado)
COPY target/*.jar app.jar
# Expõe a porta que a aplicação Spring Boot utiliza
EXPOSE 8080
# Comando para executar a aplicação quando o contentor iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]