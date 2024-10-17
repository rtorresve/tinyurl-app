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

## MariaDB:

**Descripción:** Base de datos para el almacenamiento persistente de URLs.

**Interacción:** Almacena registros de URLs para uso a largo plazo.

## Prometheus:

**Descripción:** Registra el estado y trafico de los servicios de tinyurl-app.

**Interacción:** Recoge registros del servicio TinyURL para su visualización en Grafana.

## Grafana:

**Descripción:** Herramienta para visualizar el estado de los servicios.

**Interacción:** Recoge los datos de Prometheus para construir dashboard y alertas.

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

```sh
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

```sh
export TINYURL_REPLICAS=7
```

se deja una lista de los comandos incluidos en el makefile.

```sh
make build
make up
make run
make test
make clean-install
make ha-setting
make remove
make down
make logs
make createsuperuser
make restart
```

1. Clonamos el repositorio.
 ```sh
 git clone https://github.com/rtorresve/tinyurl-app.git
 cd tinyurl-app
 ```

2. Asignamos las variables de entorno con las replicas que consideremos necesarias.
```sh
export TINYURL_REPLICAS=3
```

3. Creamos los archivos de configuracion para prometheus y HaProxy
```sh
make ha-setting
make prometheus-setting 
```

4. Procedemos a construir las imagenes:
```sh
make build 
```

5. Procedemos a levantar el proyecto:
```sh
make up
```

6. Para activar la aplicacion de administracion de los registros, necesitamos crear el superusuario, al mismo tiempo inicializara los modelos de base de datos necesarios para desplegar esta, luego reiniciamos las instancias para corregir el redireccionamiento de algunos servicios como el HAPROXY
```sh
make createsuperuser
make restart
```

7. Una vez desplegado el proyecto podemos acceder a los dashboard habilitados, para fines del ejercicio el dominio utilizado es `infotor.online` pero puede reemplazarlo por el del host donde se despliegue la aplicación o por **localhost** 

* Para acceder al dashboard del balanceador http://www.infotor.online:8404
* La consola de administración del modelo de datos esta disponible en https://infotor.online/admin/
* El cliente de swagger a través de http://infotor.online/swagger-ui/index.html
* Las consultas del Prometheus a través de http://infotor.online:9090/
* Los dashboard del grafana pueden consultarse en http://infotor.online:3000 (ver las credenciales que se definieron en el archivo docker-compose.yml)

8. Para ejecutar pruebas de estres se uso Apache Benchmark, puede user la imagen incluida en el docker-compose.yml **ab-test** o descargarlo en su host, para pc con sistemas gnu/linux puede ejecutar
```sh
sudo apt-get install apache2-utils
```
El script para ejecutarlas se encuentra en la carpeta `bulk_test`

## <U> Documentación </U>

Para consultar las API disponibles puede dirigirce al [swaguer](http://localhost/swagger-ui/index.html) del proyecto.

`{ruta_servidor}/swagger-ui/index.html`


## <u> Administración </u>

Para habilitar la consola de administracion previamente debe ejecutar el comando.

`make createsuperuser`

Esto agregara las tabalas restantes de la base de datos y asignara el usuario junto a las credenciales necesarias para poder gestionar los datos del modelo.
