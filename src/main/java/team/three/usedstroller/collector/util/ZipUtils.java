package team.three.usedstroller.collector.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ZipUtils {

	public static void unzipFile(File zipFile, String targetDir) throws IOException {
		InputStream inputStream = new FileInputStream(zipFile);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		byte[] buffer = new byte[1024];
		ZipEntry zipEntry = zipInputStream.getNextEntry();

		while (zipEntry != null) {
			String entryFileName = zipEntry.getName();
			File entryFile = new File(targetDir + File.separator + entryFileName);

			if (zipEntry.isDirectory()) {
				entryFile.mkdirs();
			} else {
				new File(entryFile.getParent()).mkdirs();
				try (OutputStream outputStream = new FileOutputStream(entryFile)) {
					int len;
					while ((len = zipInputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, len);
						// chmod +x chromedriver
						if (entryFileName.contains("chromedriver")) {
							entryFile.setExecutable(true);
						}
					}
				}
			}
			zipEntry = zipInputStream.getNextEntry();
		}
	}

}
