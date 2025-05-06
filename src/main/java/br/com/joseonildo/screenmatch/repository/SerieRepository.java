package br.com.joseonildo.screenmatch.repository;

import br.com.joseonildo.screenmatch.model.Categoria;
import br.com.joseonildo.screenmatch.model.Episodio;
import br.com.joseonildo.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
   Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

   List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

   @Query("SELECT s FROM Serie s WHERE s.atores ILIKE %:nomeAtor% AND s.avaliacao >= :avaliacao")
   List<Serie> seriePorAtorEAvaliacao(String nomeAtor, Double avaliacao);

   List<Serie> findTop5ByOrderByAvaliacaoDesc();

   List<Serie> findByGenero(Categoria categoria);

   List<Serie> findByTotalTemporadasAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);

   List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);

   @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
   List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

   @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
   List<Episodio> episodioPorTrecho(String trechoEpisodio);

   @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
   List<Episodio> top5episodiosPorSerie(Serie serie);

   @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
   List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);

   @Query("SELECT s FROM Serie s WHERE YEAR(s.dataLancamento) >= :anoLancamento ORDER BY s.dataLancamento ASC")
   List<Serie> seriePorAno(int anoLancamento);

}
