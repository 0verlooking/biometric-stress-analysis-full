-- Тестові дані для системи аналізу біометричних показників стресу
-- Паролі всіх користувачів: "password123"
-- BCrypt hash: $2a$10$rQ8ZqPWW.uKZ7RqvW7a7V.xJDr1H.YXj7HqOYcQk1Kx8YvBqYqKZW

-- ============================================
-- Очищення даних (якщо потрібно перезапустити)
-- ============================================
-- TRUNCATE TABLE recommendations CASCADE;
-- TRUNCATE TABLE stress_analysis CASCADE;
-- TRUNCATE TABLE biometric_data CASCADE;
-- TRUNCATE TABLE users CASCADE;

-- ============================================
-- 1. Користувачі
-- ============================================

-- Адміністратор
INSERT INTO users (username, email, password, first_name, last_name, gender, date_of_birth, age, phone_number, role, active, created_at, updated_at)
VALUES
('admin', 'admin@biometric.com', '$2a$10$rQ8ZqPWW.uKZ7RqvW7a7V.xJDr1H.YXj7HqOYcQk1Kx8YvBqYqKZW',
 'Адміністратор', 'Системи', 'MALE', '1985-05-15', 38, '+380501234567', 'ADMIN', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Лікар
INSERT INTO users (username, email, password, first_name, last_name, gender, date_of_birth, age, phone_number, role, active, created_at, updated_at)
VALUES
('doctor_ivan', 'doctor@biometric.com', '$2a$10$rQ8ZqPWW.uKZ7RqvW7a7V.xJDr1H.YXj7HqOYcQk1Kx8YvBqYqKZW',
 'Іван', 'Петренко', 'MALE', '1980-03-20', 43, '+380502345678', 'DOCTOR', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Звичайні користувачі
INSERT INTO users (username, email, password, first_name, last_name, gender, date_of_birth, age, phone_number, role, active, created_at, updated_at)
VALUES
('john_doe', 'john@example.com', '$2a$10$rQ8ZqPWW.uKZ7RqvW7a7V.xJDr1H.YXj7HqOYcQk1Kx8YvBqYqKZW',
 'Джон', 'Доу', 'MALE', '1990-07-10', 33, '+380503456789', 'USER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('maria_smith', 'maria@example.com', '$2a$10$rQ8ZqPWW.uKZ7RqvW7a7V.xJDr1H.YXj7HqOYcQk1Kx8YvBqYqKZW',
 'Марія', 'Смит', 'FEMALE', '1995-11-25', 28, '+380504567890', 'USER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('olena_koval', 'olena@example.com', '$2a$10$rQ8ZqPWW.uKZ7RqvW7a7V.xJDr1H.YXj7HqOYcQk1Kx8YvBqYqKZW',
 'Олена', 'Коваль', 'FEMALE', '1988-02-14', 35, '+380505678901', 'USER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 2. Біометричні дані
-- ============================================

-- Дані для john_doe (ID=3) - НИЗЬКИЙ СТРЕС
INSERT INTO biometric_data (user_id, measurement_time, heart_rate, systolic_pressure, diastolic_pressure, body_temperature, cortisol_level, sleep_hours, sleep_quality, respiratory_rate, oxygen_saturation, activity_level, notes, created_at, updated_at)
VALUES
(3, CURRENT_TIMESTAMP - INTERVAL '5 hours', 72, 118, 78, 36.6, 250.0, 7.5, 'GOOD', 16, 98.0, 8000, 'Відчуваю себе добре', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, CURRENT_TIMESTAMP - INTERVAL '1 day', 75, 120, 80, 36.7, 280.0, 7.0, 'GOOD', 15, 97.5, 7500, 'Нормальний день', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, CURRENT_TIMESTAMP - INTERVAL '2 days', 70, 115, 75, 36.5, 240.0, 8.0, 'EXCELLENT', 14, 98.5, 9000, 'Відмінне самопочуття', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Дані для maria_smith (ID=4) - ПОМІРНИЙ СТРЕС
INSERT INTO biometric_data (user_id, measurement_time, heart_rate, systolic_pressure, diastolic_pressure, body_temperature, cortisol_level, sleep_hours, sleep_quality, respiratory_rate, oxygen_saturation, activity_level, notes, created_at, updated_at)
VALUES
(4, CURRENT_TIMESTAMP - INTERVAL '3 hours', 85, 130, 85, 36.9, 450.0, 6.0, 'FAIR', 18, 96.0, 5000, 'Трохи втомлена', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, CURRENT_TIMESTAMP - INTERVAL '1 day', 88, 135, 88, 37.0, 480.0, 5.5, 'FAIR', 19, 95.5, 4500, 'Багато роботи', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, CURRENT_TIMESTAMP - INTERVAL '2 days', 82, 128, 82, 36.8, 420.0, 6.5, 'GOOD', 17, 96.5, 5500, 'Стресовий тиждень', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Дані для olena_koval (ID=5) - ВИСОКИЙ СТРЕС
INSERT INTO biometric_data (user_id, measurement_time, heart_rate, systolic_pressure, diastolic_pressure, body_temperature, cortisol_level, sleep_hours, sleep_quality, respiratory_rate, oxygen_saturation, activity_level, notes, created_at, updated_at)
VALUES
(5, CURRENT_TIMESTAMP - INTERVAL '2 hours', 95, 145, 95, 37.2, 650.0, 5.0, 'POOR', 22, 94.0, 3000, 'Дуже втомлена та напружена', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, CURRENT_TIMESTAMP - INTERVAL '1 day', 98, 148, 98, 37.3, 680.0, 4.5, 'POOR', 23, 93.5, 2500, 'Погано спала', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, CURRENT_TIMESTAMP - INTERVAL '3 days', 92, 142, 92, 37.1, 620.0, 5.5, 'POOR', 21, 94.5, 3500, 'Проблеми на роботі', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 3. Аналізи стресу
-- ============================================

-- Аналіз для john_doe - НИЗЬКИЙ СТРЕС
INSERT INTO stress_analysis (user_id, biometric_data_id, stress_level, stress_score, cardiovascular_score, thermal_score, biochemical_score, sleep_score, respiratory_score, analysis, created_at, updated_at)
VALUES
(3, 1, 'LOW', 22.5, 18.0, 15.0, 20.0, 12.0, 10.0,
 'Ваші показники в межах норми. Рівень стресу низький, що свідчить про хороше фізичне та емоційне самопочуття. Продовжуйте дотримуватися здорового способу життя.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 2, 'LOW', 25.0, 20.0, 18.0, 22.0, 15.0, 12.0,
 'Показники в нормі. Спостерігається невелике підвищення деяких параметрів, але загалом стан добрий.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Аналіз для maria_smith - ПОМІРНИЙ СТРЕС
INSERT INTO stress_analysis (user_id, biometric_data_id, stress_level, stress_score, cardiovascular_score, thermal_score, biochemical_score, sleep_score, respiratory_score, analysis, created_at, updated_at)
VALUES
(4, 4, 'MODERATE', 42.5, 35.0, 28.0, 45.0, 38.0, 32.0,
 'Виявлено помірний рівень стресу. Рекомендується звернути увагу на якість сну та знизити рівень навантаження. Підвищений кортизол може свідчити про хронічний стрес.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(4, 5, 'MODERATE', 45.8, 38.0, 32.0, 48.0, 42.0, 35.0,
 'Стрес зростає. Необхідно вжити заходів для відновлення. Погіршення якості сну негативно впливає на загальний стан.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Аналіз для olena_koval - ВИСОКИЙ СТРЕС
INSERT INTO stress_analysis (user_id, biometric_data_id, stress_level, stress_score, cardiovascular_score, thermal_score, biochemical_score, sleep_score, respiratory_score, analysis, created_at, updated_at)
VALUES
(5, 7, 'HIGH', 68.5, 65.0, 52.0, 75.0, 68.0, 58.0,
 'УВАГА! Виявлено високий рівень стресу. Значно підвищені показники серцево-судинної системи та кортизолу. Рекомендується термінова консультація лікаря та відпочинок.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(5, 8, 'HIGH', 72.3, 68.0, 58.0, 78.0, 72.0, 62.0,
 'КРИТИЧНА СИТУАЦІЯ! Показники погіршуються. Необхідна негайна консультація лікаря. Дуже низька якість сну та високий пульс.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 4. Рекомендації
-- ============================================

-- Рекомендації для john_doe (LOW stress)
INSERT INTO recommendations (stress_analysis_id, category, title, description, priority, completed, created_at, updated_at)
VALUES
(1, 'PHYSICAL_ACTIVITY', 'Підтримка активності',
 'Продовжуйте займатися фізичними вправами 3-4 рази на тиждень по 30-40 хвилин. Це допоможе підтримувати хорошу форму.',
 'MEDIUM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(1, 'NUTRITION', 'Збалансоване харчування',
 'Дотримуйтесь збалансованого раціону з достатньою кількістю овочів, фруктів та білків.',
 'LOW', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(1, 'LIFESTYLE', 'Регулярний моніторинг',
 'Продовжуйте регулярно вимірювати показники для відстеження стану здоров''я.',
 'LOW', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Рекомендації для maria_smith (MODERATE stress)
INSERT INTO recommendations (stress_analysis_id, category, title, description, priority, completed, created_at, updated_at)
VALUES
(3, 'SLEEP', 'Покращення якості сну',
 'Намагайтеся лягати спати в один і той же час. Уникайте гаджетів за годину до сну. Створіть комфортні умови в спальні.',
 'HIGH', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 'RELAXATION', 'Техніки релаксації',
 'Практикуйте глибоке дихання 10-15 хвилин на день. Спробуйте медитацію або йогу для зниження стресу.',
 'HIGH', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 'PHYSICAL_ACTIVITY', 'Помірна активність',
 'Додайте щоденні прогулянки на свіжому повітрі протягом 20-30 хвилин. Уникайте інтенсивних тренувань ввечері.',
 'MEDIUM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 'NUTRITION', 'Зменшення кофеїну',
 'Обмежте споживання кави та енергетичних напоїв, особливо після обіду.',
 'MEDIUM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Рекомендації для olena_koval (HIGH stress)
INSERT INTO recommendations (stress_analysis_id, category, title, description, priority, completed, created_at, updated_at)
VALUES
(5, 'MEDICAL', 'ТЕРМІНОВА консультація лікаря',
 'Необхідно терміново звернутися до лікаря для детального обстеження. Ваші показники вимагають медичного втручання.',
 'URGENT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(5, 'SLEEP', 'Нормалізація сну',
 'Критично важливо налагодити режим сну. Спробуйте м''які снодійні за рекомендацією лікаря. Сон має бути 7-8 годин.',
 'URGENT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(5, 'LIFESTYLE', 'Зменшення навантаження',
 'Негайно зменшіть робоче навантаження. Візьміть відпустку або лікарняний. Стрес досяг критичного рівня.',
 'URGENT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(5, 'RELAXATION', 'Професійна психологічна допомога',
 'Звернітеся до психолога або психотерапевта. Необхідна професійна допомога в управлінні стресом.',
 'URGENT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(5, 'PHYSICAL_ACTIVITY', 'Легка фізична активність',
 'Тільки легкі прогулянки на свіжому повітрі. Уникайте інтенсивних навантажень до нормалізації стану.',
 'HIGH', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- Підтвердження завантаження даних
-- ============================================
DO $$
BEGIN
    RAISE NOTICE 'Тестові дані успішно завантажено!';
    RAISE NOTICE 'Користувачів: 5 (admin, doctor, john_doe, maria_smith, olena_koval)';
    RAISE NOTICE 'Біометричних записів: 9';
    RAISE NOTICE 'Аналізів стресу: 6';
    RAISE NOTICE 'Рекомендацій: 12';
    RAISE NOTICE '';
    RAISE NOTICE '=== ОБЛІКОВІ ДАНІ ДЛЯ ВХОДУ ===';
    RAISE NOTICE 'Всі користувачі мають пароль: password123';
    RAISE NOTICE '';
    RAISE NOTICE 'admin@biometric.com - Адміністратор';
    RAISE NOTICE 'doctor@biometric.com - Лікар';
    RAISE NOTICE 'john@example.com - Користувач (низький стрес)';
    RAISE NOTICE 'maria@example.com - Користувач (помірний стрес)';
    RAISE NOTICE 'olena@example.com - Користувач (високий стрес)';
END $$;
