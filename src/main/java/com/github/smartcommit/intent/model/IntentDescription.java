package com.github.smartcommit.intent.model;

import org.omg.CORBA.UNKNOWN;

public enum IntentDescription {
  ADD("add"), CRT("create"), MAK("make"), IMP("implement"),
  FIX("fix"),
  RMV("remove"),
  UPD("update"), UPG("upgrade"),
  USE("use"),
  MOV("move"), CHG("change"),
  PRP("prepare"),
  IPV("improve"),
  IGN("ignore"),
  HDL("handle"),
  RNM("rename"),
  ALW("allow"),
  SET("set"),
  RVT("revert"),
  RPL("replace"),
  NFC("NoFileChange"),
  FIL("file"), DOC("doc");

  public String label;
  IntentDescription(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
  public void setLabel(String label) {
    this.label = label;
  }

}