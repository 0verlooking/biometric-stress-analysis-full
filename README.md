# Система аналізу біометричних показників стресу

Веб-система для аналізу біометричних показників стресу користувачів з використанням сучасних технологій та принципів проектування програмного забезпечення.

## 📋 Зміст

- [Опис проекту](#опис-проекту)
- [Технології](#технології)
- [Архітектура системи](#архітектура-системи)
- [Швидкий старт](#швидкий-старт)
- [Документація](#документація)
- [Структура проекту](#структура-проекту)
- [API Документація](#api-документація)

## 🎯 Опис проекту

Система призначена для:
- Збору та аналізу біометричних даних користувачів
- Розрахунку рівня стресу на основі комплексних показників
- Надання персоналізованих рекомендацій для зменшення стресу
- Візуалізації історії показників та трендів

### Основні можливості

- **Автентифікація та авторизація** користувачів з JWT токенами
- **Збір біометричних даних**: пульс, тиск, температура, рівень кортизолу, якість сну
- **Аналіз стресу**: автоматичний розрахунок рівня стресу з використанням комплексних алгоритмів
- **Рекомендації**: генерація персоналізованих порад для покращення самопочуття
- **Історія**: перегляд історії вимірювань та аналізів
- **Візуалізація**: графіки та діаграми для відстеження показників у часі

## 🛠 Технології

### Backend
- **Java 17** - мова програмування
- **Spring Boot 3.2.0** - фреймворк для створення веб-додатків
- **Spring Data JPA** - робота з базою даних
- **Spring Security** - безпека та автентифікація
- **PostgreSQL 16** - реляційна база даних
- **JWT (JJWT)** - токени для автентифікації
- **Lombok** - зменшення boilerplate коду
- **MapStruct** - маппінг між DTO та Entity
- **Springdoc OpenAPI** - документація API (Swagger)
- **Maven** - система збірки

### Frontend
- **React 19** - бібліотека для створення UI
- **TypeScript** - типізована надбудова над JavaScript
- **Vite** - швидкий інструмент збірки
- **React Router** - маршрутизація
- **Axios** - HTTP клієнт
- **Recharts** - візуалізація даних
- **Lucide React** - іконки

### DevOps
- **Docker** - контейнеризація
- **Docker Compose** - оркестрація контейнерів
- **Nginx** - веб-сервер для frontend
- **PostgreSQL** - база даних

## 🏗 Архітектура системи

Система побудована з використанням **трирівневої архітектури** (Three-tier architecture):

1. **Presentation Layer (Frontend)** - React додаток
2. **Business Logic Layer (Backend)** - Spring Boot REST API
3. **Data Layer** - PostgreSQL база даних

### Архітектурні принципи

#### SOLID принципи
- **S**ingle Responsibility Principle - кожен клас має одну відповідальність
- **O**pen/Closed Principle - класи відкриті для розширення, закриті для модифікації
- **L**iskov Substitution Principle - об'єкти можуть бути замінені їх підтипами
- **I**nterface Segregation Principle - клієнти не залежать від непотрібних їм інтерфейсів
- **D**ependency Inversion Principle - залежність від абстракцій, а не конкретних реалізацій

#### Патерни проектування
- **Repository Pattern** - абстракція доступу до даних
- **Service Layer Pattern** - бізнес-логіка в окремому шарі
- **DTO Pattern** - передача даних між шарами
- **Factory Pattern** - створення об'єктів
- **Strategy Pattern** - вибір алгоритму аналізу
- **Builder Pattern** - побудова складних об'єктів
- **Dependency Injection** - впровадження залежностей через Spring

Детальний опис реалізації див. у [SOLID.md](docs/SOLID.md) та [DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md)

## 🚀 Швидкий старт

### Передумови

Переконайтеся, що у вас встановлено:
- **Docker** (версія 20.0+)
- **Docker Compose** (версія 2.0+)
- **Git**

### Встановлення та запуск

1. **Клонуйте репозиторій**
```bash
git clone https://github.com/0verlooking/biometric-stress-analysis-full.git
cd biometric-stress-analysis-full
```

2. **Створіть .env файл**
```bash
cp .env.example .env
```

3. **Запустіть всі сервіси через Docker Compose**
```bash
docker-compose up --build
```

Ця команда:
- Створить та запустить PostgreSQL базу даних
- Зберіть та запустить Backend (Spring Boot)
- Зберіть та запустить Frontend (React + Nginx)

4. **Доступ до додатку**

Після успішного запуску (це може зайняти 2-3 хвилини):

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### Зупинка системи

```bash
docker-compose down
```

### Повне очищення (включаючи дані БД)

```bash
docker-compose down -v
```

## 📚 Документація

Повна документація знаходиться в папці [docs/](docs/):

1. **[TECHNICAL_SPECIFICATION.md](docs/TECHNICAL_SPECIFICATION.md)** - Технічне завдання (5 балів)
2. **[USE_CASE.md](docs/USE_CASE.md)** - Use Case діаграми (5 балів)
3. **[DATABASE.md](docs/DATABASE.md)** - ORM та структура БД (10 балів)
4. **[WIREFRAMES.md](docs/WIREFRAMES.md)** - Wireframes інтерфейсу (10 балів)
5. **[FRONTEND.md](docs/FRONTEND.md)** - Документація Front-End (10 балів)
6. **[SOLID.md](docs/SOLID.md)** - Реалізація SOLID принципів (15 балів)
7. **[DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md)** - Патерни проектування (20 балів)
8. **[SEQUENCE_DIAGRAMS.md](docs/SEQUENCE_DIAGRAMS.md)** - Sequence діаграми (10 балів)
9. **[DEPLOYMENT.md](docs/DEPLOYMENT.md)** - Docker та розгортання (10 балів)

## 📁 Структура проекту

```
biometric-stress-analysis-full/
├── backend/                    # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/biometric/stressanalysis/
│   │   │   │       ├── controller/      # REST Controllers
│   │   │   │       ├── service/         # Business Logic
│   │   │   │       ├── repository/      # Data Access
│   │   │   │       ├── entity/          # JPA Entities
│   │   │   │       ├── dto/             # Data Transfer Objects
│   │   │   │       ├── config/          # Configuration
│   │   │   │       ├── security/        # Security
│   │   │   │       ├── exception/       # Exception Handling
│   │   │   │       └── pattern/         # Design Patterns
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # React Frontend
│   ├── src/
│   │   ├── components/         # React Components
│   │   ├── pages/              # Page Components
│   │   ├── services/           # API Services
│   │   ├── contexts/           # React Contexts
│   │   ├── types/              # TypeScript Types
│   │   └── main.tsx            # Entry Point
│   ├── public/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.ts
│
├── docs/                       # Documentation
│   ├── TECHNICAL_SPECIFICATION.md
│   ├── USE_CASE.md
│   ├── DATABASE.md
│   ├── WIREFRAMES.md
│   ├── FRONTEND.md
│   ├── SOLID.md
│   ├── DESIGN_PATTERNS.md
│   ├── SEQUENCE_DIAGRAMS.md
│   └── DEPLOYMENT.md
│
├── docker-compose.yml          # Docker Compose Configuration
├── init-db.sql                 # Database Initialization
├── .env                        # Environment Variables
├── .env.example                # Example Environment Variables
├── .gitignore
└── README.md                   # This file
```

## 🔌 API Документація

### Основні ендпоінти

#### Автентифікація
- `POST /api/auth/register` - Реєстрація користувача
- `POST /api/auth/login` - Вхід користувача
- `GET /api/auth/me` - Отримання інформації про поточного користувача

#### Біометричні дані
- `POST /api/biometric` - Створення нових біометричних даних
- `GET /api/biometric/user/{userId}` - Отримання даних користувача
- `GET /api/biometric/{id}` - Отримання даних за ID
- `DELETE /api/biometric/{id}` - Видалення даних

#### Аналіз стресу
- `POST /api/stress/analyze/{biometricDataId}` - Аналіз біометричних даних
- `GET /api/stress/user/{userId}` - Отримання всіх аналізів користувача
- `GET /api/stress/{id}` - Отримання аналізу за ID
- `GET /api/stress/latest/{userId}` - Останній аналіз користувача

#### Користувачі
- `GET /api/users/{id}` - Отримання користувача за ID
- `PUT /api/users/{id}` - Оновлення профілю користувача
- `DELETE /api/users/{id}` - Видалення користувача

Повна документація API доступна через Swagger UI: http://localhost:8080/swagger-ui.html

## 🧪 Тестування

### Backend
```bash
cd backend
mvn test
```

### Frontend
```bash
cd frontend
npm test
```

## 👥 Автори

- Студент: [Ваше ім'я]
- Університет: [Назва університету]
- Курс: Курсова робота

## 📄 Ліцензія

Цей проект створено для навчальних цілей.

## 🤝 Внесок

Проект є курсовою роботою і не приймає зовнішні внески.

## 📞 Контакти

Для питань щодо проекту звертайтесь до викладача курсу.

---

**Примітка**: Для захисту курсової роботи необхідно ознайомитись з усією документацією в папці `docs/`.
