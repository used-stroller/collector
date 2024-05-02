package team.three.usedstroller.collector.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Data;

@Data
public class ImageDownloader {

  public static void main(String[] args) {

    String imageUrl = "https://shopping-phinf.pstatic.net/main_4715053/47150530258.jpg";
    String fileName = "boogabu_1.jpg";
    String destinationFile = "C:/stroller_img/boogabu/" + fileName;

    convertToFile(imageUrl, destinationFile);
  }

  private static void convertToFile(String imageUrl, String destinationFile) {
    try {
      URL url = new URL(imageUrl);
      InputStream inputStream = url.openStream();
      BufferedInputStream bis = new BufferedInputStream(inputStream);
      OutputStream outputStream = new FileOutputStream(destinationFile);
      BufferedOutputStream bos = new BufferedOutputStream(outputStream);

      byte[] buffer = new byte[2048];
      int length;

      while ((length = bis.read(buffer)) != -1) {
        bos.write(buffer, 0, length);
      }
      inputStream.close();
      outputStream.close();

    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
