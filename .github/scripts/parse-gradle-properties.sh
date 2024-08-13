#!/bin/bash

version=$1

while IFS='=' read -r key value; do
    # Trim leading/trailing whitespace from key and value
    key=$(echo "$key" | awk '{$1=$1;print}')
    value=$(echo "$value" | awk '{$1=$1;print}')

    # Convert key to uppercase and replace non-alphanumeric characters with underscores
    key=$(echo "$key" | tr '[:lower:]' '[:upper:]' | tr -c '[:alnum:]' '_')

    # Output the key-value pair to the GitHub output
    echo "${key}=${value}" >> "$GITHUB_OUTPUT"
done < gradle.properties

while IFS='=' read -r key value; do
    # Trim leading/trailing whitespace from key and value
    key=$(echo "$key" | awk '{$1=$1;print}')
    value=$(echo "$value" | awk '{$1=$1;print}')

    # Convert key to uppercase and replace non-alphanumeric characters with underscores
    key=$(echo "$key" | tr '[:lower:]' '[:upper:]' | tr -c '[:alnum:]' '_')

    # Output the key-value pair to the GitHub output
    echo "${key}=${value}" >> "$GITHUB_OUTPUT"
done < versions/"${version}"/gradle.properties