🚦 Syntra
Reporta. Conecta. Mejora tu ciudad.

🌍 ¿Qué es Syntra?

Syntra es una aplicación móvil que une a la ciudadanía con las autoridades de tránsito.
Permite reportar semáforos dañados o con fallas en tiempo real, enviando ubicación, descripción y evidencia fotográfica para que los equipos técnicos puedan actuar de forma rápida y eficiente.

Su meta es simple pero poderosa:
convertir cada reporte ciudadano en una acción que mejore la movilidad urbana.

🎯 Misión del proyecto

Promover una cultura vial más segura y colaborativa, donde la tecnología y la comunidad trabajen juntas para construir ciudades más inteligentes, conectadas y sostenibles.

⚙️ Principales características

✨ Interfaz intuitiva: Diseño limpio, pensado para que cualquier usuario pueda reportar en segundos.
📍 Ubicación automática: Detección GPS del semáforo con falla.
📸 Soporte visual: Adjunta fotos del incidente o daño.
📊 Seguimiento en tiempo real: Consulta el estado de tus reportes y su avance.
🚓 Panel para autoridades: Visualización centralizada de los reportes, con filtros por zona o tipo de falla.
🔔 Notificaciones inteligentes: Recibe actualizaciones del estado de tu reporte.
🧠 Gestión de datos: Base sólida para análisis de tendencias, mantenimiento predictivo y planificación urbana.

🧩 Arquitectura del sistema
[ Usuario Ciudadano ] → [ API REST / Firebase ] → [ Base de Datos ]
                                     ↓
                          [ Panel de Control - Tránsito ]
El flujo es bidireccional: los usuarios reportan, el tránsito responde y la app mantiene la comunicación activa entre ambos.

💡 Tecnologías utilizadas

| Módulo                   | Tecnologías                                     |
| ------------------------ | ----------------------------------------------- |
| **Frontend**             | Android Studio (Jetpack Compose), Figma (UI/UX) |
| **Backend**              | Firebase / Node.js / API REST                   |
| **Base de datos**        | Firestore o PostgreSQL                          |
| **Mapas**                | Google Maps API                                 |
| **Visualización futura** | Power BI / Grafana                              |
| **Control de versiones** | Git + GitHub                                    |

📚 Documentación adicional

- [Guía para Renombrar Commits](COMMIT_RENAME_GUIDE.md): Mejores prácticas para mensajes de commit y cómo modificarlos.

👥 Roles de usuario

👤 Ciudadano:

Envía reportes de fallas en semáforos.
Adjunta evidencia y localización.
Consulta el estado de sus reportes.

🛠️ Autoridad de Tránsito:

Accede a un panel centralizado con todos los reportes.
Cambia el estado de cada caso (Pendiente / En revisión / Solucionado).
Gestiona mantenimiento y prioriza zonas críticas.

🚀 Próximos pasos

🔔 Notificaciones push en tiempo real.
🗺️ Mapa dinámico con calor de reportes.
📈 Dashboard estadístico para el análisis de patrones.
🤖 IA para detectar fallas recurrentes o zonas críticas.
🧠 Impacto social

Syntra no es solo una app: es un puente entre la comunidad y las autoridades.
Cada reporte es una voz ciudadana que impulsa el cambio, reduce accidentes y mejora la gestión vial.
Promueve una movilidad inteligente, participativa y responsable.

👨‍💻 Equipo de desarrollo

| Nombre                            | Rol                                         |
| --------------------------------- | ------------------------------------------- |
| **Juan Diego Niño Solano**        | Diseñador UX/UI & Desarrollador Fullstack   |
| **Nicole Camila Niño Ariza**      | Diseñador UX/UI & Desarrollador Fullstack   |
| **Julian David Lizcano Manrique** | Diseñador UX/UI & Desarrollador Fullstack   |
