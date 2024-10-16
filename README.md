# tinyurl-app
Servicio REST para acortar las direcciones web.

## <U> C4 Nivel 1: Diagrama de Contexto del Sistema</U>

Este diagrama proporciona una visión general de alto nivel de la solución, mostrando las relaciones entre actores externos y los componentes del sistema.

![Nivel1 c4](./doc/tinyurl-c4-l1.svg)

## Componentes:

### Usuarios:

**Descripción:** Usuarios finales que interactúan con el servicio TinyURL a través de servicios REST.

**Interacción:** Envía solicitudes para acortar o resolver URLs.

## Balanceador de Carga (HAProxy):

**Descripción:** Dirige el tráfico al servicio de backend apropiado.

**Interacción:** Distribuye solicitudes entre los servicios basados en Spring o redirige IPs sospechosas al honeypot.

## Servicio TinyURL basado en Spring:

**Descripción:** Servicio principal responsable de acortar y recuperar URLs.

**Interacción:** Recibe tráfico del balanceador de carga y se comunica con Redis (caché), Cassandra (almacenamiento persistente) y ELK (registros).

## Redis:

**Descripción:** Capa de caché para almacenar URLs acortadas con un TTL de 1 hora.

**Interacción:** Proporciona recuperación rápida de URLs desde la caché.

## Cassandra:

**Descripción:** Base de datos para el almacenamiento persistente de URLs.

**Interacción:** Almacena registros de URLs para uso a largo plazo.

## ELK Stack (Elasticsearch, Logstash, Kibana):

**Descripción:** Registra, analiza y monitorea todas las actividades de la aplicación.

**Interacción:** Recoge registros del servicio TinyURL para su visualización en Kibana.

## Circuit Breaker:

**Descripción:** Asegura que la aplicación siga siendo resiliente bajo carga controlando fallos.

**Interacción:** Monitorea la salud de la aplicación y previene fallos en cascada.

## Honeypot:

**Descripción:** Atrae y registra tráfico malicioso.

**Interacción:** Recibe tráfico redirigido por HAProxy desde direcciones IP sospechosas.

## SonarQube:

**Descripción:** Plataforma de análisis de código para monitorear la calidad del código, la cobertura de pruebas y la mantenibilidad.

**Interacción:** Evalúa el código fuente del servicio TinyURL para métricas de calidad.

## <U> Estructura del proyecto </U>

Se plantea construir el servicio usando los principios de arquitecturas limpias.

```
tiny-url-app/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tinyurl/
│   │   │           ├── controller/         # Capa que maneja las solicitudes HTTP y las respuestas.
│   │   │           ├── domain/             # Capa de dominio
│   │   │           │   ├── config/         # Configuraciones para la inyecci{on de dependencias 
│   │   │           │   ├── model/          # Modelos de datos de la aplicación
│   │   │           │   ├── repository/     # Abstracciones de persistencia
│   │   │           │   └── usecase/        # Lógica de negocio específica de la aplicación
│   │   │           ├── infrastructure/     # Capa de infraestructura (implementaciones técnicas)
│   │   │           │   └── persistence/    # Implementaciones de persistencia
│   │   │           │   └── config/         # Configuraciones (Zookeeper, Cassandra, Redis, ELK)
│   │   │           └── utils/              # Clases de utilidad y funciones auxiliares
│   │   └── resources/                      # Configuración de la app
│   ├── test/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tinyurl/
│   │   │           ├── domain/             # Pruebas de la lógica de dominio
│   │   │           └── controller/         # Pruebas de las peticiones y respuestas de la aplicación
├── docker-compose.yml                      # Docker Compose para desplegar la propuesta
├── Dockerfile                              # Dockerfile para la app
└── pom.xml                                 # Maven
```

## <U> Despliegue </U>

Para desplegar el proyecto se agrego un archivo Makefile, que sirve para agilizar las tareas para la configuracion y despliegue del proyecto, vale la pena destacar que por defecto se despliegan 3 replicas de la **tinyurl-app**, sin embargo si desea cambiar el número de estas debe configurar la variable **TINYURL_REPLICAS** en su entorno local.

```
export TINYURL_REPLICAS=7
```

se deja una lista de los comandos incluidos en el makefile.

```
make build
make up
make run
make test
make clean-install
make ha-setting
make down
```

## <U> Documentación </U>

Para consultar las API disponibles puede dirigirce al swaguer del proyecto.

http://localhost:8080/swagger-ui/index.html

