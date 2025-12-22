# TodoList API

API REST per la gestione di una lista di attività (todo) sviluppata con Spring Boot 4.0.0.

## 📋 Descrizione

Applicazione backend che fornisce un sistema completo di gestione di todo list con autenticazione JWT. Gli utenti possono registrarsi, autenticarsi e gestire le proprie attività personali.

## 🛠️ Tecnologie Utilizzate

- **Spring Boot** 4.0.0
- **Java** 17
- **Spring Security** - Autenticazione e autorizzazione
- **Spring Data JPA** - Persistenza dati
- **PostgreSQL** - Database relazionale
- **JWT (JSON Web Token)** - Autenticazione stateless
- **Lombok** - Riduzione boilerplate code
- **MapStruct** 1.5.5 - Mapping automatico Entity ↔ DTO (compile-time)
- **Jakarta Validation** - Validazione dei dati
- **Jackson** - Serializzazione/deserializzazione JSON

## 📦 Dipendenze Principali

- `spring-boot-starter-webmvc` - Framework web
- `spring-boot-starter-data-jpa` - Persistenza con JPA/Hibernate
- `spring-boot-starter-security` - Sicurezza e autenticazione
- `spring-boot-starter-validation` - Validazione dei dati
- `postgresql` - Driver database PostgreSQL
- `jjwt` (0.11.5) - Gestione JWT
- `lombok` - Generazione codice automatica
- `mapstruct` (1.5.5.Final) - Mapping automatico Entity ↔ DTO
- `mapstruct-processor` - Annotation processor per MapStruct
- `lombok-mapstruct-binding` (0.2.0) - Integrazione Lombok-MapStruct
- `spring-boot-devtools` - Strumenti di sviluppo

## 🏗️ Architettura del Progetto

```
src/main/java/com/example/dataware/todolist/
├── config/
│   └── SecurityConfig.java          # Configurazione Spring Security
├── controller/
│   ├── AuthController.java          # Endpoint autenticazione
│   ├── TodoController.java          # Endpoint gestione todo
│   └── UserController.java          # Endpoint gestione utente
├── dto/
│   ├── LoginDto.java                # DTO per login
│   ├── validator/
│   │   ├── UserDto.java             # DTO validazione registrazione utente
│   │   ├── TodoDto.java             # DTO validazione creazione todo
│   │   └── TodoUpdateDto.java       # DTO validazione aggiornamento todo
│   └── response/
│       ├── UserResponse.java        # Response utente completo
│       ├── UserSimpleResponse.java  # Response utente semplificato
│       ├── TodoResponse.java        # Response todo completo
│       ├── TodoSimpleResponse.java  # Response todo semplificato
│       └── TokenResponse.java       # Response con accessToken e refreshToken
├── entity/
│   ├── BaseEntity.java              # Entità base con id, createdAt, updatedAt
│   ├── User.java                    # Entità utente
│   └── Todo.java                    # Entità todo
├── exception/
│   ├── ErrorResponse.java           # Modello risposta errore
│   └── GlobalExceptionHandler.java  # Gestore globale eccezioni
├── jwt/
│   ├── enums/
│   │   └── TokenType.java           # Enum per distinguere ACCESS e REFRESH token
│   ├── filter/
│   │   ├── JwtAccessFilter.java     # Filtro per validazione access token
│   │   └── JwtRefreshFilter.java    # Filtro per validazione refresh token
│   ├── JwtPayload.java              # Payload JWT (userId, email)
│   └── service/
│       └── JwtService.java          # Servizio gestione JWT (generazione e validazione)
├── mapper/
│   ├── TodoMapper.java              # Interfaccia MapStruct per mapping Todo ↔ DTO
│   └── UserMapper.java              # Interfaccia MapStruct per mapping User ↔ DTO
│   # ⚠️ Le implementazioni sono auto-generate da MapStruct durante la compilazione
│   # 📁 Posizione: target/generated-sources/annotations/com/example/dataware/todolist/mapper/
├── repository/
│   ├── TodoRepository.java          # Repository JPA per Todo
│   └── UserRepository.java          # Repository JPA per User
├── interfaces/
│   ├── AuthService.java             # Interfaccia servizio autenticazione
│   ├── TodoService.java             # Interfaccia servizio todo
│   └── UserService.java             # Interfaccia servizio utente
├── service/
│   ├── AuthServiceImpl.java         # Implementazione servizio autenticazione
│   ├── TodoServiceImpl.java         # Implementazione servizio todo
│   └── UserServiceImpl.java         # Implementazione servizio utente
└── util/
    ├── SuccessResponse.java         # Modello risposta successo
    └── SuccessResponseBuilder.java  # Builder per risposte di successo
```

## 🔐 Sistema di Autenticazione

L'applicazione utilizza **JWT (JSON Web Token)** con sistema di **Access Token** e **Refresh Token** per l'autenticazione stateless:

- Gli endpoint `/auth/**` sono pubblici e non richiedono autenticazione (eccetto `/auth/refresh-token` che richiede un refresh token)
- Tutti gli altri endpoint richiedono un **access token** valido nell'header `Authorization: Bearer <accessToken>`
- Il sistema utilizza due tipi di token:
  - **Access Token**: Token a breve durata per autenticare le richieste API
  - **Refresh Token**: Token a lunga durata per ottenere nuovi access token senza ri-autenticarsi
- Entrambi i token contengono `email` e `userId` dell'utente
- I token utilizzano chiavi segrete separate per maggiore sicurezza
- La sessione è configurata come `STATELESS`
- Due filtri separati gestiscono la validazione: `JwtAccessFilter` per gli access token e `JwtRefreshFilter` per i refresh token

## 🗄️ Modello Dati

### User

