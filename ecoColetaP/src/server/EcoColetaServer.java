package server;

import model.PontoColeta;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EcoColetaServer {
    private static final int PORTA = 12345; 
    private static List<PontoColeta> bancoDeDados = new ArrayList<>();
    private static int proximoId = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor EcoColeta ONLINE. Porta: " + PORTA); 
            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                new Thread(new ClientHandler(clientSocket)).start(); 
            }
        } catch (IOException e) {
            System.err.println("ERRO (RNF03): Não foi possível iniciar o servidor na porta " + PORTA + ". " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                String comando = (String) in.readObject();
                
                if (comando.equals("CADASTRAR")) {
                    cadastrarPonto(in, out); 
                } else if (comando.equals("CONSULTAR")) {
                    consultarTodos(out); 
                }

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
            } 
        }

        private void cadastrarPonto(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
            PontoColeta novoPonto = (PontoColeta) in.readObject();
            
            
            if (novoPonto.getTipoResiduo() == null || novoPonto.getTipoResiduo().trim().isEmpty()) {
                out.writeObject("ERRO (RF04): Tipo de Resíduo é obrigatório."); 
                return;
            }
            
           
            synchronized (EcoColetaServer.class) { 
                novoPonto.setId(proximoId++);
                bancoDeDados.add(novoPonto);
            }
            
            out.writeObject("SUCESSO (RF05): Ponto cadastrado com ID " + novoPonto.getId()); 
        }

        private void consultarTodos(ObjectOutputStream out) throws IOException {
            long inicio = System.currentTimeMillis();
           
           
            out.writeObject(bancoDeDados); 
            long fim = System.currentTimeMillis();
            System.out.printf("Consulta (RNF01) executada em %d ms.\n", (fim - inicio));
        }
    }
}