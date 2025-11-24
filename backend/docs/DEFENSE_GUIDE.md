# Керівництво для захисту курсової роботи
# Веб-система для аналізу біометричних показників стресу

## 🎯 Презентація проекту (5-7 хвилин)

### 1. Вступ (1 хвилина)

**Тема:** Веб-система для аналізу біометричних показників стресу

**Актуальність:**
- Зростання рівня стресу в сучасному суспільстві
- Необхідність об'єктивної оцінки стану здоров'я
- Потреба в персоналізованих рекомендаціях

**Мета проекту:**
Розробити веб-систему для автоматичного аналізу біометричних показників та надання персоналізованих рекомендацій щодо зниження рівня стресу.

### 2. Функціональні можливості (1 хвилина)

**Основні функції:**
1. Реєстрація та авторизація користувачів з JWT токенами
2. Введення біометричних показників:
   - Серцево-судинна система (пульс, тиск)
   - Температура тіла
   - Рівень кортизолу (гормон стресу)
   - Якість та тривалість сну
   - Показники дихання

3. Багатофакторний аналіз стресу з ваговими коефіцієнтами
4. Автоматична генерація персоналізованих рекомендацій
5. Моніторинг динаміки показників

### 3. Технічний стек (30 секунд)

**Backend:**
- Java 17 + Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA + PostgreSQL 16

**Frontend:**
- HTML5/CSS3 + Vanilla JavaScript
- Responsive Design

**DevOps:**
- Docker + Docker Compose

### 4. Use Case діаграма (30 секунд)

**Актори:**
- Користувач - введення даних, перегляд аналізів
- Лікар - перегляд даних пацієнтів, створення висновків
- Адміністратор - управління системою

**Основні Use Cases:**
- Реєстрація/Авторизація
- Введення біометричних даних
- Запуск аналізу стресу
- Перегляд рекомендацій
- Відстеження динаміки

### 5. Архітектура БД (1 хвилина)

**4 основні таблиці:**

1. **users** - користувачі
   - id, username, email, password (BCrypt)
   - personal info (name, gender, age)
   - role (USER, DOCTOR, ADMIN)

2. **biometric_data** - біометричні показники
   - heart rate, blood pressure
   - body temperature, cortisol level
   - sleep quality, respiratory rate

3. **stress_analysis** - результати аналізу
   - stress_level (LOW, MODERATE, HIGH, CRITICAL)
   - stress_score (0-100)
   - component scores (cardiovascular, thermal, etc.)

4. **recommendations** - рекомендації
   - category, title, description
   - priority (LOW, MEDIUM, HIGH, URGENT)

**Зв'язки:**
- Users 1:N Biometric Data
- Biometric Data 1:1 Stress Analysis
- Stress Analysis 1:N Recommendations

### 6. SOLID принципи (1.5 хвилини)

#### S - Single Responsibility Principle
**Кожен клас має одну відповідальність**

Приклад:
```java
// UserService - тільки управління користувачами
@Service
public class UserServiceImpl {
    // Створення, оновлення, видалення користувачів
}

// StressAnalysisService - тільки аналіз стресу
@Service
public class StressAnalysisServiceImpl {
    // Аналіз біометричних даних
}
```

#### O - Open/Closed Principle
**Відкритий для розширення, закритий для модифікації**

Приклад - Strategy Pattern:
```java
public interface StressAnalysisStrategy {
    StressAnalysis analyze(BiometricData data);
}

// Можна додати нові алгоритми без зміни існуючого коду
public class StandardStrategy implements StressAnalysisStrategy {...}
public class AdvancedMLStrategy implements StressAnalysisStrategy {...}
```

#### L - Liskov Substitution Principle
**Підтипи замінні на базові типи**

Приклад - JPA Repositories:
```java
// Будь-яка реалізація JpaRepository може замінити базовий інтерфейс
public interface UserRepository extends JpaRepository<User, Long> {
    // Спеціалізовані методи
}
```

#### I - Interface Segregation Principle
**Спеціалізовані інтерфейси замість одного великого**

