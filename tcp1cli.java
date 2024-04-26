import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class tcp1cli {
    public  static void main(String[] args) throws IOException {

        //Se comprueba si los argumentos de entrada son los correctos
        if (args.length != 2) {
            System.out.println("Sintaxis incorrecta. Uso correcto: tcp1cli <direccion_ip> <puerto>");
            System.out.println("Ejemplo: tcp1cli 127.0.0.1 12345");
            return;
        }

        //Se obtiene la dirección del servidor a partir del argumento indicado por línea de comandos
        InetAddress serverAddress = InetAddress.getByName(args[0]);

     

         // Se obtine el puerot del servidor por la linea de comandos
         int serverPort = Integer.parseInt(args[1]);

          //Creamos el Soket 
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress(serverAddress, serverPort),15000);
            socket.setSoTimeout(15000);

             // Crear streams de entrada y salida
        InputStream entrada = socket.getInputStream();
        OutputStream salida = socket.getOutputStream();

        //Bucle de comunicacion del servidor y puerto
        while(true){
            
            // Solicitar operación al usuario
            System.out.print("Introduzca operacion o QUIT: ");
            String operacionLinea = new BufferedReader(new InputStreamReader(System.in)).readLine();

            // Salir si el usuario introduce QUIT
            if ("QUIT".equals(operacionLinea)) {
                
                break;
            }

            //Dividimos por partes la operacion
            String[] partes = operacionLinea.split("[\\+\\-\\*\\/\\%\\!]");
            
            Arrays.stream(partes).filter(part -> !part.isEmpty()).toArray(String[]::new);
        

            int operando1 = Integer.parseInt(partes[0]);
            int operando2 = (partes.length == 2) ? Integer.parseInt(partes[1]) : 0;


            int simbolo;
            int longitud;
                
                if(operacionLinea.contains("+")){
                    simbolo = 1;
                   longitud = 2;
                } else if(operacionLinea.contains("-")){
                    simbolo = 2;
                    longitud = 2;
                } else if(operacionLinea.contains("*")){
                    simbolo = 3;
                    longitud = 2;
                } else if(operacionLinea.contains("/")){
                    simbolo = 4;
                    longitud = 2;
                } else if(operacionLinea.contains("%")){
                    simbolo = 5;
                    longitud = 2;
                } else {
                    simbolo = 6;
                    longitud = 1;
                }
                
                // Crear mensaje TLV
                byte[] mensaje = new byte[2 + longitud];
                
                
                mensaje[0] = (byte) simbolo;
                
                mensaje[1] = (byte) longitud;
                
                mensaje[2] = (byte) operando1;
                
                if(longitud == 2){
                    mensaje[3] = (byte) operando2;
                }
               

                // Enviar mensaje al servidor
                salida.write(mensaje);

                //System.out.println("Mensaje COD"+ Arrays.toString(mensaje));
                
                // Recibir respuesta del servidor
                
                byte[] respuesta = new byte[10];
                entrada.read(respuesta);
                
                

 
                // Validar respuesta
                if (respuesta[0] != 16 || respuesta[1] != 8) {
                     System.err.println("Respuesta del servidor no valida");
                     continue;
                }
 
                 // Obtener valor del acumulador
                
                long acumulador = 0;
                for (int i = 2; i < 10; i++) {
                    acumulador = (acumulador << 8) | (respuesta[i] & 0xFFL); 
            
                }
 
                // Mostrar valor del acumulador
                System.out.println("Valor del acumulador: " + acumulador);


            
              
          }
        }catch (SocketTimeoutException e) {
            System.out.println("Han pasado los 15 segundos del TimeOut");
        } 

       



        }






    }
