# Sequence Діаграми
# Головні процеси системи

## 1. Процес реєстрації користувача

```mermaid
sequenceDiagram
    participant User as Користувач
    participant Frontend as Frontend
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserService as User Service
    participant UserRepo as User Repository
    participant DB as PostgreSQL
    participant JwtUtil as JWT Util

    User->>Frontend: Заповнює форму реєстрації
    Frontend->>AuthController: POST /api/auth/register
    AuthController->>AuthService: register(registerRequest)

    AuthService->>UserService: existsByUsername(username)
    UserService->>UserRepo: existsByUsername(username)
    UserRepo->>DB: SELECT FROM users WHERE username = ?
    DB-->>UserRepo: false
    UserRepo-->>UserService: false
    UserService-->>AuthService: false

    AuthService->>UserService: existsByEmail(email)
    UserService->>UserRepo: existsByEmail(email)
    UserRepo->>DB: SELECT FROM users WHERE email = ?
    DB-->>UserRepo: false
    UserRepo-->>UserService: false
    UserService-->>AuthService: false

    AuthService->>UserService: createUser(registerRequest)
    UserService->>UserService: passwordEncoder.encode(password)
    UserService->>UserRepo: save(user)
    UserRepo->>DB: INSERT INTO users
    DB-->>UserRepo: User entity
    UserRepo-->>UserService: User entity
    UserService-->>AuthService: UserDTO

    AuthService->>JwtUtil: generateToken(userDetails)
    JwtUtil-->>AuthService: JWT Token

    AuthService-->>AuthController: AuthResponse (token, userDTO)
    AuthController-->>Frontend: 201 Created + AuthResponse
    Frontend-->>User: Успішна реєстрація
```

## 2. Процес авторизації

```mermaid
sequenceDiagram
    participant User as Користувач
    participant Frontend as Frontend
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant AuthManager as Authentication Manager
    participant UserDetailsService as UserDetails Service
    participant UserRepo as User Repository
    participant DB as PostgreSQL
    participant JwtUtil as JWT Util

    User->>Frontend: Вводить логін і пароль
    Frontend->>AuthController: POST /api/auth/login
    AuthController->>AuthService: login(authRequest)

    AuthService->>AuthManager: authenticate(credentials)
    AuthManager->>UserDetailsService: loadUserByUsername(username)
    UserDetailsService->>UserRepo: findByUsername(username)
    UserRepo->>DB: SELECT FROM users WHERE username = ?
    DB-->>UserRepo: User entity
    UserRepo-->>UserDetailsService: User entity
    UserDetailsService-->>AuthManager: UserDetails

    AuthManager->>AuthManager: validate password
    AuthManager-->>AuthService: Authentication

    AuthService->>JwtUtil: generateToken(userDetails)
    JwtUtil-->>AuthService: JWT Token

    AuthService->>UserService: getUserByUsername(username)
    UserService->>UserRepo: findByUsername(username)
    UserRepo->>DB: SELECT FROM users
    DB-->>UserRepo: User
    UserRepo-->>UserService: User
    UserService-->>AuthService: UserDTO

    AuthService-->>AuthController: AuthResponse (token, userDTO)
    AuthController-->>Frontend: 200 OK + AuthResponse
    Frontend-->>User: Вхід виконано
```

## 3. Процес введення біометричних даних та аналізу стресу

