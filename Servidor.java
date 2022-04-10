import java.util.*;
import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) throws IOException {
        // inicia o servidor
        new Servidor(12345).executa();
    }

    private int porta;
    private List<PrintStream> clientes;
    List<String> nomes;

    public Servidor (int porta) {
        this.porta = porta;
        this.clientes = new ArrayList<PrintStream>();
        this.nomes = new ArrayList<String>();
    }

    public void executa () throws IOException {
        ServerSocket servidor = new ServerSocket(this.porta);
        System.out.println("Porta 12345 aberta!");

        while (true) {
            // aceita um cliente
            Socket cliente = servidor.accept();
            System.out.println("Nova conexao com o cliente " +
                cliente.getInetAddress().getHostAddress()
            );

            // adiciona saida do cliente à lista
            PrintStream ps = new PrintStream(cliente.getOutputStream());
            this.clientes.add(ps);

            // cria tratador de cliente numa nova thread
            TrataCliente tc = new TrataCliente(cliente.getInputStream(), this,ps);
            new Thread(tc).start();
        }

    }

    public void distribuiMensagem(String msg, PrintStream print) {
        // envia msg para todo mundo
        int pos = clientes.indexOf(print);
        for (int i = 0; i< clientes.size(); i++) {
            if(i != pos){
                clientes.get(i).println(nomes.get(pos) + " lhe disse:\n" + msg);
            }
        }
    }
}

class TrataCliente implements Runnable {

    private InputStream cliente;
    private Servidor servidor;
    PrintStream print;
    boolean inicio;

    TrataCliente(InputStream cliente, Servidor servidor, PrintStream print) {
        this.cliente = cliente;
        this.servidor = servidor;
        this.print = print;
        this.inicio = true;
    }

    public void run() {
        // quando chegar uma msg, distribui pra todos
        Scanner s = new Scanner(this.cliente);
        while (s.hasNextLine()) {
            String mensagem = s.nextLine();
            if(inicio){
                servidor.nomes.add(mensagem);
                inicio = false;
            }
            else{
                servidor.distribuiMensagem(mensagem,print);
            }
        }
        s.close();
    }
}
