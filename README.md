# TodoList API

API REST per la gestione di una lista di attività (todo) sviluppata con Spring Boot 4.0.0.

## 📋 Descrizione

Applicazione backend che fornisce un sistema completo di gestione di todo list con autenticazione JWT e autorizzazione basata su ruoli. Gli utenti possono registrarsi, autenticarsi e gestire le proprie attività personali. Il sistema supporta due ruoli: **USER** (utente standard) e **ADMIN** (amministratore con privilegi aggiuntivi).

## 🛠️ Tecnologie Utilizzate

- **Spring Boot** 4.0.0
- **Java** 17
- **Spring Security** - Autenticazione e autorizzazione
- **Spring Security Crypto** - Crittografia dei refresh token salvati nel database
- **Spring Data JPA** - Persistenza dati
- **PostgreSQL** - Database relazionale
- **JWT (JSON Web Token)** - Autenticazione stateless
- **AWS SDK v2** - Integrazione con Amazon S3 per storage file
- **Apache Tika** - Rilevamento MIME type basato sul contenuto dei file
- **Lombok** - Riduzione boilerplate code
- **MapStruct** 1.5.5 - Mapping automatico Entity ↔ DTO (compile-time)
- **Jakarta Validation** - Validazione dei dati
- **Jackson** - Serializzazione/deserializzazione JSON
- **Bucket4j** - Rate limiting distribuito con algoritmo token bucket
- **Redis** - Storage distribuito per il rate limiting (tramite Redisson)
- **Redisson** - Client Java Redis per integrazione con Bucket4j

## 📦 Dipendenze Principali

- `spring-boot-starter-webmvc` - Framework web
- `spring-boot-starter-data-jpa` - Persistenza con JPA/Hibernate
- `spring-boot-starter-security` - Sicurezza e autenticazione
- `spring-boot-starter-validation` - Validazione dei dati
- `postgresql` - Driver database PostgreSQL
- `jjwt` (0.11.5) - Gestione JWT
- `aws-sdk-s3` (2.26.0) - SDK AWS per integrazione S3
- `aws-sdk-auth` (2.26.0) - SDK AWS per autenticazione
- `tika-core` (2.9.0) - Rilevamento MIME type basato sul contenuto
- `lombok` - Generazione codice automatica
- `mapstruct` (1.5.5.Final) - Mapping automatico Entity ↔ DTO
- `mapstruct-processor` - Annotation processor per MapStruct
- `lombok-mapstruct-binding` (0.2.0) - Integrazione Lombok-MapStruct
- `spring-boot-devtools` - Strumenti di sviluppo
- `bucket4j-core` - Libreria rate limiting con algoritmo token bucket
- `bucket4j-redis` - Integrazione Bucket4j con Redis tramite Redisson
- `redisson` - Client Java Redis per operazioni distribuite

## 🏗️ Architettura del Progetto

```
src/main/java/com/example/dataware/todolist/
├── config/
│   ├── SecurityConfig.java          # Configurazione Spring Security
│   ├── S3Config.java                # Configurazione client AWS S3
│   └── RedisConfig.java             # Configurazione Redis e Bucket4j per rate limiting
├── controller/
│   ├── AuthController.java          # Endpoint autenticazione
│   ├── TodoController.java          # Endpoint gestione todo
│   └── UserController.java          # Endpoint gestione utente
├── dto/
│   ├── validator/
│   │   ├── LoginDto.java            # DTO per login
│   │   ├── UserDto.java             # DTO validazione registrazione utente
│   │   ├── TodoDto.java             # DTO validazione creazione todo
│   │   └── TodoUpdateDto.java       # DTO validazione aggiornamento todo
│   └── response/
│       ├── UserResponse.java        # Response utente completo
│       ├── TodoResponse.java        # Response todo completo
│       ├── TokenResponse.java       # Response con accessToken e refreshToken
│       ├── PageResponse.java        # DTO per risposte paginate
│       └── builder/
│           ├── SuccessResponse.java         # Modello risposta successo
│           └── SuccessResponseBuilder.java  # Builder per risposte di successo
├── entity/
│   ├── BaseEntity.java              # Entità base con id, createdAt, updatedAt
│   ├── enums/
│   │   └── Role.java                # Enum ruoli utente (USER, ADMIN)
│   ├── User.java                    # Entità utente
│   └── Todo.java                    # Entità todo
├── exception/
│   ├── ErrorResponse.java           # Modello risposta errore con metodo buildResponse()
│   ├── CustomExceptionHandler.java  # Gestore eccezioni custom (priorità alta)
│   ├── GlobalExceptionHandler.java  # Gestore eccezioni native/Spring (priorità bassa)
│   └── custom/
│       ├── BaseCustomException.java         # Interfaccia comune per eccezioni custom
│       ├── EmailConflictException.java      # Eccezione conflitto email
│       ├── EmptyFileException.java          # Eccezione file vuoto
│       ├── InvalidCredentialsException.java # Eccezione credenziali non valide
│       ├── InvalidFileTypeException.java    # Eccezione tipo file non valido
│       ├── InvalidSortablePropertyException.java # Eccezione proprietà ordinabile non valida
│       ├── S3UploadException.java           # Eccezione errore upload S3
│       ├── TodoNotFoundException.java       # Eccezione todo non trovato
│       └── UserNotFoundException.java       # Eccezione utente non trovato
├── filter/
│   ├── jwt/
│   │   ├── enums/
│   │   │   └── TokenType.java       # Enum per distinguere ACCESS e REFRESH token
│   │   ├── payload/
│   │   │   └── JwtPayload.java      # Payload JWT (userId, email)
│   │   ├── service/
│   │   │   └── JwtService.java      # Servizio gestione JWT (generazione e validazione)
│   │   ├── JwtAccessFilter.java     # Filtro per validazione access token
│   │   └── JwtRefreshFilter.java    # Filtro per validazione refresh token
│   └── rateLimiter/
│       ├── enums/
│       │   └── RateLimitEndpoint.java  # Enum endpoint con limiti configurabili
│       ├── service/
│       │   └── RateLimiteService.java  # Servizio rate limiting con Bucket4j e Redis
│       └── RateLimitFilter.java     # Filtro per applicare rate limiting
├── mapper/
│   ├── TodoMapper.java              # Interfaccia MapStruct per mapping Todo ↔ DTO
│   └── UserMapper.java              # Interfaccia MapStruct per mapping User ↔ DTO
│   # ⚠️ Le implementazioni sono auto-generate da MapStruct durante la compilazione
│   # 📁 Posizione: target/generated-sources/annotations/com/example/dataware/todolist/mapper/
├── repository/
│   ├── TodoRepository.java          # Repository JPA per Todo
│   └── UserRepository.java          # Repository JPA per User
├── service/
│   ├── interfaces/
│   │   ├── AuthService.java         # Interfaccia servizio autenticazione
│   │   ├── TodoService.java         # Interfaccia servizio todo
│   │   └── UserService.java         # Interfaccia servizio utente
│   ├── implementation/
│   │   ├── AuthServiceImpl.java     # Implementazione servizio autenticazione
│   │   ├── TodoServiceImpl.java     # Implementazione servizio todo
│   │   └── UserServiceImpl.java     # Implementazione servizio utente
│   └── EncryptionService.java       # Servizio crittografia/decrittografia refresh token
├── s3/
│   ├── S3Properties.java            # Proprietà configurazione S3
│   └── S3Service.java               # Servizio gestione upload/delete file su S3
└── util/
    ├── fileValidation/
    │   ├── ImageValidation.java      # Validazione file immagine con Apache Tika
    │   └── enums/
    │       └── ImageMimeType.java    # Enum tipi MIME supportati (JPEG, PNG, WEBP, GIF, HEIC, HEIF)
    └── sort/
        └── TodoSortableProperty.java  # Enum proprietà ordinabili per i todo
```

