package shop.ink3.api.order.shipment.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class ShipmentNotFoundException extends NotFoundException {
    public ShipmentNotFoundException() {
        super("Available Shipment not found.");
    }

    public ShipmentNotFoundException(long shipmentId) {
        super("Shipment not found. ID: %d".formatted(shipmentId));
    }

}
