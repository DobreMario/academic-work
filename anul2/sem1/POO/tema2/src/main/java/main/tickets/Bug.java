package main.tickets;

import main.enums.Frequency;
import main.enums.Severity;
import main.enums.Priority;

/**
 * Represents a Bug ticket.
 * This class is immutable and cannot be extended.
 */
public final class Bug extends Ticket {
    private final String expectedBehavior;
    private final String actualBehavior;
    private final Frequency frequency;
    private final Severity severity;
    private final String environment;
    private final Integer errorCode;

    private Bug(final BugBuilder builder) {
        super(builder);
        this.expectedBehavior = builder.expectedBehavior;
        this.actualBehavior = builder.actualBehavior;
        this.frequency = builder.frequency;
        this.severity = builder.severity;
        this.environment = builder.environment;
        this.errorCode = builder.errorCode;
    }

    @Override
    public int toSeniorityLevel() {
        return 0;
    }

    /**
     * Gets the expected behavior description.
     *
     * @return The expected behavior string.
     */
    public String getExpectedBehavior() {
        return expectedBehavior;
    }

    /**
     * Gets the actual behavior description.
     *
     * @return The actual behavior string.
     */
    public String getActualBehavior() {
        return actualBehavior;
    }

    /**
     * Gets the frequency of the bug.
     *
     * @return The frequency enum.
     */
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * Gets the severity of the bug.
     *
     * @return The severity enum.
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Gets the environment where the bug occurs.
     *
     * @return The environment string.
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Gets the error code associated with the bug.
     *
     * @return The error code integer.
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "Bug{"
                + "id=" + id
                + ", type='" + type + '\''
                + ", title='" + title + '\''
                + ", businessPriority=" + businessPriority
                + ", status=" + status
                + ", expertiseArea=" + expertiseArea
                + ", description='" + description + '\''
                + ", reportedBy='" + reportedBy + '\''
                + ", expectedBehavior='" + expectedBehavior + '\''
                + ", actualBehavior='" + actualBehavior + '\''
                + ", frequency=" + frequency
                + ", severity=" + severity
                + ", environment='" + environment + '\''
                + ", errorCode=" + errorCode
                + "} " + super.toString();
    }

    /**
     * Builder class for constructing Bug instances.
     */
    public static final class BugBuilder extends TicketBuilder<BugBuilder> {
        private String expectedBehavior;
        private String actualBehavior;
        private Frequency frequency;
        private Severity severity;
        private String environment;
        private Integer errorCode;

        /**
         * Default constructor initializing type to BUG.
         */
        public BugBuilder() {
            this.type = "BUG";
        }

        /**
         * Sets the expected behavior.
         *
         * @param expBehavior The expected behavior description.
         * @return The builder instance.
         */
        public BugBuilder setExpectedBehavior(final String expBehavior) {
            this.expectedBehavior = expBehavior;
            return self();
        }

        /**
         * Sets the actual behavior.
         *
         * @param actBehavior The actual behavior description.
         * @return The builder instance.
         */
        public BugBuilder setActualBehavior(final String actBehavior) {
            this.actualBehavior = actBehavior;
            return self();
        }

        /**
         * Sets the frequency.
         *
         * @param freq The frequency enum.
         * @return The builder instance.
         */
        public BugBuilder setFrequency(final Frequency freq) {
            this.frequency = freq;
            return self();
        }

        /**
         * Sets the frequency from a string.
         *
         * @param freqString The frequency string representation.
         * @return The builder instance.
         */
        public BugBuilder setFrequency(final String freqString) {
            this.frequency = Frequency.valueOf(freqString);
            return self();
        }

        /**
         * Sets the severity.
         *
         * @param sev The severity enum.
         * @return The builder instance.
         */
        public BugBuilder setSeverity(final Severity sev) {
            this.severity = sev;
            return self();
        }

        /**
         * Sets the severity from a string.
         *
         * @param sevString The severity string representation.
         * @return The builder instance.
         */
        public BugBuilder setSeverity(final String sevString) {
            this.severity = Severity.valueOf(sevString);
            return self();
        }

        /**
         * Sets the environment.
         *
         * @param env The environment string.
         * @return The builder instance.
         */
        public BugBuilder setEnvironment(final String env) {
            this.environment = env;
            return self();
        }

        /**
         * Sets the error code.
         *
         * @param code The error code.
         * @return The builder instance.
         */
        public BugBuilder setErrorCode(final Integer code) {
            this.errorCode = code;
            return self();
        }

        @Override
        protected BugBuilder self() {
            return this;
        }

        /**
         * Sets the business priority.
         * Adjusts priority based on reporter existence.
         *
         * @param priorityStr The priority string.
         * @return The builder instance.
         */
        @Override
        public BugBuilder setBusinessPriority(final String priorityStr) {
            if (this.reportedBy == null || this.reportedBy.isEmpty()) {
                this.businessPriority = Priority.LOW;
                return self();
            }

            this.businessPriority = Priority.valueOf(priorityStr);
            return self();
        }

        /**
         * Sets the reporter username.
         *
         * @param reporter The username of the reporter.
         * @return The builder instance.
         */
        @Override
        public BugBuilder setReportedBy(final String reporter) {
            if (reporter == null || reporter.isEmpty()) {
                this.reportedBy = "";
                return self();
            }

            this.reportedBy = reporter;
            return self();
        }

        /**
         * Builds the Bug instance.
         *
         * @return A new Bug object.
         */
        @Override
        public Bug build() {
            return new Bug(this);
        }
    }
}
