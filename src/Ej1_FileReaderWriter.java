import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Ejercicio 1 - FileReader y FileWriter.
 * Lee entrada.txt caracter a caracter y genera salida.txt aplicando:
 *  - las letras se pasan a mayusculas,
 *  - cada digito del 0 al 9 se sustituye por '#',
 *  - el resto de caracteres (espacios, saltos de linea...) se conservan.
 */
public class Ej1_FileReaderWriter {

	public static void main(String[] args) {
		// Rutas relativas: se resuelven desde la carpeta raiz del proyecto
		File entrada = new File("entrada.txt");
		File salida = new File("salida.txt");

		if (!entrada.exists()) {
			System.out.println("No se encuentra el fichero de entrada: " + entrada.getAbsolutePath());
			return;
		}

		int totalCaracteres = 0;
		int digitosSustituidos = 0;

		// try-with-resources: el lector y el escritor se cierran solos al terminar,
		// tanto si todo va bien como si salta una excepcion
		try (FileReader lector = new FileReader(entrada);
			 FileWriter escritor = new FileWriter(salida)) {

			int c;
			// read() devuelve el caracter leido como int, o -1 al llegar al fin de fichero
			while ((c = lector.read()) != -1) {
				char caracter = (char) c;

				if (caracter >= '0' && caracter <= '9') {
					escritor.write('#');
					digitosSustituidos++;
				} else {
					// toUpperCase solo cambia las letras; el resto lo deja igual
					escritor.write(Character.toUpperCase(caracter));
				}
				totalCaracteres++;
			}

			System.out.println("Fichero generado: " + salida.getName());
			System.out.println("Caracteres procesados: " + totalCaracteres);
			System.out.println("Digitos sustituidos por #: " + digitosSustituidos);

		} catch (IOException e) {
			System.out.println("Error de entrada/salida: " + e.getMessage());
		}
	}
}
