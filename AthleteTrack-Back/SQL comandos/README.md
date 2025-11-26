# Scripts SQL - AthleteTrack

Esta carpeta contiene scripts SQL útiles para administrar la base de datos H2 del proyecto AthleteTrack.

## Cómo usar estos scripts

### 1. Acceder a la Consola H2

1. Asegúrate de que el backend esté corriendo (`mvn spring-boot:run`)
2. Abre tu navegador en: `http://localhost:8080/h2-console`
3. Configura la conexión:
   - **JDBC URL**: `jdbc:h2:file:./data/athletetrack`
   - **User Name**: `sa`
   - **Password**: (dejar vacío)
4. Click en **Connect**

### 2. Ejecutar Scripts

Copia y pega el contenido de los archivos SQL en la consola H2 y ejecuta.

---

## Archivos Disponibles

### `hacer-admin.sql`
Convierte usuarios normales en administradores.

**Uso común:**
```sql
UPDATE USERS SET ROLE = 'ADMIN' WHERE USERNAME = 'raulmv1222';
```

### `quitar-admin.sql`
Quita privilegios de administrador a usuarios.

**Uso común:**
```sql
UPDATE USERS SET ROLE = 'USER' WHERE USERNAME = 'username';
```

### `crear-usuario.sql`
Crea usuarios manualmente en la base de datos.

**Nota:** Necesitas generar un hash BCrypt para la contraseña.
- Usar: https://bcrypt-generator.com/
- Complejidad recomendada: 10 rounds

### `eliminar.sql`
Elimina usuarios, eventos o entrenamientos.

**CUIDADO:** Estos comandos son IRREVERSIBLES.

### `consultas-utiles.sql`
Consultas para ver y analizar datos del sistema.

**Ejemplos:**
- Ver todos los usuarios
- Ver estadísticas generales
- Buscar usuarios por email o nombre
- Ver entrenamientos de un usuario

---

## Comandos Rápidos

### Hacer admin al usuario actual
```sql
UPDATE USERS SET ROLE = 'ADMIN' WHERE USERNAME = 'raulmv1222';
```

### Ver todos los admins
```sql
SELECT * FROM USERS WHERE ROLE = 'ADMIN';
```

### Ver estadísticas del sistema
```sql
SELECT 
    (SELECT COUNT(*) FROM USERS) AS TOTAL_USUARIOS,
    (SELECT COUNT(*) FROM EVENTS) AS TOTAL_EVENTOS,
    (SELECT COUNT(*) FROM WORKOUTS) AS TOTAL_ENTRENAMIENTOS;
```

---

## Advertencias Importantes

1. **Backups**: H2 guarda la base de datos en `./data/athletetrack.mv.db`. Haz copias antes de cambios importantes.

2. **Contraseñas**: Al crear usuarios manualmente, SIEMPRE usa hash BCrypt, nunca guardes contraseñas en texto plano.

3. **Eliminaciones**: Los comandos DELETE son irreversibles. Verifica antes de ejecutar.

4. **Producción**: Estos scripts son para desarrollo. En producción, usa una base de datos real (PostgreSQL, MySQL).

---

## Recursos

- [Generador BCrypt](https://bcrypt-generator.com/)
- [Documentación H2](https://www.h2database.com/html/tutorial.html)
- [SQL Tutorial](https://www.w3schools.com/sql/)
