import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Ejercicio 3 - InputStream y firma de un fichero.
 * Recibe la ruta de un fichero como argumento y lee SOLO sus 4 primeros bytes
 * para comprobar si coinciden con la firma de un ZIP: 80, 75, 3, 4 ("PK\3\4").
 * Nota: que la cabecera coincida no demuestra que todo el fichero sea un ZIP valido.
 *
 * Uso: java Ej3_FirmaZip firmas/zip_real_valido.zip
 */
public class Ej3_FirmaZip {

	private static final int[] FIRMA_ZIP = {80, 75, 3, 4};

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Uso: java Ej3_FirmaZip <ruta_del_fichero>");
			return;
		}

		File fichero = new File(args[0]);
		if (!fichero.exists()) {
			System.out.println("No existe el fichero: " + fichero.getPath());
			return;
		}
		if (!fichero.isFile()) {
			System.out.println("La ruta no es un fichero (es un directorio): " + fichero.getPath());
			return;
		}

		byte[] cabecera = new byte[FIRMA_ZIP.length];
		int leidos = 0;

		try (
				InputStream entrada = new FileInputStream(fichero)
			) {
			// read(array, desde, cuantos) puede devolver menos bytes de los pedidos,
			// asi que se repite hasta tener los 4 o llegar al fin de fichero (-1)
			while (leidos < cabecera.length) {
				int n = entrada.read(cabecera, leidos, cabecera.length - leidos);
				if (n == -1) {
					break;
				}
				leidos += n;
			}
		} catch (IOException e) {
			System.out.println("Error de entrada/salida: " + e.getMessage());
			return;
		}

		System.out.println("Fichero: " + fichero.getPath());
		System.out.println("Bytes leidos: " + leidos);

		if (leidos < FIRMA_ZIP.length) {
			System.out.println("El fichero tiene menos de " + FIRMA_ZIP.length
					+ " bytes: no se puede comprobar la firma.");
			return;
		}

		boolean coincide = true;
		StringBuilder bytesLeidos = new StringBuilder();
		for (int i = 0; i < FIRMA_ZIP.length; i++) {
			// en Java el byte tiene signo (-128..127); & 0xFF lo pasa a 0..255
			int valor = cabecera[i] & 0xFF;
			bytesLeidos.append(valor).append(" ");
			if (valor != FIRMA_ZIP[i]) {
				coincide = false;
			}
		}
		System.out.println("Cabecera: " + bytesLeidos.toString().trim());

		if (coincide) {
			System.out.println("La cabecera es compatible con un fichero ZIP.");
		} else {
			System.out.println("La cabecera no corresponde a la firma esperada de un fichero ZIP.");
		}
	}
}