- `id` (Long) - Identificatore univoco
- `nome` (String) - Nome utente (min 4 caratteri)
- `email` (String) - Email univoca
- `password` (String) - Password hashata con BCrypt
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
    "todos": [],
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

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
- Restituisce una nuova coppia di access token e refresh token
- Utile quando l'access token è scaduto senza dover effettuare nuovamente il login

### Todo (`/todos`)

**Nota:** Tutti gli endpoint richiedono autenticazione JWT.

#### GET `/todos`

Ottiene tutti i todo dell'utente autenticato.

**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "title": "Fare la spesa",
      "completed": false,
      "user": {
        "id": 1,
        "nome": "Mario Rossi",
        "email": "mario@example.com"
      },
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-01-01T10:00:00Z"
    }
  ],
  "timestamp": "2024-01-01T10:00:00Z"
}
```

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

**Nota:** Tutti gli endpoint richiedono autenticazione JWT.

#### GET `/users/profile`

Ottiene il profilo dell'utente autenticato.

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
    "todos": [
      {
        "id": 1,
        "title": "Fare la spesa",
        "completed": false
      }
    ],
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### DELETE `/users`

Elimina l'account dell'utente autenticato (cascade delete dei todo).

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
```

**Descrizione variabili:**

- `DB_URL` - URL di connessione al database PostgreSQL
- `DB_USERNAME` - Username per il database
- `DB_PASSWORD` - Password per il database
- `JWT_ACCESS_SECRET` - Chiave segreta per la firma degli access token (minimo 32 caratteri)
- `JWT_ACCESS_EXPIRATION` - Durata dell'access token (es: `2h` = 2 ore)
- `JWT_REFRESH_SECRET` - Chiave segreta per la firma dei refresh token (minimo 32 caratteri, diversa dall'access secret)
- `JWT_REFRESH_EXPIRATION` - Durata del refresh token (es: `7d` = 7 giorni)

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

## 🚀 Installazione e Avvio

### Prerequisiti

- Java 17 o superiore
- Maven 3.6+
- PostgreSQL in esecuzione

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

3. **Configurare le variabili d'ambiente**
   Creare il file `.env` nella root del progetto con le credenziali del database e la chiave JWT.

4. **Compilare e avviare l'applicazione**

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

## 🔒 Gestione Errori

L'applicazione utilizza un **GlobalExceptionHandler** che gestisce:

- `ResponseStatusException` - Eccezioni personalizzate con codice HTTP
- `MethodArgumentNotValidException` - Errori di validazione
- `HttpMessageNotReadableException` - Body mancante o non valido
- `MethodArgumentTypeMismatchException` - Tipo parametro non valido
- `Exception` - Errori generici

Tutte le risposte di errore seguono il formato:

```json
{
  "statusCode": 400,
  "error": "Bad Request",
  "message": "Messaggio di errore"
}
```

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
       TodoSimpleResponse toSimpleDTO(Todo todo);
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
- **Tipi annidati**: `User` → `UserSimpleResponse` (genera metodo helper automaticamente)
- **Liste**: `List<Todo>` → `List<TodoSimpleResponse>` (conversione automatica)

Le implementazioni generate sono visibili in `target/generated-sources/annotations/com/example/dataware/todolist/mapper/` dopo la compilazione.

## 🔄 Sistema di Refresh Token

Il progetto implementa un sistema completo di **Access Token** e **Refresh Token** per migliorare la sicurezza e l'esperienza utente.

### Come Funziona

1. **Login**: Quando un utente effettua il login, riceve due token:

   - **Access Token**: Token a breve durata (es: 15 minuti) utilizzato per autenticare le richieste API
   - **Refresh Token**: Token a lunga durata (es: 7 giorni) utilizzato per ottenere nuovi access token

2. **Utilizzo Access Token**: L'access token viene inviato nell'header `Authorization: Bearer <accessToken>` per tutte le richieste API protette.

3. **Refresh Token**: Quando l'access token scade, invece di effettuare nuovamente il login, è possibile utilizzare il refresh token per ottenere una nuova coppia di token tramite l'endpoint `/auth/refresh-token`.

### Architettura dei Filtri

Il sistema utilizza due filtri separati per gestire i diversi tipi di token:

- **JwtAccessFilter**:

  - Valida gli access token per tutti gli endpoint tranne `/auth/**`
  - Estrae `userId` e `email` dal token e li inserisce nel `SecurityContext`
  - Gestisce errori di token scaduto, malformato o mancante

- **JwtRefreshFilter**:
  - Valida i refresh token solo per l'endpoint `/auth/refresh-token`
  - Estrae `userId` e `email` dal refresh token
  - Permette l'accesso all'endpoint di refresh senza access token

### Sicurezza

- **Chiavi Separate**: Access token e refresh token utilizzano chiavi segrete diverse (`JWT_ACCESS_SECRET` e `JWT_REFRESH_SECRET`)
- **Scadenze Diverse**: Access token con durata breve, refresh token con durata più lunga
- **Validazione Separata**: Ogni tipo di token viene validato con la propria chiave segreta
- **Enum TokenType**: Utilizzato per distinguere tra ACCESS e REFRESH token durante la validazione

### Vantaggi

- ✅ **Sicurezza Migliorata**: Access token a breve durata limitano i danni in caso di compromissione
- ✅ **Esperienza Utente**: Nessun bisogno di ri-autenticarsi frequentemente grazie al refresh token
- ✅ **Revoca Implicita**: Quando un refresh token scade, l'utente deve ri-autenticarsi
- ✅ **Separazione delle Responsabilità**: Chiavi e filtri separati per maggiore sicurezza

## 👤 Autore

Progetto sviluppato per la gestione di una lista di attività (todo).
