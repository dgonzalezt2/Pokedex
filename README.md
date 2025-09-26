# Pokedex Android

Aplicación nativa Android para consultar, agregar y proteger un carrito de compras de Pokémon.

## APIs utilizadas
- **PokeAPI**: [https://pokeapi.co/](https://pokeapi.co/) (consulta de datos de Pokémon)
- **Room**: Persistencia local del carrito
- **Biometric API**: Autenticación biométrica nativa (huella dactilar)
- **Jetpack Compose**: UI declarativa moderna

## Instrucciones para correr el proyecto
1. Clona el repositorio:
   ```bash
   git clone https://github.com/dgonzalezt2/pokedex.git
   ```
2. Abre el proyecto en Android Studio.
3. Configura un emulador o dispositivo físico con Android 8.0+.
4. Sincroniza dependencias (Gradle Sync).
5. Ejecuta el proyecto (Run).
6. Para probar la autenticación biométrica en el emulador:
   - Abre la app de "Configuración" del emulador y agrega una huella digital (puedes buscar "Security" o "Biometrics").
   - Para simular la huella, abre el panel de controles del emulador:
     - Ve a **Extended Controls** (Ctrl+Shift+M o desde el menú del emulador).
     - Selecciona **Fingerprint** en la barra lateral.
     - Haz clic en **Touch Sensor** para simular la autenticación.

   Ejemplo visual:
   <p align="center">
     <img src="https://github.com/user-attachments/assets/5622d03e-e5ed-4447-a28c-4b6bc6e70b7d" width="220" alt="Abrir controles extendidos" />
     <img src="https://github.com/user-attachments/assets/62346835-c2f5-4f3c-b324-aaa064f08b5e" width="220" alt="Simular huella digital" />
   </p>
   <p align="center">
     <span style="font-size:12px;">Abrir controles extendidos</span>
     <span style="font-size:12px; margin-left:40px;">Simular huella digital</span>
   </p>

7. Si usas dispositivo físico, asegúrate de tener una huella registrada en el sistema.

### Recomendaciones para desarrollo y pruebas
- Usa un emulador con Google APIs y Play Store para mejor compatibilidad.
- Si tienes problemas con la biometría, revisa los permisos en el sistema y en la app.
- Para pruebas automáticas, puedes simular la huella desde los controles del emulador.

## Arquitectura y decisiones técnicas
- **MVVM (Model-View-ViewModel)**: Separación clara entre UI, lógica y datos.
- **Jetpack Compose**: UI reactiva y fácil de mantener.
- **Room**: Persistencia local para el carrito, permitiendo que los productos agregados permanezcan entre sesiones.
- **StateFlow/LiveData**: Observabilidad y reactividad en la UI.
- **BiometricPrompt**: Protección nativa del acceso al carrito.

### Justificación de la funcionalidad nativa (biometría)
- **Valor de negocio**: Proteger el carrito con biometría mejora la confianza del usuario, evitando compras accidentales o acceso no autorizado. Es especialmente útil en apps donde el carrito puede estar persistente entre sesiones o en dispositivos compartidos.
- **Impacto UX**: Baja fricción (desbloqueo rápido) y alto beneficio (confianza y seguridad). El usuario accede al carrito de forma ágil y segura, sin contraseñas adicionales.

## Enfoque de diseño visual
- **Minimalista y funcional**: Uso de Jetpack Compose para una interfaz limpia, con componentes nativos y feedback inmediato (snackbar).
- **Notificaciones internas**: Todas las acciones relevantes (agregar/eliminar) muestran mensajes visuales en la app, evitando distracciones del sistema.
- **Consistencia**: El diseño mantiene la coherencia entre pantallas, con navegación intuitiva y accesibilidad.
- **Colores y tipografía**: Se usan estilos nativos de Material Design para facilitar la lectura y navegación.

## Vistas de la aplicación

<p align="center">
  <img src="https://github.com/user-attachments/assets/ab536a87-c440-46be-8e2f-8bad4a1a9be1" width="250" />
  <img src="https://github.com/user-attachments/assets/0633f8c2-56ce-4ff7-8aea-dd6ce4beca10" width="250" />
  <img src="https://github.com/user-attachments/assets/04a5efe8-3d51-4467-8a7d-6fd9e0e311a5" width="250" />
  <img src="https://github.com/user-attachments/assets/73d312a8-2d1d-4521-96d0-ea3374068dcc" width="250" />
</p>

---

Desarrollado por David Gonzalez Tamayo.