## 🔐 Sistema di Autenticazione e Autorizzazione

L'applicazione utilizza **JWT (JSON Web Token)** con sistema di **Access Token** e **Refresh Token** per l'autenticazione stateless e **autorizzazione basata su ruoli**:

- Gli endpoint `/auth/register` e `/auth/login` sono pubblici e non richiedono autenticazione
- L'endpoint `/auth/refresh-token` richiede un **refresh token** valido nell'header `Authorization: Bearer <refreshToken>`
- L'endpoint `/auth/logout` richiede un **access token** valido nell'header `Authorization: Bearer <accessToken>`
- Tutti gli altri endpoint richiedono un **access token** valido nell'header `Authorization: Bearer <accessToken>`
- Il sistema utilizza due tipi di token:
  - **Access Token**: Token a breve durata per autenticare le richieste API
  - **Refresh Token**: Token a lunga durata per ottenere nuovi access token senza ri-autenticarsi, salvato nel database (crittografato)
- Entrambi i token contengono `email`, `userId` e `role` dell'utente
- I token utilizzano chiavi segrete separate per maggiore sicurezza
- I refresh token vengono crittografati prima di essere salvati nel database
- La sessione è configurata come `STATELESS`
- **Ordine dei filtri**: I filtri sono configurati nell'ordine seguente nella catena di Spring Security:
  1. `RateLimitFilter` - Applica il rate limiting (prima di tutto)
  2. `JwtAccessFilter` - Valida gli access token
  3. `JwtRefreshFilter` - Valida i refresh token (solo per `/auth/refresh-token`)
- Due filtri separati gestiscono la validazione: `JwtAccessFilter` per gli access token e `JwtRefreshFilter` per i refresh token
- **Autorizzazione basata su ruoli**: Il sistema supporta due ruoli (`USER` e `ADMIN`) e utilizza `@PreAuthorize` per controllare l'accesso agli endpoint in base al ruolo dell'utente
- Il ruolo viene estratto dal token JWT e aggiunto alle authorities di Spring Security con il prefisso `ROLE_`

## 🗄️ Modello Dati

### User

- `id` (Long) - Identificatore univoco
- `nome` (String) - Nome utente (min 4 caratteri)
- `email` (String) - Email univoca
- `password` (String) - Password hashata con BCrypt
- `profileImageUrl` (String) - URL dell'immagine profilo su S3 o URL default
- `role` (Role) - Ruolo dell'utente (`USER` o `ADMIN`). Default: `USER` (impostato automaticamente tramite `@PrePersist`)
- `refreshToken` (String) - Refresh token crittografato salvato nel database (nullable)
- `todos` (List<Todo>) - Lista di todo associati
- `createdAt` (Instant) - Data di creazione
- `updatedAt` (Instant) - Data ultimo aggiornamento

### Todo

- `id` (Long) - Identificatore univoco
- `title` (String) - Titolo del todo (min 4 caratteri)
- `completed` (boolean) - Stato di completamento (default: false)
- `user` (User) - Utente proprietario
- `createdAt` (Instant) - Data di creazione
- `updatedAt` (Instant) - Data ultimo aggiornamento

## 📡 Endpoint API

### Autenticazione (`/auth`)

#### POST `/auth/register`

Registrazione nuovo utente.

**Body:**

```json
{
  "nome": "string (min 4 caratteri)",
  "email": "string (formato email valido)",
  "password": "string (min 6 caratteri)"
}
```

**Response:** `201 Created`

