#!/bin/bash

# Initialize an empty array for the matrix in JSON format
matrix_content="["

# Extract enabled platforms from gradle.properties
enabled_platforms=$(awk -F= '/enabled_platforms/{print $2}' gradle.properties | tr -d ' ')

# Iterate over each platform and gather versions
for platform in $(echo $enabled_platforms | tr ',' ' '); do
  versions=$(awk -F= '/enabled_'$platform'_versions/{print $2}' gradle.properties | tr -d ' ')
  for version in $(echo $versions | tr ',' ' '); do
    # Create each entry
    matrix_entry="{\"mod_loader\":\"$platform\",\"version\":\"$version\",\"script\":[\"client\",\"server\"]}"
    # Append to matrix content
    matrix_content+="$matrix_entry,"
  done
done

# Remove the trailing comma and close the JSON array
matrix_content="${matrix_content%,}]"

# Output the matrix content
echo "Generated matrix: $matrix_content"

# Export the matrix as an environment variable
echo "matrix=$matrix_content" >> $GITHUB_ENV