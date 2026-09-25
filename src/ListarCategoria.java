import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Ejercicio 4.c - Listar los equipos de una categoria de inventario.dat.
 * Recorre TODOS los registros sin saber de antemano cuantos hay
 * y muestra los que son de la categoria indicada (sin distinguir mayusculas).
 *
 * Uso: java ListarCategoria <categoria>
 * Ejemplo: java ListarCategoria periferico
 *
 * Estructura del registro (96 bytes):
 *   id (4) | codigo (16) | nombre (40) | categoria (24) | stock (4) | precio (8)
 */
public class ListarCategoria {

	static final int LONG_CODIGO = 8;
	static final int LONG_NOMBRE = 20;
	static final int LONG_CATEGORIA = 12;

	static final int TAM_REGISTRO = Integer.BYTES
			+ LONG_CODIGO * Character.BYTES
			+ LONG_NOMBRE * Character.BYTES
			+ LONG_CATEGORIA * Character.BYTES
			+ Integer.BYTES
			+ Double.BYTES;                                          // 96

	public static void main(String[] args) {
		// 1. Validar el argumento
		if (args.length != 1 || args[0].trim().isEmpty()) {
			System.out.println("Uso: java ListarCategoria <categoria>");
			return;
		}
		String categoriaBuscada = args[0].trim();

		File fichero = new File("inventario.dat");
		if (!fichero.exists()) {
			System.out.println("No existe el fichero " + fichero.getName()
					+ ". Ejecuta antes Ej4a_CrearInventario.");
			return;
		}

		int encontrados = 0;

		try (
				RandomAccessFile raf = new RandomAccessFile(fichero, "r")
			) {
			// 2. Calcular cuantos registros hay a partir del tamano del fichero
			long numRegistros = raf.length() / TAM_REGISTRO;

			// 3. Recorrer todos los registros, uno detras de otro
			for (long i = 0; i < numRegistros; i++) {
				raf.seek(i * TAM_REGISTRO);

				int id = raf.readInt();
				String codigo = leerTexto(raf, LONG_CODIGO);
				String nombre = leerTexto(raf, LONG_NOMBRE);
				String categoria = leerTexto(raf, LONG_CATEGORIA);
				int stock = raf.readInt();
				double precio = raf.readDouble();

				// un id 0 es un hueco vacio: no hay equipo guardado ahi
				if (id == 0) {
					continue;
				}

				// 4. Comparar sin distinguir mayusculas de minusculas
				if (categoria.equalsIgnoreCase(categoriaBuscada)) {
					if (encontrados == 0) {
						System.out.println("Equipos de la categoria \"" + categoriaBuscada + "\":");
						System.out.printf("%-4s %-8s %-20s %-12s %6s %10s%n",
								"ID", "CODIGO", "NOMBRE", "CATEGORIA", "STOCK", "PRECIO");
					}
					System.out.printf("%-4d %-8s %-20s %-12s %6d %10.2f%n",
							id, codigo, nombre, categoria, stock, precio);
					encontrados++;
				}
			}
		} catch (IOException e) {
			System.out.println("Error de entrada/salida: " + e.getMessage());
			return;
		}

		// 5. Resultado final
		if (encontrados == 0) {
			System.out.println("No hay ningun equipo de la categoria \"" + categoriaBuscada + "\".");
		} else {
			System.out.println("Total de equipos encontrados: " + encontrados);
		}
	}

	/**
	 * Lee 'longitud' caracteres y quita el relleno (caracteres 0) que puso setLength.
	 */
	static String leerTexto(RandomAccessFile raf, int longitud) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < longitud; i++) {
			char c = raf.readChar();
			if (c != '\0') {
				sb.append(c);
			}
		}
		return sb.toString().trim();
	}
}
