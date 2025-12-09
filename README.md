# Loterias Caixa - Download Automatizado

Sistema automatizado para download de todas as loterias da Caixa Econômica Federal usando Docker.

## 📋 Pré-requisitos

- Docker
- Docker Compose
- Arquivo JAR compilado:  `target/cli-loterias-caixa-1.5-SNAPSHOT.jar`

## 🚀 Como usar

### Opção 1: Usando Docker Compose (Recomendado)

```bash
# 1. Compilar o projeto Java (se ainda não compilou)
mvn clean package

# 2. Construir e executar os containers
docker-compose up --build

# 3. Para executar em background
docker-compose up -d --build

# 4. Ver logs
docker-compose logs -f loterias-downloader

# 5. Parar os containers
docker-compose down
```

### Opção 2: Usando Docker diretamente

```bash
# 1. Construir a imagem
docker build -t loterias-caixa: latest .

# 2. Executar o container
docker run --name loterias-downloader loterias-caixa:latest

# 3. Ver logs
docker logs -f loterias-downloader
```

### Opção 3: Executar uma loteria específica

```bash
# Sobrescrever o entrypoint para executar apenas uma loteria
docker run --rm loterias-caixa:latest \
  bash -c "java -jar /app/cli-loterias-caixa.jar -l quina"
```

## 🎲 Loterias suportadas

O script processa automaticamente as seguintes loterias:

1.  Mega-Sena
2. Quina
3. Lotofácil
4. Lotomania
5. Timemania
6. Dupla Sena
7. Federal
8. Loteca
9. Dia de Sorte
10. Super Sete

## 📊 Estrutura do projeto

```
.
├── Dockerfile                          # Imagem Docker com Java 17
├── docker-compose.yml                  # Orquestração com MongoDB
├── run-all-loterias. sh                # Script de execução das loterias
├── target/
│   └── cli-loterias-caixa-1.5-SNAPSHOT.jar
└── downloads/                          # Arquivos baixados (volume)
```

## 🔧 Configuração

### Variáveis de ambiente (MongoDB)

Edite o `docker-compose.yml` para alterar as credenciais:

```yaml
environment:
  - MONGODB_HOST=mongodb
  - MONGODB_PORT=27017
  - MONGODB_DATABASE=loterias
  - MONGODB_USERNAME=admin
  - MONGODB_PASSWORD=loterias123  # Altere para uma senha segura! 
```

### Ajustar intervalo entre downloads

No arquivo `run-all-loterias.sh`, linha 52:

```bash
sleep 2  # Pausa de 2 segundos entre cada loteria
```

## 📝 Logs

O script exibe um relatório completo:

```
==========================================
Iniciando download de todas as loterias
Data: 2025-12-08 10:30:00
==========================================

----------------------------------------
[1/10] Processando: megasena
----------------------------------------
✓ megasena processada com sucesso!

... 

==========================================
Processamento finalizado!
==========================================
Total de loterias: 10
Sucesso: 10
Erros: 0
Data final: 2025-12-08 10:32:15
==========================================
```

## 🐳 Publicar no Docker Hub

```bash
# 1. Login no Docker Hub
docker login

# 2. Tag da imagem
docker tag loterias-caixa:latest daniloop85/loterias-caixa:latest
docker tag loterias-caixa:latest daniloop85/loterias-caixa: 1.5

# 3. Push para o Docker Hub
docker push daniloop85/loterias-caixa:latest
docker push daniloop85/loterias-caixa:1.5
```

Depois você pode usar diretamente do Docker Hub:

```bash
docker pull daniloop85/loterias-caixa:latest
docker run daniloop85/loterias-caixa:latest
```

## 🌩️ Deploy na AWS

### EC2 com Docker

```bash
# 1. Conectar na instância EC2
ssh -i sua-chave. pem ec2-user@seu-ip

# 2. Instalar Docker
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# 3. Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 4. Clonar o repositório ou copiar os arquivos
# Depois executar: 
docker-compose up -d
```

## 📅 Agendar execução periódica

### Usar cron (Linux)

```bash
# Editar crontab
crontab -e

# Executar todos os dias às 2h da manhã
0 2 * * * cd /caminho/do/projeto && docker-compose up

# Executar a cada 6 horas
0 */6 * * * cd /caminho/do/projeto && docker-compose up
```

## 🆘 Troubleshooting

### Container falha ao iniciar

```bash
# Ver logs detalhados
docker-compose logs loterias-downloader

# Verificar se o JAR existe
docker run --rm loterias-caixa ls -la /app/
```

### MongoDB não conecta

```bash
# Verificar se o MongoDB está rodando
docker-compose ps

# Testar conexão
docker exec -it loterias-mongodb mongosh -u admin -p loterias123
```

## 📄 Licença

Projeto pessoal - DaniloP85
```