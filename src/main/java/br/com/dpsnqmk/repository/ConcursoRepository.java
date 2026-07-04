package br.com.dpsnqmk.repository;

import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConcursoRepository extends MongoRepository<ConcursoMongoDTO, String> {

    Page<ConcursoMongoDTO> findByLoteria(String loteria, Pageable pageable);

    List<ConcursoMongoDTO> findByLoteriaOrderByConcursoAsc(String loteria);

    // "Between" no Spring Data Mongo gera $gt/$lt (exclusivo), por isso o nome explícito
    List<ConcursoMongoDTO> findByLoteriaAndConcursoGreaterThanEqualAndConcursoLessThanEqualOrderByConcursoAsc(
            String loteria, int concursoInicial, int concursoFinal);

    Optional<ConcursoMongoDTO> findByLoteriaAndConcurso(String loteria, int concurso);

    Optional<ConcursoMongoDTO> findTopByLoteriaOrderByConcursoDesc(String loteria);

    long countByLoteria(String loteria);

    long deleteByLoteria(String loteria);
}
