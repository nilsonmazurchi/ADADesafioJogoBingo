import java.util.*;

public class bingo {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Jogo de Bingo Multiplayer!");

        String[] nomesJogadores = obterNomesJogadores();
        int[][] cartelasJogadores = inicializarCartelasJogadores(nomesJogadores);
        int[][] cartelasIniciaisJogadores = copiarCartelasInicial(cartelasJogadores);

        exibirJogadoresECartelas(nomesJogadores, cartelasJogadores);

        int rodadas = 0;
        Set<Integer> numerosSorteados = new HashSet<>();

        System.out.println("Escolha o modo de sorteio: 0 para AUTO ou 1 para MANUAL:");
        int modoSorteio = Integer.parseInt(scanner.nextLine());

        while (true) {
            rodadas++;
            System.out.println("Rodada " + rodadas);

            if (modoSorteio == 0) {
                sortearNumerosEAtualizarCartelas(cartelasJogadores, numerosSorteados);
            } else if (modoSorteio == 1) {
                sortearNumeroManualEAtualizarCartelas(cartelasJogadores, numerosSorteados);
            }

            exibirRanking(nomesJogadores, cartelasJogadores);

            if (verificarBingo(cartelasJogadores)) {
                System.out.println("Bingo! " + nomesJogadores[encontrarIndiceVencedor(cartelasJogadores)] + " venceu!");
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

    private static String[] obterNomesJogadores() {
        System.out.println("Insira os nicknames dos jogadores separados por hífen(EX.:nome-nome):");
        return scanner.nextLine().split("-");
    }

    private static int[][] inicializarCartelasJogadores(String[] nomesJogadores) {
        int[][] cartelasJogadores = new int[nomesJogadores.length][5];

        for (int i = 0; i < nomesJogadores.length; i++) {
            System.out.println(nomesJogadores[i] + ", escolha 0 para AUTO ou 1 para MANUAL para gerar sua cartela:");
            int escolhaCartela = Integer.parseInt(scanner.nextLine());

            if (escolhaCartela == 0) {
                cartelasJogadores[i] = gerarCartelaAleatoria();
            } else if (escolhaCartela == 1) {
                while (!jogadorCadastrado(nomesJogadores[i], nomesJogadores) || !jogadorUnico(nomesJogadores, i)) {
                    System.out.println("Você não está cadastrado ou o nickname já foi escolhido. Aguarde a próxima rodada.");
                    System.out.println("Insira seu nickname novamente:");
                    nomesJogadores[i] = scanner.nextLine();
                }

                System.out.println("Digite os números da sua cartela separados por vírgula (1,2,3,4,5): ");
                String[] numeros = scanner.nextLine().split(",");
                cartelasJogadores[i] = gerarCartelaManual(numeros);
            }
        }

        return cartelasJogadores;
    }

    private static boolean jogadorCadastrado(String nomeJogador, String[] nomesJogadores) {
        return Arrays.asList(nomesJogadores).contains(nomeJogador);
    }

    private static boolean jogadorUnico(String[] nomesJogadores, int indiceAtual) {
        return Collections.frequency(Arrays.asList(nomesJogadores), nomesJogadores[indiceAtual]) == 1;
    }

    private static void sortearNumerosEAtualizarCartelas(int[][] cartelasJogadores, Set<Integer> numerosSorteados) {
        int numeroSorteado = sortearNumeroUnicoAleatorio(numerosSorteados);
        System.out.println("Número sorteado: " + numeroSorteado);

        for (int[] cartela : cartelasJogadores) {
            atualizarCartelaEAcertos(cartela, numeroSorteado);
        }
    }

    private static void sortearNumeroManualEAtualizarCartelas(int[][] cartelasJogadores, Set<Integer> numerosSorteados) {
        System.out.println("Digite o número sorteado manualmente (1 a 60): ");
        int numeroSorteado = Integer.parseInt(scanner.nextLine());

        while (numeroSorteado < 1 || numeroSorteado > 60 || numerosSorteados.contains(numeroSorteado)) {
            System.out.println("Número inválido ou já sorteado. Digite novamente: ");
            numeroSorteado = Integer.parseInt(scanner.nextLine());
        }

        System.out.println("Número sorteado: " + numeroSorteado);

        for (int[] cartela : cartelasJogadores) {
            atualizarCartelaEAcertos(cartela, numeroSorteado);
        }

        numerosSorteados.add(numeroSorteado);
    }

    private static void atualizarCartelaEAcertos(int[] cartela, int numeroSorteado) {
        for (int i = 0; i < cartela.length; i++) {
            if (cartela[i] == numeroSorteado) {
                cartela[i] = 0;
                break;
            }
        }
    }

    private static void exibirRanking(String[] nomesJogadores, int[][] cartelasJogadores) {
        int[][] rankingOrdenado = Arrays.copyOf(cartelasJogadores, cartelasJogadores.length);
        Arrays.sort(rankingOrdenado, (a, b) -> Integer.compare(contarAcertos(b), contarAcertos(a)));

        System.out.println("Ranking:");
        for (int i = 0; i < Math.min(3, nomesJogadores.length); i++) {
            int indiceJogador = encontrarIndiceJogador(cartelasJogadores, rankingOrdenado[i]);
            System.out.println((i + 1) + ". " + nomesJogadores[indiceJogador] + " - " + contarAcertos(rankingOrdenado[i]) + " acertos");
        }
    }

    private static void exibirEstatisticasFinais(int rodadas, String[] nomesJogadores, int[][] cartelasJogadores, Set<Integer> numerosSorteados, int[][] cartelasIniciaisJogadores) {
        System.out.println("\nEstatísticas Finais do Jogo:");

        System.out.println("Quantidade de Rodadas: " + rodadas);

        int indiceVencedor = encontrarIndiceVencedor(cartelasJogadores);
        System.out.println("Nickname do Vencedor: " + nomesJogadores[indiceVencedor]);

        /*System.out.println("Números da Cartela Vencedora:");
        int[] cartelaVencedora = cartelasJogadores[indiceVencedor];
        exibirIntArray(cartelaVencedora);*/

        int indiceVencedorAtual = encontrarIndiceVencedor(cartelasJogadores);

        if (indiceVencedorAtual != -1) {
            System.out.println("Vencedor: " + nomesJogadores[indiceVencedorAtual]);
            System.out.print("Cartela Inicial do Vencedor(a): ");
            exibirIntArray(cartelasIniciaisJogadores[indiceVencedorAtual]);
        } else {
            System.out.println("Nenhum vencedor encontrado.");
        }

        System.out.println("Números sorteados em ordem:");
        for (int numeroSorteado : numerosSorteados) {
            System.out.print(numeroSorteado + " ");
        }
        System.out.println();

        System.out.println("Ranking Geral Ordenado pelo Número de Acertos:");
        int[][] rankingOrdenado = Arrays.copyOf(cartelasJogadores, cartelasJogadores.length);
        Arrays.sort(rankingOrdenado, Comparator.comparingInt(bingo::contarAcertos).reversed());

        for (int i = 0; i < nomesJogadores.length; i++) {
            System.out.println((i + 1) + ". " + nomesJogadores[encontrarIndiceJogador(cartelasJogadores, rankingOrdenado[i])] + " - " + contarAcertos(rankingOrdenado[i]) + " acertos");
        }
    }

    private static int encontrarIndiceVencedor(int[][] cartelasJogadores) {
        for (int i = 0; i < cartelasJogadores.length; i++) {
            if (verificarBingo(cartelasJogadores[i])) {
                return i;
            }
        }
        return -1;
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

    private static int[] gerarCartelaAleatoria() {
        int[] cartela = new int[5];
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            int num;
            do {
                num = random.nextInt(60) + 1;
            } while (contemNaArray(cartela, num));
            cartela[i] = num;
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
        return Arrays.asList(array).contains(num);
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

    private static int[][] copiarCartelasInicial(int[][] cartelas) {
        int[][] copia = new int[cartelas.length][];

        for (int i = 0; i < cartelas.length; i++) {
            copia[i] = Arrays.copyOf(cartelas[i], cartelas[i].length);
        }

        return copia;
    }
}

