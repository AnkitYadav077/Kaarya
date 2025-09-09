#!/bin/sh
host="$1"
port="$2"
shift 2

echo "Waiting for $host:$port to be available..."

while ! nc -z "$host" "$port"; do
    echo "Waiting for $host:$port..."
    sleep 2
done

echo "$host:$port is available, starting application."
# shellcheck disable=SC2145
exec "$@"