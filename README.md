# The Ledger — Money Expense Tracker (Spring Boot)

A full-stack expense tracker: **Spring Boot** REST API backend, a real
**SQL database** (H2, file-based — no separate DB server to install),
**Spring Security login** so multiple people can each have their own
private set of expenses, and the same browser front end from before.

Weekly / monthly / quarterly / yearly views, category-wise breakdowns
(Shopping, Traveling, Eating, Bills, Health, Entertainment, Education,
Other, or any custom category), all backed by persistent SQL storage,
all scoped to whoever is currently signed in.

## Technology stack

| Layer | Technology |
|---|---|
| Backend framework | Spring Boot 3.2.5 (Java 17) |
| Web layer | Spring MVC (`@RestController`), embedded Tomcat |
| Authentication | Spring Security — session-based form login, BCrypt password hashing |
| Data access | Spring Data JPA (Hibernate under the hood) |
| Database | H2 — a real embedded SQL database, stored as a file (`data/ledgerdb.mv.db`) |
| Validation | Jakarta Bean Validation (`@NotNull`, `@DecimalMin`, etc.) |
| Server-rendered pages | Thymeleaf (login + register only) |
| Frontend (the ledger itself) | Plain HTML/CSS/JavaScript, served as static files |
| Build tool | Maven |

## How the login system works

- Every expense belongs to exactly one account (`AppUser`) via a foreign
  key. Every database query in `ExpenseService` is scoped to
  "whoever is currently logged in" — there's no code path that returns
  or edits another account's data.
- Passwords are hashed with BCrypt (`PasswordEncoder`) before being
  stored — the plain-text password is never saved anywhere.
- Visiting any page other than `/login` or `/register` while logged out
  redirects you to the login page automatically (enforced by
  `SecurityConfig`, not by the front-end JavaScript).
- **A demo account is created automatically the first time you run the
  app** (only if the accounts table is completely empty):
  - username: `demo`
  - password: `demo1234`

  Use it to try the app immediately, or click "Create an account" on the
  login page to register your own.
- Categories (Shopping, Eating, etc.) are shared across all accounts —
  only the expenses themselves are private per user. That's a deliberate
  simplification; say the word if you'd rather each user have fully
  separate categories too.

### A security simplification worth knowing about

CSRF protection is disabled (`SecurityConfig`) so the plain-JavaScript
front end can call the API with `fetch()` without needing to forward a
CSRF token. Session cookies still keep every request correctly scoped to
the logged-in user, so this doesn't expose other people's data — but
CSRF protection is a standard defense against a *different* class of
attack (a malicious site tricking your browser into submitting a request
on your behalf while you're logged in here). Fine for personal/local use
or a small trusted group; if you deploy this somewhere with untrusted
users, ask me to wire up CSRF tokens between the front end and API — it's
a contained change (add a meta tag with the token, read it in the
`fetch()` calls, stop disabling CSRF in `SecurityConfig`).

## Project structure

```
expense-tracker-springboot/
├── pom.xml
├── src/main/java/com/expensetracker/
│   ├── LedgerApplication.java
│   ├── model/
│   │   ├── Expense.java              (@Entity — "expenses" table, owned by an AppUser)
│   │   ├── Category.java             (@Entity — "categories" table)
│   │   └── AppUser.java              (@Entity — "app_users" table: login accounts)
│   ├── repository/
│   │   ├── ExpenseRepository.java    (owner-scoped queries)
│   │   ├── CategoryRepository.java
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── SecurityConfig.java       (access rules, login/logout URLs)
│   │   └── CustomUserDetailsService.java (loads AppUser for Spring Security)
│   ├── dto/
│   │   ├── ExpenseRequest.java
│   │   ├── ExpensesResponse.java
│   │   ├── SingleExpenseResponse.java
│   │   ├── ImportRequest.java
│   │   └── RegisterRequest.java
│   ├── service/
│   │   ├── ExpenseService.java       (every method scoped to current user)
│   │   └── CategoryService.java
│   ├── controller/
│   │   ├── ExpenseController.java    (REST API — /api/expenses, /api/import)
│   │   ├── AuthController.java       (/api/me — who's logged in)
│   │   ├── AuthViewController.java   (/login page)
│   │   └── RegistrationController.java (/register page + form handling)
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ApiError.java
│   └── config/
│       └── DataInitializer.java      (seeds default categories + demo account)
├── src/main/resources/
│   ├── application.properties
│   ├── templates/
│   │   ├── login.html                (Thymeleaf — sign-in form)
│   │   └── register.html             (Thymeleaf — sign-up form)
│   └── static/index.html             (the ledger — requires login to reach)
└── data/                              (created automatically; holds the H2 database file)
```

## Requirements

- **JDK 17 or newer** — check with `java -version`
- **Maven 3.6+** — check with `mvn -version`

## Run it

```bash
cd expense-tracker-springboot
mvn spring-boot:run
```

Or build a runnable jar and run that directly:

```bash
mvn clean package
java -jar target/ledger-expense-tracker.jar
```

Then open **http://localhost:8080** — you'll land on the login page.
Sign in with `demo` / `demo1234`, or click through to register your own
account.

### A note on testing

I built and reviewed every file carefully — checked brace balance across
every class, re-validated the front-end JavaScript's syntax after every
edit, and traced through the login → session → per-user query flow by
hand. But this sandbox doesn't have Maven or a JDK compiler available (no
internet access to fetch either), so I wasn't able to run
`mvn clean package` myself before handing this to you. If you hit a
compile error, paste it back to me and I'll fix it immediately.

