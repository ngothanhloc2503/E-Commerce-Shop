package com.shopping.common.entity.order;

public enum OrderStatus {
    NEW {
        @Override
        public String defaultDescription() {
            return "Order was placed by the customer";
        }
    },
    CANCEL {
        @Override
        public String defaultDescription() {
            return "Order was rejected";
        }
    },
    PROCESSING {
        @Override
        public String defaultDescription() {
            return "Order is being processed";
        }
    },
    PACKAGED {
        @Override
        public String defaultDescription() {
            return "Products were packaged";
        }
    },
    PICKED {
        @Override
        public String defaultDescription() {
            return "Shipper picked the package";
        }
    },
    SHIPPING {
        @Override
        public String defaultDescription() {
            return "Shipper is delivering the package";
        }
    },
    DELIVERED {
        @Override
        public String defaultDescription() {
            return "Customer received products";
        }
    },
    RETURNED {
        @Override
        public String defaultDescription() {
            return "Products were returned";
        }
    },
    RETURN_REQUESTED {
        @Override
        public String defaultDescription() {
            return "Customer send request to return purchase";
        }
    },
    PAID {
        @Override
        public String defaultDescription() {
            return "Customer has paid this order";
        }
    },
    REFUNDED {
        @Override
        public String defaultDescription() {
            return "Customer has been refunded";
        }
    };

    public abstract String defaultDescription();
}
