package client;

import model.PontoColeta;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class EcoColetaClient {
    private static final String HOST = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Cliente EcoColeta ---");
        
        while (true) {
           
            System.out.println("\nMENU:");
            System.out.println("1. Cadastrar Ponto (RF01)");
            System.out.println("2. Consultar Todos (RF02)");
            System.out.println("3. Sair");
            System.out.print("Opção: ");
            
            String opcao = scanner.nextLine();
            if (opcao.equals("3")) break;
            
            try {
                processarRequisicao(opcao, scanner);
            } catch (Exception e) {
                System.err.println("\n[ERRO FATAL] Falha na comunicação: Verifique se o servidor está rodando.");
            }
        }
        scanner.close();
    }
    
    private static void processarRequisicao(String opcao, Scanner scanner) throws IOException, ClassNotFoundException {
        
        try (
            Socket socket = new Socket(HOST, PORTA); 
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            if (opcao.equals("1")) {
               
                out.writeObject("CADASTRAR");
                
                PontoColeta novoPonto = coletarDadosCadastro(scanner);
                out.writeObject(novoPonto); 
                
                String resposta = (String) in.readObject(); 
                System.out.println("\n>>> RESPOSTA: " + resposta);

            } else if (opcao.equals("2")) {
                
                out.writeObject("CONSULTAR");
                
                List<PontoColeta> pontos = (List<PontoColeta>) in.readObject();
                System.out.println("\n--- LISTA DE PONTOS (RF02) ---");
                pontos.forEach(System.out::println);
                System.out.println("---------------------------------");
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }
    
    private static PontoColeta coletarDadosCadastro(Scanner scanner) {
        System.out.print("  Nome do Ponto: ");
        String nome = scanner.nextLine();
        System.out.print("  Endereço: ");
        String endereco = scanner.nextLine();
        System.out.print("  Tipo de Resíduo (Papel, Plástico, etc.): ");
        String residuo = scanner.nextLine();
        
        return new PontoColeta(nome, endereco, residuo);
    }
}