#!/bin/bash

NUM_REPLICAS=${TINYURL_REPLICAS:-3}  # Default to 3 if not set
DOCKER_COMPOSE_VERSION=$(docker-compose --version | awk '{print $3}' | sed 's/,//')

version_lt() {
    [ "$1" != "$2" ] && [ "$(printf '%s\n' "$1" "$2" | sort -V | head -n1)" = "$1" ]
}

if version_lt "$DOCKER_COMPOSE_VERSION" "2.0.0"; then
    SEPARATOR="_"
else
    SEPARATOR="-"
fi

# HAProxy configuration template
HAPROXY_TEMPLATE=$(cat <<-END
global
    log stdout format raw local0

defaults
    log global
    mode http
    option httplog
    option dontlognull
    timeout connect 5000ms
    timeout client  50000ms
    timeout server  50000ms
    timeout http-keep-alive 10s  
    timeout http-request 5s
    option http-server-close
    option forwardfor

frontend stats
  bind *:8404
  stats enable
  stats uri /
  stats refresh 10s


frontend http_front
    bind *:80
    acl is_static path_beg /static/
    acl is_admin path_beg /admin/
    use_backend admin_back if is_static
    use_backend admin_back if is_admin
    default_backend http_back

frontend admin_front
    bind *:8000
    default_backend admin_back

backend admin_back
    server admin tinyurl_admin:8000 check

backend http_back
    balance leastconn
    compression algo gzip
    compression type text/html text/plain text/css application/javascript application/json
END
)

# Generate server entries
SERVER_ENTRIES=""
for i in $(seq 1 $NUM_REPLICAS); do
    SERVER_ENTRIES+="    server s${i} tinyurl-app${SEPARATOR}tinyurl-app${SEPARATOR}${i}:8080 check\n"
done

# Combine the template and server entries
HAPROXY_CONFIG="${HAPROXY_TEMPLATE}\n${SERVER_ENTRIES}"

# Write the configuration to haproxy.cfg
echo -e "$HAPROXY_CONFIG" > ./docker/haproxy/haproxy.cfg

echo "haproxy.cfg generated successfully."

