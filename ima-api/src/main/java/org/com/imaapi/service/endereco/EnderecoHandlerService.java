package org.com.imaapi.service.endereco;

import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.com.imaapi.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EnderecoHandlerService {
    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public Endereco buscarSalvarEndereco(String cep, String numero, String complemento) {
        ResponseEntity<EnderecoOutput> enderecoResponse = enderecoService.buscaEndereco(cep, numero, complemento);
        EnderecoOutput enderecoOutput = enderecoResponse.getBody();

        if (enderecoOutput == null) {
            throw new RuntimeException("😢 Endereço não encontrado para o CEP: " + cep);
        }

        Endereco endereco = new Endereco();
        endereco.setCep(enderecoOutput.getCep());
        endereco.setLogradouro(enderecoOutput.getLogradouro());
        endereco.setBairro(enderecoOutput.getBairro());
        endereco.setNumero(enderecoOutput.getNumero());
        endereco.setComplemento(enderecoOutput.getComplemento());
        endereco.setUf(enderecoOutput.getUf());
        endereco.setLocalidade(enderecoOutput.getLocalidade());
        return enderecoRepository.save(endereco);
    }
}
