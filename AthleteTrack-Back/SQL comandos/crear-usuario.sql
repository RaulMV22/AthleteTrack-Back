-- ================================================
-- CREAR USUARIOS MANUALMENTE
-- ================================================

-- Crear usuario básico
-- NOTA: La contraseña debe estar en formato BCrypt
-- Puedes generar un hash BCrypt en: https://bcrypt-generator.com/
INSERT INTO USERS (EMAIL, PASSWORD, NAME, USERNAME, ROLE, CREATED_AT) 
VALUES (
    'nuevo@email.com',
    '$2a$10$ejemplo.de.hash.bcrypt.aqui',  -- Reemplaza con hash BCrypt real
    'Nombre Completo',
    'username123',
    'USER',
    CURRENT_TIMESTAMP
);

-- Crear usuario administrador
INSERT INTO USERS (EMAIL, PASSWORD, NAME, USERNAME, ROLE, CREATED_AT) 
VALUES (
    'admin@email.com',
    '$2a$10$ejemplo.de.hash.bcrypt.aqui',  -- Reemplaza con hash BCrypt real
    'Administrador',
    'admin',
    'ADMIN',
    CURRENT_TIMESTAMP
);

-- Verificar usuarios creados
SELECT ID, USERNAME, EMAIL, NAME, ROLE, CREATED_AT FROM USERS ORDER BY CREATED_AT DESC LIMIT 5;
