package org.com.imaapi.repository;

import org.com.imaapi.model.Usuario.Endereco;
import org.com.imaapi.model.Usuario.output.EnderecoOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

}
