import java.util.*;
import java.io.*;
import java.net.*;

public class Cliente {

    PrintStream saida;

    public static void main(String[] args)
        throws UnknownHostException, IOException {
        // dispara cliente
        String nome;
        System.out.println("Nome");
        Scanner teclado = new Scanner(System.in);
        nome = teclado.nextLine();
        new Cliente("127.0.0.1", 12345,nome).executa();


    }

    private String host;
    private int porta;
    private String nome;

    public Cliente (String host, int porta, String nome) {
        this.host = host;
        this.porta = porta;
        this.nome = nome;
    }

    public void executa() throws UnknownHostException, IOException {
        Socket cliente = new Socket(this.host, this.porta);
        System.out.println("O cliente se conectou ao servidor!");
        saida = new PrintStream(cliente.getOutputStream());
        saida.println(this.nome);
        // thread para receber mensagens do servidor
        Recebedor r = new Recebedor(cliente.getInputStream());
        new Thread(r).start();

        // lê msgs do teclado e manda pro servidor
        Scanner teclado = new Scanner(System.in);
        
        while (teclado.hasNextLine()) {
            saida.println(teclado.nextLine());
        }

        saida.close();
        teclado.close();
        cliente.close();
    }
}

class Recebedor implements Runnable {

    private InputStream servidor;

    Recebedor(InputStream servidor) {
        this.servidor = servidor;
    }

    public void run() {
        // recebe msgs do servidor e imprime na tela
        Scanner s = new Scanner(this.servidor);
        while (s.hasNextLine()) {
            System.out.println(s.nextLine());
        }
    }
}
