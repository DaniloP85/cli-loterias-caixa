package br.com.dpsnqmk.repository;

import br.com.dpsnqmk.dto.JogoMongoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JogoRepository extends MongoRepository<JogoMongoDTO, String> {

    List<JogoMongoDTO> findAllByOrderByCriadoEmDesc();
}
