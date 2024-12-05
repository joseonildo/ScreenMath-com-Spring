package br.com.joseonildo.screenmatch;

import br.com.joseonildo.screenmatch.model.DadosSerie;
import br.com.joseonildo.screenmatch.service.ConsumoAPI;
import br.com.joseonildo.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<DadosSerie> listaTitulos = new ArrayList<>();
		buscaTitulo(listaTitulos);
		System.out.println("Titulos buscados: ");
		Object item;
		for (DadosSerie item: listaTitulos) {

		}
	}

	public void buscaTitulo(List<DadosSerie> listaTitulos) {
		var leitura = new Scanner(System.in);
		var consumoAPI = new ConsumoAPI();
		do {
			System.out.print("Digite o nome do Filme: ");
			String busca = leitura.nextLine();
			if (busca.isBlank()) break;
			var json = consumoAPI.obterDados("http://www.omdbapi.com/?t=" + busca + "&apikey=fc511a2f");
			System.out.println("Json encontrado: ");
			System.out.println(json);
			var conversor = new ConverteDados();
			var dados = conversor.obterDados(json, DadosSerie.class);
			listaTitulos.add(dados);
			System.out.println("Titulo adicionado: ");
			System.out.println(dados);
		} while (true);
	}
}


