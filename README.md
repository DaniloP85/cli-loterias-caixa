# Loterias Caixa — monólito web

Aplicação Spring Boot (web + JSP) que importa resultados das loterias da Caixa Econômica Federal para o MongoDB, calcula features estatísticas por concurso e expõe tudo via API REST e páginas web — pensada como base de estudo de machine learning com os números das loterias.

## 🎲 Loterias suportadas

- Mega-Sena (`megasena`)
- Lotofácil (`lotofacil`)
- Quina (`quina`)
- Lotomania (`lotomania`)

## 📋 Pré-requisitos

- Docker + Docker Compose (recomendado), ou
- JDK 17+, Maven e um MongoDB acessível

## 🚀 Como usar

### Opção 1: Docker Compose (recomendado)

```bash
docker-compose up -d --build
```

Sobe o MongoDB e a aplicação em `http://localhost:8080`.

```bash
docker-compose logs -f loterias-web   # logs
docker-compose down                   # parar
```

### Opção 2: local

```bash
mvn clean package                     # gera target/loterias-caixa.war
java -jar target/loterias-caixa.war   # WAR executável (Tomcat embutido)
```

> O empacotamento é **WAR** (não JAR) porque JSP exige isso no Spring Boot. O WAR continua executável com `java -jar`.

## 🌐 Páginas

| Página | URL |
|---|---|
| Home (cards das loterias + botão importar) | `/` |
| Concursos (tabela paginada) | `/loterias/{loteria}` |
| Detalhe do concurso + features | `/loterias/{loteria}/concursos/{numero}` |
| Dashboard (frequência das dezenas, médias) | `/loterias/{loteria}/dashboard` |

## 🔌 API REST

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/loterias/{loteria}/importacao?completo=false` | Dispara a importação em background (202). `completo=true` apaga e reimporta tudo; sem ele, retoma do último concurso salvo. |
| GET | `/api/loterias/{loteria}/importacao/status` | Progresso da importação (estado, processados, total, percentual). |
| GET | `/api/loterias/{loteria}/concursos?page=0&size=20` | Concursos paginados (mais recentes primeiro). |
| GET | `/api/loterias/{loteria}/concursos/{numero}` | Um concurso com as features estatísticas. |
| GET | `/api/loterias/{loteria}/estatisticas` | Agregações: frequência de cada dezena e médias das features. |
| GET | `/api/loterias/{loteria}/export?formato=csv` | Dataset flat para ML (`csv` ou `json`): `concurso, data, n1..nK, soma, media, desvio_padrao, log_produto, pares, impares, baixos, altos`. |

Exemplo de fluxo:

```bash
curl -X POST localhost:8080/api/loterias/lotofacil/importacao
curl localhost:8080/api/loterias/lotofacil/importacao/status
curl -o lotofacil.csv "localhost:8080/api/loterias/lotofacil/export?formato=csv"
```

O CSV cai direto num `pandas.read_csv('lotofacil.csv')` para começar o estudo de ML.

## 🔧 Configuração

Conexão com o MongoDB via variáveis de ambiente (defaults em `application.properties`):

| Variável | Default |
|---|---|
| `MONGODB_HOST` | `localhost` |
| `MONGODB_PORT` | `27017` |
| `MONGODB_DATABASE` | `loterias` |
| `MONGODB_USERNAME` | `admin` |
| `MONGODB_PASSWORD` | `loterias123` |

O `docker-compose.yaml` já aponta a aplicação para o serviço `mongodb`.

### Certificado TLS da Caixa

O certificado da API da Caixa não é confiado pelo truststore padrão da JVM. No build da imagem Docker, o `install-caixa-cert.sh` baixa e importa o certificado no `cacerts`. Rodando fora do Docker, execute o script manualmente uma vez (requer `openssl` e `keytool`).

## 🆘 Troubleshooting

```bash
docker-compose ps                                          # serviços no ar?
docker-compose logs loterias-web                           # logs da aplicação
docker exec -it loterias-mongodb mongosh -u admin -p loterias123   # testar o Mongo
```
