package com.example.legacydemo.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCustomer {
  @XmlElement
  private Long id;
  @XmlElement
  private String name;
  @XmlElement
  private String email;

  public XmlCustomer() {}
  public XmlCustomer(Long id, String name, String email) {
    this.id = id; this.name = name; this.email = email;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
