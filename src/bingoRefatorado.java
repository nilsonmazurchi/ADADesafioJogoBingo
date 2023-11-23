import java.util.*;

public class bingoRefatorado {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Jogo de Bingo Multiplayer!");

        String[] playerNames = getPlayerNames();
        int[][] playerCards = initializePlayerCards(playerNames);
        int[][] initialPlayerCards = copyInitialPlayerCards(playerCards);

        displayPlayersAndCards(playerNames, playerCards);

        int rounds = 0;
        Set<Integer> drawnNumbers = new TreeSet<>();

        System.out.println("Escolha o modo de sorteio: 0 para AUTO ou 1 para MANUAL:");
        int drawMode = readOption();

        while (true) {
            rounds++;
            System.out.println("Rodada " + rounds);

            if (drawMode == 0) {
                drawNumbersAndUpdateCards(playerCards, drawnNumbers);
            } else if (drawMode == 1) {
                drawManualNumberAndUpdateCards(playerCards, drawnNumbers);
            } else {
                System.out.println("Escolha inválida. Por favor, digite 0 para AUTO ou 1 para MANUAL.");
                continue;
            }

            displayRanking(playerNames, playerCards);

            if (checkBingo(playerCards)) {
                List<Integer> winnerIndices = findWinnerIndices(playerCards);
                System.out.print("Bingo! Vencedores:");
                for (int index : winnerIndices) {
                    System.out.print(" " + playerNames[index]);
                }
                System.out.println("!");
                displayFinalStatistics(rounds, playerNames, playerCards, drawnNumbers, initialPlayerCards);
                break;
            }

            System.out.println("Pressione Enter para continuar ou X para abortar o jogo.");
            String input = scanner.nextLine().toUpperCase();
            if (input.equals("X")) {
                displayFinalStatistics(rounds, playerNames, playerCards, drawnNumbers, initialPlayerCards);
                break;
            }
        }
    }

    private static int readOption() {
        while (true) {
            try {
                int mode = Integer.parseInt(scanner.nextLine());
                if (mode == 0 || mode == 1) {
                    return mode;
                } else {
                    System.out.println("Escolha inválida. Por favor, digite 0 para AUTO ou 1 para MANUAL.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite 0 para AUTO ou 1 para MANUAL.");
            }
        }
    }

    private static String[] getPlayerNames() {
        System.out.println("Insira os nicknames dos jogadores separados por hífen (EX.:nome-nome):");
        return scanner.nextLine().split("-");
    }

    private static int[][] initializePlayerCards(String[] playerNames) {
        int[][] playerCards = new int[playerNames.length][5];

        System.out.println("Escolha 0 para AUTO ou 1 para MANUAL para gerar as cartelas:");

        int cardChoice = readOption();

        if (cardChoice == 0) {
            for (int i = 0; i < playerNames.length; i++) {
                playerCards[i] = generateAutomaticCard();
            }
        } else if (cardChoice == 1) {
            boolean validInput = false;

            while (!validInput) {
                System.out.println("Digite os números das cartelas dos jogadores separados por '-' (Ex.: 1,2,3,4,5-1,3,4,5,6):");
                String cardInput = scanner.nextLine();

                if (isValidCardInput(cardInput, playerNames.length)) {
                    validInput = true;

                    String[] cardInputs = cardInput.split("-");

                    for (int i = 0; i < playerNames.length; i++) {
                        String[] numbers = cardInputs[i].split(",");
                        Set<Integer> chosenNumbers = new HashSet<>();

                        for (int j = 0; j < numbers.length; j++) {
                            int chosenNumber = Integer.parseInt(numbers[j].trim());

                            while (chosenNumber < 1 || chosenNumber > 60 || chosenNumbers.contains(chosenNumber)) {
                                System.out.println("Número inválido ou já escolhido. Digite novamente (entre 1 e 60): ");
                                chosenNumber = Integer.parseInt(scanner.nextLine());
                            }

                            chosenNumbers.add(chosenNumber);
                            playerCards[i][j] = chosenNumber;
                        }
                    }
                } else {
                    System.out.println("Formato inválido. Tente novamente.");
                }
            }
        }

        return playerCards;
    }

    private static boolean isValidCardInput(String input, int expectedLength) {
        String[] cardInputs = input.split("-");

        if (cardInputs.length != expectedLength) {
            return false;
        }

        for (String cardInput : cardInputs) {
            String[] numbers = cardInput.split(",");
            Set<String> uniqueNumbers = new HashSet<>(Arrays.asList(numbers));

            if (numbers.length != uniqueNumbers.size() || numbers.length != 5) {
                return false;
            }

            for (String number : numbers) {
                if (!number.matches("\\d+")) {
                    return false;
                }

                int num = Integer.parseInt(number);
                if (num < 1 || num > 60) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void drawNumbersAndUpdateCards(int[][] playerCards, Set<Integer> drawnNumbers) {
        Set<Integer> drawnNumbersRound = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            int numberDrawn = drawUniqueRandomNumber(drawnNumbers);
            drawnNumbersRound.add(numberDrawn);
            System.out.println("Número sorteado: " + numberDrawn);
        }

        for (int[] playerCard : playerCards) {
            updateCardAndHits(playerCard, drawnNumbersRound);
        }

        drawnNumbers.addAll(drawnNumbersRound);
    }

    private static void drawManualNumberAndUpdateCards(int[][] playerCards, Set<Integer> drawnNumbers) {
        System.out.println("Digite os 5 números sorteados manualmente separados por vírgula (1,2,3,4,5): ");
        String manualInput = scanner.nextLine();

        while (!isValidManualInput(manualInput)) {
            System.out.println("Formato inválido. Por favor, digite exatamente 5 números separados por vírgula.");
            manualInput = scanner.nextLine();
        }

        String[] manualDrawnNumbers = manualInput.split(",");

        Set<Integer> drawnNumbersRound = new HashSet<>();

        for (String manualNumber : manualDrawnNumbers) {
            int numberDrawn = Integer.parseInt(manualNumber.trim());

            while (numberDrawn < 1 || numberDrawn > 60 || drawnNumbers.contains(numberDrawn)) {
                System.out.println("Número inválido ou já sorteado. Digite novamente: ");
                numberDrawn = Integer.parseInt(scanner.nextLine());
            }

            drawnNumbersRound.add(numberDrawn);
            System.out.println("Número sorteado: " + numberDrawn);
        }

        for (int[] playerCard : playerCards) {
            updateCardAndHits(playerCard, drawnNumbersRound);
        }

        drawnNumbers.addAll(drawnNumbersRound);
    }

    private static boolean isValidManualInput(String input) {
        String[] numbers = input.split(",");
        return numbers.length == 5 && Arrays.stream(numbers).allMatch(s -> s.matches("\\d+"));
    }

    private static void updateCardAndHits(int[] playerCard, Set<Integer> drawnNumbersRound) {
        for (int numberDrawn : drawnNumbersRound) {
            for (int i = 0; i < playerCard.length; i++) {
                if (playerCard[i] == numberDrawn) {
                    playerCard[i] = 0;
                    break;
                }
            }
        }
    }

    private static List<Integer> findWinnerIndices(int[][] playerCards) {
        List<Integer> winnerIndices = new ArrayList<>();
        for (int i = 0; i < playerCards.length; i++) {
            if (checkBingo(playerCards[i])) {
                winnerIndices.add(i);
            }
        }
        return winnerIndices;
    }

    private static int drawUniqueRandomNumber(Set<Integer> drawnNumbers) {
        int numberDrawn;

        do {
            numberDrawn = random.nextInt(60) + 1;
        } while (drawnNumbers.contains(numberDrawn));

        drawnNumbers.add(numberDrawn);
        return numberDrawn;
    }

    private static int[] generateAutomaticCard() {
        int[] card = new int[5];
        Set<Integer> generatedNumbers = new HashSet<>();

        int i = 0;
        while (i < 5) {
            int num = random.nextInt(60) + 1;
            if (!generatedNumbers.contains(num)) {
                card[i++] = num;
                generatedNumbers.add(num);
            }
        }

        return card;
    }

    private static boolean checkBingo(int[]... playerCards) {
        for (int[] playerCard : playerCards) {
            boolean bingo = Arrays.stream(playerCard).allMatch(num -> num == 0);
            if (bingo) {
                return true;
            }
        }
        return false;
    }

    private static void displayRanking(String[] playerNames, int[][] playerCards) {
        int[][] sortedRanking = Arrays.copyOf(playerCards, playerCards.length);
        Integer[] indices = new Integer[playerNames.length];

        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        Arrays.sort(indices, (a, b) -> {
            int comparison = Integer.compare(countHits(sortedRanking[b]), countHits(sortedRanking[a]));
            return (comparison != 0) ? comparison : Integer.compare(a, b);
        });

        System.out.println("Ranking Geral Ordenado pelo Número de Acertos:");
        for (int i = 0; i < playerNames.length; i++) {
            int playerIndex = indices[i];
            System.out.println((i + 1) + ". " + playerNames[playerIndex] + " - " + countHits(sortedRanking[playerIndex]) + " acertos");
        }
    }

    private static void displayFinalStatistics(int rounds, String[] playerNames, int[][] playerCards, Set<Integer> drawnNumbers, int[][] initialPlayerCards) {
        System.out.println("\nEstatísticas Finais do Jogo:");

        System.out.println("Quantidade de Rodadas: " + rounds);

        List<Integer> winnerIndices = findWinnerIndices(playerCards);
        System.out.print("Vencedores:");
        for (int winnerIndex : winnerIndices) {
            System.out.print(" " + playerNames[winnerIndex]);
        }
        System.out.println();

        for (int winnerIndex : winnerIndices) {
            if (winnerIndex != -1) {
                System.out.println("Vencedor: " + playerNames[winnerIndex]);
                System.out.print("Cartela Inicial do Vencedor(a): ");
                displayIntArray(initialPlayerCards[winnerIndex]);
            } else {
                System.out.println("Nenhum vencedor encontrado.");
            }
        }

        System.out.println("Números sorteados em ordem crescente:");
        for (int drawnNumber : drawnNumbers) {
            System.out.print(drawnNumber + " ");
        }
        System.out.println();

        displayRanking(playerNames, playerCards);
    }

    private static void displayPlayersAndCards(String[] playerNames, int[][] playerCards) {
        System.out.println("Jogadores e Cartelas:");

        for (int i = 0; i < playerNames.length; i++) {
            System.out.print(playerNames[i] + ": ");
            displayIntArray(playerCards[i]);
        }
    }

    private static void displayIntArray(int[] array) {
        Arrays.stream(array).forEach(value -> System.out.print(value + " "));
        System.out.println();
    }

    private static void displayIntArray(int[][] arrays) {
        for (int[] array : arrays) {
            displayIntArray(array);
        }
    }

    private static int countHits(int[] numbers) {
        return (int) Arrays.stream(numbers).filter(num -> num == 0).count();
    }

    private static int[][] copyInitialPlayerCards(int[][] playerCards) {
        int[][] copy = new int[playerCards.length][];

        for (int i = 0; i < playerCards.length; i++) {
            copy[i] = Arrays.copyOf(playerCards[i], playerCards[i].length);
        }

        return copy;
    }
}

