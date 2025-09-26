# Pokedex Android

Aplicación nativa Android para consultar, agregar y proteger un carrito de compras de Pokémon.

## APIs utilizadas
- **PokeAPI**: https://pokeapi.co/ (consulta de datos de Pokémon)
- **Room**: Persistencia local del carrito
- **Biometric API**: Autenticación biométrica nativa (huella dactilar)
- **Jetpack Compose**: UI declarativa moderna

## Instrucciones para correr el proyecto
1. Clona el repositorio:
   ```
   git clone https://github.com/dgonzalezt2/pokedex.git
   ```
2. Abre el proyecto en Android Studio.
3. Configura un emulador o dispositivo físico con Android 8.0+.
4. Sincroniza dependencias (Gradle Sync).
5. Ejecuta el proyecto (Run).

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

---

Desarrollado por David Gonzalez Tamayo.

