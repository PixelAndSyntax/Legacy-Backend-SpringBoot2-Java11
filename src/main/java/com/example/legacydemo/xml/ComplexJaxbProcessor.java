package com.example.legacydemo.xml;

import org.springframework.stereotype.Component;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Complex JAXB integration with custom type adapters and marshallers.
 *
 * MIGRATION CHALLENGE: Migrating from javax.xml.bind to jakarta.xml.bind requires:
 * 1. Changing ALL imports from javax.xml.bind.* to jakarta.xml.bind.*
 * 2. Updating custom XmlAdapters to jakarta versions
 * 3. Testing all marshalling/unmarshalling logic as behavior may differ
 * 4. Updating JAXB runtime dependencies
 * 5. Checking if custom ValidationEventHandler logic still works
 */
@Component
public class ComplexJaxbProcessor {

    /**
     * Complex domain object with multiple custom adapters
     */
    @XmlRootElement(name = "transaction")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Transaction {

        private String id;

        @XmlJavaTypeAdapter(DateAdapter.class)
        private Date timestamp;

        @XmlJavaTypeAdapter(MoneyAdapter.class)
        private Double amount;

        @XmlJavaTypeAdapter(StatusAdapter.class)
        private TransactionStatus status;

        @XmlJavaTypeAdapter(MetadataAdapter.class)
        private Map<String, String> metadata;

        private List<TransactionItem> items;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public TransactionStatus getStatus() { return status; }
        public void setStatus(TransactionStatus status) { this.status = status; }

        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

        public List<TransactionItem> getItems() { return items; }
        public void setItems(List<TransactionItem> items) { this.items = items; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TransactionItem {
        private String code;
        private Integer quantity;
        private Double price;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }

    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    /**
     * Custom date adapter using javax.xml.bind - requires migration to jakarta
     */
    public static class DateAdapter extends XmlAdapter<String, Date> {
        private final SimpleDateFormat formatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        @Override
        public Date unmarshal(String v) throws Exception {
            return v == null ? null : formatter.parse(v);
        }

        @Override
        public String marshal(Date v) throws Exception {
            return v == null ? null : formatter.format(v);
        }
    }

    /**
     * Money adapter with complex formatting logic
     */
    public static class MoneyAdapter extends XmlAdapter<String, Double> {
        @Override
        public Double unmarshal(String v) throws Exception {
            if (v == null || v.isEmpty()) return null;
            // Remove currency symbols and parse
            return Double.parseDouble(v.replaceAll("[^0-9.]", ""));
        }

        @Override
        public String marshal(Double v) throws Exception {
            return v == null ? null : String.format("$%.2f", v);
        }
    }

    /**
     * Status adapter with validation
     */
    public static class StatusAdapter extends XmlAdapter<String, TransactionStatus> {
        @Override
        public TransactionStatus unmarshal(String v) throws Exception {
            return v == null ? null : TransactionStatus.valueOf(v.toUpperCase());
        }

        @Override
        public String marshal(TransactionStatus v) throws Exception {
            return v == null ? null : v.name();
        }
    }

    /**
     * Complex map adapter - requires special handling in migration
     */
    public static class MetadataAdapter extends XmlAdapter<MetadataWrapper, Map<String, String>> {
        @Override
        public Map<String, String> unmarshal(MetadataWrapper v) throws Exception {
            if (v == null || v.entries == null) return null;
            Map<String, String> map = new HashMap<>();
            for (MetadataEntry entry : v.entries) {
                map.put(entry.key, entry.value);
            }
            return map;
        }

        @Override
        public MetadataWrapper marshal(Map<String, String> v) throws Exception {
            if (v == null) return null;
            MetadataWrapper wrapper = new MetadataWrapper();
            wrapper.entries = new ArrayList<>();
            for (Map.Entry<String, String> entry : v.entrySet()) {
                MetadataEntry me = new MetadataEntry();
                me.key = entry.getKey();
                me.value = entry.getValue();
                wrapper.entries.add(me);
            }
            return wrapper;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MetadataWrapper {
        private List<MetadataEntry> entries;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MetadataEntry {
        private String key;
        private String value;
    }

    /**
     * Custom Marshaller with complex configuration using javax.xml.bind
     *
     * MIGRATION CHALLENGE: Marshaller properties and behavior may differ in jakarta
     */
    public String marshalWithCustomConfig(Transaction transaction) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Transaction.class);
        Marshaller marshaller = context.createMarshaller();

        // Complex marshaller configuration - javax.xml.bind specific
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);

        // Custom schema location
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
            "http://example.com/transaction http://example.com/transaction.xsd");

        // Custom validation event handler - may need migration
        marshaller.setEventHandler(new CustomValidationEventHandler());

        StringWriter writer = new StringWriter();
        marshaller.marshal(transaction, writer);
        return writer.toString();
    }

    /**
     * Custom unmarshaller with validation
     */
    public Transaction unmarshalWithValidation(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Transaction.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Set custom event handler for validation
        unmarshaller.setEventHandler(new CustomValidationEventHandler());

        // Set schema validation (complex setup)
        // Note: Schema factory might need updates for jakarta

        StringReader reader = new StringReader(xml);
        return (Transaction) unmarshaller.unmarshal(reader);
    }

    /**
     * Custom validation event handler using javax.xml.bind
     *
     * MIGRATION NOTE: Must migrate to jakarta.xml.bind.ValidationEventHandler
     */
    private static class CustomValidationEventHandler implements ValidationEventHandler {
        @Override
        public boolean handleEvent(ValidationEvent event) {
            // Complex validation logic
            if (event.getSeverity() == ValidationEvent.ERROR ||
                event.getSeverity() == ValidationEvent.FATAL_ERROR) {

                ValidationEventLocator locator = event.getLocator();
                System.err.println("Validation Error at line " +
                                 locator.getLineNumber() +
                                 ", column " + locator.getColumnNumber() +
                                 ": " + event.getMessage());
                return false; // Stop on errors
            }
            return true; // Continue on warnings
        }
    }

    /**
     * Complex marshalling with Marshaller.Listener callbacks
     */
    public String marshalWithCallbacks(Transaction transaction) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Transaction.class);
        Marshaller marshaller = context.createMarshaller();

        // Set custom marshaller listener - javax.xml.bind specific
        marshaller.setListener(new Marshaller.Listener() {
            @Override
            public void beforeMarshal(Object source) {
                System.out.println("Before marshalling: " + source.getClass().getSimpleName());
            }

            @Override
            public void afterMarshal(Object source) {
                System.out.println("After marshalling: " + source.getClass().getSimpleName());
            }
        });

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(transaction, writer);
        return writer.toString();
    }

    /**
     * Creates sample transaction with complex nested data
     */
    public Transaction createSampleTransaction() {
        Transaction tx = new Transaction();
        tx.setId("TX-" + UUID.randomUUID().toString());
        tx.setTimestamp(new Date());
        tx.setAmount(1234.56);
        tx.setStatus(TransactionStatus.APPROVED);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("customer", "John Doe");
        metadata.put("channel", "WEB");
        metadata.put("region", "US-EAST");
        tx.setMetadata(metadata);

        List<TransactionItem> items = new ArrayList<>();
        TransactionItem item1 = new TransactionItem();
        item1.setCode("ITEM-001");
        item1.setQuantity(2);
        item1.setPrice(500.00);
        items.add(item1);

        TransactionItem item2 = new TransactionItem();
        item2.setCode("ITEM-002");
        item2.setQuantity(1);
        item2.setPrice(234.56);
        items.add(item2);

        tx.setItems(items);

        return tx;
    }
}
