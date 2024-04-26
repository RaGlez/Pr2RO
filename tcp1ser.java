import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;


public class tcp1ser{

    public static void main (String [] args) throws IOException{

         // Validar argumentos
         if (args.length != 1) {
            System.out.println("Sintaxis incorrecta. Uso correcto: tcp1ser puerto");
        return;
        }
        System.out.println("Servidor activo");
        // Obtener puerto
        int puerto = Integer.parseInt(args[0]);

        // Crear acumulador
        long acumulador = 0;

        // Crear ServerSocket
        ServerSocket serverSocket = new ServerSocket(puerto);

        // Bucle de atención a clientes
        while (true) {

            // Esperar conexión de un cliente
            Socket socket = serverSocket.accept();

            // Crear streams de entrada y salida
            InputStream entrada = socket.getInputStream();
            OutputStream salida = socket.getOutputStream();

            // Bucle de comunicación con el cliente
            while (true) {
                try{

                // Obtener operación y operandos
                byte[] mensajeSinFiltrar = new byte [4]; 
            
                entrada.read(mensajeSinFiltrar);
                byte[] mensaje;
                if (mensajeSinFiltrar[0] == 6) {
                    mensaje = new byte[3];
                    mensaje[0] = mensajeSinFiltrar[0];
                    mensaje[1] = mensajeSinFiltrar[1];
                    mensaje[2] = mensajeSinFiltrar[2];
                } else {
                    mensaje = new byte[4];
                    mensaje = mensajeSinFiltrar;
                }
                int operacion = mensaje[0];
                int longitud = mensaje[1];
                int operando1 = mensaje[2];
            
                int operando2 = (longitud == 2) ? mensaje[3] : 0;

                // Realizar operación
                int resultado = 0;
                switch (operacion) {
                    case 1:
                        resultado = operando1 + operando2; 
                        System.out.println("Operacion recibida: "+operando1+"+"+operando2);
                        break;
                    case 2:
                        resultado = operando1 - operando2;
                        System.out.println("Operacion recibida: "+operando1+"-"+operando2);
                        break;
                    case 3:
                        resultado = operando1 * operando2;
                        System.out.println("Operacion recibida: "+operando1+"*"+operando2);
                        break;
                    case 4:
                        resultado = operando1 / operando2;
                        System.out.println("Operacion recibida: "+operando1+"/"+operando2);
                        break;
                    case 5:
                        resultado = operando1 % operando2;
                        System.out.println("Operacion recibida: "+operando1+"%"+operando2);
                        break;
                    case 6:
                        resultado = factorial(operando1);
                        System.out.println("Operacion recibida: "+operando1+"!");
                        break;
                }
                System.out.println("Resultado operacion: "+resultado);

                // Actualizar acumulador
                acumulador += resultado;
                System.out.println("Valor acumulador: "+acumulador);
            
                // Crear mensaje de respuesta
                byte[] respuesta = new byte[10];
                respuesta[0] = 16;
                respuesta[1] = 8;
                
                byte[] bufferAux = ByteBuffer.allocate(Long.BYTES).putLong(acumulador).array();
                System.arraycopy(bufferAux, 0, respuesta, 2, 8);
                // Enviar respuesta al cliente
                salida.write(respuesta);
                //System.out.println("Mensaje COD server"+Arrays.toString(respuesta));
            } catch (IOException e) {
                System.err.println("Cliente se ha desconectado");
                break; // Salir del bucle interno si hay un error
            }
            }

            // Cerrar socket del cliente
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("IOException: "+e.getMessage());
            }
        }
    }

    private static int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }


    

}