Приклад:
```java
// Замість одного UserManagementService
public interface UserService { /* методи користувачів */ }
public interface AuthService { /* методи аутентифікації */ }
```

#### D - Dependency Inversion Principle
**Залежність від абстракцій**

Приклад:
```java
@Service
public class StressAnalysisServiceImpl {
    // Залежність від інтерфейсу, а не конкретної реалізації
    private final StressAnalysisStrategy strategy;
    private final RecommendationFactory factory;
}
```

### 7. Патерни проектування (2 хвилини)

#### 1. Strategy Pattern
**Призначення:** Різні алгоритми аналізу стресу

**Реалізація:**
```java
public interface StressAnalysisStrategy {
    StressAnalysis analyze(BiometricData data);
}

@Component
public class StandardStressAnalysisStrategy implements StressAnalysisStrategy {
    private static final double CARDIOVASCULAR_WEIGHT = 0.30;
    private static final double THERMAL_WEIGHT = 0.15;
    // ...

    public StressAnalysis analyze(BiometricData data) {
        // Багатофакторний аналіз
        double cardiovascularScore = calculateCardiovascularScore(data);
        // ...
        double totalScore = (cardiovascularScore * CARDIOVASCULAR_WEIGHT) + ...;
        return StressAnalysis.builder()
                .stressScore(totalScore)
                .stressLevel(determineStressLevel(totalScore))
                .build();
    }
}
```

**Переваги:**
- Легко додати нові алгоритми (ML, AI-based)
- Відповідає Open/Closed Principle

#### 2. Factory Pattern
**Призначення:** Створення рекомендацій на основі рівня стресу

**Реалізація:**
```java
@Component
public class RecommendationFactory {
    public List<Recommendation> createRecommendations(StressAnalysis analysis) {
        return switch (analysis.getStressLevel()) {
            case LOW -> createLowStressRecommendations();
            case MODERATE -> createModerateStressRecommendations();
            case HIGH -> createHighStressRecommendations();
            case CRITICAL -> createCriticalStressRecommendations();
        };
    }

    private List<Recommendation> createCriticalStressRecommendations() {
        return List.of(
            Recommendation.builder()
                .category(MEDICAL)
                .title("ТЕРМІНОВА медична консультація")
                .priority(URGENT)
                .build()
        );
    }
}
```

**Переваги:**
- Інкапсуляція логіки створення
- Централізоване управління

#### 3. Repository Pattern
**Призначення:** Абстракція доступу до даних

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
}
```

#### 4. Builder Pattern
**Призначення:** Створення складних об'єктів

```java
User user = User.builder()
        .username("john_doe")
        .email("john@example.com")
        .password(encodedPassword)
        .firstName("John")
        .build();
```

#### 5. Chain of Responsibility
**Призначення:** JWT фільтрація запитів

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    protected void doFilterInternal(...) {
        String jwt = getJwtFromRequest(request);
        if (jwtUtil.validateToken(jwt)) {
            // Встановити аутентифікацію
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response); // Передати далі
    }
}
```

#### 6. Adapter Pattern
**Призначення:** Адаптація до Spring Security

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        // Адаптуємо нашу Entity до Spring Security UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(...)
                .build();
    }
}
```

### 8. Sequence діаграма (1 хвилина)

**Процес аналізу стресу:**

```
User → Frontend → BiometricController → BiometricService → Repository → DB
                                                                ↓
                           StressController → StressService → AnalysisStrategy
                                                     ↓
                                            RecommendationFactory
                                                     ↓
                                              Save to DB
                                                     ↓
                                           Return to Frontend
```

**Ключові моменти:**
1. Збереження біометричних даних
2. Виклик Strategy для аналізу
3. Використання Factory для рекомендацій
4. Транзакційне збереження

### 9. Docker конфігурація (30 секунд)

**docker-compose.yml:**
```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: stress_analysis_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres

  backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
