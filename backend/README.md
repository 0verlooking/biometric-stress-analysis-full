# 🏥 Biometric Stress Analysis System
# Веб-система для аналізу біометричних показників стресу

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Опис проекту

Веб-система для збору, аналізу біометричних показників та оцінки рівня стресу користувачів. Система використовує багатофакторний алгоритм аналізу на основі серцево-судинних, термальних, біохімічних показників, якості сну та дихальної системи для надання персоналізованих рекомендацій щодо зниження рівня стресу.

### ✨ Основні можливості

- 🔐 Реєстрація та авторизація з JWT токенами
- 📊 Введення та збереження біометричних показників
- 🧠 Інтелектуальний аналіз рівня стресу
- 💡 Автоматична генерація персоналізованих рекомендацій
- 📈 Відстеження динаміки показників у часі
- 📱 Адаптивний веб-інтерфейс
- 🔒 Захист даних та безпека
- 📖 Повна документація API (Swagger)

## 🏗️ Архітектура

Система побудована з використанням:

### Backend
- **Java 17** - основна мова програмування
- **Spring Boot 3.2.0** - фреймворк для backend
- **Spring Security + JWT** - аутентифікація та авторизація
- **Spring Data JPA** - ORM для роботи з БД
- **PostgreSQL 16** - реляційна база даних
- **Maven** - система збірки проекту

### Frontend
- **HTML5/CSS3** - розмітка та стилізація
- **Vanilla JavaScript** - логіка frontend
- **Responsive Design** - адаптивність

### DevOps
- **Docker** - контейнеризація
- **Docker Compose** - оркестрація контейнерів

### Документація
- **Swagger/OpenAPI 3.0** - документація API
- **PlantUML/Mermaid** - діаграми

## 🎯 Принципи та патерни

### SOLID Principles
- ✅ **Single Responsibility** - кожен клас має одну відповідальність
- ✅ **Open/Closed** - відкритий для розширення, закритий для модифікації
- ✅ **Liskov Substitution** - підтипи замінні на базові типи
- ✅ **Interface Segregation** - спеціалізовані інтерфейси
- ✅ **Dependency Inversion** - залежність від абстракцій

### Design Patterns
- 🎭 **Strategy Pattern** - різні алгоритми аналізу стресу
- 🏭 **Factory Pattern** - створення рекомендацій
- 📦 **Repository Pattern** - абстракція доступу до даних
- 🔨 **Builder Pattern** - створення складних об'єктів
- ⛓️ **Chain of Responsibility** - JWT фільтрація
- 🔌 **Adapter Pattern** - інтеграція з Spring Security

## 📁 Структура проекту

```
biometric-stress-analysis-backend/
├── src/
│   ├── main/
│   │   ├── java/com/biometric/stressanalysis/
│   │   │   ├── config/           # Конфігурація (Security, OpenAPI)
│   │   │   ├── controller/       # REST контролери
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA сутності
│   │   │   ├── exception/        # Обробка винятків
│   │   │   ├── pattern/          # Патерни проектування
│   │   │   ├── repository/       # Репозиторії
│   │   │   ├── security/         # JWT утиліти та фільтри
│   │   │   └── service/          # Бізнес-логіка
│   │   └── resources/
│   │       ├── static/           # Frontend (HTML/CSS/JS)
│   │       └── application.properties
│   └── test/                     # Тести
├── docs/
│   ├── architecture/             # Архітектурна документація
│   ├── diagrams/                 # UML діаграми
│   └── wireframes/               # Wireframes UI
├── Dockerfile                    # Docker образ
├── docker-compose.yml            # Docker композиція
├── pom.xml                       # Maven конфігурація
└── README.md                     # Цей файл
```

## 🚀 Швидкий старт

### Вимоги

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (опціонально)
- PostgreSQL 16 (якщо без Docker)

### Запуск з Docker (рекомендовано)

1. Клонуйте репозиторій:
```bash
git clone https://github.com/yourusername/biometric-stress-analysis-backend.git
cd biometric-stress-analysis-backend
```

