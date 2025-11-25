@echo off
echo ============================================
echo ПОВНА ОЧИСТКА І ПЕРЕБУДОВА ПРОЕКТУ
echo ============================================

echo.
echo [1/5] Зупинка контейнерів...
docker-compose down

echo.
echo [2/5] Видалення старих images...
docker rmi biometric-stress-analysis-full-backend biometric-stress-analysis-full-frontend 2>NUL

echo.
echo [3/5] Очистка Docker build cache...
docker builder prune -f

echo.
echo [4/5] Видалення старої бази даних...
docker volume rm biometric-stress-analysis-full_postgres_data 2>NUL

echo.
echo [5/5] Перебудова і запуск з нуля...
docker-compose up --build

echo.
echo ============================================
echo ГОТОВО!
echo ============================================
