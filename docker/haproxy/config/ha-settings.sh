#!/bin/bash

# Define the number of replicas
if [ -n "$1" ]; then
  NUM_REPLICAS=$1
else
  NUM_REPLICAS=${NUM_REPLICAS:-3}  # Default to 3 if not set
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
    default_backend http_back

backend http_back
    balance leastconn
    compression algo gzip
    compression type text/html text/plain text/css application/javascript application/json
END
)

# Generate server entries
SERVER_ENTRIES=""
for i in $(seq 1 $NUM_REPLICAS); do
    SERVER_ENTRIES+="    server s${i} tinyurl-app-tinyurl-app-${i}:8080 check\n"
done

# Combine the template and server entries
HAPROXY_CONFIG="${HAPROXY_TEMPLATE}\n${SERVER_ENTRIES}"

# Write the configuration to haproxy.cfg
echo -e "$HAPROXY_CONFIG" > ./haproxy.cfg

echo "haproxy.cfg generated successfully."

