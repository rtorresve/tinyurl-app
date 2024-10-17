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

PROMETHEUS_TEMPLATE=$(cat <<-END
global:
  scrape_interval: 10s

scrape_configs:
  - job_name: 'tinyurl-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
END
)

TARGETS_ENTRIES=""
for i in $(seq 1 $NUM_REPLICAS); do
    TARGETS_ENTRIES+="      - targets: ['tinyurl-app${SEPARATOR}tinyurl-app${SEPARATOR}${i}:8080']\n"
done

PROMETHEUS_CONFIG="${PROMETHEUS_TEMPLATE}\n${TARGETS_ENTRIES}"

echo -e "$PROMETHEUS_CONFIG" > ./docker/prometheus/config/prometheus.yml

echo "prometheus.yml generated successfully."