```json
{
  "statusCode": 201,
  "message": "Success",
  "data": {
    "id": 1,
    "nome": "Mario Rossi",
    "email": "mario@example.com",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- Alla registrazione, il ruolo viene impostato automaticamente a `USER` se non specificato
- Il ruolo viene incluso nei token JWT generati durante il login

#### POST `/auth/login`

Login utente esistente.

**Body:**

```json
{
  "email": "string",
  "password": "string (min 6 caratteri)"
}
```

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### POST `/auth/refresh-token`

Ottiene nuovi access token e refresh token utilizzando un refresh token valido.

**Headers:** `Authorization: Bearer <refreshToken>`

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- Richiede un refresh token valido nell'header `Authorization`
- Il refresh token viene validato contro quello salvato nel database (crittografato)
- Restituisce una nuova coppia di access token e refresh token
- Il nuovo refresh token viene salvato nel database (sostituendo quello precedente)
- Utile quando l'access token è scaduto senza dover effettuare nuovamente il login

#### DELETE `/auth/logout`

Effettua il logout dell'utente autenticato, rimuovendo il refresh token dal database.

**Headers:** `Authorization: Bearer <accessToken>`

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": "Logged out successfully",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- Richiede un access token valido nell'header `Authorization`
- Rimuove il refresh token salvato nel database per l'utente
- Dopo il logout, il refresh token non può più essere utilizzato per ottenere nuovi token
- L'utente dovrà effettuare nuovamente il login per ottenere nuovi token

### Todo (`/todos`)

**Nota:** Tutti gli endpoint richiedono autenticazione JWT.

#### GET `/todos`

Ottiene tutti i todo dell'utente autenticato con paginazione e filtro opzionale.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**

- `page` (opzionale, default: `1`) - Numero di pagina (min: 1)
- `limit` (opzionale, default: `10`) - Numero di elementi per pagina (min: 1, max: 100)
- `completed` (opzionale) - Filtra per stato di completamento (`true` o `false`)
- `sort` (opzionale) - Parametro di ordinamento nel formato Spring Data (es: `sort=title,asc` o `sort=updatedAt,desc`)

**Ordinamento:**

- Se non specificato, i todo sono ordinati per `updatedAt` in ordine decrescente (più recenti prima) per default
- Le proprietà ordinabili sono: `id`, `title`, `completed`, `createdAt`, `updatedAt`
- Formato: `sort=<proprietà>,<direzione>` dove direzione può essere `asc` (crescente) o `desc` (decrescente)
- Esempi: `sort=title,asc`, `sort=createdAt,desc`, `sort=completed,asc&sort=title,desc` (ordinamento multiplo)

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Fare la spesa",
        "completed": false,
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      }
    ],
    "currentPage": 1,
    "limit": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- La paginazione è 1-based (la prima pagina è `page=1`)
- Il parametro `completed` è opzionale: se non specificato, vengono restituiti tutti i todo
- Il parametro `sort` è opzionale: se non specificato, viene utilizzato l'ordinamento di default (`updatedAt` decrescente)
- Esempi:
  - `GET /todos?page=2&limit=5&completed=false` - Seconda pagina di 5 todo non completati
  - `GET /todos?sort=title,asc` - Todo ordinati per titolo crescente
  - `GET /todos?sort=createdAt,desc&completed=true` - Todo completati ordinati per data di creazione decrescente
  - `GET /todos?sort=completed,asc&sort=title,desc` - Ordinamento multiplo: prima per completamento, poi per titolo decrescente

#### GET `/todos/{todoId}`

Ottiene un singolo todo per ID.

**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

#### POST `/todos`

Crea un nuovo todo.

**Headers:** `Authorization: Bearer <token>`

**Body:**

```json
{
  "title": "string (min 4 caratteri)"
}
```

**Response:** `201 Created`

#### PATCH `/todos/{todoId}`

Aggiorna un todo esistente.

**Headers:** `Authorization: Bearer <token>`

**Body:**

```json
{
  "title": "string (opzionale, min 4 caratteri)",
  "completed": true/false (opzionale)
}
```

**Response:** `200 OK`

#### DELETE `/todos/{todoId}`

Elimina un todo.

**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

### Utente (`/users`)

**Nota:** Tutti gli endpoint richiedono autenticazione JWT e ruoli specifici.

**Autorizzazione:**

- Tutti gli endpoint richiedono almeno il ruolo `USER` o `ADMIN`
- L'endpoint `GET /users` richiede il ruolo `ADMIN`

#### GET `/users`

Ottiene tutti gli utenti registrati nel sistema con paginazione.

**Ruolo richiesto:** `ADMIN`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**

- `page` (opzionale, default: `1`) - Numero di pagina (min: 1)
- `limit` (opzionale, default: `10`) - Numero di elementi per pagina (min: 1, max: 100)

**Ordinamento:** Gli utenti sono ordinati per `id` in ordine crescente.

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "nome": "Mario Rossi",
        "email": "mario@example.com",
        "role": "USER",
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      },
      {
        "id": 2,
        "nome": "Admin User",
        "email": "admin@example.com",
        "role": "ADMIN",
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      }
    ],
    "currentPage": 1,
    "limit": 10,
    "totalElements": 15,
    "totalPages": 2,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- La paginazione è 1-based (la prima pagina è `page=1`)
- Esempio: `GET /users?page=2&limit=5` per ottenere la seconda pagina di 5 utenti

#### GET `/users/profile`

Ottiene il profilo dell'utente autenticato.

**Ruolo richiesto:** `USER` o `ADMIN`

**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "nome": "Mario Rossi",
    "email": "mario@example.com",
    "profileImageUrl": "https://bucket.s3.region.amazonaws.com/users/1/profile.jpg",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### POST `/users/profile/image`

Carica un'immagine profilo per l'utente autenticato su Amazon S3.

**Ruolo richiesto:** `USER` o `ADMIN`

**Headers:** `Authorization: Bearer <token>`

**Content-Type:** `multipart/form-data`

**Body:**

- `file` (MultipartFile) - File immagine da caricare

**Validazione:**

- Il file deve essere un'immagine valida (JPEG, PNG, WEBP, GIF, HEIC, HEIF)
- Dimensione massima: 20MB
- Il tipo MIME viene rilevato dal contenuto del file (Apache Tika), non dall'estensione

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "nome": "Mario Rossi",
    "email": "mario@example.com",
    "profileImageUrl": "https://bucket.s3.region.amazonaws.com/users/1/profile.jpg",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- L'immagine viene caricata su S3 con il path: `users/{userId}/profile.{ext}`
- Se esiste già un'immagine profilo, viene eliminata automaticamente dopo il successo dell'upload
- Il vecchio file viene eliminato solo dopo che il database è stato aggiornato con successo
- L'immagine di default non viene mai eliminata

#### DELETE `/users/profile/image`

Elimina l'immagine profilo dell'utente autenticato e ripristina l'avatar di default.

**Ruolo richiesto:** `USER` o `ADMIN`

**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "nome": "Mario Rossi",
    "email": "mario@example.com",
    "profileImageUrl": "https://default-avatar-url.com/avatar.png",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- Se l'immagine è già quella di default, non viene eseguita alcuna operazione
- Il file viene eliminato da S3 e l'URL viene impostato all'avatar di default configurato

#### DELETE `/users`

Elimina l'account dell'utente autenticato (cascade delete dei todo).

**Ruolo richiesto:** `USER` o `ADMIN`

**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

## ⚙️ Configurazione

### Variabili d'Ambiente

Creare un file `.env` nella root del progetto con le seguenti variabili:

```properties
DB_URL=jdbc:postgresql://localhost:5432/todolist
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_ACCESS_SECRET=your_access_secret_key_min_32_characters
JWT_ACCESS_EXPIRATION=2h
JWT_REFRESH_SECRET=your_refresh_secret_key_min_32_characters
JWT_REFRESH_EXPIRATION=7d
ENCRYPTION_KEY=your_encryption_key_min_16_characters
ENCRYPTION_SALT=your_encryption_salt_min_8_characters
AWS_ACCESS_KEY_ID=your_aws_access_key_id
AWS_SECRET_ACCESS_KEY=your_aws_secret_access_key
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-bucket-name
DEFAULT_AVATAR_URL=https://default-avatar-url.com/avatar.png
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

**Descrizione variabili:**

