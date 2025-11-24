# SOLID Principles
## Реалізація в Системі аналізу біометричних показників стресу

---

## Зміст
1. [Single Responsibility Principle (SRP)](#1-single-responsibility-principle-srp)
2. [Open/Closed Principle (OCP)](#2-openclosed-principle-ocp)
3. [Liskov Substitution Principle (LSP)](#3-liskov-substitution-principle-lsp)
4. [Interface Segregation Principle (ISP)](#4-interface-segregation-principle-isp)
5. [Dependency Inversion Principle (DIP)](#5-dependency-inversion-principle-dip)
6. [Висновки](#висновки)

---

## 1. Single Responsibility Principle (SRP)

**Принцип**: Кожен клас повинен мати лише одну причину для зміни.

### 1.1. Реалізація в проекті

#### Controllers - відповідають лише за HTTP запити

**`AuthController.java`** - тільки автентифікація
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // Відповідальність: приймати HTTP запити автентифікації
    // НЕ містить бізнес-логіку!

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/controller/AuthController.java`

#### Services - відповідають лише за бізнес-логіку

**`AuthServiceImpl.java`** - тільки логіка автентифікації
```java
@Service
public class AuthServiceImpl implements AuthService {
    // Відповідальність: бізнес-логіка автентифікації
    // НЕ знає про HTTP!

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Перевірка унікальності
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Створення користувача
        User user = createUserFromRequest(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Збереження через інший сервіс
        userService.createUser(user);

        // Генерація токена
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, convertToDTO(user));
    }
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/service/impl/AuthServiceImpl.java`

#### Repositories - відповідають лише за доступ до даних

**`UserRepository.java`**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Відповідальність: доступ до даних користувачів
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/repository/UserRepository.java`

### 1.2. Переваги SRP в проекті

✅ **Легкість тестування**: кожен клас можна тестувати окремо
✅ **Простота підтримки**: зміни в одній відповідальності не впливають на інші
✅ **Зрозумілість коду**: кожен клас має чітку мету
✅ **Повторне використання**: компоненти легко комбінувати

### 1.3. Приклади порушення SRP (чого НЕМАЄ в проекті)

❌ **Неправильно**:
```java
public class UserController {
    // Порушує SRP: контролер не повинен мати бізнес-логіку
    @PostMapping("/register")
    public ResponseEntity register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email exists");
        }
        User user = new User();
        user.setPassword(bcrypt.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
```

✅ **Правильно** (як у проекті):
```java
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity register(RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }
}
```

---

## 2. Open/Closed Principle (OCP)

**Принцип**: Класи повинні бути відкриті для розширення, але закриті для модифікації.

### 2.1. Strategy Pattern для аналізу стресу

**`StressAnalysisStrategy` interface**
```java
public interface StressAnalysisStrategy {
    StressAnalysis analyze(BiometricData biometricData);
    String getStrategyName();
}
```

**`StandardStressAnalysisStrategy` implementation**
```java
@Component
public class StandardStressAnalysisStrategy implements StressAnalysisStrategy {
    @Override
    public StressAnalysis analyze(BiometricData data) {
        StressAnalysis analysis = new StressAnalysis();

        // Розрахунок компонентів
        double cardiovascular = calculateCardiovascular(data);
        double thermal = calculateThermal(data);
        double biochemical = calculateBiochemical(data);
        double sleep = calculateSleep(data);
        double respiratory = calculateRespiratory(data);

        // Загальний розрахунок
        double overallScore = calculateOverallScore(
            cardiovascular, thermal, biochemical, sleep, respiratory
        );

        analysis.setStressScore(overallScore);
        analysis.setStressLevel(
            StressAnalysis.determineStressLevel(overallScore)
        );

        return analysis;
    }

    @Override
    public String getStrategyName() {
        return "Standard Analysis";
    }
}
```

**Файли**:
- `backend/src/main/java/com/biometric/stressanalysis/pattern/StressAnalysisStrategy.java`
- `backend/src/main/java/com/biometric/stressanalysis/pattern/StandardStressAnalysisStrategy.java`

### 2.2. Розширення без модифікації

Щоб додати новий алгоритм аналізу стресу, просто створіть новий клас:

```java
@Component
public class AdvancedMLStressAnalysisStrategy implements StressAnalysisStrategy {
    @Override
    public StressAnalysis analyze(BiometricData data) {
        // Новий алгоритм з машинним навчанням
        // Жодних змін в існуючому коді!
    }

    @Override
    public String getStrategyName() {
        return "Advanced ML Analysis";
    }
}
```

### 2.3. Factory Pattern для рекомендацій

**`RecommendationFactory`**
```java
@Component
public class RecommendationFactory {
    // Відкритий для розширення: легко додавати нові типи рекомендацій
    public List<Recommendation> createRecommendations(StressAnalysis analysis) {
        List<Recommendation> recommendations = new ArrayList<>();

        if (needsPhysicalActivity(analysis)) {
            recommendations.add(createPhysicalActivityRecommendation(analysis));
        }

        if (needsNutritionAdvice(analysis)) {
            recommendations.add(createNutritionRecommendation(analysis));
        }

        // Додавання нових рекомендацій не змінює існуючий код

        return recommendations;
    }
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/pattern/RecommendationFactory.java`

### 2.4. Переваги OCP

✅ **Нові функції без ризику**: нові стратегії не ламають існуючі
✅ **Легке тестування**: кожна стратегія тестується окремо
✅ **Гнучкість**: можна вибирати стратегію в runtime

---

## 3. Liskov Substitution Principle (LSP)

**Принцип**: Об'єкти можуть бути замінені їх підтипами без зміни коректності програми.

### 3.1. BaseEntity для всіх Entity

**`BaseEntity.java`**
```java
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

**Наслідування**:
```java
@Entity
public class User extends BaseEntity {
    // Можна використовувати як BaseEntity
}

@Entity
public class BiometricData extends BaseEntity {
    // Можна використовувати як BaseEntity
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/entity/BaseEntity.java`

### 3.2. Заміна типів без проблем

```java
public void logCreation(BaseEntity entity) {
    log.info("Entity created at: {}", entity.getCreatedAt());
}

// Можна передати будь-який тип Entity
logCreation(new User());
logCreation(new BiometricData());
logCreation(new StressAnalysis());
```

### 3.3. Service interfaces

**Інтерфейс**:
```java
public interface StressAnalysisService {
    StressAnalysisDTO analyzeStress(Long biometricDataId);
    Optional<StressAnalysisDTO> getStressAnalysisById(Long id);
}
```

**Реалізація**:
```java
@Service
public class StressAnalysisServiceImpl implements StressAnalysisService {
    @Override
    public StressAnalysisDTO analyzeStress(Long biometricDataId) {
        // Реалізація
    }
}
```

**Використання**:
```java
// Можна замінити реалізацію без зміни коду
StressAnalysisService service = new StressAnalysisServiceImpl();
// або
StressAnalysisService service = new CachedStressAnalysisServiceImpl();
```

### 3.4. Переваги LSP

✅ **Поліморфізм**: використання інтерфейсів замість конкретних класів
✅ **Тестування**: легко мокувати сервіси
✅ **Заміна реалізацій**: можна міняти сервіси без зламу коду

---

## 4. Interface Segregation Principle (ISP)

**Принцип**: Клієнти не повинні залежати від інтерфейсів, які вони не використовують.

### 4.1. Специфічні інтерфейси сервісів

**Замість одного великого інтерфейсу**:
```java
// ❌ НЕПРАВИЛЬНО
public interface UserManagementService {
    // Автентифікація
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);

    // CRUD користувачів
    UserDTO createUser(User user);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);

    // Біометричні дані
    BiometricDataDTO addBiometricData(BiometricData data);
    List<BiometricDataDTO> getBiometricData(Long userId);

    // Аналіз стресу
    StressAnalysisDTO analyzeStress(Long biometricDataId);
}
```

**Маємо окремі інтерфейси**:

✅ **ПРАВИЛЬНО**:

**AuthService** - тільки автентифікація
```java
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}
```

**UserService** - тільки CRUD користувачів
```java
public interface UserService {
    UserDTO createUser(User user);
    Optional<UserDTO> getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
```

**BiometricDataService** - тільки біометричні дані
```java
public interface BiometricDataService {
    BiometricDataDTO createBiometricData(BiometricData data);
    List<BiometricDataDTO> getBiometricDataByUserId(Long userId);
    Optional<BiometricDataDTO> getBiometricDataById(Long id);
}
```

**StressAnalysisService** - тільки аналіз стресу
```java
public interface StressAnalysisService {
    StressAnalysisDTO analyzeStress(Long biometricDataId);
    List<StressAnalysisDTO> getStressAnalysisByUserId(Long userId);
}
```

### 4.2. Переваги ISP

✅ **Мінімальні залежності**: клас залежить лише від потрібних методів
✅ **Легкість мокування**: маленькі інтерфейси легше мокувати
✅ **Гнучкість**: можна комбінувати сервіси за потребою

---

## 5. Dependency Inversion Principle (DIP)

**Принцип**: Залежність від абстракцій, а не від конкретних реалізацій.

### 5.1. Dependency Injection через Spring

**Controller залежить від інтерфейсу, а не реалізації**:

```java
@RestController
@RequestMapping("/api/stress")
public class StressAnalysisController {
    // ✅ Залежність від інтерфейсу (абстракції)
    private final StressAnalysisService stressAnalysisService;

    // ❌ НЕ залежність від реалізації:
    // private final StressAnalysisServiceImpl stressAnalysisService;

    @Autowired
    public StressAnalysisController(StressAnalysisService stressAnalysisService) {
        this.stressAnalysisService = stressAnalysisService;
    }

    @PostMapping("/analyze/{biometricDataId}")
    public ResponseEntity<StressAnalysisDTO> analyzeStress(
        @PathVariable Long biometricDataId
    ) {
        return ResponseEntity.ok(
            stressAnalysisService.analyzeStress(biometricDataId)
        );
    }
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/controller/StressAnalysisController.java`

### 5.2. Service залежить від Repository інтерфейсів

```java
@Service
public class StressAnalysisServiceImpl implements StressAnalysisService {
    // ✅ Залежність від абстракцій
    private final StressAnalysisRepository stressAnalysisRepository;
    private final BiometricDataRepository biometricDataRepository;
    private final StressAnalysisStrategy analysisStrategy;

    @Autowired
    public StressAnalysisServiceImpl(
        StressAnalysisRepository stressAnalysisRepository,
        BiometricDataRepository biometricDataRepository,
        StressAnalysisStrategy analysisStrategy
    ) {
        this.stressAnalysisRepository = stressAnalysisRepository;
        this.biometricDataRepository = biometricDataRepository;
        this.analysisStrategy = analysisStrategy;
    }
}
```

### 5.3. Інверсія залежностей

**Традиційна залежність (порушує DIP)**:
```
┌───────────────┐
│  Controller   │
└───────┬───────┘
        │ залежить
        ▼
┌───────────────┐
│ServiceImpl    │
└───────────────┘
```

**Інверсія (дотримується DIP)**:
```
┌───────────────┐
│  Controller   │
└───────┬───────┘
        │ залежить
        ▼
┌───────────────┐
│<<interface>>  │
│   Service     │
└───────▲───────┘
        │ implements
        │
┌───────┴───────┐
│ServiceImpl    │
└───────────────┘
```

### 5.4. Конфігурація через Spring

**SecurityConfig** - залежить від абстракцій:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(
        UserDetailsService userDetailsService,
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Файл**: `backend/src/main/java/com/biometric/stressanalysis/config/SecurityConfig.java`

### 5.5. Переваги DIP

✅ **Тестування**: легко мокувати залежності
✅ **Гнучкість**: можна міняти реалізації
✅ **Loose coupling**: компоненти слабко зв'язані
✅ **Повторне використання**: сервіси можна використовувати в різних контекстах

---

## Висновки

### Дотримання SOLID в проекті

| Принцип | Реалізація | Приклади |
|---------|------------|----------|
| **SRP** | ✅ Повністю | Controllers, Services, Repositories мають одну відповідальність |
| **OCP** | ✅ Повністю | Strategy Pattern для аналізу, Factory для рекомендацій |
| **LSP** | ✅ Повністю | BaseEntity, Service interfaces |
| **ISP** | ✅ Повністю | Окремі інтерфейси для кожного сервісу |
| **DIP** | ✅ Повністю | Dependency Injection через Spring, залежність від інтерфейсів |

### Переваги дотримання SOLID

1. **Тестування**: Кожен компонент легко тестується окремо
2. **Підтримка**: Зміни в одному місці не впливають на інші
3. **Розширюваність**: Легко додавати нові функції
4. **Читабельність**: Код зрозумілий та структурований
5. **Повторне використання**: Компоненти можна використовувати в різних контекстах
6. **Гнучкість**: Легко міняти реалізації

### Приклади дотримання SOLID

**1. Controller + Service + Repository (SRP + DIP)**:
```
AuthController → AuthService (interface) → AuthServiceImpl → UserRepository
```

**2. Strategy Pattern (OCP)**:
```
StressAnalysisService → StressAnalysisStrategy (interface) → StandardStrategy
                                                           → AdvancedStrategy
                                                           → MLStrategy
```

**3. Factory Pattern (OCP + SRP)**:
```
RecommendationFactory → створює різні типи рекомендацій без зміни коду
```

### Рекомендації для розвитку

Для майбутніх покращень:
- Додати більше стратегій аналізу (Machine Learning)
- Створити окремі інтерфейси для різних типів рекомендацій
- Впровадити Event-Driven Architecture для аналізу

**Проект демонструє глибоке розуміння та правильне застосування всіх п'яти принципів SOLID, що є фундаментом якісного об'єктно-орієнтованого дизайну.**
