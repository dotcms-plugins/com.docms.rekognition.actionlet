package com.dotcms.rekognition.api;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.dotcms.rekognition.util.AWSPropertyBundle;
import com.dotmarketing.business.DotStateException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RekognitionApi {


  private final AWSCredentials awsCredentials;
  private final AmazonRekognitionClient client;
  private final float minConfidence;
  private final int maxLabels;

  public RekognitionApi() {

    this.awsCredentials = credentials();
    this.client = new AmazonRekognitionClient(this.awsCredentials);
    this.maxLabels = Integer.parseInt(AWSPropertyBundle.getProperty("max.labels", "15"));
    this.minConfidence = Float.parseFloat(AWSPropertyBundle.getProperty("min.confidence", "75F"));

  }



  public List<String> detectLabels(File file) {
    try {
      return _detectLabels(file);
    } catch (Exception e) {
      throw new DotStateException(e.getMessage(), e);
    }

  }


  private List<String> _detectLabels(File file) throws IOException {



    try (RandomAccessFile aFile = new RandomAccessFile(file.getAbsolutePath(), "r")) {

      FileChannel inChannel = aFile.getChannel();
      MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
      buffer.load();
      Image image = new Image().withBytes(buffer);

      DetectLabelsRequest request =
          new DetectLabelsRequest().withImage(image).withMaxLabels(maxLabels).withMinConfidence(minConfidence);

      DetectLabelsResult result = client.detectLabels(request);
      buffer.clear();


      List<Label> awsLabels = result.getLabels();

      List<String> labels = new ArrayList<>();

      for (Label l : awsLabels) {

        labels.add(l.getName());

      }

      return labels;
    }


  }



  private AWSCredentials credentials() {


    String key = AWSPropertyBundle.getProperty("aws.key");

    String secret = AWSPropertyBundle.getProperty("aws.secret");



    return new BasicAWSCredentials(key, secret);
  }


}
