CREATE TABLE tb_carro (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          modelo VARCHAR(50) NOT NULL,
                          marca VARCHAR(100) NOT NULL,
                          cor VARCHAR(10) NOT NULL UNIQUE,
                          ano INT(4) NOT NULL
);

