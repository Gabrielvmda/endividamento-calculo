💻Sistema de Cálculo de Endividamento e Plano de Quitação

Este projeto foi desenvolvido em Java + Spring Boot, utilizando MySQL, Docker e um front-end simples em HTML + JavaScript, com o objetivo de calcular o nível de endividamento de um usuário e gerar planos de quitação simulados (FLEX 6, 12, 24 e 36 meses).

O sistema permite cadastrar usuários, incomes (rendas), expenses (despesas) e debts (dívidas), além de fazer o cálculo financeiro completo de cada caso.

🎯 Objetivo do Projeto

O objetivo é criar uma aplicação capaz de:

-Consolidar todas as rendas e despesas de um usuário

-Registrar todas as suas dívidas (valor total, juros anual e parcela mínima)

-Calcular seu índice de endividamento

-Classificar o usuário entre:

◽BAIXO

◽MÉDIO

◽ALTO

-Calcular automaticamente planos de quitação com juros médios ponderados:

◽FLEX 6 meses

◽FLEX 12 meses

◽FLEX 24 meses

◽FLEX 36 meses

O resultado é exibido no front-end como JSON.

----------------------------------------------------------------------------------

🏗️ Arquitetura do Sistema

A aplicação é dividida em três partes:

🔹 1. Back-End (Java + Spring Boot)

Contém:

◽Controllers (endpoints REST)

◽Repositories (JPA)

◽Services

◽Entidades (User, Income, Expense, Debt)

◽Camada de cálculo financeiro (SummaryService)

- Principais funcionalidades do backend:
  
Função	Descrição
| Função           | Descrição                                               |
| ---------------- | ------------------------------------------------------- |
| CRUD de usuários | Criar, editar, listar, excluir                          |
| CRUD de incomes  | Rendas mensais                                          |
| CRUD de expenses | Despesas mensais                                        |
| CRUD de debts    | Dívidas com juros                                       |
| Summary          | Calcula o índice de endividamento e o plano de quitação |

🔹 2. Banco de Dados (MySQL via Docker)

Você roda um MySQL local usando:

docker run --name mysql-endividamento -e MYSQL_ROOT_PASSWORD=1234 -p 3306:3306 -d mysql:latest


As tabelas são criadas automaticamente pelo Spring (via Hibernate).

🔹 3. Front-End (HTML + JavaScript)

Simples, com foco apenas em:

◽Criar usuários

◽Editar dados

◽Adicionar renda/despesa/dívida

◽Consultar o Summary

◽Exibir resultados como JSON

-Totalmente conectado aos endpoints via fetch().

📊 Lógica Financeira Explicada (Didático)

A lógica está em SummaryService.

◽Soma total da renda mensal

Soma de todos os incomes.

◽Soma total das despesas mensais

Soma de todos os expenses.

◽Soma total das dívidas

Somatório dos valores principais de todas as dívidas.

◽ Soma das parcelas mínimas

Usado para calcular o comprometimento mensal.

🧮 Índice de Endividamento (Debt Ratio)

A fórmula usada:

debtRatio = somaParcelasMinimas / rendaMensal


Classificação:

| Índice         | Classificação |
| -------------- | ------------- |
| menor que 0.30 | BAIXO         |
| até 0.50       | MÉDIO         |
| acima de 0.50  | ALTO          |

🧠 Cálculo dos Planos de Quitação

O sistema calcula:

◽Plano FLEX 6 meses

◽Plano FLEX 12 meses

◽Plano FLEX 24 meses

◽Plano FLEX 36 meses

Usando juros anual MÉDIO PONDERADO:
taxa_media = soma(jurosAnual * principal) / soma(principal)

Fórmula da Parcela (Amortização Price)

Usada para calcular a prestação mensal:

A = P * r / (1 - (1 + r)^(-n))

Onde:

| Variável | Significado                     |
| -------- | ------------------------------- |
| A        | Prestação mensal                |
| P        | Valor total da dívida           |
| r        | Juros mensal (juros anual / 12) |
| n        | Número de parcelas              |

---------------------------------------------------------------------------
▶️ Como Rodar o Projeto
◽ Subir MySQL com Docker
docker run --name mysql-endividamento \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -p 3306:3306 -d mysql:latest

◽Rodar o Spring Boot

No IntelliJ ou via terminal:

mvn spring-boot:run

O backend sobe em:

http://localhost:8080

◽ Abrir o Front-End

Abra o arquivo:

src/main/resources/static/users.html

ou acesse:

http://localhost:8080/users.html

Funcionalidades do Front-End
✔ Criar usuário
✔ Editar usuário + atualizar rendas/despesas/dívidas
✔ Adicionar novos registros
✔ Excluir usuário (com deleção em cascata)
✔ Gerar resumo financeiro (Summary)
✔ Exibir JSON completo do plano de quitação

Autor

Gabriel Almeida.
Samuel Felix
