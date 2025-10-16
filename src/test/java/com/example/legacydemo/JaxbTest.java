package com.example.legacydemo;

import com.example.legacydemo.domain.XmlCustomer;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class JaxbTest {
  @Test void marshalXmlCustomer() throws Exception {
    JAXBContext ctx = JAXBContext.newInstance(XmlCustomer.class);
    Marshaller m = ctx.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    StringWriter sw = new StringWriter();
    m.marshal(new XmlCustomer(1L, "Alice", "alice@example.com"), sw);
    assertThat(sw.toString()).contains("<customer>");
  }
}