```mermaid
sequenceDiagram
    participant User as Користувач
    participant Frontend as Frontend
    participant BiometricController as Biometric Controller
    participant BiometricService as Biometric Service
    participant BiometricRepo as Biometric Repository
    participant StressController as Stress Controller
    participant StressService as Stress Service
    participant AnalysisStrategy as Analysis Strategy
    participant RecommendationFactory as Recommendation Factory
    participant StressRepo as Stress Repository
    participant DB as PostgreSQL

    User->>Frontend: Вводить біометричні дані
    Frontend->>BiometricController: POST /api/biometric-data (+ JWT)
    BiometricController->>BiometricService: createBiometricData(dto)

    BiometricService->>BiometricService: getUserEntityById(userId)
    BiometricService->>BiometricRepo: save(biometricData)
    BiometricRepo->>DB: INSERT INTO biometric_data
    DB-->>BiometricRepo: Saved entity
    BiometricRepo-->>BiometricService: BiometricData
    BiometricService-->>BiometricController: BiometricDataDTO
    BiometricController-->>Frontend: 201 Created

    Frontend->>User: Дані збережено
    User->>Frontend: Натискає "Аналізувати стрес"
    Frontend->>StressController: POST /api/stress-analysis/analyze/{id}
    StressController->>StressService: analyzeStress(biometricDataId)

    StressService->>BiometricRepo: findById(biometricDataId)
    BiometricRepo->>DB: SELECT FROM biometric_data
    DB-->>BiometricRepo: BiometricData
    BiometricRepo-->>StressService: BiometricData

    StressService->>AnalysisStrategy: analyze(biometricData)
    Note over AnalysisStrategy: Розрахунок показників:<br/>- cardiovascular (30%)<br/>- thermal (15%)<br/>- biochemical (25%)<br/>- sleep (20%)<br/>- respiratory (10%)
    AnalysisStrategy-->>StressService: StressAnalysis

    StressService->>RecommendationFactory: createRecommendations(analysis)
    Note over RecommendationFactory: Factory Pattern:<br/>Створення рекомендацій<br/>на основі рівня стресу
    RecommendationFactory-->>StressService: List<Recommendation>

    StressService->>StressRepo: save(stressAnalysis)
    StressRepo->>DB: INSERT INTO stress_analysis + recommendations
    DB-->>StressRepo: Saved entities
    StressRepo-->>StressService: StressAnalysis

    StressService-->>StressController: StressAnalysisDTO
    StressController-->>Frontend: 201 Created + результати
    Frontend-->>User: Показ результатів аналізу
```

## 4. Процес аутентифікації з JWT

```mermaid
sequenceDiagram
    participant Client as Клієнт
    participant Filter as JWT Filter
    participant JwtUtil as JWT Util
    participant UserDetailsService as UserDetails Service
    participant SecurityContext as Security Context
    participant Controller as Controller

    Client->>Filter: HTTP Request + Authorization: Bearer {token}
    Filter->>Filter: extractJwtFromRequest()
    Filter->>JwtUtil: extractUsername(token)
    JwtUtil-->>Filter: username

    Filter->>SecurityContext: getAuthentication()
    SecurityContext-->>Filter: null

    Filter->>UserDetailsService: loadUserByUsername(username)
    UserDetailsService->>DB: findByUsername()
    DB-->>UserDetailsService: User
    UserDetailsService-->>Filter: UserDetails

    Filter->>JwtUtil: validateToken(token, userDetails)
    JwtUtil->>JwtUtil: check expiration
    JwtUtil->>JwtUtil: validate signature
    JwtUtil-->>Filter: true

    Filter->>SecurityContext: setAuthentication(authentication)
    Filter->>Controller: Continue filter chain
    Controller->>Controller: Process request
    Controller-->>Client: Response
```

## 5. Процес отримання історії аналізів

```mermaid
sequenceDiagram
    participant User as Користувач
    participant Frontend as Frontend
    participant StressController as Stress Controller
    participant StressService as Stress Service
    participant StressRepo as Stress Repository
    participant DB as PostgreSQL

    User->>Frontend: Запит історії аналізів
    Frontend->>StressController: GET /api/stress-analysis/user/{userId}
    StressController->>StressService: getStressAnalysisByUserId(userId)

    StressService->>StressRepo: findByUserIdOrderByCreatedAtDesc(userId)
    StressRepo->>DB: SELECT * FROM stress_analysis<br/>WHERE user_id = ?<br/>ORDER BY created_at DESC
    DB-->>StressRepo: List<StressAnalysis>
    StressRepo-->>StressService: List<StressAnalysis>

    StressService->>StressService: Convert to DTOs
    StressService-->>StressController: List<StressAnalysisDTO>
    StressController-->>Frontend: 200 OK + JSON
    Frontend-->>User: Відображення історії
```

## Опис патернів у Sequence діаграмах

### 1. Strategy Pattern (AnalysisStrategy)
Використовується для реалізації різних алгоритмів аналізу стресу. Поточна реалізація - StandardStressAnalysisStrategy, але можна додати інші стратегії без зміни коду.

### 2. Factory Pattern (RecommendationFactory)
Створює рекомендації на основі результатів аналізу стресу. Інкапсулює логіку створення різних типів рекомендацій.

### 3. Chain of Responsibility (JWT Filter)
JWT фільтр перевіряє токен та передає запит далі по ланцюгу фільтрів Spring Security.

### 4. Repository Pattern
Абстракція доступу до даних через репозиторії, що дозволяє легко змінювати реалізацію збереження даних.
