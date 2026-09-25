import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Ejercicio 4.a - Crear inventario.dat con RandomAccessFile.
 * Guarda los equipos de los arrays en un fichero binario de registros de longitud fija.
 *
 * Estructura de cada registro:
 *   id (int)            4 bytes
 *   codigo (8 chars)   16 bytes
 *   nombre (20 chars)  40 bytes
 *   categoria (12 chars) 24 bytes
 *   stock (int)         4 bytes
 *   precio (double)     8 bytes
 *   TOTAL              96 bytes
 */
public class Ej4a_CrearInventario {

	// longitud fija (en caracteres) de cada campo String
	static final int LONG_CODIGO = 8;
	static final int LONG_NOMBRE = 20;
	static final int LONG_CATEGORIA = 12;

	// tamano del registro en bytes: cada char ocupa 2 bytes (Character.BYTES)
	static final int TAM_REGISTRO = Integer.BYTES                 // id
			+ LONG_CODIGO * Character.BYTES                          // codigo
			+ LONG_NOMBRE * Character.BYTES                          // nombre
			+ LONG_CATEGORIA * Character.BYTES                       // categoria
			+ Integer.BYTES                                          // stock
			+ Double.BYTES;                                          // precio

	public static void main(String[] args) {

		// Datos iniciales (copiados de ArraysInventario.txt)
		int[] ids = {1, 2, 3, 4, 5, 6, 7, 8};

		String[] codigos = {
			"EQ0001", "EQ0002", "EQ0003", "EQ0004",
			"EQ0005", "EQ0006", "EQ0007", "EQ0008"
		};

		String[] nombres = {
			"Portatil Lenovo",
			"Monitor Dell 24",
			"Teclado Logitech",
			"Raton Inalambrico",
			"Webcam Logitech",
			"Proyector Epson",
			"Dock USB-C",
			"Auriculares Jabra"
		};

		String[] categorias = {
			"portatil",
			"monitor",
			"periferico",
			"periferico",
			"periferico",
			"proyector",
			"accesorio",
			"audio"
		};

		int[] stocks = {6, 12, 18, 25, 9, 4, 14, 11};

		double[] precios = {899.90, 189.95, 49.90, 24.50, 79.00, 549.99, 129.00, 159.90};

		// el numero de registros sale de los arrays, no se escribe a mano
		int numRegistros = ids.length;
		if (codigos.length != numRegistros || nombres.length != numRegistros
				|| categorias.length != numRegistros || stocks.length != numRegistros
				|| precios.length != numRegistros) {
			System.out.println("Error: los arrays no tienen todos la misma longitud.");
			return;
		}

		File fichero = new File("inventario.dat");
		// si ya existia, se borra para no dejar registros antiguos al final
		if (fichero.exists()) {
			fichero.delete();
		}

		try (
				RandomAccessFile raf = new RandomAccessFile(fichero, "rw")
			) {
			for (int i = 0; i < numRegistros; i++) {
				// el id coincide con la posicion logica: id 1 -> posicion 0, id 2 -> posicion 96...
				long posicion = (long) (ids[i] - 1) * TAM_REGISTRO;
				raf.seek(posicion);

				raf.writeInt(ids[i]);
				raf.writeChars(ajustar(codigos[i], LONG_CODIGO));
				raf.writeChars(ajustar(nombres[i], LONG_NOMBRE));
				raf.writeChars(ajustar(categorias[i], LONG_CATEGORIA));
				raf.writeInt(stocks[i]);
				raf.writeDouble(precios[i]);
			}

			System.out.println("Fichero creado: " + fichero.getName());
			System.out.println("Registros guardados: " + numRegistros);
			System.out.println("Tamano de cada registro: " + TAM_REGISTRO + " bytes");
			System.out.println("Tamano del fichero: " + raf.length() + " bytes");
		} catch (IOException e) {
			System.out.println("Error de entrada/salida: " + e.getMessage());
		}
	}

	/**
	 * Devuelve el texto con exactamente 'longitud' caracteres:
	 * si es mas corto lo rellena (con el caracter 0) y si es mas largo lo corta.
	 */
	static String ajustar(String texto, int longitud) {
		StringBuffer sb = new StringBuffer(texto);
		sb.setLength(longitud);
		return sb.toString();
	}
}
