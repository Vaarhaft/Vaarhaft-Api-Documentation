import requests
import base64
import os


def encode_image_to_base64(image_path):
    """
    Encode the image at the given path to base64.
    """
    with open(image_path, "rb") as image_file:
        encoded_image = base64.b64encode(image_file.read()).decode("utf-8")
    return encoded_image

def send_image_to_api(api_url, api_key, image_path, customerId):
    """
    Send the encoded image to the API and return the response.
    """
    try:
        encoded_image = encode_image_to_base64(image_path)
    except Exception as e:
        return f"Error encoding image: {e}"

    headers = {"Content-Type": "application/json", "x-api-key": api_key}

    payload = {
        "image": encoded_image,
        "customerId": customerId,
    }

    try:
        response = requests.post(api_url, json=payload, headers=headers)
        if response.status_code == 200:
            return response.text
        else:
            return f"Error: {response.status_code}"
    except Exception as e:
        return f"Request failed: {e}"

def process_images_in_folder(api_url, api_key, folder_path, output_file, customerId):
    """
    Process all images in the specified folder and write results to a text file.
    """
    if not os.path.exists(folder_path):
        return "Folder path does not exist."

    with open(output_file, "w") as result_file:
        for filename in os.listdir(folder_path):
            if filename.lower().endswith((".png", ".jpg", ".jpeg")):
                image_path = os.path.join(folder_path, filename)
                response_text = send_image_to_api(api_url, api_key, image_path, customerId)
                result_file.write(f"File: {filename}\nResponse: {response_text}\n\n")
                
                
# Usage example
api_url = "<Your API URL Here>"
api_key = "<Your API Key Here>"
output_file = "api_results.txt"
folder_path = "<Your Folder Path Here>"
customerId = "<Your Customer ID>"


process_images_in_folder(api_url, api_key, folder_path, output_file, customerId)
