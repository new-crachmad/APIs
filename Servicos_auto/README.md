# Serviços Auto

Este projeto é uma API para a busca de fornecedores de serviços automotivos, desenvolvida com Spring Boot e instrumentada com OpenTelemetry. A aplicação está containerizada e pode ser facilmente executada via Docker.

## 🎯 Observabilidade
Este projeto já está instrumentado com OpenTelemetry. Os traces, logs e métricas serão enviados para os serviços configurados. Você pode visualizar os dados no Grafana ou outra ferramenta conectada ao OpenTelemetry.

## 📌 Pré-requisitos
Antes de iniciar, certifique-se de ter os seguintes softwares instalados:
- Docker
- Git

## 🏗️ Passo a passo para rodar o projeto

### 1️⃣ Clonar o repositório
```sh
 git clone https://github.com/LTilio/Servicos_auto.git
```
```sh
cd servicos-auto
```

### 2️⃣ Criar o arquivo `application.properties`
Antes de rodar o projeto, crie o arquivo `application.properties` na pasta `src/main/resources/` usando o `application.properties.exemple` como base.

### 3️⃣ Criar a rede Docker (se ainda não existir)
```sh
docker network create sentinel
```

### 4️⃣ Construir e subir os containers
```sh
docker compose up --build -d
```
Isso irá:
- Criar o container do banco de dados PostgreSQL
- Criar o container da aplicação Spring Boot
- Configurar a instrumentação com OpenTelemetry

### 5️⃣ Acessar a API
A API estará rodando em `http://localhost:8085`

### 6️⃣ Documentação Interativa (Swagger UI)
A documentação interativa pode ser acessada em:
```sh
http://localhost:8085/docs
```

### 7️⃣ Para visualizar os logs
Caso precise visualizar os logs da aplicação, utilize:
```sh
docker logs -f servicos_auto
```

### 8️⃣ Para parar os containers
Se precisar parar a aplicação, execute:
```sh
docker compose down
```



## 📝 Configurando o Cloudinary para upload de imagens
A API utiliza o Cloudinary para armazenar imagens. Para configurar as credenciais, siga os passos abaixo:

### 1️⃣ Criar uma conta no Cloudinary
Acesse [Cloudinary](https://cloudinary.com/) e crie uma conta.

### 2️⃣ Obter as credenciais
No painel do Cloudinary, você encontrará as seguintes credenciais:
- `cloud_name`
- `api_key`
- `api_secret`

### 3️⃣ Adicionar as credenciais ao `application.properties`
Adicione as credenciais ao seu arquivo `application.properties`:
```
cloudinary.cloud-name=SEU_CLOUD_NAME
cloudinary.api-key=SUA_API_KEY
cloudinary.api-secret=SEU_API_SECRET
```

### 🔥 Observações
- O banco de dados roda na porta `5434`.
- A API roda na porta `8085`.
- Os dados do PostgreSQL não são persistidos após `docker compose down`.
- OpenTelemetry já está configurado para capturar logs, métricas e traces.

