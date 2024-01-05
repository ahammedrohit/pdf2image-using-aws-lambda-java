# pdf2image-using-aws-lambda-java

A aws lambda based on Java 21 to generate image from pdf file.

You need two buckets, one for the pdf file, one for the image file.

Replace the pdfBucket and imageBucket with your own bucket names in the App.java.

Create a lambda function with java 21 runtime, and upload the jar file.

Give the S3 read and write permission to the lambda function.

You can use the following command to generate the jar file.

mvn clean package shade:shade

Jar file will be generated in the target folder.

Test the lambda function with the following json:

```json
{
  "detail": {
    "object": {
      "key": "Name of the pdf file stored in the pdfBucket.pdf"
    }
  }
}
```

## 構成 Lambda
  * PDF請求書ファイルのサムネイル画像を作成
    * Apache PDFBox-2.0.27 is used for PDF to Image conversion. But sometimes it doesn't recognize the Japanese texts in PDF. 
    * So, pdfbox-2.0.27.jar is modified as such : 
      * The fallback font inside the jar is changed from 'LiberationSans-Regular' to 'IPAexGothic' 
      * The font is located in org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf
      * ipaexg.ttf(IPAexGothic) is renamed to LiberationSans-Regular.ttf
      * The modified jar is located in libs/pdfbox-2.0.27-with-ipaexg.jar and loaded in maven as system scope dependency.
