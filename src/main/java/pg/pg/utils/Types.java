package pg.pg.utils;

public class Types {

    public enum PrefixType {

        ARTICLESKU,VARIANTSKU,LOCATION,BUILDING ,FLOOR,ROOM,BED}

    public enum Status {
        ACTIVE, INACTIVE
    }

    public enum OrderStatus {
        NEW,
        CONFIRMED,
        PACKED,
        PAID,
        INVOICED,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED,
        ABANDONED,
    }

    public enum ReturnStatus{
        PENDING,
        CONFIRMED,
        CLOSED
    }

    public enum Media {
        IMAGE, VIDEO
    }

    public enum Uom {
        SALES, PURCHASE
    }

    public enum ApproveStatus {
        DRAFT, APPROVED
    }

    public enum CartType {
        SAMPLE, WHOLESALE, COMBO, SWATCH
    }

    public enum PaymentStatus {
        DUE, PAID, UNPAID
    }


    public enum PickListStatus{
        PENDING,
        CONFIRMED,
        PICKED
    }

}