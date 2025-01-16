package br.com.minhabiblioteca.livros;

import java.sql.*;

class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:catalogo_livros.db";

    public void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS livros (" +
                    "id INTEGER PRIMARY KEY," +
                    "titulo TEXT," +
                    "autor TEXT," +
                    "lingua TEXT" +
                    ");";
            stmt.execute(createTableSQL);
        }
    }

    public void listarLivrosNoBanco() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM livros");
            while (rs.next()) {
                System.out.printf("ID: %d | Título: %s | Autor: %s | Língua: %s\n",
                        rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"), rs.getString("lingua"));
            }
        }
    }

    public void exibirDetalhesDoLivro(int id) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM livros WHERE id = ?")) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.printf("ID: %d\nTítulo: %s\nAutor: %s\nLíngua: %s\n",
                        rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"), rs.getString("lingua"));
            } else {
                System.out.println("Livro não encontrado.");
            }
        }
    }
}
