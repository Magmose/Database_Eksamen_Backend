# Database eksamen

For at køre vores projekt skal vores 3 databaser sættes op.

### Neo4J
Installer Neo4J. Vi har brugt Neo4J desktop 1.4.4
Opret et projekt med en Graph DBSM med password 123 (version 4.0.0 eller nyere)
Installer Data Science Library i projektet (version 1.5.0 eller nyere)
Start databasen med standard Bolt connector port (7687)

### Redis 
Installer og kør redis 
Anbefalet docker script: 
“docker run --name redisMMR -p 6379:6379 -v redis-data:/data -d redis:alpine”


### PostgreSQL
Installer og kør PostgreSQL
Anbefalet docker script: 
“docker run --name postgresMMR -p 5432:5432 -d -e POSTGRES_PASSWORD=softdb -e POSTGRES_USER=softdb -e POSTGRES_DB=movieusers -v pgdata:/var/lib/postgressql/data postgres”


Postman 
Her kan vores postman projekt findes:
MANGLER

Angular frontend
Her kan vores angular frontend findes: 
MANGLER
