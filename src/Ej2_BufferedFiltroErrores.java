import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Ejercicio 2 - BufferedReader y BufferedWriter.
 * Lee accesos.log linea a linea (formato fecha;usuario;resultado) y genera
 * errores.log solo con las lineas cuyo resultado es ERROR.
 * Al final de errores.log se anade una linea con el total de errores.
 */
public class Ej2_BufferedFiltroErrores {

	public static void main(String[] args) {
		File entrada = new File("accesos.log");
		File salida = new File("errores.log");

		if (!entrada.exists()) {
			System.out.println("No se encuentra el fichero de entrada: " + entrada.getAbsolutePath());
			return;
		}

		int lineasLeidas = 0;
		int totalErrores = 0;
		int lineasIncorrectas = 0;

		try (
				BufferedReader lector = new BufferedReader(new FileReader(entrada));
				BufferedWriter escritor = new BufferedWriter(new FileWriter(salida))
			) {

			String linea;
			// readLine() devuelve la linea sin el salto de linea, o null al llegar al fin de fichero
			while ((linea = lector.readLine()) != null) {
				lineasLeidas++;

				if (linea.isBlank()) {
					continue; // las lineas vacias se ignoran
				}

				String[] campos = linea.split(";");
				if (campos.length != 3) {
					lineasIncorrectas++;
					System.out.println("Linea " + lineasLeidas + " con formato incorrecto, se ignora: " + linea);
					continue;
				}

				String resultado = campos[2].trim();
				if (resultado.equals("ERROR")) {
					escritor.write(linea);
					escritor.newLine(); // salto de linea correcto segun el sistema operativo
					totalErrores++;
				}
			}

			escritor.write("Total de errores: " + totalErrores);
			escritor.newLine();

			System.out.println("Fichero generado: " + salida.getName());
			System.out.println("Lineas leidas: " + lineasLeidas);
			System.out.println("Errores encontrados: " + totalErrores);
			if (lineasIncorrectas > 0) {
				System.out.println("Lineas con formato incorrecto: " + lineasIncorrectas);
			}

		} catch (IOException e) {
			System.out.println("Error de entrada/salida: " + e.getMessage());
		}
	}
}
