import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Teste7RedeNeuralAutoOrganizada {
    private final int tamanhoEntrada;
    private final int tamanhoSaida;
    private final int maxIteracoes;
    private final double taxaAprendizado;
    private final double[][] matrizPesos;
    private final Map<String, int[]> tiposVeiculos;

    public Teste7RedeNeuralAutoOrganizada(int tamanhoEntrada, int tamanhoSaida, int maxIteracoes, double taxaAprendizado) {
        this.tamanhoEntrada = tamanhoEntrada;
        this.tamanhoSaida = tamanhoSaida;
        this.maxIteracoes = maxIteracoes;
        this.taxaAprendizado = taxaAprendizado;
        this.matrizPesos = new double[tamanhoSaida][tamanhoEntrada];
        this.tiposVeiculos = new HashMap<>();
    }

    public void adicionarTipoVeiculo(String nomeTipo, int[] vetorTipo) {
        tiposVeiculos.put(nomeTipo, vetorTipo);
    }

    public void treinar() {
        inicializarPesos();

        for (Map.Entry<String, int[]> entrada : tiposVeiculos.entrySet()) {
            String nomeTipo = entrada.getKey();
            int[] vetorTipo = entrada.getValue();
            int iteracao = 0;
            double erro = Double.MAX_VALUE;

            while (iteracao < maxIteracoes && erro >= 0.001) {
                int indiceVencedor = encontrarIndiceVencedor(vetorTipo);
                boolean ocorreuAlteracao = atualizarPesos(indiceVencedor, vetorTipo);
                if (ocorreuAlteracao) {
                    System.out.println("Vetor: " + nomeTipo + " - Iteração: " + iteracao);
                    imprimirPesos();
                }
                erro = calcularErro(vetorTipo, matrizPesos[indiceVencedor]);
                System.out.println("Erro: " + erro);
                iteracao++;
            }
        }
    }

    private void inicializarPesos() {
        matrizPesos[0] = new double[]{1, 0, 0, 0, 0, 1, 0};
        matrizPesos[1] = new double[]{0, 1, 1, 1, 0, 0, 1};
        matrizPesos[2] = new double[]{1, 0, 1, 0, 1, 0, 1};
        matrizPesos[3] = new double[]{0, 0, 0, 0, 0, 1, 1};
    }

    private int encontrarIndiceVencedor(int[] entrada) {
        double menorDistancia = Double.MAX_VALUE;
        int indiceVencedor = 0;

        for (int i = 0; i < tamanhoSaida; i++) {
            double distancia = calcularDistanciaEuclidiana(entrada, matrizPesos[i]);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                indiceVencedor = i;
            }
        }

        return indiceVencedor;
    }

    private double calcularDistanciaEuclidiana(int[] vetor1, double[] vetor2) {
        double soma = 0;
        for (int i = 0; i < vetor1.length; i++) {
            double diferenca = vetor1[i] - vetor2[i];
            soma += diferenca * diferenca;
        }
        return Math.sqrt(soma);
    }

    private boolean atualizarPesos(int indiceVencedor, int[] entrada) {
        final double raioVizinhanca = tamanhoSaida / 2;
        boolean ocorreuAlteracao = false;

        for (int i = 0; i < tamanhoSaida; i++) {
            double distanciaParaVencedor = Math.abs(i - indiceVencedor);

            if (distanciaParaVencedor <= raioVizinhanca) {
                double influencia = 1 - (distanciaParaVencedor / raioVizinhanca);
                for (int j = 0; j < tamanhoEntrada; j++) {
                    double delta = influencia * taxaAprendizado * (entrada[j] - matrizPesos[i][j]);
                    if (Math.abs(delta) > 1e-5) {
                        matrizPesos[i][j] += delta;
                        ocorreuAlteracao = true;
                    }
                }
            }
        }

        return ocorreuAlteracao;
    }

    private double calcularErro(int[] vetor1, double[] vetor2) {
        double soma = 0;
        for (int i = 0; i < vetor1.length; i++) {
            double diferenca = vetor1[i] - vetor2[i];
            soma += diferenca * diferenca;
        }
        return Math.sqrt(soma);
    }

    public String classificar(int[] entrada) {
        int indiceVencedor = encontrarIndiceVencedor(entrada);

        // Mapeamento dos índices vencedores para os tipos de veículos
        String[] tiposVeiculos = {"Caminhão", "Ônibus", "Carro", "Moto"};

        return tiposVeiculos[indiceVencedor];
    }

    public int[] lerVetorEntrada() {
        Scanner scanner = new Scanner(System.in);
        int[] vetor = new int[tamanhoEntrada];

        System.out.println("Digite o vetor de entrada (separado por espaços):");
        for (int i = 0; i < tamanhoEntrada; i++) {
            vetor[i] = scanner.nextInt();
        }

        return vetor;
    }

    private void imprimirPesos() {
        System.out.println("Pesos atualizados:");
        for (int i = 0; i < tamanhoSaida; i++) {
            for (int j = 0; j < tamanhoEntrada; j++) {
                System.out.print(matrizPesos[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Teste7RedeNeuralAutoOrganizada redeNeural = new Teste7RedeNeuralAutoOrganizada(7, 4, 100, 0.5);

        redeNeural.adicionarTipoVeiculo("Caminhão", new int[]{0, 1, 1, 1, 1, 1, 0});
        redeNeural.adicionarTipoVeiculo("Ônibus", new int[]{1, 0, 0, 1, 1, 1, 0});
        redeNeural.adicionarTipoVeiculo("Carro", new int[]{0, 0, 0, 0, 1, 0, 1});
        redeNeural.adicionarTipoVeiculo("Moto", new int[]{0, 0, 0, 0, 0, 0, 1});

        redeNeural.treinar();

        int[] entrada = redeNeural.lerVetorEntrada();
        String tipoVeiculo = redeNeural.classificar(entrada);

        System.out.println("Tipo de veículo: " + tipoVeiculo);
    }
}
