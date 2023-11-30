import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageProcessor {

    private static OkHttpClient client = new OkHttpClient();

    public static String encodeImageToBase64(String imagePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(new File(imagePath).toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static String sendImageToApi(String apiUrl, String apiKey, String imagePath, String customerId) throws IOException {
        String encodedImage = encodeImageToBase64(imagePath);

        MediaType mediaType = MediaType.parse("application/json");
        String json = String.format("{\"image\": \"%s\", \"customerId\": \"%s\"}", encodedImage, customerId);

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void processImagesInFolder(String apiUrl, String apiKey, String folderPath, String customerId, String outputFilePath) throws IOException {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder path does not exist.");
            System.exit(1);
        }

        File outputFile = new File(outputFilePath);

        for (File file : folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"))) {
            if (file.isFile()) {
                String filename = file.getName();
                String responseText = sendImageToApi(apiUrl, apiKey, file.getAbsolutePath(), customerId);

                String result = String.format("File: %s\nResponse: %s\n\n", filename, responseText);
                Files.write(outputFile.toPath(), result.getBytes(), java.nio.file.StandardOpenOption.APPEND);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String apiUrl = "<Your API URL Here>";
        String apiKey = "<Your API Key Here>";
        String outputFilePath = "api_results.txt";
        String folderPath = "<Your Folder Path Here>";
        String customerId = "<Your Customer ID>";

        processImagesInFolder(apiUrl, apiKey, folderPath, customerId, outputFilePath);
    }
}
