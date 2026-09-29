package main.tickets;

import main.enums.Value;
import main.enums.Demand;

/**
 * Represents a Feature Request ticket.
 * This class is immutable and cannot be extended.
 */
public final class FeatureRequest extends Ticket {
    private final Value businessValue;
    private final Demand customerDemand;

    private FeatureRequest(final FeatureRequestBuilder builder) {
        super(builder);
        this.businessValue = builder.businessValue;
        this.customerDemand = builder.customerDemand;
    }

    @Override
    public int toSeniorityLevel() {
        return 1;
    }

    /**
     * Retrieves the business value associated with the feature.
     *
     * @return The business value enum.
     */
    public Value getBusinessValue() {
        return businessValue;
    }

    /**
     * Retrieves the customer demand level.
     *
     * @return The customer demand enum.
     */
    public Demand getCustomerDemand() {
        return customerDemand;
    }

    @Override
    public String toString() {
        return "FeatureRequest{"
                + "businessValue=" + businessValue
                + ", customerDemand=" + customerDemand
                + ", " + super.toString()
                + '}';
    }

    /**
     * Builder class for constructing FeatureRequest instances.
     */
    public static final class FeatureRequestBuilder
            extends TicketBuilder<FeatureRequestBuilder> {
        private Value businessValue;
        private Demand customerDemand;

        /**
         * Default constructor initializing type to FEATURE_REQUEST.
         */
        public FeatureRequestBuilder() {
            this.type = "FEATURE_REQUEST";
        }

        /**
         * Sets the business value.
         *
         * @param value The business value enum.
         * @return The builder instance.
         */
        public FeatureRequestBuilder setBusinessValue(final Value value) {
            this.businessValue = value;
            return self();
        }

        /**
         * Sets the business value from a string.
         *
         * @param valueStr The business value string.
         * @return The builder instance.
         */
        public FeatureRequestBuilder setBusinessValue(final String valueStr) {
            this.businessValue = Value.valueOf(valueStr);
            return self();
        }

        /**
         * Sets the customer demand.
         *
         * @param demand The customer demand enum.
         * @return The builder instance.
         */
        public FeatureRequestBuilder setCustomerDemand(final Demand demand) {
            this.customerDemand = demand;
            return self();
        }

        /**
         * Sets the customer demand from a string.
         *
         * @param demandStr The customer demand string.
         * @return The builder instance.
         */
        public FeatureRequestBuilder setCustomerDemand(final String demandStr) {
            this.customerDemand = Demand.valueOf(demandStr);
            return self();
        }

        @Override
        protected FeatureRequestBuilder self() {
            return this;
        }

        @Override
        public FeatureRequest build() {
            return new FeatureRequest(this);
        }
    }
}
