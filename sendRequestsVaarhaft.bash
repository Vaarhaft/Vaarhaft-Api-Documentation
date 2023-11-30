#!/bin/bash

encode_image_to_base64() {
    # Encode the image at the given path to base64.
    encoded_image=$(base64 -w 0 "$1")
    echo "$encoded_image"
}

send_image_to_api() {
    # Send the encoded image to the API and return the response.
    encoded_image=$(encode_image_to_base64 "$2")

    headers=(
        "Content-Type: application/json"
        "x-api-key: $3"
    )

    payload="{\"image\": \"$encoded_image\", \"customerId\": \"$4\"}"

    response=$(curl -s -X POST -H "${headers[@]}" -d "$payload" "$1")

    if [ $? -eq 0 ]; then
        echo "$response"
    else
        echo "Error: Request failed"
    fi
}

process_images_in_folder() {
    # Process all images in the specified folder and write results to a text file.
    if [ ! -d "$3" ]; then
        echo "Folder path does not exist."
        exit 1
    fi

    for file in "$3"/*.{png,jpg,jpeg}; do
        if [ -f "$file" ]; then
            filename=$(basename "$file")
            response_text=$(send_image_to_api "$1" "$2" "$file" "$4")
            echo -e "File: $filename\nResponse: $response_text\n" >> "$5"
        fi
    done
}

# Usage example
api_url="<Your API URL Here>"
api_key="<Your API Key Here>"
output_file="api_results.txt"
folder_path="<Your Folder Path Here>"
customerId="<Your Customer ID>"

process_images_in_folder "$api_url" "$api_key" "$folder_path" "$customerId" "$output_file"
