package main.tickets;

import main.enums.Value;

/**
 * Represents a UI Feedback ticket.
 * This class is immutable and cannot be extended.
 */
public final class UiFeedback extends Ticket {
    private static final int MAX_USABILITY_SCORE = 10;
    private static final int MIN_USABILITY_SCORE = 1;

    private final String uiElement;
    private final Value businessValue;
    private final Integer usabilityScore;
    private final String screenshotUrl;
    private final String suggestedFix;

    private UiFeedback(final UiFeedbackBuilder builder) {
        super(builder);
        this.uiElement = builder.uiElement;
        this.businessValue = builder.businessValue;
        this.usabilityScore = builder.usabilityScore;
        this.screenshotUrl = builder.screenshotUrl;
        this.suggestedFix = builder.suggestedFix;
    }

    @Override
    public int toSeniorityLevel() {
        return 0;
    }

    /**
     * Retrieves the UI element identifier.
     *
     * @return The UI element ID.
     */
    public String getUiElement() {
        return uiElement;
    }

    /**
     * Retrieves the business value associated with the feedback.
     *
     * @return The business value enum.
     */
    public Value getBusinessValue() {
        return businessValue;
    }

    /**
     * Retrieves the usability score provided by the user.
     *
     * @return The usability score (1-10).
     */
    public Integer getUsabilityScore() {
        return usabilityScore;
    }

    /**
     * Retrieves the URL of the screenshot.
     *
     * @return The screenshot URL.
     */
    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    /**
     * Retrieves the suggested fix provided by the user.
     *
     * @return The suggested fix description.
     */
    public String getSuggestedFix() {
        return suggestedFix;
    }

    /**
     * Builder class for constructing UiFeedback instances.
     */
    public static final class UiFeedbackBuilder extends TicketBuilder<UiFeedbackBuilder> {
        private String uiElement;
        private Value businessValue;
        private Integer usabilityScore;
        private String screenshotUrl;
        private String suggestedFix;

        /**
         * Default constructor initializing type to UI_FEEDBACK.
         */
        public UiFeedbackBuilder() {
            this.type = "UI_FEEDBACK";
        }

        /**
         * Sets the UI element identifier.
         *
         * @param elementId The UI element ID.
         * @return The builder instance.
         */
        public UiFeedbackBuilder setUiElement(final String elementId) {
            this.uiElement = elementId;
            return self();
        }

        /**
         * Sets the business value.
         *
         * @param value The business value enum.
         * @return The builder instance.
         */
        public UiFeedbackBuilder setBusinessValue(final Value value) {
            this.businessValue = value;
            return self();
        }

        /**
         * Sets the business value from a string.
         *
         * @param valueStr The business value string.
         * @return The builder instance.
         */
        public UiFeedbackBuilder setBusinessValue(final String valueStr) {
            this.businessValue = Value.valueOf(valueStr);
            return self();
        }

        /**
         * Sets the usability score.
         *
         * @param score The usability score (integer).
         * @return The builder instance.
         */
        public UiFeedbackBuilder setUsabilityScore(final Integer score) {
            this.usabilityScore = score;
            return self();
        }

        /**
         * Sets the screenshot URL.
         *
         * @param url The URL string.
         * @return The builder instance.
         */
        public UiFeedbackBuilder setScreenshotUrl(final String url) {
            this.screenshotUrl = url;
            return self();
        }

        /**
         * Sets the suggested fix.
         *
         * @param fix The suggested fix description.
         * @return The builder instance.
         */
        public UiFeedbackBuilder setSuggestedFix(final String fix) {
            this.suggestedFix = fix;
            return self();
        }

        @Override
        protected UiFeedbackBuilder self() {
            return this;
        }

        /**
         * Builds the UiFeedback instance after validation.
         *
         * @return A new UiFeedback object.
         * @throws IllegalArgumentException if the usability score is invalid.
         */
        @Override
        public UiFeedback build() {
            if (this.usabilityScore < MIN_USABILITY_SCORE
                    || this.usabilityScore > MAX_USABILITY_SCORE) {
                throw new IllegalArgumentException("Usability score must be between "
                        + MIN_USABILITY_SCORE + " and " + MAX_USABILITY_SCORE);
            }
            return new UiFeedback(this);
        }
    }
}
