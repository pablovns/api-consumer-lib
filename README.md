# Biblioteca em Java para consumo de APIs

Uma biblioteca Java simples para facilitar o consumo de APIs REST. Permite realizar requisições HTTP de forma personalizada, interpretar e categorizar respostas por código de status (2xx, 3xx, 4xx, 5xx) e converter facilmente a resposta em JSON em objetos Java utilizando [Gson](https://github.com/google/gson).

## ✨ Funcionalidades

- Suporte a requisições HTTP personalizadas (`GET`, `POST`, `PUT`, `DELETE`, etc.)
- Tratamento e categorização automática de respostas com base nos códigos HTTP
- Conversão automática de respostas JSON em objetos Java usando Gson
- Reutilização de código para múltiplas requisições com facilidade
- Interface simples e extensível

## 📦 Instalação

Adicione a dependência no seu projeto:

### Maven

```xml
<dependency>
  <groupId>io.github.pablovns</groupId>
  <artifactId>api-consumer-lib</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
