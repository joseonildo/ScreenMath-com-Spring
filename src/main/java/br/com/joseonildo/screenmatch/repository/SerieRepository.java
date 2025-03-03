package br.com.joseonildo.screenmatch.repository;

import br.com.joseonildo.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {
}
