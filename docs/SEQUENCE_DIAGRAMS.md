# Sequence Діаграми
## Система аналізу біометричних показників стресу

---

## Зміст
1. [Процес реєстрації користувача](#1-процес-реєстрації-користувача)
2. [Процес автентифікації](#2-процес-автентифікації)
3. [Процес додавання біометричних даних](#3-процес-додавання-біометричних-даних)
4. [Процес аналізу стресу](#4-процес-аналізу-стресу)
5. [Процес генерації рекомендацій](#5-процес-генерації-рекомендацій)
6. [Процес перегляду історії](#6-процес-перегляду-історії)
7. [Процес відображення Dashboard](#7-процес-відображення-dashboard)

---

## 1. Процес реєстрації користувача

### 1.1. Опис
Користувач заповнює форму реєстрації, система валідує дані, шифрує пароль, створює обліковий запис та генерує JWT токен.

### 1.2. Sequence діаграма

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "AuthService\n(Frontend)" as AuthFE
participant "API Gateway\n(Nginx)" as Gateway
participant "AuthController" as Controller
participant "AuthService" as AuthService
participant "UserService" as UserService
participant "UserRepository" as UserRepo
participant "PostgreSQL" as DB
participant "BCryptEncoder" as Encoder
participant "JwtUtil" as JWT

User -> Frontend: Заповнює форму реєстрації
activate Frontend

Frontend -> Frontend: Валідація клієнтської сторони
note right
  - Email format
  - Password length >= 6
  - Required fields
end note

Frontend -> AuthFE: register(registerData)
activate AuthFE

AuthFE -> Gateway: POST /api/auth/register
activate Gateway

Gateway -> Controller: registerUser(RegisterRequest)
activate Controller

Controller -> Controller: Валідація @Valid
note right
  Spring Validation
  annotations
end note

Controller -> AuthService: register(RegisterRequest)
activate AuthService

AuthService -> UserService: existsByEmail(email)
activate UserService
UserService -> UserRepo: existsByEmail(email)
activate UserRepo
UserRepo -> DB: SELECT EXISTS(...)
activate DB
DB --> UserRepo: false
deactivate DB
UserRepo --> UserService: false
deactivate UserRepo
UserService --> AuthService: false
deactivate UserService

AuthService -> UserService: existsByUsername(username)
activate UserService
UserService -> UserRepo: existsByUsername(username)
activate UserRepo
UserRepo -> DB: SELECT EXISTS(...)
activate DB
DB --> UserRepo: false
deactivate DB
UserRepo --> UserService: false
deactivate UserRepo
UserService --> AuthService: false
deactivate UserService

AuthService -> Encoder: encode(password)
activate Encoder
Encoder --> AuthService: hashedPassword
deactivate Encoder

AuthService -> UserService: createUser(userData)
activate UserService

UserService -> UserRepo: save(user)
activate UserRepo
UserRepo -> DB: INSERT INTO users...
activate DB
DB --> UserRepo: saved user
deactivate DB
UserRepo --> UserService: User entity
deactivate UserRepo
UserService --> AuthService: User entity
deactivate UserService

AuthService -> JWT: generateToken(user)
activate JWT
JWT -> JWT: Create claims
JWT -> JWT: Sign with secret
JWT --> AuthService: JWT token
deactivate JWT

AuthService --> Controller: AuthResponse(token, user)
deactivate AuthService

Controller --> Gateway: 200 OK + JSON
deactivate Controller

Gateway --> AuthFE: AuthResponse
deactivate Gateway

AuthFE -> AuthFE: localStorage.setItem('jwt_token')
AuthFE -> AuthFE: localStorage.setItem('user')
AuthFE --> Frontend: Success
deactivate AuthFE

Frontend -> Frontend: Redirect to /dashboard
Frontend --> User: Welcome screen
deactivate Frontend

@enduml
```

### 1.3. Кроки процесу

1. **Користувач** заповнює форму реєстрації
2. **Frontend** валідує дані на клієнті
3. **Frontend** відправляє POST запит до API
4. **AuthController** приймає запит та валідує через @Valid
5. **AuthService** перевіряє унікальність email та username
6. **BCryptEncoder** шифрує пароль
7. **UserService** створює користувача в БД
8. **JwtUtil** генерує JWT токен
9. **Frontend** зберігає токен в localStorage
10. **Frontend** перенаправляє користувача на Dashboard

---

## 2. Процес автентифікації

### 2.1. Опис
Користувач вводить облікові дані, система перевіряє їх, генерує JWT токен та повертає інформацію про користувача.

### 2.2. Sequence діаграма

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "AuthService\n(Frontend)" as AuthFE
participant "AuthController" as Controller
participant "AuthService" as AuthService
participant "UserService" as UserService
participant "UserRepository" as UserRepo
participant "PostgreSQL" as DB
participant "BCryptEncoder" as Encoder
participant "JwtUtil" as JWT

User -> Frontend: Вводить email і пароль
activate Frontend

Frontend -> AuthFE: login(email, password)
activate AuthFE

AuthFE -> Controller: POST /api/auth/login
activate Controller

Controller -> AuthService: authenticate(AuthRequest)
activate AuthService

AuthService -> UserService: findByEmail(email)
activate UserService
UserService -> UserRepo: findByEmail(email)
activate UserRepo
UserRepo -> DB: SELECT * FROM users WHERE email = ?
activate DB
DB --> UserRepo: User row
deactivate DB
UserRepo --> UserService: Optional<User>
deactivate UserRepo
UserService --> AuthService: User entity
deactivate UserService

alt User not found
    AuthService --> Controller: throw BadCredentialsException
    Controller --> Frontend: 401 Unauthorized
    Frontend --> User: "Invalid credentials"
else User found
    AuthService -> Encoder: matches(plainPassword, hashedPassword)
    activate Encoder
    Encoder --> AuthService: true/false
    deactivate Encoder

    alt Password mismatch
        AuthService --> Controller: throw BadCredentialsException
        Controller --> Frontend: 401 Unauthorized
        Frontend --> User: "Invalid credentials"
    else Password match
        AuthService -> AuthService: Check if user.active

        alt User inactive
            AuthService --> Controller: throw DisabledException
            Controller --> Frontend: 403 Forbidden
            Frontend --> User: "Account disabled"
        else User active
            AuthService -> JWT: generateToken(user)
            activate JWT
            JWT --> AuthService: JWT token
            deactivate JWT

            AuthService --> Controller: AuthResponse(token, user)
            deactivate AuthService

            Controller --> Frontend: 200 OK + JSON
            deactivate Controller

            AuthFE -> AuthFE: localStorage.setItem('jwt_token')
            AuthFE -> AuthFE: localStorage.setItem('user')
            AuthFE --> Frontend: Success
            deactivate AuthFE

            Frontend -> Frontend: Redirect to /dashboard
            Frontend --> User: Dashboard page
            deactivate Frontend
        end
    end
end

@enduml
```

---

## 3. Процес додавання біометричних даних

### 3.1. Sequence діаграма

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "BiometricService\n(Frontend)" as BioFE
participant "BiometricController" as Controller
participant "JwtFilter" as JwtFilter
participant "BiometricService" as BioService
participant "BiometricRepository" as BioRepo
participant "PostgreSQL" as DB

User -> Frontend: Заповнює форму біометричних даних
activate Frontend

Frontend -> Frontend: Валідація діапазонів
note right
  Heart rate: 40-200
  Systolic: 80-200
  Diastolic: 40-130
  Temperature: 35-42
end note

Frontend -> BioFE: createBiometricData(data)
activate BioFE

BioFE -> Controller: POST /api/biometric\n+ JWT token in header
activate Controller

Controller -> JwtFilter: Validate JWT
activate JwtFilter
JwtFilter -> JwtFilter: Extract userId from token
JwtFilter --> Controller: userId
deactivate JwtFilter

Controller -> BioService: createBiometricData(userId, data)
activate BioService

BioService -> BioService: Валідація бізнес-правил
note right
  - Check data ranges
  - Warn if abnormal values
  - Set measurement time
end note

BioService -> BioRepo: save(biometricData)
activate BioRepo

BioRepo -> DB: INSERT INTO biometric_data...
activate DB
DB --> BioRepo: Saved row with ID
deactivate DB

BioRepo --> BioService: BiometricData entity
deactivate BioRepo

BioService -> BioService: Convert to DTO
BioService --> Controller: BiometricDataDTO
deactivate BioService

Controller --> BioFE: 201 Created + JSON
deactivate Controller

BioFE --> Frontend: Success
deactivate BioFE

Frontend -> Frontend: Show success message
Frontend -> User: "Prompt: Analyze stress?"
User -> Frontend: Click "Yes, Analyze"

Frontend -> Frontend: Navigate to Analyze

deactivate Frontend

@enduml
```

---

## 4. Процес аналізу стресу

### 4.1. Опис
Найскладніший процес у системі. Включає розрахунок множинних компонентів стресу, визначення рівня та генерацію рекомендацій.

### 4.2. Sequence діаграма

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "StressService\n(Frontend)" as StressFE
participant "StressController" as Controller
participant "StressAnalysisService" as StressService
participant "BiometricDataService" as BioService
participant "BiometricRepository" as BioRepo
participant "StressCalculator" as Calculator
participant "RecommendationStrategy" as RecStrategy
participant "StressRepository" as StressRepo
participant "RecommendationRepository" as RecRepo
participant "PostgreSQL" as DB

User -> Frontend: Click "Analyze Stress"
activate Frontend

Frontend -> Frontend: Show loading spinner

Frontend -> StressFE: analyzeStress(biometricDataId)
activate StressFE

StressFE -> Controller: POST /api/stress/analyze/{id}
activate Controller

Controller -> StressService: analyzeBiometricData(id, userId)
activate StressService

' Fetch biometric data
StressService -> BioService: getBiometricDataById(id)
activate BioService
BioService -> BioRepo: findById(id)
activate BioRepo
BioRepo -> DB: SELECT * FROM biometric_data WHERE id = ?
activate DB
DB --> BioRepo: Row
deactivate DB
BioRepo --> BioService: BiometricData
deactivate BioRepo
BioService --> StressService: BiometricData
deactivate BioService

' Calculate cardiovascular score
StressService -> Calculator: calculateCardiovascularScore(data)
activate Calculator
Calculator -> Calculator: Analyze heart rate
Calculator -> Calculator: Analyze blood pressure
Calculator -> Calculator: Combine scores
Calculator --> StressService: cardiovascularScore (0-100)
deactivate Calculator

' Calculate thermal score
StressService -> Calculator: calculateThermalScore(data)
activate Calculator
Calculator -> Calculator: Analyze body temperature
Calculator --> StressService: thermalScore (0-100)
deactivate Calculator

' Calculate biochemical score (if available)
alt Cortisol level present
    StressService -> Calculator: calculateBiochemicalScore(data)
    activate Calculator
    Calculator -> Calculator: Analyze cortisol level
    Calculator --> StressService: biochemicalScore (0-100)
    deactivate Calculator
end

' Calculate sleep score (if available)
alt Sleep data present
    StressService -> Calculator: calculateSleepScore(data)
    activate Calculator
    Calculator -> Calculator: Analyze sleep hours
    Calculator -> Calculator: Analyze sleep quality
    Calculator --> StressService: sleepScore (0-100)
    deactivate Calculator
end

' Calculate respiratory score (if available)
alt Respiratory data present
    StressService -> Calculator: calculateRespiratoryScore(data)
    activate Calculator
    Calculator -> Calculator: Analyze respiratory rate
    Calculator -> Calculator: Analyze oxygen saturation
    Calculator --> StressService: respiratoryScore (0-100)
    deactivate Calculator
end

' Calculate overall stress score
StressService -> Calculator: calculateOverallStress(componentScores)
activate Calculator
Calculator -> Calculator: Apply weights\n- Cardiovascular: 30%\n- Thermal: 15%\n- Biochemical: 25%\n- Sleep: 20%\n- Respiratory: 10%
Calculator --> StressService: overallStressScore (0-100)
deactivate Calculator

' Determine stress level
StressService -> StressService: determineStressLevel(score)
note right
  < 25: LOW
  25-49: MODERATE
  50-74: HIGH
  75-100: CRITICAL
end note

' Generate analysis text
StressService -> StressService: generateAnalysisText(level, scores)

' Create StressAnalysis entity
StressService -> StressRepo: save(stressAnalysis)
activate StressRepo
StressRepo -> DB: INSERT INTO stress_analysis...
activate DB
DB --> StressRepo: Saved row with ID
deactivate DB
StressRepo --> StressService: StressAnalysis entity
deactivate StressRepo

' Generate recommendations
StressService -> RecStrategy: generateRecommendations(stressAnalysis)
activate RecStrategy

loop For each recommendation category
    RecStrategy -> RecStrategy: Select strategy based on\nstress level and scores
    RecStrategy -> RecRepo: save(recommendation)
    activate RecRepo
    RecRepo -> DB: INSERT INTO recommendations...
    activate DB
    DB --> RecRepo: Saved row
    deactivate DB
    RecRepo --> RecStrategy: Recommendation
    deactivate RecRepo
end

RecStrategy --> StressService: List<Recommendation>
deactivate RecStrategy

' Convert to DTO with recommendations
StressService -> StressService: Convert to StressAnalysisDTO
StressService --> Controller: StressAnalysisDTO + Recommendations
deactivate StressService

Controller --> StressFE: 200 OK + JSON
deactivate Controller

StressFE --> Frontend: Analysis result
deactivate StressFE

Frontend -> Frontend: Hide loading spinner
Frontend -> Frontend: Display results\n- Stress score gauge\n- Level badge\n- Component scores\n- Analysis text\n- Recommendations list

Frontend --> User: Show detailed analysis
deactivate Frontend

@enduml
```

### 4.3. Компоненти розрахунку стресу

#### Cardiovascular Score (30% ваги)
- Пульс: нормальний діапазон 60-80 уд/хв
- Систолічний тиск: нормальний < 120 мм рт.ст.
- Діастолічний тиск: нормальний < 80 мм рт.ст.

#### Thermal Score (15% ваги)
- Температура: нормальний діапазон 36.5-37.2°C

#### Biochemical Score (25% ваги)
- Рівень кортизолу: нормальний 138-690 нмоль/л

#### Sleep Score (20% ваги)
- Кількість годин: рекомендовано 7-9 годин
- Якість сну: EXCELLENT > GOOD > FAIR > POOR

#### Respiratory Score (10% ваги)
- Частота дихання: нормальний 12-20 вдихів/хв
- Сатурація кисню: нормальний > 95%

---

## 5. Процес генерації рекомендацій

### 5.1. Sequence діаграма

```plantuml
@startuml
participant "StressAnalysisService" as StressService
participant "RecommendationStrategy" as Strategy
participant "PhysicalActivityStrategy" as Physical
participant "NutritionStrategy" as Nutrition
participant "SleepStrategy" as Sleep
participant "RelaxationStrategy" as Relaxation
participant "MedicalStrategy" as Medical
participant "LifestyleStrategy" as Lifestyle
participant "RecommendationRepository" as RecRepo
participant "PostgreSQL" as DB

StressService -> Strategy: generateRecommendations(stressAnalysis)
activate Strategy

Strategy -> Strategy: Analyze stress level and scores

' Physical Activity Recommendations
Strategy -> Physical: generate(stressAnalysis)
activate Physical

alt High cardiovascular score
    Physical -> Physical: Create intense exercise recommendation
else Moderate score
    Physical -> Physical: Create moderate exercise recommendation
else Low score
    Physical -> Physical: Create gentle exercise recommendation
end

Physical --> Strategy: Recommendation(PHYSICAL_ACTIVITY, priority)
deactivate Physical

Strategy -> RecRepo: save(recommendation)
activate RecRepo
RecRepo -> DB: INSERT INTO recommendations...
activate DB
DB --> RecRepo: Saved
deactivate DB
RecRepo --> Strategy: Recommendation
deactivate RecRepo

' Nutrition Recommendations
Strategy -> Nutrition: generate(stressAnalysis)
activate Nutrition

alt Critical stress level
    Nutrition -> Nutrition: Recommend stress-reducing foods
else High stress
    Nutrition -> Nutrition: Recommend balanced diet
end

Nutrition --> Strategy: Recommendation(NUTRITION, priority)
deactivate Nutrition

Strategy -> RecRepo: save(recommendation)
activate RecRepo
RecRepo -> DB: INSERT INTO recommendations...
activate DB
DB --> RecRepo: Saved
deactivate DB
RecRepo --> Strategy: Recommendation
deactivate RecRepo

' Sleep Recommendations
alt Poor sleep score
    Strategy -> Sleep: generate(stressAnalysis)
    activate Sleep

    Sleep -> Sleep: Analyze sleep hours and quality
    Sleep -> Sleep: Create sleep hygiene recommendations

    Sleep --> Strategy: Recommendation(SLEEP, HIGH priority)
    deactivate Sleep

    Strategy -> RecRepo: save(recommendation)
    activate RecRepo
    RecRepo -> DB: INSERT INTO recommendations...
    activate DB
    DB --> RecRepo: Saved
    deactivate DB
    RecRepo --> Strategy: Recommendation
    deactivate RecRepo
end

' Relaxation Recommendations
alt Stress level >= MODERATE
    Strategy -> Relaxation: generate(stressAnalysis)
    activate Relaxation

    Relaxation -> Relaxation: Select relaxation techniques
    note right
      - Meditation
      - Deep breathing
      - Yoga
      - Progressive muscle relaxation
    end note

    Relaxation --> Strategy: Recommendation(RELAXATION, priority)
    deactivate Relaxation

    Strategy -> RecRepo: save(recommendation)
    activate RecRepo
    RecRepo -> DB: INSERT INTO recommendations...
    activate DB
    DB --> RecRepo: Saved
    deactivate DB
    RecRepo --> Strategy: Recommendation
    deactivate RecRepo
end

' Medical Recommendations
alt Stress level == CRITICAL
    Strategy -> Medical: generate(stressAnalysis)
    activate Medical

    Medical -> Medical: Create urgent consultation recommendation

    Medical --> Strategy: Recommendation(MEDICAL, URGENT)
    deactivate Medical

    Strategy -> RecRepo: save(recommendation)
    activate RecRepo
    RecRepo -> DB: INSERT INTO recommendations...
    activate DB
    DB --> RecRepo: Saved
    deactivate DB
    RecRepo --> Strategy: Recommendation
    deactivate RecRepo
end

' Lifestyle Recommendations
Strategy -> Lifestyle: generate(stressAnalysis)
activate Lifestyle

Lifestyle -> Lifestyle: Analyze overall pattern
Lifestyle -> Lifestyle: Create lifestyle adjustments

Lifestyle --> Strategy: Recommendation(LIFESTYLE, priority)
deactivate Lifestyle

Strategy -> RecRepo: save(recommendation)
activate RecRepo
RecRepo -> DB: INSERT INTO recommendations...
activate DB
DB --> RecRepo: Saved
deactivate DB
RecRepo --> Strategy: Recommendation
deactivate RecRepo

Strategy --> StressService: List<Recommendation>
deactivate Strategy

@enduml
```

### 5.2. Strategy Pattern для рекомендацій

Кожна категорія рекомендацій має власну стратегію:

1. **PhysicalActivityStrategy** - рекомендації щодо фізичної активності
2. **NutritionStrategy** - рекомендації щодо харчування
3. **SleepStrategy** - рекомендації для покращення сну
4. **RelaxationStrategy** - техніки релаксації
5. **MedicalStrategy** - медичні консультації
6. **LifestyleStrategy** - загальні зміни стилю життя

---

## 6. Процес перегляду історії

### 6.1. Sequence діаграма

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "BiometricService\n(Frontend)" as BioFE
participant "BiometricController" as Controller
participant "BiometricDataService" as BioService
participant "BiometricRepository" as BioRepo
participant "PostgreSQL" as DB

User -> Frontend: Navigate to /history
activate Frontend

Frontend -> Frontend: Show loading state

Frontend -> BioFE: getUserBiometricData(userId)
activate BioFE

BioFE -> Controller: GET /api/biometric/user/{userId}
activate Controller

Controller -> BioService: getBiometricDataByUserId(userId)
activate BioService

BioService -> BioRepo: findByUserIdOrderByMeasurementTimeDesc(userId)
activate BioRepo

BioRepo -> DB: SELECT * FROM biometric_data\nWHERE user_id = ?\nORDER BY measurement_time DESC
activate DB
DB --> BioRepo: List of rows
deactivate DB

BioRepo --> BioService: List<BiometricData>
deactivate BioRepo

BioService -> BioService: Convert to DTOs

BioService --> Controller: List<BiometricDataDTO>
deactivate BioService

Controller --> BioFE: 200 OK + JSON array
deactivate Controller

BioFE --> Frontend: List of biometric data
deactivate BioFE

Frontend -> Frontend: Render table with data
note right
  Columns:
  - Date/Time
  - Heart Rate
  - Blood Pressure
  - Temperature
  - Actions (View, Delete, Analyze)
end note

Frontend --> User: Display history table
deactivate Frontend

User -> Frontend: Click "View Details" on a record
activate Frontend

Frontend -> Frontend: Show modal with full details
Frontend --> User: Detailed view
deactivate Frontend

@enduml
```

---

## 7. Процес відображення Dashboard

### 7.1. Sequence діаграма

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "Multiple Services\n(Frontend)" as Services
participant "BiometricController" as BioController
participant "StressController" as StressController
participant "BiometricService" as BioService
participant "StressAnalysisService" as StressService
participant "Repositories" as Repos
participant "PostgreSQL" as DB

User -> Frontend: Navigate to /dashboard
activate Frontend

Frontend -> Frontend: Show loading state

par Parallel API calls
    Frontend -> Services: getLatestBiometricData()
    activate Services
    Services -> BioController: GET /api/biometric/latest/{userId}
    activate BioController
    BioController -> BioService: getLatestBiometricData(userId)
    activate BioService
    BioService -> Repos: findFirstByUserIdOrderByMeasurementTimeDesc(userId)
    activate Repos
    Repos -> DB: SELECT * FROM biometric_data\nWHERE user_id = ?\nORDER BY measurement_time DESC\nLIMIT 1
    activate DB
    DB --> Repos: Latest row
    deactivate DB
    Repos --> BioService: BiometricData
    deactivate Repos
    BioService --> BioController: BiometricDataDTO
    deactivate BioService
    BioController --> Services: 200 OK + JSON
    deactivate BioController
    Services --> Frontend: Latest biometric data
    deactivate Services

    Frontend -> Services: getLatestStressAnalysis()
    activate Services
    Services -> StressController: GET /api/stress/latest/{userId}
    activate StressController
    StressController -> StressService: getLatestAnalysis(userId)
    activate StressService
    StressService -> Repos: findFirstByUserIdOrderByCreatedAtDesc(userId)
    activate Repos
    Repos -> DB: SELECT * FROM stress_analysis\nWHERE user_id = ?\nORDER BY created_at DESC\nLIMIT 1
    activate DB
    DB --> Repos: Latest analysis
    deactivate DB
    Repos --> StressService: StressAnalysis
    deactivate Repos
    StressService --> StressController: StressAnalysisDTO
    deactivate StressService
    StressController --> Services: 200 OK + JSON
    deactivate StressController
    Services --> Frontend: Latest stress analysis
    deactivate Services

    Frontend -> Services: getBiometricHistory(7 days)
    activate Services
    Services -> BioController: GET /api/biometric/user/{userId}/range
    activate BioController
    BioController -> BioService: getDataInRange(userId, startDate, endDate)
    activate BioService
    BioService -> Repos: findByUserIdAndMeasurementTimeBetween(...)
    activate Repos
    Repos -> DB: SELECT * FROM biometric_data\nWHERE user_id = ? AND measurement_time BETWEEN ? AND ?
    activate DB
    DB --> Repos: List of rows
    deactivate DB
    Repos --> BioService: List<BiometricData>
    deactivate Repos
    BioService --> BioController: List<BiometricDataDTO>
    deactivate BioService
    BioController --> Services: 200 OK + JSON
    deactivate BioController
    Services --> Frontend: Historical data
    deactivate Services

    Frontend -> Services: getActiveRecommendations()
    activate Services
    Services -> StressController: GET /api/stress/{analysisId}/recommendations
    activate StressController
    StressController -> StressService: getRecommendations(analysisId)
    activate StressService
    StressService -> Repos: findByStressAnalysisIdAndCompletedFalse(...)
    activate Repos
    Repos -> DB: SELECT * FROM recommendations\nWHERE stress_analysis_id = ? AND completed = false
    activate DB
    DB --> Repos: Active recommendations
    deactivate DB
    Repos --> StressService: List<Recommendation>
    deactivate Repos
    StressService --> StressController: List<RecommendationDTO>
    deactivate StressService
    StressController --> Services: 200 OK + JSON
    deactivate StressController
    Services --> Frontend: Active recommendations
    deactivate Services
end

Frontend -> Frontend: Aggregate all data

Frontend -> Frontend: Render Dashboard:
note right
  1. Stats cards (latest values)
  2. Stress level gauge
  3. Line chart (7-day trend)
  4. Recommendations list
  5. Quick action button
end note

Frontend --> User: Display complete dashboard
deactivate Frontend

@enduml
```

---

## 8. Взаємодія з JWT автентифікацією

### 8.1. Sequence діаграма JWT потоку

```plantuml
@startuml
actor User
participant "React App" as Frontend
participant "Axios Interceptor" as Interceptor
participant "API Endpoint" as API
participant "JwtAuthenticationFilter" as JwtFilter
participant "JwtUtil" as JWT
participant "UserDetailsService" as UserDetails
participant "SecurityContext" as Security

User -> Frontend: Make authenticated request
activate Frontend

Frontend -> Interceptor: HTTP request
activate Interceptor

Interceptor -> Interceptor: Add Authorization header
note right
  Authorization: Bearer <jwt_token>
end note

Interceptor -> API: Request with JWT
activate API

API -> JwtFilter: Filter incoming request
activate JwtFilter

JwtFilter -> JwtFilter: Extract JWT from header
JwtFilter -> JWT: validateToken(token)
activate JWT

JWT -> JWT: Check expiration
JWT -> JWT: Verify signature
JWT --> JwtFilter: valid/invalid
deactivate JWT

alt Token invalid or expired
    JwtFilter --> API: 401 Unauthorized
    API --> Interceptor: 401 response
    Interceptor -> Interceptor: Catch 401 error
    Interceptor -> Interceptor: Clear localStorage
    Interceptor -> Frontend: Redirect to /login
    Frontend --> User: Login page
else Token valid
    JwtFilter -> JWT: extractUsername(token)
    activate JWT
    JWT --> JwtFilter: username
    deactivate JWT

    JwtFilter -> UserDetails: loadUserByUsername(username)
    activate UserDetails
    UserDetails --> JwtFilter: UserDetails
    deactivate UserDetails

    JwtFilter -> Security: Set authentication
    activate Security
    Security --> JwtFilter: Success
    deactivate Security

    JwtFilter --> API: Continue to controller
    deactivate JwtFilter

    API -> API: Process business logic
    API --> Interceptor: 200 OK + Data
    deactivate API

    Interceptor --> Frontend: Response data
    deactivate Interceptor

    Frontend --> User: Display result
    deactivate Frontend
end

@enduml
```

---

## 9. Обробка помилок

### 9.1. Sequence діаграма обробки помилок

```plantuml
@startuml
participant "Frontend" as FE
participant "API Endpoint" as API
participant "Service Layer" as Service
participant "Exception Handler" as ExHandler

FE -> API: Request
activate API

API -> Service: Business operation
activate Service

alt Business logic error
    Service -> Service: Validation fails
    Service --> API: throw CustomException
    deactivate Service

    API -> ExHandler: Handle exception
    activate ExHandler

    ExHandler -> ExHandler: Log error
    ExHandler -> ExHandler: Create ErrorResponse
    note right
      {
        "timestamp": "2025-11-24T10:00:00",
        "status": 400,
        "error": "Bad Request",
        "message": "Invalid data",
        "path": "/api/biometric"
      }
    end note

    ExHandler --> API: ErrorResponse
    deactivate ExHandler

    API --> FE: 400 Bad Request + JSON
    deactivate API

    FE -> FE: Display error message
    FE -> FE: Show notification

else Database error
    Service -> Service: DB operation fails
    Service --> API: throw DataAccessException
    deactivate Service

    API -> ExHandler: Handle exception
    activate ExHandler
    ExHandler -> ExHandler: Log error
    ExHandler --> API: ErrorResponse
    deactivate ExHandler

    API --> FE: 500 Internal Server Error
    deactivate API

    FE -> FE: Show generic error message

else Success
    Service --> API: Success data
    deactivate Service
    API --> FE: 200 OK + Data
    deactivate API
    FE -> FE: Display data
end

@enduml
```

---

## 10. Висновки

Sequence діаграми демонструють:

1. **Повний потік даних** від користувача до БД і назад
2. **Взаємодію компонентів**: Frontend → Controller → Service → Repository → DB
3. **Асинхронну комунікацію** через REST API
4. **Безпеку**: JWT автентифікація та авторизація
5. **Обробку помилок**: різні сценарії помилок та їх обробка
6. **Складну бізнес-логіку**: аналіз стресу з множинними розрахунками
7. **Паралельні запити**: оптимізація завантаження Dashboard
8. **Паттерни проектування**: Strategy для рекомендацій

### Ключові процеси:
- ✅ Реєстрація та автентифікація з JWT
- ✅ Додавання та валідація біометричних даних
- ✅ Складний аналіз стресу з компонентними оцінками
- ✅ Генерація персоналізованих рекомендацій
- ✅ Відображення історії та Dashboard
- ✅ Обробка помилок на всіх рівнях

Ці діаграми можуть бути використані для:
- Розуміння архітектури системи
- Документування API взаємодій
- Планування тестування
- Навчання нових розробників
- Презентації проекту

**Примітка**: Всі діаграми створені у форматі PlantUML і можуть бути згенеровані у графічному вигляді.
