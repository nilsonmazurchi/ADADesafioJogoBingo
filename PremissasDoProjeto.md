# Bingo Game

## TL;DL
Construir um jogo de bingo multiplayer para rodar local com opções para sorteio automático aleatório ou manual que ao final indique alguns dados estatísticos

## Game
### 01 - Etapa 1 - Apresentação e Jogadores
1. Mensagem de boas vindas e mostrar opções de comando
2. Dar um design bacana para o visual e dê um nome ao jogo
3. Considerar o modo multiplayer automático com 1 ou mais jogadores
4. Separar por hifen o nickname: player1-player2-player3

### 02 - Etapa 2 - Cartelas
1. Gerar as cartelas desejadas
2. Opções de comando para cartelas geradas RANDOM ou MANUAL
3. Para MANUAL localizar o nickname preencher a cartela 
4. O input deverá ser: `"1,2,3,4,5-6,7,8,9,1-2,3,4,5,6"`
5. Para RANDOM será gerado automaticamente aleatórias cartelas não repetidas
6. A cartela não pode ter números repetidos
7. No dia do game iremos expor todas as cartelas como neste exemplo que fizemos no pad
8. Veja que [aqui no pad](https://pad.riseup.net/p/1JDJ0JDs07YTO5qP8cGt) fizemos um ensaio manual. A idéia é que o programa gere as cartelas

### 03 - Etapa 3 - Números Sorteados
1. Opções de comando do sorteio podem ser RANDOM ou MANUAL
2. Para MANUAL os números sorteados entrarão via Scanner
3. O input deverá seguir a sintaxe: `"1,2,3,4,5"`
4. Para RANDOM serão números aleatórios não repetidos na cartela
5. A cada round deverá imprimir:
6. Um ranking dos top 3 mais bem pontuados no game
7. Um pedido de pressionar a tecla para continuar via Scanner
8. Se pressionar X aborta o game

### 04 - Etapa 4 - Fim do Jogo
1. Cada jogador terá um array para indicar os números acertados
2. Esse array indica a posição de cada número na cartela
3. Aqui temos os dois ultimos números acertados, ex: `0,0,0,1,1`
4. O bingo será eleito quando o jogador tiver todos com número 1
5. Ao final do game exibir o ranking geral e estatísticas do game:
    - Quantidade de rounds
    - Cartela premiada com números ordenados e nome do vencedor
    - Quantidade e números sorteados em ordem
    - Ranking geral ordenado pelo número de acertos

### 05 - Etapa 5 - Regras Gerais e Sugestões para implementar
1. Não usar classes e derivadas de Collections, use array/matriz
2. No modo manual iremos anunciar as cartelas no pad.riseup
3. Daí cada um marca a sua cartela manualmente para acompanhar
4. Pode usar classes utilitárias do java.util como Random e Arrays
5. Estruture seu código em métodos por responsabilidade

## Insight
- Imagem ilustrativa aleatória de pessoas jogando bingo

![This is an image](https://lottokeeper.com/wp-content/uploads/2019/04/People-performed-the-sport-of-Bingo.jpg)