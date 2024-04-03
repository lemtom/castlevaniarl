package sz.util;

import java.io.*;

public class FileUtil {

	public static int filasEnArchivo(String pArchivo) throws IOException {
		Debug.enterMethod("", "FileUtil.filasEnArchivo", pArchivo);
		File vArchivo = new File(pArchivo);
		int lines = 0;
		try (BufferedReader inx = new BufferedReader(new FileReader(vArchivo))) {
			while (inx.readLine() != null) {
				lines++;
			}
		}
		Debug.exitMethod(lines + "");
		return lines;
	}

	public static void deleteFile(String what) {
		new File(what).delete();
	}

	public static void copyFile(File origen, File destino) throws Exception {
		try (FileInputStream fis = new FileInputStream(origen); FileOutputStream fos = new FileOutputStream(destino)) {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}
	}

	public static BufferedReader getReader(String fileName) throws IOException {
		Debug.enterStaticMethod("FileUtil", "getReader");
		BufferedReader x = new BufferedReader(new FileReader(fileName));
		Debug.exitMethod(x);
		return x;
	}

	public static BufferedWriter getWriter(String fileName) throws IOException {
		Debug.enterStaticMethod("FileUtil", "getWriter");
		BufferedWriter x = new BufferedWriter(new FileWriter(fileName));
		Debug.exitMethod(x);
		return x;
	}

	public static boolean fileExists(String filename) {
		return new File(filename).exists();
	}

}