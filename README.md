# app-microservice-auth

Microservicio de autenticación para el sistema de paisajes. Maneja el registro y login de usuarios generando tokens JWT.

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring WebFlux
- Spring Security
- R2DBC + PostgreSQL
- JWT (jjwt)
- BCrypt

## Requisitos previos

- Java 17
- Maven
- PostgreSQL corriendo (ver infraestructura)
- Variables de entorno configuradas

## Variables de entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `JWT_KEY` | Clave secreta para firmar tokens JWT en Base64 | `MsUEV8IkLNB7...` |
| `DB_AUTH_HOST` | Host de la base de datos | `localhost` |
| `DB_AUTH_PORT` | Puerto de la base de datos | `5432` |
| `DB_AUTH_NAME` | Nombre de la base de datos | `authdb` |
| `DB_AUTH_USER` | Usuario de la base de datos | `brayanpv` |
| `DB_AUTH_PASSWORD` | Contraseña de la base de datos | `brayanpv` |

> Las variables con valor por defecto en el `application.yml` funcionan en local si no se configuran explícitamente, excepto `JWT_KEY` que debe definirse siempre.

## Cómo ejecutar localmente

1. Levantar la infraestructura:
```bash
cd infrastructure
docker-compose up -d
```

2. Configurar las variables de entorno en `~/.bashrc`:
```bash
export JWT_KEY=tu_clave_base64
export DB_AUTH_USER=brayanpv
export DB_AUTH_PASSWORD=brayanpv
source ~/.bashrc
```

3. Correr el servicio:
```bash
cd app-microservice-auth
mvn spring-boot:run
```

El servicio queda disponible en `http://localhost:8000/app-microservice-auth`

## Endpoints

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/app-microservice-auth/signUp` | Registro de usuario | No |
| `POST` | `/app-microservice-auth/login` | Login y generación de JWT | No |

### POST /signUp

**Request:**
```json
{
  "username": "brayan",
  "email": "brayan@gmail.com",
  "password": "123456"
}
```

**Response:**
```json
{
  "dateTime": "2026-03-28T20:00:00",
  "code": 200,
  "data": {
    "username": "brayan"
  }
}
```

### POST /login

**Request:**
```json
{
  "username": "brayan",
  "password": "123456"
}
```

**Response:**
```json
{
  "dateTime": "2026-03-28T20:00:00",
  "code": 200,
  "data": {
    "jwtToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

## Estructura del JWT

El token generado contiene los siguientes claims:

```json
{
  "sub": "1",
  "id": "1",
  "email": "brayan@gmail.com",
  "iat": 1234567890,
  "exp": 1234567890
}
```

## Base de datos

El servicio se conecta a `authdb` en PostgreSQL. La tabla se crea con el script de inicialización del docker-compose:

```sql
CREATE TABLE IF NOT EXISTS au_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
```

## Notas

- Las contraseñas se almacenan hasheadas con BCrypt
- El token JWT tiene una expiración de 24 horas (86400000 ms)
- Para producción, nunca dejes `JWT_KEY` con valor por defecto

### Forgot password
```
Usuario ingresa su email
↓
Auth Service genera código de 6 dígitos
lo guarda en BD con expiración de 15 minutos
↓
Resend envía el email con el código
↓
Usuario ingresa el código en el frontend
↓
Auth Service valida el código
Si válido → permite cambiar la contraseña
```

```sql
CREATE TABLE password_reset_token (
                                      id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      user_id     BIGINT NOT NULL,
                                      code        VARCHAR(6) NOT NULL,
                                      used        BOOLEAN NOT NULL DEFAULT FALSE,
                                      expires_at  TIMESTAMP NOT NULL,
                                      created_at  TIMESTAMP NOT NULL DEFAULT now()
);
```
- Endpoints:
- POST /forgot-password   → recibe email, genera código, envía email
- POST /verify-code       → recibe email + código, valida
- POST /reset-password    → recibe email + código + nueva contraseña