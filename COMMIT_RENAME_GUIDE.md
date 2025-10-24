# Guía para Renombrar Commits en Git

## ¿Por qué renombrar commits?

Los mensajes de commit claros y descriptivos son esenciales para:
- Mantener un historial de proyecto comprensible
- Facilitar la colaboración en equipo
- Permitir auditorías y revisiones de código efectivas

## Métodos para Renombrar Commits

### 1. Renombrar el último commit (no pusheado)

Si el commit aún no ha sido pusheado al repositorio remoto:

```bash
git commit --amend -m "Nuevo mensaje de commit"
```

### 2. Renombrar el último commit (ya pusheado)

Si el commit ya fue pusheado y tienes permisos para force push:

```bash
git commit --amend -m "Nuevo mensaje de commit"
git push --force origin <branch-name>
```

⚠️ **Advertencia**: El force push puede afectar a otros colaboradores. Úsalo con precaución.

### 3. Renombrar commits más antiguos

Para commits que no son el más reciente, usa rebase interactivo:

```bash
git rebase -i HEAD~3  # Para los últimos 3 commits
```

En el editor que se abre:
1. Cambia `pick` a `reword` en los commits que quieres renombrar
2. Guarda y cierra el editor
3. Se abrirá un nuevo editor para cada commit marcado como `reword`
4. Escribe el nuevo mensaje y guarda

Luego, si ya fue pusheado:
```bash
git push --force origin <branch-name>
```

### 4. Alternativa sin force push

Si no puedes hacer force push (restricciones del repositorio):

```bash
# Crea un nuevo commit que corrija el mensaje
git commit --allow-empty -m "docs: Actualizar documentación del proyecto"
```

## Mejores Prácticas para Mensajes de Commit

### Formato Convencional

```
<tipo>(<alcance>): <descripción breve>

<cuerpo opcional>

<footer opcional>
```

### Tipos comunes:
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Cambios de formato (no afectan el código)
- `refactor`: Refactorización de código
- `test`: Agregar o modificar tests
- `chore`: Tareas de mantenimiento

### Ejemplos de buenos mensajes:

```
feat(reportes): Agregar funcionalidad de reporte de semáforos
fix(maps): Corregir detección de ubicación GPS
docs(README): Actualizar instrucciones de instalación
refactor(api): Mejorar estructura de endpoints REST
```

## Aplicación en Syntra

Para este proyecto de reportes de semáforos, algunos ejemplos específicos:

```bash
feat(ui): Implementar interfaz de reporte de semáforos
fix(location): Corregir precisión de ubicación GPS
docs(architecture): Documentar arquitectura del sistema
test(reports): Agregar pruebas unitarias para módulo de reportes
```

## Referencias

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Documentation - Rewriting History](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History)
- [How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/)
