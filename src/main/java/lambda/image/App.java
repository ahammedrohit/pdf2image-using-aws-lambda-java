package lambda.image;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class App implements RequestHandler<Map<String, Object>, String> {
  public static void main(String[] args) {
  }

  @Override
  public String handleRequest(Map<String, Object> input, Context context) {
    context.getLogger().log("Input: " + input);

    try {
      final String pdfBucket = ""; // Your bucket name containing the PDF file
      final String imageBucket = ""; // Bucket where you want to upload the image

      Object records = input.get("detail");
      String pdfPath = getValue(records, "object", "key");

      createImage(pdfBucket, imageBucket, pdfPath);
    } catch (Throwable e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return "OK";
  }

  public void createImage(String pdfBucket, String imageBucket, String path) throws IOException {
    try (PDDocument doc = PDDocument.load(getObject(pdfBucket, path))) {

      PDFRenderer pdfRenderer = new PDFRenderer(doc);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      BufferedOutputStream os = new BufferedOutputStream(bos);
      BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
      ImageIO.write(image, "JPG", os);

      upload(imageBucket, getImagePath(path, 1), bos.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("error");
      throw e;
    }
  }


  public String getValue(Object obj, String... keys) {
    if (obj instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) obj;
      for (String key : keys) {
        Object value = map.get(key);
        if (value == null) {
          return null;
        } else if (value instanceof Map) {
          map = (Map<?, ?>) value;
        } else {
          return value.toString();
        }
      }
    }
    return null;
  }


  public InputStream getObject(String bucket, String path) {

    AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    final GetObjectRequest getRequest = new GetObjectRequest(bucket, path);

    S3Object object = s3.getObject(getRequest);

    return object.getObjectContent();
  }

  public void upload(String bucket, String path, byte[] content) throws IOException {

    AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    ObjectMetadata om = new ObjectMetadata();
    om.setContentLength(content.length);

    try (InputStream is = new ByteArrayInputStream(content)) {
      final PutObjectRequest put = new PutObjectRequest(bucket, path, is, om);

      s3.putObject(put);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("error");
      throw e;
    }
  }

  public String getImagePath(String path, int page) {

    return "image/" + path.replace("pdf", "jpg");
  }
}