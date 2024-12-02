import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONObject;

import java.util.Scanner;

public class ConversorDeMoedasAPI {

    private static final String API_KEY = "SUA_CHAVE_API"; // Substitua com sua chave da API
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Conversor de Moedas - API de Taxas de Câmbio");
        System.out.println("Escolha a moeda base (USD, EUR, BRL):");
        String moedaBase = scanner.nextLine().toUpperCase();

        System.out.println("Escolha a moeda de destino (USD, EUR, BRL):");
        String moedaDestino = scanner.nextLine().toUpperCase();

        System.out.println("Digite o valor a ser convertido:");
        double valor = scanner.nextDouble();

        // Verificar se a moeda base é a mesma que a moeda de destino
        if (moedaBase.equals(moedaDestino)) {
            System.out.println("O valor em " + moedaBase + " é: " + valor);
        } else {
            try {
                double taxaDeCambio = obterTaxaDeCambio(moedaBase, moedaDestino);
                double valorConvertido = valor * taxaDeCambio;
                System.out.println(valor + " " + moedaBase + " é equivalente a " 
                        + String.format("%.2f", valorConvertido) + " " + moedaDestino);
            } catch (IOException e) {
                System.out.println("Erro ao obter taxas de câmbio. Tente novamente mais tarde.");
            }
        }

        scanner.close();
    }

    // Função para obter a taxa de câmbio entre duas moedas usando a API
    public static double obterTaxaDeCambio(String moedaBase, String moedaDestino) throws IOException {
        String urlString = API_URL + moedaBase;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();

        if (status != 200) {
            throw new IOException("Erro na resposta da API: " + status);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse o JSON da resposta
        JSONObject jsonResponse = new JSONObject(response.toString());
        if (!jsonResponse.getString("result").equals("success")) {
            throw new IOException("Erro ao obter dados da API.");
        }

        // Obter a taxa de câmbio entre as moedas
        JSONObject conversionRates = jsonResponse.getJSONObject("conversion_rates");
        if (!conversionRates.has(moedaDestino)) {
            throw new IOException("Moeda de destino não encontrada.");
        }

        return conversionRates.getDouble(moedaDestino);
    }
}
