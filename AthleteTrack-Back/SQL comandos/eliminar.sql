-- ================================================
-- ELIMINAR DATOS
-- ================================================

-- IMPORTANTE: Ten cuidado al ejecutar estos comandos, son IRREVERSIBLES

-- Eliminar un usuario específico por username
DELETE FROM USERS WHERE USERNAME = 'username_a_eliminar';

-- Eliminar un usuario por email
DELETE FROM USERS WHERE EMAIL = 'usuario@email.com';

-- Eliminar un usuario por ID
DELETE FROM USERS WHERE ID = 123;

-- Eliminar todos los entrenamientos de un usuario
DELETE FROM WORKOUTS WHERE USER_ID = 1;

-- Eliminar un evento específico
DELETE FROM EVENTS WHERE ID = 1;

-- Eliminar todas las inscripciones de un evento
DELETE FROM EVENT_REGISTRATIONS WHERE EVENT_ID = 1;

-- CUIDADO: Eliminar TODOS los datos (reiniciar BD)
-- Descomenta solo si estás SEGURO
-- DELETE FROM EVENT_REGISTRATIONS;
-- DELETE FROM EXERCISES;
-- DELETE FROM WORKOUTS;
-- DELETE FROM EVENTS;
-- DELETE FROM USERS;

-- Verificar datos restantes
SELECT COUNT(*) AS TOTAL_USUARIOS FROM USERS;
SELECT COUNT(*) AS TOTAL_EVENTOS FROM EVENTS;
SELECT COUNT(*) AS TOTAL_ENTRENAMIENTOS FROM WORKOUTS;
