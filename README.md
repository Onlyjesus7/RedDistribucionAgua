# Simulador de Red de Distribución Fluvial

## Descripción

Simulador de redes de distribución de agua basado en **teoría de grafos**. Permite diseñar, visualizar y analizar redes de tuberías que abastecen de agua a diferentes barrios desde uno o varios embalses. Implementa el algoritmo de **Ford-Fulkerson** con BFS (Edmonds-Karp) para el cálculo de flujo máximo.

---

##  Características Principales

| Característica | Descripción |
|----------------|-------------|
| **Diseño interactivo** | Arrastra nodos desde la paleta al grafo |
| **Conexiones dinámicas** | Crea tuberías con click derecho |
| **Cálculo de flujo** | Algoritmo Ford-Fulkerson con BFS |
| **Visualización** | Colores según estado y uso de tuberías |
| **Persistencia** | Guarda y carga redes en formato JSON |
| **Escenarios** | Simula fallas, aumentos de demanda y más |
| **Estadísticas** | Reportes detallados de la red |

---

## 🏗️ Arquitectura del Proyecto
RedDistribucionAgua/
├── src/
│ └── com/
│ └── fluvial/
│ ├── modelo/ # Capa de datos (Nodos, Aristas, Grafo)
│ ├── algoritmo/ # Algoritmos de flujo máximo
│ ├── controlador/ # Controlador MVC
│ ├── vista/ # Interfaz de usuario (Swing)
│ └── util/ # Utilidades (Persistencia)
├── lib/ # Librerías externas (Gson)
├── redes/ # Ejemplos de redes guardadas
└── README.md # Este archivo


---

##  Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 11+ | Lenguaje de programación |
| **Swing** | - | Interfaz gráfica |
| **Gson** | 2.10.1 | Persistencia en JSON |
| **Git** | - | Control de versiones |

---

---

## Cómo Ejecutar el Proyecto

### Desde NetBeans:
1. Abre el proyecto en **NetBeans**
2. Haz clic derecho en el proyecto → **Run**
3. O ejecuta la clase `VistaPrincipal`

### Desde línea de comandos:
```bash
# Navegar a la carpeta del proyecto
cd RedDistribucionAgua

Guía de Uso
1. Agregar Nodos
Arrastra un nodo desde la paleta al grafo

O haz clic en el nodo de la paleta para agregarlo automáticamente

2. Crear Tuberías
Click derecho en un nodo origen

Click en el nodo destino para completar la conexión

3. Configurar Propiedades
Selecciona un nodo haciendo clic en él

Modifica los valores en el panel de propiedades

Haz clic en "Aplicar Cambios"

4. Calcular Flujo
Haz clic en "Calcular Flujo"

El sistema mostrará el flujo máximo de la red

5. Guardar/Cargar Red
Guardar Red: Guarda la red en un archivo .json

Cargar Red: Carga una red desde un archivo .json

6. Simular Escenarios
Haz clic en "Escenarios"

Selecciona un escenario (Falla de Tubería, Aumento de Demanda, etc.)

Ejecuta la simulación

 Dependencias
Librería	Descarga	Versión
Gson	Descargar	2.10.1


# Compilar
javac -d build/classes src/com/fluvial/**/*.java

# Ejecutar
java -cp build/classes com.fluvial.vista.VistaPrincipal

Referencias
Algoritmo de Ford-Fulkerson: [Introduction to Algorithms - CLRS]

Teoría de Grafos: [Cormen, Leiserson, Rivest, Stein]

Patrones de Diseño: [Gamma, Helm, Johnson, Vlissides]

 Licencia
MIT License

Contacto
Para dudas o sugerencias, contacta al equipo de desarrollo.

¡Gracias por usar el Simulador de Red de Distribución Fluvial!
