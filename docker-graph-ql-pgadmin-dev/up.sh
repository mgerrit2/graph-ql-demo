#!/bin/bash

# Bepaal de absolute locatie van dit script
SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)

# Navigeer naar de docker map relatief aan dit script
# Dit voorkomt dat Docker in de verkeerde map mappen gaat aanmaken
cd "$SCRIPT_DIR" || { echo "Fout: Map 'docker' niet gevonden"; exit 1; }

echo "Huidige werkmap: $(pwd)"

echo "--- 1. Database en volumes volledig verwijderen ---"
docker compose -f docker-compose-postgresql.yml up -d