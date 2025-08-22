-- Criação da tabela de roles (caso não exista)
CREATE TABLE IF NOT EXISTS tb_roles (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(255) NOT NULL
    );

-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS tb_usuarios (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
    );

-- Criação da tabela de relacionamento entre usuários e roles
CREATE TABLE IF NOT EXISTS tb_usuarios_roles (
                                                 user_id BIGINT NOT NULL,
                                                 role_id BIGINT NOT NULL,
                                                 PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_roles(id) ON DELETE CASCADE
    );

-- Inserção de roles
INSERT INTO tb_roles (nome) VALUES
                                ('ROLE_ADMIN'),
                                ('ROLE_BASIC'),
                                ('ROLE_GERENTE');

-- Inserção de usuários
INSERT INTO tb_usuarios (login, senha) VALUES
                                           ('admin', '$2a$10$1gsoSHRdVMa7UwJImK2a5OkKADIzzG9zVaA.zcXpVWPhBZ5MIY8Mi'),  -- senha: senha123
                                           ('usuario2', '$2a$10$wgeAMfb8E1olrHj5Ko5P7T7FyvhYrgHQt18sJll8eLg1BYJc0AXve'),  -- senha: senha456
                                           ('usuario3', '$2a$10$Vt6ldlS92W5N6HF1OS5qfIWdb0P7Zfjdqxq6rzQ3S1CnllXaZRaBu'),  -- senha: senha789
                                           ('usuario4', '$2a$10$gu9vENxyFVVic/ZJmv1iXtnH/q64jLg5c9PjBdVRwYHhM19wXy6jS'),  -- senha: senha101
                                           ('usuario5', '$2a$10$ADqjEwM1joxBvl0ivQiqK3odF2gGbzRslfvtnwTqfmRbx11P0RHgi'),  -- senha: senha202
                                           ('usuario6', '$2a$10$KKQzCN0v5qfAtKzqBaECx6HsIMHzl2i8UGyUmVoGL9NjZldY8xBda'),  -- senha: senha303
                                           ('usuario7', '$2a$10$5nMjTjSKM1jOayfnePQ3JZgE0.V7MeYtHpUw33HBoDdbwrB9ZHyRi'),  -- senha: senha404
                                           ('usuario8', '$2a$10$9oj9J3XaZnT.ygyQllk1D3b9BkdlfyB0lR21ZC75zayGBfLRrdFgW'),  -- senha: senha505
                                           ('usuario9', '$2a$10$Q0Uwz7fHpDJ.mkjXo0yHjNDqb3AdQLZiywCmzzXjWQLBYWVVF.d1u'),  -- senha: senha606
                                           ('usuario10', '$2a$10$XHEAh47g/J6n90v8gPr7ZyItS.VF6yynlgHIVBOA.Gw3z8eV8fmYq');  -- senha: senha707

-- Inserção na tabela de relacionamento tb_usuarios_roles
-- Associações de usuários com roles
INSERT INTO tb_usuarios_roles (user_id, role_id) VALUES
                                                     (1, 1),  -- usuario1 com ROLE_USER
                                                     (2, 2),  -- usuario2 com ROLE_ADMIN
                                                     (3, 1),  -- usuario3 com ROLE_USER
                                                     (4, 3),  -- usuario4 com ROLE_MANAGER
                                                     (5, 1),  -- usuario5 com ROLE_USER
                                                     (6, 2),  -- usuario6 com ROLE_ADMIN
                                                     (7, 3),  -- usuario7 com ROLE_MANAGER
                                                     (8, 1),  -- usuario8 com ROLE_USER
                                                     (9, 2),  -- usuario9 com ROLE_ADMIN
                                                     (10, 1); -- usuario10 com ROLE_USER