package com.dotcms.rekognition.actionlet;


import com.dotcms.rekognition.api.RekognitionApi;
import com.dotcms.rekognition.util.AWSPropertyBundle;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.image.filter.ResizeImageFilter;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.workflows.actionlet.WorkFlowActionlet;
import com.dotmarketing.portlets.workflows.model.WorkflowActionClassParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowActionFailureException;
import com.dotmarketing.portlets.workflows.model.WorkflowActionletParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowProcessor;
import com.dotmarketing.tag.business.TagAPI;
import com.dotmarketing.tag.model.Tag;
import com.dotmarketing.util.UtilMethods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RekognitionActionlet extends WorkFlowActionlet {

  private static final long serialVersionUID = 1L;

  private final String TAGGED_BY_AWS = "TAGGED_BY_AWS";



  @Override
  public List<WorkflowActionletParameter> getParameters() {
      List<WorkflowActionletParameter> params = new ArrayList<WorkflowActionletParameter>();

      params.add(new WorkflowActionletParameter("maxLabels", "Max Labels", AWSPropertyBundle.getProperty("max.labels", "15"), true));
      params.add(new WorkflowActionletParameter("minConfidence", "Minimum Confidence (percent)", AWSPropertyBundle.getProperty("min.confidence", "75"), true));
      return params;
  }

  @Override
  public String getName() {
    return "Auto Tag Images - AWS";
  }

  @Override
  public String getHowTo() {
    return "Max Labels is the maximum number of labels you are looking to return and Minimum Confidence is the minimum confidence level you will accept as valid tags";
  }



  @Override
  public void executeAction(WorkflowProcessor processor, Map<String, WorkflowActionClassParameter> params)
      throws WorkflowActionFailureException {


    Contentlet con = processor.getContentlet();

    Field tagField = null;
    File image = null;
    TagAPI tapi = APILocator.getTagAPI();

    for (Field f : con.getStructure().getFields()) {
      if (f.getFieldType().equals(Field.FieldType.TAG.toString())) {
        tagField = f;
      }
    }
    if (tagField == null) {
      return;
    }


    for (Field f : con.getStructure().getFields()) {
      if ("binary".equals(f.getFieldType())) {
        try {
          image = con.getBinary(f.getVelocityVarName());
    
          if (UtilMethods.isImage(image.getAbsolutePath())) {
          } else {
            return;
          }
        } catch (IOException e) {
          return;
        }
      }

    }
    


    try {
      List<Tag> tags = tapi.getTagsByInode(con.getInode());
      if (tags.contains(TAGGED_BY_AWS)){
        return;
      }
      
      String min = params.get("minConfidence").getValue();
      
      
      float minConfidence = Float.parseFloat(min );
      int maxLabels = Integer.parseInt(params.get("maxLabels").getValue());
      


      if(image.length() > 5242879){
        Map<String, String[]> args = new HashMap<>();
        args.put("resize_w", new String[]{"1000"});
        image =  new ResizeImageFilter().runFilter(image, args);
      }
      
      
      List<String> awsTags = new RekognitionApi().detectLabels(image, maxLabels, minConfidence);
      
      
      
      
      awsTags.add(TAGGED_BY_AWS);
      for (String tag : awsTags) {
        tapi.addContentletTagInode(tag, con.getInode(), con.getHost(), tagField.getVelocityVarName());
      }

      APILocator.getContentletAPI().refresh(con);

    } catch (DotDataException | DotSecurityException e) {
      throw new WorkflowActionFailureException(e.getMessage(), e);
    }



  }

}
