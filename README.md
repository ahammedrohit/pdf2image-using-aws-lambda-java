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

PS : If the text is not recognized correctly for Japanese, here's a workaround.

1. Create the jar file.
2. inside the jar file navigate here : \org\apache\pdfbox\resources\ttf
3. Replace the font file(LiberationSans-Regular.ttf) with the one you want to use. (I used IPAexGothic.ttf) But remember to rename it to LiberationSans-Regular.ttf
4. Upload the jar file to the lambda function.