## Exploring the database directly

Spring Boot's H2 console is enabled at **http://localhost:8080/h2-console**
while the app is running (this route is intentionally left open, unauthenticated,
for convenience during development — see the note below). Connect with:
- JDBC URL: `jdbc:h2:file:./data/ledgerdb`
- User: `sa`
- Password: *(leave blank)*

```sql
SELECT u.username, e.category, SUM(e.amount) AS total
FROM expenses e
JOIN app_users u ON e.user_id = u.id
GROUP BY u.username, e.category
ORDER BY u.username, total DESC;
```

**Before deploying anywhere public**, either disable the H2 console
(`spring.h2.console.enabled=false` in `application.properties`) or add
authentication to it — right now anyone who reaches `/h2-console` can
browse the raw database, including other users' data and password
hashes.

## API reference

| Method | Route | Auth required? | Description |
|---|---|---|---|
| GET | `/login` | No | Login page |
| POST | `/login` | No | Submits credentials (handled by Spring Security) |
| GET | `/register` | No | Sign-up page |
| POST | `/register` | No | Creates a new account |
| POST | `/logout` | Yes | Ends the session |
| GET | `/api/me` | Yes | `{username, displayName}` for whoever's logged in |
| GET | `/api/expenses` | Yes | Current user's expenses + categories |
| GET | `/api/expenses?period=week` | Yes | This week (Mon–Sun) |
| GET | `/api/expenses?period=month&year=2026&month=6` | Yes | A specific month |
| GET | `/api/expenses?period=quarter&year=2026&quarter=2` | Yes | A specific quarter (1–4) |
| GET | `/api/expenses?period=year&year=2026` | Yes | A specific year |
| GET | `/api/expenses?from=2026-01-01&to=2026-03-31` | Yes | Custom date range |
| POST | `/api/expenses` | Yes | Add one — body: `{amount, category, note, date}` |
| PUT | `/api/expenses/{id}` | Yes | Update one (must be your own) |
| DELETE | `/api/expenses/{id}` | Yes | Delete one (must be your own) |
| POST | `/api/import` | Yes | Replace all of *your* data |
| GET | `/api/health` | Yes | Liveness check |

`date` must be `yyyy-MM-dd`. All responses are JSON. Any "Yes" route
redirects to `/login` if you're not authenticated (or returns 401/403 for
API calls made without a valid session).

## Switching to a different database

Swapping H2 for MySQL or PostgreSQL only touches two files — the rest of
the app doesn't change, because Spring Data JPA generates SQL for
whichever database you configure.

### MySQL

1. In `pom.xml`, remove (or comment out) the `h2` dependency and uncomment
   the `mysql-connector-j` block already included in the file.
2. In `application.properties`, replace the H2 lines with:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ledger?createDatabaseIfNotExist=true
   spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```
3. Make sure a MySQL server is running locally (or point at a hosted one).

### PostgreSQL

1. Add to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
2. In `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ledger
   spring.datasource.driverClassName=org.postgresql.Driver
   spring.datasource.username=postgres
   spring.datasource.password=yourpassword
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   ```

`spring.jpa.hibernate.ddl-auto=update` auto-creates the `expenses`,
`categories`, and `app_users` tables in the new database the first time
you run the app — no manual `CREATE TABLE` needed.

## Deploying publicly

I don't have the ability to deploy this myself (no hosting account or
live internet access from this sandbox). Once you've confirmed it runs
locally, two free, Java-friendly options:

- **Render.com** — "New → Web Service", build command
  `mvn clean package -DskipTests`, start command
  `java -jar target/ledger-expense-tracker.jar`
- **Railway.app** — auto-detects Maven projects; same build/start commands
  as above if it doesn't infer them automatically

Before going public: disable the H2 console (see above), and change the
demo account's password or delete it entirely. If you want the database
to survive redeploys (not just restarts), attach a persistent volume/disk
on the host, or switch to a hosted MySQL/PostgreSQL instance using the
steps above.

"# Expense-Tracker" 
