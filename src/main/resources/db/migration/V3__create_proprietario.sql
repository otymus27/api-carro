CREATE TABLE tb_proprietario (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          nome VARCHAR(50) NOT NULL
);

-- Inserção de registros iniciais
INSERT INTO tb_proprietario (nome) VALUES
                                ('FABIO'),
                                ('DJANE');
