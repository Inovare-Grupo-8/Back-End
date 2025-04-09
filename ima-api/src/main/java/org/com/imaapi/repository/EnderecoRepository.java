package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.com.imaapi.model.usuario.output.EnderecoOutput;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

}