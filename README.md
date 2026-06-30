# Calculador de Circuito

Aplicação desktop em **Java Swing** para análise de um circuito resistivo: um resistor `R1` em série com três resistores `R2` iguais associados em paralelo, alimentados por uma fonte de tensão `E`. A partir dos valores informados, o programa calcula a corrente em um dos ramos paralelos, seu sentido e a energia total dissipada em um intervalo de tempo.

## Funcionalidades

- Entrada de `R1`, `R2`, tensão da fonte (`E`) e tempo de análise, com campos que aceitam apenas números.
- Validação de limites de bancada (resistências até 100 kΩ, tensão até 60 V, tempo até 60 min), com mensagens de erro quando algum valor é digitado fora da faixa.
- Cálculo automático de:
  - **(a)** Valor absoluto da corrente `i1`
  - **(b)** Sentido da corrente `i1`
  - **(c)** Energia total dissipada no tempo informado
- Painel de **Cálculos** mostrando resumidamente as fórmulas e resultados intermediários (R2,eq, Rtotal, Itotal, i1, t e E).

## Como executar

Pré-requisito: JDK instalado (Java 8 ou superior).

```bash
# Compilar
javac calculadorCircuito/CalculadorCircuito.java

# Executar (a partir da pasta que contém o pacote calculadorCircuito)
java calculadorCircuito.CalculadorCircuito
```

> O arquivo está dentro do pacote `calculadorCircuito`, então o `.java` deve estar em uma pasta com esse nome (ex: `src/calculadorCircuito/CalculadorCircuito.java`) para compilar e rodar corretamente.

## Estrutura do circuito

```
        R1
E ──/\/\/\──┬──/\/\/\── R2 ──┐
            ├──/\/\/\── R2 ──┤
            └──/\/\/\── R2 ──┘
```

- `R2,eq = R2 / 3`
- `Rtotal = R1 + R2,eq`
- `Itotal = E / Rtotal`
- `i1 = Itotal / 3`
- `E_dissipada = E × Itotal × t`

## Tecnologias

- Java SE
- Swing (interface gráfica)

## Licença

Projeto acadêmico, livre para uso e estudo.
