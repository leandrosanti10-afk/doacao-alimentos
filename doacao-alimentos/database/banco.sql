CREATE DATABASE IF NOT EXISTS doacao_alimentos
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE doacao_alimentos;

CREATE TABLE IF NOT EXISTS doadores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf_cnpj VARCHAR(18) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    cidade VARCHAR(100) NOT NULL,
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS instituicoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    cidade VARCHAR(100) NOT NULL,
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doador_id BIGINT NOT NULL,
    instituicao_id BIGINT NOT NULL,
    descricao TEXT NOT NULL,
    data_doacao DATE NOT NULL,

    CONSTRAINT fk_doacoes_doador
        FOREIGN KEY (doador_id)
        REFERENCES doadores(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_doacoes_instituicao
        FOREIGN KEY (instituicao_id)
        REFERENCES instituicoes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_doacoes_data
    ON doacoes (data_doacao);

CREATE INDEX idx_doacoes_doador
    ON doacoes (doador_id);

CREATE INDEX idx_doacoes_instituicao
    ON doacoes (instituicao_id);
