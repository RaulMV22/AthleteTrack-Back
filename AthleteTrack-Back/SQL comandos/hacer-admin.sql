-- ================================================
-- HACER USUARIO ADMINISTRADOR
-- ================================================

-- Hacer admin al usuario @raulmv1222
UPDATE USERS SET ROLE = 'ADMIN' WHERE USERNAME = 'raulmv1222';

-- Hacer admin a un usuario por email
UPDATE USERS SET ROLE = 'ADMIN' WHERE EMAIL = 'usuario@email.com';

-- Hacer admin a un usuario por ID
UPDATE USERS SET ROLE = 'ADMIN' WHERE ID = 1;

-- Verificar cambio
SELECT ID, USERNAME, EMAIL, NAME, ROLE FROM USERS WHERE USERNAME = 'raulmv1222';
