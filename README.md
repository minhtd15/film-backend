# Film Archive — Backend

REST API for a personal film photography archive. Built with Java Spring Boot, MySQL, and MinIO for object storage.

## Tech Stack

- **Java 21** + **Spring Boot 3.3**
- **MySQL 8** — relational database
- **MinIO** — self-hosted object storage for photos
- **Liquibase** — database migration management
- **Docker Compose** — local infrastructure

## Architecture

```
Client
  │
  ▼
Spring Boot API (:8080)
  ├── MySQL         — stores metadata (film rolls, photos, cameras)
  └── MinIO         — stores photo files
```

## Data Model

```
cameras
  └── id, name

film_rolls
  └── id, name, manufacturer, iso, film_format, color_type, date, date_type, camera_id

photos
  └── id, film_roll_id, storage_url, storage_key, caption, location, width, height
```

## API Endpoints

### Public (no auth required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/film-rolls` | List all film rolls |
| GET | `/api/film-rolls/{id}` | Get a film roll |
| GET | `/api/film-rolls/{id}/photos` | Get photos of a roll (paginated) |
| GET | `/api/photos?page=0&size=20` | Get all photos (paginated) |
| GET | `/api/cameras` | List all cameras |

### Protected (requires `X-API-Key` header)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/film-rolls` | Create a film roll |
| PUT | `/api/film-rolls/{id}` | Update a film roll |
| DELETE | `/api/film-rolls/{id}` | Delete a film roll |
| POST | `/api/photos/film-rolls/{id}` | Upload a photo |
| DELETE | `/api/photos/{id}` | Delete a photo |
| POST | `/api/cameras` | Create a camera |
| DELETE | `/api/cameras/{id}` | Delete a camera |

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker + Docker Compose

### 1. Configure

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

Edit `application.yml` with your credentials.

### 2. Start infrastructure

```bash
docker compose up mysql minio -d
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Liquibase will automatically create the database tables on first run.

## Example Requests

**Upload a photo**
```bash
curl -X POST http://localhost:8080/api/photos/film-rolls/1 \
  -H "X-API-Key: your-api-key" \
  -F "file=@photo.jpg" \
  -F "caption=Sunset" \
  -F "location=Ha Noi"
```

**Get all photos (page 2)**
```bash
curl http://localhost:8080/api/photos?page=1&size=20
```

**Create a film roll**
```bash
curl -X POST http://localhost:8080/api/film-rolls \
  -H "X-API-Key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kodak Portra 400",
    "manufacturer": "Kodak",
    "iso": 400,
    "filmFormat": "35mm",
    "colorType": "color",
    "date": "2025-04-01",
    "dateType": "in",
    "cameraId": 1
  }'
```
