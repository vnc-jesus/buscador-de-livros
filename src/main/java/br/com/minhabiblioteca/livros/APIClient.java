package br.com.minhabiblioteca.livros;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class APIClient {

    private static final String API_URL = "https://gutendex.com/books";

    public static void buscarLivrosPorTitulo(String titulo) throws Exception {
        String encodedTitle = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String queryUrl = API_URL + "?languages=pt&search=" + encodedTitle;
        String jsonResponse = getJsonResponse(queryUrl);
        processarLivros(jsonResponse);
    }

    public static void buscarLivrosPorAutor(String autor) throws Exception {
        String encodedAuthor = URLEncoder.encode(autor, StandardCharsets.UTF_8);
        String queryUrl = API_URL + "?search=" + encodedAuthor;
        String jsonResponse = getJsonResponse(queryUrl);
        processarLivros(jsonResponse);
    }


    private static String getJsonResponse(String queryUrl) throws Exception {
        //System.out.println("URL chamada: " + queryUrl); // Log da URL

        URL url = new URL(queryUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        //System.out.println("Código de resposta: " + responseCode);  Log do código de resposta

        if (responseCode != 200) { // Valida qualquer erro diferente de sucesso (200)
            throw new IOException("Erro na requisição. Código: " + responseCode);
        }

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private static void processarLivros(String jsonResponse) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
           //System.out.println("Resposta JSON: " + jsonResponse); // Log da resposta JSON

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            List<Map<String, Object>> livros = (List<Map<String, Object>>) responseMap.get("results");


            if (livros == null || livros.isEmpty()) {
                System.out.println("Nenhum livro encontrado.  Deseja tentar novamente? (s/n)");
                String resposta = scanner.nextLine().trim().toLowerCase();

                if (resposta.equals("n")) {
                    System.out.println("Encerrando a busca.");
                    return;
                }

                System.out.println("Digite o título ou autor do livro:");
                String novaEntrada = scanner.nextLine().trim();


                String novoQueryUrl = API_URL + "?search=" + URLEncoder.encode(novaEntrada, StandardCharsets.UTF_8);
                jsonResponse = getJsonResponse(novoQueryUrl);
                continue;
            }

            System.out.println("Livros encontrados: " + livros.size());

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catalogo_livros.db");
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT OR IGNORE INTO livros (id, titulo, autor, lingua) VALUES (?, ?, ?, ?)")) {

                for (Map<String, Object> livro : livros) {
                    int id = (int) livro.get("id");
                    String titulo = (String) livro.get("title");
                    String autor = livro.get("authors").toString();
                    String lingua = livro.get("languages").toString();

                    pstmt.setInt(1, id);
                    pstmt.setString(2, titulo);
                    pstmt.setString(3, autor);
                    pstmt.setString(4, lingua);
                    pstmt.executeUpdate();

                    System.out.printf("ID: %d | Título: %s | Autor: %s | Idioma: %s\n", id, titulo, autor, lingua);
                }
            }
            return;
        }
    }

}

