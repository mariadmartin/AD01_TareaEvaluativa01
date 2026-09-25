import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Ejercicio 4.b - Modificar directamente el stock de un equipo en inventario.dat.
 * Va directo al registro con seek(), sin recorrer los anteriores, y cambia SOLO el campo stock.
 *
 * Uso: java ActualizarStock <id> <nuevo_stock>
 * Ejemplo: java ActualizarStock 3 17
 *
 * Estructura del registro (96 bytes):
 *   id (4) | codigo (16) | nombre (40) | categoria (24) | stock (4) | precio (8)
 */
public class ActualizarStock {

	static final int LONG_CODIGO = 8;
	static final int LONG_NOMBRE = 20;
	static final int LONG_CATEGORIA = 12;

	static final int TAM_REGISTRO = Integer.BYTES
			+ LONG_CODIGO * Character.BYTES
			+ LONG_NOMBRE * Character.BYTES
			+ LONG_CATEGORIA * Character.BYTES
			+ Integer.BYTES
			+ Double.BYTES;                                          // 96

	// desplazamientos (en bytes) dentro del registro
	static final int DESP_NOMBRE = Integer.BYTES + LONG_CODIGO * Character.BYTES;       // 4 + 16 = 20
	static final int DESP_STOCK = DESP_NOMBRE + LONG_NOMBRE * Character.BYTES
			+ LONG_CATEGORIA * Character.BYTES;                                          // 20 + 40 + 24 = 84

	public static void main(String[] args) {
		// 1. Validar los argumentos
		if (args.length != 2) {
			System.out.println("Uso: java ActualizarStock <id> <nuevo_stock>");
			return;
		}

		int id;
		int nuevoStock;
		try {
			id = Integer.parseInt(args[0]);
			nuevoStock = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("El id y el stock deben ser numeros enteros.");
			return;
		}

		if (id < 1) {
			System.out.println("El id debe ser mayor o igual que 1.");
			return;
		}
		if (nuevoStock < 0) {
			System.out.println("El stock no puede ser negativo.");
			return;
		}

		File fichero = new File("inventario.dat");
		if (!fichero.exists()) {
			System.out.println("No existe el fichero " + fichero.getName()
					+ ". Ejecuta antes Ej4a_CrearInventario.");
			return;
		}

		try (
				RandomAccessFile raf = new RandomAccessFile(fichero, "rw")
			) {
			// 2. Comprobar que el registro existe
			long numRegistros = raf.length() / TAM_REGISTRO;
			if (id > numRegistros) {
				System.out.println("No existe ningun equipo con id " + id
						+ " (el fichero tiene " + numRegistros + " registros).");
				return;
			}

			// 3. Ir directamente al principio del registro
			long posicion = (long) (id - 1) * TAM_REGISTRO;
			raf.seek(posicion);
			int idLeido = raf.readInt();
			if (idLeido != id) {
				System.out.println("El registro con id " + id + " esta vacio o no es valido.");
				return;
			}

			// 4. Leer el nombre (saltando el codigo)
			raf.seek(posicion + DESP_NOMBRE);
			String nombre = leerTexto(raf, LONG_NOMBRE);

			// 5. Leer el stock anterior
			raf.seek(posicion + DESP_STOCK);
			int stockAnterior = raf.readInt();

			// 6. Escribir el nuevo stock: hay que volver atras, porque readInt avanzo 4 bytes
			raf.seek(posicion + DESP_STOCK);
			raf.writeInt(nuevoStock);

			int variacion = nuevoStock - stockAnterior;
			System.out.println("Equipo: " + nombre + " (id " + id + ")");
			System.out.println("Stock anterior: " + stockAnterior);
			System.out.println("Stock nuevo: " + nuevoStock);
			System.out.println("Variacion: " + (variacion > 0 ? "+" : "") + variacion);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida: " + e.getMessage());
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
