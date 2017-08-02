# vazante

## Primeiros passos

Primeiramente, você vai precisar do MongoDB instalado para fazer testes localmente, entao siga os passos do guia de instalçao oficial:


https://docs.mongodb.com/manual/installation/


Agora, basta clonar o repositório do Github em alguma pasta, e criar um arquivo `.env` usando o `.env.example`, ele tem como default o endereço do banco de dados local (em produçao este endereço é outro)

```sh
https://github.com/felipe-augusto/vazante.git
cd vazante
cp .env.example .env
```

Agora, basta digitar o seguinte comando para instalar as dependências e subir o servidor (isto pode demorar algum tempo e travar seu computador - agradeça a JVM)

```sh
sbt run
```

Agora você pode acessar as seguintes rotas (por enquanto):

`http://localhost:9000`
`http://localhost:9000/preferred`

## Fluxo de trabalho em equipe

Agora, quando cada pessoa for trabalhar ela deve seguir o seguinte fluxo:

1. Criar uma branch nova com a tarefa que irá realizar
2. Quando fizer modificaçoes, criar commits nesta nova branch
3. Enviar os commits para o repositório remoto
4. Abrir um Pull-Request da sua branch para a master

Na prática isto é feito da seguinte forma

```sh
# cria branch com nome minha_branch e muda para ela
git checkout -b minha_branch 
# adiciona todas as modificacoes que foram feitas em arquivos
git add -A
# cria um commit (um snapshot das ultimas atualizacoes)
git commit -m "fiz modificacoes no banco de dados"
# envia as modificacoes para o github (repositorio online)
git push origin minha_branch
```

É importante seguir estes passos porque mudanças na branch `master` tem deploy automático para o servidor de produçao.