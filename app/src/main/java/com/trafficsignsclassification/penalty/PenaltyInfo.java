package com.trafficsignsclassification.penalty;

public class PenaltyInfo {
    private String signLabel;
    private String violationName;
    private String legalReference;
    private String fineRangeCar;
    private String fineRangeMotorbike;
    private String additionalPenalty;
    private String notes;
    private String updatedAt;
    private String severity;

    public PenaltyInfo(String signLabel, String violationName, String legalReference,
                       String fineRangeCar, String fineRangeMotorbike,
                       String additionalPenalty, String notes, String updatedAt, String severity) {
        this.signLabel = signLabel;
        this.violationName = violationName;
        this.legalReference = legalReference;
        this.fineRangeCar = fineRangeCar;
        this.fineRangeMotorbike = fineRangeMotorbike;
        this.additionalPenalty = additionalPenalty;
        this.notes = notes;
        this.updatedAt = updatedAt;
        this.severity = severity;
    }

    // Getters
    public String getSignLabel() { return signLabel; }
    public String getViolationName() { return violationName; }
    public String getLegalReference() { return legalReference; }
    public String getFineRangeCar() { return fineRangeCar; }
    public String getFineRangeMotorbike() { return fineRangeMotorbike; }
    public String getAdditionalPenalty() { return additionalPenalty; }
    public String getNotes() { return notes; }
    public String getUpdatedAt() { return updatedAt; }
    public String getSeverity() { return severity; }
    
    public String getSeverityDisplay() {
        switch (severity) {
            case "critical": return "Rất nghiêm trọng";
            case "high": return "Nghiêm trọng";
            case "medium": return "Trung bình";
            default: return "Nhẹ";
        }
    }
    
    public int getSeverityColor() {
        switch (severity) {
            case "critical": return android.graphics.Color.parseColor("#C62828");
            case "high": return android.graphics.Color.parseColor("#E65100");
            case "medium": return android.graphics.Color.parseColor("#F57C00");
            default: return android.graphics.Color.parseColor("#757575");
        }
    }
}
