import java.util.*;

public class bingo1 {

    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Jogo de Bingo Multiplayer!");

        String[] nomesJogadores = obterNomesJogadores();
        int[][] cartelasJogadores = inicializarCartelasJogadores(nomesJogadores);
        int[][] cartelasIniciaisJogadores = copiarCartelasInicial(cartelasJogadores);

        exibirJogadoresECartelas(nomesJogadores, cartelasJogadores);

        int rodadas = 0;
        Set<Integer> numerosSorteados = new TreeSet<>(); // Utilize TreeSet para manter os números ordenados

        System.out.println("Escolha o modo de sorteio: 0 para AUTO ou 1 para MANUAL:");
        int modoSorteio = lerOpcao();

        while (true) {
            rodadas++;
            System.out.println("Rodada " + rodadas);

            if (modoSorteio == 0) {
                sortearNumerosEAtualizarCartelas(cartelasJogadores, numerosSorteados);
            } else if (modoSorteio == 1) {
                sortearNumeroManualEAtualizarCartelas(cartelasJogadores, numerosSorteados);
            } else {
                System.out.println("Escolha inválida. Por favor, digite 0 para AUTO ou 1 para MANUAL.");
                continue;  // Reinicia o loop para que o usuário faça uma escolha válida.
            }

            exibirRanking(nomesJogadores, cartelasJogadores);

            if (verificarBingo(cartelasJogadores)) {
                List<Integer> indicesVencedores = encontrarIndicesVencedores(cartelasJogadores);
                System.out.print("Bingo! Vencedores:");
                for (int indice : indicesVencedores) {
                    System.out.print(" " + nomesJogadores[indice]);
                }
                System.out.println("!");
                exibirEstatisticasFinais(rodadas, nomesJogadores, cartelasJogadores, numerosSorteados, cartelasIniciaisJogadores);
                break;
            }

            System.out.println("Pressione Enter para continuar ou X para abortar o jogo.");
            String entrada = scanner.nextLine().toUpperCase();
            if (entrada.equals("X")) {
                exibirEstatisticasFinais(rodadas, nomesJogadores, cartelasJogadores, numerosSorteados, cartelasIniciaisJogadores);
                break;
            }
        }
    }

    private static int lerOpcao() {
        while (true) {
            try {
                int modo = Integer.parseInt(scanner.nextLine());
                if (modo == 0 || modo == 1) {
                    return modo;
                } else {
                    System.out.println("Escolha inválida. Por favor, digite 0 para AUTO ou 1 para MANUAL.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite 0 para AUTO ou 1 para MANUAL.");
            }
        }
    }

    private static String[] obterNomesJogadores() {
        System.out.println("Insira os nicknames dos jogadores separados por hífen (EX.:nome-nome):");
        return scanner.nextLine().split("-");
    }

    private static int[][] inicializarCartelasJogadores(String[] nomesJogadores) {
        int[][] cartelasJogadores = new int[nomesJogadores.length][5];

        System.out.println("Escolha 0 para AUTO ou 1 para MANUAL para gerar as cartelas:");

        int escolhaCartela = lerOpcao();

        if (escolhaCartela == 0) {
            for (int i = 0; i < nomesJogadores.length; i++) {
                cartelasJogadores[i] = gerarCartelaAutomatica();
            }
        } else if (escolhaCartela == 1) {
            System.out.println("Digite os números das cartelas dos jogadores separados por '-' (Ex.: 1,2,3,4,5-1,3,4,5,6):");
            String[] cartelasEntrada = scanner.nextLine().split("-");

            for (int i = 0; i < nomesJogadores.length; i++) {
                String[] numeros = cartelasEntrada[i].split(",");
                Set<Integer> numerosEscolhidos = new HashSet<>();

                for (int j = 0; j < numeros.length; j++) {
                    int numeroEscolhido = Integer.parseInt(numeros[j].trim());

                    while (numeroEscolhido < 1 || numeroEscolhido > 60 || numerosEscolhidos.contains(numeroEscolhido)) {
                        System.out.println("Número inválido ou já escolhido. Digite novamente (entre 1 e 60): ");
                        numeroEscolhido = Integer.parseInt(scanner.nextLine());
                    }

                    numerosEscolhidos.add(numeroEscolhido);
                    cartelasJogadores[i][j] = numeroEscolhido;
                }
            }
        }

        return cartelasJogadores;
    }

    private static boolean jogadorCadastrado(String nomeJogador, String[] nomesJogadores) {
        return Arrays.asList(nomesJogadores).contains(nomeJogador);
    }

    private static void sortearNumerosEAtualizarCartelas(int[][] cartelasJogadores, Set<Integer> numerosSorteados) {
        Set<Integer> numerosSorteadosRodada = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            int numeroSorteado = sortearNumeroUnicoAleatorio(numerosSorteados);
            numerosSorteadosRodada.add(numeroSorteado);
            System.out.println("Número sorteado: " + numeroSorteado);
        }

        for (int[] cartela : cartelasJogadores) {
            atualizarCartelaEAcertos(cartela, numerosSorteadosRodada);
        }

        numerosSorteados.addAll(numerosSorteadosRodada);
    }

    private static void sortearNumeroManualEAtualizarCartelas(int[][] cartelasJogadores, Set<Integer> numerosSorteados) {
        System.out.println("Digite os números sorteados manualmente separados por vírgula (1,2,3,4,5): ");
        String[] numerosSorteadosManual = scanner.nextLine().split(",");

        Set<Integer> numerosSorteadosRodada = new HashSet<>();

        for (String numero : numerosSorteadosManual) {
            int numeroSorteado = Integer.parseInt(numero.trim());

            while (numeroSorteado < 1 || numeroSorteado > 60 || numerosSorteados.contains(numeroSorteado)) {
                System.out.println("Número inválido ou já sorteado. Digite novamente: ");
                numeroSorteado = Integer.parseInt(scanner.nextLine());
            }

            numerosSorteadosRodada.add(numeroSorteado);
            System.out.println("Número sorteado: " + numeroSorteado);
        }

        for (int[] cartela : cartelasJogadores) {
            atualizarCartelaEAcertos(cartela, numerosSorteadosRodada);
        }

        numerosSorteados.addAll(numerosSorteadosRodada);
    }

    private static void atualizarCartelaEAcertos(int[] cartela, Set<Integer> numerosSorteadosRodada) {
        for (int numeroSorteado : numerosSorteadosRodada) {
            for (int i = 0; i < cartela.length; i++) {
                if (cartela[i] == numeroSorteado) {
                    cartela[i] = 0;
                    break;
                }
            }
        }
    }

    private static void exibirRanking(String[] nomesJogadores, int[][] cartelasJogadores) {
        int[][] rankingOrdenado = Arrays.copyOf(cartelasJogadores, cartelasJogadores.length);
        Integer[] indices = new Integer[nomesJogadores.length];

        // Inicializa o array de índices
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // Ordena os índices com base no número de acertos e na ordem original
        Arrays.sort(indices, (a, b) -> {
            int comparacao = Integer.compare(contarAcertos(rankingOrdenado[b]), contarAcertos(rankingOrdenado[a]));
            return (comparacao != 0) ? comparacao : Integer.compare(a, b);
        });

        System.out.println("Ranking Geral Ordenado pelo Número de Acertos:");
        for (int i = 0; i < nomesJogadores.length; i++) {
            int indiceJogador = indices[i];
            System.out.println((i + 1) + ". " + nomesJogadores[indiceJogador] + " - " + contarAcertos(rankingOrdenado[indiceJogador]) + " acertos");
        }
    }


    private static void exibirEstatisticasFinais(int rodadas, String[] nomesJogadores, int[][] cartelasJogadores, Set<Integer> numerosSorteados, int[][] cartelasIniciaisJogadores) {
        System.out.println("\nEstatísticas Finais do Jogo:");

        System.out.println("Quantidade de Rodadas: " + rodadas);

        List<Integer> indicesVencedores = encontrarIndicesVencedores(cartelasJogadores);
        System.out.print("Vencedores:");
        for (int indice : indicesVencedores) {
            System.out.print(" " + nomesJogadores[indice]);
        }
        System.out.println();

        for (int indiceVencedorAtual : indicesVencedores) {
            if (indiceVencedorAtual != -1) {
                System.out.println("Vencedor: " + nomesJogadores[indiceVencedorAtual]);
                System.out.print("Cartela Inicial do Vencedor(a): ");
                exibirIntArray(cartelasIniciaisJogadores[indiceVencedorAtual]);
            } else {
                System.out.println("Nenhum vencedor encontrado.");
            }
        }

        System.out.println("Números sorteados em ordem crescente:");
        for (int numeroSorteado : numerosSorteados) {
            System.out.print(numeroSorteado + " ");
        }
        System.out.println();

        exibirRanking(nomesJogadores, cartelasJogadores);
    }


    private static List<Integer> encontrarIndicesVencedores(int[][] cartelasJogadores) {
        List<Integer> indicesVencedores = new ArrayList<>();
        for (int i = 0; i < cartelasJogadores.length; i++) {
            if (verificarBingo(cartelasJogadores[i])) {
                indicesVencedores.add(i);
            }
        }
        return indicesVencedores;
    }


    private static int sortearNumeroUnicoAleatorio(Set<Integer> numerosSorteados) {
        Random random = new Random();
        int numeroSorteado;

        do {
            numeroSorteado = random.nextInt(60) + 1;
        } while (numerosSorteados.contains(numeroSorteado));

        numerosSorteados.add(numeroSorteado);
        return numeroSorteado;
    }

    private static int[] gerarCartelaAutomatica() {
        int[] cartela = new int[5];
        Set<Integer> numerosGerados = new HashSet<>();

        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            int num;
            do {
                num = random.nextInt(60) + 1;
            } while (numerosGerados.contains(num));

            cartela[i] = num;
            numerosGerados.add(num);
        }

        return cartela;
    }

    private static int[] gerarCartelaManual(String[] numeros) {
        int[] cartela = new int[5];

        for (int i = 0; i < 5; i++) {
            cartela[i] = Integer.parseInt(numeros[i]);
        }

        return cartela;
    }

    private static boolean contemNaArray(int[] array, int num) {
        for (int value : array) {
            if (value == num) {
                return true;
            }
        }
        return false;
    }

    private static int contarAcertos(int[] numerosAcertados) {
        int count = 0;
        for (int num : numerosAcertados) {
            if (num == 0) {
                count++;
            }
        }
        return count;
    }

    private static int encontrarIndiceJogador(int[][] original, int[] alvo) {
        for (int i = 0; i < original.length; i++) {
            if (Arrays.equals(original[i], alvo)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean verificarBingo(int[]... cartelas) {
        for (int[] cartela : cartelas) {
            boolean bingo = true;
            for (int num : cartela) {
                if (num != 0) {
                    bingo = false;
                    break;
                }
            }
            if (bingo) {
                return true;
            }
        }
        return false;
    }

    private static void exibirJogadoresECartelas(String[] nomesJogadores, int[][] cartelasJogadores) {
        System.out.println("Jogadores e Cartelas:");

        for (int i = 0; i < nomesJogadores.length; i++) {
            System.out.print(nomesJogadores[i] + ": ");
            exibirIntArray(cartelasJogadores[i]);
        }
    }

    private static void exibirIntArray(int[] array) {
        Arrays.stream(array).forEach(value -> System.out.print(value + " "));
        System.out.println();
    }

    private static void exibirIntArray(int[][] arrays) {
        for (int[] array : arrays) {
            exibirIntArray(array);
        }
    }

    private static int[][] copiarCartelasInicial(int[][] cartelas) {
        int[][] copia = new int[cartelas.length][];

        for (int i = 0; i < cartelas.length; i++) {
            copia[i] = Arrays.copyOf(cartelas[i], cartelas[i].length);
        }

        return copia;
    }
}

