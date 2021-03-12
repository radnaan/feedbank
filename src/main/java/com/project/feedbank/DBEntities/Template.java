package com.project.feedbank.DBEntities;

import java.io.Serializable;

public class Template implements Serializable {
    int templateid;
    String templatename;
    boolean editable;

    public Template(){
        templateid = 0;
        editable = true;
        templatename = "";
    }


    public Template(int id, String name, boolean edit){
        templateid = id;
        editable = edit;
        templatename = name;
    }

    public int getTemplateid(){
        return templateid;
    }
    public String getTemplatename(){
        return templatename;
    }

    public boolean getEditable(){
        return editable;
    }

    public void setTemplateid(int tid){
        templateid = tid;
    }
    public void setTemplatename(String tname){
        templatename = tname;
    }

    public void setEditable(boolean edit){
        editable= edit;
    }
}
