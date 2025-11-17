# TODO: Implementar restricciones de acceso por rol

## Información Recopilada
- **SecurityConfig.java**: Actualmente, todas las rutas excepto /auth/** requieren rol ADMIN. Necesitamos cambiar para que solo rutas de usuarios (/api/usuarios/**) requieran ADMIN, y otras rutas permitan USER también.
- **Usuario.java**: Implementa UserDetails con roles ADMIN y USER, usando "ROLE_" + rol.name().
- **Rol.java**: Enum con ADMIN y USER.
- **Controladores**: UsuarioController maneja /api/usuarios/**, CamaraController /api/camaras/**, etc. Solo usuarios necesitan restricción específica.

## Plan
- Modificar SecurityConfig.java para ajustar authorizeHttpRequests:
  - /auth/**: permitAll
  - /api/usuarios/**: hasRole("ADMIN")
  - anyRequest: hasAnyRole("ADMIN", "USER")

## Archivos a Editar
- Proyecto_TA/src/main/java/org/example/proyecto_ta/config/SecurityConfig.java

## Pasos de Seguimiento
- [x] Editar SecurityConfig.java con los nuevos permisos.
- [x] Verificar que el cambio compile y funcione correctamente (puede requerir pruebas manuales).
- [x] Actualizar AuthContext para incluir rol decodificado del JWT.
- [x] Modificar App.tsx para restringir rutas de administración solo a ADMIN.
- [x] Corregir imports innecesarios de React en componentes.
- [x] Verificar que el frontend compile correctamente.
