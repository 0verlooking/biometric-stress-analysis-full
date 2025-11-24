# Biometric Stress Analysis - Frontend

Фронтенд-додаток для системи біометричного аналізу стресу, побудований на React + TypeScript + Vite.

## 🚀 Функціонал

- **Автентифікація**: Реєстрація та вхід користувачів з JWT токенами
- **Dashboard**: Введення біометричних даних та отримання аналізу стресу
- **Історія**: Перегляд попередніх аналізів з візуалізацією динаміки
- **Responsive Design**: Адаптивний інтерфейс для всіх пристроїв

## 🛠️ Технології

- **React 19** - UI фреймворк
- **TypeScript** - Типізація
- **Vite** - Build tool
- **React Router** - Маршрутизація
- **Axios** - HTTP клієнт
- **Recharts** - Візуалізація даних
- **Lucide React** - Іконки
- **Docker** - Контейнеризація

## 📋 Передумови

- Node.js 18+ та npm
- Docker та Docker Compose (для контейнерного запуску)
- Backend API (https://github.com/0verlooking/biometric-stress-analysis-backend)

## 🔧 Встановлення

### Локальний запуск

1. Клонуйте репозиторій:
```bash
git clone https://github.com/0verlooking/biometric-stress-analysis-frontend.git
cd biometric-stress-analysis-frontend
```

2. Встановіть залежності:
```bash
npm install
```

3. Налаштуйте змінні середовища:
```bash
cp .env.example .env
# Відредагуйте .env файл за необхідності
```

4. Запустіть в режимі розробки:
```bash
npm run dev
```

Додаток буде доступний за адресою: http://localhost:5173

### Запуск з Docker

1. Побудуйте образ:
```bash
docker build -t biometric-frontend .
```

2. Запустіть контейнер:
```bash
docker run -p 3000:80 biometric-frontend
```

### Запуск з Docker Compose (Full Stack)

Для запуску разом з backend та базою даних:

```bash
docker-compose up -d
```

Сервіси будуть доступні за адресами:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432

## 📦 Build для Production

```bash
npm run build
```

Результат буде в папці `dist/`.

## 🏗️ Структура проекту

```
src/
├── components/          # React компоненти
│   ├── Layout.tsx      # Головний layout з навігацією
│   └── ProtectedRoute.tsx  # Захищені маршрути
├── contexts/           # React Context
│   └── AuthContext.tsx # Контекст автентифікації
├── pages/              # Сторінки
│   ├── LoginPage.tsx   # Сторінка входу
│   ├── RegisterPage.tsx # Сторінка реєстрації
│   ├── DashboardPage.tsx # Головна панель
│   └── HistoryPage.tsx # Історія аналізів
├── services/           # API сервіси
│   ├── api.config.ts   # Конфігурація Axios
│   ├── auth.service.ts # Сервіс автентифікації
│   ├── biometric.service.ts # Сервіс біометричних даних
│   └── stress.service.ts    # Сервіс аналізу стресу
├── types/              # TypeScript типи
│   └── api.types.ts    # Типи API моделей
├── App.tsx             # Головний компонент
├── App.css             # Стилі
└── main.tsx            # Точка входу
```

## 🔐 Автентифікація

Додаток використовує JWT токени для автентифікації:
- Токени зберігаються в localStorage
- Автоматичне додавання токену до HTTP заголовків
- Перенаправлення на сторінку входу при 401 помилці

## 📊 API Endpoints

Frontend інтегрується з наступними backend endpoints:

### Автентифікація
- `POST /api/auth/register` - Реєстрація
- `POST /api/auth/login` - Вхід

### Біометричні дані
- `POST /api/biometric-data` - Створення запису
- `GET /api/biometric-data/user/{userId}` - Історія даних
- `GET /api/biometric-data/user/{userId}/latest` - Останні дані

### Аналіз стресу
- `POST /api/stress-analysis/analyze/{biometricDataId}` - Аналіз
- `GET /api/stress-analysis/user/{userId}` - Історія аналізів
- `GET /api/stress-analysis/user/{userId}/average-score` - Середня оцінка

## 🎨 Кольорова схема

- Primary: `#8b5cf6` (Фіолетовий)
- Success: `#22c55e` (Зелений)
- Danger: `#ef4444` (Червоний)
- Warning: `#f59e0b` (Помаранчевий)

## 📝 Ліцензія

MIT

## 👨‍💻 Автор

Курсова робота з розробки веб-додатків
