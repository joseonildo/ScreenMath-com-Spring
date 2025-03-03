package br.com.joseonildo.screenmatch;
import br.com.joseonildo.screenmatch.principal.Principal;
import br.com.joseonildo.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {
	@Autowired
	private SerieRepository repositorio;

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws IOException, InterruptedException {
		Principal buscarTitulo = new Principal(repositorio);
		buscarTitulo.exibeMenu();




	}
}
