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
│       └── TodoSimpleResponse.java  # Response todo semplificato
├── entity/
│   ├── BaseEntity.java              # Entità base con id, createdAt, updatedAt
│   ├── User.java                    # Entità utente
│   └── Todo.java                    # Entità todo
├── exception/
│   ├── ErrorResponse.java           # Modello risposta errore
│   └── GlobalExceptionHandler.java  # Gestore globale eccezioni
├── jwt/
│   ├── JwtFilter.java               # Filtro JWT per Spring Security
│   ├── JwtPayload.java              # Payload JWT
│   └── JwtService.java              # Servizio gestione JWT
├── mapper/
│   ├── TodoMapper.java              # Mapper entità -> DTO
│   └── UserMapper.java              # Mapper entità -> DTO
├── repository/
│   ├── TodoRepository.java          # Repository JPA per Todo
│   └── UserRepository.java          # Repository JPA per User
├── service/
│   ├── AuthService.java             # Logica autenticazione
│   ├── TodoService.java             # Logica business todo
│   └── UserService.java             # Logica business utente
└── util/
    ├── SuccessResponse.java         # Modello risposta successo
    └── SuccessResponseBuilder.java  # Builder per risposte di successo
```

## 🔐 Sistema di Autenticazione

L'applicazione utilizza **JWT (JSON Web Token)** per l'autenticazione stateless:

- Gli endpoint `/auth/**` sono pubblici e non richiedono autenticazione
- Tutti gli altri endpoint richiedono un token JWT valido nell'header `Authorization: Bearer <token>`
- Il token contiene `email` e `userId` dell'utente
- La sessione è configurata come `STATELESS`

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
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

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
JWT_SECRET=your_secret_key_min_32_characters
JWT_EXPIRES_IN=86400000
```

**Descrizione variabili:**
- `DB_URL` - URL di connessione al database PostgreSQL
- `DB_USERNAME` - Username per il database
- `DB_PASSWORD` - Password per il database
- `JWT_SECRET` - Chiave segreta per la firma dei JWT (minimo 32 caratteri)
- `JWT_EXPIRES_IN` - Tempo di scadenza del token in millisecondi (es: 86400000 = 24 ore)

### application.properties

Il file `application.properties` è configurato con:

- **Porta server:** 3001
- **Hibernate DDL:** `update` (aggiorna automaticamente lo schema del database)
- **Show SQL:** `true` (mostra le query SQL nei log)
- **Error handling:** Stack trace disabilitato, messaggi abilitati
- **Logging:** DEBUG per il package dell'applicazione, INFO per root

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

## 👤 Autore

Progetto sviluppato per la gestione di una lista di attività (todo).

