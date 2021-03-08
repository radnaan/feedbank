public class Mood {
    public static final byte NEGATIVE = 0;
    public static final byte NEUTRAL = 1;
    public static final byte POSITIVE = 2;
    public static final byte UNCLASSIFIED = 3;
    
    private byte status;
    private String classification;
    private String[] requests;

    public Mood() {
        status = this.UNCLASSIFIED;
        classification = "unclassified";
        requests = null;
    }

    public void setClassification(String classification) {
        this.changeClassification(classification);
    }

    private void changeClassification(String classification) {
        switch (classification) {
            case "positive":
                this.changeClassification(this.POSITIVE, classification);
                break;
            case "neutral":
                this.changeClassification(this.NEUTRAL, classification);
                break;
            case "negative":
                this.changeClassification(this.NEGATIVE, classification);
                break;
            default:
                this.changeClassification(this.UNCLASSIFIED, "unclassified");
                break;
        }
    }

    private void changeClassification(byte status) {
        switch (status) {
            case Mood.POSITIVE:
                this.changeClassification(Mood.POSITIVE, classification);
                break;
            case Mood.NEUTRAL:
                this.changeClassification(Mood.NEUTRAL, classification);
                break;
            case Mood.NEGATIVE:
                this.changeClassification(Mood.NEGATIVE, classification);
                break;
            default:
                this.changeClassification(Mood.UNCLASSIFIED, "unclassified");
                break;
        }
    }

    //Does not check if the information is correct
    private void changeClassification(byte status, String classification) {
        this.status  = status;
        this.classification = classification;
    }

    public boolean isMood(String classification) {
        return this.classification.equals(classification);
    }

    public void setRequests(String[] requests) {
        this.requests = requests;
    }

    public String[] getRequests() {
        return this.requests;
    }

    public String toString() {
        return "Mood: " + classification;
    }
}