```

**Запуск:**
```bash
docker-compose up -d
```

**Переваги:**
- Ізольоване середовище
- Легке розгортання
- Масштабованість

### 10. Демонстрація (30 секунд)

**Показати:**
1. Swagger UI - http://localhost:8080/swagger-ui.html
2. Frontend - http://localhost:8080
3. Процес реєстрації
4. Введення біометричних даних
5. Результати аналізу з рекомендаціями

## 🎤 Відповіді на типові питання

### Q1: Чому обрали PostgreSQL, а не NoSQL?

**Відповідь:**
- Реляційна структура даних з чіткими зв'язками
- ACID транзакції для критичних медичних даних
- Підтримка складних запитів з JOIN
- Індекси для оптимізації пошуку

### Q2: Як забезпечується безпека даних?

**Відповідь:**
1. JWT токени для аутентифікації
2. BCrypt хешування паролів
3. Spring Security для авторизації
4. HTTPS для транспортного шифрування
5. Валідація вхідних даних
6. Захист від SQL-ін'єкцій через JPA

### Q3: Як система масштабується?

**Відповідь:**
1. Stateless архітектура (JWT)
2. Docker контейнеризація
3. Горизонтальне масштабування backend
4. Connection pooling для БД
5. Можливість додавання балансувальника навантаження

### Q4: Як додати новий алгоритм аналізу?

**Відповідь:**
Завдяки Strategy Pattern:
```java
@Component
public class MLBasedAnalysisStrategy implements StressAnalysisStrategy {
    @Override
    public StressAnalysis analyze(BiometricData data) {
        // Новий ML алгоритм
    }
}

// Змінити bean в конфігурації
@Configuration
public class AnalysisConfig {
    @Bean
    public StressAnalysisStrategy strategy() {
        return new MLBasedAnalysisStrategy();
    }
}
```

### Q5: Як тестується система?

**Відповідь:**
1. Unit тести для бізнес-логіки
2. Integration тести для API
3. Security тести для аутентифікації
4. H2 in-memory DB для тестів
5. Покриття коду >70%

### Q6: Які обмеження системи?

**Відповідь:**
1. Потрібне ручне введення даних (можна інтегрувати з носимими пристроями)
2. Алгоритм аналізу - статистичний (можна покращити ML)
3. Рекомендації загальні (можна персоналізувати глибше)

### Q7: Плани розвитку системи?

**Відповідь:**
1. Інтеграція з носимими пристроями (Apple Watch, Fitbit)
2. ML алгоритми для точнішого аналізу
3. Мобільний додаток (React Native)
4. Телемедицина - зв'язок з лікарями
5. Групові аналітики та звіти

## 📊 Оцінювання компонентів

| Компонент | Бали | Статус |
|-----------|------|--------|
| Технічне завдання | 5 | ✅ Виконано |
| Use Case діаграми | 5 | ✅ Виконано |
| ORM та структура БД | 10 | ✅ Виконано |
| Wireframes інтерфейсу | 10 | ✅ Виконано |
| Реалізація Front-End | 10 | ✅ Виконано |
| Архітектура з SOLID | 15 | ✅ Виконано |
| Патерни проектування | 20 | ✅ Виконано |
| Sequence diagrams | 10 | ✅ Виконано |
| Docker конфігурація | 10 | ✅ Виконано |
| Захист курсової роботи | 5 | ⏳ Очікується |
| **ВСЬОГО** | **100** | **95** |

## 💡 Поради для захисту

1. **Впевненість** - добре знайте свій проект
2. **Структурованість** - дотримуйтесь плану презентації
3. **Демонстрація** - покажіть працюючу систему
4. **Технічність** - використовуйте правильну термінологію
5. **Чесність** - якщо не знаєте відповіді, скажіть про це
6. **Час** - не перевищуйте відведений час

## 🎯 Ключові тези для запам'ятовування

1. **SOLID** - 5 принципів якісної архітектури
2. **7 патернів** - Strategy, Factory, Repository, Builder, Chain, Adapter, Singleton
3. **4 таблиці** - Users, BiometricData, StressAnalysis, Recommendations
4. **Багатофакторний аналіз** - 5 компонентів з ваговими коефіцієнтами
5. **Docker** - контейнеризація для легкого розгортання
6. **JWT** - безпека та stateless архітектура

---

**Успіхів на захисті! 🎓**
