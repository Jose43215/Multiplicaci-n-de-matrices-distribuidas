
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author JARM
 */
public class Matrices {
    
    public static void main(String args[]) {
        int nodo = Integer.parseInt(args[0]);
        int N = 6;
        
        if(args.length > 1){
            N = Integer.parseInt(args[1]);
        }
        
        if(nodo == 0){
            // Cliente TCP
            try{
                // Declarar las matrices A, B y C
                double[][] A = new double[N][N];
                double[][] B = new double[N][N];
                double[][] C = new double[N][N];
                
                // Inicializar las matrices A y B
                for(int i = 0; i < N; i++){
                    for(int j = 0; j < N; j++){
                        A[i][j] = 2*i+j;
                        B[i][j] = 3*i-j;
                    }
                }
                
                if(N <= 12){
                // Imprimimos la matriz B
                System.out.println("Matriz B:");
                imprimirMatriz(B);
                }
                
                // Transponer la matriz B
                for(int i = 0; i< N; i++){
                    for (int j = i+1; j < N; j++){
                        double temp = B[i][j];
                        B[i][j] = B[j][i];
                        B[j][i] = temp;
                    }
                }
                
                //Dividir la matriz A en tres partes
                double[][] A1 = Arrays.copyOfRange(A, 0, N/3);
                double[][] A2 = Arrays.copyOfRange(A, N/3, 2*N/3);
                double[][] A3 = Arrays.copyOfRange(A, 2*N/3, N);
                
                //Dividir la matriz B en tres partes
                double[][] B1 = Arrays.copyOfRange(B, 0, N/3);
                double[][] B2 = Arrays.copyOfRange(B, N/3, 2*N/3);
                double[][] B3 = Arrays.copyOfRange(B, 2*N/3, N);
                
                // Conectar con los servidores y enviar las matrices
                Socket[] sockets = new Socket[3];
                for (int i = 0; i < 3; i++) {
                    sockets[i] = new Socket(args[i+2], 8000 + i + 1);
                    ObjectOutputStream out = new ObjectOutputStream(sockets[i].getOutputStream());
                    out.writeObject(i == 0 ? A1 : i == 1 ? A2 : A3);
                    out.writeObject(B1);
                    out.writeObject(B2);
                    out.writeObject(B3);
                }
                
                // Recibir las matrices C
                double[][] C1 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C2 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C3 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C4 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C5 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C6 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C7 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C8 = new double[N/3 + N%3][N/3 + N%3];
                double[][] C9 = new double[N/3 + N%3][N/3 + N%3];
                
                for (int i = 0; i < 3; i++) {
                    ObjectInputStream ins = new ObjectInputStream(sockets[i].getInputStream());
                    if(i == 0){
                        C1 = (double[][]) ins.readObject();
                        C2 = (double[][]) ins.readObject();
                        C3 = (double[][]) ins.readObject();
                    }else if(i == 1){
                        C4 = (double[][]) ins.readObject();
                        C5 = (double[][]) ins.readObject();
                        C6 = (double[][]) ins.readObject();
                    }else if(i==2){
                        C7 = (double[][]) ins.readObject();
                        C8 = (double[][]) ins.readObject();
                        C9 = (double[][]) ins.readObject();
                    }
                }
                
                // Unimos las 9 matrices en una sola matriz AUX
                double[][][][] AUX = new double[][][][] {
                    {C1, C2, C3},
                    {C4, C5, C6},
                    {C7, C8, C9}
                };
                
                // Creamos la matriz final de tamaño N x N y la llenamos con los valores de las matrices C1, C2, ..., C9
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        double[][] Cij = AUX[i][j];
                        int filasCij = Cij.length;
                        int columnasCij = Cij[0].length;
                        for (int k = 0; k < filasCij; k++) {
                            for (int l = 0; l < columnasCij; l++) {
                                C[i*filasCij + k][j*columnasCij + l] = Cij[k][l];
                            }
                        }
                    }
                }
                
                // Realizamos el checksum
                double checksum = 0.0;
                for(int i = 0; i < N; i++){
                    for(int j = 0; j < N; j++){
                        checksum += C[i][j];
                    }
                }
                
                
                if(N<=12){
                    System.out.println("\nMatriz A:");
                    imprimirMatriz(A);
                    System.out.println("\nMatriz BT:");
                    imprimirMatriz(B);
                    System.out.println("\nMatriz C:");
                    imprimirMatriz(C);
                }
                
                //Imprimimos el checkSum
                System.out.println("\nCheckSum: "+checksum);
                
                
                //cerramos los sockets
                for(int i = 0; i<sockets.length; i++){
                    sockets[i].close();
                }
                
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }else if(nodo == 1){
            try{
                 // Paso 1: Crear el socket del servidor
                int puerto = 8001;
                ServerSocket servidor = new ServerSocket(puerto);
                System.out.println("Servidor en nodo 1 iniciado.");
                
                while(true){
                    // Paso 2: Esperar a que llegue una conexión del nodo 0
                    Socket conexion = servidor.accept();
                    System.out.println("Conexión establecida con el nodo 0.");
                    
                    // Paso 3: Recibir las matrices A1, B1, B2 y B3 del nodo 0
                    ObjectInputStream entrada = new ObjectInputStream(conexion.getInputStream());
                    double[][] A1 = (double[][]) entrada.readObject();
                    double[][] B1 = (double[][]) entrada.readObject();
                    double[][] B2 = (double[][]) entrada.readObject();
                    double[][] B3 = (double[][]) entrada.readObject();
                    
                     // Paso 4: Calcular las matrices C1, C2 y C3
                    int tamBloque = N/3;
                    double[][] C1 = new double[tamBloque][tamBloque];
                    double[][] C2 = new double[tamBloque][tamBloque];
                    double[][] C3 = new double[tamBloque][tamBloque];
                    for (int i = 0; i < tamBloque; i++) {
                        for (int j = 0; j < tamBloque; j++) {
                            for (int k = 0; k < N; k++) {
                                C1[i][j] += A1[i][k] * B1[j][k];
                                C2[i][j] += A1[i][k] * B2[j][k];
                                C3[i][j] += A1[i][k] * B3[j][k];
                            }
                        }
                    }
                    
                    if(N <= 12){
                    // Imprimimos las matrices c1,c2,c3
                    System.out.println("Matriz C1:");
                    imprimirMatriz(C1);
                    System.out.println("\n Matriz C2:");
                    imprimirMatriz(C2);
                    System.out.println("\nMatriz C3:");
                    imprimirMatriz(C3);
                    }
                    
                    // Paso 5: Enviar las matrices C1, C2 y C3 al nodo 0
                    ObjectOutputStream salida = new ObjectOutputStream(conexion.getOutputStream());
                    salida.writeObject(C1);
                    salida.writeObject(C2);
                    salida.writeObject(C3);
                    
                    // Paso 6: Cerrar la conexión con el nodo 0
                    conexion.close();
                }
            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            
        }else if(nodo == 2){
            try{
                 // Paso 1: Crear el socket del servidor
                int puerto = 8002;
                ServerSocket servidor = new ServerSocket(puerto);
                System.out.println("Servidor en nodo 2 iniciado.");
                
                while(true){
                    // Paso 2: Esperar a que llegue una conexión del nodo 0
                    Socket conexion = servidor.accept();
                    System.out.println("Conexión establecida con el nodo 0.");
                    
                    // Paso 3: Recibir las matrices A1, B1, B2 y B3 del nodo 0
                    ObjectInputStream entrada = new ObjectInputStream(conexion.getInputStream());
                    double[][] A2 = (double[][]) entrada.readObject();
                    double[][] B1 = (double[][]) entrada.readObject();
                    double[][] B2 = (double[][]) entrada.readObject();
                    double[][] B3 = (double[][]) entrada.readObject();
                    
                     // Paso 4: Calcular las matrices C1, C2 y C3
                    int tamBloque = N/3;
                    double[][] C4 = new double[tamBloque][tamBloque];
                    double[][] C5 = new double[tamBloque][tamBloque];
                    double[][] C6 = new double[tamBloque][tamBloque];
                    for (int i = 0; i < tamBloque; i++) {
                        for (int j = 0; j < tamBloque; j++) {
                            for (int k = 0; k < N; k++) {
                                C4[i][j] += A2[i][k] * B1[j][k];
                                C5[i][j] += A2[i][k] * B2[j][k];
                                C6[i][j] += A2[i][k] * B3[j][k];
                            }
                        }
                    }
                    
                    if(N <= 12){
                    // Imprimimos las matrices c4,c5,c6
                    System.out.println("Matriz C4:");
                    imprimirMatriz(C4);
                    System.out.println("\n Matriz C5:");
                    imprimirMatriz(C5);
                    System.out.println("\nMatriz C6:");
                    imprimirMatriz(C6);
                    }
                    
                    // Paso 5: Enviar las matrices C1, C2 y C3 al nodo 0
                    ObjectOutputStream salida = new ObjectOutputStream(conexion.getOutputStream());
                    salida.writeObject(C4);
                    salida.writeObject(C5);
                    salida.writeObject(C6);
                    
                    // Paso 6: Cerrar la conexión con el nodo 0
                    conexion.close();
                }
            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            
        }else if(nodo == 3){
            try{
                 // Paso 1: Crear el socket del servidor
                int puerto = 8003;
                ServerSocket servidor = new ServerSocket(puerto);
                System.out.println("Servidor en nodo 3 iniciado.");
                
                while(true){
                    // Paso 2: Esperar a que llegue una conexión del nodo 0
                    Socket conexion = servidor.accept();
                    System.out.println("Conexión establecida con el nodo 0.");
                    
                    // Paso 3: Recibir las matrices A1, B1, B2 y B3 del nodo 0
                    ObjectInputStream entrada = new ObjectInputStream(conexion.getInputStream());
                    double[][] A3 = (double[][]) entrada.readObject();
                    double[][] B1 = (double[][]) entrada.readObject();
                    double[][] B2 = (double[][]) entrada.readObject();
                    double[][] B3 = (double[][]) entrada.readObject();
                    
                     // Paso 4: Calcular las matrices C1, C2 y C3
                    int tamBloque = N/3;
                    double[][] C7 = new double[tamBloque][tamBloque];
                    double[][] C8 = new double[tamBloque][tamBloque];
                    double[][] C9 = new double[tamBloque][tamBloque];
                    for (int i = 0; i < tamBloque; i++) {
                        for (int j = 0; j < tamBloque; j++) {
                            for (int k = 0; k < N; k++) {
                                C7[i][j] += A3[i][k] * B1[j][k];
                                C8[i][j] += A3[i][k] * B2[j][k];
                                C9[i][j] += A3[i][k] * B3[j][k];
                            }
                        }
                    }
                    
                    if(N <= 12){
                    // Imprimimos las matrices c7,c8,c9
                    System.out.println("Matriz C7:");
                    imprimirMatriz(C7);
                    System.out.println("\n Matriz C8:");
                    imprimirMatriz(C8);
                    System.out.println("\nMatriz C9:");
                    imprimirMatriz(C9);
                    }
                    
                    // Paso 5: Enviar las matrices C1, C2 y C3 al nodo 0
                    ObjectOutputStream salida = new ObjectOutputStream(conexion.getOutputStream());
                    salida.writeObject(C7);
                    salida.writeObject(C8);
                    salida.writeObject(C9);
                    
                    // Paso 6: Cerrar la conexión con el nodo 0
                    conexion.close();
                }
            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void imprimirMatriz(double[][] matriz){
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                System.out.print(matriz[i][j] + " ");
            }
        System.out.println();
        }
    }
}
