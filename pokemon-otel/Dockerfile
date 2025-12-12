FROM node:20

WORKDIR /app

COPY package*.json ./
RUN npm install --legacy-peer-deps

COPY . .

# Porta da API
EXPOSE 3000

# Porta do Prometheus Exporter
EXPOSE 9464

CMD ["node", "src/server.js"]
