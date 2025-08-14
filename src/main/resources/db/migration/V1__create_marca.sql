CREATE TABLE tb_marca (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          nome VARCHAR(50) NOT NULL
);

-- Inserção de registros iniciais
INSERT INTO tb_marca (nome) VALUES
                                ('RENAULT'),
                                ('FIAT');
