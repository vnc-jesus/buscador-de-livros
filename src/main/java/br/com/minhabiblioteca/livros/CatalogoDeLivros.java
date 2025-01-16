package br.com.minhabiblioteca.livros;

import java.util.Scanner;

public class CatalogoDeLivros {

	public static void main(String[] args) {
		DatabaseManager dbManager = new DatabaseManager();
		try {
			dbManager.setupDatabase();
			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.println("\nBem-vindo ao Catálogo de Livros! Escolha uma opção:");
				System.out.println("1. Buscar livros por título");
				System.out.println("2. Listar todos os livros no banco de dados");
				System.out.println("3. Buscar livros por autor");
				System.out.println("4. Exibir detalhes de um livro pelo ID");
				System.out.println("5. Sair");
				System.out.print("Opção: ");
				int opcao = scanner.nextInt();
				scanner.nextLine();

				switch (opcao) {
					case 1:
						System.out.print("Digite o título do livro: ");
						String titulo = scanner.nextLine();
						APIClient.buscarLivrosPorTitulo(titulo);
						break;
					case 2:
						dbManager.listarLivrosNoBanco();
						break;
					case 3:
						System.out.print("Digite o nome do autor: ");
						String autor = scanner.nextLine();
						APIClient.buscarLivrosPorAutor(autor);
						break;
					case 4:
						System.out.print("Digite o ID do livro: ");
						int id = scanner.nextInt();
						dbManager.exibirDetalhesDoLivro(id);
						break;
					case 5:
						System.out.println("Saindo...");
						return;
					default:
						System.out.println("Opção inválida. Tente novamente.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
