package com.dotcms.rekognition.actionlet;


import com.dotcms.rekognition.api.RekognitionApi;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
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
import java.util.List;
import java.util.Map;

public class RekognitionActionlet extends WorkFlowActionlet {

  private static final long serialVersionUID = 1L;

  private final String TAGGED_BY_AWS = "TAGGED_BY_AWS";



  @Override
  public List<WorkflowActionletParameter> getParameters() {
    return null;
  }

  @Override
  public String getName() {
    return "Rekognition Actionlet";
  }

  @Override
  public String getHowTo() {
    return null;
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
      if (tags.contains(TAGGED_BY_AWS))
        return;

      List<String> awsTags = new RekognitionApi().detectLabels(image);
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
