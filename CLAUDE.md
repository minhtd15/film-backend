# Film Archive — Backend

REST API for a personal film photography archive. Java Spring Boot + MySQL + MinIO.

## Stack
- Java 21, Spring Boot 3.3, Maven
- Spring Security + JWT (jjwt 0.12.6)
- MySQL 8, MinIO 8.5.11, Liquibase
- Lombok, Bean Validation
- Port: 8080

## Project Structure
```
config/
  SecurityConfig.java       — Spring Security, CORS, stateless JWT
  MinioConfig.java          — MinioClient bean
  ApplicationConfig.java    — PasswordEncoder, AuthenticationManager
  CorsConfig.java           — CORS via WebMvcConfigurer
controller/
  AuthController.java       — /api/auth/register, /api/auth/login
  FilmRollController.java   — CRUD film rolls + photos per roll
  PhotoController.java      — paginated photos, upload, delete
  CameraController.java     — CRUD cameras
filter/
  JwtAuthFilter.java        — validates Bearer token
  ApiKeyFilter.java         — X-API-Key fallback for write ops
model/
  User.java                 — id, email, password, role (implements UserDetails)
  FilmRoll.java             — id, name, manufacturer, iso, filmFormat, colorType, date, dateType, cameraId
  Photo.java                — id, filmRollId, storageUrl, storageKey, caption, location, width, height
  Camera.java               — id, name
service/
  AuthService.java          — register/login → JWT
  JwtService.java           — generate/validate JWT
  FilmRollService.java      — CRUD + creates MinIO folder on roll creation
  PhotoService.java         — upload (auto-reads image dimensions) + delete
  MinioService.java         — upload, delete, create folder in MinIO
  CameraService.java        — CRUD
dto/
  FilmRollRequest.java      — cameraId is @NotNull (required field)
  CameraRequest.java, LoginRequest.java, RegisterRequest.java, AuthResponse.java
repository/
  FilmRollRepository.java, PhotoRepository.java, CameraRepository.java, UserRepository.java
```

## Database (MySQL)
- DB name: film_archive
- **Never add FK constraints on the DB** — relationships are logical only
- **All schema changes must go through Liquibase** — never use ddl-auto: create/update
- Migration files: src/main/resources/db/changelog/changes/
  - 001-create-cameras.yaml
  - 002-create-film-rolls.yaml
  - 003-create-photos.yaml
  - 004-create-users.yaml
- New migration: add 00N-description.yaml and include in db.changelog-master.yaml

## Schema
```
cameras:    id, name, created_at
film_rolls: id, name, manufacturer, iso, film_format,
            color_type ENUM(color/black_and_white/slide),
            date, date_type ENUM(in/out), camera_id, created_at
photos:     id, film_roll_id, storage_url, storage_key,
            caption, location, width, height, created_at
users:      id, email (unique), password, role (default USER), created_at
```

## Auth
- POST /api/auth/register + /api/auth/login → returns JWT token
- Write ops: Authorization: Bearer <token>
- Fallback: X-API-Key: tongminh1510 (for direct API calls without JWT)
- GET /api/** — fully public, no auth

## API Endpoints
```
Public GET:  /api/film-rolls, /api/film-rolls/{id}, /api/film-rolls/{id}/photos
             /api/photos, /api/film-rolls/photo-counts, /api/cameras
Protected:   POST/PUT/DELETE /api/film-rolls, /api/cameras
             POST /api/photos/film-rolls/{id}  (multipart: file, caption?, location?)
             DELETE /api/photos/{id}
Auth:        POST /api/auth/register, POST /api/auth/login
```

## MinIO
- Bucket: film-archive (public read)
- storageKey: UUID.ext — stored in photos table
- storageUrl: endpoint/bucket/key
- On film roll create: auto-creates folder {name-slug}/ in MinIO

## Local Dev Credentials
- MySQL: root / tongminh1510, DB: film_archive, port 3306
- MinIO: minhtong / tongminh1510, console http://localhost:9001, port 9000
- API Key: tongminh1510
- Docker: docker compose up mysql minio -d (from /film-backend/)

## Conventions
- No FK constraints on DB — ever
- application.yml is gitignored — copy from application.yml.example
- Do not add comments unless logic is non-obvious
- Throw ResponseStatusException for user-facing errors, not RuntimeException
- Do not create new files unless necessary — prefer editing existing
