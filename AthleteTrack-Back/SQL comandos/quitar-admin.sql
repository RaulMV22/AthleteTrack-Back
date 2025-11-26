-- ================================================
-- QUITAR PRIVILEGIOS DE ADMINISTRADOR
-- ================================================

-- Quitar admin a un usuario por username
UPDATE USERS SET ROLE = 'USER' WHERE USERNAME = 'raulmv1222';

-- Quitar admin a un usuario por email
UPDATE USERS SET ROLE = 'USER' WHERE EMAIL = 'usuario@email.com';

-- Quitar admin a un usuario por ID
UPDATE USERS SET ROLE = 'USER' WHERE ID = 1;

-- Verificar cambio
SELECT ID, USERNAME, EMAIL, NAME, ROLE FROM USERS WHERE USERNAME = 'raulmv1222';