- `DB_URL` - URL di connessione al database PostgreSQL
- `DB_USERNAME` - Username per il database
- `DB_PASSWORD` - Password per il database
- `JWT_ACCESS_SECRET` - Chiave segreta per la firma degli access token (minimo 32 caratteri)
- `JWT_ACCESS_EXPIRATION` - Durata dell'access token (es: `2h` = 2 ore)
- `JWT_REFRESH_SECRET` - Chiave segreta per la firma dei refresh token (minimo 32 caratteri, diversa dall'access secret)
- `JWT_REFRESH_EXPIRATION` - Durata del refresh token (es: `7d` = 7 giorni)
- `ENCRYPTION_KEY` - Chiave per la crittografia dei refresh token salvati nel database (minimo 16 caratteri)
- `ENCRYPTION_SALT` - Salt per la crittografia dei refresh token (minimo 8 caratteri)
- `AWS_ACCESS_KEY_ID` - Access Key ID per AWS S3
- `AWS_SECRET_ACCESS_KEY` - Secret Access Key per AWS S3
- `AWS_REGION` - Regione AWS dove si trova il bucket S3 (es: `us-east-1`, `eu-west-1`)
- `AWS_S3_BUCKET` - Nome del bucket S3 per lo storage delle immagini profilo
- `DEFAULT_AVATAR_URL` - URL dell'avatar di default da utilizzare quando l'utente non ha un'immagine profilo
- `REDIS_HOST` - Host di Redis (default: `localhost`)
- `REDIS_PORT` - Porta di Redis (default: `6379`)
- `REDIS_PASSWORD` - Password di Redis (opzionale, lasciare vuoto se non configurata)

### application.properties

Il file `application.properties` è configurato con:

- **Porta server:** 3001
- **Hibernate DDL:** `update` (aggiorna automaticamente lo schema del database)
- **Show SQL:** `true` (mostra le query SQL nei log)
- **Error handling:** Stack trace disabilitato, messaggi abilitati
- **Logging:** DEBUG per il package dell'applicazione, INFO per root
- **Circular references:** Abilitati per i mapper MapStruct (`spring.main.allow-circular-references=true`)
- **Caricamento .env:** Il file `.env` viene caricato automaticamente tramite `spring.config.import`
- **JWT separati:** Configurazione separata per access token e refresh token con chiavi e scadenze indipendenti
- **Crittografia refresh token:** Configurazione per crittografare i refresh token salvati nel database usando Spring Security Crypto
- **Method Security:** Abilitato tramite `@EnableMethodSecurity` in `SecurityConfig` per supportare l'autorizzazione basata su ruoli con `@PreAuthorize`
- **Multipart file upload:** Dimensione massima file 20MB (`spring.servlet.multipart.max-file-size=20MB`)
- **AWS S3:** Configurazione per l'integrazione con Amazon S3 per lo storage delle immagini profilo
- **Redis:** Configurazione per il rate limiting distribuito (host, port, password configurabili)

## 🚀 Installazione e Avvio

### Prerequisiti

- Java 17 o superiore
- Maven 3.6+
- PostgreSQL in esecuzione
- Redis in esecuzione (per il rate limiting distribuito)

### Passaggi

1. **Clonare il repository**

```bash
git clone <repository-url>
cd todolist
```

2. **Configurare il database PostgreSQL**

```bash
createdb todolist
```

3. **Avviare Redis** (se non già in esecuzione)

```bash
# Con Docker
docker run -d -p 6379:6379 redis:latest

# Oppure con redis-server (se installato localmente)
redis-server
```

4. **Configurare le variabili d'ambiente**
   Creare il file `.env` nella root del progetto con le credenziali del database, la chiave JWT e le configurazioni Redis.

5. **Compilare e avviare l'applicazione**

```bash
./mvnw spring-boot:run
```

Oppure tramite Maven:

```bash
mvn clean install
java -jar target/todolist-0.0.1-SNAPSHOT.jar
```

**Nota sulla compilazione:**

- Durante la compilazione, MapStruct genera automaticamente le implementazioni dei mapper in `target/generated-sources/annotations/`
- Le classi generate sono annotate con `@Component` e possono essere iniettate come bean Spring
- Se modifichi le interfacce mapper, ricompila per rigenerare le implementazioni

L'applicazione sarà disponibile su `http://localhost:3001`

## 📝 Validazione

L'applicazione utilizza **Jakarta Validation** per validare i dati in ingresso:

- **UserDto:**

  - `nome`: NotBlank, Size(min=4)
  - `email`: NotBlank, Email
  - `password`: NotBlank, Size(min=6)

- **LoginDto:**

  - `email`: NotBlank, Email
  - `password`: NotBlank, Size(min=6)

- **TodoDto:**

  - `title`: NotBlank, Size(min=4)

- **TodoUpdateDto:**

  - `title`: Pattern (opzionale), Size(min=4)
  - `completed`: Boolean (opzionale)

- **Upload Immagini Profilo:**
  - Il file deve essere un'immagine valida (rilevamento MIME type dal contenuto con Apache Tika)
  - Formati supportati: JPEG, PNG, WEBP, GIF, HEIC, HEIF
  - Dimensione massima: 20MB (configurato in `application.properties`)
  - Validazione basata sul contenuto del file, non sull'estensione

## 📄 Paginazione

L'applicazione utilizza **Spring Data Pagination** per gestire grandi quantità di dati in modo efficiente. Gli endpoint che restituiscono liste di elementi supportano la paginazione tramite query parameters.

### Endpoint con Paginazione

- **GET `/todos`** - Lista todo dell'utente autenticato
- **GET `/users`** - Lista tutti gli utenti (solo ADMIN)

### Parametri di Paginazione

- `page` (opzionale, default: `1`) - Numero di pagina (1-based, minimo: 1)
- `limit` (opzionale, default: `10`) - Numero di elementi per pagina (minimo: 1, massimo: 100)

### Filtri Opzionali

- **GET `/todos`** supporta anche il parametro `completed` (Boolean) per filtrare i todo per stato di completamento

### Struttura Risposta Paginata

Tutte le risposte paginate utilizzano il DTO `PageResponse<T>` che contiene:

```json
{
  "content": [...],           // Array di elementi della pagina corrente
  "currentPage": 1,           // Numero della pagina corrente (1-based)
  "limit": 10,               // Numero di elementi per pagina
  "totalElements": 25,        // Totale elementi in tutte le pagine
  "totalPages": 3,            // Totale pagine disponibili
  "first": true,              // Se è la prima pagina
  "last": false,              // Se è l'ultima pagina
  "hasNext": true,            // Se esiste una pagina successiva
  "hasPrevious": false        // Se esiste una pagina precedente
}
```

### Ordinamento

- **GET `/todos`**:
  - Default: Ordinati per `updatedAt` in ordine decrescente (più recenti prima) se non specificato
  - Configurabile tramite parametro `sort` con proprietà: `id`, `title`, `completed`, `createdAt`, `updatedAt`
  - Formato: `sort=<proprietà>,<direzione>` (es: `sort=title,asc`, `sort=createdAt,desc`)
  - Supporta ordinamento multiplo: `sort=completed,asc&sort=title,desc`
- **GET `/users`**: Ordinati per `id` in ordine crescente

### Esempi di Utilizzo

```bash
# Prima pagina con 10 elementi (default)
GET /todos

# Seconda pagina con 5 elementi
GET /todos?page=2&limit=5

# Prima pagina di todo non completati
GET /todos?page=1&limit=10&completed=false

# Todo ordinati per titolo crescente
GET /todos?sort=title,asc

# Todo completati ordinati per data di creazione decrescente
GET /todos?sort=createdAt,desc&completed=true

# Ordinamento multiplo: prima per completamento, poi per titolo decrescente
GET /todos?sort=completed,asc&sort=title,desc

# Lista utenti, terza pagina con 20 elementi
GET /users?page=3&limit=20
```

### Vantaggi

- ✅ **Performance**: Carica solo i dati necessari per la pagina richiesta
- ✅ **Scalabilità**: Gestisce grandi quantità di dati senza problemi di memoria
- ✅ **Flessibilità**: Parametri configurabili per adattarsi a diverse esigenze
- ✅ **Navigazione**: Metadati completi per implementare la navigazione tra pagine
- ✅ **Filtri**: Supporto per filtri opzionali (es: `completed` per i todo)

## 🔒 Gestione Errori

L'applicazione utilizza un sistema di gestione errori rifattorizzato e centralizzato con due handler separati per una migliore organizzazione e manutenibilità.

### Architettura

Il sistema di gestione errori è diviso in due handler con priorità diversa:

1. **`CustomExceptionHandler`** (`@Order(HIGHEST_PRECEDENCE)`)

   - Gestisce tutte le eccezioni custom dell'applicazione
   - Ha priorità massima per essere valutato per primo
   - Tutte le eccezioni custom implementano `BaseCustomException`

2. **`GlobalExceptionHandler`** (`@Order(LOWEST_PRECEDENCE)`)
   - Gestisce tutte le eccezioni native di Java/Spring
   - Ha priorità minima come fallback per eccezioni non gestite altrove

### Eccezioni Custom

Tutte le eccezioni custom dell'applicazione implementano l'interfaccia **`BaseCustomException`**, che definisce i metodi comuni (`getStatusCode()`, `getErrorReasonPhrase()`, `getMessage()`) per una gestione uniforme:

- `EmailConflictException` - Conflitto email durante la registrazione (409 Conflict)
- `EmptyFileException` - File vuoto o nullo durante l'upload (400 Bad Request)
- `InvalidCredentialsException` - Credenziali non valide durante il login (400 Bad Request)
- `InvalidFileTypeException` - Tipo file non valido durante l'upload (400 Bad Request)
- `InvalidSortablePropertyException` - Proprietà di ordinamento non valida (400 Bad Request)
- `S3UploadException` - Errore durante l'upload su S3 (502 Bad Gateway)
- `TodoNotFoundException` - Todo non trovato (404 Not Found)
- `UserNotFoundException` - Utente non trovato (404 Not Found)

### Eccezioni Standard (Native/Spring)

Il `GlobalExceptionHandler` gestisce tutte le eccezioni standard di Java/Spring:

- `ResponseStatusException` - Eccezioni personalizzate con codice HTTP
- `MethodArgumentNotValidException` - Errori di validazione dei DTO (restituisce `Map<String, String>` con errori di validazione)
- `HttpMessageNotReadableException` - Body mancante o non valido
- `MethodArgumentTypeMismatchException` - Tipo parametro non valido (PathVariable/RequestParam)
- `AuthorizationDeniedException` - Accesso negato per autorizzazione (403 Forbidden)
- `MultipartException` - Errori durante l'upload di file multipart
- `MissingServletRequestPartException` - Parte della richiesta multipart mancante
- `MaxUploadSizeExceededException` - File supera la dimensione massima consentita
- `Exception` - Errori generici non gestiti (500 Internal Server Error)

### Pattern di Rifattorizzazione

Il sistema è stato rifattorizzato per eliminare la duplicazione del codice e migliorare la manutenibilità:

#### 1. Metodo `buildResponse()` in `ErrorResponse`

Il metodo statico `ErrorResponse.buildResponse()` centralizza la costruzione di `ResponseEntity<ErrorResponse>`:

```java
public static ResponseEntity<ErrorResponse> buildResponse(
    int statusCode, String reasonPhrase, Object message
)
```

**Vantaggi:**

- Elimina la duplicazione tra i due handler
- Logica di costruzione centralizzata in un unico punto
- Manutenibilità migliorata (modifiche in un solo posto)

#### 2. Metodo Helper in `CustomExceptionHandler`

```java
private ResponseEntity<ErrorResponse> handleException(BaseCustomException ex)
```

**Caratteristiche:**

- Accetta direttamente l'istanza dell'eccezione
- Estrae automaticamente tutti i dati dall'interfaccia `BaseCustomException`
- Estrae automaticamente il nome della classe per il logging

#### 3. Metodo Helper in `GlobalExceptionHandler`

```java
private ResponseEntity<ErrorResponse> handleException(
    Exception ex, int statusCode, String reasonPhrase, Object message
)
```

**Caratteristiche:**

- Accetta l'istanza dell'eccezione e i parametri già estratti
- Necessario perché le eccezioni Spring native non hanno un'interfaccia comune
- Ogni handler method estrae manualmente i dati specifici, poi delega al metodo helper per logging e costruzione della risposta

### Formato Risposta Errore

Tutte le risposte di errore seguono il formato standardizzato:

```json
{
  "statusCode": 400,
  "reason": "Bad Request",
  "message": "Messaggio di errore",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Per errori di validazione multipli**, il campo `message` può essere un oggetto:

```json
{
  "statusCode": 400,
  "reason": "Bad Request",
  "message": {
    "email": "Email non valida",
    "password": "Password troppo corta"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Note:**

- Il campo `message` può essere una `String` o un `Object` (es: `Map<String, String>` per errori di validazione multipli)
- Il campo `reason` contiene la frase di errore HTTP standardizzata (es: "Bad Request", "Not Found")
- Il campo `timestamp` viene aggiunto automaticamente in formato UTC
- Nessuno stack trace viene esposto nelle risposte per motivi di sicurezza
- Il logging è consistente: tutti i log utilizzano il formato `{ExceptionName}: {statusCode} - {message}`

## 🗺️ MapStruct - Mapping Automatico

Il progetto utilizza **MapStruct** per la conversione automatica tra entità JPA e DTO di risposta, eliminando completamente il boilerplate code manuale.

### Caratteristiche

- **Zero boilerplate**: Le interfacce mapper contengono solo le definizioni dei metodi
- **Type-safe**: Tutti gli errori di mapping vengono rilevati a compile-time
- **Performance**: Il codice viene generato a compile-time (no reflection a runtime)
- **Auto-mapping**: MapStruct mappa automaticamente i campi con lo stesso nome
- **Conversione automatica**: Gestisce automaticamente conversioni di tipi annidati e liste

### Come Funziona

1. **Interfacce Mapper**: Definite in `src/main/java/.../mapper/`

   ```java
   @Mapper(componentModel = "spring")
   public interface TodoMapper {
       TodoResponse toDTO(Todo todo);
   }
   ```

2. **Implementazioni Generate**: MapStruct genera automaticamente le implementazioni in `target/generated-sources/annotations/` durante la compilazione

3. **Componenti Spring**: Le implementazioni generate sono automaticamente annotate con `@Component`, quindi possono essere iniettate nei controller

### Configurazione

MapStruct è configurato nel `pom.xml`:

- **Dipendenza**: `mapstruct` (1.5.5.Final)
- **Annotation Processor**: `mapstruct-processor` configurato in `maven-compiler-plugin`
- **Integrazione Lombok**: `lombok-mapstruct-binding` per compatibilità con Lombok

### Vantaggi rispetto ai Mapper Manuali

- ✅ **Nessun codice manuale**: Tutto generato automaticamente
- ✅ **Type-safe**: Errori rilevati in compilazione
- ✅ **Manutenibilità**: Cambi alle entità/DTO si riflettono automaticamente
- ✅ **Performance**: Codice generato ottimizzato, no reflection
- ✅ **Auto-mapping intelligente**: Gestisce automaticamente conversioni complesse

### Esempio di Mapping Automatico

MapStruct gestisce automaticamente:

- **Campi semplici**: `id`, `title`, `completed` → mappati per nome
- **Tipi annidati**: `User` → `UserResponse` (genera metodo helper automaticamente)
- **Liste**: `List<Todo>` → `List<TodoResponse>` (conversione automatica)

Le implementazioni generate sono visibili in `target/generated-sources/annotations/com/example/dataware/todolist/mapper/` dopo la compilazione.

## 🔄 Sistema di Refresh Token

Il progetto implementa un sistema completo di **Access Token** e **Refresh Token** con persistenza e crittografia per migliorare la sicurezza e l'esperienza utente.

### Come Funziona

1. **Login**: Quando un utente effettua il login:

   - Vengono generati due token: **Access Token** (breve durata) e **Refresh Token** (lunga durata)
   - Il refresh token viene **crittografato** e **salvato nel database** nella colonna `refresh_token` della tabella `users`
   - Entrambi i token vengono restituiti al client

2. **Utilizzo Access Token**: L'access token viene inviato nell'header `Authorization: Bearer <accessToken>` per tutte le richieste API protette.

3. **Refresh Token**: Quando l'access token scade:

   - Il client invia il refresh token all'endpoint `/auth/refresh-token`
   - Il sistema valida il refresh token contro quello salvato nel database (dopo decrittografia)
   - Se valido, genera una nuova coppia di token e aggiorna il refresh token nel database

4. **Logout**: Quando l'utente effettua il logout:
   - Il refresh token viene **rimosso dal database**
   - Il refresh token non può più essere utilizzato per ottenere nuovi token
   - L'utente dovrà effettuare nuovamente il login

### Persistenza e Crittografia

- **Persistenza**: I refresh token vengono salvati nel database nella colonna `refresh_token` della tabella `users`
- **Crittografia**: I refresh token vengono crittografati prima di essere salvati usando **Spring Security Crypto** (`EncryptionService`)
- **Validazione**: Durante il refresh, il token fornito viene confrontato con quello salvato nel database (dopo decrittografia)
- **Sicurezza**: Anche se il database viene compromesso, i refresh token sono crittografati e non utilizzabili direttamente

### Architettura dei Filtri

Il sistema utilizza due filtri separati per gestire i diversi tipi di token:

- **JwtAccessFilter**:

  - Valida gli access token per tutti gli endpoint tranne `/auth/**`
  - Estrae `userId`, `email` e `role` dal token e li inserisce nel `SecurityContext`
  - Aggiunge il ruolo alle authorities di Spring Security con il prefisso `ROLE_` (es: `ROLE_USER`, `ROLE_ADMIN`)
  - Gestisce errori di token scaduto, malformato o mancante

- **JwtRefreshFilter**:
  - Valida i refresh token solo per l'endpoint `/auth/refresh-token`
  - Estrae `userId` e `email` dal refresh token
  - **Recupera l'utente dal database** e valida che il refresh token corrisponda a quello salvato
  - Decrittografa il token salvato e lo confronta con quello fornito
  - Permette l'accesso all'endpoint di refresh solo se la validazione ha successo

### Servizi Coinvolti

- **JwtService**: Genera e valida i token JWT (access e refresh)
- **EncryptionService**: Crittografa e decrittografa i refresh token usando Spring Security Crypto
- **AuthService**: Gestisce login, logout e refresh token, coordinando JwtService e EncryptionService

### Sicurezza

- **Chiavi Separate**: Access token e refresh token utilizzano chiavi segrete diverse (`JWT_ACCESS_SECRET` e `JWT_REFRESH_SECRET`)
- **Scadenze Diverse**: Access token con durata breve, refresh token con durata più lunga
- **Validazione Separata**: Ogni tipo di token viene validato con la propria chiave segreta
- **Enum TokenType**: Utilizzato per distinguere tra ACCESS e REFRESH token durante la validazione
- **Crittografia Database**: I refresh token salvati sono crittografati con chiave e salt configurabili
- **Validazione Database**: Il refresh token viene validato contro quello salvato nel database
- **Revoca Esplicita**: Il logout rimuove il refresh token dal database, invalidandolo immediatamente

### Vantaggi

- ✅ **Sicurezza Migliorata**: Access token a breve durata limitano i danni in caso di compromissione
- ✅ **Esperienza Utente**: Nessun bisogno di ri-autenticarsi frequentemente grazie al refresh token
- ✅ **Revoca Implicita**: Quando un refresh token scade, l'utente deve ri-autenticarsi
- ✅ **Revoca Esplicita**: Il logout invalida immediatamente il refresh token
- ✅ **Separazione delle Responsabilità**: Chiavi e filtri separati per maggiore sicurezza
- ✅ **Protezione Database**: I refresh token salvati sono crittografati
- ✅ **Validazione Robusta**: Il refresh token deve corrispondere a quello salvato nel database

## 👥 Sistema di Autorizzazione Basata su Ruoli

Il progetto implementa un sistema completo di **autorizzazione basata su ruoli (RBAC)** utilizzando Spring Security e le annotation `@PreAuthorize`.

### Ruoli Disponibili

Il sistema supporta due ruoli definiti nell'enum `Role`:

- **USER**: Ruolo standard per gli utenti registrati. Permette di gestire i propri todo e il proprio profilo.
- **ADMIN**: Ruolo amministratore con privilegi aggiuntivi. Oltre alle funzionalità di `USER`, può visualizzare tutti gli utenti registrati nel sistema.

### Come Funziona

1. **Registrazione**: Quando un utente si registra, il ruolo viene impostato automaticamente a `USER` tramite il metodo `@PrePersist` nell'entità `User`.

2. **Token JWT**: Il ruolo dell'utente viene incluso nei token JWT (sia access che refresh) come claim `role`.

3. **Validazione Token**: Il `JwtAccessFilter` estrae il ruolo dal token e lo aggiunge alle authorities di Spring Security con il prefisso `ROLE_` (es: `ROLE_USER`, `ROLE_ADMIN`).

4. **Autorizzazione Endpoint**: I controller utilizzano l'annotation `@PreAuthorize` per specificare quali ruoli possono accedere a ciascun endpoint:
   - `@PreAuthorize("hasAnyRole('USER', 'ADMIN')")` - Richiede uno dei ruoli specificati
   - `@PreAuthorize("hasRole('ADMIN')")` - Richiede esclusivamente il ruolo ADMIN

### Configurazione

- **SecurityConfig**: Abilita `@EnableMethodSecurity` per supportare le annotation `@PreAuthorize`
- **JwtAccessFilter**: Estrae il ruolo dal token e lo aggiunge alle authorities del `SecurityContext`
- **User Entity**: Campo `role` di tipo `Role` con default `USER`

### Endpoint e Autorizzazioni

| Endpoint               | Metodo             | Ruolo Richiesto                  | Descrizione                |
| ---------------------- | ------------------ | -------------------------------- | -------------------------- |
| `/auth/register`       | POST               | Nessuno                          | Registrazione pubblica     |
| `/auth/login`          | POST               | Nessuno                          | Login pubblico             |
| `/auth/refresh-token`  | POST               | Nessuno (richiede refresh token) | Refresh token              |
| `/auth/logout`         | DELETE             | USER o ADMIN                     | Logout                     |
| `/todos`               | GET, POST          | USER o ADMIN                     | Gestione todo              |
| `/todos/{id}`          | GET, PATCH, DELETE | USER o ADMIN                     | Operazioni su singolo todo |
| `/users`               | GET                | **ADMIN**                        | Lista tutti gli utenti     |
| `/users/profile`       | GET                | USER o ADMIN                     | Profilo utente autenticato |
| `/users/profile/image` | POST               | USER o ADMIN                     | Carica immagine profilo    |
| `/users/profile/image` | DELETE             | USER o ADMIN                     | Elimina immagine profilo   |
| `/users`               | DELETE             | USER o ADMIN                     | Elimina account utente     |

### Esempio di Utilizzo

```java
@RestController
@RequestMapping("/users")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // Autorizzazione a livello di classe
public class UserController {

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")  // Autorizzazione a livello di metodo
    public ResponseEntity<...> findAll() {
        // Solo ADMIN può accedere
    }

    @GetMapping("/profile")
    public ResponseEntity<...> getProfile() {
        // USER o ADMIN possono accedere (eredita dalla classe)
    }
}
```

### Sicurezza

- ✅ **Ruolo nel Token**: Il ruolo è incluso nel JWT e viene validato ad ogni richiesta
- ✅ **Method Security**: Utilizzo di `@PreAuthorize` per controllo granulare degli accessi
- ✅ **Default Sicuro**: Nuovi utenti ricevono automaticamente il ruolo `USER` (privilegi minimi)
- ✅ **Separazione Ruoli**: Chiaro distinguo tra privilegi USER e ADMIN
- ✅ **Type-Safe**: Utilizzo di enum per i ruoli (previene errori di digitazione)

## 📸 Gestione Immagini Profilo con AWS S3

Il progetto implementa un sistema completo di gestione immagini profilo utilizzando **Amazon S3** per lo storage e **Apache Tika** per la validazione sicura dei file.

### Caratteristiche

- **Storage su S3**: Le immagini profilo vengono caricate su Amazon S3 invece che sul filesystem locale
- **Validazione Robusta**: Utilizzo di **Apache Tika** per rilevare il MIME type dal contenuto del file (magic numbers), non dall'estensione
- **Formati Supportati**: JPEG, PNG, WEBP, GIF, HEIC, HEIF
- **Gestione Rollback**: Il vecchio file viene eliminato solo dopo il successo dell'upload e del salvataggio nel database
- **Avatar Default**: Sistema di fallback con avatar di default configurabile
- **Dimensione Massima**: 20MB per file (configurabile in `application.properties`)

### Architettura

#### Componenti Principali

1. **S3Service**: Gestisce upload e delete dei file su S3

   - `uploadUserProfileImage()`: Carica un'immagine profilo su S3
   - `deleteFileByUrl()`: Elimina un file da S3 usando il suo URL pubblico
   - `extractKeyFromUrl()`: Estrae la key S3 dall'URL (supporta virtual-hosted style)

2. **ImageValidation**: Valida i file immagine

   - `validateAndGetImageMimeType()`: Rileva il MIME type dal contenuto usando Apache Tika
   - Supporta solo formati immagine validi e sicuri

3. **UserService**: Coordina le operazioni
   - `updateProfileImage()`: Gestisce l'upload con rollback in caso di errore
   - `deleteProfileImage()`: Elimina l'immagine e ripristina il default
   - `deleteImageFromS3()`: Metodo privato per eliminazione sicura

### Flusso di Upload

1. **Validazione**: Il file viene validato con Apache Tika per verificare che sia un'immagine valida
2. **Upload S3**: L'immagine viene caricata su S3 con path: `users/{userId}/profile.{ext}`
3. **Salvataggio DB**: L'URL dell'immagine viene salvato nel database
4. **Eliminazione Vecchio File**: Solo dopo il successo del salvataggio, il vecchio file viene eliminato da S3

### Sicurezza

- ✅ **Validazione Contenuto**: Il MIME type viene rilevato dal contenuto, non dall'estensione (previene attacchi di tipo spoofing)
- ✅ **Formati Limitati**: Solo formati immagine sicuri sono supportati
- ✅ **Dimensione Massima**: Limite di 20MB per prevenire DoS
- ✅ **Rollback Sicuro**: Il vecchio file non viene eliminato se l'upload o il salvataggio falliscono
- ✅ **Protezione Default**: L'avatar di default non viene mai eliminato

### Configurazione S3

Il client S3 viene configurato in `S3Config` utilizzando:

- **Credentials**: Access Key ID e Secret Access Key da variabili d'ambiente
- **Region**: Regione AWS configurabile
- **Bucket**: Nome del bucket S3 per lo storage

### Eccezioni Personalizzate

- `EmptyFileException`: File nullo o vuoto (400 Bad Request)
- `InvalidFileTypeException`: Tipo file non supportato (400 Bad Request)
- `S3UploadException`: Errore durante l'upload su S3 (502 Bad Gateway)

### Esempio di Utilizzo

```bash
# Carica immagine profilo
curl -X POST http://localhost:3001/users/profile/image \
  -H "Authorization: Bearer <token>" \
  -F "file=@profile.jpg"

# Elimina immagine profilo (ripristina default)
curl -X DELETE http://localhost:3001/users/profile/image \
  -H "Authorization: Bearer <token>"
```

### Note Importanti

- Le immagini vengono caricate con il formato originale mantenuto
- Il path su S3 è strutturato: `users/{userId}/profile.{ext}`
- Se l'utente carica una nuova immagine, quella precedente viene eliminata automaticamente
- L'eliminazione del vecchio file avviene solo dopo il successo dell'operazione per garantire consistenza

## ⏱️ Sistema di Rate Limiting

Il progetto implementa un sistema completo di **rate limiting distribuito** utilizzando **Bucket4j** con **Redis** per proteggere gli endpoint critici da abusi e attacchi DDoS.

### Caratteristiche

- **Rate Limiting Distribuito**: Utilizza Redis per sincronizzare i limiti tra più istanze dell'applicazione
- **Algoritmo Token Bucket**: Implementato tramite Bucket4j con refill intervallato
- **Configurazione per Endpoint**: Ogni endpoint può avere limiti personalizzati
- **Rilevamento IP Intelligente**: Gestisce correttamente gli IP dei client dietro proxy e load balancer
- **Header Informativi**: Restituisce header HTTP standard (`X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`)
- **Single Request Check**: Ottimizzato per verificare il rate limit con una singola chiamata a Redis

### Architettura

#### Componenti Principali

1. **RateLimitFilter**: Filtro Spring che applica il rate limiting

   - Estende `OncePerRequestFilter` per essere eseguito una sola volta per richiesta
   - Posizionato prima di `JwtAccessFilter` nella catena dei filtri
   - Estrae l'IP reale del client considerando proxy/load balancer
   - Imposta gli header di rate limit in tutte le risposte

2. **RateLimiteService**: Servizio che gestisce la logica di rate limiting

   - Utilizza Bucket4j con Redis (tramite Redisson)
   - Restituisce tutte le informazioni necessarie in una singola chiamata (`checkRateLimit`)
   - Calcola il reset time solo quando necessario (quando il limite è stato superato)

3. **RateLimitEndpoint**: Enum che configura gli endpoint protetti

   - Ogni endpoint ha: metodo HTTP, path, numero massimo richieste, finestra temporale
   - Matching preciso per metodo HTTP e path
   - Facilmente estendibile per aggiungere nuovi endpoint

4. **RedisConfig**: Configurazione Redis e Bucket4j
   - Configura Redisson client per connessione a Redis
   - Configura ProxyManager per Bucket4j con supporto distribuito
   - Supporta autenticazione Redis (password opzionale)

### Endpoint Protetti

Il sistema protegge i seguenti endpoint con rate limiting:

| Endpoint               | Metodo | Limite      | Finestra   |
| ---------------------- | ------ | ----------- | ---------- |
| `/auth/register`       | POST   | 4 richieste | 60 secondi |
| `/auth/login`          | POST   | 4 richieste | 60 secondi |
| `/auth/logout`         | DELETE | 4 richieste | 60 secondi |
| `/auth/refresh-token`  | POST   | 4 richieste | 60 secondi |
| `/users/profile/image` | POST   | 2 richieste | 60 secondi |
| `/users/profile/image` | DELETE | 2 richieste | 60 secondi |

### Come Funziona

1. **Richiesta Incoming**: Il `RateLimitFilter` intercetta tutte le richieste
2. **Verifica Endpoint**: Controlla se l'endpoint richiede rate limiting tramite l'enum `RateLimitEndpoint`
3. **Estrae IP Client**: Estrae l'IP reale del client considerando header `X-Forwarded-For` e `X-Real-IP`
4. **Genera Key**: Crea una chiave univoca nel formato: `{ENDPOINT_NAME}:{METHOD}:{CLIENT_IP}`
5. **Verifica Limite**: Chiama `RateLimiteService.checkRateLimit()` che:
   - Ottiene o crea un bucket per la chiave
   - Tenta di consumare un token dal bucket
   - Restituisce tutte le informazioni necessarie (allowed, remaining, resetTime)
6. **Imposta Header**: Imposta gli header HTTP di rate limit nella risposta
7. **Gestisce Esito**: Se il limite è stato superato, restituisce `429 Too Many Requests`, altrimenti prosegue con la richiesta

### Rilevamento IP Client

Il sistema gestisce correttamente gli IP dei client in diversi scenari:

- **Proxy/Load Balancer**: Controlla l'header `X-Forwarded-For` e prende il primo IP della lista
- **X-Real-IP**: Fallback all'header `X-Real-IP` se presente
- **Connessione Diretta**: Fallback a `request.getRemoteAddr()` per connessioni dirette

```java
private String getClientIpAddress(HttpServletRequest request) {
    // 1. Controlla X-Forwarded-For (può contenere lista di IP)
    // 2. Controlla X-Real-IP
    // 3. Fallback a getRemoteAddr()
}
```

### Header HTTP

Tutte le risposte includono gli header di rate limiting:

- **X-RateLimit-Limit**: Numero massimo di richieste consentite nella finestra temporale
- **X-RateLimit-Remaining**: Numero di richieste rimanenti nella finestra corrente
- **X-RateLimit-Reset**: Timestamp Unix (in secondi) quando il bucket si resetta (solo quando limitato)

**Esempio di risposta:**

```
X-RateLimit-Limit: 4
X-RateLimit-Remaining: 2
X-RateLimit-Reset: 1704110400
```

### Risposta Rate Limit Superato

Quando il rate limit viene superato, il sistema restituisce:

**Status Code:** `429 Too Many Requests`

**Response Body:**

```json
{
  "statusCode": 429,
  "reason": "Too Many Requests",
  "message": "Too many requests. Maximum 4 requests per 1 minute(s) allowed.",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Algoritmo Token Bucket

Bucket4j utilizza l'algoritmo **Token Bucket** con refill intervallato:

- **Capacity**: Numero massimo di token nel bucket (es: 4)
- **Refill**: Il bucket si riempie completamente ogni `windowSeconds` (es: 60 secondi)
- **Consumption**: Ogni richiesta consuma 1 token
- **Distribuito**: I bucket sono sincronizzati tra tutte le istanze tramite Redis

### Configurazione Redis

Il sistema utilizza Redis per il rate limiting distribuito:

- **Host**: Configurabile tramite `REDIS_HOST` (default: `localhost`)
- **Port**: Configurabile tramite `REDIS_PORT` (default: `6379`)
- **Password**: Configurabile tramite `REDIS_PASSWORD` (opzionale)
- **Client**: Redisson per operazioni distribuite
- **Proxy Manager**: Bucket4j RedissonBasedProxyManager per sincronizzazione

### Vantaggi

- ✅ **Protezione DDoS**: Previene attacchi di tipo Denial of Service limitando le richieste
- ✅ **Protezione Brute Force**: Limita i tentativi di login/registrazione
- ✅ **Distribuito**: Funziona correttamente con più istanze dell'applicazione
- ✅ **Efficiente**: Singola chiamata a Redis per verificare il limite
- ✅ **Trasparente**: Header informativi per il client
- ✅ **Configurabile**: Facile aggiungere/modificare limiti per endpoint
- ✅ **IP-Aware**: Gestisce correttamente proxy e load balancer
- ✅ **Produzione-Ready**: Ottimizzato per ambienti di produzione

### Estendere il Rate Limiting

Per aggiungere un nuovo endpoint al rate limiting:

1. Aggiungi una nuova entry all'enum `RateLimitEndpoint`:

```java
NEW_ENDPOINT(HttpMethod.POST, "/api/new-endpoint", 10, 60);
```

2. Il filtro applicherà automaticamente il rate limiting all'endpoint specificato.

### Sicurezza

- ✅ **IP-Based**: Il rate limiting è basato sull'IP del client
- ✅ **Distribuito**: I limiti sono sincronizzati tra tutte le istanze
- ✅ **Non By-Passabile**: Il filtro è posizionato all'inizio della catena
- ✅ **Header Standard**: Utilizza header HTTP standard per trasparenza
- ✅ **Logging**: Registra tutti i tentativi di superamento del limite

## 👤 Autore

Progetto sviluppato per la gestione di una lista di attività (todo).
