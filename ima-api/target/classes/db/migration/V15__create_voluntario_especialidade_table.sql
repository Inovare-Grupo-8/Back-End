-- Criar tabela de relacionamento entre voluntário e especialidade
CREATE TABLE voluntario_especialidade (
    id_voluntario_especialidade INT AUTO_INCREMENT PRIMARY KEY,
    fk_voluntario INT NOT NULL,
    fk_especialidade INT NOT NULL,
    principal BOOLEAN DEFAULT false,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    versao INT DEFAULT 0,
    INDEX idx_fk_voluntario (fk_voluntario),
    INDEX idx_fk_especialidade (fk_especialidade),
    CONSTRAINT uk_voluntario_especialidade UNIQUE (fk_voluntario, fk_especialidade),
    CONSTRAINT fk_ve_voluntario FOREIGN KEY (fk_voluntario) 
        REFERENCES voluntario(id_voluntario) ON DELETE CASCADE,
    CONSTRAINT fk_ve_especialidade FOREIGN KEY (fk_especialidade) 
        REFERENCES especialidade(id_especialidade) ON DELETE CASCADE
);
