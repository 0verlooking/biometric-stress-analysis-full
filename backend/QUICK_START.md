# 🚀 Швидкий старт

## Варіант 1: Запуск через Docker (Рекомендовано)

```bash
# 1. Запустіть Docker Compose (PostgreSQL + Backend)
docker-compose up --build

# Очікуйте повідомлення: "Started BiometricStressAnalysisApplication"
# Це може зайняти 2-3 хвилини при першому запуску
```

Після успішного запуску:
- **Frontend**: http://localhost:8080
- **Swagger API**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Варіант 2: Локальний запуск

### Крок 1: Встановіть PostgreSQL

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Windows:**
- Завантажте з https://www.postgresql.org/download/windows/
- Встановіть PostgreSQL 16

**macOS:**
```bash
brew install postgresql@16
brew services start postgresql@16
```

### Крок 2: Створіть базу даних

```bash
# Увійдіть в PostgreSQL
sudo -u postgres psql

# Створіть базу даних
CREATE DATABASE stress_analysis_db;

# Створіть користувача (опціонально)
CREATE USER stressuser WITH PASSWORD 'stresspass';
GRANT ALL PRIVILEGES ON DATABASE stress_analysis_db TO stressuser;

# Вийдіть
\q
```

### Крок 3: Налаштуйте application.properties (якщо потрібно)

Якщо ви змінили пароль PostgreSQL, відредагуйте файл:
`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stress_analysis_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

### Крок 4: Зберіть та запустіть проект

```bash
# Збірка проекту (перший раз може зайняти час)
mvn clean install

# Запуск Spring Boot
mvn spring-boot:run
```

**Або через JAR файл:**
```bash
mvn clean package
java -jar target/stress-analysis-1.0.0.jar
```

## Перевірка роботи

1. Відкрийте браузер: http://localhost:8080
2. Зареєструйте нового користувача
3. Увійдіть в систему
4. Додайте біометричні дані
5. Подивіться аналіз стресу

## Тестування API через Swagger

1. Відкрийте: http://localhost:8080/swagger-ui.html
2. Знайдіть `/api/auth/register`
3. Натисніть "Try it out"
4. Введіть дані:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```
5. Натисніть "Execute"
6. Скопіюйте `token` з відповіді
7. Натисніть кнопку "Authorize" вгорі
8. Введіть: `Bearer YOUR_TOKEN`
9. Тепер можете використовувати всі endpoints!

## Зупинка проекту

**Docker:**
```bash
docker-compose down
```

**Локально:**
- Натисніть `Ctrl+C` в терміналі

## Видалення даних

**Docker (повне очищення):**
```bash
docker-compose down -v
```

**Локально:**
```bash
sudo -u postgres psql -c "DROP DATABASE stress_analysis_db;"
sudo -u postgres psql -c "CREATE DATABASE stress_analysis_db;"
```

## Troubleshooting

### Помилка "Connection refused" або 500 error

**Причина:** База даних не запущена або неправильні credentials

**Рішення:**
```bash
# Перевірте статус PostgreSQL
sudo systemctl status postgresql

# Якщо не запущена
sudo systemctl start postgresql

# Перевірте чи існує база даних
sudo -u postgres psql -l | grep stress_analysis
```

### Помилка "Port 8080 already in use"

**Рішення:**
```bash
# Знайдіть процес на порту 8080
sudo lsof -i :8080

# Зупиніть процес (замість PID підставте номер процесу)
kill -9 PID

# Або змініть порт в application.properties
server.port=8081
```

### Maven build fails

**Рішення:**
```bash
# Очистіть Maven cache
mvn clean

# Видаліть .m2 репозиторій і спробуйте знову
rm -rf ~/.m2/repository
mvn clean install
```

### Docker build fails

**Рішення:**
```bash
# Очистіть Docker cache
docker system prune -a

# Rebuild без cache
docker-compose build --no-cache
docker-compose up
```

## Корисні команди

```bash
# Перегляд логів Docker
docker-compose logs -f backend

# Перегляд логів PostgreSQL
docker-compose logs -f postgres

# Перезапуск тільки backend
docker-compose restart backend

# Підключення до PostgreSQL в Docker
docker-compose exec postgres psql -U postgres -d stress_analysis_db
```

## Доступ до бази даних

**GUI клієнти:**
- DBeaver: https://dbeaver.io/
- pgAdmin: https://www.pgadmin.org/

**Параметри підключення:**
- Host: localhost
- Port: 5432
- Database: stress_analysis_db
- Username: postgres
- Password: postgres

## Наступні кроки

1. ✅ Зареєструйте користувача
2. ✅ Увійдіть в систему
3. ✅ Додайте біометричні дані
4. ✅ Подивіться аналіз
5. ✅ Перевірте рекомендації
6. ✅ Експлоруйте API через Swagger

---

**Потрібна допомога?** Перевірте логи або створіть issue в репозиторії.