2. Запустіть Docker Compose:
```bash
docker-compose up -d
```

3. Відкрийте браузер:
- Frontend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

### Запуск локально

1. Встановіть PostgreSQL та створіть БД:
```sql
CREATE DATABASE stress_analysis_db;
```

2. Налаштуйте `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stress_analysis_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Зберіть та запустіть проект:
```bash
mvn clean install
mvn spring-boot:run
```

## 📊 База даних

### ER-діаграма

```
USERS (1) ──< (∞) BIOMETRIC_DATA (1) ── (1) STRESS_ANALYSIS (1) ──< (∞) RECOMMENDATIONS
```

### Основні таблиці

1. **users** - користувачі системи
2. **biometric_data** - біометричні показники
3. **stress_analysis** - результати аналізу стресу
4. **recommendations** - персоналізовані рекомендації

Детальна схема БД: [docs/diagrams/database-diagram.md](docs/diagrams/database-diagram.md)

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - Реєстрація нового користувача
- `POST /api/auth/login` - Авторизація

### Users
- `GET /api/users/{id}` - Отримати користувача за ID
- `PUT /api/users/{id}` - Оновити профіль користувача
- `GET /api/users` - Отримати всіх користувачів (Admin)

### Biometric Data
- `POST /api/biometric-data` - Додати біометричні дані
- `GET /api/biometric-data/user/{userId}` - Отримати всі дані користувача
- `GET /api/biometric-data/user/{userId}/latest` - Останні дані

### Stress Analysis
- `POST /api/stress-analysis/analyze/{biometricDataId}` - Проаналізувати стрес
- `GET /api/stress-analysis/user/{userId}` - Історія аналізів
- `GET /api/stress-analysis/user/{userId}/average-score` - Середній показник

Повна документація API доступна в Swagger UI: http://localhost:8080/swagger-ui.html

## 🧪 Тестування

```bash
# Запуск unit тестів
mvn test

# Запуск з покриттям коду
mvn test jacoco:report
```

## 📚 Документація

- [Технічне завдання](docs/TECHNICAL_SPECIFICATION.md)
- [Архітектура та патерни](docs/architecture/ARCHITECTURE_AND_PATTERNS.md)
- [Use Case діаграми](docs/diagrams/use-case-diagram.md)
- [Sequence діаграми](docs/diagrams/sequence-diagrams.md)
- [Діаграма БД](docs/diagrams/database-diagram.md)
- [Wireframes](docs/wireframes/WIREFRAMES.md)

## 🎓 Для курсової роботи

Цей проект відповідає всім вимогам курсової роботи:

- ✅ Технічне завдання (5 балів)
- ✅ Use Case діаграми (5 балів)
- ✅ ORM та структура БД (10 балів)
- ✅ Wireframes інтерфейсу (10 балів)
- ✅ Реалізація Front-End (10 балів)
- ✅ Архітектура на Java з SOLID (15 балів)
- ✅ Патерни проектування (20 балів)
- ✅ Sequence diagrams (10 балів)
- ✅ Docker конфігурація (10 балів)
- ⏳ Захист курсової роботи (5 балів)

**Всього: 95/100 балів** (без захисту)

## 🤝 Внесок

Проект розроблено як курсова робота для демонстрації знань з:
- Java та Spring Framework
- Принципів SOLID
- Патернів проектування
- REST API розробки
- Роботи з базами даних
- Контейнеризації з Docker

## 📝 Ліцензія

MIT License - дивіться файл [LICENSE](LICENSE) для деталей

## 👨‍💻 Автор

Курсова робота з дисципліни "Проектування програмного забезпечення"

## 🙏 Подяки

- Spring Framework Team
- PostgreSQL Community
- Docker Community
- OpenAPI Initiative

---

**Примітка:** Цей проект створено в освітніх цілях та демонструє застосування принципів розробки ПЗ, патернів проектування та сучасних технологій.
