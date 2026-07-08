package br.com.dpsnqmk.repository;

import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConcursoRepository extends MongoRepository<ConcursoMongoDTO, String> {

    Page<ConcursoMongoDTO> findByLoteria(String loteria, Pageable pageable);

    List<ConcursoMongoDTO> findByLoteriaOrderByConcursoAsc(String loteria);

    // Query derivada não suporta duas condições no mesmo campo (o Criteria do
    // Mongo rejeita o segundo 'concurso'), por isso a @Query explícita
    @Query(value = "{ 'loteria': ?0, 'concurso': { $gte: ?1, $lte: ?2 } }", sort = "{ 'concurso': 1 }")
    List<ConcursoMongoDTO> findConcursosNoIntervalo(String loteria, int concursoInicial, int concursoFinal);

    Optional<ConcursoMongoDTO> findByLoteriaAndConcurso(String loteria, int concurso);

    Optional<ConcursoMongoDTO> findTopByLoteriaOrderByConcursoDesc(String loteria);

    long countByLoteria(String loteria);

    long deleteByLoteria(String loteria);
}
